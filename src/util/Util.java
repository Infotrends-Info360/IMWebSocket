package util;
 
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Util {
	public static String getSdfDateFormat(){
		return Attr.sdfDateFormat;
	}
	public static String getSdfTimeFormat(){
		return Attr.sdfTimeFormat;
	}
	public static String getSdfDateTimeFormat(){
		return Attr.sdfDateTimeFormat;
	}
	public static String getMaxRingTime() {
		return Attr.SystemParam.get("MaxRingTime");
	}
	public static String getAfterCallStatus() {
		return Attr.SystemParam.get("AfterCallStatus");
	}
	public static Map<String, String> getSystemParam() {
		return Attr.SystemParam;
	}
	public static void setSystemParam(Map<String, String> systemParam) {
		Attr.SystemParam = systemParam;
	}
	public static Map<String, Map<String, String>> getAgentStatus() {
		return Attr.AgentStatus;
	}
	public static void setAgentStatus(Map<String, Map<String, String>> agentStatus) {
		Attr.AgentStatus = agentStatus;
	}
	public static Map<String, Map<String, String>> getAgentReason() {
		return Attr.AgentReason;
	}
	public static void setAgentReason(Map<String, Map<String, String>> agentReason) {
		Attr.AgentReason = agentReason;
	}
	public static JsonObject getGJsonObject(String aMsg){
		JsonParser jsonParser = new JsonParser(); 
		JsonObject msgJson = jsonParser.parse(aMsg).getAsJsonObject();
		return msgJson;
	}


	private static class Attr {
		private static final String sdfDateFormat = "yyyy-MM-dd";
		private static final String sdfTimeFormat = "HH:mm:ss";
		private static final String sdfDateTimeFormat = "yyyy-MM-dd HH:mm:ss";
		private static Map<String,String> SystemParam = null;
		private static Map<String, Map<String, String>> AgentStatus = null;
		private static Map<String, Map<String, String>> AgentReason = null;
	}

}
