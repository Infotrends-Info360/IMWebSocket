package websocket;

import java.util.Calendar;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import websocket.function.ClientFunction;
import websocket.pools.WebSocketGroupPool;
import websocket.pools.WebSocketTypePool;
import websocket.pools.WebSocketUserPool;

public class HeartBeat {
	private String healthStatus = "GREEN";

	public void heartbeating(org.java_websocket.WebSocket conn) {
		// String User = WebSocketPool.getUserByKey(conn);
		HeartBeat hb = new HeartBeat();
		hb.setHealthStatus(conn.toString());
		Timer timer = new Timer(conn.toString());
		TimerTask taskToExecute = new TimerTaskSendHeartBeat(hb, conn, timer);
		timer.scheduleAtFixedRate(taskToExecute, 1000, 10000);

		// Wait for 60 seconds and then cancel the timer cleanly
		// try {
		// Thread.sleep(60000);
		// } catch (InterruptedException e) {
		// }
		// System.out.println("Cancelling Timer Cleanly after 60 seconds");
		// timer.cancel();
	}

	/**
	 * Get Heartbeat Status of the application, could be GREEN / AMBER / RED
	 * based on any exceptions or service health
	 * 
	 * @return String
	 */
	public String getHealthStatus() {
		return this.healthStatus;
	}

	/**
	 * Set the status for the application could be GREEN / AMBER / RED
	 * 
	 * @param healthStatus
	 */
	public void setHealthStatus(String healthStatus) {
		this.healthStatus = healthStatus;
	}
	
	public static void heartbeattouser(org.java_websocket.WebSocket conn){
		String User = WebSocketUserPool.getUserByKey(conn);
		if (User != null && !"".equals(User)) {
			JSONObject sendjson = new JSONObject();
			sendjson.put("Event", "heartbeattouser");
			sendjson.put("heartbeat", "AP");
			WebSocketUserPool.sendMessageToUser(conn, sendjson.toString());
		}else{
//			WebSocketPool.removeUserID(conn);
//			WebSocketPool.removeUserName(conn);
			WebSocketUserPool.removeUser(conn);
			// 以下可省略
			WebSocketTypePool.removeUserinTYPE("Client", conn);
			WebSocketUserPool.removeUserheartbeat(conn);
			String groupid = WebSocketUserPool.getUserGroupByKey(conn);
			if (groupid != null && !"".equals(groupid)) {
				WebSocketUserPool.removeUserGroup(conn);
				WebSocketGroupPool.removeUseringroup(groupid, conn);
			}
		}
	}// end of heartbeattouser
	
	public static void heartbeattoserver(String message, org.java_websocket.WebSocket conn){
		JSONObject obj = new JSONObject(message);
		String value = null;
		Boolean heartbeat = false;
		Set<String> keySet = obj.keySet();
		synchronized (keySet) {
			for (String key : keySet) {
				if(key.equals("heartbeat")){
					value = obj.getString("heartbeat");
					heartbeat = true;
				}
			}
		}
		if(heartbeat){
			WebSocketUserPool.addUserheartbeat(value, conn);
		}else{
//			WebSocketPool.removeUserID(conn);
//			WebSocketPool.removeUserName(conn);
			WebSocketUserPool.removeUser(conn);
			WebSocketTypePool.removeUserinTYPE("Client", conn);
			WebSocketUserPool.removeUserheartbeat(conn);
			String groupid = WebSocketUserPool.getUserGroupByKey(conn);
			if (groupid != null && !"".equals(groupid)) {
				WebSocketUserPool.removeUserGroup(conn);
				WebSocketGroupPool.removeUseringroup(groupid, conn);
			}
		}
	}// end of heartbeattoserver
	
}

class TimerTaskSendHeartBeat extends TimerTask {

	HeartBeat healthStatusHolder = null;

	org.java_websocket.WebSocket conn;

	Timer timer;

	public TimerTaskSendHeartBeat(HeartBeat healthStatusHolder,
			org.java_websocket.WebSocket conn, Timer timer) {
		this.healthStatusHolder = healthStatusHolder;
		this.conn = conn;
		this.timer = timer;
	}

	@Override
	public void run() {

//		WebSocket.heartbeattouser(conn);
		HeartBeat.heartbeattouser(conn);

		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String heartbeat = WebSocketUserPool.getUserheartbeatByKey(conn);
		String User = WebSocketUserPool.getUserByKey(conn);
//		System.out.println("heartbeat: "+heartbeat);
		if (User != null && !"".equals(User)) {
			if (heartbeat.equals("ap")) {

				System.out.println("HeartBeat Get " + User + " Online");

			} else {
				String message = WebSocketUserPool.getUserInteractionByKey(conn);
				System.out.println(message);
				WebSocketUserPool.removeUserheartbeat(conn);
				if (message != null && !"".equals(message)) {
					JSONObject obj = new JSONObject(message);
					String closefrom = obj.getString("closefrom");
					if (closefrom.equals("default")) {
						obj.put("status", 3);
						obj.put("stoppedreason", "server:HeartBeatLose");
						obj.put("closefrom", "server:HeartBeatLose");
						message = obj.toString();
					}
					ClientFunction.interactionlog(message, conn);
					System.out.println("Heartbeat lose Timer Cleanly and set Interaction log, conn:" + conn);
					timer.cancel();
				}
			}
		} else {
			String message = WebSocketUserPool.getUserInteractionByKey(conn);
			System.out.println("message: "+message);
			WebSocketUserPool.removeUserheartbeat(conn);
			if (message != null && !"".equals(message)) {
				JSONObject obj = new JSONObject(message);
				String closefrom = obj.getString("closefrom");
				if (closefrom.equals("default")) {
					obj.put("status", 3);
					obj.put("stoppedreason", "server:UserNotFind");
					obj.put("closefrom", "server:UserNotFind");
					message = obj.toString();
				}
				ClientFunction.interactionlog(message, conn);
				System.out.println("User unfind Timer Cleanly and set Interaction log, conn:" + conn);
				timer.cancel();
			}
		}

	}// end of run()
	
}