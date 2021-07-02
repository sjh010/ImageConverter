package com.mobileleader.image.type;

/**
 * 데몬 헬스 체크 메시지
 */
public enum PingType {

    PING(1,	"PING",	1),
    PONG(2, "PONG", 2);
 

    private int code;

    private String description;
    
    private int order;

    private PingType(int code, String description, int order) {
        this.code = code;
        this.description = description;
        this.order = order;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
    
    public int getOrder() {
		return order;
	}

	public static PingType getByCode(int code) {
        for (PingType value : PingType.values()) {
           if (value.code == code) {
        	   return value;
           }
        }
        return null;
    }
}
