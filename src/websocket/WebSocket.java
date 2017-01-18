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
import java.util.Date;
import java.util.Set;

import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

import websocket.function.AgentFunction;
import websocket.function.CommonFunction;
import websocket.pools.WebSocketGroupPool;
import websocket.pools.WebSocketTypePool;
import websocket.pools.WebSocketUserPool;

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
		System.out.println("Socket Exception:" + message.toString());
		message.printStackTrace();
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
//			this.getMessage(message.toString(), conn);
			CommonFunction.getMessage(message.toString(), conn);
			break;
		case "login":
//			this.userjoin(message.toString(), conn);
			CommonFunction.userjoin(message.toString(), conn);
			break;
		case "online":
//			this.online(message.toString(), conn);
			CommonFunction.online(message.toString(), conn);
			break;
		case "Exit":
//			this.userExit(message.toString(), conn);
			CommonFunction.userExit(message.toString(), conn);
			break;
		case "addGroup":
//			this.userjointogroup(message.toString(), conn);
			CommonFunction.userjointogroup(message.toString(), conn);
			break;
		case "leaveGroup":
//			this.userExitfromgroup(message.toString(), conn);
			CommonFunction.userExitfromgroup(message.toString(), conn);
			break;
		case "messagetogroup":
//			this.getMessageingroup(message.toString(), conn);
			CommonFunction.getMessageingroup(message.toString(), conn);
			break;
		case "grouponline":
//			this.onlineingroup(message.toString(), conn);
			CommonFunction.onlineingroup(message.toString(), conn);
			break;
		case "typeonline":
//			this.onlineinTYPE(message.toString(), conn);
			CommonFunction.onlineinTYPE(message.toString(), conn);
			break;
		case "typein":
//			this.userjointoTYPE(message.toString(), conn);
			CommonFunction.userjointoTYPE(message.toString(), conn);
			break;
		case "typeout":
//			this.userExitfromTYPE(message.toString(), conn);
			CommonFunction.userExitfromTYPE(message.toString(), conn);
			break;
		case "Agentclosegroup":
//			this.Agentclosegroup(message.toString(), conn);
			AgentFunction.Agentclosegroup(message.toString(), conn);
			break;
		case "Clientclosegroup":
			this.Clientclosegroup(message.toString(), conn);
			break;
		case "AcceptEvent":
//			this.AcceptEvent(message.toString(), conn);
			AgentFunction.AcceptEvent(message.toString(), conn);
			break;
		case "RejectEvent":
//			this.RejectEvent(message.toString(), conn);
			AgentFunction.RejectEvent(message.toString(), conn);
			break;
		case "ReleaseEvent":
//			this.ReleaseEvent(message.toString(), conn);
			AgentFunction.ReleaseEvent(message.toString(), conn);
			break;
		case "findAgentEvent":
			this.findAgentEvent(message.toString(), conn);
			break;
		case "updateStatus":
//			this.updateStatus(message.toString(), conn);
			CommonFunction.updateStatus(message.toString(), conn);
			break;
		case "getUserStatus":
//			this.getUserStatus(message.toString(), conn);
			AgentFunction.getUserStatus(message.toString(), conn);
			break;
		case "findAgent":
			this.findAgent(message.toString(), conn);
			break;
		case "creategroupId":
//			this.creategroupId(message.toString(), conn);
			AgentFunction.creategroupId(message.toString(), conn);
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
//			this.heartbeattoserver(message.toString(), conn);
			HeartBeat.heartbeattoserver(message.toString(), conn);
			break;
		case "test":
			this.test();
			break;
		}
	}
	
	







	/** * user leave websocket (Demo) */
	public void userLeave(org.java_websocket.WebSocket conn) {
		String user = WebSocketUserPool.getUserByKey(conn);
//		boolean b = WebSocketPool.removeUserID(conn);
//		WebSocketPool.removeUserName(conn);
		boolean b = WebSocketUserPool.removeUser(conn);
		if (b) {
			WebSocketUserPool.sendMessage(user.toString());
			String joinMsg = "[Server]" + user + "Offline";
			WebSocketUserPool.sendMessage(joinMsg);
		}
	}









	/** * Get Message from Agent or Client list */
	public void getMessageinTYPE(String message,
			org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String ACtype = obj.getString("ACtype");
		String username = obj.getString("UserName");
		String text = obj.getString("text");
		WebSocketTypePool.sendMessageinTYPE(ACtype, username + ": " + text);
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
		WebSocketGroupPool.sendMessageingroup(group,sendjson.toString());
	}

	
	

	
	/** * findAgentEvent */
	public void findAgentEvent(String message, org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		org.java_websocket.WebSocket sendto = WebSocketUserPool
				.getWebSocketByUser(obj.getString("sendto"));
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "findAgentEvent");
		sendjson.put("from", obj.getString("id"));
		sendjson.put("fromName",  obj.getString("UserName"));
		sendjson.put("channel", obj.getString("channel"));
		WebSocketUserPool.sendMessageToUser(sendto, sendjson.toString());
	}
	


	/** * find online Longest Agent */
	public void findAgent(String message, org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String Agent;
		try {
			Agent = WebSocketTypePool.getOnlineLongestUserinTYPE("Agent");
		} catch (Exception e) {
			Agent = null;
		}
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "findAgent");
		sendjson.put("from", obj.getString("id"));
		sendjson.put("Agent",  Agent);
		sendjson.put("channel", obj.getString("channel"));
		WebSocketUserPool.sendMessageToUser(conn, sendjson.toString());
	}

	/** * Get user data */
	public void senduserdata(String message, org.java_websocket.WebSocket conn) {
		System.out.println(message);
		JSONObject obj = new JSONObject(message);
		String lang = obj.getString("lang");
		String searchtype = obj.getString("searchtype");
		JSONObject attributes = obj.getJSONObject("attributes");
		String attributenames = obj.getJSONObject("attributes").getString(
				"attributenames");
		StringBuilder responseSB = null;
		try {
			// Encode the query
			String postData = 
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
		org.java_websocket.WebSocket sendto = WebSocketUserPool
				.getWebSocketByUser(obj.getString("sendto"));
		JSONObject responseSBjson = new JSONObject(responseSB.toString());
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "searchuserdata");
		sendjson.put("from", obj.getJSONObject("attributes").getString("id"));
		sendjson.put("userdata",  responseSBjson);
		sendjson.put("channel", obj.getString("channel"));
		WebSocketUserPool.sendMessageToUser(conn, sendjson.toString());
		WebSocketUserPool.sendMessageToUser(sendto, sendjson.toString());
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
		WebSocketUserPool.addUserInteraction(message, conn);
	}
	
	
	private void test() {
	// TODO Auto-generated method stub
	System.out.println("test method");
	
	/*********** usernameconnections ************/
//	System.out.println("************* getOnlineUserName ************");
//	Collection<String> onlineUserNames = WebSocketPool.getOnlineUserName();
//	for (String name: onlineUserNames ){
//		System.out.println("Name: " + name);
//	}
//	System.out.println("************ getUserNameByKey *************");
//	Set<org.java_websocket.WebSocket> conns = WebSocketPool.userallconnections.keySet();
//	System.out.println("conns.size(): " + conns.size());
//	for (org.java_websocket.WebSocket conn : conns){
//		System.out.println("Name: " + WebSocketPool.getUserNameByKey(conn));			
//	}	
	/************* userconnections *************/
//	System.out.println("\n************ getUserNByKey *************");
//	Set<org.java_websocket.WebSocket> conns3 = WebSocketPool.userallconnections.keySet();
//	System.out.println("conns.size(): " + conns3.size());
//	for (org.java_websocket.WebSocket conn : conns3){
//		System.out.println("Id: " + WebSocketPool.getUserByKey(conn));			
//	}
//	System.out.println("\n************ getUserCount *************");
//	System.out.println("WebSocketPool.getUserCount(): " + WebSocketPool.getUserCount());
//	
//	
//	System.out.println("\n************ getWebSocketByUser *************");
//	Set<org.java_websocket.WebSocket> conns4 = WebSocketPool.userallconnections.keySet();
//	System.out.println("conns.size(): " + conns4.size());
//	for (org.java_websocket.WebSocket conn : conns4){
////		System.out.println("Id: " + WebSocketPool.getUserByKey(conn));
//		String userid = WebSocketPool.getUserByKey(conn);
//		org.java_websocket.WebSocket tmpconn = WebSocketPool.getWebSocketByUser(userid);
//		System.out.println("tmpconn: " + tmpconn);
//		
//	}		
//	System.out.println("\n************ getOnlineUser *************");
//	Collection<String> userids = WebSocketPool.getOnlineUser();
//	System.out.println("userids.size(): " + userids.size());
//	for (String userid: userids){
//		System.out.println("userid: " + userid);
//	}	
//	
//	System.out.println("\n************ sendMessage *************");
//	WebSocketPool.sendMessage("Hello Everybody!");		
//	
//	System.out.println("\n************ removeUser *************");
//	Set<org.java_websocket.WebSocket> conns5 = WebSocketPool.userallconnections.keySet();
//	System.out.println("conns.size(): " + conns5.size());
//	Iterator<org.java_websocket.WebSocket> itr = conns5.iterator();
//	while(itr.hasNext()){
//		org.java_websocket.WebSocket conn = itr.next();
//		WebSocketPool.removeUser(conn); // 注意此removeUser方法非用iterator刪除,所以不能一次刪兩個或以上的內容物件
////		itr.remove();
//		if (WebSocketPool.userallconnections.get(conn) == null){
//			System.out.println(conn + " has been deleted");
//		}
//	}		
	
	/*********** user整體測試 **************/
	System.out.println("\n************ getXXX*************");
	Set<org.java_websocket.WebSocket> conns6 = WebSocketUserPool.userallconnections.keySet();
	System.out.println("conns.size(): " + conns6.size());
	for (org.java_websocket.WebSocket conn : conns6){
		System.out.println("Id: " + WebSocketUserPool.getUserByKey(conn));			
		System.out.println("name: " + WebSocketUserPool.getUserNameByKey(conn));			
		System.out.println("group: " + WebSocketGroupPool.getUserGroupByKey(conn));			
		System.out.println("Interaction: " + WebSocketTypePool.getUserInteractionByKey(conn));			
		System.out.println("heartbeat: " + WebSocketUserPool.getUserheartbeatByKey(conn));			
	}
	
}
	
}


