package websocket;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
//import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONObject;

import websocket.bean.RoomInfo;
import websocket.function.AgentFunction;
import websocket.function.ClientFunction;
import websocket.function.CommonFunction;
import websocket.pools.WebSocketRoomPool;
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
		// 此方法沒有用到,先放著,並不會影響到主流程
		//userLeave(conn);
//		System.out.println("Someone unlink in Socket conn:" + conn);
		System.out.println("conn: " + conn + " is disconnected. !!!!!");
		// 將Heartbeat功能移轉到這裡:
		inputInteractionLog(conn,reason);
		clearUserData(conn); // 包含removeUser, removerUserinTYPE, removeUserinroom
	}


	/** * trigger Exception Event */
	@Override
	public void onError(org.java_websocket.WebSocket conn, Exception message) {
		System.out.println("On Error: Socket Exception:" + message.toString());
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
	 * message login online Exit addRoom leaveRoom messagetoRoom roomonline
	 * typeonline typein typeout AcceptEvent
	 * Event updateStatus getUserStatus findAgent createroomId senduserdata
	 */
	@Override
	public void onMessage(org.java_websocket.WebSocket conn, String message) {
		message = message.toString();
		// System.out.println(message);
		JSONObject obj = new JSONObject(message);
		switch (obj.getString("type").trim()) {
		case "message":
			CommonFunction.getMessage(message.toString(), conn);
			break;
		case "login":
			CommonFunction.userjoin(message.toString(), conn);
			break;
		case "online":
			CommonFunction.online(message.toString(), conn);
			break;
		case "Exit":
			CommonFunction.userExit(message.toString(), conn);
			break;
		case "addRoom":
			CommonFunction.userjointoRoom(message.toString(), conn);
			break;
		case "leaveRoom":
			CommonFunction.userExitfromRoom(message.toString(), conn);
			break;
		case "messagetoRoom":
			CommonFunction.getMessageinRoom(message.toString(), conn);
			break; 
		case "roomonline":
			CommonFunction.onlineinRoom(message.toString(), conn);
			break;
		case "typeonline":
			CommonFunction.onlineinTYPE(message.toString(), conn);
			break;
		case "typein":
			CommonFunction.userjointoTYPE(message.toString(), conn);
			break;
		case "typeout":
			CommonFunction.userExitfromTYPE(message.toString(), conn);
			break;
		case "AcceptEvent":
			AgentFunction.AcceptEvent(message.toString(), conn);
			break;
		case "RejectEvent":
			AgentFunction.RejectEvent(message.toString(), conn);
			break;
		case "findAgentEvent":
			ClientFunction.findAgentEvent(message.toString(), conn);
			break;
		case "updateStatus":
			CommonFunction.updateStatus(message.toString(), conn);
			break;
		case "getUserStatus":
			AgentFunction.getUserStatus(message.toString(), conn);
			break;
		case "findAgent":
			ClientFunction.findAgent(message.toString(), conn);
			break;
		case "createroomId":
			AgentFunction.createRoomId(message.toString(), conn);
			break;
		case "senduserdata":
			ClientFunction.senduserdata(message.toString(), conn);			
			break;
		case "entrylog":
			ClientFunction.entrylog(message.toString(), conn);
			break;
		case "interactionlog":
			ClientFunction.interactionlog(message.toString(), conn);
			break;
		case "setinteraction":
			ClientFunction.setinteraction(message.toString(), conn);
			break;
		case "inviteAgent3way":
			inviteAgent3way(message.toString(), conn);
			break;
		case "responseThirdParty":
			System.out.println("responseThirdParty got here!");
			responseThirdParty(message.toString(), conn);			
			break;			
		case "test":
			this.test();
			break;
		}
	}
	


	/** * user leave websocket (Demo) */
	// 此方法沒有用到,先放著,並不會影響到主流程
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
	
	private void clearUserData(org.java_websocket.WebSocket conn) {
		System.out.println("clearUserData() called");
		// 清GROUP:
		// 取得一個user所屬的所有roomid
		List<String> roomids = WebSocketUserPool.getUserRoomByKey(conn);
		for (String roomid: roomids){
			//使用每個roomid,並找出相對應的room,再將其中的conn remove掉
			WebSocketRoomPool.removeUserinroom(roomid, conn);
		}
		// 清TYPE:
		String ACType = WebSocketUserPool.getACTypeByKey(conn);
		WebSocketTypePool.removeUserinTYPE(ACType, conn);
		// 清USER:
		WebSocketUserPool.removeUser(conn);
	}
	
	private void inputInteractionLog(org.java_websocket.WebSocket conn, String reason) {
		System.out.println("inputInteractionLog() called");
//		System.out.println("conn: " + conn);
		
		String message = WebSocketUserPool.getUserInteractionByKey(conn); // 一定取得到: 1. 在user login時就會呼叫setUserInteraction, 2. 在user logut or 重整時也會呼叫setUserInteraction
		String username = WebSocketUserPool.getUserNameByKey(conn);
//		System.out.println("message: " + message);
//		System.out.println("username: " + username);
		
		// 目前只有client端會再登入時、登出時、重整時寫入Log
		if (message != null){
			JSONObject obj = new JSONObject(message);
			String closefrom = obj.getString("closefrom");
			if (closefrom.equals("default")) {
				obj.put("status", 3);
				obj.put("stoppedreason", "server:HeartBeatLose"); // 看之後是否考慮更改為變數reason
				obj.put("closefrom", "server:HeartBeatLose"); 
				message = obj.toString();
			}
			ClientFunction.interactionlog(message, conn);			
		}

	}
	
	private void inviteAgent3way(String message, org.java_websocket.WebSocket conn){
		// 讀出送進來的JSON物件
		System.out.println("inviteAgent3way() called");
		JSONObject obj = new JSONObject(message);
		String ACtype = obj.getString("ACtype");
		String roomID = obj.getString("roomID");
		String fromAgentID = obj.getString("fromAgentID");
		String invitedAgentID = obj.getString("invitedAgentID");
		String fromAgentName = obj.getString("fromAgentName");
		String inviteType = obj.getString("inviteType");
		
		
		
		
		//籌備要寄出的JSON物件
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "inviteAgent3way");
		sendjson.put("roomID", roomID);
		sendjson.put("fromAgentID", fromAgentID);
		sendjson.put("invitedAgentID", invitedAgentID);
		sendjson.put("fromAgentName", fromAgentName);
		sendjson.put("inviteType", inviteType);
		
		System.out.println("sendjson: " + sendjson);
		
		// 寄給invitedAgent:
		org.java_websocket.WebSocket invitedAgent_conn = WebSocketUserPool.getWebSocketByUser(invitedAgentID);
		System.out.println("invitedAgent_conn: " + invitedAgent_conn);
		WebSocketUserPool.sendMessageToUser(invitedAgent_conn, sendjson.toString());
		
//		type : "inviteAgent3way",
//		ACtype : "Agent",
//		roomID : RoomID, //先預設目前每個Agent最多也就只有一個RoomID,之後會再調整
//		fromAgentID : UserID,
//		invitedAgentID : myInvitedAgentID,
//		fromAgentName : UserName
	}

	private void responseThirdParty(String message, org.java_websocket.WebSocket conn) {
		// TODO Auto-generated method stub
		JSONObject obj = new JSONObject(message);
		String ACtype = obj.getString("ACtype");
		String roomID = obj.getString("roomID");
		String fromAgentID = obj.getString("fromAgentID");
		String invitedAgentID = obj.getString("invitedAgentID");
		String invitedAgentName = WebSocketUserPool.getUserNameByKey(conn);
		String response = obj.getString("response");
		String inviteType = obj.getString("inviteType");
		
		if ("accept".equals(response)){
			System.out.println("responseThirdParty() - accept");
			WebSocketRoomPool.addUserinroom(roomID, invitedAgentName, invitedAgentID, conn);
			// 通知各房間成員成員數改變了
			JSONObject sendJson = new JSONObject();
			sendJson.put("Event", "responseThirdParty");
			sendJson.put("roomID", roomID);
			sendJson.put("fromAgentID", fromAgentID);
			sendJson.put("invitedAgentID", invitedAgentID);
			sendJson.put("roomMembers", WebSocketRoomPool.getOnlineUserinroom(roomID).toString());
			
			// 若是屬於轉接的要求,則將原Agent(邀請者)踢出
			if ("transfer".equals(inviteType)){
				System.out.println("responseThirdParty() - transfer");
				WebSocketRoomPool.removeUserinroom(roomID, WebSocketUserPool.getWebSocketByUser(fromAgentID));
			}
			
			WebSocketRoomPool.sendMessageinroom(roomID, sendJson.toString());
			
		}else if("reject".equals(response)){
			System.out.println("responseThirdParty() - reject");			
		}
	
	}
	
	
	private void test() {
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
//	System.out.println("\n************ getXXX*************");
//	Set<org.java_websocket.WebSocket> conns6 = WebSocketUserPool.userallconnections.keySet();
//	System.out.println("conns.size(): " + conns6.size());
//	for (org.java_websocket.WebSocket conn : conns6){
//		System.out.println("Id: " + WebSocketUserPool.getUserByKey(conn));			
//		System.out.println("name: " + WebSocketUserPool.getUserNameByKey(conn));			
//		System.out.println("room: " + WebSocketUserPool.getUserRoomByKey(conn));			
//		System.out.println("Interaction: " + WebSocketUserPool.getUserInteractionByKey(conn));			
//		System.out.println("heartbeat: " + WebSocketUserPool.getUserheartbeatByKey(conn));			
//	}
	/*********** log4j測試 **************/
	Logger logger = Logger.getLogger(WebSocket.class);

	// 對應的 Log4j.properties 設定要在等級 Info 之上才會顯示，所以logger.debug 不會出現
	logger.debug("Hello Log4j, this is debug message");

	// 以下的訊息會出現在 console 和 log file 中
	logger.info("Hi Log4j, this will appear in console and log file");
	logger.error("This is error message!!!");
	
	
}
	
}


