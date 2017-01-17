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

import org.java_websocket.WebSocket;
import websocket.bean.UserInfo;

public class WebSocketTypePool extends WebSocketGroupPool{
	/**********************************************************************************/
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