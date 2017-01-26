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

import websocket.bean.TypeInfo;
import websocket.bean.UserInfo;

//此類別給AgentFunction.java共同使用
//此類別給ClientFunction.java共同使用
//此類別給CommonFunction.java共同使用

//此類別給HeartBeat.java使用
//此類別給GetKPIServlet.java使用
public class WebSocketTypePool{
	/**********************************************************************************/
	/**
	 * Agent & Client Members Map
	 */
	// TYPEconnections第一層Map: 用來區分type是Agent or Client
	// TYPEconnections第二層Map: 用來區分每個WebSocket所對應到的Bean
	private static final Map<String, Map<WebSocket, TypeInfo>> TYPEconnections = new HashMap<>();

	/** * Add User to Agent or Client * @param inbound */
	public static void addUserinTYPE(String TYPE,String username, String userid,String date, WebSocket conn) {
		Map<WebSocket, TypeInfo> TYPEmap = TYPEconnections.get(TYPE);
		
		if (TYPEmap == null || TYPEmap.isEmpty()){
			TYPEmap = new HashMap<>();
		}
		
		TypeInfo typeinfo = new TypeInfo();
		typeinfo.setUserid(userid);
		typeinfo.setUsername(username);
		if(TYPE.equals("Agent")){
			typeinfo.setStatus("not ready");
		}else if(TYPE.equals("Client")){
			typeinfo.setStatus("wait");
		}
		typeinfo.setTime(date);
		TYPEmap.put(conn, typeinfo);
		TYPEconnections.put(TYPE, TYPEmap);
	}
	
	/** * Agent or Client User Information Update */
	public static void UserUpdate(String TYPE,String username, String userid,String date,String status,String reason, WebSocket conn) {
		Map<WebSocket, TypeInfo> TYPEmap = TYPEconnections.get(TYPE);
		TypeInfo TYPEdatamap = TYPEmap.get(conn);
		TYPEdatamap.setStatus(status);
		TYPEdatamap.setReason(reason);
		TYPEdatamap.setTime(date);
		TYPEmap.put(conn, TYPEdatamap);
		TYPEconnections.put(TYPE, TYPEmap);
	}
	
	/** * Remove User from Agent or Client* @param inbound */
	public static boolean removeUserinTYPE(String TYPE,WebSocket conn) {
		Map<WebSocket,  TypeInfo> TYPEmap = TYPEconnections.get(TYPE);
		if (TYPEmap.containsKey(conn)) {
			TYPEmap.remove(conn);
			return true;
		} else {
			return false;
		}
	}
	
	/** * Send Message to all of User in Agent or Client * @param message */
	public static void sendMessageinTYPE(String TYPE,String message) {
		Map<WebSocket,  TypeInfo> TYPEmap = TYPEconnections.get(TYPE);
		Set<WebSocket> keySet = TYPEmap.keySet();
		synchronized (keySet) {
			for (WebSocket conn : keySet) {
				/*
				String user = TYPEmap.get(conn).get("userid").toString();
				if (user != null) {
					conn.send(message);
				}
				*/
				TypeInfo userdata = TYPEmap.get(conn);
				if (userdata != null) {
					conn.send(message);
				}
			}
		}
	}
	
	/** * Get Online Longest User(Agent) * @return */
	public static String getOnlineLongestUserinTYPE(String TYPE) {
		Map<WebSocket,  TypeInfo> TYPEmap = TYPEconnections.get(TYPE);
		//List<String> setUsers = new ArrayList<String>();
		String settingUser = null;
		Date date = new Date();
		long UserStayTime = date.getTime();
		Collection<TypeInfo> setUser = TYPEmap.values();
		for (TypeInfo u : setUser) {
			//setUsers.add(u.get("userid").toString());
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			String userdatestring = u.getTime();
			Date userdate = null;
			try {
				userdate = sdf.parse(userdatestring);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(userdate.getTime() <= UserStayTime && u.getStatus().trim().equals("ready")){
				UserStayTime = userdate.getTime(); // 每次都會將UserStayTime拿去當作"上一個"Uset的等待時間
				settingUser = u.getUserid();
			}
		}
		return settingUser;
	}
	
	/** * Get Online User Name in Agent or Client * @return */
	public static Collection<String> getOnlineUserNameinTYPE(String TYPE) {
		Map<WebSocket,  TypeInfo> TYPEmap = TYPEconnections.get(TYPE);
		List<String> setUsers = new ArrayList<String>();
		Collection<TypeInfo> setUser = TYPEmap.values();
		for (TypeInfo u : setUser) {
			setUsers.add(u.getUsername());
		}
		return setUsers;
	}
	
	/** * Get Online User ID in Agent or Client * @return */
	public static Collection<String> getOnlineUserIDinTYPE(String TYPE) {
		Map<WebSocket,  TypeInfo> TYPEmap = TYPEconnections.get(TYPE);
		List<String> setUsers = new ArrayList<String>();
		if (TYPEmap != null && !TYPEmap.isEmpty()){
			Collection<TypeInfo> setUser = TYPEmap.values();
			for (TypeInfo u : setUser) {
				setUsers.add(u.getUserid());
			}			
		}
		return setUsers;
	}
	
	/** * Get Online count in Agent or Client * @return */
	public static int getOnlineUserIDinTYPECount(String TYPE) {
		return TYPEconnections.get(TYPE).size();
	}
	
	/** * Get User Status in Agent or Client * ready * not ready * established * party remove * @param session */
	public static String getUserStatusByKeyinTYPE(String TYPE, WebSocket conn) {
		Map<WebSocket,  TypeInfo> TYPEmap = TYPEconnections.get(TYPE);
		return TYPEmap.get(conn).getStatus();
	}
	
	/** * Get User Status in Agent or Client * ready * not ready * established * party remove * @param session */
	public static String getUserReasonByKeyinTYPE(String TYPE, WebSocket conn) {
		Map<WebSocket,  TypeInfo> TYPEmap = TYPEconnections.get(TYPE);
		return TYPEmap.get(conn).getReason();
	}
	
	/** * Get User ID By Key in Agent or Client * @param session */
	public static String getUserByKeyinTYPE(String TYPE, WebSocket conn) {
		Map<WebSocket,  TypeInfo> TYPEmap = TYPEconnections.get(TYPE);
		return TYPEmap.get(conn).getUserid();
	}

	/** * Get Agent or Client Count * @param */
	public static int getUserTYPECount(String TYPE) {
		return TYPEconnections.size();
	}

	/** * Get WebSocket By User ID in Agent or Client * @param user */
	public static WebSocket getWebSocketByUserinTYPE(String TYPE,String user) {
		Map<WebSocket,  TypeInfo> TYPEmap = TYPEconnections.get(TYPE);
		Set<WebSocket> keySet = TYPEmap.keySet();
		synchronized (keySet) {
			for (WebSocket conn : keySet) {
				String cuser = TYPEmap.get(conn).getUserid();
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

	public static Map<String, Map<WebSocket, TypeInfo>> getTypeconnections() {
		return TYPEconnections;
	}
	
	
	
}