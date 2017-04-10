package websocket.function;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import util.StatusEnum;
import util.Util;





























import com.google.gson.JsonParser;

import websocket.HeartBeat;
import websocket.bean.RingCountDownTask;
import websocket.bean.RoomInfo;
import websocket.bean.SystemInfo;
import websocket.bean.UpdateStatusBean;
import websocket.bean.UserInfo;
//import websocket.HeartBeat;
import websocket.pools.WebSocketRoomPool;
import websocket.pools.WebSocketTypePool;
import websocket.pools.WebSocketUserPool;
import websocket.thread.findAgent.FindAgentThread;

//此類別給WebSocjet.java使用
public class CommonFunction {

	/** get private messages **/
	public static void getMessage(String message, org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String userID = WebSocketUserPool.getUserID(conn);
		String username = WebSocketUserPool.getUserNameByKey(conn);
		
		/*** 原方法內容 ***/
		org.java_websocket.WebSocket sendto = WebSocketUserPool.getWebSocketByUserID(
											  obj.getString("sendto"));
		WebSocketUserPool.sendMessageToUserWithTryCatch(sendto,
											username + " private message to " + obj.getString("sendto")
											+ ": " + obj.getString("text"));		
		/*** 三方-私訊: A2A ***/
		obj.put("Event", "privateMsg");
//		Util.getConsoleLogger().debug("conn" +  conn);
//		Util.getConsoleLogger().debug("sendto" +  sendto);
		obj.put("UserID", userID);
		obj.put("UserName", username);
		WebSocketUserPool.sendMessageToUserWithTryCatch(conn, obj.toString());
		WebSocketUserPool.sendMessageToUserWithTryCatch(sendto, obj.toString());
	}
	
	/** * user join websocket * @param user */
	public static void userjoin(String user, org.java_websocket.WebSocket aConn) {
		Util.getConsoleLogger().debug("userjoin() called");
		Util.getFileLogger().info("userjoin() called");
		JsonObject obj = Util.getGJsonObject(user);
		String username = Util.getGString(obj, "UserName");
		String maxCount = Util.getGString(obj, "maxCount");
		String ACtype = Util.getGString(obj, "ACtype");
		String channel = Util.getGString(obj, "channel");
		String userId = null;
		
		JSONObject sendjson = new JSONObject();
		
		/*** 檢查是否已經登入過 ***/		
		if(ACtype.equals("Agent")){
			userId = obj.get("id").toString(); //20170222 Lin
		}else if(ACtype.equals("Client")){
			userId = java.util.UUID.randomUUID().toString();
		}
		Util.getConsoleLogger().debug("userId: " + userId);
		if (ACtype.equals("Agent")){
			WebSocket oldAgentConn = WebSocketUserPool.getWebSocketByUserID(userId);
			if (oldAgentConn != null){
				Util.getConsoleLogger().debug("oldAgentConn exists");
				Util.getFileLogger().info("oldAgentConn exists");
				// 更新原JsonObject
				sendjson.put("isLoggedIn", true);
				sendjson.put("isLoggedInText", "已將前一個使用者剔除");
				try{
					// 這邊關掉,並告知前一個連線有人將其剔除
					JSONObject loggedInAgainJson = new JSONObject();
					loggedInAgainJson.put("Event", "userjoinAgain");
					loggedInAgainJson.put("text", "你已被其他使用者剔除");
					WebSocketUserPool.sendMessageToUserWithTryCatch(oldAgentConn, loggedInAgainJson.toString());
					// 清理相關資料
//					oldAgentConn.close();
				}catch(WebsocketNotConnectedException e){
					Util.getFileLogger().info("e.getMessage(): " + e.getMessage());
					Util.getConsoleLogger().info("e.getMessage(): " + e.getMessage());
				}finally{
					// 確保一定將此Agent排除掉(再持續觀察)
					// 若已經失去連線,則只清理資料面訊息
					if (oldAgentConn.isClosing() || oldAgentConn.isClosed())
						CommonFunction.onCloseHelper(oldAgentConn, "repeatedLogin");
					// 若尚未失去連線,則同時清理資料並關閉連線
					else
						oldAgentConn.close();
//				clearUserData(oldAgentConn);
//				WebSocketUserPool.removeUser(oldAgentConn);					
				}// end of try-catch-finally
				
			}// end of if (oldAgentConn != null)
		}// end of if

		/*** 開始新增使用者 ***/
		if (ACtype.equals("Agent")){
			WebSocketUserPool.addUser(username, userId, aConn, ACtype, Integer.parseInt(maxCount)); // 在此刻,已將user conn加入Pool中,並建立其UserInfo資訊物件
		}else{
			WebSocketUserPool.addUser(username, userId, aConn, ACtype, 0); // 在此刻,已將user conn加入Pool中,並建立其UserInfo資訊物件			
		}
		
		/*** 告知user其成功登入的UserID ***/
		sendjson.put("Event", "userjoin");
		sendjson.put("from", userId);
		sendjson.put("channel", channel);
		sendjson.put("statusList", Util.getAgentStatus());
//		sendjson.put("maxCount", maxCount); // 此key-value只須Agent接就好(先不做過濾)
		AgentFunction.GetAgentReasonInfo("0");
		sendjson.put("reasonList", Util.getAgentReason()); // 此key-value只須Agent接就好(先不做過濾) ; 此內容已於伺服器啟動時拿取
		sendjson.put(SystemInfo.TAG_SYS_MSG, SystemInfo.getLoginMsg()); // 加上系統訊息(目前前端客戶端版本仍為一次訊息全送,故暫解不將此改為JsonObject物件)
		Util.getConsoleLogger().debug("Util.getAgentReason(): " + Util.getAgentReason());
		Util.getFileLogger().info("Util.getAgentReason(): " + Util.getAgentReason());
		Util.getConsoleLogger().debug("Util.getAgentStatus(): " + Util.getAgentStatus());
		Util.getFileLogger().info("Util.getAgentStatus(): " + Util.getAgentStatus());
		WebSocketUserPool.sendMessageToUserWithTryCatch(aConn, sendjson.toString());
//		WebSocketUserPool.sendMessage("online people: "
//				+ WebSocketUserPool.getOnlineUser().toString());
		
		/*** 將user加入到各自的TYPEmap ***/
		SimpleDateFormat sdf = new SimpleDateFormat(Util.getSdfTimeFormat());
		String date = sdf.format(new java.util.Date());
		WebSocketTypePool.addUserinTYPE(ACtype, username, userId, date, aConn);
	
		
		
		/*** 讓Agent與Client都有Heartbeat ***/
		HeartBeat heartbeat = new HeartBeat();
		heartbeat.heartbeating(aConn);
		
		/*** Agent - 更新狀態 ***/
		if(WebSocketTypePool.isAgent(aConn)) {
			UpdateStatusBean usb = null;
			// LOGIN狀態開始
			Util.getStatusFileLogger().info("###### [userjoin()]");
			usb = new UpdateStatusBean();
			usb.setStatus(StatusEnum.LOGIN.getDbid());
			usb.setStartORend("start");
			CommonFunction.updateStatus(new Gson().toJson(usb), aConn);	
			// NOTREADY狀態開始
			Util.getStatusFileLogger().info("###### [userjoin()]");
			usb = new UpdateStatusBean();
			usb.setStatus(StatusEnum.NOTREADY.getDbid());
			usb.setStartORend("start");
			CommonFunction.updateStatus(new Gson().toJson(usb), aConn);
		}// end of if (WebSocketTypePool.isAgent(...))
		
		
		/*** 更新Agent list - 私訊用 ***/
		if ("Agent".equals(ACtype)){
			AgentFunction.refreshAgentList();
		}
		
	}
	
	/** ask online people **/
	public static void online(String message, org.java_websocket.WebSocket conn) {
		WebSocketUserPool.sendMessageToUserWithTryCatch(conn, "online people: "
				+ WebSocketUserPool.getOnlineUser().toString());
	}
	
	/** * user leave websocket */
	public static void userExit(String aMsg, org.java_websocket.WebSocket aConn) {
		Util.getConsoleLogger().debug("userExit() called");
		JsonObject jsonIn = Util.getGJsonObject(aMsg);
		String id = jsonIn.get("id").getAsString();
		String UserName = jsonIn.get("UserName").getAsString();
		UserInfo userInfo = WebSocketUserPool.getUserInfoByKey(aConn);
		userInfo.setRingEndExpected(true);
				
		// 關係Heartbeat
		Timer timer = WebSocketUserPool.getUserHeartbeatTimerByKey(aConn);
		if (timer != null){
			timer.cancel();			
		}
		
		// for Client
		// waittingAgent - 當有Agent正在決定是否Accept此通通話, 若Client先離開了, 則告知此Agent此Client已經離開, 不用再等了
		if ( WebSocketTypePool.isClient(aConn) && jsonIn.get("waittingAgent") != null){
//			Util.getConsoleLogger().debug("userExit() - waittingAgent: " + jsonIn.get("waittingAgent").getAsBoolean());
			if (jsonIn.get("waittingAgent").getAsBoolean()){
				String waittingAgentID = jsonIn.get("waittingAgentID").getAsString();
				WebSocket agentConn = WebSocketUserPool.getWebSocketByUserID(waittingAgentID);
//				Util.getConsoleLogger().debug("userExit() - waittingAgentID: " + waittingAgentID);
				
				// 關閉RING
				UserInfo agentUserInfo = WebSocketUserPool.getUserInfoByKey(agentConn);
				agentUserInfo.setRingEndExpected(true);
				agentUserInfo.setStopRing(true);
				
				// 寄出"clientLeft",告知Agent有人離開了
				JsonObject jsonTo = new JsonObject();
				jsonTo.addProperty("Event", "clientLeft");
				jsonTo.addProperty("from", WebSocketUserPool.getUserID(aConn));
				
				WebSocketUserPool.sendMessageToUserWithTryCatch(agentConn, jsonTo.toString());
			}			
		}
		
		// for Agent
		// 1. waittingClientIDList - 當Agent離開後,若有Clinet在等待其回應,則告知此Client此Agent已經離開, 不用再等了, 請他再繼續找其他人
		// 2. waittingAgentIDList - 當Agent離開後,若有其他Agent在等待其回應,如三方/轉接,則告知另一個Agent此Agent已經離開, 不用再等了
		if (WebSocketTypePool.isAgent(aConn)){
			if (!"[]".equals(jsonIn.get("waittingClientIDList"))){
//				Util.getConsoleLogger().debug("userExit() - waittingClientIDList got here");
//				Util.getConsoleLogger().debug("userExit() - " + jsonIn.get("waittingClientIDList").toString());
				JsonArray clientIDJsonAry = jsonIn.getAsJsonArray("waittingClientIDList");
//				String clientIDStr = jsonIn.get("waittingClientIDList").toString();
//				String[] waittingClientIDList = clientIDStr.substring(1, clientIDStr.length()-1).split(",");
//				Util.getConsoleLogger().debug("userExit() - " + waittingClientIDList.length);
//				Util.getConsoleLogger().debug("userExit() - clientIDJsonAry: " + clientIDJsonAry);
				for(final JsonElement clientID_je : clientIDJsonAry) {
//				    String clientID = clientID_je.getAsJsonObject().get("clientID").getAsString();
				    String clientID = clientID_je.getAsString();
				    WebSocket clientConn = WebSocketUserPool.getWebSocketByUserID(clientID);
				    Util.getConsoleLogger().debug("userExit() - waitting clientID: " + clientID);
				    jsonIn.addProperty("Event", "agentLeft");
				    jsonIn.addProperty(SystemInfo.TAG_SYS_MSG, SystemInfo.getCancelLedReqMsg()); // 增加系統訊息
					WebSocketUserPool.sendMessageToUserWithTryCatch(clientConn, jsonIn.toString());
				    
				}				
			}
			
			if (!"[]".equals(jsonIn.get("waittingAgentIDList").toString())){
//				Util.getConsoleLogger().debug("userExit() - waittingAgentIDList got here");
//				Util.getConsoleLogger().debug("userExit() - " + jsonIn.get("waittingAgentIDList").toString());
				JsonArray agentIDJsonAry = jsonIn.getAsJsonArray("waittingAgentIDList");
//				String clientIDStr = jsonIn.get("waittingClientIDList").toString();
//				String[] waittingClientIDList = clientIDStr.substring(1, clientIDStr.length()-1).split(",");
//				Util.getConsoleLogger().debug("userExit() - " + waittingClientIDList.length);
//				Util.getConsoleLogger().debug("userExit() - agentIDJsonAry: " + agentIDJsonAry);
				for(final JsonElement agentID_je : agentIDJsonAry) {
				    String agentID = agentID_je.getAsJsonObject().get("agentID").getAsString();
				    WebSocket agentConn = WebSocketUserPool.getWebSocketByUserID(agentID);
//				    Util.getConsoleLogger().debug("userExit() - agentID: " + agentID);
				    jsonIn.addProperty("Event", "agentLeftThirdParty");
					WebSocketUserPool.sendMessageToUserWithTryCatch(agentConn, jsonIn.toString());
				}
			}
//			for(String clientID: waittingClientIDList){
//				Util.getConsoleLogger().debug("userExit() - " + clientID);
//			}
		}
		
		// 寄送Exit訊息回去,告知server已處理好,可以關閉連線了
	    JsonObject jsonOut = new JsonObject();
		jsonOut.addProperty("Event", "Exit");
		WebSocketUserPool.sendMessageToUserWithTryCatch(aConn, jsonOut.toString());

		//Billy哥部分前端需求:
		String joinMsg = "[Server] - " + UserName + " Offline";
		WebSocketUserPool.sendMessageToUserWithTryCatch(aConn, joinMsg); // 只須原登出Agent收到此訊息即可
		
		
//		Util.getConsoleLogger().debug("before close: " + WebSocketUserPool.getUserID(aConn));
		// 最後關閉連線
		aConn.close();
	}
	
	/** * user join room */
	public static void userjointoRoom(String message,
			org.java_websocket.WebSocket conn) {
		Util.getConsoleLogger().debug("userjointoRoom() called - conn: " + conn);
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
			Util.getConsoleLogger().debug("userjointoroom - one Agent joined");
			refreshRoomList(conn);						
		}
		
	}
	
	/** * user leave room */
	public static void userExitfromRoom(String message,
			org.java_websocket.WebSocket conn) {
		Util.getConsoleLogger().debug("userExitfromRoom() called");
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
		
		/*** 更新UserInteraction ***/ 
		String userinteractionMsg = WebSocketUserPool.getUserInteractionByKey(clientConn);
		JsonObject userinteractionJsonMsg = Util.getGJsonObject(userinteractionMsg);
//		Util.getConsoleLogger().debug("getMessageinRoom() - userinteractionMsg: " + userinteractionMsg);
		// 因此方法只有Client呼叫,故最多一個Client也就只有一個roomID,若有再更新即可
		// 更新text
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
//		Util.getConsoleLogger().debug("after - getMessageinRoom() - userinteractionMsg: " + WebSocketUserPool.getUserInteractionByKey(roomInfo.getClientConn()));

		/*** 將訊息寄給room線上使用者 ***/  
		if (msgJsonNew.get("roomID") == null) return;
		msgJsonNew.addProperty("Event", "messagetoRoom");
		WebSocketRoomPool.sendMessageinroom(msgJsonNew.get("roomID").getAsString(), msgJsonNew.toString());
		
//		Util.getConsoleLogger().debug("final msgJson: \n"+ msgJson); // for debugging
	}
	
	// room
	/** * ask online people in room */
	public static void onlineinRoom(String message, org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String roomID = obj.getString("roomID");
		WebSocketUserPool.sendMessageToUserWithTryCatch(conn, "room people: "
				+ WebSocketRoomPool.getOnlineUserNameinroom(roomID).toString());
	}
	
	/** * search online people from Agent or client */
	public static void onlineinTYPE(String message, org.java_websocket.WebSocket conn) {
//		Util.getConsoleLogger().debug("onlineinTYPE");
		JSONObject obj = new JSONObject(message);
		String ACtype = obj.getString("ACtype"); // 請求者的TYPE

		/**** 告知現在在線Agent有誰 - 放在訊息中 ****/
		Collection<String> agentNameList = WebSocketTypePool.getOnlineUserNameinTYPE(ACtype);
		WebSocketUserPool.sendMessageToUserWithTryCatch(conn, ACtype + " people: " + agentNameList.toString());
		
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
		WebSocketUserPool.sendMessageToUserWithTryCatch(conn, sendjson.toString());
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
	synchronized public static void updateStatus(String aMsg, org.java_websocket.WebSocket aConn) {
		Util.getConsoleLogger().debug("updateStatus() called");
		if (!WebSocketTypePool.isAgent(aConn)) return; // 防呆
		
		Util.getStatusFileLogger().info("###### updateStatus() called ######");
		JsonObject obj = Util.getGJsonObject(aMsg);
//		JSONObject obj = new JSONObject(message); 
		String ACtype = WebSocketTypePool.getUserType(aConn);
		String username = WebSocketUserPool.getUserNameByKey(aConn); 
		String userid = WebSocketUserPool.getUserID(aConn);
		SimpleDateFormat sdf = new SimpleDateFormat( Util.getSdfDateFormat() );
		String date = sdf.format(new java.util.Date());
		
		String status_dbid = Util.getGString(obj, "status"); // 以數字代表 dbid
		String startORend = Util.getGString(obj, "startORend"); 
		String dbid = Util.getGString(obj, "dbid"); // 若有值,代表此次是要寫end的 // for "end"
		String reason_dbid =  Util.getGString(obj, "reason_dbid"); // for NOTREADY
		if (reason_dbid == null) reason_dbid = "0"; // 設定reason預設值為'0'
		String roomID = Util.getGString(obj, "roomID");  // for IESTABLISHED
		String clientID = Util.getGString(obj, "clientID"); // for RING
		UserInfo userInfo = WebSocketUserPool.getUserInfoByKey(aConn);
		StatusEnum currStatusEnum = StatusEnum.getStatusEnumByDbid(status_dbid);
				
		// 原方法區塊 - 更新Agent UserInfo中的status
		if(status_dbid.equals("lose")){
			WebSocketTypePool.addleaveClient();
		}
		WebSocketTypePool.UserUpdate(ACtype, username, userid, date, StatusEnum.getStatusEnumByDbid(status_dbid), reason_dbid, aConn);
		
		// 更新DB狀態時間
		Util.getConsoleLogger().info("updateStatus: " + startORend + " - " + currStatusEnum + " - " + username);
		Util.getStatusFileLogger().info("updateStatus: " + startORend + " - " + currStatusEnum + " - " + username);

		Util.getStatusFileLogger().info("" + currStatusEnum + ": ");
		Util.getStatusFileLogger().printf(Level.INFO,"%10s	%10s %10s %10s %10s %10s" , "status", "startORend", "dbid", "roomID", "clientID", "reason");
		Util.getStatusFileLogger().info("----------------------------------------------------------------------------");
		Util.getStatusFileLogger().printf(Level.INFO,"%10s	%10s %10s %10s %10s %10s" , status_dbid, startORend, dbid, roomID, clientID, reason_dbid);
		
		if ("start".equals(startORend)){
//			String userID = Util.getTmpID(userid);
			// 將開始時間寫入DB
			
			if (StatusEnum.READY.getDbid().equals(status_dbid) &&
				userInfo.isReady()){
				Util.getConsoleLogger().debug("Agent is READY already. No update is processed to DB");
				return;
			}else if (StatusEnum.READY.getDbid().equals(status_dbid)){
				
				// maxCount檢查 - 若達到,則立即中斷狀態更新,並寄出事件告知使用者,切換成READY狀態失敗
				Util.getConsoleLogger().debug("WebSocketUserPool.getUserRoomCount(aConn): " + WebSocketUserPool.getUserRoomCount(aConn));
				Util.getConsoleLogger().debug("userInfo.getMaxCount(): " + userInfo.getMaxCount());
				if (WebSocketUserPool.getUserRoomCount(aConn) >= userInfo.getMaxCount()){
					Util.getConsoleLogger().debug("maxCountReached");
					JsonObject maxCountMsg = new JsonObject();
					maxCountMsg.addProperty("Event", "updateStatus");
					maxCountMsg.addProperty("maxCountReached", true);
					maxCountMsg.addProperty("currStatusEnum", StatusEnum.NOTREADY.toString());
					WebSocketUserPool.sendMessageToUserWithTryCatch(aConn, maxCountMsg.toString());
					return;
				}
				
				// READY狀態開始
				dbid = AgentFunction.RecordStatusStart(userid, status_dbid, "0"); // 重要				
				// NOTREADY狀態結束(須排除初次登入狀況)
				if (userInfo.getStatusDBIDMap().get(StatusEnum.NOTREADY) != null){

					Util.getStatusFileLogger().info("updateStatus: " + "end" + " - " + StatusEnum.NOTREADY + " - " + username);
					Util.getStatusFileLogger().info("" + currStatusEnum + ": ");
					Util.getStatusFileLogger().printf(Level.INFO,"%10s	%10s %10s %10s %10s %10s" , "status", "startORend", "dbid", "roomID", "clientID", "reason");
					Util.getStatusFileLogger().info("----------------------------------------------------------------------------");
					Util.getStatusFileLogger().printf(Level.INFO,"%10s	%10s %10s %10s %10s %10s" , StatusEnum.NOTREADY, "end", userInfo.getStatusDBIDMap().get(StatusEnum.NOTREADY), null, null, null);
					
					// 直接處理,不再呼叫一次本方法
					AgentFunction.RecordStatusEnd(userInfo.getStatusDBIDMap().get(StatusEnum.NOTREADY));
					// 清理Bean
//					StatusEnum currStatusEnum = StatusEnum.getStatusEnumByDbid(status_dbid);
					userInfo.getStatusDBIDMap().remove(StatusEnum.NOTREADY);
				}
				
				// 將Agent加入到ReadyAgentQueue
				WebSocketUserPool.getReadyAgentQueue().offer(userid);
				Util.getConsoleLogger().debug("(READY)WebSocketUserPool.getReadyAgentQueue().size(): " + WebSocketUserPool.getReadyAgentQueue().size());

				
				
			}// end of READY
			
			if (StatusEnum.NOTREADY.getDbid().equals(status_dbid) &&
					userInfo.isNotReady()){
					Util.getConsoleLogger().debug("Agent is NOTREADY already. No update is processed to DB");
					return;
			}else if(StatusEnum.NOTREADY.getDbid().equals(status_dbid)){
					dbid = AgentFunction.RecordStatusStart(userid, status_dbid, reason_dbid); // 重要
					// 去除ReadyAgent
					boolean result = WebSocketUserPool.getReadyAgentQueue().remove(userid); // 重要
					Util.getConsoleLogger().debug("(NOTREADY)WebSocketUserPool.getReadyAgentQueue().remove(userid): " + result);
					Util.getConsoleLogger().debug("(NOTREADY)WebSocketUserPool.getReadyAgentQueue().size(): " + WebSocketUserPool.getReadyAgentQueue().size());				
					
					// READY狀態結束(須排除初次登入狀況)
					if (userInfo.getStatusDBIDMap().get(StatusEnum.READY) != null){
						
						Util.getStatusFileLogger().info("updateStatus: " + "end" + " - " + StatusEnum.READY + " - " + username);
						Util.getStatusFileLogger().info("" + currStatusEnum + ": ");
						Util.getStatusFileLogger().printf(Level.INFO,"%10s	%10s %10s %10s %10s %10s" , "status", "startORend", "dbid", "roomID", "clientID", "reason");
						Util.getStatusFileLogger().info("----------------------------------------------------------------------------");
						Util.getStatusFileLogger().printf(Level.INFO,"%10s	%10s %10s %10s %10s %10s" , StatusEnum.READY, "end", userInfo.getStatusDBIDMap().get(StatusEnum.READY), null, null, null);

						// 直接處理,不再呼叫一次本方法
						AgentFunction.RecordStatusEnd(userInfo.getStatusDBIDMap().get(StatusEnum.READY));
						// 清理Bean
//						StatusEnum currStatusEnum = StatusEnum.getStatusEnumByDbid(status_dbid);
						userInfo.getStatusDBIDMap().remove(StatusEnum.READY);
					}
			}// end of NOTREADY			

			if (StatusEnum.LOGIN.getDbid().equals(status_dbid)){
				dbid = AgentFunction.RecordStatusStart(userid, status_dbid, "0"); // 重要
			}// end of LOGIN

			if (StatusEnum.LOGOUT.getDbid().equals(status_dbid)){
				dbid = AgentFunction.RecordStatusStart(userid, status_dbid, "0"); // 重要
			}// end of LOGOUT
			
			if (StatusEnum.RING.getDbid().equals(status_dbid)){
				dbid = AgentFunction.RecordStatusStart(userid, status_dbid, "0"); // 重要
				userInfo.setStopRing(false); // 回復為預設false
				userInfo.setTimeout(false); // 回復為預設false
				userInfo.setRingEndExpected(false); // 回復為預設false
				WebSocket clientConn = WebSocketUserPool.getWebSocketByUserID(clientID);
				RingCountDownTask ringCountDownTask = new RingCountDownTask(clientConn, dbid, userInfo);
				ringCountDownTask.operate();
			}// end of RING
			
			if (StatusEnum.IESTABLISHED.getDbid().equals(status_dbid)){
				dbid = AgentFunction.RecordStatusStart(userid, status_dbid, "0"); // 重要
				Util.getConsoleLogger().debug("IESTABLISHED - roomID: " + roomID);
				RoomInfo roomInfo = WebSocketRoomPool.getRoomInfo(roomID);
				roomInfo.setIestablish_dbid(dbid);
			}// end of IESTABLISHED
			
			if (StatusEnum.AFTERCALLWORK.getDbid().equals(status_dbid)){
				dbid = AgentFunction.RecordStatusStart(userid, status_dbid, "0"); // 重要
			}// end of AFTERCALLWORK
			
			if (StatusEnum.OESTABLISHED.getDbid().equals(status_dbid)){
				dbid = AgentFunction.RecordStatusStart(userid, status_dbid, "0"); // 重要
			}// end of OESTABLISHED

			if (StatusEnum.REJECT.getDbid().equals(status_dbid)){
				// REJECT開始狀態
				dbid = AgentFunction.RecordStatusStart(userid, status_dbid, "0"); // 重要
				// REJECT結束狀態
				AgentFunction.RecordStatusEnd(dbid);
			}// end of OESTABLISHED
			
			
			// for all
			String dbid_key = currStatusEnum.toString().toLowerCase() + "_dbid";
//			Util.getConsoleLogger().debug("dbid_key: " + dbid_key);
			obj.addProperty(dbid_key, dbid); // ex. login_dbid
			userInfo.getStatusDBIDMap().put(currStatusEnum, dbid); // 更新Bean
			Util.getConsoleLogger().debug("userInfo.getStatusDBIDMap().get(currStatusEnum): " + userInfo.getStatusDBIDMap().get(currStatusEnum));
			
		}else if ("end".equals(startORend)){ 
			if (dbid != null){
				
				// 如果是RING結束,現在交由後端統一處理
				if (StatusEnum.RING.getDbid().equals(status_dbid)){
					userInfo.setRingEndExpected(true);
					userInfo.setStopRing(true);
					return;
				}
				
				AgentFunction.RecordStatusEnd(dbid);
				
				// 若是AFTERCALLWORK,則告知前端將狀態更新為DB設定狀態
				if (StatusEnum.AFTERCALLWORK.getDbid().equals(status_dbid)){
					JsonObject ACWCountMsg = new JsonObject();
					ACWCountMsg.addProperty("Event", "updateStatus");
					if (userInfo.isReady()){
						ACWCountMsg.addProperty("currStatusEnum", StatusEnum.READY.toString());
					}else if (userInfo.isNotReady()){
						ACWCountMsg.addProperty("currStatusEnum", StatusEnum.NOTREADY.toString());
					}
					WebSocketUserPool.sendMessageToUserWithTryCatch(aConn, ACWCountMsg.toString());
//					return; // 先於此停住,若以後需要回傳結束事件時,再加上去
				}
								
				// 清理Bean
//				StatusEnum currStatusEnum = StatusEnum.getStatusEnumByDbid(status_dbid);
				userInfo.getStatusDBIDMap().remove(currStatusEnum);
			}
		}// end of if "start" or "end"
		
		// 寄送訊息
		// "start"時dbid_key會對應到值, 寄送EVENT,讓前端能拿到相對應的dbid
		obj.addProperty("Event", "updateStatus");
		obj.addProperty("currStatusEnum", currStatusEnum.toString());
		if (aConn.isClosing() || aConn.isClosed()) return;
		WebSocketUserPool.sendMessageToUserWithTryCatch(aConn, obj.toString());
		
	}
	
	public static void refreshRoomList(org.java_websocket.WebSocket conn){
		Util.getConsoleLogger().debug("refreshRoomList() called");
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
//		Util.getConsoleLogger().debug("roomIDList.size(): " + roomIDList.size());
		for (String roomID: roomIDList){
			RoomInfo roomInfo = WebSocketRoomPool.getRoomInfo(roomID);
			UserInfo clientInfo = WebSocketUserPool.getUserInfoByKey(roomInfo.getClientConn());
			roomIDListJson.add(roomID);
			memberListJson.add(WebSocketRoomPool.getOnlineUserNameinroom(roomID).toString());
			clietIDListJson.add(clientInfo.getUserid());
			
			// 如果有room歷史訊息,則放到room物件中傳給client
			if (WebSocketUserPool.getUserInteractionByKey(roomInfo.getClientConn()) != null){
				JsonObject msgJsonOld = Util.getGJsonObject(WebSocketUserPool.getUserInteractionByKey(roomInfo.getClientConn()));
				Util.getConsoleLogger().debug("setinteraction() - msgJsonOld: " + msgJsonOld);
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
		
//		Util.getConsoleLogger().debug("roomIDListJson.size(): " + roomIDListJson.size());
//		Util.getConsoleLogger().debug("sendJson: " + sendJson);
		
		
		// end of 將此Agent所屬的Room list塞入json中
		WebSocketUserPool.sendMessageToUserWithTryCatch(conn, sendJson.toString());
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
			Util.getConsoleLogger().debug("agent: " + agent);
			// 再來跟每個Agent講要更新client list了
		}
		
	}
	
	public static void onCloseHelper(org.java_websocket.WebSocket aConn, String aReason){
		Util.getFileLogger().info("onCloseHelper() called - userName: " + WebSocketUserPool.getUserNameByKey(aConn));
		Util.getConsoleLogger().info("onCloseHelper() called - userName: " + WebSocketUserPool.getUserNameByKey(aConn));
		
		// 此方法沒有用到,先放著,並不會影響到主流程
		//userLeave(conn);
		UserInfo userInfo = WebSocketUserPool.getUserInfoByKey(aConn);
		if (WebSocketTypePool.isAgent(aConn)){
			userInfo.setClosing(true);
		}
		// 將Heartbeat功能移轉到這裡:
		onClose_inputInteractionLog(aConn,aReason);
		onClose_updateStatus(aConn);
		onClose_clearUserData(aConn); // 包含removeUser, removerUserinTYPE, removeUserinroom
		
		// 確認最後資訊:
		Util.getConsoleLogger().debug("WebSocketUserPool.getUserCount(): " + WebSocketUserPool.getUserCount());
		Util.getConsoleLogger().debug("WebSocketUserPool.getUserCount(): " + WebSocketUserPool.getUserCount());
	}
	
	private static void onClose_updateStatus(org.java_websocket.WebSocket aConn) { // here
		Util.getConsoleLogger().debug("onClose_updateStatus() called");
		if (!WebSocketTypePool.isAgent(aConn)) return;
		
		UserInfo agentUserInfo = WebSocketUserPool.getUserInfoByKey(aConn);
		Map<StatusEnum, String> statusDBIDMap = agentUserInfo.getStatusDBIDMap();
		Set<StatusEnum> keys = new HashSet<>( statusDBIDMap.keySet() ); // 因為於更新end後,會將此StatusEnum key從map中移除->使用shallow copy避免出現concurrent Exception
		UpdateStatusBean usb = null;
		for (StatusEnum currStatusEnum : keys){ 
			if (currStatusEnum.getDbid() == null) continue;
			Util.getConsoleLogger().debug("currStatusEnum: " + currStatusEnum + " update end time");
			usb = new UpdateStatusBean();
			usb.setDbid(statusDBIDMap.get(currStatusEnum));
			usb.setStatus(currStatusEnum.getDbid());
			usb.setStartORend("end");
			
			if (StatusEnum.RING == currStatusEnum){
				// 若到了此處,agentUserInfo.setRingEndExpected(true);仍然未設定,則表示此次RING關閉並非預期內
				agentUserInfo.setStopRing(true);
				Util.getConsoleLogger().debug("RING set to Stopped");
				continue;
			}
			
			if (StatusEnum.IESTABLISHED == currStatusEnum){
				// removeUserinroom(...)會負責處理removeUserinroom
				continue;
			}
			
			if (StatusEnum.NOTREADY == currStatusEnum){
				// 可於此處設定系統Logout或中斷離線時,預設的notreadyreason
			}
			
			Util.getStatusFileLogger().info("###### [onClose()]");
			CommonFunction.updateStatus(new Gson().toJson(usb), aConn);	
		}
		// 如果有dbid在,則寫入結束時間
		
	}// end of updateStatus()
	
	private static void onClose_inputInteractionLog(org.java_websocket.WebSocket conn, String reason) {
		Util.getConsoleLogger().debug("onClose_inputInteractionLog() called");
		// 若不是Client, 就離開此方法
		if (!WebSocketTypePool.isClient(conn)) return;
		
		// only Client stores userInteraction
		String message = WebSocketUserPool.getUserInteractionByKey(conn); // 一定取得到: 1. 在user login時就會呼叫setUserInteraction, 2. 在user logut or 重整時也會呼叫setUserInteraction
		Util.getConsoleLogger().debug("message: " + message);
		String username = WebSocketUserPool.getUserNameByKey(conn);
		
		// 目前只有client端會再登入時、登出時、重整時寫入Log
		if (message != null){
			JsonObject obj = Util.getGJsonObject(message);
			String closefrom = Util.getGString(obj, "closefrom");
			Util.getConsoleLogger().debug("closefrom: " + closefrom);
			// 正常登出是"client", 重整是空值, "default"待確認
			if (closefrom == null || closefrom.equals("default")) {
				obj.addProperty("status", 3);
				obj.addProperty("stoppedreason", "server:HeartBeatLose"); // 看之後是否考慮更改為變數reason
				obj.addProperty("closefrom", "server:HeartBeatLose"); 
			}
//			// 更新RoomOwnerAgentID
//			String roomID =  obj.getString("ixnid"); // ixnid 就是 roomID
//			obj.put("agentid", WebSocketRoomPool.getRoomInfo(roomID).getRoomOwnerAgentID()); // 取代前端傳入值
			if (WebSocketTypePool.isClient(conn)){
				UserInfo clientUserInfo = WebSocketUserPool.getUserInfoByKey(conn);
				if (clientUserInfo.getRoomOwner() != null)
					obj.addProperty("agentid", clientUserInfo.getRoomOwner()); 
			}
			
			// 最後寫入
			ClientFunction.interactionlog(obj.toString(), conn);			
		}// end of if

	}// end of interaction
	
	public static void onClose_clearUserData(org.java_websocket.WebSocket conn) {
		Util.getConsoleLogger().debug("onClose_clearUserData() called");
		
		/** 清RingCountDownConfTask **/
		if (WebSocketTypePool.isAgent(conn)){
			UserInfo agentUserInfo = WebSocketUserPool.getUserInfoByKey(conn);
			agentUserInfo.setStopConfRing(true);			
		}
		/** 清ReadyAgentQueue **/
		if (WebSocketTypePool.isAgent(conn)){
			String userid = WebSocketUserPool.getUserID(conn);
			boolean result = false;
			if ( WebSocketUserPool.getReadyAgentQueue().contains(userid)){
				result = WebSocketUserPool.getReadyAgentQueue().remove(userid);
			}
			Util.getConsoleLogger().debug("(onClose)WebSocketUserPool.getReadyAgentQueue().remove(userid): " + result);
			Util.getConsoleLogger().debug("(onClose)WebSocketUserPool.getReadyAgentQueue().size(): " + WebSocketUserPool.getReadyAgentQueue().size());
		}
		
		/** 清Client-findAgentTaskResult **/
		if (WebSocketTypePool.isClient(conn)){
			// 若有正在執行的findAgent sub thread正在等待,中斷它
			UserInfo clientUserInfo = WebSocketUserPool.getUserInfoByKey(conn);
			if (clientUserInfo.getFindAgentTaskResult() != null){
				clientUserInfo.getFindAgentTaskResult().cancel(true);
			}
			// 若此Client曾經寄出的請求,仍然在queue排隊,移除它
			if (WebSocketUserPool.getClientfindagentqueue().contains(clientUserInfo.getFindAgentCallable())){
				Util.getFileLogger().info(FindAgentThread.TAG + " onClose - before queue size: " + WebSocketUserPool.getClientfindagentqueue().size());
				WebSocketUserPool.getClientfindagentqueue().remove(clientUserInfo.getFindAgentCallable());
				Util.getFileLogger().info(FindAgentThread.TAG + " onClose - after queue size: " + WebSocketUserPool.getClientfindagentqueue().size());
			}
		}

		/** 清ROOM **/
		// 取得一個user所屬的所有roomid
		List<String> roomids = WebSocketUserPool.getUserRoomByKey(conn);
		List<String> tmpRoomids = new ArrayList<String>(roomids); // 重要: 為了接下來要動態移除掉UserInfo.userRoom List, 為了在跑迴圈時仍保留著reference variable, 故須用light copy建立另一相對物件, 這樣就能實現動態刪除
		Util.getConsoleLogger().debug("before - roomids.size(): " + roomids.size());
		Iterator<String> itr = tmpRoomids.iterator();
		while(itr.hasNext()){
			String roomid = itr.next();
			Util.getConsoleLogger().debug("***** get roomid: " + roomid);
			//使用每個roomid,並找出相對應的room,再將其中的conn remove掉
			WebSocketRoomPool.removeUserinroom(roomid, conn); // 當room裡面user為 <=1 時,會把room從conatiner中去除掉,所以才需要light copy
		}
		Util.getConsoleLogger().debug("after - roomids.size(): " + roomids.size());

		/** 清TYPE **/
		if (WebSocketTypePool.isAgent(conn)){
			WebSocketTypePool.removeUserinTYPE("Agent", conn);			
		}else if(WebSocketTypePool.isClient(conn)){
			WebSocketTypePool.removeUserinTYPE("Client", conn);			
		}
		
		// 此段先不更動,暫時調整清TYPE,確保會清理到
		String ACType = WebSocketUserPool.getACTypeByKey(conn);
		/*** 更新Agent list - 私訊用 ***/
		if ("Agent".equals(ACType)){
			AgentFunction.refreshAgentList();
		}
		
		// 清USER:
		WebSocketUserPool.removeUser(conn);
	}// end of clearUserData
	



}
