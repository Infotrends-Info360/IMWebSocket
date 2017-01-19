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

import websocket.bean.GroupInfo;
import websocket.bean.UserInfo;

//此類別給AgentFunction.java共同使用
//此類別給ClientFunction.java共同使用
//此類別給CommonFunction.java共同使用

//此類別給HeartBeat.java使用
//此類別給GetKPIServlet.java使用
public class WebSocketGroupPool{
	/**********************************************************************************/
	/**
	 * Group Members Map
	 */
	private static final Map<String, Map<WebSocket, GroupInfo>> groupuserconnections = new HashMap<String, Map<WebSocket, GroupInfo>>();
	
	/** * Add User to Group Map * @param inbound */
	public static void addUseringroup(String group,String username,String userid, WebSocket conn) {
		
		GroupInfo groupinfo = new GroupInfo();
		groupinfo.setUserid(userid);
		groupinfo.setUsername(username);
		
		Map<WebSocket, GroupInfo> groupmap = groupuserconnections.get(group);
		if (groupmap == null || groupmap.isEmpty()){
			groupmap = new HashMap<WebSocket, GroupInfo>();			
		}
		groupmap.put(conn, groupinfo);
		groupuserconnections.put(group, groupmap);
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
//		System.out.println("removeUseringroup(String group,WebSocket conn) called");
		Map<WebSocket, GroupInfo> groupmap = groupuserconnections.get(group);
		if (groupmap.containsKey(conn)) {
//			System.out.println(conn + "'s group is " + " removed");
			groupmap.remove(conn);
			return true;
		} else {
			return false;
		}
	}
	
	/** * Send Message to all of User in Group * @param message */
	public static void sendMessageingroup(String group,String message) {
		Map<WebSocket, GroupInfo> groupmap = groupuserconnections.get(group);
		Set<WebSocket> keySet = groupmap.keySet();
		synchronized (keySet) {
			for (WebSocket conn : keySet) {
				String user = groupmap.get(conn).getUserid();
				if (user != null) {
					conn.send(message);
				}
			}
		}
	}
	
	/** * Get Online User Name in group * @return */
	// 先預留著,以後判斷須不須保留
	public static Collection<String> getOnlineUseringroup(String group) {
		Map<WebSocket, GroupInfo> groupmap = groupuserconnections.get(group);
		List<String> setUsers = new ArrayList<String>();
		Collection<GroupInfo> setUser = groupmap.values();
		for (GroupInfo u : setUser) {
			setUsers.add(u.getUsername());
		}
		return setUsers;
	}
	
	/** * Get Online count in group * @return */
	// 先預留著,以後判斷須不須保留
	public static int getOnlineUseringroupCount(String group) {
		return groupuserconnections.get(group).size();
	}
	
	/** * Get User Name By WebSocket Key from Group * @param session */
	// 先預留著,以後判斷須不須保留
	public static String getUserByKeyingroup(String group, WebSocket conn) {
		Map<WebSocket, GroupInfo> groupmap = groupuserconnections.get(group);
		return groupmap.get(conn).getUsername();
	}

	/** * Get Online Group Count * @param */
	public static int getGroupCount() {
		return groupuserconnections.size();
	}
	
	/** * Get Online Group * @param */
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
	// 先預留著,以後判斷須不須保留
	public static WebSocket getWebSocketByUseringroup(String group,String user) {
		Map<WebSocket, GroupInfo> groupmap = groupuserconnections.get(group);
		Set<WebSocket> keySet = groupmap.keySet();
		synchronized (keySet) {
			for (WebSocket conn : keySet) {
				String cuser = groupmap.get(conn).getUserid();
				if (cuser.equals(user)) {
					return conn;
				}
			}
		}
		return null;
	}
	
}