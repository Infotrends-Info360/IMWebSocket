package util;
 
import java.util.Map;




import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import filter.SystemListener;

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
	public static String getEstablishedStatus() {
		return Attr.SystemParam.get("EstablishedStatus");
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
	public static String getGString(JsonObject aObj, String aKey){
		return (aObj.get(aKey) != null && !(aObj.get(aKey)instanceof JsonNull))?aObj.get(aKey).getAsString():null;
	}
	public static Logger getFileLogger(){
		return Attr.fileLogger;
	}
	public static Logger getConsoleLogger(){
		return Attr.consoleLogger;
	}
	public static String getHostURLStr(String aHost){
//		systemParam.put("IMWebSocket_protocol", IMWebSocket_protocol);
//		systemParam.put("IMWebSocket_hostname", IMWebSocket_hostname);
//		systemParam.put("IMWebSocket_port", IMWebSocket_port);		
		
		String protocol = Attr.SystemParam.get(aHost + "_protocol");
		String hostname = Attr.SystemParam.get(aHost + "_hostname");
		String port = Attr.SystemParam.get(aHost + "_port");
		
		return protocol + "//" + hostname + ":" + port;
	}
//	public static String getTmpID(String aID){
//		return aID.replaceAll( "[^\\d]", "" ).substring(0,6);
//	}

	private static class Attr {
		private static final String sdfDateFormat = "yyyy-MM-dd";
		private static final String sdfTimeFormat = "HH:mm:ss";
		private static final String sdfDateTimeFormat = "yyyy-MM-dd HH:mm:ss";
		private static Map<String,String> SystemParam = null;
		private static Map<String, Map<String, String>> AgentStatus = null;
		private static Map<String, Map<String, String>> AgentReason = null;
//		private static final Logger fileLogger = Logger.getLogger("fileLogger");
		private static final Logger fileLogger = LogManager.getLogger("util.fileLogger");
//		private static final Logger consoleLogger = Logger.getLogger("consoleLogger");
		private static final Logger consoleLogger = LogManager.getLogger("util.consoleLogger");

	}

}
