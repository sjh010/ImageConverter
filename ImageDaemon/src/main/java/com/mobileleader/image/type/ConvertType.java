package com.mobileleader.image.type;

/**
 * 이미지 변환 요청 타입
 */
public enum ConvertType {

    IMAGE_TO_PDF("01", "Image -> PDF", 1),
    PDF_TO_IMAGE("02", "PDF -> Image", 2),
	IMAGE_TO_IMAGE("03", "Image -> Image", 3);

    private String code;

    private String description;
    
    private int order;

    private ConvertType(String code, String description, int order) {
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

	public static ConvertType getByCode(String code) {
        for (ConvertType value : ConvertType.values()) {
           if (value.code.equals(code)) {
        	   return value;
           }
        }
        return null;
    }
}
