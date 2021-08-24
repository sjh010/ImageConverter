package com.mobileleader.image.type;

/**
 * 이미지 변환 요청 타입
 */
public enum ResultExtentionType {

    PDF			("00", "PDF", 		1),
    JPG			("01", "JPG", 		2),
	TIFF		("02", "TIFF", 		3),
	BMP			("03", "BMP", 		4),
	PNG			("04", "PNG", 		5),
	TIFF_JPEG	("05", "TIFF_JPEG", 6)
	;

    private String code;

    private String description;
    
    private int order;

    private ResultExtentionType(String code, String description, int order) {
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

	public static ResultExtentionType getByCode(String code) {
        for (ResultExtentionType value : ResultExtentionType.values()) {
           if (value.code.equals(code)) {
        	   return value;
           }
        }
        return null;
    }
}
