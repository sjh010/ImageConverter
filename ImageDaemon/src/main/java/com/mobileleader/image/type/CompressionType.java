package com.mobileleader.image.type;

/**
 * ImageIO에서 관리하는 파일형식
 */
public enum CompressionType {

    NONE			(1,	 	"NONE",	 	1),
    RLE				(2,  	"JPEG", 	2),
	G3				(3,  	"JPEG2000", 3),
	G4				(4,  	"TIFF", 	4),
	LZW				(5,  	"JBIG2", 	5),
	JPEG_IN_TIFF	(7,  	"IZT", 	 	6),
	JPEG2000_IN_TIFF(34712, "GIF", 	 	7);

    private int code;

    private String description;
    
    private int order;

    private CompressionType(int code, String description, int order) {
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

	public static CompressionType getByCode(int code) {
        for (CompressionType value : CompressionType.values()) {
           if (value.code == code) {
        	   return value;
           }
        }
        return null;
    }
}
