package websocket.function;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;

import org.java_websocket.WebSocket;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import util.StatusEnum;
import util.Util;















import com.google.gson.JsonParser;

import websocket.HeartBeat;
import websocket.bean.RoomInfo;
import websocket.bean.UserInfo;
//import websocket.HeartBeat;
import websocket.pools.WebSocketRoomPool;
import websocket.pools.WebSocketTypePool;
import websocket.pools.WebSocketUserPool;

//此類別給WebSocjet.java使用
public class CommonFunction {

	/** get private messages **/
	public static void getMessage(String message, org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		
		/*** 原方法內容 ***/
		String username = obj.getString("UserName");
		org.java_websocket.WebSocket sendto = WebSocketUserPool
				.getWebSocketByUser(obj.getString("sendto"));
		
		WebSocketUserPool.sendMessageToUser(sendto,
				username + " private message to " + obj.getString("sendto")
						+ ": " + obj.getString("text"));
		WebSocketUserPool.sendMessageToUser(conn, username + " private message to "
				+ obj.getString("sendto") + ": " + obj.getString("text"));
		
		/*** 三方-私訊: A2A ***/
		obj.put("Event", "privateMsg");
		System.out.println("conn" +  conn);
		System.out.println("sendto" +  sendto);
		WebSocketUserPool.sendMessageToUser(conn, obj.toString());
		WebSocketUserPool.sendMessageToUser(sendto, obj.toString());
	}
	
	/** * user join websocket * @param user */
	public static void userjoin(String user, org.java_websocket.WebSocket aConn) {
		System.out.println("userjoin() called");
		JSONObject obj = new JSONObject(user);
		String userId = java.util.UUID.randomUUID().toString();
		String username = obj.getString("UserName");
		String MaxCount = obj.getString("MaxCount"); //新增 MaxCount
		System.out.println("MaxCount: "+MaxCount);
		String ACtype = obj.getString("ACtype");
		String channel = obj.getString("channel");
		WebSocketUserPool.addUser(username, userId, aConn, ACtype); // 在此刻,已將user conn加入倒Pool中
//		String joinMsg = "[Server]" + username + " Online";
//		WebSocketUserPool.sendMessage(joinMsg);
		
		/*** 告知user其成功登入的UserID ***/
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "userjoin");
		sendjson.put("from", userId);
		sendjson.put("channel", channel);
		sendjson.put("statusList", Util.getAgentStatus());
//		sendjson.put("login_dbid", login_dbid); // 此key-value只須Agent接就好(先不做過濾)
//		sendjson.put("notready_dbid", notready_dbid); // 此key-value只須Agent接就好(先不做過濾)
		sendjson.put("MaxCount", MaxCount); // 此key-value只須Agent接就好(先不做過濾)
		WebSocketUserPool.sendMessageToUser(aConn, sendjson.toString());
//		WebSocketUserPool.sendMessage("online people: "
//				+ WebSocketUserPool.getOnlineUser().toString());
		
		/*** 將user加入到各自的TYPEmap ***/
		SimpleDateFormat sdf = new SimpleDateFormat(Util.getSdfTimeFormat());
		String date = sdf.format(new java.util.Date());
		WebSocketTypePool.addUserinTYPE(ACtype, username, userId, date, aConn);
		
		/*** 更新Agent list - 私訊用 ***/
		if ("Agent".equals(ACtype)){
			AgentFunction.refreshAgentList();
		}
		
//		/** 告訴Agent開啟layui **/
//		JSONObject sendjson2 = new JSONObject();
//		sendjson2.put("Event", "userjointoTYPE");
//		sendjson2.put("from", userId);
//		sendjson2.put("username",  username);
//		sendjson2.put("ACtype", ACtype);
//		sendjson2.put("channel", channel);
//		WebSocketUserPool.sendMessage(sendjson2.toString());
		
		/*** 讓Agent與Client都有Heartbeat ***/
		HeartBeat heartbeat = new HeartBeat();
		heartbeat.heartbeating(aConn);
	}
	
	/** ask online people **/
	public static void online(String message, org.java_websocket.WebSocket conn) {
		WebSocketUserPool.sendMessageToUser(conn, "online people: "
				+ WebSocketUserPool.getOnlineUser().toString());
	}
	
	/** * user leave websocket */
	public static void userExit(String aMsg, org.java_websocket.WebSocket aConn) {
		System.out.println("userExit() called");
		JsonObject jsonIn = Util.getGJsonObject(aMsg);
		String id = jsonIn.get("id").getAsString();
		String UserName = jsonIn.get("UserName").getAsString();
		
		//Billy哥部分前端需求:
		String joinMsg = "[Server] - " + UserName + " Offline";
		WebSocketUserPool.sendMessageToUser(aConn, joinMsg); // 只須原登出Agent收到此訊息即可
		
		// 關係Heartbeat
		Timer timer = WebSocketUserPool.getUserHeartbeatTimerByKey(aConn);
		if (timer != null){
			timer.cancel();			
		}
		
		// Client
		// 若已經有Agent正在決定是否Accept此通通話, 若Client先離開了, 則告知此Agent此Client已經離開, 不用再等了
//		String waittingAgent = jsonIn.get("waittingAgent").getAsBoolean();
		if ( WebSocketTypePool.isClient(aConn) && jsonIn.get("waittingAgent") != null){
//			System.out.println("userExit() - waittingAgent: " + jsonIn.get("waittingAgent").getAsBoolean());
			if (jsonIn.get("waittingAgent").getAsBoolean()){
				String waittingAgentID = jsonIn.get("waittingAgentID").getAsString();
				WebSocket agentConn = WebSocketUserPool.getWebSocketByUser(waittingAgentID);
//				System.out.println("userExit() - waittingAgentID: " + waittingAgentID);
				// "clientLeft"
				JsonObject jsonTo = new JsonObject();
				jsonTo.addProperty("Event", "clientLeft");
				jsonTo.addProperty("from", WebSocketUserPool.getUserID(aConn));
				
				WebSocketUserPool.sendMessageToUser(agentConn, jsonTo.toString());
			}			
		}
		
		// Agent
//		waittingClientIDList
		if (WebSocketTypePool.isAgent(aConn)){
			if (!"[]".equals(jsonIn.get("waittingClientIDList"))){
//				System.out.println("userExit() - waittingClientIDList got here");
//				System.out.println("userExit() - " + jsonIn.get("waittingClientIDList").toString());
				JsonArray clientIDJsonAry = jsonIn.getAsJsonArray("waittingClientIDList");
//				String clientIDStr = jsonIn.get("waittingClientIDList").toString();
//				String[] waittingClientIDList = clientIDStr.substring(1, clientIDStr.length()-1).split(",");
//				System.out.println("userExit() - " + waittingClientIDList.length);
//				System.out.println("userExit() - clientIDJsonAry: " + clientIDJsonAry);
				for(final JsonElement clientID_je : clientIDJsonAry) {
				    String clientID = clientID_je.getAsJsonObject().get("clientID").getAsString();
				    WebSocket clientConn = WebSocketUserPool.getWebSocketByUser(clientID);
				    System.out.println("userExit() - clientID: " + clientID);
				    jsonIn.addProperty("Event", "agentLeft");
					WebSocketUserPool.sendMessageToUser(clientConn, jsonIn.toString());
				    
				}				
			}
			
			if (!"[]".equals(jsonIn.get("waittingAgentIDList").toString())){
//				System.out.println("userExit() - waittingAgentIDList got here");
//				System.out.println("userExit() - " + jsonIn.get("waittingAgentIDList").toString());
				JsonArray agentIDJsonAry = jsonIn.getAsJsonArray("waittingAgentIDList");
//				String clientIDStr = jsonIn.get("waittingClientIDList").toString();
//				String[] waittingClientIDList = clientIDStr.substring(1, clientIDStr.length()-1).split(",");
//				System.out.println("userExit() - " + waittingClientIDList.length);
//				System.out.println("userExit() - agentIDJsonAry: " + agentIDJsonAry);
				for(final JsonElement agentID_je : agentIDJsonAry) {
				    String agentID = agentID_je.getAsJsonObject().get("agentID").getAsString();
				    WebSocket agentConn = WebSocketUserPool.getWebSocketByUser(agentID);
//				    System.out.println("userExit() - agentID: " + agentID);
				    jsonIn.addProperty("Event", "agentLeftThirdParty");
					WebSocketUserPool.sendMessageToUser(agentConn, jsonIn.toString());
				}
			}
//			for(String clientID: waittingClientIDList){
//				System.out.println("userExit() - " + clientID);
//			}
		}
		
//		System.out.println("before close: " + WebSocketUserPool.getUserID(aConn));
		// 最後關閉連線
		aConn.close();
	}
	
	/** * user join room */
	public static void userjointoRoom(String message,
			org.java_websocket.WebSocket conn) {
		System.out.println("userjointoRoom() called - conn: " + conn);
		JSONObject obj = new JSONObject(message);
		String roomID = obj.getString("roomID");
		String userid = obj.getString("id");
		String username = obj.getString("UserName");
		String joinMsg = "[Server]" + username + " join " + roomID + " room";
		WebSocketRoomPool.addUserInRoom(roomID, username, userid, conn); //重要步驟
		WebSocketUserPool.addUserRoom(roomID, conn); //重要步驟
		WebSocketRoomPool.sendMessageinroom(roomID, joinMsg);
		WebSocketRoomPool.sendMessageinroom(roomID, "room people: "
				+ WebSocketRoomPool.getOnlineUserNameinroom(roomID).toString());
		
		// 之後可做更詳細的判斷-如為Agent才執行就好
		// 尚有例外: JSONObject["ACtype"] not found.
		String ACtype = obj.getString("ACtype");
		if ("Agent".equals(ACtype)){
			System.out.println("userjointoroom - one Agent joined");
			refreshRoomList(conn);						
		}
		
	}
	
	/** * user leave room */
	public static void userExitfromRoom(String message,
			org.java_websocket.WebSocket conn) {
		System.out.println("userExitfromRoom() called");
		JSONObject obj = new JSONObject(message);
		String roomID = obj.getString("roomID");
		String username = obj.getString("UserName");
		String joinMsg = "[Server]" + username + " leave " + roomID + " room";
		WebSocketRoomPool.sendMessageinroom(roomID, joinMsg);
//		WebSocketRoomPool.removeRoom(room); // 這邊要改成removeUserinroom()
		WebSocketRoomPool.removeUserinroom(roomID, conn);
	}
	
	/** * Get Message from Room */
	public static void getMessageinRoom(String message,
			org.java_websocket.WebSocket conn) {
		
		// 拿取資料(gson)
		JsonObject msgJsonNew = Util.getGJsonObject(message);
		RoomInfo roomInfo = WebSocketRoomPool.getRoomInfo(msgJsonNew.get("roomID").getAsString());
		org.java_websocket.WebSocket clientConn = roomInfo.getClientConn();
		
			// 更新UserInteraction 
		String userinteractionMsg = WebSocketUserPool.getUserInteractionByKey(clientConn);
		JsonObject userinteractionJsonMsg = Util.getGJsonObject(userinteractionMsg);
//		System.out.println("getMessageinRoom() - userinteractionMsg: " + userinteractionMsg);
		// 因此方法只有Client呼叫,故最多一個Client也就只有一個roomID,若有再更新即可
				//更新text
		String text = "";
		if (userinteractionJsonMsg.get("text") != null){
			text = userinteractionJsonMsg.get("text").getAsString();
		}
		text += msgJsonNew.get("UserName").getAsString() + ": " + msgJsonNew.get("text").getAsString() + "\n";
		userinteractionJsonMsg.addProperty("text", text);
				// 更新structuredtext
		JsonArray structuredtext = new JsonArray();
		if (userinteractionJsonMsg.get("structuredtext") != null){
			structuredtext = userinteractionJsonMsg.get("structuredtext").getAsJsonArray();
		}
					// 更新時間
		SimpleDateFormat sdf = new SimpleDateFormat(Util.getSdfDateTimeFormat());
		String dateStr = sdf.format(new java.util.Date()); 
		msgJsonNew.addProperty("date", dateStr);
		structuredtext.add(msgJsonNew);
		userinteractionJsonMsg.add("structuredtext", structuredtext);
		
		WebSocketUserPool.addUserInteraction(userinteractionJsonMsg.toString(), clientConn); // final step
//		System.out.println("after - getMessageinRoom() - userinteractionMsg: " + WebSocketUserPool.getUserInteractionByKey(roomInfo.getClientConn()));

		// 將訊息寄給room線上使用者:
		if (msgJsonNew.get("roomID") == null) return;
		msgJsonNew.addProperty("Event", "messagetoRoom");
		WebSocketRoomPool.sendMessageinroom(msgJsonNew.get("roomID").getAsString(), msgJsonNew.toString());
		
//		System.out.println("final msgJson: \n"+ msgJson); // for debugging
	}
	
	// room
	/** * ask online people in room */
	public static void onlineinRoom(String message, org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String roomID = obj.getString("roomID");
		WebSocketUserPool.sendMessageToUser(conn, "room people: "
				+ WebSocketRoomPool.getOnlineUserNameinroom(roomID).toString());
	}
	
	/** * search online people from Agent or client */
	public static void onlineinTYPE(String message, org.java_websocket.WebSocket conn) {
//		System.out.println("onlineinTYPE");
		JSONObject obj = new JSONObject(message);
		String ACtype = obj.getString("ACtype"); // 請求者的TYPE

		/**** 告知現在在線Agent有誰 - 放在訊息中 ****/
		Collection<String> agentNameList = WebSocketTypePool.getOnlineUserNameinTYPE(ACtype);
		WebSocketUserPool.sendMessageToUser(conn, ACtype + " people: " + agentNameList.toString());
		
		/**** 告知現在在線Agent有誰 - 顯示在'線上Agents'表格中 ****/
		JSONObject sendjson = new JSONObject();
		Collection<String> agentIDList = WebSocketTypePool.getOnlineUserIDinTYPE(ACtype);
		sendjson.put("Event", "onlineinTYPE");
		sendjson.put("from", agentIDList.toString().replace("[", "").replace("]", ""));
		sendjson.put("username",  WebSocketTypePool.getOnlineUserNameinTYPE(ACtype)
				.toString().replace("[", "").replace("]", ""));
		sendjson.put("ACtype", ACtype);
		sendjson.put("channel", obj.getString("channel"));
		// 修正為只跟要求知道現在在線人員的user就好,而非整個user都要知道:
		WebSocketUserPool.sendMessageToUser(conn, sendjson.toString());
//		WebSocketTypePool.sendMessageinTYPE(ACtype,sendjson.toString());
	}
	
	/** * user join from Agent or Client list */
	public static void userjointoTYPE(String message, org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String ACtype = obj.getString("ACtype");
		String userid = obj.getString("id");
		String username = obj.getString("UserName");
		SimpleDateFormat sdf = new SimpleDateFormat(Util.getSdfTimeFormat());
		String date = sdf.format(new java.util.Date());
		String joinMsg = "[Server]" + username + " join " + ACtype;
		WebSocketTypePool.addUserinTYPE(ACtype, username, userid, date, conn); // 
		WebSocketTypePool.sendMessageinTYPE(ACtype, joinMsg);
		WebSocketTypePool.sendMessageinTYPE(ACtype, ACtype + " people: "
				+ WebSocketTypePool.getOnlineUserNameinTYPE(ACtype).toString());
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "userjointoTYPE");
		sendjson.put("from", userid);
		sendjson.put("username",  username);
		sendjson.put("ACtype", ACtype);
		sendjson.put("channel", obj.getString("channel"));
		WebSocketUserPool.sendMessage(sendjson.toString());
	}
	
	/** * user leave from Agent or Client list */
	public static void userExitfromTYPE(String message,
			org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String ACtype = obj.getString("ACtype");
		String userid = obj.getString("id");
		String username = obj.getString("UserName");
		String joinMsg = "[Server]" + username + " leave " + ACtype;
		WebSocketTypePool.sendMessageinTYPE(ACtype, joinMsg);
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "userExitfromTYPE");
		sendjson.put("from", userid);
		sendjson.put("username",  username);
		sendjson.put("ACtype", ACtype);
		sendjson.put("channel", obj.getString("channel"));
		WebSocketTypePool.sendMessageinTYPE(ACtype,sendjson.toString());
		WebSocketTypePool.removeUserinTYPE(ACtype, conn);
	}
	
	/** * update Agent Status */
	public static void updateStatus(String message, org.java_websocket.WebSocket conn) {
		System.out.println("updateStatus() called");
		JsonObject obj = Util.getGJsonObject(message);
//		JSONObject obj = new JSONObject(message); 
		String ACtype = obj.get("ACtype").getAsString();
		String username = obj.get("UserName").getAsString();
		String userid = obj.get("id").getAsString();
		String date = obj.get("date").getAsString();
		String status = obj.get("status").getAsString(); // 以數字代表
		String reason = obj.get("reason").getAsString();
		String dbid = Util.getGString(obj, "dbid");
		String startORend = Util.getGString(obj, "startORend"); 
				
		if(status.equals("lose")){
			WebSocketTypePool.addleaveClient();
		}
		WebSocketTypePool.UserUpdate(ACtype, username, userid, date, status, reason, conn);
		
		// 更新DB狀態時間
		System.out.println("status: " + status);
		System.out.println("obj: " + obj);
		System.out.println("dbid: " + dbid);
		System.out.println("startORend: " + startORend);
		
		if ("start".equals(startORend)){
			String userID = Util.getTmpID(userid);
			// 如果是LOGIN狀態,則同時新增NOTREADY狀態
			StatusEnum currStatusEnum = StatusEnum.getStatusEnumByDbid(status);
			System.out.println("currStatusEnum: " + currStatusEnum);
			dbid = AgentFunction.RecordStatusStart(userID, status, "8");
			String dbid_key = currStatusEnum.toString().toLowerCase() + "_dbid";
			System.out.println("dbid_key: " + dbid_key);
			obj.addProperty(dbid_key, dbid); // ex. login_dbid

			// 先只有新增時寄送EVENT,讓前端能拿到相對應的dbid
			obj.addProperty("Event", "updateStatus");
			WebSocketUserPool.sendMessageToUser(conn, obj.toString());

		}else if ("end".equals(startORend)){
			System.out.println("end");
			if (dbid != null){
				System.out.println("dbid: " + dbid);
				// 查看是否為iestablished list要更新
				if ("[".equals(dbid.substring(0, 1))){
					System.out.println("here!!");
					dbid = dbid.substring(1, dbid.length()-1);
					System.out.println("dbid inner: " + dbid);
					for (String dbid_tmp: dbid.split(",")){ // dbid_tmp 原本長這樣 ["414","418"] 全部是一個字串
						dbid_tmp = dbid_tmp.substring(1,dbid_tmp.length()-1);
						System.out.println("dbid_tmp: " + dbid_tmp);
						AgentFunction.RecordStatusEnd(dbid_tmp);
					}
					
				}else{
					AgentFunction.RecordStatusEnd(dbid);					
				}
				
			}
		}
		
		
		
		
	}
	
	public static void refreshRoomList(org.java_websocket.WebSocket conn){
		System.out.println("refreshRoomList() called");
		JsonObject sendJson = new JsonObject();
		sendJson.addProperty("Event", "refreshRoomList");
		sendJson.addProperty("UserID", conn.toString());
		
		JsonArray roomIDListJson = new JsonArray();
		JsonArray memberListJson = new JsonArray();
		JsonArray clietIDListJson = new JsonArray();
		JsonArray textListJson = new JsonArray();
		JsonArray structuredtextListJson = new JsonArray();
		
		// 將 此Agent所屬的Room list 塞入json中
		List<String> roomIDList = WebSocketUserPool.getUserRoomByKey(conn);
//		System.out.println("roomIDList.size(): " + roomIDList.size());
		for (String roomID: roomIDList){
			RoomInfo roomInfo = WebSocketRoomPool.getRoomInfo(roomID);
			UserInfo clientInfo = WebSocketUserPool.getUserInfoByKey(roomInfo.getClientConn());
			roomIDListJson.add(roomID);
			memberListJson.add(WebSocketRoomPool.getOnlineUserNameinroom(roomID).toString());
			clietIDListJson.add(clientInfo.getUserid());
			
			// 如果有room歷史訊息,則放到room物件中傳給client
			if (WebSocketUserPool.getUserInteractionByKey(roomInfo.getClientConn()) != null){
				JsonObject msgJsonOld = Util.getGJsonObject(WebSocketUserPool.getUserInteractionByKey(roomInfo.getClientConn()));
				System.out.println("setinteraction() - msgJsonOld: " + msgJsonOld);
				if (msgJsonOld.get("text") != null &&
					msgJsonOld.get("structuredtext") != null){
					textListJson.add(msgJsonOld.get("text").getAsString());
					structuredtextListJson.add(msgJsonOld.get("structuredtext").getAsJsonArray());							
				}
			}
		}
		
		// 將 membersInRoom list 塞入json中
		
		
		// 將 RoomContent list 塞入json中
		
		// 測試用-新增幾個room
//		roomList_json.put("2982ebfa-0359-4777-8746-e6de5e493712");
//		roomList_json.put("2982ebfa-0359-4777-8746-e6de5e493778");
		
		sendJson.add("roomList", roomIDListJson);
		sendJson.add("memberList", memberListJson);
		sendJson.add("textList", textListJson);
		sendJson.add("structuredtextList", structuredtextListJson);
		
//		System.out.println("roomIDListJson.size(): " + roomIDListJson.size());
//		System.out.println("sendJson: " + sendJson);
		
		
		// end of 將此Agent所屬的Room list塞入json中
		WebSocketUserPool.sendMessageToUser(conn, sendJson.toString());
	}
	
	private static void refreshClientList(String user){
		JSONObject obj = new JSONObject(user);
//		String userId = java.util.UUID.randomUUID().toString();
//		String username = obj.getString("UserName");
//		String ACtype = obj.getString("ACtype");
		// 再來把所有client list放入JsonArray
		
		// end of 再來把所有client list放入JsonArray
		Collection<String> agentList = WebSocketTypePool.getOnlineUserIDinTYPE("Agent");
		for (String agent : agentList){
			System.out.println("agent: " + agent);
			// 再來跟每個Agent講要更新client list了
		}
		
	}
	
}
