package websocket;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.java_websocket.WebSocket;

public class WebSocketPool {
	/**
	 * online User ID Map
	 */
	private static final Map<WebSocket, String> userconnections = new HashMap<WebSocket, String>();
	/**
	 * online User Name Map
	 */
//	private static final Map<WebSocket, String> usernameconnections = new HashMap<WebSocket, String>();
	
	/**
	 * online User ID/NAME Map
	 * 暫時先設為public,測試用
	 */
	public static final Map<WebSocket, Map<String,String>> userallconnections = new HashMap<WebSocket, Map<String,String>>();	
	
	
	
	/**
	 * online User Group Map
	 */
	private static final Map<WebSocket, String> usergroupconnections = new HashMap<WebSocket, String>();
	
	/**
	 * online User Interaction Map
	 */
	private static final Map<WebSocket, String> userinteractionconnections = new HashMap<WebSocket, String>();
	
	/**
	 * online User heartbeat Map
	 */
	private static final Map<WebSocket, String> userheartbeatconnections = new HashMap<WebSocket, String>();
	
	/** * Get User By Key * @param session */ /* Done */
	public static String getUserByKey(WebSocket conn) {
//		return userconnections.get(conn);
		return userallconnections.get(conn).get("userid");
	}
	
	/** * Get User By Key * @param session */ /* Done */
	public static String getUserNameByKey(WebSocket conn) {
//		return usernameconnections.get(conn);
		return userallconnections.get(conn).get("username");
	}
	
	/** * Get User By Key * @param session */
	public static String getUserGroupByKey(WebSocket conn) {
		return usergroupconnections.get(conn);
	}

	/** * Get User By Key * @param session */
	public static String getUserInteractionByKey(WebSocket conn) {
		return userinteractionconnections.get(conn);
	}
	
	/** * Get User By Key * @param session */
	public static String getUserheartbeatByKey(WebSocket conn) {
		return userheartbeatconnections.get(conn);
	}

	/** * Get Online User Count * @param */
	public static int getUserCount() {
//		return userconnections.size();
		return userallconnections.size();
	}

	/** * Get WebSocket By User ID * @param user */ /* Done */
	public static WebSocket getWebSocketByUser(String user) {
		/* 原方法
		Set<WebSocket> keySet = userconnections.keySet();
		synchronized (keySet) {
			for (WebSocket conn : keySet) {
				String cuser = userconnections.get(conn);
				if (cuser.equals(user)) {
					return conn;
				}
			}
		}
		*/
		
		Set<WebSocket> conns = userallconnections.keySet();
		synchronized (conns) {
			for (WebSocket conn : conns) {
//				String cuser = userconnections.get(conn);
				String cuser = userallconnections.get(conn).get("userid");
				if (cuser.equals(user)) {
					System.out.println("(getWebSocketByUser)found user id: " + cuser + "的connection");
					return conn;
				}
			}
		}
		
		return null;
	}

	/** * Add User to WebSocket Pool* @param inbound */ /* Done */
	public static void addUser(String username,String userid, WebSocket conn) {
		userconnections.put(conn, userid); // 最後再註解掉
//		usernameconnections.put(conn, username);
		
		Map<String,String> userinfo = new HashMap<String,String>();
		userinfo.put("userid", userid);
		userinfo.put("username", username);
		userallconnections.put(conn, userinfo); // 每一個client的connection配一個map
////		Map<String,String> clientmap = userallconnections.get(conn); 
//		if(clientmap != null && !clientmap.isEmpty()){
////			clientmap.put(conn, userinfo);
//			userallconnections.put(conn, userinfo);
//		}else{
////			clientmap = new HashMap<String,String>();
////			Map<WebSocket, Map<String,String>> userconnections = new HashMap<WebSocket, Map<String,String>>();
////			userconnections.put(conn, userinfo);
//			userallconnections.put(conn, userconnections);
//		}		
	}
	
	/** * Add User to WebSocket Pool* @param inbound */
	public static void addUserGroup(String usergroup, WebSocket conn) {
		usergroupconnections.put(conn, usergroup);
	}
	
	/** * Add User to WebSocket Pool* @param inbound */
	public static void addUserInteraction(String userinteraction, WebSocket conn) {
		userinteractionconnections.put(conn, userinteraction);
	}
	
	/** * Add User to WebSocket Pool* @param inbound */
	public static void addUserheartbeat(String userheartbeat, WebSocket conn) {
		userheartbeatconnections.put(conn, userheartbeat);
	}
	
	/** * Get Online User Name * @return */
	public static Collection<String> getOnlineUser() {
		List<String> setUsers = new ArrayList<String>();
		/*
		Collection<String> setUser = userconnections.values();
		for (String u : setUser) {
			setUsers.add(u);
		}
		*/
		/* 原方法
		Collection<String> setUserid = userconnections.values();
		for (String u : setUserid) {
			setUsers.add(u);
		}
		*/
		
		Set<org.java_websocket.WebSocket> conns3 = WebSocketPool.userallconnections.keySet();
//		System.out.println("conns.size(): " + conns3.size());
		for (org.java_websocket.WebSocket conn : conns3){
			String userid = WebSocketPool.getUserByKey(conn);
			setUsers.add(userid);
		}
		
		return setUsers;
	}

	/** * Get Online User Name * @return */ /* Done */
	public static Collection<String> getOnlineUserName() {
		List<String> setUsers = new ArrayList<String>();
		/*
		Collection<String> setUser = userconnections.values();
		for (String u : setUser) {
			setUsers.add(u);
		}
		*/
		/*
		Collection<String> setUsername = usernameconnections.values();
		for (String u : setUsername) {
			setUsers.add(u);
		}
		*/
		
//		Set<WebSocket> userconns = userallconnections.keySet();
		Collection<Map<String,String>> userinfos = userallconnections.values();
//		userinfo.put("username", username);
		for (Map<String,String> userinfo : userinfos){
			setUsers.add(userinfo.get("username"));
		}
		
		return setUsers;
	}

	/** * Remove User ID from WebSocket Pool * @param inbound */
	public static boolean removeUserID(WebSocket conn) {
		// 原方法
//		if (userconnections.containsKey(conn)) {
//			userconnections.remove(conn);
//			return true;
//		} else {
//			return false;
//		}
		
		// 修改方法:
		if (userallconnections.containsKey(conn)) {
			userallconnections.get(conn).remove("userid"); // 須再測試,牽涉範圍廣
			return true;
		} else {
			return false;
		}
		
	}
	
	/** * Remove User Name from WebSocket Pool * @param inbound */ /* Done */
	public static boolean removeUserName(WebSocket conn) {
		/* 原來方法 
		if (usernameconnections.containsKey(conn)) {
			usernameconnections.remove(conn);
			return true;
		} else {
			return false;
		}
		*/
		
		if (userallconnections.containsKey(conn)) {
			userallconnections.get(conn).remove("username");
			return true;
		} else {
			return false;
		}
		
		
	}
	
	/** * Remove User Group from WebSocket Pool * @param inbound */
	public static boolean removeUserGroup(WebSocket conn) {
		if (usergroupconnections.containsKey(conn)) {
			usergroupconnections.remove(conn);
			return true;
		} else {
			return false;
		}
	}
	
	/** * Remove User heartbeat from WebSocket Pool * @param inbound */
	public static boolean removeUserheartbeat(WebSocket conn) {
		if (userheartbeatconnections.containsKey(conn)) {
			userheartbeatconnections.remove(conn);
			return true;
		} else {
			return false;
		}
	}

	/** * Send Message to a User * @param user * @param message */
	public static void sendMessageToUser(WebSocket conn, String message)  {
		if (null != conn) {
			conn.send(message);
		}
	}

	/** * Send Message to all of User * @param message */
	public static void sendMessage(String message) {
		Set<WebSocket> keySet = userconnections.keySet();
		synchronized (keySet) {
			for (WebSocket conn : keySet) {
				String user = userconnections.get(conn);
				if (user != null) {
					conn.send(message);
				}
			}
		}
	}
	
	
	/**
	 * Group Members Map
	 */
	private static final Map<String, Map<WebSocket, Map<String,String>>> groupuserconnections = new HashMap<String, Map<WebSocket, Map<String,String>>>();
	
	/** * Add User to Group Map * @param inbound */
	public static void addUseringroup(String group,String username,String userid, WebSocket conn) {
		Map<String,String> userinfo = new HashMap<String,String>();
		userinfo.put("userid", userid);
		userinfo.put("username", username);
		Map<WebSocket, Map<String,String>> groupmap = groupuserconnections.get(group);
		if(groupmap != null && !groupmap.isEmpty()){
			groupmap.put(conn, userinfo);
			groupuserconnections.put(group, groupmap);
		}else{
			Map<WebSocket, Map<String,String>> userconnections = new HashMap<WebSocket, Map<String,String>>();
			userconnections.put(conn, userinfo);
			groupuserconnections.put(group, userconnections);
		}
	}
	
	/** * Remove User from Group * @param inbound */
	public static boolean removeUseringroup(String group,WebSocket conn) {
		Map<WebSocket, Map<String,String>> groupmap = groupuserconnections.get(group);
		if (groupmap.containsKey(conn)) {
			groupmap.remove(conn);
			return true;
		} else {
			return false;
		}
	}
	
	/** * Send Message to all of User in Group * @param message */
	public static void sendMessageingroup(String group,String message) {
		Map<WebSocket, Map<String,String>> groupmap = groupuserconnections.get(group);
		Set<WebSocket> keySet = groupmap.keySet();
		synchronized (keySet) {
			for (WebSocket conn : keySet) {
				String user = groupmap.get(conn).get("userid");
				if (user != null) {
					conn.send(message);
				}
			}
		}
	}
	
	/** * Get Online User Name in group * @return */
	public static Collection<String> getOnlineUseringroup(String group) {
		Map<WebSocket, Map<String,String>> groupmap = groupuserconnections.get(group);
		List<String> setUsers = new ArrayList<String>();
		Collection<Map<String,String>> setUser = groupmap.values();
		for (Map<String,String> u : setUser) {
			setUsers.add(u.get("username"));
		}
		return setUsers;
	}
	
	/** * Get Online count in group * @return */
	public static int getOnlineUseringroupCount(String group) {
		return groupuserconnections.get(group).size();
	}
	
	/** * Get User Name By WebSocket Key from Group * @param session */
	public static String getUserByKeyingroup(String group, WebSocket conn) {
		Map<WebSocket, Map<String,String>> groupmap = groupuserconnections.get(group);
		return groupmap.get(conn).get("username");
	}

	/** * Get Online Group Count * @param */
	public static int getGroupCount() {
		return groupuserconnections.size();
	}
	
	/** * Get Online Group Count * @param */
	public static Collection<String> getGroups() {
		List<String> setGroups = new ArrayList<String>();
		Collection<String> setGroup = groupuserconnections.keySet();
		for (String u : setGroup) {
			//System.out.println("u: "+u);
			setGroups.add(u);
		}
		return setGroups;
	}

	/** * Get WebSocket Key By User ID from Group * @param user */
	public static WebSocket getWebSocketByUseringroup(String group,String user) {
		Map<WebSocket, Map<String,String>> groupmap = groupuserconnections.get(group);
		Set<WebSocket> keySet = groupmap.keySet();
		synchronized (keySet) {
			for (WebSocket conn : keySet) {
				String cuser = groupmap.get(conn).get("userid");
				if (cuser.equals(user)) {
					return conn;
				}
			}
		}
		return null;
	}
	
	

	/**
	 * Agent & Client Members Map
	 */
	private static final Map<String, Map<WebSocket, Map<String, Object>>> TYPEconnections = new HashMap<String, Map<WebSocket, Map<String, Object>>>();

	/** * Add User to Agent or Client * @param inbound */
	public static void addUserinTYPE(String TYPE,String username, String userid,String date, WebSocket conn) {
		Map<WebSocket, Map<String, Object>> TYPEmap = TYPEconnections.get(TYPE);
		Map<String, Object> TYPEdatamap = new HashMap<String, Object>();
		if(TYPEmap != null && !TYPEmap.isEmpty()){
			TYPEdatamap.put("userid", userid);
			TYPEdatamap.put("username", username);
			if(TYPE.equals("Agent")){
				TYPEdatamap.put("status", "not ready");
			}else if(TYPE.equals("Client")){
				TYPEdatamap.put("status", "wait");
			}
			TYPEdatamap.put("time", date);
			TYPEmap.put(conn, TYPEdatamap);
			TYPEconnections.put(TYPE, TYPEmap);
		}else{
			Map<WebSocket, Map<String, Object>> TYPEuserconnections = new HashMap<WebSocket, Map<String, Object>>();
			TYPEdatamap.put("userid", userid);
			TYPEdatamap.put("username", username);
			if(TYPE.equals("Agent")){
				TYPEdatamap.put("status", "not ready");
			}else if(TYPE.equals("Client")){
				TYPEdatamap.put("status", "wait");
			}
			TYPEdatamap.put("time", date);
			TYPEuserconnections.put(conn, TYPEdatamap);
			TYPEconnections.put(TYPE, TYPEuserconnections);
		}
	}
	
	/** * Agent or Client User Information Update */
	public static void UserUpdate(String TYPE,String username, String userid,String date,String status,String reason, WebSocket conn) {
		Map<WebSocket, Map<String, Object>> TYPEmap = TYPEconnections.get(TYPE);
		Map<String, Object> TYPEdatamap = TYPEmap.get(conn);
		TYPEdatamap.put("status", status);
		TYPEdatamap.put("reason", reason);
		TYPEdatamap.put("time", date);
		TYPEmap.put(conn, TYPEdatamap);
		TYPEconnections.put(TYPE, TYPEmap);
	}
	
	/** * Remove User from Agent or Client* @param inbound */
	public static boolean removeUserinTYPE(String TYPE,WebSocket conn) {
		Map<WebSocket,  Map<String, Object>> TYPEmap = TYPEconnections.get(TYPE);
		if (TYPEmap.containsKey(conn)) {
			TYPEmap.remove(conn);
			return true;
		} else {
			return false;
		}
	}
	
	/** * Send Message to all of User in Agent or Client * @param message */
	public static void sendMessageinTYPE(String TYPE,String message) {
		Map<WebSocket,  Map<String, Object>> TYPEmap = TYPEconnections.get(TYPE);
		Set<WebSocket> keySet = TYPEmap.keySet();
		synchronized (keySet) {
			for (WebSocket conn : keySet) {
				/*
				String user = TYPEmap.get(conn).get("userid").toString();
				if (user != null) {
					conn.send(message);
				}
				*/
				Map<String, Object> userdata = TYPEmap.get(conn);
				if (userdata != null) {
					conn.send(message);
				}
			}
		}
	}
	
	/** * Get Online Longest User(Agent) * @return */
	public static String getOnlineLongestUserinTYPE(String TYPE) {
		Map<WebSocket,  Map<String, Object>> TYPEmap = TYPEconnections.get(TYPE);
		//List<String> setUsers = new ArrayList<String>();
		String settingUser = null;
		Date date = new Date();
		long UserStayTime = date.getTime();
		Collection<Map<String, Object>> setUser = TYPEmap.values();
		for (Map<String, Object> u : setUser) {
			//setUsers.add(u.get("userid").toString());
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			String userdatestring = u.get("time").toString();
			Date userdate = null;
			try {
				userdate = sdf.parse(userdatestring);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(userdate.getTime() <= UserStayTime && u.get("status").toString().trim().equals("ready")){
				UserStayTime = userdate.getTime();
				settingUser = u.get("userid").toString();
			}
		}
		return settingUser;
	}
	
	/** * Get Online User Name in Agent or Client * @return */
	public static Collection<String> getOnlineUserNameinTYPE(String TYPE) {
		Map<WebSocket,  Map<String, Object>> TYPEmap = TYPEconnections.get(TYPE);
		List<String> setUsers = new ArrayList<String>();
		Collection<Map<String, Object>> setUser = TYPEmap.values();
		for (Map<String, Object> u : setUser) {
			setUsers.add(u.get("username").toString());
		}
		return setUsers;
	}
	
	/** * Get Online User ID in Agent or Client * @return */
	public static Collection<String> getOnlineUserIDinTYPE(String TYPE) {
		Map<WebSocket,  Map<String, Object>> TYPEmap = TYPEconnections.get(TYPE);
		List<String> setUsers = new ArrayList<String>();
		Collection<Map<String, Object>> setUser = TYPEmap.values();
		for (Map<String, Object> u : setUser) {
			setUsers.add(u.get("userid").toString());
		}
		return setUsers;
	}
	
	/** * Get Online count in Agent or Client * @return */
	public static int getOnlineUserIDinTYPECount(String TYPE) {
		return TYPEconnections.get(TYPE).size();
	}
	
	/** * Get User Status in Agent or Client * ready * not ready * established * party remove * @param session */
	public static String getUserStatusByKeyinTYPE(String TYPE, WebSocket conn) {
		Map<WebSocket,  Map<String, Object>> TYPEmap = TYPEconnections.get(TYPE);
		return TYPEmap.get(conn).get("status").toString();
	}
	
	/** * Get User Status in Agent or Client * ready * not ready * established * party remove * @param session */
	public static String getUserReasonByKeyinTYPE(String TYPE, WebSocket conn) {
		Map<WebSocket,  Map<String, Object>> TYPEmap = TYPEconnections.get(TYPE);
		return TYPEmap.get(conn).get("reason").toString();
	}
	
	/** * Get User ID By Key in Agent or Client * @param session */
	public static String getUserByKeyinTYPE(String TYPE, WebSocket conn) {
		Map<WebSocket,  Map<String, Object>> TYPEmap = TYPEconnections.get(TYPE);
		return TYPEmap.get(conn).get("userid").toString();
	}

	/** * Get Agent or Client Count * @param */
	public static int getUserTYPECount(String TYPE) {
		return TYPEconnections.size();
	}

	/** * Get WebSocket By User ID in Agent or Client * @param user */
	public static WebSocket getWebSocketByUserinTYPE(String TYPE,String user) {
		Map<WebSocket,  Map<String, Object>> TYPEmap = TYPEconnections.get(TYPE);
		Set<WebSocket> keySet = TYPEmap.keySet();
		synchronized (keySet) {
			for (WebSocket conn : keySet) {
				String cuser = TYPEmap.get(conn).get("userid").toString();
				if (cuser.equals(user)) {
					return conn;
				}
			}
		}
		return null;
	}
	
	private static int leaveClient = 0;
	
	/** * Add a count to leaveClient* @param inbound */
	public static void addleaveClient() {
		leaveClient += 1;
	}
	
	/** * clean leaveClient* @param inbound */
	public static void cleanleaveClient() {
		leaveClient = 0;
	}
	
	/** * Get leaveClient * @param */
	public static int getleaveClient() {
		return leaveClient;
	}
}
