package com.mobileleader.image.type;

/**
 * 이미지 변환 요청 타입
 */
public enum JobType {

    REALTIME("R", "실시간", 0),
    BATCH("B", "배치", 1);

    private String code;

    private String description;
    
    private int order;

    private JobType(String code, String description, int order) {
        this.code = code;
        this.description = description;
        this.order = order;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
    
    public int getOrder() {
		return order;
	}

	public static JobType getByCode(String code) {
        for (JobType value : JobType.values()) {
           if (value.code.equals(code)) {
        	   return value;
           }
        }
        return null;
    }
}
