package websocket.bean;

import java.text.SimpleDateFormat;

import util.Util;

public class SystemInfo {
	public static final String TAG_SYS_MSG = "chatRoomMsg";
	
	public static final String SYS_NAME = "系統通知:";
	public static final String WELCOME_MSG = "歡迎使用玉O客服系統";
	public static final String LOOKING_FOR_AGENT_MSG = "正在為您尋找客服人員...";
	public static final String WAITING_FOR_AGENT_MSG = "正在為您接通客服人員";
	public static final String CANCELLED_REQ_MSG = "客服忙線中,正在為您尋找其他客服人員...";
	public static final String JOINED_ROOM_MSG = "進入聊天視窗";
	public static final String LEFT_ROOM_MSG = "離開聊天視窗";
	public static final String CLOSED_ROOM_MSG = "關閉聊天視窗";
	
	public static String getLoginMsg(){
		return getFormattedMsg(WELCOME_MSG) + "<br>" + getFormattedMsg(LOOKING_FOR_AGENT_MSG);
	}
	
	public static String getWelcomeMsg() {
		return getFormattedMsg(WELCOME_MSG);
	}
	public static String getLookingForAgentMsg() {
		return getFormattedMsg(LOOKING_FOR_AGENT_MSG);
	}
	public static String getWaitingForAgentMsg(String aAgentName) {
		return getFormattedMsg(WAITING_FOR_AGENT_MSG + " " + "<b>" + aAgentName + "</b>");
	}
	public static String getCancelLedReqMsg() {
		return getFormattedMsg(CANCELLED_REQ_MSG);
	}
	public static String getJoinedRoomMsg(String aUserName) {
		return getFormattedMsg(aUserName + " " + JOINED_ROOM_MSG);
	}
	public static String getLeftRoomMsg(String aUserName) {
		return getFormattedMsg(aUserName + " " + LEFT_ROOM_MSG);
	}
	public static String getClosedRoomMsg(String aUserName) {
		return getFormattedMsg(aUserName + " " + CLOSED_ROOM_MSG);
	}
	
	private static String getFormattedMsgDate(String aMsg){
		String tmpMsg = getFormattedMsg(aMsg);
//		tmpMsg = tmpMsg.substring(1, tmpMsg.length()-1);
		
		SimpleDateFormat sdf = new SimpleDateFormat(Util.getSdfTimeFormat());
		String currTime = sdf.format(new java.util.Date());
		return String.format("%s %s", currTime, tmpMsg);
	}
	
	private static String getFormattedMsg(String aMsg){
		return String.format("%s %s", SYS_NAME, aMsg);
	}
	
}
