package websocket;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
//import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
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
		clearUserData(conn); // 包含removeUser, removerUserinTYPE, removeUseringroup
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
//			this.Clientclosegroup(message.toString(), conn);
			ClientFunction.Clientclosegroup(message.toString(), conn);
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
//			this.findAgentEvent(message.toString(), conn);
			ClientFunction.findAgentEvent(message.toString(), conn);
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
//			this.findAgent(message.toString(), conn);
			ClientFunction.findAgent(message.toString(), conn);
			break;
		case "creategroupId":
//			this.creategroupId(message.toString(), conn);
			AgentFunction.creategroupId(message.toString(), conn);
			break;
		case "senduserdata":
//			this.senduserdata(message.toString(), conn);
			ClientFunction.senduserdata(message.toString(), conn);			
			break;
		case "entrylog":
//			this.entrylog(message.toString(), conn);
			ClientFunction.entrylog(message.toString(), conn);
			break;
		case "interactionlog":
//			this.interactionlog(message.toString(), conn);
			ClientFunction.interactionlog(message.toString(), conn);
			break;
		case "setinteraction":
//			this.setinteraction(message.toString(), conn);
			ClientFunction.setinteraction(message.toString(), conn);
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
		// 取得一個user所屬的所有groupid
		List<String> groupids = WebSocketUserPool.getUserRoomByKey(conn);
		for (String groupid: groupids){
			//使用每個groupid,並找出相對應的group,再將其中的conn remove掉
			WebSocketRoomPool.removeUserinroom(groupid, conn); // 這邊是否須考慮如果此user退出group,只剩下agent在的狀況? 還是此狀況交由其他處來做處理?
			// 當去掉之後只剩下一個人在group中
//			Map<String, Map<org.java_websocket.WebSocket, GroupInfo>> groupuserconnections = WebSocketGroupPool.getGroupuserconnections();
//			Map<org.java_websocket.WebSocket, GroupInfo> currGroup = groupuserconnections.get(groupid);
//			System.out.println("groupId: " + groupid + " size " + currGroup.size());
//			if (currGroup.size() == 1){
//				System.out.println("groupId: " + groupid + " size " + currGroup.size());
//			}
		}
		// 清TYPE:
		WebSocketTypePool.removeUserinTYPE("Client", conn);
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
//	System.out.println("\n************ getXXX*************");
//	Set<org.java_websocket.WebSocket> conns6 = WebSocketUserPool.userallconnections.keySet();
//	System.out.println("conns.size(): " + conns6.size());
//	for (org.java_websocket.WebSocket conn : conns6){
//		System.out.println("Id: " + WebSocketUserPool.getUserByKey(conn));			
//		System.out.println("name: " + WebSocketUserPool.getUserNameByKey(conn));			
//		System.out.println("group: " + WebSocketUserPool.getUserGroupByKey(conn));			
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


