package restful.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;
import org.java_websocket.WebSocket;

import util.StatusEnum;
import util.Util;
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
		
		
		Collection<String> CollectionRooms = WebSocketRoomPool.getRoomIDs();
		JSONArray roomsjsonarray = new JSONArray();
		for (String room : CollectionRooms) {
			JSONObject roomsjsonobject = new JSONObject();
			roomsjsonobject.put("roomname", room);
			roomsjsonobject.put("roommember", WebSocketRoomPool.getOnlineUserNameinroom(room));
			roomsjsonobject.put("roommembercount", WebSocketRoomPool.getOnlineUserInRoomCount(room));
			roomsjsonarray.put(roomsjsonobject);
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
		jsonObject.put("Rooms", roomsjsonarray);
		jsonObject.put("Agents", agentsjsonarray);
		jsonObject.put("Clients", clientsjsonarray);
		jsonObject.put("usercount", WebSocketUserPool.getUserCount());
		jsonObject.put("roomcount", WebSocketRoomPool.getRoomCount());
		jsonObject.put("agentcount", WebSocketTypePool.getOnlineUserIDinTYPECount("Agent"));
		jsonObject.put("clientcount", WebSocketTypePool.getOnlineUserIDinTYPECount("Client"));
		
		
		jsonObject.put("leaveclientcount", WebSocketTypePool.getleaveClient());
		
		if(user!=null && !"".equals(user)){
			JSONObject userroomjsonObject = new JSONObject();
			WebSocket conn = WebSocketUserPool.getWebSocketByUser(user);
			//WebSocketUserPool.getUserroomCount(conn);
			jsonObject.put("userid", user);
			jsonObject.put("username", WebSocketUserPool.getUserNameByKey(conn));
			jsonObject.put("userroomcount", WebSocketUserPool.getUserRoomCount(conn));
			Collection<String> UserRooms = WebSocketUserPool.getUserRoomByKey(conn);
			JSONArray userroomjsonarray = new JSONArray();
			for (String room : UserRooms) {
				userroomjsonObject.put("roomid", room);
				userroomjsonObject.put("ProcessTime", WebSocketRoomPool.getProcessTimeCount(room, conn));
				userroomjsonarray.put(userroomjsonObject);
			}
			jsonObject.put("userrooms", userroomjsonarray);
			
			jsonObject.put("notready_usetime", getagentstatus_usetime(user, StatusEnum.NOTREADY.getDbid()));
			jsonObject.put("ready_usetime", getagentstatus_usetime(user, StatusEnum.READY.getDbid()));
			jsonObject.put("iestablished_usetime", getagentstatus_usetime(user, StatusEnum.IESTABLISHED.getDbid()));
			jsonObject.put("iestablished_usetime_avg", getagentstatus_usetime_avg(user, StatusEnum.IESTABLISHED.getDbid()));
			
		}

		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();
	}
	
	
		public static String getagentstatus_usetime(String person_dbid, String status_dbid) {
				
				StringBuilder responseSB = null;
				
				String postData = "person_dbid=" + person_dbid
						+"&status_dbid=" + status_dbid;

				try {
					// Connect to URL
					String hostURL = Util.getHostURLStr("IMWebSocket");
//					Util.getConsoleLogger().debug("hostURL: " + hostURL);
					URL url = new URL( hostURL + "/IMWebSocket/RESTful/Select_rpt_agentstatus_usetime");
//					URL url = new URL(
//							"http://127.0.0.1:8080/IMWebSocket/RESTful/Select_rpt_agentstatus_usetime");

					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();
					connection.setDoOutput(true);
					connection.setRequestMethod("POST");
					connection.setRequestProperty("Content-Type",
							"application/x-www-form-urlencoded");
					connection.setRequestProperty("Content-Length",
							String.valueOf(postData.length()));
					// Write data
					OutputStream os = connection.getOutputStream();
					os.write(postData.getBytes());
					// Read response
					responseSB = new StringBuilder();
					BufferedReader br = new BufferedReader(new InputStreamReader(
							connection.getInputStream(), "UTF-8"));
					String line;
					while ((line = br.readLine()) != null)
						responseSB.append(line);
					// Close streams
					br.close();
					os.close();
					// Util.getConsoleLogger().debug(responseSB);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				JSONObject jsonobject = new JSONObject(responseSB.toString());
				int usetime= jsonobject.getInt("second");
				
				SimpleDateFormat sdf = new SimpleDateFormat(util.Util.getSdfTimeFormat());
				SimpleDateFormat Ssdf = new SimpleDateFormat("ss");
				Date date = new Date();
				try {
					date = Ssdf.parse(String.valueOf(usetime));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String usertime_str = sdf.format(date);
				
				return usertime_str;
			}
		
		public static String getagentstatus_usetime_avg(String person_dbid, String status_dbid) {
			
			StringBuilder responseSB = null;
			
			String postData = "person_dbid=" + person_dbid
					+"&status_dbid=" + status_dbid;

			try {
				// Connect to URL
				String hostURL = Util.getHostURLStr("IMWebSocket");
//				Util.getConsoleLogger().debug("hostURL: " + hostURL);
				URL url = new URL( hostURL + "/IMWebSocket/RESTful/Select_rpt_agentstatus_usetime_avg");
//				URL url = new URL(
//						"http://127.0.0.1:8080/IMWebSocket/RESTful/Select_rpt_agentstatus_usetime_avg");

				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setDoOutput(true);
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				connection.setRequestProperty("Content-Length",
						String.valueOf(postData.length()));
				// Write data
				OutputStream os = connection.getOutputStream();
				os.write(postData.getBytes());
				// Read response
				responseSB = new StringBuilder();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						connection.getInputStream(), "UTF-8"));
				String line;
				while ((line = br.readLine()) != null)
					responseSB.append(line);
				// Close streams
				br.close();
				os.close();
				// Util.getConsoleLogger().debug(responseSB);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JSONObject jsonobject = new JSONObject(responseSB.toString());
			int usetime= jsonobject.getInt("second");
			
			SimpleDateFormat sdf = new SimpleDateFormat(util.Util.getSdfTimeFormat());
			SimpleDateFormat Ssdf = new SimpleDateFormat("ss");
			Date date = new Date();
			try {
				date = Ssdf.parse(String.valueOf(usetime));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String usertime_str = sdf.format(date);
			
			return usertime_str;
		}
	
}
