package websocket.bean;

import java.util.Timer;
import java.util.TimerTask;

import org.java_websocket.WebSocket;

import com.google.gson.JsonObject;

import util.Util;
import websocket.function.AgentFunction;
import websocket.pools.WebSocketUserPool;

/*
 *  重要架構: 此類別以composition的方式連接一個UserInfo。
 *  若要終止此timertask,則只要原UserInfo呼叫其.setStopRing(true),
 *  則所有與此UserInfo相連之RingCountDownTask都會一併終止
 */

public class RingCountDownTask extends TimerTask {
	// Timer所需屬性
	private Timer timer;
	private static long delay = 1000;
	private static long period = 1000;
//	public static Integer maxRingTime = Integer.parseInt(Util.getMaxRingTime()); // 正式用
	public static Integer maxRingTime = 20; // 測試用
	private Integer currCount = 0;
	// RING status所需屬性
	private WebSocket clientConn;
	private String ring_dbid;
	// 讓所有物件皆牽連於特定一個UserInfo(composition)
	private UserInfo agentUserInfo;
	
	public RingCountDownTask(WebSocket aConn, String aRing_dbid, UserInfo aAgentUserInfo){
		this.clientConn = aConn;
		this.ring_dbid = aRing_dbid;
		this.agentUserInfo = aAgentUserInfo;
	}
	
	@Override
	public void run() {
		if (this.agentUserInfo.isStopRing()) {
			System.out.println("TimerTaskRingHeartBeat - "  + "RING STOPPED");
			this.timer.cancel();
			// 若為超過時間狀況,告知Client,此通通話已經超過等待時間,請繼續找下一位Agent
			if (this.agentUserInfo.getTimeout()){
				JsonObject jsonTo = new JsonObject();
				jsonTo.addProperty("Event", "ringTimeout");
				jsonTo.addProperty("clientID", WebSocketUserPool.getUserID(this.clientConn));
				WebSocketUserPool.sendMessageToUser(this.clientConn, jsonTo.toString());
				WebSocketUserPool.sendMessageToUser(WebSocketUserPool.getWebSocketByUser(this.agentUserInfo.getUserid()), jsonTo.toString());
			}
			
			// 寫入DB
			AgentFunction.RecordStatusEnd(this.ring_dbid);
			
			return;
		}
		
		System.out.println("TimerTaskRingHeartBeat - " + ++currCount);
//		if (currCount == 5){
//			System.out.println("here");
//			agentUserInfo.setStopRing(true);
//		}
		if (this.currCount >= this.maxRingTime){
			System.out.println("TimerTaskRingHeartBeat - "  + "RING TIMEOUT");
			this.agentUserInfo.setStopRing(true);
			this.agentUserInfo.setTimeout(true);
		}
	}
	
	public void operate(){
		this.timer = new Timer("some name");
		timer.scheduleAtFixedRate(this, RingCountDownTask.delay, RingCountDownTask.period);
		
	}

	public Integer getCurrCount() {
		return currCount;
	}
	
	
}