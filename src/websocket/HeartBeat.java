package websocket;
 
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.java_websocket.exceptions.WebsocketNotConnectedException;
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
//		hb.setHealthStatus(conn.toString());
		Timer timer = new Timer(conn.toString());
		TimerTask taskToExecute = new TimerTaskSendHeartBeat(hb, conn, timer);
		timer.scheduleAtFixedRate(taskToExecute, 1000, 1000);
		WebSocketUserPool.addUserHeartbeatTimer(timer, conn);

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
	
	public static void heartbeattouser(org.java_websocket.WebSocket conn, HeartBeat healthStatusHolder){
		System.out.println("conn " + conn + " is still online. ********************");
//		System.out.println("heartbeattouser() called");
		JSONObject sendjson = new JSONObject();
//		WebSocketUserPool.sendMessageToUser(conn, sendjson.toString()); // 透過此去偵測使用者連線sessiong是否還在
		
		try{
			WebSocketUserPool.sendMessageToUser(conn, sendjson.toString()); // 透過此去偵測使用者連線sessiong是否還在
		}catch(WebsocketNotConnectedException e){
			healthStatusHolder.setHealthStatus("RED"); // 透過此flag關掉HeartBeat排程
			conn.close();
		}
		
	}// end of heartbeattouser
	
//	public static void heartbeattoserver(String message, org.java_websocket.WebSocket conn){
//		JSONObject obj = new JSONObject(message);
//		String value = null;
//		Boolean heartbeat = false;
//		Set<String> keySet = obj.keySet();
//		synchronized (keySet) {
//			for (String key : keySet) {
//				if(key.equals("heartbeat")){
//					value = obj.getString("heartbeat");
//					heartbeat = true;
//				}
//			}
//		}
//		// 原則上,client.js會呼叫到這個方法時,通常都有拿到heartbeat="ap"這對key-value,此判斷是為了未來準備,暫時無特別用途
//		if(heartbeat){
//			WebSocketUserPool.addUserheartbeat(value, conn);
//		}else{
//			WebSocketUserPool.removeUser(conn);
//			WebSocketTypePool.removeUserinTYPE("Client", conn);
//			// 取得一個user所屬的所有groupid
//			List<String> groupids = WebSocketUserPool.getUserGroupByKey(conn);
//			for (String groupid: groupids){
//				//使用每個groupid,並找出相對應的group,再將其中的conn remove掉
//				WebSocketGroupPool.removeUseringroup(groupid, conn); // 這邊是否須考慮如果此user退出group,只剩下agent在的狀況? 還是此狀況交由其他處來做處理?				
//			}
//		}
//	}// end of heartbeattoserver
	
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
		System.out.println("this.healthStatusHolder.getHealthStatus():" + this.healthStatusHolder.getHealthStatus());
		if ("GREEN".equals(this.healthStatusHolder.getHealthStatus())){
			HeartBeat.heartbeattouser(conn,this.healthStatusHolder);
		}else{
			timer.cancel();
		}
		
	}
	
}