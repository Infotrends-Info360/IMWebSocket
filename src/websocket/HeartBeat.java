package websocket;
 
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.json.JSONObject;

import util.Util;
import websocket.function.ClientFunction;
import websocket.function.CommonFunction;
import websocket.pools.WebSocketRoomPool;
import websocket.pools.WebSocketTypePool;
import websocket.pools.WebSocketUserPool;

public class HeartBeat {
	private String healthStatus = "GREEN";

	public void heartbeating(org.java_websocket.WebSocket conn) {
		HeartBeat hb = new HeartBeat();
		Util.getConsoleLogger().info( WebSocketUserPool.getUserNameByKey(conn) + " is connected. (HeartBeat)");
		Util.getFileLogger().info( WebSocketUserPool.getUserNameByKey(conn) + " is connected. (HeartBeat)");
		Timer timer = new Timer(conn.toString());
		TimerTask taskToExecute = new TimerTaskSendHeartBeat(hb, conn, timer);
		timer.scheduleAtFixedRate(taskToExecute, 1000, 1000);
		WebSocketUserPool.addUserHeartbeatTimer(timer, conn);
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
		Util.getConsoleLogger().debug("heartbeattouser - " + WebSocketUserPool.getUserNameByKey(conn));
		JSONObject sendjson = new JSONObject();
		try{
			WebSocketUserPool.sendMessageToUser(conn, sendjson.toString()); // 透過此去偵測使用者連線sessiong是否還在
		}catch(WebsocketNotConnectedException e){
			Util.getFileLogger().info(e.getMessage());
			Util.getConsoleLogger().info(e.getMessage());
			healthStatusHolder.setHealthStatus("RED"); // 透過此flag關掉HeartBeat排程
		}
	}// end of heartbeattouser	
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
		if ("GREEN".equals(this.healthStatusHolder.getHealthStatus())){
			HeartBeat.heartbeattouser(conn,this.healthStatusHolder);
		}else{
			Util.getConsoleLogger().info( WebSocketUserPool.getUserNameByKey(conn) + " is disconnected. (HeartBeat)");				
			Util.getFileLogger().info( WebSocketUserPool.getUserNameByKey(conn) + " is disconnected. (HeartBeat)");				
			timer.cancel();
			
			// 若使用heartbeat, 會出現 "遠端主機已關閉一個現有連線", 
			// 若已經失去連線,則只清理資料面訊息
//			if (conn.isClosing() || conn.isClosed())
//				CommonFunction.onCloseHelper(conn, "heartbeatMissed");
//			// 若尚未失去連線,則同時清理資料並關閉連線
//			else
//				conn.close();
			
			
				
		}
		
	}
	
}