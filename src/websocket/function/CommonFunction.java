package websocket.function;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import org.java_websocket.WebSocket;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import util.Util;









import com.google.gson.JsonParser;

import websocket.HeartBeat;
import websocket.bean.RoomInfo;
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
	public static void userjoin(String user, org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(user);
		String userId = java.util.UUID.randomUUID().toString();
		String username = obj.getString("UserName");
		String ACtype = obj.getString("ACtype");
		String joinMsg = "[Server]" + username + " Online";
		String channel = obj.getString("channel");
		WebSocketUserPool.addUser(username, userId, conn, ACtype); // 在此刻,已將user conn加入倒Pool中
		WebSocketUserPool.sendMessage(joinMsg);
		
		/*** 告知user其成功登入的UserID ***/
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "userjoin");
		sendjson.put("from", userId);
		sendjson.put("channel", channel);
		WebSocketUserPool.sendMessageToUser(conn, sendjson.toString());
		WebSocketUserPool.sendMessage("online people: "
				+ WebSocketUserPool.getOnlineUser().toString());
		
		/*** 將user加入到各自的TYPEmap ***/
		SimpleDateFormat sdf = new SimpleDateFormat(Util.getSdfTimeFormat());
		String date = sdf.format(new java.util.Date());
		WebSocketTypePool.addUserinTYPE(ACtype, username, userId, date, conn);
		
		/** 告訴Agent開啟layui **/
		JSONObject sendjson2 = new JSONObject();
		sendjson2.put("Event", "userjointoTYPE");
		sendjson2.put("from", userId);
		sendjson2.put("username",  username);
		sendjson2.put("ACtype", ACtype);
		sendjson2.put("channel", channel);
		WebSocketUserPool.sendMessage(sendjson2.toString());
		
		/*** 讓Agent與Client都有Heartbeat ***/
		HeartBeat heartbeat = new HeartBeat();
		heartbeat.heartbeating(conn);
	}
	
	/** ask online people **/
	public static void online(String message, org.java_websocket.WebSocket conn) {
		WebSocketUserPool.sendMessageToUser(conn, "online people: "
				+ WebSocketUserPool.getOnlineUser().toString());
	}
	
	/** * user leave websocket */
	public static void userExit(String user, org.java_websocket.WebSocket conn) {
		System.out.println("userExit() called");
		JSONObject obj = new JSONObject(user);
		String username = obj.getString("UserName");
//		user = WebSocketUserPool.getUserByKey(conn);
		String joinMsg = "[Server]" + username + " Offline";
		WebSocketUserPool.sendMessage(joinMsg);
//		WebSocketUserPool.removeUser(conn);
		Timer timer = WebSocketUserPool.getUserHeartbeatTimerByKey(conn);
		// 取消HeartBeat
		if (timer != null){
			timer.cancel();			
		}

		conn.close();
//		WebSocketPool.removeUserID(conn);
//		WebSocketPool.removeUserName(conn);
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
		WebSocketRoomPool.addUserinroom(roomID, username, userid, conn); //重要步驟
		WebSocketUserPool.addUserRoom(roomID, conn); //重要步驟
		WebSocketRoomPool.sendMessageinroom(roomID, joinMsg);
		WebSocketRoomPool.sendMessageinroom(roomID, "room people: "
				+ WebSocketRoomPool.getOnlineUserinroom(roomID).toString());
		
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
//		JSONObject obj = new JSONObject(message);
//		String roomID = obj.getString("roomID");
//		String userid = obj.getString("id");
//		String username = obj.getString("UserName");
//		String text = obj.getString("text");
//		JSONObject sendjson = new JSONObject();
//		sendjson.put("Event", "messagetoRoom");
//		sendjson.put("from", userid);
//		sendjson.put("username", username);
//		sendjson.put("message", text);
//		sendjson.put("channel", obj.getString("channel"));
//		WebSocketRoomPool.sendMessageinroom(roomID, sendjson.toString());
		
		// 拿取資料(gson)
		JsonParser jsonParser = new JsonParser(); 
		JsonObject msgJson = jsonParser.parse(message).getAsJsonObject();
		System.out.println("initial msgJson: "+ msgJson);
		
		// 將新訊息更新到RoomInfo bean上
		System.out.println("msgJson.text: " + msgJson.get("text").getAsString());
		RoomInfo roomInfo = WebSocketRoomPool.getRoomInfo(msgJson.get("roomID").getAsString());
			// 更新text
		StringBuilder text = roomInfo.getText();
		text.append(msgJson.get("UserName").getAsString() + ": " + msgJson.get("text").getAsString() + "\n");
		System.out.println("roomInfo.getText()\n" + roomInfo.getText()); // for debugging
			// 更新structuredtext
		JsonArray structuredtext = roomInfo.getStructuredtext();
		SimpleDateFormat sdf = new SimpleDateFormat(Util.getSdfDateTimeFormat());
		String dateStr = sdf.format(new java.util.Date());
		msgJson.addProperty("date", dateStr);
		structuredtext.add(msgJson);
		System.out.println("roomInfo.getStructuredtext()\n" + roomInfo.getStructuredtext()); // for debugging
		
		// 將訊息寄給room線上使用者:
		if (msgJson.get("roomID") == null) return;
		msgJson.addProperty("Event", "messagetoRoom");
		WebSocketRoomPool.sendMessageinroom(msgJson.get("roomID").getAsString(), msgJson.toString());
		
		System.out.println("final msgJson: \n"+ msgJson); // for debugging
	}
	
	// room
	/** * ask online people in room */
	public static void onlineinRoom(String message, org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String roomID = obj.getString("roomID");
		WebSocketUserPool.sendMessageToUser(conn, "room people: "
				+ WebSocketRoomPool.getOnlineUserinroom(roomID).toString());
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
		JSONObject obj = new JSONObject(message);
		String ACtype = obj.getString("ACtype");
		String username = obj.getString("UserName");
		String userid = obj.getString("id");
		String date = obj.getString("date");
		String status = obj.getString("status");
		if(status.equals("lose")){
			WebSocketTypePool.addleaveClient();
		}
		String reason = obj.getString("reason");
		WebSocketTypePool.UserUpdate(ACtype, username, userid, date, status, reason, conn);
	}
	
	public static void refreshRoomList(org.java_websocket.WebSocket conn){
		System.out.println("refreshRoomList() called");
		JsonObject sendJson = new JsonObject();
		sendJson.addProperty("Event", "refreshRoomList");
		sendJson.addProperty("UserID", conn.toString());
		
		JsonArray roomIDListJson = new JsonArray();
		JsonArray memberListJson = new JsonArray();
		
		// 將 此Agent所屬的Room list 塞入json中
		List<String> roomIDList = WebSocketUserPool.getUserRoomByKey(conn);
		System.out.println("roomIDList.size(): " + roomIDList.size());
		for (String roomID: roomIDList){
			roomIDListJson.add(roomID);
			memberListJson.add(WebSocketRoomPool.getOnlineUserinroom(roomID).toString());
		}
		
		// 將 membersInRoom list 塞入json中
		
		
		// 將 RoomContent list 塞入json中
		
		// 
		
		// 測試用-新增幾個room
//		roomList_json.put("2982ebfa-0359-4777-8746-e6de5e493712");
//		roomList_json.put("2982ebfa-0359-4777-8746-e6de5e493778");
		
		sendJson.add("roomList", roomIDListJson);
		sendJson.add("memberList", memberListJson);
		
		
		System.out.println("roomIDListJson.size(): " + roomIDListJson.size());
		System.out.println("sendJson: " + sendJson);
		
		
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
