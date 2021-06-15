package com.mobileleader.image.type;

/**
 * ImageIO에서 관리하는 파일형식
 */
public enum FileType {

    BMP		(1,	 "BMP", 	 1),
    JPEG	(2,  "JPEG", 	 2),
	JPEG2000(3,  "JPEG2000", 3),
	TIFF	(4,  "TIFF", 	 4),
	JBIG2	(5,  "JBIG2", 	 5),
	IZT		(6,  "IZT", 	 6),
	GIF		(7,  "GIF", 	 7),
	PNG		(8,  "PNG", 	 8),
	PDF		(11, "PDF", 	 9),
	NONE	(0, "UNKNOWN", 10);	

    private int code;

    private String description;
    
    private int order;

    private FileType(int code, String description, int order) {
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

	public static FileType getByCode(int code) {
        for (FileType value : FileType.values()) {
           if (value.code == code) {
        	   return value;
           }
        }
        return null;
    }
}
