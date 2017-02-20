package util;
 
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
	public static String getMaxRingTime(){
		return Attr.MaxRingTime;
	}
	public static void setMaxRingTime(String maxRingTime) {
		Attr.MaxRingTime = maxRingTime;
	}
	public static String getAfterCallStatus(){
		return Attr.AfterCallStatus;
	}
	public static void setAfterCallStatus(String afterCallStatus) {
		Attr.AfterCallStatus = afterCallStatus;
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
		private static String MaxRingTime = null;
		private static String AfterCallStatus = null;
	}

}
