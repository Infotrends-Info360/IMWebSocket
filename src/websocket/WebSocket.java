package websocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

public class WebSocket extends WebSocketServer {
	public WebSocket(InetSocketAddress address) {
		super(address);
		System.out.println("IP address: " + address);
	}

	public WebSocket(int port) throws UnknownHostException {
		super(new InetSocketAddress(port));
		System.out.println("Port: " + port);
	}

	/** * trigger close Event */
	@Override
	public void onClose(org.java_websocket.WebSocket conn, int message,
			String reason, boolean remote) {
		userLeave(conn);
		System.out.println("Someone unlink in Socket conn:" + conn);
	}

	/** * trigger Exception Event */
	@Override
	public void onError(org.java_websocket.WebSocket conn, Exception message) {
// 先註解調,debug其他問題用
//		System.out.println("Socket Exception:" + message.toString());
//		message.printStackTrace();
		e++;
	}

	/** * trigger link Event */
	@Override
	public void onOpen(org.java_websocket.WebSocket conn,
			ClientHandshake handshake) {
		System.out.println("Someone link in Socket conn:" + conn);
		l++;
	}

	/** * The event is triggered when the client sends a message to the server */
	int j = 0;
	int h = 0;
	int e = 0;
	int l = 0;

	/**
	 * WebSocket GetMessage
	 * 
	 * message login online Exit addGroup leaveGroup messagetogroup grouponline
	 * typeonline typein typeout Agentclosegroup Clientclosegroup AcceptEvent
	 * Event updateStatus getUserStatus findAgent creategroupId senduserdata
	 */
	@Override
	public void onMessage(org.java_websocket.WebSocket conn, String message) {
		message = message.toString();
		// System.out.println(message);
		JSONObject obj = new JSONObject(message);
		switch (obj.getString("type").trim()) {
		case "message":
			this.getMessage(message.toString(), conn);
			break;
		case "login":
			this.userjoin(message.toString(), conn);
			break;
		case "online":
			this.online(message.toString(), conn);
			break;
		case "Exit":
			this.userExit(message.toString(), conn);
			break;
		case "addGroup":
			this.userjointogroup(message.toString(), conn);
			break;
		case "leaveGroup":
			this.userExitfromgroup(message.toString(), conn);
			break;
		case "messagetogroup":
			this.getMessageingroup(message.toString(), conn);
			break;
		case "grouponline":
			this.onlineingroup(message.toString(), conn);
			break;
		case "typeonline":
			this.onlineinTYPE(message.toString(), conn);
			break;
		case "typein":
			this.userjointoTYPE(message.toString(), conn);
			break;
		case "typeout":
			this.userExitfromTYPE(message.toString(), conn);
			break;
		case "Agentclosegroup":
			this.Agentclosegroup(message.toString(), conn);
			break;
		case "Clientclosegroup":
			this.Clientclosegroup(message.toString(), conn);
			break;
		case "AcceptEvent":
			this.AcceptEvent(message.toString(), conn);
			break;
		case "RejectEvent":
			this.RejectEvent(message.toString(), conn);
			break;
		case "ReleaseEvent":
			this.ReleaseEvent(message.toString(), conn);
			break;
		case "findAgentEvent":
			this.findAgentEvent(message.toString(), conn);
			break;
		case "updateStatus":
			this.updateStatus(message.toString(), conn);
			break;
		case "getUserStatus":
			this.getUserStatus(message.toString(), conn);
			break;
		case "findAgent":
			this.findAgent(message.toString(), conn);
			break;
		case "creategroupId":
			this.creategroupId(message.toString(), conn);
			break;
		case "senduserdata":
			this.senduserdata(message.toString(), conn);
			break;
		case "entrylog":
			this.entrylog(message.toString(), conn);
			break;
		case "interactionlog":
			this.interactionlog(message.toString(), conn);
			break;
		case "setinteraction":
			this.setinteraction(message.toString(), conn);
			break;
		case "heartbeattoserver":
			this.heartbeattoserver(message.toString(), conn);
			break;
		case "test":
			this.test();
			break;
		}
	}



	/** * create a groupId * @param message */
	public void creategroupId(String message, org.java_websocket.WebSocket conn) {
		String groupId = java.util.UUID.randomUUID().toString();
//		String sendjson = "{ \"Event\": \"creategroupId\", \"groupId\": \""
//				+ groupId + "\" }";
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "creategroupId");
		sendjson.put("groupId", groupId);
		//sendjson.put("channel", "chat");
		WebSocketPool.sendMessageToUser(conn, sendjson.toString());
	}

	/** * user join websocket * @param user */
	public void userjoin(String user, org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(user);
		String userId = java.util.UUID.randomUUID().toString();
		String username = obj.getString("UserName");
		String ACtype = obj.getString("ACtype");
		String joinMsg = "[Server]" + username + " Online";
		WebSocketPool.addUser(username, userId, conn);
		WebSocketPool.sendMessage(joinMsg);
//		String sendjson = "{ \"Event\": \"userjoin\", \"from\": \"" + userId
//				+ "\" }";
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "userjoin");
		sendjson.put("from", userId);
		sendjson.put("channel", obj.getString("channel"));
		WebSocketPool.sendMessageToUser(conn, sendjson.toString());
		WebSocketPool.sendMessage("online people: "
				+ WebSocketPool.getOnlineUser().toString());
		if(ACtype.equals("Client")){
			HeartBeat heartbeat = new HeartBeat();
			heartbeat.heartbeating(conn);
		}
	}

	/** get private messages **/
	public void getMessage(String message, org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String username = obj.getString("UserName");
		org.java_websocket.WebSocket sendto = WebSocketPool
				.getWebSocketByUser(obj.getString("sendto"));
		WebSocketPool.sendMessageToUser(sendto,
				username + " private message to " + obj.getString("sendto")
						+ ": " + obj.getString("text"));
		WebSocketPool.sendMessageToUser(conn, username + " private message to "
				+ obj.getString("sendto") + ": " + obj.getString("text"));
	}

	/** ask online people **/
	public void online(String message, org.java_websocket.WebSocket conn) {
		WebSocketPool.sendMessageToUser(conn, "online people: "
				+ WebSocketPool.getOnlineUser().toString());
	}

	/** * user leave websocket */
	public void userExit(String user, org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(user);
		String username = obj.getString("UserName");
		user = WebSocketPool.getUserByKey(conn);
		String joinMsg = "[Server]" + username + " Offline";
		WebSocketPool.sendMessage(joinMsg);
		WebSocketPool.removeUserID(conn);
		WebSocketPool.removeUserName(conn);
	}

	/** * user leave websocket (Demo) */
	public void userLeave(org.java_websocket.WebSocket conn) {
		String user = WebSocketPool.getUserByKey(conn);
		boolean b = WebSocketPool.removeUserID(conn);
		WebSocketPool.removeUserName(conn);
		if (b) {
			WebSocketPool.sendMessage(user.toString());
			String joinMsg = "[Server]" + user + "Offline";
			WebSocketPool.sendMessage(joinMsg);
		}
	}

	// group
	/** * ask online people in group */
	public void onlineingroup(String message, org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String group = obj.getString("group");
		WebSocketPool.sendMessageToUser(conn, "group people: "
				+ WebSocketPool.getOnlineUseringroup(group).toString());
		/*
		WebSocketPool.sendMessageingroup(group, "group people: "
				+ WebSocketPool.getOnlineUseringroup(group).toString());
				*/
	}

	/** * user leave group */
	public void userExitfromgroup(String message,
			org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String group = obj.getString("group");
		// String userId = WebSocketPool.getUserByKey(conn);
		String username = obj.getString("UserName");
		String joinMsg = "[Server]" + username + " leave " + group + " group";
		WebSocketPool.removeUserGroup(conn);
		WebSocketPool.sendMessageingroup(group, joinMsg);
		WebSocketPool.removeUseringroup(group, conn);
	}

	/** * user join group */
	public void userjointogroup(String message,
			org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String group = obj.getString("group");
		String userid = obj.getString("id");
		String username = obj.getString("UserName");
		String joinMsg = "[Server]" + username + " join " + group + " group";
		WebSocketPool.addUseringroup(group, username, userid, conn);
		WebSocketPool.addUserGroup(group, conn);
		WebSocketPool.sendMessageingroup(group, joinMsg);
		WebSocketPool.sendMessageingroup(group, "group people: "
				+ WebSocketPool.getOnlineUseringroup(group).toString());
	}

	/** * Get Message from Group */
	public void getMessageingroup(String message,
			org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String group = obj.getString("group");
		String userid = obj.getString("id");
		String username = obj.getString("UserName");
		// String date = obj.get("date").toString();
		String text = obj.getString("text");
//		String sendjson = "{ \"Event\": \"groupmessage\", \"from\": \""
//				+ userid + "\", \"username\": \"" + username
//				+ "\", \"message\": \"" + text + "\" }";
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "groupmessage");
		sendjson.put("from", userid);
		sendjson.put("username", username);
		sendjson.put("message", text);
		sendjson.put("channel", obj.getString("channel"));
		WebSocketPool.sendMessageingroup(group, sendjson.toString());
	}

	/** * search online people from Agent or client */
	public void onlineinTYPE(String message, org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String ACtype = obj.getString("ACtype");
		WebSocketPool.sendMessageToUser(conn, ACtype + " people: "
				+ WebSocketPool.getOnlineUserNameinTYPE(ACtype).toString());
		
		
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "onlineinTYPE");
		sendjson.put("from", WebSocketPool.getOnlineUserIDinTYPE(ACtype)
				.toString().replace("[", "").replace("]", ""));
		sendjson.put("username",  WebSocketPool.getOnlineUserNameinTYPE(ACtype)
				.toString().replace("[", "").replace("]", ""));
		sendjson.put("ACtype", ACtype);
		sendjson.put("channel", obj.getString("channel"));
		WebSocketPool.sendMessageinTYPE(ACtype,sendjson.toString());
	}

	/** * user leave from Agent or Client list */
	public void userExitfromTYPE(String message,
			org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String ACtype = obj.getString("ACtype");
		String userid = obj.getString("id");
		String username = obj.getString("UserName");
		String joinMsg = "[Server]" + username + " leave " + ACtype;
		WebSocketPool.sendMessageinTYPE(ACtype, joinMsg);
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "userExitfromTYPE");
		sendjson.put("from", userid);
		sendjson.put("username",  username);
		sendjson.put("ACtype", ACtype);
		sendjson.put("channel", obj.getString("channel"));
		WebSocketPool.sendMessageinTYPE(ACtype,sendjson.toString());
//				"{ \"Event\": \"userExitfromTYPE\", \"from\": \"" + userid
//						+ "\", \"username\": \"" + username
//						+ "\", \"ACtype\": \"" + ACtype + "\" }");
		WebSocketPool.removeUserinTYPE(ACtype, conn);
	}

	/** * user join from Agent or Client list */
	public void userjointoTYPE(String message, org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String ACtype = obj.getString("ACtype");
		String userid = obj.getString("id");
		String username = obj.getString("UserName");
		String date = obj.get("date").toString();
		String joinMsg = "[Server]" + username + " join " + ACtype;
		WebSocketPool.addUserinTYPE(ACtype, username, userid, date, conn);
		WebSocketPool.sendMessageinTYPE(ACtype, joinMsg);
		WebSocketPool.sendMessageinTYPE(ACtype, ACtype + " people: "
				+ WebSocketPool.getOnlineUserNameinTYPE(ACtype).toString());
//		String sendjson = "{ \"Event\": \"userjointoTYPE\", \"from\": \""
//				+ userid + "\", \"username\": \"" + username
//				+ "\", \"ACtype\": \"" + ACtype + "\" }";
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "userjointoTYPE");
		sendjson.put("from", userid);
		sendjson.put("username",  username);
		sendjson.put("ACtype", ACtype);
		sendjson.put("channel", obj.getString("channel"));
		WebSocketPool.sendMessage(sendjson.toString());
	}

	/** * Get Message from Agent or Client list */
	public void getMessageinTYPE(String message,
			org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String ACtype = obj.getString("ACtype");
		// String userId = obj.getString("id");
		String username = obj.getString("UserName");
		String text = obj.getString("text");
		WebSocketPool.sendMessageinTYPE(ACtype, username + ": " + text);
	}

	/** * Agent close Group */
	public void Agentclosegroup(String message,
			org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String group = obj.getString("group");
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "Agentclosegroup");
		sendjson.put("from", obj.getString("id"));
		sendjson.put("fromName",  obj.getString("UserName"));
		sendjson.put("channel", obj.getString("channel"));
		WebSocketPool.sendMessageingroup(
				group,sendjson.toString());
//				"{ \"Event\": \"Agentclosegroup\", \"from\": \""
//						+ obj.getString("id") + "\", \"fromName\": \""
//						+ obj.getString("UserName") + "\" }");
	}

	/** * Client close Group */
	public void Clientclosegroup(String message,
			org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String group = obj.getString("group");
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "Clientclosegroup");
		sendjson.put("from", obj.getString("id"));
		sendjson.put("fromName",  obj.getString("UserName"));
		sendjson.put("channel", obj.getString("channel"));
		WebSocketPool.sendMessageingroup(
				group,sendjson.toString());
//				"{ \"Event\": \"Clientclosegroup\", \"from\": \""
//						+ obj.getString("id") + "\", \"fromName\": \""
//						+ obj.getString("UserName") + "\" }");
	}

	/** * send Accept Event */
	public void AcceptEvent(String message, org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String group = obj.getString("group");
		org.java_websocket.WebSocket sendto = WebSocketPool
				.getWebSocketByUser(obj.getString("sendto"));
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "AcceptEvent");
		sendjson.put("from", obj.getString("id"));
		sendjson.put("fromName",  obj.getString("UserName"));
		sendjson.put("group",  group);
		sendjson.put("channel", obj.getString("channel"));
		WebSocketPool.sendMessageToUser(
				sendto,sendjson.toString());
//				"{ \"Event\": \"AcceptEvent\", \"from\": \""
//						+ obj.getString("id") + "\", \"fromName\": \""
//						+ obj.getString("UserName") + "\", \"group\": \""
//						+ group + "\" }");
	}
	
	/** * RejectEvent */
	public void RejectEvent(String message, org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
//		String Event = obj.getString("Event");
		org.java_websocket.WebSocket sendto = WebSocketPool
				.getWebSocketByUser(obj.getString("sendto"));
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "RejectEvent");
		sendjson.put("from", obj.getString("id"));
		sendjson.put("fromName",  obj.getString("UserName"));
		sendjson.put("channel", obj.getString("channel"));
		WebSocketPool.sendMessageToUser(sendto, sendjson.toString());
//				"{ \"Event\": \"" + Event
//				+ "\", \"from\": \"" + obj.getString("id")
//				+ "\", \"fromName\": \"" + obj.getString("UserName") + "\" }");
	}
	
	/** * ReleaseEvent */
	public void ReleaseEvent(String message, org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
//		String Event = obj.getString("Event");
		org.java_websocket.WebSocket sendto = WebSocketPool
				.getWebSocketByUser(obj.getString("sendto"));
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "ReleaseEvent");
		sendjson.put("from", obj.getString("id"));
		sendjson.put("fromName",  obj.getString("UserName"));
		sendjson.put("channel", obj.getString("channel"));
		WebSocketPool.sendMessageToUser(sendto, sendjson.toString());
//				"{ \"Event\": \"" + Event
//				+ "\", \"from\": \"" + obj.getString("id")
//				+ "\", \"fromName\": \"" + obj.getString("UserName") + "\" }");
	}
	
	/** * findAgentEvent */
	public void findAgentEvent(String message, org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
//		String Event = obj.getString("Event");
		org.java_websocket.WebSocket sendto = WebSocketPool
				.getWebSocketByUser(obj.getString("sendto"));
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "findAgentEvent");
		sendjson.put("from", obj.getString("id"));
		sendjson.put("fromName",  obj.getString("UserName"));
		sendjson.put("channel", obj.getString("channel"));
		WebSocketPool.sendMessageToUser(sendto, sendjson.toString());
//				"{ \"Event\": \"" + Event
//				+ "\", \"from\": \"" + obj.getString("id")
//				+ "\", \"fromName\": \"" + obj.getString("UserName") + "\" }");
	}
	
//	/** * other Event */
//	public void Event(String message, org.java_websocket.WebSocket conn) {
//		JSONObject obj = new JSONObject(message);
//		String Event = obj.getString("Event");
//		org.java_websocket.WebSocket sendto = WebSocketPool
//				.getWebSocketByUser(obj.getString("sendto"));
//		JSONObject sendjson = new JSONObject();
//		sendjson.put("Event", Event);
//		sendjson.put("from", obj.getString("id"));
//		sendjson.put("fromName",  obj.getString("UserName"));
//		WebSocketPool.sendMessageToUser(sendto, sendjson.toString());
////				"{ \"Event\": \"" + Event
////				+ "\", \"from\": \"" + obj.getString("id")
////				+ "\", \"fromName\": \"" + obj.getString("UserName") + "\" }");
//	}

	/** * update Agent Status */
	public void updateStatus(String message, org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String ACtype = obj.getString("ACtype");
		String username = obj.getString("UserName");
		String userid = obj.getString("id");
		String date = obj.getString("date");
		String status = obj.getString("status");
		if(status == "lose"){
			WebSocketPool.addleaveClient();
		}
//		String reason = "";
//		if(!status.equals("ready")){
//			reason = obj.getString("reason");
//		}
		String reason = obj.getString("reason");
		WebSocketPool.UserUpdate(ACtype, username, userid, date, status, reason, conn);
	}

	/** * get Agent Status */
	public void getUserStatus(String message, org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String ACtype = obj.getString("ACtype");
		String status = WebSocketPool.getUserStatusByKeyinTYPE(ACtype, conn);
		String reason = WebSocketPool.getUserReasonByKeyinTYPE(ACtype, conn);
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "getUserStatus");
		sendjson.put("from", obj.getString("id"));
		sendjson.put("Status",  status);
		sendjson.put("Reason",  reason);
		sendjson.put("channel", obj.getString("channel"));
		WebSocketPool.sendMessageToUser(
				conn, sendjson.toString());
//				"{ \"Event\": \"getUserStatus\", \"from\": \""
//						+ obj.getString("id") + "\", \"Status\": \"" + status
//						+ "\" }");
	}

	/** * find online Longest Agent */
	public void findAgent(String message, org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
//		String Event = obj.getString("Event");
		String Agent;
		try {
			Agent = WebSocketPool.getOnlineLongestUserinTYPE("Agent");
		} catch (Exception e) {
			Agent = null;
		}
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "findAgent");
		sendjson.put("from", obj.getString("id"));
		sendjson.put("Agent",  Agent);
		sendjson.put("channel", obj.getString("channel"));
		WebSocketPool.sendMessageToUser(conn, sendjson.toString());
//				"{ \"Event\": \"" + Event
//				+ "\", \"from\": \"" + obj.getString("id")
//				+ "\", \"Agent\": \"" + Agent + "\" }");
	}

	/** * Get user data */
	public void senduserdata(String message, org.java_websocket.WebSocket conn) {
		System.out.println(message);
		JSONObject obj = new JSONObject(message);
		String lang = obj.getString("lang");
		//String searchkey = obj.getString("searchkey");
		//String pkey = obj.getString("pkey");
		String searchtype = obj.getString("searchtype");
		JSONObject attributes = obj.getJSONObject("attributes");
		String attributenames = obj.getJSONObject("attributes").getString(
				"attributenames");
		StringBuilder responseSB = null;
		try {
			// Encode the query
			String postData = 
//					"searchkey=" + searchkey
//					+ "&pkey=" + pkey
//					+ 
					"&searchtype=" + searchtype
					+ "&attributes=" + attributes.toString()
					+ "&attributenames=" + attributenames
					+ "&lang=" + lang;
			// Connect to URL
			URL url = new URL(
					"http://127.0.0.1:8080/IMWebSocket/RESTful/searchUserdata");
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Length",
					String.valueOf(postData.length()));
			// Write data
			OutputStream os = connection.getOutputStream();
			os.write(postData.getBytes());
			// Read response
			responseSB = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), "UTF-8"));
			String line;
			while ((line = br.readLine()) != null)
				responseSB.append(line);
			// Close streams
			br.close();
			os.close();
			// System.out.println(responseSB);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		org.java_websocket.WebSocket sendto = WebSocketPool
				.getWebSocketByUser(obj.getString("sendto"));
		JSONObject responseSBjson = new JSONObject(responseSB.toString());
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "searchuserdata");
		sendjson.put("from", obj.getJSONObject("attributes").getString("id"));
		sendjson.put("userdata",  responseSBjson);
		sendjson.put("channel", obj.getString("channel"));
		WebSocketPool.sendMessageToUser(conn, sendjson.toString());
//				"{ \"Event\": \"searchuserdata\", \"from\": \""
//						+ obj.getJSONObject("attributes").getString("id")
//						+ "\", \"userdata\": " + responseSB + " }");
		WebSocketPool.sendMessageToUser(sendto, sendjson.toString());
//				"{ \"Event\": \"searchuserdata\", \"from\": \""
//						+ obj.getJSONObject("attributes").getString("id")
//						+ "\", \"userdata\": " + responseSB + " }");
	}

	/** * send entry log */
	public void entrylog(String message, org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String userid = obj.getString("userid");
		String username = obj.getString("username");
		String ipaddress = obj.getString("ipaddress");
		String browser = obj.getString("browser");
		String platfrom = obj.getString("platfrom");
		String channel = obj.getString("channel");
		String language = obj.getString("language");
		String enterkey = obj.getString("enterkey");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date now = new Date();
		String entertime = sdf.format(now);
		// String entertime = obj.getString("entertime");
		StringBuilder responseSB = null;
		try {
			// Encode the query
			String postData = "userid=" + userid + "&username=" + username
					+ "&entertime=" + entertime + "&ipaddress=" + ipaddress
					+ "&browser=" + browser + "&platfrom=" + platfrom
					+ "&channel=" + channel + "&language=" + language
					+ "&enterkey=" + enterkey;
			// Connect to URL
			URL url = new URL(
					"http://127.0.0.1:8080/IMWebSocket/RESTful/ServiceEntry");
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Length",
					String.valueOf(postData.length()));
			// Write data
			OutputStream os = connection.getOutputStream();
			os.write(postData.getBytes());
			// Read response
			responseSB = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), "UTF-8"));
			String line;
			while ((line = br.readLine()) != null)
				responseSB.append(line);
			// Close streams
			br.close();
			os.close();
			// System.out.println(responseSB);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// org.java_websocket.WebSocket sendto = WebSocketPool
		// .getWebSocketByUser(obj.getString("sendto"));
		// WebSocketPool.sendMessageToUser(sendto,
		// "{ \"Event\": \"searchuserdata\", \"from\": \""+obj.getJSONObject("attributes").getString("id")+"\", \"userdata\": "+responseSB+" }");
	}

	/** * send interaction log */
	public static void interactionlog(String message, org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);

		String contactid = null;
		String ixnid = null;
		String agentid = null;
		int status = 0;
		String typeid = null;
		int entitytypeid = 0;
		String subtypeid = null;
		String text = null;
		String structuredtext = null;
		String thecomment = null;
		String stoppedreason = null;
		String activitycode = null;
		String structuredmimetype = null;
		String subject = null;
		String closefrom = null;

		Set<String> keySet = obj.keySet();
		synchronized (keySet) {
			for (String key : keySet) {
				// String value = obj.getString(key);
				// userdatamap.put(key, value);
				switch (key) {
				case "contactid":
					contactid = obj.getString(key);
					break;
				case "ixnid":
					ixnid = obj.getString(key);
					break;
				case "agentid":
					agentid = obj.getString(key);
					break;
				case "status":
					status = obj.getInt(key);
					break;
				case "typeid":
					typeid = obj.getString(key);
					break;
				case "entitytypeid":
					entitytypeid = obj.getInt(key);
					break;
				case "subtypeid":
					subtypeid = obj.getString(key);
					break;
				case "text":
					text = obj.getString(key);
					break;
				case "structuredtext":
					structuredtext = obj.getString(key);
					break;
				case "thecomment":
					thecomment = obj.getString(key);
					break;
				case "stoppedreason":
					stoppedreason = obj.getString(key);
					break;
				case "activitycode":
					activitycode = obj.getString(key);
					break;
				case "structuredmimetype":
					structuredmimetype = obj.getString(key);
					break;
				case "subject":
					subject = obj.getString(key);
					break;
				case "closefrom":
					closefrom = obj.getString(key);
					break;
				}
			}
		}
		
		if(closefrom.equals("server:heartbeatlose")){
			status=3;
			activitycode=closefrom;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date now = new Date();
		String enddate = sdf.format(now);

		SimpleDateFormat getsdf = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Date startdated = null;
		try {
			startdated = getsdf.parse(obj.getString("startdate"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String startdate = sdf.format(startdated);
		// String entertime = obj.getString("entertime");
		StringBuilder responseSB = null;
		try {
			// Encode the query
			String postData = "contactid=" + contactid + "&ixnid=" + ixnid
					+ "&agentid=" + agentid + "&startdate=" + startdate
					+ "&enddate=" + enddate + "&status=" + status + "&typeid="
					+ typeid + "&entitytypeid=" + entitytypeid + "&subtypeid="
					+ subtypeid + "&text=" + text + "&structuredtext="
					+ structuredtext + "&thecomment=" + thecomment
					+ "&stoppedreason=" + stoppedreason + "&activitycode="
					+ activitycode + "&structuredmimetype="
					+ structuredmimetype + "&subject=" + subject;

			// Connect to URL
			URL url = new URL(
					"http://127.0.0.1:8080/IMWebSocket/RESTful/Interaction");
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Length",
					String.valueOf(postData.length()));
			// Write data
			OutputStream os = connection.getOutputStream();
			os.write(postData.getBytes());
			// Read response
			responseSB = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), "UTF-8"));
			String line;
			while ((line = br.readLine()) != null)
				responseSB.append(line);
			// Close streams
			br.close();
			os.close();
			// System.out.println(responseSB);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setinteraction(String message, org.java_websocket.WebSocket conn){
		System.out.println("setinteraction:"+message);
		WebSocketPool.addUserInteraction(message, conn);
	}
	
	public static void heartbeattouser(org.java_websocket.WebSocket conn){
		String User = WebSocketPool.getUserByKey(conn);
		if (User != null && !"".equals(User)) {
			JSONObject sendjson = new JSONObject();
			sendjson.put("Event", "heartbeattouser");
			sendjson.put("heartbeat", "AP");
			//sendjson.put("channel", "chat");
			WebSocketPool.sendMessageToUser(conn, sendjson.toString());
//					"{ \"Event\": \"heartbeattouser\", \"heartbeat\": \"AP\" }");
		}else{
			WebSocketPool.removeUserID(conn);
			WebSocketPool.removeUserName(conn);
			WebSocketPool.removeUserinTYPE("Client", conn);
			WebSocketPool.removeUserheartbeat(conn);
			String groupid = WebSocketPool.getUserGroupByKey(conn);
			if (groupid != null && !"".equals(groupid)) {
				WebSocketPool.removeUserGroup(conn);
				WebSocketPool.removeUseringroup(groupid, conn);
			}
		}
	}
	
	public void heartbeattoserver(String message, org.java_websocket.WebSocket conn){
		JSONObject obj = new JSONObject(message);
		String value = null;
		//String groupid = null;
		Boolean heartbeat = false;
		Set<String> keySet = obj.keySet();
		synchronized (keySet) {
			for (String key : keySet) {
				if(key.equals("heartbeat")){
					value = obj.getString("heartbeat");
					//WebSocketPool.addUserheartbeat(value, conn);
					heartbeat = true;
				}
//				if(key.equals("groupid")){
//					groupid = obj.getString("groupid");
//				}
			}
		}
		if(heartbeat){
			WebSocketPool.addUserheartbeat(value, conn);
		}else{
			WebSocketPool.removeUserID(conn);
			WebSocketPool.removeUserName(conn);
			WebSocketPool.removeUserinTYPE("Client", conn);
			WebSocketPool.removeUserheartbeat(conn);
			String groupid = WebSocketPool.getUserGroupByKey(conn);
			if (groupid != null && !"".equals(groupid)) {
				WebSocketPool.removeUserGroup(conn);
				WebSocketPool.removeUseringroup(groupid, conn);
			}
		}
	}
	
	
	private void test() {
		// TODO Auto-generated method stub
		System.out.println("test method");
		
		/*********** usernameconnections ************/
//		System.out.println("************* getOnlineUserName ************");
//		Collection<String> onlineUserNames = WebSocketPool.getOnlineUserName();
//		for (String name: onlineUserNames ){
//			System.out.println("Name: " + name);
//		}
//		System.out.println("************ getUserNameByKey *************");
//		Set<org.java_websocket.WebSocket> conns = WebSocketPool.userallconnections.keySet();
//		System.out.println("conns.size(): " + conns.size());
//		for (org.java_websocket.WebSocket conn : conns){
//			System.out.println("Name: " + WebSocketPool.getUserNameByKey(conn));			
//		}
//		System.out.println("************ removeKey *************");
//		Set<org.java_websocket.WebSocket> conns2 = WebSocketPool.userallconnections.keySet();
//		System.out.println("conns.size(): " + conns2.size());
//		for (org.java_websocket.WebSocket conn : conns2){
//			if (WebSocketPool.getUserNameByKey(conn).equals("agent07")){
//				WebSocketPool.removeUserName(conn);
//				System.out.println("agent07 deleted");
//			}
//			if (WebSocketPool.getUserNameByKey(conn) == null){
//				System.out.println("conn: " + conn + ": username not found.");
//			}
//			System.out.println("Name: " + WebSocketPool.getUserNameByKey(conn));			
//		}		
		/************* userconnections *************/
		System.out.println("************ getUserNByKey *************");
		Set<org.java_websocket.WebSocket> conns3 = WebSocketPool.userallconnections.keySet();
		System.out.println("conns.size(): " + conns3.size());
		for (org.java_websocket.WebSocket conn : conns3){
			System.out.println("Id: " + WebSocketPool.getUserByKey(conn));			
		}
		System.out.println("************ getUserCount *************");
		System.out.println("WebSocketPool.getUserCount(): " + WebSocketPool.getUserCount());
		
		
		System.out.println("************ getWebSocketByUser *************");
		Set<org.java_websocket.WebSocket> conns4 = WebSocketPool.userallconnections.keySet();
		System.out.println("conns.size(): " + conns4.size());
		for (org.java_websocket.WebSocket conn : conns4){
//			System.out.println("Id: " + WebSocketPool.getUserByKey(conn));
			String userid = WebSocketPool.getUserByKey(conn);
			org.java_websocket.WebSocket tmpconn = WebSocketPool.getWebSocketByUser(userid);
			System.out.println("tmpconn: " + tmpconn);
			
		}		
		System.out.println("************ getOnlineUser *************");
		Collection<String> userids = WebSocketPool.getOnlineUser();
		for (String userid: userids){
			System.out.println("userid: " + userid);
		}
		
		System.out.println("************ sendMessage *************");
		WebSocketPool.sendMessage("Hello Everybody!");		
		
	}
	
	
}


