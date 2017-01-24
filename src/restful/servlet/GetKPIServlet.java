package restful.servlet;

import java.io.IOException;
import java.util.Collection;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;
import org.java_websocket.WebSocket;

import websocket.pools.WebSocketRoomPool;
import websocket.pools.WebSocketTypePool;
import websocket.pools.WebSocketUserPool;

/**
 * RESTful Get KPI
 * @author Lin
 */

@Path("/GetKPI")
public class GetKPIServlet {
	
	@POST
	public Response GetFromPath(@FormParam("user")
			String user) throws IOException {
		
		Collection<String> CollectionUsers = WebSocketUserPool.getOnlineUser();
		JSONArray usersjsonarray = new JSONArray();
		for (String userid : CollectionUsers) {
			JSONObject usersjsonobject = new JSONObject();
			usersjsonobject.put("userid", userid);
			WebSocket websocket = WebSocketUserPool.getWebSocketByUser(userid);
			usersjsonobject.put("websocket", websocket);
			usersjsonobject.put("username", WebSocketUserPool.getUserNameByKey(websocket));
			usersjsonarray.put(usersjsonobject);
		}
		
		
		Collection<String> CollectionGroups = WebSocketRoomPool.getRooms();
		JSONArray groupsjsonarray = new JSONArray();
		for (String group : CollectionGroups) {
			JSONObject groupsjsonobject = new JSONObject();
			groupsjsonobject.put("groupname", group);
			groupsjsonobject.put("groupmember", WebSocketRoomPool.getOnlineUserinroom(group));
			groupsjsonobject.put("groupmembercount", WebSocketRoomPool.getOnlineUserInRoomCount(group));
			groupsjsonarray.put(groupsjsonobject);
		}
		
		
		Collection<String> CollectionAgents = WebSocketTypePool.getOnlineUserIDinTYPE("Agent");
		JSONArray agentsjsonarray = new JSONArray();
		for (String agent : CollectionAgents) {
			JSONObject agentsjsonobject = new JSONObject();
			agentsjsonobject.put("agentid", agent);
			WebSocket websocket = WebSocketUserPool.getWebSocketByUser(agent);
			agentsjsonobject.put("websocket", websocket);
			agentsjsonobject.put("agentname", WebSocketUserPool.getUserNameByKey(websocket));
			agentsjsonobject.put("agentstatus", WebSocketTypePool.getUserStatusByKeyinTYPE("Agent", websocket));
			agentsjsonarray.put(agentsjsonobject);
		}
		
		Collection<String> CollectionClients = WebSocketTypePool.getOnlineUserIDinTYPE("Client");
		JSONArray clientsjsonarray = new JSONArray();
		for (String client : CollectionClients) {
			JSONObject clientsjsonobject = new JSONObject();
			clientsjsonobject.put("clientid", client);
			WebSocket websocket = WebSocketUserPool.getWebSocketByUser(client);
			clientsjsonobject.put("websocket", websocket);
			clientsjsonobject.put("clientname", WebSocketUserPool.getUserNameByKey(websocket));
			clientsjsonobject.put("cliententstatus", WebSocketTypePool.getUserStatusByKeyinTYPE("Client", websocket));
			clientsjsonarray.put(clientsjsonobject);
		}
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("Users", usersjsonarray);
		jsonObject.put("Groups", groupsjsonarray);
		jsonObject.put("Agents", agentsjsonarray);
		jsonObject.put("Clients", clientsjsonarray);
		jsonObject.put("usercount", WebSocketUserPool.getUserCount());
		jsonObject.put("groupcount", WebSocketRoomPool.getRoomCount());
		jsonObject.put("agentcount", WebSocketTypePool.getOnlineUserIDinTYPECount("Agent"));
		jsonObject.put("clientcount", WebSocketTypePool.getOnlineUserIDinTYPECount("Client"));
		
		jsonObject.put("leaveclientcount", WebSocketTypePool.getleaveClient());
		
		if(user!=null && !"".equals(user)){
			JSONObject usergroupjsonObject = new JSONObject();
			WebSocket conn = WebSocketUserPool.getWebSocketByUser(user);
			//WebSocketUserPool.getUsergroupCount(conn);
			jsonObject.put("userid", user);
			jsonObject.put("username", WebSocketUserPool.getUserNameByKey(conn));
			jsonObject.put("usergroupcount", WebSocketUserPool.getUserRoomCount(conn));
			Collection<String> UserGroups = WebSocketUserPool.getUserRoomByKey(conn);
			JSONArray usergroupjsonarray = new JSONArray();
			for (String group : UserGroups) {
				usergroupjsonObject.put("groupid", group);
				usergroupjsonObject.put("ProcessTime", WebSocketRoomPool.getProcessTimeCount(group, conn));
				usergroupjsonarray.put(usergroupjsonObject);
			}
			jsonObject.put("usergroups", usergroupjsonarray);
			
		}

		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
	
}
