//======================================================
/* Copyright (c) 1999-2015 INZISOFT Co., Ltd. All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited 
 * All information contained herein is, and remains the property of INZISOFT Co., Ltd.
 * The intellectual and technical concepts contained herein are proprietary to INZISOFT Co., Ltd.
 * Dissemination of this information or reproduction of this material is 
 * strictly forbidden unless prior written permission is obtained from INZISOFT Co., Ltd.
 * Proprietary and Confidential.
 */
//======================================================

package com.inzisoft.server.pdf;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PDFEditorJNI
{
	/* load our native library */
	static {
		try {
			System.loadLibrary("JNI_InziPDFEditor");
		} catch (Exception e) {
			log.error("library load fail.", e);
		}
		
	}

	/* The native functions */
	
	/**
	 * 하위 모듈의 경로 설정하는 함수(AIX에서사용, 다른 플랫폼은 사용 안해도 됨)
	 * 
	 * @param modulepath		[in] PDFEditor 등이 있는 패스(풀경로) 
	 * @return 1 : 성공
	 *		     그외 : 실패
	 */
	public static native int SetPELibraryPath(String modulepath);
		
	/**
	 * 로그 설정 파일 및 로그 생성 위치 설정
	 * 
	 * @param confpath		[in] 로그 설정 파일 경로
	 * @param conffilename	[in] 로그 설정 파일 이름(일반적으로 InziCIP.cfg) 
	 * @param logpath		[in] 로그 파일 생성 경로(로그가 생성될 폴더는 UNIX/LINUX(쓰기, 실행), Windows(쓰기, 읽기) 권한이 있어야 함)
	 * @return 0 : 성공
	 *		     -30000 이하 : 실패
	 */
	public static native int SetPELogPath(String confpath, String conffilename, String logpath);
	
	/**
	 * PDF 파일 생성
	 *
	 * @param pagecount		[in] 생성될 PDF의 페이지 개수
	 * @param pagewidth		[in] 생성될 PDF 페이지의 가로 길이 (A4 크기로 생성시 595)
	 * @param pageheight	[in] 생성될 PDF 페이지의 세로 길이 (A4 크기로 생성시 842)
	 * @return 0보다 큼: 성공. 문서 참조 번호
	 *		   0 이하: 실패
	 */
	public static native int createDocument(int pagecount, float pagewidth, float pageheight);
	
	/**
	 * PDF 파일 열기
	 * 
	 * @param filename	[in] PDF 파일 명
	 * @return 0보다 큼: 성공. 문서 참조 번호
	 *		   0 이하: 실패
	 */
	public static native int openDocument(String filename);

	/**
	 * 문서의 전체 페이지 수를 반환
	 * 
	 * @param docref	[in] 문서 참조 번호
	 * @return 전체 페이지 수
	 *		   0: 실패
	 */
	public static native int countPages(int docref);

	/**
	 * 페이지 폭 반환
	 * 
	 * @param docref	[in] 문서 참조 번호
	 * @param page		[in] 페이지 번호 (1부터 시작)
	 * @return 페이지의 폭
	 *		   0.0f: 실패
	 */
	public static native float getPageWid(int docref, int page);

	/**
	 * 페이지 높이 반환
	 * 
	 * @param docref	[in] 문서 참조 번호
	 * @param page		[in] 페이지 번호 (1부터 시작)
	 * @return 페이지의 높이
	 *		   0.0f: 실패
	 */
	public static native float getPageHgt(int docref, int page);

	/**
	 * 이미지에 대한 참조 생성
	 * 
	 * @param docref	[in] 문서 참조 번호
	 * @param filename	[in] 이미지 파일 명
	 * @return 0 아님: 성공. 이미지 참조 번호
	 *		   0: 실패
	 */
	public static native int createImageFromFile(int docref, String filename);

	/**
	 * 이미지 폭 리턴
	 * 
	 * @param docref	[in] 문서 참조 번호
	 * @param imageref	[in] 이미지 참조 번호
	 * @return 이미지의 폭
	 *		   0: 실패
	 */
	public static native int getImageWid(int docref, int imageref);

	/**
	 * 이미지 높이 리턴
	 * 
	 * @param docref	[in] 문서 참조 번호
	 * @param imageref	[in] 이미지 참조 번호
	 * @return 이미지의 높이
	 *		   0: 실패
	 */
	public static native int getImageHgt(int docref, int imageref);

	/**
	 * 이미지 수평 DPI 리턴
	 * 
	 * @param docref	[in] 문서 참조 번호
	 * @param imageref	[in] 이미지 참조 번호
	 * @return 이미지의 수평 DPI
	 *		   0: 실패
	 */
	public static native int getImageDpiX(int docref, int imageref);

	/**
	 * 이미지 수직 DPI 리턴
	 * 
	 * @param docref	[in] 문서 참조 번호
	 * @param imageref	[in] 이미지 참조 번호
	 * @return 이미지의 수직 DPI
	 *		   0: 실패
	 */
	public static native int getImageDpiY(int docref, int imageref);

	/**
	 * 이미지 추가
	 * 
	 * @param docref	[in] 문서 참조 번호
	 * @param page		[in] 페이지 번호 (1부터 시작)
	 * @param imageref	[in] 이미지 참조 번호
	 * @param xpos		[in] 이미지 삽입 X 위치
	 * @param ypos		[in] 이미지 삽입 Y 위치
	 * @param hsize		[in] 이미지 폭
	 * @param vsize		[in] 이미지 높이
	 * @return 1: 성공
	 *		   0 이하: 실패
	 */
	public static native int addImage(int docref, int page, int imageref,
					float xpos, float ypos, float hsize, float vsize);

	/**
	 * 텍스트 추가
	 * 
	 * @param docref		[in] 문서 참조 번호
	 * @param page			[in] 페이지 번호 (1부터 시작)
	 * @param text			[in] 텍스트 내용
	 *                             - 멀티라인 텍스트 출력 가능. 줄 구분 기호는 LF(Line Feed: 0x0A, \n)이어야 함.
	 * @param xpos			[in] 텍스트 영역 X 위치
	 * @param ypos			[in] 텍스트 영역 Y 위치
	 * @param hsize			[in] 텍스트 영역 폭
	 * @param vsize			[in] 텍스트 영역 높이
	 * @param halign		[in] 수평 정렬 (1: 왼쪽, 2: 가운데, 3: 오른쪽)
	 * @param valign		[in] 수직 정렬 (1: 위쪽, 2: 가운데, 3: 아래쪽)
	 * @param fontmode      [in] 폰트 지정 방식
	 *                             - 1: 폰트 파일 지정. 폰트 임베딩
	 *                             - 2: 폰트 파일 지정. 폰트 임베딩하지 않음.
	 *                             - 3: 영문 기본 폰트 지정. 폰트 임베딩하지 않음.
	 *                             - 4: 한글 기본 폰트 지정. 폰트 임베딩하지 않음.
	 * @param fontfullpath	[in] 폰트 파일 경로 및 이름
	 *                             - fontmode = 1 또는 2: ttc, ttf 등의 TrueType 폰트 파일이어야 함.
	 *                             - fontmode = 3: 영문 폰트 이름. 다음 중 하나
	 *                                                         "Courier", "Courier-Bold", "Courier-Oblique", "Courier-BoldOblique"
	 *                                                         "Helvetica", "Helvetica-Bold", "Helvetica-Oblique", "Helvetica-BoldOblique"
	 *                                                         "Times-Roman", "Times-Bold", "Times-Italic", "Times-BoldItalic"
	 *                                                         "Symbol", "ZapfDingbats"
	 *                             - fontmode = 4: 한글 폰트 이름. 다음 중 하나
	 *                                                         "DotumChe", "DotumChe,Bold", "DotumChe,Italic", "DotumChe,BoldItalic"
	 *                                                         "Dotum", "Dotum,Bold", "Dotum,Italic", "Dotum,BoldItalic"
	 *                                                         "BatangChe", "BatangChe,Bold", "BatangChe,Italic", "BatangChe,BoldItalic"
	 *                                                         "Batang", "Batang,Bold", "Batang,Italic", "Batang,BoldItalic"
	 * @param fontindex		[in] 폰트 파일 내의 폰트 인덱스
	 *                             - fontmode = 1 또는 2: ttc 파일의 경우에는 0 이상의 값으로, ttf 파일의 경우에는 음수로 지정해야 함.
	 *                             - fontmode = 3 또는 4: 의미 없음.
	 * @param fontsize		[in] 텍스트 크기 ( ~ 300)
	 * @param charspacing	[in] 텍스트 자간 (-30 ~ 300: 기준값은 0)
	 * @param horizscaling	[in] 텍스트 장평 (10 ~ 300: 기준값은 100)
	 * @param colorR		[in] 컬러 red   (0.0 ~ 1.0)
	 * @param colorG		[in] 컬러 green (0.0 ~ 1.0)
	 * @param colorB		[in] 컬러 blue  (0.0 ~ 1.0)
	 * @param alpha			[in] 알파값     (0.0 ~ 1.0: 1.0이 불투명)
	 * @return 1: 성공
	 *		   그 외: 실패
	 */
	public static native int addText(int docref, int page, String text,
					float xpos, float ypos, float hsize, float vsize,
					int halign, int valign,
					int fontmode, String fontfullpath, int fontindex, float fontsize,
					float charspacing, float horizscaling,
					float colorR, float colorG, float colorB, float alpha);

	/**
	 * 텍스트 추가 UTF8 추가 가능(addText 참조)
	 * 
	 * @param textencoding		[in] 텍스트 인코딩 (1 : EUC-KR, 2 : UTF8)
	 */
	
	
	public static native int addTextEx(int docref, int page, String text, int textencoding,
					float xpos, float ypos, float hsize, float vsize,
					int halign, int valign,
					int fontmode, String fontfullpath, int fontindex, float fontsize,
					float charspacing, float horizscaling,
					float colorR, float colorG, float colorB, float alpha);

	public static float STROKE_END = -1000.0f;
	
	/**
	 * 펜 데이터 추가
	 * 
	 * @param docref		[in] 문서 참조 번호
	 * @param page			[in] 페이지 번호 (1부터 시작)
	 * @param points		[in] 펜 데이터
	 *                             - 점의 좌표와 선 굵기의 배열임. X좌표, Y좌표, 굵기, X좌표, Y좌표, 굵기, … 의 순서로 나열됨.
	 *                             - 선의 굵기는 thickmode에 따라 다르게 반영됨.
	 *                                           thickmode=1인 경우, 스트로크의 첫번째 점의 굵기 값만 사용됨. 나머지 굵기 값은 무시됨.
	 *                                           thickmode=2인 경우, 각 선의 굵기는 시작점의 굵기 값이 적용됨. 마지막 점의 굵기 값은 무시됨.
	 *                             - 각 스트로크의 끝은 STROKE_END로 표시함.
	 *                             - 맨 끝에는 스트로크의 끝 표시가 있어야 함.
	 *                             - points의 length는 점의 개수가 아니라 좌표의 개수이고, 3의 배수이어야 함.
	 *                                           예) 스트로크 개수는 2개, 스트로크의 점 개수는 각각 3개, 2개인 경우:
	 *                                                         points[0] = 10.0f, point[1] = 10.0f, point[2] = 2.0f
	 *                                                         points[3] = 20.0f, point[4] = 10.0f, point[5] = 1.5f
	 *                                                         points[6] = 20.0f, point[7] = 20.0f, point[8] = 0.0f
	 *                                                         points[9] = STROKE_END, point[10] = STROKE_END, point[11] = STROKE_END
	 *                                                         points[12] = 30.0f, point[13] = 10.0f, point[14] = 1.0f
	 *                                                         points[15] = 30.0f, point[16] = 20.0f, point[17] = 0.0f
	 *                                                         points[18] = STROKE_END, point[19] = STROKE_END, point[20]=STROKE_END
	 * @param thickmode		[in] 굵기 지정 모드
	 *                             - 1: 고정 굵기 사용. 각 스트로크의 첫번째 선의 굵기가 그 스트로크의 전체 굵기로 반영됨.
	 *                             - 2: 가변 굵기 사용. 각 선의 굵기가 반영됨.
	 * @param colorR		[in] 컬러 red   (0.0 ~ 1.0)
	 * @param colorG		[in] 컬러 green (0.0 ~ 1.0)
	 * @param colorB		[in] 컬러 blue  (0.0 ~ 1.0)
	 * @param alpha			[in] 알파값     (0.0 ~ 1.0: 1.0이 불투명)
	 * @return 1: 성공
	 *		   0 이하: 실패
	 */
	public static native int addPen(int docref, int page,
			float[] points, int thickmode,
			float colorR, float colorG, float colorB, float alpha);

	/**
	 * PDF 마지막에 공백 페이지 추가(페이지의 가로 세로는 이전 페이지값 사용)
	 *
	 * @param docref			[in] 문서 참조 번호
	 * @param pagecount			[in] 추가할 페이지 개수
	 *
	 * @return 1: 성공
	 *         그외 : 실패
	 **/
	public static native int appendBlankPages(int docref, int pagecount);

	/**
	 * PDF 특정 위치에 공백 페이지 추가
	 *
	 * @param docref		[in] 문서 참조 번호
	 * @param page			[in] 추가할 위치의 페이지 번호 (1부터 시작)
	 * @param pagecount		[in] 추가할 페이지 개수
	 * @param pagewidth		[in] 추가할 페이지의 가로 길이 (A4 크기로 생성시 595)
	 * @param pageheight	[in] 추가할 페이지의 세로 길이 (A4 크기로 생성시 842)
	 *
	 * @return 1: 성공
	 *         그외 : 실패
	 **/
	public static native int insertBlankPages(int docref, int page, int pagecount, float pagewidth, float pageheight);
			
			
	
	/**
	 * PDF 파일 저장
	 * 
	 * @param docref	[in] 문서 참조 번호
	 * @param filename	[in] PDF 파일 명
	 * @return 1: 성공
	 *		   그외: 실패
	 */
	public static native int saveDocument(int docref, String filename);

	/**
	 * PDF 파일 닫기
	 * 
	 * @param docref	[in] 문서 참조 번호
	 */
	public static native void closeDocument(int docref);

	/**
	 * 서버 라이선스 파일의 경로 지정
	 * 
	 * @param filename			[in] 라이선스 파일의 절대 경로
	 * @return 1: 성공
	 *		   그외: 실패
	 */
	public static native int loadLicenseFile(String filename);
	
	/**
	 * 2개의 PDF를 하나의 PDF로 병합하는 함수(단독으로 사용됨)
	 * 
	 * @param filename1	        [in] 병합할 PDF 파일명
	 * @param filename2	        [in] 병합할 PDF 파일명
	 * @param mergedfilename	[in] 병합된 PDF 파일명
	 * @return 1: 성공
				0 이하: 실패
	 */
	public static native int mergePDF(String filename1, String filename2, String mergedfilename);

    /**
	 * 다수의 PDF를 하나의 PDF로 병합하는 함수(단독으로 사용됨)
	 * 
	 * @param filenames	        [in] 병합할 PDF 파일들 이름, ";"로 구분(PDF 파일 개수는 최대 20개)
	 * @param mergedfilename	[in] 병합된 PDF 파일명
	 * @return 1: 성공
				0 이하: 실패
	 */
	public static native int mergeMultiPDF(String filenames, String mergedfilename);

	public PDFEditorJNI()
	{
	}
}

