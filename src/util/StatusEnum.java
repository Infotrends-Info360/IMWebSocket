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
		}else if (PAPERWORK.toString().equals(aStatusname)){
			return PAPERWORK;
		}else if (RING.toString().equals(aStatusname)){
			return RING;
		}else if (IESTABLISHED.toString().equals(aStatusname)){
			return IESTABLISHED;
		}else if (OESTABLISHED.toString().equals(aStatusname)){
			return OESTABLISHED;
		}
		System.out.println("StatusEnmu - getStatusEnumByDescription: " + " no match");
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
		}else if (PAPERWORK.getDbid().equals(aDbid)){
			return PAPERWORK;
		}else if (RING.getDbid().equals(aDbid)){
			return RING;
		}else if (IESTABLISHED.getDbid().equals(aDbid)){
			return IESTABLISHED;
		}else if (OESTABLISHED.getDbid().equals(aDbid)){
			return OESTABLISHED;
		}
		System.out.println("StatusEnmu - getStatusEnumByDbid: " + " no match");
		return null;
	}
	
}
