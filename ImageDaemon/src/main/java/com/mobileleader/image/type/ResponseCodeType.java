package com.mobileleader.image.type;

import java.util.HashMap;
import java.util.Map;

/**
 * 이미지 변환 요청 타입
 */
public enum ResponseCodeType {

    SUCEESS				("200", "Success", 							1),
    INVALID_RST_TYPE	("810", "Invalid convert extention type", 	2),
	INVALID_FILE_TYPE	("811", "Invalid image file type", 			3),
	INVALID_FILE_NAME	("812", "Invalid file name", 				4),
	INVALID_PARAM		("813", "Invalid parameter", 				5),
	INVALID_COMP_RATE	("814", "Invalid compression rate", 		6),
	FAIL_CALL_MODULE	("820", "Fail to call module", 				7),
	FAIL_DECODE_OR_MERGE("821", "File to decode or merge", 			8),
	FAIL_ENLARGE_REDUCT	("822", "Fail to enlarge reduct", 			8),
	UNKNOWN				("830", "Unknown", 							9),
	SHUTDOWN_DAEMON		("831", "Convert daemon is shutdowned",		10);

    private String code;

    private String description;
    
    private int mappingCode;

    // 변환모듈 JNI에서 관리하는 에러코드 대 변환서버 에러코드 매핑정보
 	public static final Map<Integer, String> MAP_IMAGE_CON_ERROR = new HashMap<Integer, String>() {
 		private static final long serialVersionUID = 1L;

 		{
 			put(0, 		ResponseCodeType.INVALID_FILE_TYPE.getCode());
 			put(-1, 	ResponseCodeType.INVALID_FILE_NAME.getCode());
 			put(-2, 	ResponseCodeType.INVALID_PARAM.getCode());
 			put(-3, 	ResponseCodeType.FAIL_CALL_MODULE.getCode());
 			put(-4, 	ResponseCodeType.FAIL_CALL_MODULE.getCode());
 			put(-5, 	ResponseCodeType.FAIL_DECODE_OR_MERGE.getCode());	// 상세로그 확인 필요
 			put(-6, 	ResponseCodeType.FAIL_CALL_MODULE.getCode());
 			put(-7, 	ResponseCodeType.FAIL_CALL_MODULE.getCode());
 			put(-15, 	ResponseCodeType.INVALID_FILE_TYPE.getCode());
 			put(-17, 	ResponseCodeType.INVALID_COMP_RATE.getCode());
 			put(-96, 	ResponseCodeType.FAIL_ENLARGE_REDUCT.getCode());	// 상세로그 확인 필요
 		}
 	};
    
    private ResponseCodeType(String code, String description, int mappingCode) {
        this.code = code;
        this.description = description;
        this.mappingCode = mappingCode;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
    
    public int getMappingCode() {
		return mappingCode;
	}

	public static ResponseCodeType getByCode(String code) {
        for (ResponseCodeType value : ResponseCodeType.values()) {
           if (value.code.equals(code)) {
        	   return value;
           }
        }
        return null;
    }
}
