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

public class WebSocketGroupPool extends WebSocketUserPool{
	/**********************************************************************************/
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
	
}