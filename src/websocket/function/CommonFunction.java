package websocket.function;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;
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
import websocket.bean.UpdateStatusBean;
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
//		Util.getConsoleLogger().debug("conn" +  conn);
//		Util.getConsoleLogger().debug("sendto" +  sendto);
		WebSocketUserPool.sendMessageToUser(conn, obj.toString());
		WebSocketUserPool.sendMessageToUser(sendto, obj.toString());
	}
	
	/** * user join websocket * @param user */
	public static void userjoin(String user, org.java_websocket.WebSocket aConn) {
		Util.getConsoleLogger().debug("userjoin() called");
		JSONObject obj = new JSONObject(user);
		String username = obj.getString("UserName");
		String MaxCount = obj.getString("MaxCount"); //新增 MaxCount
//		Util.getConsoleLogger().debug("MaxCount: "+MaxCount);
		String ACtype = obj.getString("ACtype");
		String channel = obj.getString("channel");
		String userId = null;
		if(ACtype.equals("Agent")){
			userId = obj.get("id").toString(); //20170222 Lin
		}else if(ACtype.equals("Client")){
			userId = java.util.UUID.randomUUID().toString();
		}
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
		AgentFunction.GetAgentReasonInfo("0");
		sendjson.put("reasonList", Util.getAgentReason()); // 此key-value只須Agent接就好(先不做過濾) ; 此內容已於伺服器啟動時拿取
		Util.getConsoleLogger().debug("Util.getAgentReason(): " + Util.getAgentReason());
		Util.getFileLogger().info("Util.getAgentReason(): " + Util.getAgentReason());
		Util.getConsoleLogger().debug("Util.getAgentStatus(): " + Util.getAgentStatus());
		Util.getFileLogger().info("Util.getAgentStatus(): " + Util.getAgentStatus());
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
	}
	
	/** ask online people **/
	public static void online(String message, org.java_websocket.WebSocket conn) {
		WebSocketUserPool.sendMessageToUser(conn, "online people: "
				+ WebSocketUserPool.getOnlineUser().toString());
	}
	
	/** * user leave websocket */
	public static void userExit(String aMsg, org.java_websocket.WebSocket aConn) {
		Util.getConsoleLogger().debug("userExit() called");
		JsonObject jsonIn = Util.getGJsonObject(aMsg);
		String id = jsonIn.get("id").getAsString();
		String UserName = jsonIn.get("UserName").getAsString();
				
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
				WebSocket agentConn = WebSocketUserPool.getWebSocketByUser(waittingAgentID);
//				Util.getConsoleLogger().debug("userExit() - waittingAgentID: " + waittingAgentID);
				// "clientLeft"
				JsonObject jsonTo = new JsonObject();
				jsonTo.addProperty("Event", "clientLeft");
				jsonTo.addProperty("from", WebSocketUserPool.getUserID(aConn));
				
				WebSocketUserPool.sendMessageToUser(agentConn, jsonTo.toString());
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
				    WebSocket clientConn = WebSocketUserPool.getWebSocketByUser(clientID);
				    Util.getConsoleLogger().debug("userExit() - waitting clientID: " + clientID);
				    jsonIn.addProperty("Event", "agentLeft");
					WebSocketUserPool.sendMessageToUser(clientConn, jsonIn.toString());
				    
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
				    WebSocket agentConn = WebSocketUserPool.getWebSocketByUser(agentID);
//				    Util.getConsoleLogger().debug("userExit() - agentID: " + agentID);
				    jsonIn.addProperty("Event", "agentLeftThirdParty");
					WebSocketUserPool.sendMessageToUser(agentConn, jsonIn.toString());
				}
			}
//			for(String clientID: waittingClientIDList){
//				Util.getConsoleLogger().debug("userExit() - " + clientID);
//			}
		}
		
		// 寄送Exit訊息回去,告知server已處理好,可以關閉連線了
	    JsonObject jsonOut = new JsonObject();
		jsonOut.addProperty("Event", "Exit");
		WebSocketUserPool.sendMessageToUser(aConn, jsonOut.toString());

		//Billy哥部分前端需求:
		String joinMsg = "[Server] - " + UserName + " Offline";
		WebSocketUserPool.sendMessageToUser(aConn, joinMsg); // 只須原登出Agent收到此訊息即可
		
		
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
		
			// 更新UserInteraction 
		String userinteractionMsg = WebSocketUserPool.getUserInteractionByKey(clientConn);
		JsonObject userinteractionJsonMsg = Util.getGJsonObject(userinteractionMsg);
//		Util.getConsoleLogger().debug("getMessageinRoom() - userinteractionMsg: " + userinteractionMsg);
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
//		Util.getConsoleLogger().debug("after - getMessageinRoom() - userinteractionMsg: " + WebSocketUserPool.getUserInteractionByKey(roomInfo.getClientConn()));

		// 將訊息寄給room線上使用者:
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
		WebSocketUserPool.sendMessageToUser(conn, "room people: "
				+ WebSocketRoomPool.getOnlineUserNameinroom(roomID).toString());
	}
	
	/** * search online people from Agent or client */
	public static void onlineinTYPE(String message, org.java_websocket.WebSocket conn) {
//		Util.getConsoleLogger().debug("onlineinTYPE");
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
					
//					UpdateStatusBean usb = new UpdateStatusBean();
//					usb.setStatus(StatusEnum.NOTREADY.getDbid());
//					usb.setDbid(userInfo.getStatusDBIDMap().get(StatusEnum.NOTREADY));
//					usb.setStartORend("end");
//					CommonFunction.updateStatus(new Gson().toJson(usb), aConn);
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
//						
//						UpdateStatusBean usb = new UpdateStatusBean();
//						usb.setStatus(StatusEnum.READY.getDbid());
//						usb.setDbid(userInfo.getStatusDBIDMap().get(StatusEnum.READY));
//						usb.setStartORend("end");
//						CommonFunction.updateStatus(new Gson().toJson(usb), aConn);
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
				WebSocket clientConn = WebSocketUserPool.getWebSocketByUser(clientID);
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
					userInfo.setStopRing(true);
					return;
				}
				
				AgentFunction.RecordStatusEnd(dbid);
				
				// 清理Bean
//				StatusEnum currStatusEnum = StatusEnum.getStatusEnumByDbid(status_dbid);
				userInfo.getStatusDBIDMap().remove(currStatusEnum);
			}
		}// end of if "start" or "end"
		
		// "start"時dbid_key會對應到值, 寄送EVENT,讓前端能拿到相對應的dbid
		obj.addProperty("Event", "updateStatus");
		obj.addProperty("currStatusEnum", currStatusEnum.toString());
		if (aConn.isClosing() || aConn.isClosed()) return;
		WebSocketUserPool.sendMessageToUser(aConn, obj.toString());
		
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
			Util.getConsoleLogger().debug("agent: " + agent);
			// 再來跟每個Agent講要更新client list了
		}
		
	}
	
}
