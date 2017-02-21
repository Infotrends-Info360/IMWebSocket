package util;

public enum StatusEnum {
	LOGIN(), LOGOUT(), READY(), NOTREADY(), PAPERWORK(), RING(), 
	IESTABLISHED(), OESTABLISHED();
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

	public static StatusEnum getStatusEnum(String aStatusname){
		aStatusname = aStatusname.toUpperCase();
		
		if (LOGIN.toString().equals(aStatusname)){
			return LOGIN;
		}else if (LOGOUT.toString().equals(aStatusname)){
			return LOGOUT;
		}else if (READY.toString().equals(aStatusname)){
			return READY;
		}else if (NOTREADY.toString().equals(aStatusname)){
			return NOTREADY;
		}else if (PAPERWORK.toString().equals(aStatusname)){
			return PAPERWORK;
		}else if (RING.toString().equals(aStatusname)){
			return RING;
		}else if (IESTABLISHED.toString().equals(aStatusname)){
			return IESTABLISHED;
		}else if (OESTABLISHED.toString().equals(aStatusname)){
			return OESTABLISHED;
		}
		System.out.println("StatusEnmu - getStatusEnum: " + " no match");
		return null;
	}
	
}
