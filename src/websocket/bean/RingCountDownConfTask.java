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

public class RingCountDownConfTask extends TimerTask {
	// Timer所需屬性
	private Timer timer;
	private static long delay = 1000;
	private static long period = 1000;
//	public static Integer maxRingTime = Integer.parseInt(Util.getMaxRingTime()); // 正式用
	public static Integer maxRingTime = 7; // 測試用
	private Integer currCount = 0;
	// 讓所有物件皆牽連於特定一個UserInfo(composition)
	private UserInfo invitingAgentUserInfo;
	private UserInfo invitedAgentUserInfo;
	
	
	public RingCountDownConfTask(UserInfo aInvitingAgentUserInfo, UserInfo aInvitedAgentInfo){
		this.invitingAgentUserInfo = aInvitingAgentUserInfo;
		this.invitedAgentUserInfo = aInvitedAgentInfo;
		
		this.invitingAgentUserInfo.setStopConfRing(false);
		this.invitedAgentUserInfo.setStopConfRing(false);
		
	}
	
	@Override
	public void run() {
		if (this.invitingAgentUserInfo.isStopConfRing() || this.invitedAgentUserInfo.isStopConfRing()) {
			Util.getConsoleLogger().info("RingCountDownConfTask - "  + "CONF RING STOPPED");
			Util.getFileLogger().info("RingCountDownConfTask - "  + this.invitingAgentUserInfo.isRingEndExpected());
			Util.getFileLogger().info("RingCountDownConfTask - "  + this.invitedAgentUserInfo.isRingEndExpected());
			this.timer.cancel();
			// 若為超過時間狀況,告知雙方Agent
			JsonObject jsonTo = new JsonObject();
			if (this.invitingAgentUserInfo.isTimeoutConf()){
				jsonTo.addProperty("Event", "responseThirdParty");
				jsonTo.addProperty("response", "timeout");
				WebSocketUserPool.sendMessageToUser(WebSocketUserPool.getWebSocketByUserID(this.invitingAgentUserInfo.getUserid()), jsonTo.toString());
				WebSocketUserPool.sendMessageToUser(WebSocketUserPool.getWebSocketByUserID(this.invitedAgentUserInfo.getUserid()), jsonTo.toString());
			}
			
			return;
		}
		++currCount;
		Util.getConsoleLogger().debug("RingCountDownConfTask - " + currCount);
		Util.getFileLogger().debug("RingCountDownConfTask - " + currCount);

		if (this.currCount >= this.maxRingTime){
			Util.getConsoleLogger().debug("TimerTaskRingHeartBeat - "  + "CONF RING TIMEOUT");
			Util.getFileLogger().info("TimerTaskRingHeartBeat - "  + "CONF RING TIMEOUT");
			this.invitingAgentUserInfo.setStopConfRing(true);
			this.invitingAgentUserInfo.setTimeoutConf(true);
		}
	}
	
	public void operate(){
		this.timer = new Timer();
		Util.getConsoleLogger().info("TimerTaskRingHeartBeat - "  + "RING STARTED");
		Util.getFileLogger().info("TimerTaskRingHeartBeat - "  + "RING STARTED");		
		timer.scheduleAtFixedRate(this, RingCountDownConfTask.delay, RingCountDownConfTask.period);
		
	}

	public Integer getCurrCount() {
		return currCount;
	}
	
	
}