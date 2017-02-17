package websocket.function;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.java_websocket.WebSocket;
import org.json.JSONObject;

import websocket.bean.UserInfo;
import websocket.pools.WebSocketRoomPool;
import websocket.pools.WebSocketTypePool;
import websocket.pools.WebSocketUserPool;

//此類別給WebSocjet.java使用
public class AgentFunction {

	/** * send Accept Event */
	public static void AcceptEvent(String message, org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String roomID = obj.getString("roomID");
		org.java_websocket.WebSocket sendto = WebSocketUserPool
				.getWebSocketByUser(obj.getString("sendto"));
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "AcceptEvent");
		sendjson.put("from", obj.getString("id"));
		sendjson.put("fromName",  obj.getString("UserName"));
		sendjson.put("roomID",  roomID);
		sendjson.put("channel", obj.getString("channel"));
		WebSocketUserPool.sendMessageToUser(
				sendto,sendjson.toString());
	}
	
	/** * RejectEvent */
	public static void RejectEvent(String message, org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		org.java_websocket.WebSocket sendto = WebSocketUserPool
				.getWebSocketByUser(obj.getString("sendto"));
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "RejectEvent");
		sendjson.put("from", obj.getString("id"));
		sendjson.put("fromName",  obj.getString("UserName"));
		sendjson.put("channel", obj.getString("channel"));
		WebSocketUserPool.sendMessageToUser(sendto, sendjson.toString());
	}
	
	/** * get Agent Status */
	public static void getUserStatus(String message, org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String ACtype = obj.getString("ACtype");
		String status = WebSocketTypePool.getUserStatusByKeyinTYPE(ACtype, conn);
		String reason = WebSocketTypePool.getUserReasonByKeyinTYPE(ACtype, conn);
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "getUserStatus");
		sendjson.put("from", obj.getString("id"));
		sendjson.put("Status",  status);
		sendjson.put("Reason",  reason);
		sendjson.put("channel", obj.getString("channel"));
		WebSocketUserPool.sendMessageToUser(
				conn, sendjson.toString());
	}
	
	/** * create a roomId * @param message */
	public static void createRoomId(String message, org.java_websocket.WebSocket conn) {
		String roomId = java.util.UUID.randomUUID().toString();
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "createroomId");
		sendjson.put("roomId", roomId);
		WebSocketUserPool.sendMessageToUser(conn, sendjson.toString());
	}
	
	/** * Get Message from Agent or Client list */
	// 此方法尚未用到,廣播用途,給Agent呼叫,功用為發給所有Agent
	public static void getMessageinTYPE(String message,
			org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String ACtype = obj.getString("ACtype");
		String username = obj.getString("UserName");
		String text = obj.getString("text");
		WebSocketTypePool.sendMessageinTYPE(ACtype, username + ": " + text);
	}
	
	public static void refreshAgentList(){
		Map<WebSocket, UserInfo> agentMap = WebSocketTypePool.getTypeconnections().get("Agent");
		Collection<String> agentIDList = WebSocketTypePool.getOnlineUserIDinTYPE("Agent");
		System.out.println("userjoin() - agentIDList.size(): " + agentIDList.size());
		Set<WebSocket> agentConnList = agentMap.keySet();
		for (WebSocket tmpConn: agentConnList){
			
			JSONObject sendjson02 = new JSONObject();
			sendjson02.put("Event", "refreshAgentList");
			sendjson02.put("agentIDList", agentIDList);
			WebSocketUserPool.sendMessageToUser(tmpConn, sendjson02.toString());
		}
	}
	
}
