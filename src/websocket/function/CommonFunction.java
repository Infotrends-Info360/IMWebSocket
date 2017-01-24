package websocket.function;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import org.java_websocket.WebSocket;
import org.json.JSONArray;
import org.json.JSONObject;








import websocket.HeartBeat;
import websocket.bean.TypeInfo;
//import websocket.HeartBeat;
import websocket.pools.WebSocketRoomPool;
import websocket.pools.WebSocketTypePool;
import websocket.pools.WebSocketUserPool;

//此類別給WebSocjet.java使用
public class CommonFunction {

	/** get private messages **/
	public static void getMessage(String message, org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String username = obj.getString("UserName");
		org.java_websocket.WebSocket sendto = WebSocketUserPool
				.getWebSocketByUser(obj.getString("sendto"));
		WebSocketUserPool.sendMessageToUser(sendto,
				username + " private message to " + obj.getString("sendto")
						+ ": " + obj.getString("text"));
		WebSocketUserPool.sendMessageToUser(conn, username + " private message to "
				+ obj.getString("sendto") + ": " + obj.getString("text"));
	}
	
	/** * user join websocket * @param user */
	public static void userjoin(String user, org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(user);
		String userId = java.util.UUID.randomUUID().toString();
		String username = obj.getString("UserName");
		String ACtype = obj.getString("ACtype");
		String joinMsg = "[Server]" + username + " Online";
		WebSocketUserPool.addUser(username, userId, conn, ACtype); // 在此刻,已將user conn加入倒Pool中
		WebSocketUserPool.sendMessage(joinMsg);
		
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "userjoin");
		sendjson.put("from", userId);
		sendjson.put("channel", obj.getString("channel"));
		WebSocketUserPool.sendMessageToUser(conn, sendjson.toString());
		WebSocketUserPool.sendMessage("online people: "
				+ WebSocketUserPool.getOnlineUser().toString());
		// 把下面取消掉:
		if(ACtype.equals("Client")){
			HeartBeat heartbeat = new HeartBeat();
			heartbeat.heartbeating(conn);
			// 告訴所有Agents更新等待的ClientList
			refreshClientList(user);
		}
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
		// 只有client有timer(HeartBeat)
		if (timer != null){
			timer.cancel();			
		}

		conn.close();
//		WebSocketPool.removeUserID(conn);
//		WebSocketPool.removeUserName(conn);
	}
	
	/** * user join group */
	public static void userjointoRoom(String message,
			org.java_websocket.WebSocket conn) {
		System.out.println("userjointoRoom() called - conn: " + conn);
		JSONObject obj = new JSONObject(message);
		String roomID = obj.getString("roomID");
		String userid = obj.getString("id");
		String username = obj.getString("UserName");
		String joinMsg = "[Server]" + username + " join " + roomID + " group";
		WebSocketRoomPool.addUserinroom(roomID, username, userid, conn);
		WebSocketUserPool.addUserRoom(roomID, conn);
		WebSocketRoomPool.sendMessageinroom(roomID, joinMsg);
		WebSocketRoomPool.sendMessageinroom(roomID, "group people: "
				+ WebSocketRoomPool.getOnlineUserinroom(roomID).toString());
		
		// 之後可做更詳細的判斷-如為Agent才執行就好
		// 尚有例外: JSONObject["ACtype"] not found.
		String ACtype = obj.getString("ACtype");
		if ("Agent".equals(ACtype)){
			System.out.println("userjointogroup - one Agent joined");
			refreshGroupList(conn);						
		}
		
//		String TYPE = "Agent";
//		Map<String, Map<WebSocket, TypeInfo>> TYPEconnections = WebSocketTypePool.getTypeconnections();
//		Map<WebSocket,  TypeInfo> TYPEmap = TYPEconnections.get(TYPE);
//		if (TYPEmap.containsKey(conn)){
//			System.out.println("userjointogroup - one Agent joined");
//			refreshGroupList(conn);			
//		}
		
	}
	
	/** * user leave group */
	public static void userExitfromRoom(String message,
			org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String roomID = obj.getString("roomID");
		String username = obj.getString("UserName");
		String joinMsg = "[Server]" + username + " leave " + roomID + " room";
		WebSocketRoomPool.sendMessageinroom(roomID, joinMsg);
//		WebSocketGroupPool.removeGroup(group); // 這邊要改成removeUseringroup()
		WebSocketRoomPool.removeUserinroom(roomID, conn);
	}
	
	/** * Get Message from Group */
	public static void getMessageingroup(String message,
			org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String group = obj.getString("group");
		String userid = obj.getString("id");
		String username = obj.getString("UserName");
		String text = obj.getString("text");
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "groupmessage");
		sendjson.put("from", userid);
		sendjson.put("username", username);
		sendjson.put("message", text);
		sendjson.put("channel", obj.getString("channel"));
		WebSocketRoomPool.sendMessageinroom(group, sendjson.toString());
	}
	
	// group
	/** * ask online people in group */
	public static void onlineingroup(String message, org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String group = obj.getString("group");
		WebSocketUserPool.sendMessageToUser(conn, "group people: "
				+ WebSocketRoomPool.getOnlineUserinroom(group).toString());
	}
	
	/** * search online people from Agent or client */
	public static void onlineinTYPE(String message, org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String ACtype = obj.getString("ACtype");
		WebSocketUserPool.sendMessageToUser(conn, ACtype + " people: "
				+ WebSocketTypePool.getOnlineUserNameinTYPE(ACtype).toString());
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "onlineinTYPE");
		sendjson.put("from", WebSocketTypePool.getOnlineUserIDinTYPE(ACtype)
				.toString().replace("[", "").replace("]", ""));
		sendjson.put("username",  WebSocketTypePool.getOnlineUserNameinTYPE(ACtype)
				.toString().replace("[", "").replace("]", ""));
		sendjson.put("ACtype", ACtype);
		sendjson.put("channel", obj.getString("channel"));
		WebSocketTypePool.sendMessageinTYPE(ACtype,sendjson.toString());
	}
	
	/** * user join from Agent or Client list */
	public static void userjointoTYPE(String message, org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String ACtype = obj.getString("ACtype");
		String userid = obj.getString("id");
		String username = obj.getString("UserName");
		String date = obj.get("date").toString();
		String joinMsg = "[Server]" + username + " join " + ACtype;
		WebSocketTypePool.addUserinTYPE(ACtype, username, userid, date, conn);
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
	
	private static void refreshGroupList(org.java_websocket.WebSocket conn){
		System.out.println("refreshGroupList() called");
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "refreshGroupList");
		sendjson.put("UserID", conn);
		// 將此Agent所屬的Group list塞入json中
		JSONArray groupList_json = new JSONArray();
		List<String> groupList = WebSocketUserPool.getUserRoomByKey(conn);
		//JSONObject groupList_json = new JSONObject();
		for (String group: groupList){
			groupList_json.put(group);
		}
		// 測試用-新增幾個group
//		groupList_json.put("2982ebfa-0359-4777-8746-e6de5e493712");
//		groupList_json.put("2982ebfa-0359-4777-8746-e6de5e493778");
		
		sendjson.put("groupList", groupList_json);
		System.out.println("groupList_json.length(): " + groupList_json.length());
		System.out.println("sendjson: " + sendjson);
		
		
		// end of 將此Agent所屬的Group list塞入json中
		WebSocketUserPool.sendMessageToUser(conn, sendjson.toString());
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
