package websocket.bean;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.Level;
import org.java_websocket.WebSocket;

import com.google.gson.JsonObject;

import util.StatusEnum;
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
	public static Integer maxRingTime = Integer.parseInt(Util.getMaxRingTime()); // 正式用
//	public static Integer maxRingTime = 10; // 測試用
	private Integer currCount = 0;
	// RING status所需屬性
	private WebSocket clientConn;
	private String clientID;
	private String ring_dbid;
	// 讓所有物件皆牽連於特定一個UserInfo(composition)
	private UserInfo agentUserInfo;
	
	public RingCountDownTask(WebSocket aConn, String aRing_dbid, UserInfo aAgentUserInfo){
		this.clientConn = aConn;
		this.ring_dbid = aRing_dbid;
		this.agentUserInfo = aAgentUserInfo;
		
		clientID = WebSocketUserPool.getUserID(aConn);
	}
	
	@Override
	public void run() {
		if (this.agentUserInfo.isStopRing()) {
			Util.getConsoleLogger().info("TimerTaskRingHeartBeat - "  + "RING STOPPED");
			Util.getFileLogger().info("TimerTaskRingHeartBeat - "  + "RING STOPPED");
			this.timer.cancel();
			// 若為超過時間狀況,告知Client,此通通話已經超過等待時間,請繼續找下一位Agent
			if (this.agentUserInfo.getTimeout()){
				JsonObject jsonTo = new JsonObject();
				jsonTo.addProperty("Event", "ringTimeout");
				jsonTo.addProperty("clientID", WebSocketUserPool.getUserID(this.clientConn));
				jsonTo.addProperty(SystemInfo.TAG_SYS_MSG, SystemInfo.getCancelLedReqMsg()); // 增加系統訊息
				WebSocketUserPool.sendMessageToUser(this.clientConn, jsonTo.toString());
				WebSocketUserPool.sendMessageToUser(WebSocketUserPool.getWebSocketByUserID(this.agentUserInfo.getUserid()), jsonTo.toString());
			}
			
			// 寫入DB
			Util.getStatusFileLogger().info("###### [RingCountDownTask] stopped ######");
			Util.getStatusFileLogger().info("updateStatus: " + "end" + " - " + StatusEnum.RING + " - " + agentUserInfo.getUsername());

			Util.getStatusFileLogger().info("" + StatusEnum.RING + ": ");
			Util.getStatusFileLogger().printf(Level.INFO,"%10s	%10s %10s %10s %10s %10s" , "status", "startORend", "dbid", "roomID", "clientID", "reason");
			Util.getStatusFileLogger().info("----------------------------------------------------------------------------");
			Util.getStatusFileLogger().printf(Level.INFO,"%10s	%10s %10s %10s %10s %10s" , StatusEnum.RING.getDbid(), "end" , this.ring_dbid, null, this.clientID, null);
			
			AgentFunction.RecordStatusEnd(this.ring_dbid);
			
			return;
		}
		++currCount;
		Util.getConsoleLogger().debug("TimerTaskRingHeartBeat - " + currCount);
		Util.getFileLogger().debug("TimerTaskRingHeartBeat - " + currCount);
//		if (currCount == 5){
//			Util.getConsoleLogger().debug("here");
//			agentUserInfo.setStopRing(true);
//		}
		if (this.currCount >= this.maxRingTime){
			Util.getConsoleLogger().debug("TimerTaskRingHeartBeat - "  + "RING TIMEOUT");
			Util.getFileLogger().info("TimerTaskRingHeartBeat - "  + "RING TIMEOUT");
			this.agentUserInfo.setStopRing(true);
			this.agentUserInfo.setTimeout(true);
		}
	}
	
	public void operate(){
		this.timer = new Timer();
		Util.getConsoleLogger().info("TimerTaskRingHeartBeat - "  + "RING STARTED");
		Util.getFileLogger().info("TimerTaskRingHeartBeat - "  + "RING STARTED");		
		timer.scheduleAtFixedRate(this, RingCountDownTask.delay, RingCountDownTask.period);
		
	}

	public Integer getCurrCount() {
		return currCount;
	}
	
	
}