package com.mobileleader.image.convert;

import java.util.HashMap;
import java.util.Map;

/**
 * 공통으로 사용되는 상수를 정의한 클래스
 */
public class UvConst {

	// 변환 실시간처리 여부
	public static class JOB_TYPE {
		public static final String REALTIME = "R";
		public static final String BATCH = "B";
	}
	
	// 변환방식
	public static class CONV_TYPE {
		public static final String ITP = "01";
		public static final String PTI = "02";
		public static final String ITI = "03";
	}
	
	// 변환결과 타입
	public static class RST_TYPE {
		public static final String PDF 	= "00";
		public static final String JPG 	= "01";
		public static final String TIFF	= "02";
		public static final String BMP 	= "03";
		public static final String PNG 	= "04";
	}
	
	// ImageIO에서 관리되는 파일형식
	public static class FILE_TYPE {
		public static final int BMP 		= 1;
		public static final int JPEG 		= 2;
		public static final int JPEG2000 	= 3;
		public static final int TIFF		= 4;
		public static final int JBIG2 		= 5;
		public static final int IZT 		= 6;
		public static final int GIF 		= 7;
		public static final int PNG 		= 8;
		public static final int PDF 		= 11;
	}
	
	// ImageIO에서 관리되는 압축형식
	public static class COMP_TYPE {
		public static final int NONE 				= 1;
		public static final int RLE 				= 2;
		public static final int G3 					= 3;
		public static final int G4 					= 4;
		public static final int LZW 				= 5;
		public static final int JPEG_IN_TIFF 		= 7;
		public static final int JPEG2000_IN_TIFF 	= 34712;
	}
	
	// Image2PDFJNI에서 PDF 생성시 사용할 파일목록 구분자
	public static final char DELIMITER = '|';
	
	// 변환시 파일명에 추가할 인덱스 생성 포맷
	public static final String INDEX_FORMAT = "_%02d";
	
	// 변환모듈에서 변환서버로 전달할 에러코드
	public static final class ERR_CD {
		public static final String SUCCESS 				= "200";
		public static final String INVALID_RST_TYPE 	= "810";
		public static final String INVALID_FILE_TYPE 	= "811";
		public static final String INVALID_FILE_NAME 	= "812";
		public static final String INVALID_PARAM 		= "813";
		public static final String INVALID_COMP_RATE 	= "814";
		public static final String FAIL_CALL_MODULE 	= "820";
		public static final String FAIL_DECODE_OR_MERGE = "821";
		public static final String FAIL_ENLARGE_REDUCT 	= "822";
		public static final String UNKNOWN 				= "830";
	}
	
	// 변환모듈 JNI에서 관리하는 에러코드 대 변환서버 에러코드 매핑정보
	public static final Map<Integer, String> MAP_IMAGE_CON_ERROR = new HashMap<Integer, String>() {
		private static final long serialVersionUID = 1L;

		{
			put(0, ERR_CD.INVALID_FILE_TYPE);
			put(-1, ERR_CD.INVALID_FILE_NAME);
			put(-2, ERR_CD.INVALID_PARAM);
			put(-3, ERR_CD.FAIL_CALL_MODULE);
			put(-4, ERR_CD.FAIL_CALL_MODULE);
			put(-5, ERR_CD.FAIL_DECODE_OR_MERGE);	// 상세로그 확인 필요
			put(-6, ERR_CD.FAIL_CALL_MODULE);
			put(-7, ERR_CD.FAIL_CALL_MODULE);
			put(-15, ERR_CD.INVALID_FILE_TYPE);
			put(-17, ERR_CD.INVALID_COMP_RATE);
			put(-96, ERR_CD.FAIL_ENLARGE_REDUCT);	// 상세로그 확인 필요
		}
	};
	
}
