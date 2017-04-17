package websocket.pools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;

import org.java_websocket.WebSocket;
import org.java_websocket.exceptions.WebsocketNotConnectedException;

import com.google.gson.Gson;

import util.StatusEnum;
import util.Util;
import websocket.bean.UpdateStatusBean;
import websocket.bean.UserInfo;
import websocket.function.CommonFunction;
import websocket.thread.findAgent.FindAgentCallable;

//此類別給AgentFunction.java共同使用
//此類別給ClientFunction.java共同使用
//此類別給CommonFunction.java共同使用

//此類別給HeartBeat.java使用
//此類別給GetKPIServlet.java使用
public class WebSocketUserPool {
	/**
	 * online User ID/NAME Map
	 * userallconnections在資料層級上 = WebSocketTypePool.TYPEconnections.get("Client") + 
	 * 								WebSocketTypePool.TYPEconnections.get("Agent");
	 */
	private static final Map<WebSocket, UserInfo> userallconnections = new HashMap<WebSocket,UserInfo>();
	// 由userallconnections整理出來的map(尚未開始使用)
	private static final Map<String, Map<WebSocket, UserInfo>> TYPEconnections = new HashMap<>();
	
	private static final BlockingQueue<String> readyAgentQueue = new LinkedBlockingQueue<>(); // 用在ClientFunction::getOnlineLongestUserinTYPE()
	private static final BlockingQueue<FindAgentCallable> ClientFindAgentQueue = new LinkedBlockingQueue<>(); // (進行中)用在ClientFunction::getOnlineLongestUserinTYPE()
	
	/** * Get User ID By Key * @param session */ /* Done */
	public static String getUserByKey(WebSocket conn) {
		return userallconnections.get(conn).getUserid();
	}
	
	/** * Get User Name By Key * @param session */ /* Done */
	public static String getUserNameByKey(WebSocket conn) {
		if(userallconnections.get(conn) == null){
			return "none";
		}
		return userallconnections.get(conn).getUsername();
	}
	
	/** * Get User Room By Key * @param session */ /* Done */
	public static List<String> getUserRoomByKey(WebSocket conn) {
//		Util.getConsoleLogger().debug("getUserRoomByKey(WebSocket conn) called");
		if(userallconnections.get(conn) == null){
			return new ArrayList<String>();
		}
		return userallconnections.get(conn).getUserRoom();
	}

	/** * Get User Interaction By Key * @param session */ /* Done */
	public static String getUserInteractionByKey(WebSocket conn) {
		UserInfo tmpUserInfo = userallconnections.get(conn);
		if (tmpUserInfo == null) return null;
		return tmpUserInfo.getUserinteraction();
//		return userallconnections.get(conn).getUserinteraction();
	}
	
	/** * Get User heartbeat By Key * @param session */ /* Done */
	public static String getUserheartbeatByKey(WebSocket conn) {
		return userallconnections.get(conn).getUserheartbeat();
	}
	
	

	/** * Get Online User Count * @param */ /* Done */
	public static int getUserCount() {
		return userallconnections.size();
	}
	
	/** * Get Online User Room Count * @param */ /* Done */
	public static int getUserRoomCount(WebSocket conn) {
		if(userallconnections.get(conn) == null || userallconnections.get(conn).getUserRoom() == null){
			return 0;
		}
		return userallconnections.get(conn).getUserRoom().size();
	}

	/** * Get WebSocket By User ID * @param user */ /* Done */
	public static WebSocket getWebSocketByUserID(String user) {
		Set<WebSocket> conns = userallconnections.keySet();
		synchronized (conns) {
			for (WebSocket conn : conns) {
				String cuser = userallconnections.get(conn).getUserid();
//				Util.getConsoleLogger().debug("***cuser: " + cuser);
//				Util.getConsoleLogger().debug("***user: " + user);
				if (cuser.equals(user)) {
//					Util.getConsoleLogger().debug("(getWebSocketByUser)found user id: " + cuser + "的connection");
					return conn;
				}
			}
		}
		return null;
	}

	/** * Add User to WebSocket Pool* @param inbound */ /* Done */
	public static void addUser(String username,String userid, WebSocket conn, String ACType, int aMaxCount) {
		UserInfo userinfo = new UserInfo();
		userinfo.setUserid(userid);
		userinfo.setUsername(username);
		userinfo.setACType(ACType);
		userinfo.setStartdate(new java.util.Date());
		userinfo.setMaxCount(aMaxCount);
		userallconnections.put(conn, userinfo); // 每一個client的connection配一個
		
		
		
		
	}
	
	/** * Add User to WebSocket Pool* @param inbound */ /* Done */
	public static void addUserRoom(String userRoom, WebSocket conn) {
		userallconnections.get(conn).getUserRoom().add(userRoom);
	}
	
	/** * Add User to WebSocket Pool* @param inbound */ /* Done */
	public static void addUserInteraction(String userinteraction, WebSocket conn) {
		Util.getConsoleLogger().debug("addUserInteraction() called");
		userallconnections.get(conn).setUserinteraction(userinteraction);
	}
	
	/** * Add User to WebSocket Pool* @param inbound */ /* Done */
	public static void addUserheartbeat(String userheartbeat, WebSocket conn) {
		userallconnections.get(conn).setUserheartbeat(userheartbeat);
	}
	
	/** * Get Online User ID * @return */ /* Done */
	public static Collection<String> getOnlineUser() {
		List<String> setUsers = new ArrayList<String>();
		
		Set<org.java_websocket.WebSocket> conns = WebSocketUserPool.userallconnections.keySet();
		for (org.java_websocket.WebSocket conn : conns){
			String userid = WebSocketUserPool.getUserByKey(conn);
			setUsers.add(userid);
		}
		
		return setUsers;
	}

	/** * Get Online User Name * @return */ /* Done */
	public static Collection<String> getOnlineUserName() {
		List<String> setUsers = new ArrayList<String>();
		
		Collection<UserInfo> userinfos = userallconnections.values();
		for (UserInfo userinfo : userinfos){
			setUsers.add(userinfo.getUsername());
		}
		
		return setUsers;
	}

	/** * Remove User WebSocket from WebSocket Pool * @param inbound */ /* Done */
	public static boolean removeUser(WebSocket conn){
		if (userallconnections.containsKey(conn)) {
			userallconnections.remove(conn); // 注意此處非用iterator刪除,所以不能一次刪兩個或以上的內容物件
			return true;
		} else {
			return false;
		}		
	}
	
	/** * Remove User Room from WebSocket Pool * @param inbound */ /* Done */
	public static boolean removeUserRoom(WebSocket conn, String aRoomID) {
		
		if (userallconnections.containsKey(conn)) {
			userallconnections.get(conn).getUserRoom().remove(aRoomID); // 改成clear, 不然會清過頭,不能重複用
			return true;
		} else {
			return false;
		}
		
	}
	
	/** * Remove User heartbeat from WebSocket Pool * @param inbound */ /* Done */
	public static boolean removeUserheartbeat(WebSocket conn) {
		if (userallconnections.containsKey(conn)) {
			userallconnections.get(conn).setUserheartbeat(null);
			return true;
		} else {
			return false;
		}
		
	}

	/** * Send Message to a User * @param user * @param message */ /* 給Heartbeat使用 */
	public static void sendMessageToUser(WebSocket conn, String message) throws WebsocketNotConnectedException{
//		Util.getConsoleLogger().debug("sendMessageToUser() called - to - conn: " + conn);
		if (null != conn) {
//			Util.getConsoleLogger().debug("conn.send(message) called");
			conn.send(message);
		}
	}

	
	/** * Send Message to a User * @param user * @param message */ /* Done */
	public static void sendMessageToUserWithTryCatch(WebSocket conn, String message){
//		Util.getConsoleLogger().debug("sendMessageToUser() called - to - conn: " + conn);
		
		try{
			sendMessageToUser(conn, message);
		}catch(WebsocketNotConnectedException e){
			Util.getConsoleLogger().debug(e.getMessage());
			Util.getFileLogger().info(e.getMessage());
		}
		
		
	}
	
	
	/** * Send Message to all of User * @param message */ /* Done */
	public static void sendMessage(String message) {	
		Set<WebSocket> conns = userallconnections.keySet();
		synchronized (conns) {
			for (WebSocket conn : conns) {
				String user = userallconnections.get(conn).getUserid();
				if (user != null) {
					conn.send(message);
				}
			}
		}
	}
	
	/** * Get heartbeatTimer By Key * @param session */ /* Done */
	public static Timer getUserHeartbeatTimerByKey(WebSocket conn) {
		return userallconnections.get(conn).getHeartbeatTimer();
	}
	
	/** * Add heartbeatTimer WebSocket Pool* @param inbound */ /* Done */
	public static void addUserHeartbeatTimer(Timer heartbeatTimer, WebSocket conn) {
		userallconnections.get(conn).setHeartbeatTimer(heartbeatTimer);
	}
	
	/** * get ACType By UserID* @param inbound */ /* Done */
	public static String getACTypeByKey(WebSocket conn) {
		UserInfo userInfo = userallconnections.get(conn);
		if (userInfo == null) return null;
		return userInfo.getACType();
	}
	
	/** * get LoginDate By UserID* @param inbound */ /* Done */
	public static java.util.Date getStartdateByKey(WebSocket conn) {
		UserInfo userInfo = userallconnections.get(conn);
		if (userInfo == null) return null;
		return userInfo.getStartdate();
	}

	public static Map<WebSocket, UserInfo> getUserallconnections() {
		return userallconnections;
	}
	
	public static String getUserID(WebSocket conn){
		return userallconnections.get(conn).getUserid();
	}
	
	public static UserInfo getUserInfoByKey(WebSocket conn){
		return userallconnections.get(conn);
	}

	public static BlockingQueue<String> getReadyAgentQueue() {
		return readyAgentQueue;
	}

	public static BlockingQueue<FindAgentCallable> getClientfindagentqueue() {
		return ClientFindAgentQueue;
	}
	
	/** * Get Online Longest User(Agent) * @return */
	public static String getOnlineLongestUserinTYPE(WebSocket aConn) {
//		Util.getConsoleLogger().debug("getOnlineLongestUserinTYPE() called");
		String poppedAgentID = null;
		UserInfo settingUserInfo = null;
		try {
//				String poppedAgentID = WebSocketUserPool.getReadyAgentQueue().poll();
			poppedAgentID = WebSocketUserPool.getReadyAgentQueue().take();// this will block the current Thread if the queue is empty
		} catch (InterruptedException e) {
			// if client logout or disconnect, this task in the current thread will be terminated
			Util.getFileLogger().error("[Exception] Client " + WebSocketUserPool.getUserNameByKey(aConn) + " cancelled findAgent task");
			Util.getConsoleLogger().error("[Exception] Client " + WebSocketUserPool.getUserNameByKey(aConn) + " cancelled findAgent task");
//			e.printStackTrace();
		} 
		Util.getConsoleLogger().debug("poppedAgentID: " + poppedAgentID);
		
		if (poppedAgentID == null) return poppedAgentID;
		
		// 拿取相對應UserInfo資訊
		WebSocket poppedAgentConn = WebSocketUserPool.getWebSocketByUserID(poppedAgentID);
		settingUserInfo = WebSocketUserPool.getUserInfoByKey(poppedAgentConn);
		Util.getConsoleLogger().debug("agentUserInfo.getUsername(): " + settingUserInfo.getUsername() + " popped out");
		Util.getConsoleLogger().debug("WebSocketUserPool.getReadyAgentQueue().size(): " + WebSocketUserPool.getReadyAgentQueue().size());			
		
		// 開始更新狀態: 
//		settingUserInfo.setStatusEnum(StatusEnum.NOTREADY); // 此方法已經不再有影響 // 直接改了,避免一個以上Client找到同一個Agent
		Gson gson = new Gson();
		WebSocket agentConn = WebSocketUserPool.getWebSocketByUserID(settingUserInfo.getUserid());
		// NOTREADY狀態開始
		Util.getStatusFileLogger().info("###### [findAgent]");
		UpdateStatusBean usb = new UpdateStatusBean();
		usb.setStatus(StatusEnum.NOTREADY.getDbid());
		usb.setStartORend("start");
		CommonFunction.updateStatus(gson.toJson(usb), agentConn);				

//		ExecutorService service = Executors.newCachedThreadPool();
//		Future<String> taskResult = service.submit(task);
//		while(!taskResult.isDone()){
//			// let current thread do something else
////			Util.getConsoleLogger().debug("taskResult is not done yet");
//		}
//		Util.getConsoleLogger().debug("taskResult.getClass(): " + taskResult.getClass() + " is Done");
//		
//		try {
//			poppedAgentID = taskResult.get(); // blocks if the result haven't come out
//		} catch (InterruptedException | ExecutionException e) {
//			e.printStackTrace();
//		}

		return poppedAgentID;
	}
	
	
	
}