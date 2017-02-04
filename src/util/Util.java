package util;

public class Util {
	public static String getSdfTimeFormat(){
		return Attr.sdfTimeFormat;
	}

	private static class Attr {
		private static final String sdfTimeFormat = "HH:mm:ss";
	}

}
