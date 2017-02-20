package util;

public enum StatusEnum {
	LOGIN(1), LOGOUT(2), READY(3), NOT_READY(4), PAPERWORK(5), RING(6), 
	I_ESTABLISHED(7), O_ESTABLISHED(8);
	private String value;

	private StatusEnum(int aValue) {
		this.value = Integer.toString(aValue);
	};

	public String getValue() {
		return this.value;
	}
	
}
