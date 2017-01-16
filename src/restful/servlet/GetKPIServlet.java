package restful.servlet;

import java.io.IOException;
import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;
import org.java_websocket.WebSocket;

import websocket.WebSocketPool;

/**
 * RESTful Get KPI
 * @author Lin
 */

@Path("/GetKPI")
public class GetKPIServlet {
	
	@GET
	public Response GetFromPath() throws IOException {
		
		Collection<String> CollectionUsers = WebSocketPool.getOnlineUser();
		JSONArray usersjsonarray = new JSONArray();
		for (String userid : CollectionUsers) {
			JSONObject usersjsonobject = new JSONObject();
			usersjsonobject.put("userid", userid);
			WebSocket websocket = WebSocketPool.getWebSocketByUser(userid);
			usersjsonobject.put("websocket", websocket);
			usersjsonobject.put("username", WebSocketPool.getUserNameByKey(websocket));
			usersjsonarray.put(usersjsonobject);
		}
		
		
		Collection<String> CollectionGroups = WebSocketPool.getGroups();
		JSONArray groupsjsonarray = new JSONArray();
		for (String group : CollectionGroups) {
			JSONObject groupsjsonobject = new JSONObject();
			groupsjsonobject.put("groupname", group);
			groupsjsonobject.put("groupmember", WebSocketPool.getOnlineUseringroup(group));
			groupsjsonobject.put("groupmembercount", WebSocketPool.getOnlineUseringroupCount(group));
			groupsjsonarray.put(groupsjsonobject);
		}
		
		
		Collection<String> CollectionAgents = WebSocketPool.getOnlineUserIDinTYPE("Agent");
		JSONArray agentsjsonarray = new JSONArray();
		for (String agent : CollectionAgents) {
			JSONObject agentsjsonobject = new JSONObject();
			agentsjsonobject.put("agentid", agent);
			WebSocket websocket = WebSocketPool.getWebSocketByUser(agent);
			agentsjsonobject.put("websocket", websocket);
			agentsjsonobject.put("agentname", WebSocketPool.getUserNameByKey(websocket));
			agentsjsonobject.put("agentstatus", WebSocketPool.getUserStatusByKeyinTYPE("Agent", websocket));
			agentsjsonarray.put(agentsjsonobject);
		}
		
		//Client
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("Users", usersjsonarray);
		jsonObject.put("Groups", groupsjsonarray);
		jsonObject.put("Agents", agentsjsonarray);
		jsonObject.put("usercount", WebSocketPool.getUserCount());
		jsonObject.put("groupcount", WebSocketPool.getGroupCount());
		jsonObject.put("agentcount", WebSocketPool.getOnlineUserIDinTYPECount("Agent"));
		jsonObject.put("clientcount", WebSocketPool.getOnlineUserIDinTYPECount("Client"));
		
		jsonObject.put("leaveclientcount", WebSocketPool.getleaveClient());
		

		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
	
}
