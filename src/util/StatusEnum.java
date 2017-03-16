package util;

public enum StatusEnum {
	LOGIN(), LOGOUT(), READY(), NOTREADY(), AFTERCALLWORK(), RING(), 
	IESTABLISHED(), OESTABLISHED(), REJECT();
	private String dbid; // ex. 3
	private String description; // ex. 準備就緒
	public String getDbid() {
		return dbid;
	}
	public void setDbid(String dbid) {
		this.dbid = dbid;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public static StatusEnum getStatusEnumByDescription(String aStatusname){
		aStatusname = aStatusname.toUpperCase();
		
		if (LOGIN.toString().equals(aStatusname)){
			return LOGIN;
		}else if (LOGOUT.toString().equals(aStatusname)){
			return LOGOUT;
		}else if (READY.toString().equals(aStatusname)){
			return READY;
		}else if (NOTREADY.toString().equals(aStatusname)){
			return NOTREADY;
		}else if (AFTERCALLWORK.toString().equals(aStatusname)){
			return AFTERCALLWORK;
		}else if (RING.toString().equals(aStatusname)){
			return RING;
		}else if (IESTABLISHED.toString().equals(aStatusname)){
			return IESTABLISHED;
		}else if (OESTABLISHED.toString().equals(aStatusname)){
			return OESTABLISHED;
		}else if (REJECT.toString().equals(aStatusname)){
			return REJECT;
		}
		Util.getConsoleLogger().debug("StatusEnmu - getStatusEnumByDescription: " + " no match");
		return null;
	}
	
	public static StatusEnum getStatusEnumByDbid(String aDbid){
		
		if (LOGIN.getDbid().equals(aDbid)){
			return LOGIN;
		}else if (LOGOUT.getDbid().equals(aDbid)){
			return LOGOUT;
		}else if (READY.getDbid().equals(aDbid)){
			return READY;
		}else if (NOTREADY.getDbid().equals(aDbid)){
			return NOTREADY;
		}else if (AFTERCALLWORK.getDbid().equals(aDbid)){
			return AFTERCALLWORK;
		}else if (RING.getDbid().equals(aDbid)){
			return RING;
		}else if (IESTABLISHED.getDbid().equals(aDbid)){
			return IESTABLISHED;
		}else if (OESTABLISHED.getDbid().equals(aDbid)){
			return OESTABLISHED;
		}else if (REJECT.getDbid().equals(aDbid)){
			return REJECT;
		}
		Util.getConsoleLogger().debug("StatusEnmu - getStatusEnumByDbid: " + " no match");
		return null;
	}
	
}
