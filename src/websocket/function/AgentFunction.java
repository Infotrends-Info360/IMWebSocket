package websocket.function;

import org.json.JSONObject;

import websocket.pools.WebSocketGroupPool;
import websocket.pools.WebSocketTypePool;
import websocket.pools.WebSocketUserPool;

public class AgentFunction {
	/** * Agent close Group */
	public static void Agentclosegroup(String message,
			org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String group = obj.getString("group");
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "Agentclosegroup");
		sendjson.put("from", obj.getString("id"));
		sendjson.put("fromName",  obj.getString("UserName"));
		sendjson.put("channel", obj.getString("channel"));
		WebSocketGroupPool.sendMessageingroup(
				group,sendjson.toString());
	}
	
	/** * send Accept Event */
	public static void AcceptEvent(String message, org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String group = obj.getString("group");
		org.java_websocket.WebSocket sendto = WebSocketUserPool
				.getWebSocketByUser(obj.getString("sendto"));
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "AcceptEvent");
		sendjson.put("from", obj.getString("id"));
		sendjson.put("fromName",  obj.getString("UserName"));
		sendjson.put("group",  group);
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
	
	/** * ReleaseEvent */
	public static void ReleaseEvent(String message, org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		org.java_websocket.WebSocket sendto = WebSocketUserPool
				.getWebSocketByUser(obj.getString("sendto")); // "sendto" 紀錄的是此Agent要關閉的Client UserId
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "ReleaseEvent");
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
	
	/** * create a groupId * @param message */
	public static void creategroupId(String message, org.java_websocket.WebSocket conn) {
		String groupId = java.util.UUID.randomUUID().toString();
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "creategroupId");
		sendjson.put("groupId", groupId);
		WebSocketUserPool.sendMessageToUser(conn, sendjson.toString());
	}
}
