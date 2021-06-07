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
public class Image2PDFJNI
{
	
	/* load our native library */
	static {
		// 설치할 경로에 맞게 변경 필요
		try {
			System.loadLibrary("JNI_InziPDFWriter");
		} catch (Exception e) {
			log.error("library load fail.", e);
		}
	}

	/* The native functions */
	
	/**
	 * 하위 모듈의 경로 설정하는 함수(AIX에서사용, 다른 플랫폼은 사용 안해도 됨)
	 * 
	 * @param modulepath		[in] PDFWriter, 코덱 등이 있는 패스(풀경로) 
	 * @return 1 : 성공
	 *		     그외 : 실패
	 */
	public static native int SetPWLibraryPath(String modulepath);
	
	/**
	 * 로그 설정 파일 및 로그 생성 위치 설정
	 * 
	 * @param confpath		[in] 로그 설정 파일 경로
	 * @param conffilename	[in] 로그 설정 파일 이름(일반적으로 InziCIP.cfg) 
	 * @param logpath		[in] 로그 파일 생성 경로(로그가 생성될 폴더는 UNIX/LINUX(쓰기, 실행), Windows(쓰기, 읽기) 권한이 있어야 함)
	 * @return 0 : 성공
	 *		     -30000 이하 : 실패
	 */
	public static native int SetPWLogPath(String confpath, String conffilename, String logpath);
	
	/**
	 * 이미지 파일을 PDF로 변환
	 * 
	 * @param jsingleTIFFList		[in] 이미지 파일 리스트, 파일 이름에 delimiter 포함 x
	 * 										* multi-strip TIFF 이미지는 지원 불가
	 * 										* 한 개 이미지 변환 시에도 반드시 구분자가 있어야함
	 * @param delimiter				[in] 이미지 파일 리스트 구분자
	 * @param jauthor				[in] PDF파일 속성의 작성자에 들어갈 문자열
	 * @param jcreator				[in] PDF파일 속성의 작성 프로그램에 들어갈 문자열
	 * @param jtitle				[in] PDF파일 속성의 제목에 들어갈 문자열
	 * @param jsubject				[in] PDF파일 속성의 주제에 들어갈 문자열
	 * @param jkeywords				[in] PDF파일 속성의 키워드에 들어갈 문자열
	 * @param juserPasswd			[in] 생성된 PDF파일의 암호, 미사용 시 ""처리
	 * @param permission			[in] 생성된 PDF파일의 권한 설정 (|으로 중복지정 가능)
	 * 										0: 읽기만 허용
	 * 										4: 인쇄 허용
	 * 										8: 주석 달기, 양식 필드 채우기 이외의 모든 편집 사용
	 * 										16: 내용 복사 허용
	 * 										32: 주석 달기, 양식 필드 채우기 허용
	 * @param pageSize				[in] 페이지 크기 설정
	 * 										0: LETTER - 8.5 x 11 (inches), 612 x 792 (pixel)
	 *										1: LEGAL - 8.5 x 14 (inches), 612 x 1008 (pixel)
	 *										2: A3 - 297 x 420 (mm), 841.89 x 1199.551 (pixel)
	 *										3: A4 - 210 x 297 (mm), 595.276 x 841.89 (pixel)
	 *										4: A5 - 148 x 210 (mm), 419.528 x 595.276 (pixel)
	 *										5: B4 - 250 x 353 (mm), 708.661 x 1000.63 (pixel)
	 *										6: B5 - 176 x 250 (mm), 498.898 x 708.661 (pixel)
	 *										7: EXECUTIVE - 7.25 x 10.5 (inches), 522 x 756 (pixel)
	 *										8: US4x6 - 4 x 6 (inches), 288 x 432 (pixel)
	 *										9: US4x8 - 4 x 8 (inches), 288 x 576 (pixel)
	 *										10: US5x7 - 5 x 7 (inches), 360 x 504 (pixel)
	 *										11: COMM10 - 4.125 x 9.5 (inches), 297 x 684 (pixel)
	 *										* 특정 크기로 설정하기 위해서는  pageSize를 음수로 설정하고, pageWidth, pageHeight에 값 설정
	 * @param pageDirection			[in] 페이지 방향 설정
	 * 										0: 세로
	 * 										1: 가로
	 * @param pageWidth				[in] 페이지의 가로 길이 설정
	 * 										* 길이 관련 참고사항
	 * 											- 픽셀 단위
	 * 											- 범위: 3 ~ 14400
	 * 											- pageSize가 음수 일 때만 적용
	 * @param pageHeight			[in] 페이지의 세로 길이 설정, pageWidth의 길이 관련 참고사항 참조
	 * @param jwmText				[in] 텍스트 워터마크로 삽입 할 문자열
	 * @param wmTextPosX			[in] 텍스트 워터마크 문자열 영역이 위치할 X 좌표, 좌하단 기준(0.0: 좌측)
	 * @param wmTextPosY			[in] 텍스트 워터마크 문자열 영역이 위치할 Y 좌표, 좌하단 기준(0.0: 하단)
	 * @param jwmTextFontFullPath	[in] 텍스트 워터마크 문자열에 적용할 폰트 파일의 경로
	 * 										* ttc, ttf등의 TrueType 폰트만 가능, 폰트 중 일부가 PDF파일에 임베딩
	 * @param wmTextFontIndex		[in] 텍스트 워터마크 문자열에 적용할 폰트 파일의 폰트 인덱스
	 *										ttc : 0 이상으로 지정
	 *										ttf : 음수로 지정
	 * @param wmTextFontSize		[in] 텍스트 워터마크 문자열에 적용할 폰트 크기, 최대값: 300
	 * @param jwmImageFileFullPath	[in] 이미지 워터마크로 사용 될 이미지 파일의 경로, Alpha 채널이 포함된 PNG 파일만 가능
	 * @param wmImagePosX			[in] 사용하지 않음
	 * @param wmImagePosY			[in] 사용하지 않음
	 * @param jfilenameDst			[in] 생성될 PDF 파일 이름
	 * @param pdfa      			[in] PDFA 생성 여부(1: PDFA, 그외: PDF, convertImage2PDFUTF8함수만 해당)
	 * @return 0: 성공
	 *		   그외: 실패
	 */
	public static native int convertImage2PDF(String jsingleTIFFList, int delimiter,
							String jauthor, String jcreator, String jtitle, String jsubject, String jkeywords,
							String juserPasswd, int permission,
							int pageSize, int pageDirection, float pageWidth, float pageHeight,
							String jwmText, int wmTextPosX, int wmTextPosY, String jwmTextFontFullPath, int wmTextFontIndex, int wmTextFontSize,
							String jwmImageFileFullPath, int wmImagePosX, int wmImagePosY,
							String jfilenameDst);
							
	public static native int convertImage2PDFUTF8(String jsingleTIFFList, int delimiter,
							String jauthor, String jcreator, String jtitle, String jsubject, String jkeywords,
							String juserPasswd, int permission,
							int pageSize, int pageDirection, float pageWidth, float pageHeight,
							String jwmText, int wmTextPosX, int wmTextPosY, String jwmTextFontFullPath, int wmTextFontIndex, int wmTextFontSize,
							String jwmImageFileFullPath, int wmImagePosX, int wmImagePosY,
							String jfilenameDst, int pdfa);
	
	/**
	 * 이미지 파일을 PDF로 변환
	 * 변환 과정에서 보안 요소를 배제하는 함수
	 * 파라미터는 convertImage2PDF 함수 참조
	 */
	public static native int convertImage2PDF_NS(String jsingleTIFFList, int delimiter,
							String jauthor, String jcreator, String jtitle, String jsubject, String jkeywords,							
							int pageSize, int pageDirection, float pageWidth, float pageHeight,
							String jwmText, int wmTextPosX, int wmTextPosY, String jwmTextFontFullPath, int wmTextFontIndex, int wmTextFontSize,
							String jwmImageFileFullPath, int wmImagePosX, int wmImagePosY,
							String jfilenameDst);
	
	/**
	 * 이미지 파일을 PDF로 변환
	 * 부가적인 기능 제외, 변환 기능만 제공하는 함수
	 * 파라미터는 convertImage2PDF 함수 참조
	 */	
	public static native int convertImage2PDF_LE(String jsingleTIFFList, int delimiter,							
							int pageSize, int pageDirection, float pageWidth, float pageHeight,
							String jfilenameDst);

	/**
	 * 이미지 파일을 PDF로 변환
	 * 
	 * @param jsingleTIFFList		[in] 이미지 파일 리스트, 파일 이름에 delimiter 포함 x, 상세 내용은 convertImage2PDF 참조
	 * @param delimiter				[in] 이미지 파일 리스트 구분자
	 * @param jauthor				[in] PDF파일 속성의 작성자에 들어갈 문자열
	 * @param jcreator				[in] PDF파일 속성의 작성 프로그램에 들어갈 문자열
	 * @param jtitle				[in] PDF파일 속성의 제목에 들어갈 문자열
	 * @param jsubject				[in] PDF파일 속성의 주제에 들어갈 문자열
	 * @param jkeywords				[in] PDF파일 속성의 키워드에 들어갈 문자열
	 * @param juserPasswd			[in] 생성된 PDF파일의 암호, 미사용 시 ""처리
	 * @param permission			[in] 생성된 PDF파일의 권한 설정, 상세 내용은 convertImage2PDF 참조
	 * @param pageSize				[in] 페이지 크기 설정, 상세 내용은 convertImage2PDF 참조
	 * @param pageDirection			[in] 페이지 방향 설정, 상세 내용은 convertImage2PDF 참조
	 * @param pageWidth				[in] 페이지의 가로 길이 설정, 상세 내용은 convertImage2PDF 참조
	 * @param pageHeight			[in] 페이지의 세로 길이 설정, 상세 내용은 convertImage2PDF 참조
	 * @param jwmText				[in] 텍스트 워터마크로 삽입 할 문자열
	 * @param wmTextPosX			[in] 텍스트 워터마크 문자열 영역이 위치할 X 좌표, 좌하단 기준(0.0: 좌측)
	 * @param wmTextPosY			[in] 텍스트 워터마크 문자열 영역이 위치할 Y 좌표, 좌하단 기준(0.0: 하단)
	 * @param jwmTextFontFullPath	[in] 텍스트 워터마크 문자열에 적용할 폰트 파일의 경로, 상세 내용은 convertImage2PDF 참조
	 * @param wmTextFontIndex		[in] 텍스트 워터마크 문자열에 적용할 폰트 파일의 폰트 인덱스, 상세 내용은 convertImage2PDF 참조
	 * @param wmTextFontSize		[in] 텍스트 워터마크 문자열에 적용할 폰트 크기, 최대값: 300
	 * @param jwmImageFileFullPath	[in] 이미지 워터마크로 사용 될 이미지 파일의 경로, Alpha 채널이 포함된 PNG 파일만 가능
	 * @param wmImagePosX			[in] 사용하지 않음
	 * @param wmImagePosY			[in] 사용하지 않음
	 * @param jmaskingInfo			[in] 지정된 페이지의 지정 영역에 사각형으로 마스킹
	 * 										- 여러 개의 영역을 하나의 문자열로 만들어서 전달함.
	 *										[페이지1, X1, Y1, 폭1, 높이1], [페이지2, X2, Y2, 폭2, 높이2], [...], …
	 *										예) [1, 100, 100, 50, 10], [1, 100, 200, 50, 10] -> 2개 영역 지정
	 *										페이지 번호: 1부터 시작함.
	 *										X: 영역 왼쪽의 위치 (원점은 페이지 왼쪽)
	 *										Y: 영역 아래쪽의 위치 (원점은 페이지 아래쪽)
	 *										폭: 영역 사각형의 폭
	 *										높이: 영역 사각형의 높이
	 *										- 위치 및 크기의 단위는 픽셀이며, 72 DPI를 기준으로 함.
	 *										페이지 크기가 A4인 경우, 페이지의 전체 크기는 595.276 x 841.89 (pixel)임.
	 * @param maskingColorR			[in] 마스킹 사각형의 칼라 R값, 범위: 0.0 ~ 1.0
	 * @param maskingColorG			[in] 마스킹 사각형의 칼라 G값, 범위: 0.0 ~ 1.0
	 * @param maskingColorB			[in] 마스킹 사각형의 칼라 B값, 범위: 0.0 ~ 1.0
	 * @param jfilenameDst			[in] 생성될 PDF 파일 이름
	 * @param pdfa        			[in] PDFA 생성 여부(1: PDFA, 그외: PDF, convertImage2PDFUTF8WithMasking함수만 해당)
	 * @return 0: 성공
	 *		   그외: 실패
	 */
	public static native int convertImage2PDFWithMasking(String jsingleTIFFList, int delimiter,
							String jauthor, String jcreator, String jtitle, String jsubject, String jkeywords,
							String juserPasswd, int permission,
							int pageSize, int pageDirection, float pageWidth, float pageHeight,
							String jwmText, int wmTextPosX, int wmTextPosY, String jwmTextFontFullPath, int wmTextFontIndex, int wmTextFontSize,
							String jwmImageFileFullPath, int wmImagePosX, int wmImagePosY,
							String jmaskingInfo, float maskingColorR, float maskingColorG, float maskingColorB,
							String jfilenameDst);
							
  public static native int convertImage2PDFUTF8WithMasking(String jsingleTIFFList, int delimiter,
							String jauthor, String jcreator, String jtitle, String jsubject, String jkeywords,
							String juserPasswd, int permission,
							int pageSize, int pageDirection, float pageWidth, float pageHeight,
							String jwmText, int wmTextPosX, int wmTextPosY, String jwmTextFontFullPath, int wmTextFontIndex, int wmTextFontSize,
							String jwmImageFileFullPath, int wmImagePosX, int wmImagePosY,
							String jmaskingInfo, float maskingColorR, float maskingColorG, float maskingColorB,
							String jfilenameDst, int pdfa);

	public Image2PDFJNI()
	{
	} 
}
