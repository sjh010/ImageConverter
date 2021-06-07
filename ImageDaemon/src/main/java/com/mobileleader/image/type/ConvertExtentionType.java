package com.mobileleader.image.type;

/**
 * 이미지 변환 요청 타입
 */
public enum ConvertExtentionType {

    PDF	("00", "Convert PDF", 1),
    JPG	("01", "Convert JPG", 2),
	TIFF("02", "Convert TIFF", 3),
	BMP	("03", "Convert BMP", 4),
	PNG	("04", "Convert PNG", 5);

    private String code;

    private String description;
    
    private int order;

    private ConvertExtentionType(String code, String description, int order) {
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

	public static ConvertExtentionType getByCode(String code) {
        for (ConvertExtentionType value : ConvertExtentionType.values()) {
           if (value.code.equals(code)) {
        	   return value;
           }
        }
        return null;
    }
}
