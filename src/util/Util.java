package util;
 
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;











import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import filter.SystemListener;


@Component
public class Util {
	
	
	private static AmqpTemplate template;
	
	@Autowired
	public Util(AmqpTemplate template){
		System.out.println("Util() called");
		Util.template = template;
	}	
	
	
	public static AmqpTemplate getTemplate() {
		return template;
	}

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
	public static Logger getStatusFileLogger(){
		return Attr.statusFileLogger;
	}
	public static Logger getPressureTestFileLogger(){
		return Attr.pressureTestFileLogger;
	}
	public static String getHostURLStr(String aHost){
		
		String protocol = Attr.SystemParam.get(aHost + "_protocol");
		String hostname = Attr.SystemParam.get(aHost + "_hostname");
		String port = Attr.SystemParam.get(aHost + "_port");
		
		return protocol + "//" + hostname + ":" + port;
	}
	
	public static String getProjectStr(String aHost){
		String projectName = Attr.SystemParam.get(aHost + "_project");
		return projectName;
	}
//	public static String getTmpID(String aID){
//		return aID.replaceAll( "[^\\d]", "" ).substring(0,6);
//	}
	
	public static String getWebchat_channel_name(){
		return Attr.webchat_channel_name;
	}
	
	public static String getQuery(List<AbstractMap.SimpleEntry<String,String>> params) throws UnsupportedEncodingException
	{
	    StringBuilder result = new StringBuilder();
	    boolean first = true;

	    for (AbstractMap.SimpleEntry<String,String> pair : params)
	    {
	        if (first)
	            first = false;
	        else
	            result.append("&");

	        result.append(URLEncoder.encode(pair.getKey(), "UTF-8"));
	        result.append("=");
	        result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
	    }

	    return result.toString();
	}

	
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
		private static final Logger statusFileLogger = LogManager.getLogger("util.statusFileLogger");
		private static final Logger pressureTestFileLogger = LogManager.getLogger("util.pressureTestFileLogger");
		
		private static final String webchat_channel_name = "chat";
		
	}

}
