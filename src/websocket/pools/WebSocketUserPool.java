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

import org.java_websocket.WebSocket;
import org.java_websocket.exceptions.WebsocketNotConnectedException;

import websocket.bean.UserInfo;

//此類別給AgentFunction.java共同使用
//此類別給ClientFunction.java共同使用
//此類別給CommonFunction.java共同使用

//此類別給HeartBeat.java使用
//此類別給GetKPIServlet.java使用
public class WebSocketUserPool {
	/**
	 * online User ID/NAME Map
	 * 暫時先設為public,測試用
	 */
	private static final Map<WebSocket, UserInfo> userallconnections = new HashMap<WebSocket,UserInfo>();	
	
	/** * Get User ID By Key * @param session */ /* Done */
	public static String getUserByKey(WebSocket conn) {
		return userallconnections.get(conn).getUserid();
	}
	
	/** * Get User Name By Key * @param session */ /* Done */
	public static String getUserNameByKey(WebSocket conn) {
		return userallconnections.get(conn).getUsername();
	}
	
	/** * Get User Room By Key * @param session */ /* Done */
	public static List<String> getUserRoomByKey(WebSocket conn) {
//		System.out.println("getUserRoomByKey(WebSocket conn) called");
		return userallconnections.get(conn).getUserRoom();
	}

	/** * Get User Interaction By Key * @param session */ /* Done */
	public static String getUserInteractionByKey(WebSocket conn) {
		UserInfo tmpUserInfo = userallconnections.get(conn);
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
		return userallconnections.get(conn).getUserRoom().size();
	}

	/** * Get WebSocket By User ID * @param user */ /* Done */
	public static WebSocket getWebSocketByUser(String user) {
		Set<WebSocket> conns = userallconnections.keySet();
		synchronized (conns) {
			for (WebSocket conn : conns) {
				String cuser = userallconnections.get(conn).getUserid();
//				System.out.println("***cuser: " + cuser);
//				System.out.println("***user: " + user);
				if (cuser.equals(user)) {
//					System.out.println("(getWebSocketByUser)found user id: " + cuser + "的connection");
					return conn;
				}
			}
		}
		return null;
	}

	/** * Add User to WebSocket Pool* @param inbound */ /* Done */
	public static void addUser(String username,String userid, WebSocket conn, String ACType) {
		UserInfo userinfo = new UserInfo();
		userinfo.setUserid(userid);
		userinfo.setUsername(username);
		userinfo.setACType(ACType);
		userinfo.setStartdate(new java.util.Date());
		userallconnections.put(conn, userinfo); // 每一個client的connection配一個vo
	}
	
	/** * Add User to WebSocket Pool* @param inbound */ /* Done */
	public static void addUserRoom(String userRoom, WebSocket conn) {
		userallconnections.get(conn).getUserRoom().add(userRoom);
	}
	
	/** * Add User to WebSocket Pool* @param inbound */ /* Done */
	public static void addUserInteraction(String userinteraction, WebSocket conn) {
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
	public static boolean removeUserRoom(WebSocket conn) {
		
		if (userallconnections.containsKey(conn)) {
			userallconnections.get(conn).setUserRoom(null); // 看是要null還是"",目前覺得null比較同於原本的Map.remove(USERGROUP)
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

	/** * Send Message to a User * @param user * @param message */ /* Done */
	public static void sendMessageToUser(WebSocket conn, String message) throws WebsocketNotConnectedException{
//		System.out.println("sendMessageToUser() called - to - conn: " + conn);
		if (null != conn) {
//			System.out.println("conn.send(message) called");
			conn.send(message);
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
	
}