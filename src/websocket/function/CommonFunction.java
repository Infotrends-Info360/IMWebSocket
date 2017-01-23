package websocket.function;

import java.util.Timer;

import org.json.JSONObject;



import websocket.HeartBeat;
//import websocket.HeartBeat;
import websocket.pools.WebSocketGroupPool;
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
		WebSocketUserPool.addUser(username, userId, conn); // 在此刻,已將user conn加入倒Pool中
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
		}
	}
	
	/** ask online people **/
	public static void online(String message, org.java_websocket.WebSocket conn) {
		WebSocketUserPool.sendMessageToUser(conn, "online people: "
				+ WebSocketUserPool.getOnlineUser().toString());
	}
	
	/** * user leave websocket */
	public static void userExit(String user, org.java_websocket.WebSocket conn) {
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
	public static void userjointogroup(String message,
			org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String group = obj.getString("group");
		String userid = obj.getString("id");
		String username = obj.getString("UserName");
		String joinMsg = "[Server]" + username + " join " + group + " group";
		WebSocketGroupPool.addUseringroup(group, username, userid, conn);
		WebSocketUserPool.addUserGroup(group, conn);
		WebSocketGroupPool.sendMessageingroup(group, joinMsg);
		WebSocketGroupPool.sendMessageingroup(group, "group people: "
				+ WebSocketGroupPool.getOnlineUseringroup(group).toString());
	}
	
	/** * user leave group */
	public static void userExitfromgroup(String message,
			org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String group = obj.getString("group");
		String username = obj.getString("UserName");
		String joinMsg = "[Server]" + username + " leave " + group + " group";
		WebSocketGroupPool.sendMessageingroup(group, joinMsg);
//		WebSocketGroupPool.removeGroup(group); // 這邊要改成removeUseringroup()
		WebSocketGroupPool.removeUseringroup(group, conn);
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
		WebSocketGroupPool.sendMessageingroup(group, sendjson.toString());
	}
	
	// group
	/** * ask online people in group */
	public static void onlineingroup(String message, org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String group = obj.getString("group");
		WebSocketUserPool.sendMessageToUser(conn, "group people: "
				+ WebSocketGroupPool.getOnlineUseringroup(group).toString());
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
	
}
