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
import websocket.bean.UserInfo;

public class WebSocketPool {
	private static final String USERID = "userid";
	private static final String USERNAME = "username";
	private static final String USERGROUP = "usergroup";
	private static final String USERINTERACTION = "userinteraction";
	private static final String USERHEARTBEAT = "userheartbeat";
	
	/**
	 * online User ID/NAME Map
	 * 暫時先設為public,測試用
	 */
	public static final Map<WebSocket, UserInfo> userallconnections = new HashMap<WebSocket,UserInfo>();	
	
	/** * Get User By Key * @param session */ /* Done */
	public static String getUserByKey(WebSocket conn) {
		return userallconnections.get(conn).getUserid();
	}
	
	/** * Get User By Key * @param session */ /* Done */
	public static String getUserNameByKey(WebSocket conn) {
		return userallconnections.get(conn).getUsername();
	}
	
	/** * Get User By Key * @param session */ /* Done */
	public static String getUserGroupByKey(WebSocket conn) {
		return userallconnections.get(conn).getUsergroup();
	}

	/** * Get User By Key * @param session */ /* Done */
	public static String getUserInteractionByKey(WebSocket conn) {
		return userallconnections.get(conn).getUserinteraction();
	}
	
	/** * Get User By Key * @param session */ /* Done */
	public static String getUserheartbeatByKey(WebSocket conn) {
		return userallconnections.get(conn).getUserheartbeat();
	}

	/** * Get Online User Count * @param */ /* Done */
	public static int getUserCount() {
		return userallconnections.size();
	}

	/** * Get WebSocket By User ID * @param user */ /* Done */
	public static WebSocket getWebSocketByUser(String user) {
		Set<WebSocket> conns = userallconnections.keySet();
		synchronized (conns) {
			for (WebSocket conn : conns) {
				String cuser = userallconnections.get(conn).getUserid();
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
		UserInfo userinfo = new UserInfo();
		userinfo.setUserid(userid);
		userinfo.setUsername(username);
		userallconnections.put(conn, userinfo); // 每一個client的connection配一個vo
	}
	
	/** * Add User to WebSocket Pool* @param inbound */ /* Done */
	public static void addUserGroup(String usergroup, WebSocket conn) {
		userallconnections.get(conn).setUsergroup(usergroup);
	}
	
	/** * Add User to WebSocket Pool* @param inbound */ /* Done */
	public static void addUserInteraction(String userinteraction, WebSocket conn) {
		userallconnections.get(conn).setUserinteraction(userinteraction);
	}
	
	/** * Add User to WebSocket Pool* @param inbound */ /* Done */
	public static void addUserheartbeat(String userheartbeat, WebSocket conn) {
		userallconnections.get(conn).setUserheartbeat(userheartbeat);
	}
	
	/** * Get Online User Name * @return */ /* Done */
	public static Collection<String> getOnlineUser() {
		List<String> setUsers = new ArrayList<String>();
		
		Set<org.java_websocket.WebSocket> conns = WebSocketPool.userallconnections.keySet();
		for (org.java_websocket.WebSocket conn : conns){
			String userid = WebSocketPool.getUserByKey(conn);
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
	
	/** * Remove User Group from WebSocket Pool * @param inbound */ /* Done */
	public static boolean removeUserGroup(WebSocket conn) {
		
		if (userallconnections.containsKey(conn)) {
			userallconnections.get(conn).setUsergroup(null); // 看是要null還是"",目前覺得null比較同於原本的Map.remove(USERGROUP)
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
	public static void sendMessageToUser(WebSocket conn, String message)  {
		if (null != conn) {
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
	
	
	//--------------------------------------------------------------------------
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
	public static boolean removeGroup(String group) {
		//Map<WebSocket, Map<String,String>> groupmap = groupuserconnections.get(group);
		if (groupuserconnections.containsKey(group)) {
			groupuserconnections.remove(group);
			return true;
		} else {
			return false;
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