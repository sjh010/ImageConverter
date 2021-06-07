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

package com.inzisoft.pdf2image;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InziPDF {
	
	public static final int SEARCH_INFO_SIZE = 6;
	public static final int MATRIX_SIZE = 6;
	public static final int TRANSFORM_BOX_SIZE = 4;
	
	public class PDFText
	{
		public String text;
		public int x0;
		public int y0;
		public int x1;
		public int y1;
	}
	
	/* load our native library */
	static {
		// 설치할 경로에 맞게 변경 필요
		try {
			System.loadLibrary("JNI_InziPDF2Image");
		} catch (Exception e) {
			log.error("library load fail.", e);
		}
			
	}

	/* The native functions */
	
	/**
	 * 하위 모듈의 경로 설정하는 함수(AIX에서사용, 다른 플랫폼은 사용 안해도 됨)
	 * 
	 * @param modulepath		[in] PDF2Image, 이미지 코덱 등이 있는 패스(풀경로) 
	 * @return 1 : 성공
	 *		     그외 : 실패
	 */
	public static native int SetP2ILibraryPath(String modulepath);
	
	/**
	 * 로그 설정 파일 및 로그 생성 위치 설정
	 * 
	 * @param confpath		[in] 로그 설정 파일 경로
	 * @param conffilename	[in] 로그 설정 파일 이름(일반적으로 InziCIP.cfg) 
	 * @param logpath		[in] 로그 파일 생성 경로(로그가 생성될 폴더는 UNIX/LINUX(쓰기, 실행), Windows(쓰기, 읽기) 권한이 있어야 함)
	 * @return 0 : 성공
	 *		     -30000 이하 : 실패
	 */
	public static native int SetP2ILogPath(String confpath, String conffilename, String logpath);
	
	/**
	 * 전체 페이지 수 가져오기
	 * 
	 * @param filenameSrc		[in] 파일 이름 
	 * @return 전체 페이지 수
	 *		   0: 실패
	 */
	public static native int getPDFPageCount(String filenameSrc);
	
	/**
	 * 전체 페이지를 DPI비율에 따라 이미지로 변환
	 * 
	 * @param filenameSrc		[in] 파일 이름
	 * @param folderDst			[in] 이미지 생성 폴더
	 * @param filenameDst		[in] 생성될 이미지 파일 이름(convertPDF2NamedImage API에서만 사용됨)
	 * 									ex) MyImage.tif로 지정 할 경우 생성되는 파일은 다음과 같이 생성됨
	 *										1	페이지 	: MyImage_01.tif
	 *										2	페이지 	: MyImage_02.tif
	 *										10 	페이지 	: MyImage_10.tif
	 *										100 페이지 	: MyImage_100.tif
	 * @param resolution		[in] TIFF 저장 시 헤더에 적용될 resolution값, 이미지에 적용될 DPI값
	 * @param comprate			[in] 압축을 사용하는 포맷의 압축 값
	 * 									JPEG, JPEG in TIFF: 1 ~ 100
	 * 									JPEG2000, JPEG2000 in TIFF: 24 - 1bpp, 12 - 2bpp, 48 - 0.5bpp
	 * @param filetype			[in] 저장할 파일 형식 설정
	 * 									1: BMP
	 * 									2: JPEG
	 * 									3: JPEG2000
	 * 									4: TIFF
	 * @param comptype			[in] filetype이 TIFF일 때 압축 방식 설정
	 * 									1: 무압축
	 * 									2: RLE
	 * 									3: G3
	 * 									4: G4
	 * 									5: LZW
	 * 									7: JPEG
	 * 									34712(34713): JPEG2000
	 * 									34663: JBIG2
	 * @param binarize			[in] 이진화 여부 설정
	 * 									0: 이진화 수행 안 함
	 * 									그외: 이진화 수행
	 * 									filetype이 1, 4일 때 사용 가능
	 * 									filetype이 4이고, comptype이 3, 4, 34663 일 때 사용 가능
	 * @param threshold			[in] binarize가 1일 때 이진화 기준값 설정, 0일 경우 내부적으로 적당한 기준값을 사용하여 이진화
	 * @return 1이상: 성공(변환된 페이지 개수)
	 *		       그외: 실패
	 */
	public static native int convertPDF2Image(String filenameSrc,
							String folderDst, int resolution, int comprate,
							int filetype, int comptype, int binarize, int threshold);
							
	public static native int convertPDF2NamedImage(String filenameSrc,
							String folderDst, String filenameDst, int resolution, int comprate,
							int filetype, int comptype, int binarize, int threshold);

	/**
	 * 전체 페이지를 DPI비율에 따라 이미지 및 inzi iFrom 형식의 썸네일로 변환
	 * 
	 * @param filenameSrc		[in] 파일 이름
	 * @param folderDst			[in] 이미지 생성 폴더
	 * @param filenameDst		[in] 생성될 이미지 파일 이름(convertPDF2NamedImageWithThumbnail API에서만 사용됨)
	 * 									ex) MyImage.tif로 지정 할 경우 생성되는 파일은 다음과 같이 생성됨
	 *										1	페이지 	: MyImage_01.tif
	 *										2	페이지 	: MyImage_02.tif
	 *										10 	페이지 	: MyImage_10.tif
	 *										100 페이지 	: MyImage_100.tif
	 * @param resolution		[in] TIFF 저장 시 헤더에 적용될 resolution값, 이미지에 적용될 DPI값
	 * @param comprate			[in] 압축을 사용하는 포맷의 압축 값, 상세 내용은 convertPDF2Image 참조
	 * @param filetype			[in] 저장할 파일 형식 설정, 상세 내용은 convertPDF2Image 참조
	 * @param comptype			[in] filetype이 TIFF일 때 압축 방식 설정, 상세 내용은 convertPDF2Image 참조
	 * @param binarize			[in] 이진화 여부 설정, 상세 내용은 convertPDF2Image 참조
	 * @param threshold			[in] binarize가 1일 때 이진화 기준값 설정, 0일 경우 내부적으로 적당한 기준값을 사용하여 이진화
	 * @param makeizt			[in] InziForm 형식의 썸네일 이미지를 생성 설정
	 * 									0: 생성 안 함
	 * 									그외: 생성
	 * @param thumbnailWid		[in] makeizt가 0이 아닐 때 생성되는 썸네일의 가로 길이 
	 * @param thumbnailHgt		[in] makeizt가 0이 아닐 때 생성되는 썸네일의 세로 길이
	 * @return 1이상: 성공(변환된 페이지 개수)
	 *		       그외: 실패
	 */
	public static native int convertPDF2ImageWithThumbnail(String filenameSrc,
			String folderDst, int resolution, int comprate,
			int filetype, int comptype, int binarize, int threshold,
			int makeizt, int thumbnailWid, int thumbnailHgt);
			
	public static native int convertPDF2NamedImageWithThumbnail(String filenameSrc,
			String folderDst, String filenameDst, int resolution, int comprate,
			int filetype, int comptype, int binarize, int threshold,
			int makeizt, int thumbnailWid, int thumbnailHgt);
	
	/**
	 * 특정 페이지를 DPI비율에 따라 이미지로 변환
	 * 
	 * @param filenameSrc		[in] 파일 이름
	 * @param folderDst			[in] 이미지 생성 폴더
	 * @param filenameDst		[in] 생성될 이미지 파일 이름(convertPDF2NamedImageOnePage API에서만 사용됨)
	 * 									ex) 단일 페이지 변환의 경우 지정된 파일 명 그대로 생성됨, MyImage.tif로 지정하면 MyImage.tif로 파일이 생성됨
	 * @param resolution		[in] TIFF 저장 시 헤더에 적용될 resolution값, 이미지에 적용될 DPI값
	 * @param comprate			[in] 압축을 사용하는 포맷의 압축 값, 상세 내용은 convertPDF2Image 참조
	 * @param filetype			[in] 저장할 파일 형식 설정, 상세 내용은 convertPDF2Image 참조
	 * @param comptype			[in] filetype이 TIFF일 때 압축 방식 설정, 상세 내용은 convertPDF2Image 참조
	 * @param binarize			[in] 이진화 여부 설정, 상세 내용은 convertPDF2Image 참조
	 * @param threshold			[in] binarize가 1일 때 이진화 기준값 설정, 0일 경우 내부적으로 적당한 기준값을 사용하여 이진화
	 * @param page				[in] 변환할 이미지의 페이지 번호 (0-base)
	 * @return 1: 성공
	 *		       그외: 실패
	 */
	public static native int convertPDF2ImageOnePage(String filenameSrc,
							String folderDst, int resolution, int comprate,
							int filetype, int comptype, int binarize, int threshold,
							int page); // page = 0-based
							
	public static native int convertPDF2NamedImageOnePage(String filenameSrc,
							String folderDst, String filenameDst, int resolution, int comprate,
							int filetype, int comptype, int binarize, int threshold,
							int page); // page = 0-based
	
	/**
	 * 특정 페이지를 DPI비율에 따라 이미지 및 inzi iFrom 형식의 썸네일로 변환
	 * 
	 * @param filenameSrc		[in] 파일 이름
	 * @param folderDst			[in] 이미지 생성 폴더
	 * @param filenameDst		[in] 생성될 이미지 파일 이름(convertPDF2NamedImageOnePageWithThumbnail API에서만 사용됨)
	 * 									ex) 단일 페이지 변환의 경우 지정된 파일 명 그대로 생성됨, MyImage.tif로 지정하면 MyImage.tif로 파일이 생성됨
	 * @param resolution		[in] TIFF 저장 시 헤더에 적용될 resolution값, 이미지에 적용될 DPI값
	 * @param comprate			[in] 압축을 사용하는 포맷의 압축 값, 상세 내용은 convertPDF2Image 참조
	 * @param filetype			[in] 저장할 파일 형식 설정, 상세 내용은 convertPDF2Image 참조
	 * @param comptype			[in] filetype이 TIFF일 때 압축 방식 설정, 상세 내용은 convertPDF2Image 참조
	 * @param binarize			[in] 이진화 여부 설정, 상세 내용은 convertPDF2Image 참조
	 * @param threshold			[in] binarize가 1일 때 이진화 기준값 설정, 0일 경우 내부적으로 적당한 기준값을 사용하여 이진화
	 * @param page				[in] 변환할 이미지의 페이지 번호 (0-base)
	 * @param makeizt			[in] InziForm 형식의 썸네일 이미지를 생성 설정
	 * 									0: 생성 안 함
	 * 									그외: 생성
	 * @param thumbnailWid		[in] makeizt가 0이 아닐 때 생성되는 썸네일의 가로 길이 
	 * @param thumbnailHgt		[in] makeizt가 0이 아닐 때 생성되는 썸네일의 세로 길이
	 * @return 1: 성공
	 *		       그외: 실패
	 */
	public static native int convertPDF2ImageOnePageWithThumbnail(String filenameSrc,
							String folderDst, int resolution, int comprate,
							int filetype, int comptype, int binarize, int threshold,
							int page,
							int makeizt, int thumbnailWid, int thumbnailHgt);

	public static native int convertPDF2NamedImageOnePageWithThumbnail(String filenameSrc,
							String folderDst, String filenameDst, int resolution, int comprate,
							int filetype, int comptype, int binarize, int threshold,
							int page,
							int makeizt, int thumbnailWid, int thumbnailHgt);
							
	/**
	 * 특정 페이지를 Inzi iForm 형식의 썸네일 이미지로 변환
	 * 
	 * @param filenameSrc		[in] 파일 이름
	 * @param folderDst			[in] 이미지 생성 폴더
	 * @param page				[in] 변환할 이미지의 페이지 번호 (0-base)
	 * @param thumbnailWid		[in] 썸네일의 가로 길이 
	 * @param thumbnailHgt		[in] 썸네일의 세로 길이
	 * @return 1: 성공
	 *		       그외: 실패
	 */
	public static native int convertPDF2ThumbnailOnePage(String filenameSrc,
							String folderDst,
							int page,
							int thumbnailWid, int thumbnailHgt);


	/**
	* 특정 페이지를 특정 이름의 이미지로 변환
	* 이미지의 고정 크기 사용 가능(가로, 세로 지정)
	*
	* @param filenameSrc		[in] PDF 파일 이름
	* @param filenameDst		[in] 생성될 이미지 파일 이름
	* @param resolutioninfo		[in] TIFF 저장 시 헤더에 적용될 resolution값(이미지 크기에 영향 안줌)
	* @param sizemode			[in] 이미지 크기 지정 방식
	* 									1: DPI모드
	* 									2: Pixel모드
	*									3: Fixed모드
	* @param size1				[in] 이미지 크기의 값
	* 									sizemode가 1일 경우: 100, 200, 300등 DPI값
	* 									sizemode가 2일 경우: 3509등 pixel값
	*									sizemode가 3일 경우: 이미지의 가로 길이
	* @param size2				[in] 이미지 크기의 값
	* 									sizemode가 1일 경우: 사용 안 됨
	* 									sizemode가 2일 경우: 사용 안 됨
	*									sizemode가 3일 경우: 이미지의 세로 길이
	* @param comprate			[in] 압축을 사용하는 포맷의 압축 값
	* 									JPEG, JPEG in TIFF: 1 ~ 100
	* 									JPEG2000, JPEG2000 in TIFF: 24 - 1bpp, 12 - 2bpp, 48 - 0.5bpp
	* @param filetype			[in] 저장할 파일 형식 설정
	* 									1: BMP
	* 									2: JPEG
	* 									3: JPEG2000
	* 									4: TIFF
	*									8: PNG
	* @param comptype			[in] filetype이 TIFF일 때 압축 방식 설정
	* 									1: 무압축
	* 									2: RLE
	* 									3: G3
	* 									4: G4
	* 									5: LZW
	* 									7: JPEG
	* 									34712, 34713: JPEG2000
	* 									34663: JBIG2
	* @param binarize			[in] 이진화 여부 설정
	* 									0: 이진화 수행 안 함
	* 									그외: 이진화 수행
	* 									filetype이 1, 4, 8일 때 사용 가능
	* 									filetype이 4이고, comptype이 3, 4, 34663 일 때 사용 가능
	* @param threshold			[in] binarize가 1일 때 이진화 기준값 설정, 0일 경우 내부적으로 적당한 기준값을 사용하여 이진화
	* @param page				[in] 변환할 이미지의 페이지 번호 (0-base)
	*	
	*/
	public static native int convertPDF2FixedImage(String filenameSrc, String filenameDst,
													int resolutioninfo, int sizemode, int size1, int size2,
													int comprate, int filetype, int comptype,
													int binarize, int threshold, int page);
	
	/**
	* 특정 페이지를 특정 이름의 이미지로 변환
	* 이미지의 고정 크기 사용 가능(가로, 세로 지정)
	* PDF 내부에 들어있는 TSA 관련 이미지를 렌더링 하지 않음(이미지 변환시 TSA 마크 안보임)
	*/
	public static native int convertPDF2FixedImage_NoTSA(String filenameSrc, String filenameDst,
														int resolutioninfo, int sizemode, int size1, int size2,
														int comprate, int filetype, int comptype,
														int binarize, int threshold, int page);
	
	/**
	 * 특정 페이지를 메모리 형태의 raw이미지로 변환 했을 때 크기를 계산
	 * 
	 * @param filenameSrc		[in] 파일 이름
	 * @param page				[in] 변환할 이미지의 페이지 번호 (0-base)
	 * @param sizemode			[in] 이미지 크기 지정 방식
	 * 									1: DPI모드
	 * 									2: Pixel모드
	 * @param size				[in] 이미지 크기의 값
	 * 									sizemode가 1일 경우: 100, 200, 300등 DPI값
	 * 									sizemode가 2일 경우: 3509등 pixel값
	 * @param datamode			[in] raw 이미지의 형식
	 * 									0: RGBA8888
	 * 									1: RGB565
	 * 									2: RGB
	 * 									3: DIB
	 * @param aDataInfo			[out] 결과 값
	 * 									aDataInfo[0]: raw데이터의 크기
	 * 									aDataInfo[1]: raw데이터의 가로 길이
	 * 									aDataInfo[2]: raw데이터의 세로 길이							
	 * @return 1: 성공
	 *		       그외: 실패
	 */
	public static native int convertPDF2DataApprox(String filenameSrc, int page, int sizemode, int size, int datamode, long [] aDataInfo);
	
	/**
	 * 특정 페이지를 DPI비율에 따라 메모리 형태의 raw이미지로 변환
	 * 
	 * @param filenameSrc		[in] 파일 이름
	 * @param page				[in] 변환할 이미지의 페이지 번호 (0-base)
	 * @param sizemode			[in] 이미지 크기 지정 방식
	 * @param size				[in] 이미지 크기의 값
	 * @param datamode			[in] raw 이미지의 형식
	 * @param outputdata		[out] 변환된 raw이미지가 저장 될 메모리
	 * @param aDataInfo			[in] convertPDF2DataApprox에서 반환된 array
	 * @return 1: 성공
	 *		       그외: 실패
	 */
	public static native int convertPDF2DataOnePageWithSizeMode(String filenameSrc, int page, int sizemode, int size, int datamode, byte [] outputdata, long [] aDataInfo);

	/**
	 * 외부 폰트 불러오기
	 * 
	 * @param filename			[in] 외부 폰트 파일 리스트
	 */
	public static native void loadExternalFontList(String filename);

	/**
	 * 서버 라이선스 파일의 경로 지정
	 * 
	 * @param filename			[in] 라이선스 파일의 절대 경로
	 * @return 1: 성공
	 *		       그외: 실패
	 */
	public static native int loadLicenseFile(String filename);
	
	/**
	 * 특정페이지를 PDF로 추출
	 * 
	 * @param filenameSrc		[in] 파일 이름
	 * @param filenameDst		[in] 추출될 파일 이름
	 * @param page				[in] 추출할 페이지 번호 (1-base)
	 * @return 1: 성공
	 *		       그외: 실패
	 */
	public static native int extractPDFPage(String filenameSrc, String filenameDst, int page);
	
	/**
	 * 특정페이지들을 PDF로 추출
	 * 
	 * @param filenameSrc			[in] 파일 이름
	 * @param filenameDst			[in] 추출될 파일 이름
	 * @param extractPages			[in] 추출할 페이지 번호들(1-base)
	 * 									- 각 페이지 번호는 구분자로 구분
	 * 									- 페이지 순서는 입력된 페이지 번호순과 동일
	 * @param delimiter				[in] 페이지 구분자
	 * @param skipStreamComparison	[in] PDF stream 비교를 스킵 할 지 여부
	 * @return 1: 성공
	 *		       그외: 실패
	 */
	public static native int extractPDFPages(String filenameSrc, String filenameDst, String extractPages, int delimiter, int skipStreamComparison);
	
	/**
	 * PDF의 특정페이지들을 제거
	 * 
	 * @param filenameSrc			[in] 파일 이름
	 * @param filenameDst			[in] 추출될 파일 이름
	 * @param removePages			[in] 제거할 페이지 번호들(1-base)
	 * 									- 각 페이지 번호는 구분자로 구분
	 * @param delimiter				[in] 페이지 구분자
	 * @param skipStreamComparison	[in] PDF stream 비교를 스킵 할 지 여부
	 * @return 1: 성공
	 *		       그외: 실패
	 */
	public static native int removePDFPages(String filenameSrc, String filenameDst, String removePages, int delimiter, int skipStreamComparison);

	/**
	 * PDF 특정페이지에서 영역좌표로 Text 추출, 파일로 저장
	 * 
	 * @param filenameSrc			[in] PDF 파일 이름
	 * @param filenameDst			[in] Text가 저장될 파일 이름
	 * @param page					[in] Text를 추출할 페이지 (0-base)
	 * @param x1					[in] Text를 추출할 영역의 좌상단 x좌표
	 * @param y1					[in] Text를 추출할 영역의 좌상단 y좌표
	 * @param x2					[in] Text를 추출할 영역의 우하단 x좌표
	 * @param y2					[in] Text를 추출할 영역의 우하단 y좌표
	 * @param stringtype			[in] 저장될 Text의 형태
	 *									0 : 일반 Txt 파일 형태(UTF8), 1 : JSON 형태(UTF8)
	 * @return 1: 성공
	 *		       그외: 실패
	 */
	public static native int extractTextinPDFtoFile(String filenameSrc, String filenameDst, int page, int x1, int y1, int x2, int y2, int stringtype);
	
	/**
	 * PDF 특정페이지에서 영역좌표로 Text 추출, 메모리로 저장(getTextByInfo 함수로 메모리 추출)
	 * 
	 * @param filenameSrc			[in] PDF 파일 이름	 
	 * @param page					[in] Text를 추출할 페이지 (0-base)
	 * @param x1					[in] Text를 추출할 영역의 좌상단 x좌표
	 * @param y1					[in] Text를 추출할 영역의 좌상단 y좌표
	 * @param x2					[in] Text를 추출할 영역의 우하단 x좌표
	 * @param y2					[in] Text를 추출할 영역의 우하단 y좌표
	 * @param stringtype			[in] 저장될 Text의 형태
	 *									0 : 일반 Txt 파일 형태(UTF8), 1 : JSON 형태(UTF8)
	 * @param aTxtInfo				[out] 추출된 Text 정보, 0: pointer 주소, 1: 메모리 크기, 2: string type
	 * @return 1: 성공
	 *		       그외: 실패
	 */
	public static native int extractTextinPDFtoMem(String filenameSrc, int page, int x1, int y1, int x2, int y2, int stringtype, long[] aTxtInfo);
	
	/**
	 * PDF 특정페이지에서 영역비율로 Text 추출, 파일로 저장
	 * 
	 * @param filenameSrc			[in] PDF 파일 이름
	 * @param filenameDst			[in] Text가 저장될 파일 이름
	 * @param page					[in] Text를 추출할 페이지 (0-base)
	 * @param x1rate				[in] Text를 추출할 영역의 좌상단 x위치의 비율 (0.0 ~ 1.0)
	 * @param y1rate				[in] Text를 추출할 영역의 좌상단 y위치의 비율 (0.0 ~ 1.0)
	 * @param x2rate				[in] Text를 추출할 영역의 우하단 x위치의 비율 (0.0 ~ 1.0)
	 * @param y2rate				[in] Text를 추출할 영역의 우하단 y위치의 비율 (0.0 ~ 1.0)
	 * @param stringtype			[in] 저장될 Text의 형태
	 *									0 : 일반 Txt 파일 형태(UTF8), 1 : JSON 형태(UTF8)
	 * @return 1: 성공
	 *		       그외: 실패
	 */
	public static native int extractTextRateinPDFtoFile(String filenameSrc, String filenameDst, int page, float x1rate, float y1rate, float x2rate, float y2rate, int stringtype);
	
	/**
	 * PDF 특정페이지에서 영역비율로 Text 추출, 메모리로 저장(getTextByInfo 함수로 메모리 추출)
	 * 
	 * @param filenameSrc			[in] PDF 파일 이름	 
	 * @param page					[in] Text를 추출할 페이지 (0-base)
	 * @param x1rate				[in] Text를 추출할 영역의 좌상단 x위치의 비율 (0.0 ~ 1.0)
	 * @param y1rate				[in] Text를 추출할 영역의 좌상단 y위치의 비율 (0.0 ~ 1.0)
	 * @param x2rate				[in] Text를 추출할 영역의 우하단 x위치의 비율 (0.0 ~ 1.0)
	 * @param y2rate				[in] Text를 추출할 영역의 우하단 y위치의 비율 (0.0 ~ 1.0)
	 * @param stringtype			[in] 저장될 Text의 형태
	 *									0 : 일반 Txt 파일 형태(UTF8), 1 : JSON 형태(UTF8)
	 * @param aTxtInfo				[out] 추출된 Text 정보, 0: pointer 주소, 1: 메모리 크기, 2: string type
	 * @return 1: 성공
	 *		       그외: 실패
	 */
	public static native int extractTextRateinPDFtoMem(String filenameSrc, int page, float x1rate, float y1rate, float x2rate, float y2rate, int stringtype, long[] aTxtInfo);
	
	/**
	 * 추출된 Text 데이터를 메모리 형태로 가져온다.(모듈에서 생성된 Text 데이터는 메모리 해제됨.)
	 * 
	 * @param aTxtInfo		[in] 추출된 Text 정보, 0: pointer 주소, 1: 메모리 크기, 2: string type
	 * @param outputText	[out] 추출된 Text 메모리 (JAVA에서 미리 aTxtInfo를 이용해 메모리를 할당한 것)
	 * @return	1: 성공				
	 */
	public static native int getTextByInfo(long[] aTxtInfo, byte[] outputText);



	/**
	 * PDF 파일 로딩
	 * 
	 * @param filename				[in] PDF 파일 명
	 * @param password				[in] PDF 파일 암호
	 * @param extractText			[in] 텍스트 추출할 지 여부
	 * @param enableAnnotsManaging	[in] 주석 관리 기능 사용 여부
	 * @return 0 아님: 성공. PDF 문서 포인터
	 *		   0: 실패
	 */
	public static native long LoadDocument(String filename, String password, int extractText, int enableAnnotsManaging);
	
	/**
	 * PDF 메모리 로딩
	 * 
	 * @param inputData				[in] PDF 메모리 버퍼
	 * @param inputLength			[in] PDF 메모리 버퍼의 길이
	 * @param password				[in] PDF 파일 암호
	 * @param extractText			[in] 텍스트 추출할 지 여부
	 * @param enableAnnotsManaging	[in] 주석 관리 기능 사용 여부
	 * @return 0 아님: 성공. PDF 문서 포인터
	 *		   0: 실패
	 */
	public static native long LoadMemory(byte[] inputData, int inputLength, String password, int extractText, int enableAnnotsManaging);
	
	/**
	 * 전체 페이지 개수를 추출
	 * 
	 * @param pdfdoc				[in] PDF 문서 포인터
	 * @return 전체 페이지 수
	 *		   0: 실패
	 */	
	public static native int GetPageCount(long pdfdoc);
	
	/**
	 * 로드된 PDF문서 종료
	 * 
	 * @param pdfdoc				[in] PDF 문서 포인터
	 */	
	public static native void CloseDocument(long pdfdoc);
	
	/**
	 * 특정 페이지 로드
	 * 
	 * @param pdfdoc				[in] PDF 문서 포인터	
	 * @param page					[in] 로드할 페이지 포인터 (0-base)
	 * @param reload				[in] 0 - reload 안함, 1 - reload, 1로 설정할 경우 해당 페이지가 로드 되어 있어도 다시 재로드
	 * @return 1: 성공
	 * 		       그외: 실패
	 */	
	public static native int LoadPage(long pdfdoc, int page, int reload);
	
	/**
	 * 특정 로드된 페이지 해제
	 * 
	 * @param pdfdoc				[in] PDF 문서 포인터	
	 * @param page					[in] 페이지 번호 (0-base)
	 */
	public static native void FreePage(long pdfdoc, int page);
	
	/**
	 * 로드된 전체 페이지 해제
	 * 
	 * @param pdfdoc				[in] PDF 문서 포인터	
	 */
	public static native void FreeAllPage(long pdfdoc);
	
	/**
	 * 가장 최근에 로드된 페이지의 가로 길이를 반환
	 * 
	 * @param pdfdoc				[in] PDF 문서 포인터	
	 * @return 페이지 가로 길이
	 * 		   0.0 : 실패
	 */
	public static native float GetPageWidth(long pdfdoc);
	
	/**
	 * 가장 최근에 로드된 페이지의 세로 길이를 반환
	 * 
	 * @param pdfdoc				[in] PDF 문서 포인터	
	 * @return 페이지 세로 길이
	 * 		   0.0 : 실패
	 */
	public static native float GetPageHeight(long pdfdoc);
	
	/**
	 * RenderPage를 위한 메모리 데이터 크기 계산
	 * 
	 * @param width					[in] 렌더링 될 가로 길이
	 * @param height				[in] 렌더링 될 세로 길이
	 * @param format				[in] raw 데이터 형식
	 * 										0: RGBA8888
	 * 										1: RGB565
	 * 										2: RGB
	 * 										3: DIB
	 * @return 렌더링 될 메모리 데이터 크기
	 * 		   0이하의 음수: 실패
	 */	
	public static native int CalculateRenderPageSize(int width, int height, int format);
	
	/**
	 * 로드된 페이지를 이미지로 변환
	 * 
	 * @param pdfdoc				[in] PDF 문서 포인터	
	 * @param data					[in] 렌더링 된 이미지 저장 버퍼
	 * @param dataSize				[in] 렌더링 이미지 버퍼 크기
	 * @param width					[in] 렌더링 가로 크기
	 * @param height				[in] 렌더링 세로 크기
	 * @param patchX				[in] ROI적용 시 ROI 좌상당 x좌표
	 * @param patchY				[in] ROI적용 시 ROI 좌상당 y좌표
	 * @param patchW				[in] ROI적용 시 ROI 가로 길이
	 * @param patchH				[in] ROI적용 시 ROI 세로 길이
	 * @param format				[in] raw 데이터 형식
	 * 										0: RGBA8888
	 * 										1: RGB565
	 * 										2: RGB
	 * 										3: DIB
	 * @return 1: 성공
	 * 		       그외: 실패
	 */
	public static native int RenderPage(long pdfdoc, byte[] data, int dataSize,
												int width, int height,
												int patchX, int patchY, int patchW, int patchH,
												int format);
	
	/**
	 * 특정 페이지에서 추출될 텍스트 객체의 크기 가져오기
	 * 
	 * @param pdfdoc				[in] PDF 문서 포인터	
	 * @param page					[in] 페이지 번호 (0-base)
	 * @return PDFText객체의 크기
	 *		   음수: 실패
	 */
	public static native int GetTextArraySize(long pdfdoc, int page);
	
	/**
	 * 특정 페이지에서 텍스트 정보 추출
	 * 
	 * @param pdfdoc				[in] PDF 문서 포인터	
	 * @param page					[in] 페이지 번호 (0-base)
	 * @param textArray				[out] 추출된 텍스트 정보 (배열의 각 PDFText 객체는 new로 할당된 상태이어야 함)
	 * @return 1: 성공
	 * 		       그외: 실패
	 */
	public static native int GetTextArray(long pdfdoc, int page, PDFText[] textArray);
	
	/**
	 * PDF내의 텍스트 검색
	 * 
	 * @param pdfdoc				[in] PDF 문서 포인터
	 * @param searchedPage			[out] 각 페이지별 텍스트 검색 여부 (데이터의 크기 >= 페이지 수)
	 * @param startPage				[in] 검색을 시작하고자 하는 페이지 번호 (0-base)
	 * @param startPos				[in] 검색 시작 텍스트의 위치 (-1 : 최초 검색 시)
	 * @param direction				[in] 검색 방향 (1: 순방향, 0: 역방향)
	 * @param text					[in] 검색할 텍스트
	 * @param searchedInfo			[out] 검색된 텍스트 정보
	 * 										배열의 크기는 SEARCH_INFO_SIZE 값 사용
	 * 										searchedInfo[0] : 검색된 페이지 번호
	 * 										searchedInfo[1] : 검색된 텍스트 위치
	 * 										searchedInfo[2] : 검색된 텍스트의 x0 좌표
	 * 										searchedInfo[3] : 검색된 텍스트의 y0 좌표
	 * 										searchedInfo[4] : 검색된 텍스트의 x1 좌표
	 * 										searchedInfo[5] : 검색된 텍스트의 y1 좌표
	 * @return 1: 성공
	 * 		       그외: 실패
	 */
	public static native int TextSearch(long pdfdoc, byte[] searchedPage, int startPage, int startPos, int direction,
										String text, long[] searchedInfo);
	
	/**
	 * 특정 페이지의 주석데이터 크기 가져오기
	 * 
	 * @param pdfdoc				[in] PDF 문서 포인터	
	 * @param page					[in] 페이지 번호 (0-base)
	 * @return 주석데이터의 크기
	 *		   0이하의 음수: 실패
	 */
	public static native int GetAnnotationSize(long pdfdoc, int page);
	
	/**
	 * 특정 페이지의 주석데이터 가져오기
	 * 
	 * @param pdfdoc				[in] PDF 문서 포인터	
	 * @param page					[in] 페이지 번호 (0-base)
	 * @param data					[out] 주석데이터
	 * @param dataSize				[in] 주석데이터 크기 
	 * @return 1: 성공
	 * 		       그외: 실패
	 */
	public static native int GetAnnotation(long pdfdoc, int page, byte[] data, int dataSize);
	
	/**
	 * 특정 페이지의 주석데이터 삽입하기
	 * 
	 * @param pdfdoc				[in] PDF 문서 포인터	
	 * @param page					[in] 페이지 번호 (0-base)
	 * @param data					[in] 삽입할 주석데이터
	 * @param dataSize				[in] 삽입할 주석데이터 크기 
	 * @return 1: 성공
	 * 		       그외: 실패
	 */
	public static native int SetAnnotation(long pdfdoc, int page, byte[] data, int dataSize);
	
	/**
	 * 특정 페이지의 주석데이터 삭제
	 * 
	 * @param pdfdoc				[in] PDF 문서 포인터	
	 * @param page					[in] 페이지 번호 (0-base)
	 * @return 1: 성공
	 * 		       그외: 실패
	 */
	public static native int RemoveAnnotation(long pdfdoc, int page);
	
	/**
	 * 주석데이터 처리된 특정페이지 저장
	 * 
	 * @param pdfdoc				[in] PDF 문서 포인터	
	 * @param filename				[in] 저장될 파일 이름
	 * @return 1: 성공
	 * 		       그외: 실패
	 */
	public static native int SaveAnnotation(long pdfdoc, String filename);
	
	/**
	 * 가장 최근에 렌더링된 PDF 페이지의 matrix 정보 가져오기 
	 * 
	 * @param pdfdoc				[in] PDF 문서 포인터	
	 * @param matrix				[out] matrix 정보
	 * 										배열의 크기는 MATRIX_SIZE 값 사용
	 * 										matrix[0] : matrix a값
	 * 										matrix[1] : matrix b값
	 * 										matrix[2] : matrix c값
	 * 										matrix[3] : matrix d값
	 * 										matrix[4] : matrix e값
	 * 										matrix[5] : matrix f값
	 * @return 1: 성공
	 * 		       그외: 실패
	 */
	public static native int GetTransformMatrix(long pdfdoc, float[] matrix);
	
	
	/**
	 * matrix 정보를 통해 좌표를 변환, PDF좌표와 렌더링된 이미지 좌표간 변환에 사용  
	 * 
	 * @param a						[in] matrix a값 
	 * @param b						[in] matrix b값
	 * @param c						[in] matrix c값
	 * @param d						[in] matrix d값
	 * @param e						[in] matrix e값
	 * @param f						[in] matrix f값
	 * @param oriX0					[in] 좌표 x0
	 * @param oriY0					[in] 좌표 y0
	 * @param oriX1					[in] 좌표 x1
	 * @param oriY1					[in] 좌표 y1
	 * @param transformData			[out] 변환된 좌표
	 * 										배열의 크기는 TRANSFORM_BOX_SIZE 값 사용
	 * 										transformData[0] : 변환된 좌표 x0
	 * 										transformData[1] : 변환된 좌표 y0
	 * 										transformData[2] : 변환된 좌표 x1
	 * 										transformData[3] : 변환된 좌표 y1
	 * @return 1: 성공
	 * 		       그외: 실패
	 */
	public static native int TransformBBox(float a, float b, float c, float d, float e, float f,
											int oriX0, int oriY0, int oriX1, int oriY1, 
											long[] transformData);
	
	/**
	 * matrix 정보의 역변환을 통한 좌표를 변환, PDF좌표와 렌더링된 이미지 좌표간 변환에 사용  
	 * 
	 * @param a						[in] matrix a값 
	 * @param b						[in] matrix b값
	 * @param c						[in] matrix c값
	 * @param d						[in] matrix d값
	 * @param e						[in] matrix e값
	 * @param f						[in] matrix f값
	 * @param oriX0					[in] 좌표 x0
	 * @param oriY0					[in] 좌표 y0
	 * @param oriX1					[in] 좌표 x1
	 * @param oriY1					[in] 좌표 y1
	 * @param transformData			[out] 변환된 좌표
	 * 										배열의 크기는 TRANSFORM_BOX_SIZE 값 사용
	 * 										transformData[0] : 변환된 좌표 x0
	 * 										transformData[1] : 변환된 좌표 y0
	 * 										transformData[2] : 변환된 좌표 x1
	 * 										transformData[3] : 변환된 좌표 y1
	 * @return 1: 성공
	 * 		       그외: 실패
	 */
	public static native int InverseTransformBBox(float a, float b, float c, float d, float e, float f,
												int oriX0, int oriY0, int oriX1, int oriY1, 
												long[] transformData);
	
	/**
	 * PDF 파일에서 이미지 추출
	 * 
	 * @param pdfdoc				[in] PDF 문서 포인터
	 * @param page			    	[in] 추출될 페이지 번호 (0-base), 음수일 경우 전체 이미지 추출
	 * @param prefix		  		[in] 생성될 이미지 파일의 prefix
	 * 
	 * @return 1: 성공
	 * 		   그외: 실패
	 */	
	public static native int GetImages(long pdfdoc, int page, String prefix);
	
	/**
	 * PDF 파일에서 폰트 추출
	 * 
	 * @param pdfdoc				[in] PDF 문서 포인터
	 * @param page			    	[in] 추출될 페이지 번호 (0-base), 음수일 경우 전체 폰트 추출
	 * @param prefix		  		[in] 생성될 폰트 파일의 prefix
	 * 
	 * @return 1: 성공
	 * 		   그외: 실패
	 */
	public static native int GetFonts(long pdfdoc, int page, String prefix);
	
	public InziPDF()
	{
	} 

	/*
	public static int getTotalPageTiffImages(String strPDFFile, String strImageFolder, int resolution, int comprate,
					int filetype, int comptype, int binarize, int threshold)
	{
		if(resolution == 0)
			resolution = 144;

		File dir = new File(strImageFolder);
		dir.mkdir();

		int totalPage = getPDFPageCount(strPDFFile);
		System.out.println("hhshin totalPage :" + totalPage);

		ConvertPageThread [] cTh = new ConvertPageThread[totalPage];

		for(int i = 0 ; i < totalPage ; i++)
		{
			cTh[i] = new ConvertPageThread(strPDFFile, strImageFolder, resolution, comprate,
											filetype, comptype, binarize, threshold, i);
			cTh[i].start();			
		}

		for(int i = 0 ; i < totalPage ; i++)
		{
			try {
				cTh[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return totalPage; 
	}

	public static int getTotalPageTiffImagesWithThumbnail(String strPDFFile, String strImageFolder, int resolution, int comprate,
					int filetype, int comptype, int binarize, int threshold,
					int makeizt, int thumbnailWid, int thumbnailHgt)
	{
		if(resolution == 0)
			resolution = 144;

		File dir = new File(strImageFolder);
		dir.mkdir();

		int totalPage = getPDFPageCount(strPDFFile);
		System.out.println("totalPage :" + totalPage);

		ConvertPageThread [] cTh = new ConvertPageThread[totalPage];

		for(int i = 0 ; i < totalPage ; i++)
		{
			cTh[i] = new ConvertPageThread(strPDFFile, strImageFolder, resolution, comprate,
											filetype, comptype, binarize, threshold, i,
											makeizt, thumbnailWid, thumbnailHgt);
			cTh[i].start();
			
			try {
				Thread.sleep(200);
			} catch(InterruptedException e){
				System.out.println(e.getMessage());
			}						
		}

		for(int i = 0 ; i < totalPage ; i++)
		{
			try {
				cTh[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return totalPage; 
	}
	
	public static int getTotalPageThumbnail(String strPDFFile, String strImageFolder, int thumbnailWid, int thumbnailHgt)
	{
		File dir = new File(strImageFolder);
		dir.mkdir();

		int totalPage = getPDFPageCount(strPDFFile);
		System.out.println("totalPage :" + totalPage);

		ConvertPageThread [] cTh = new ConvertPageThread[totalPage];

		for(int i = 0 ; i < totalPage ; i++)
		{
			cTh[i] = new ConvertPageThread(strPDFFile, strImageFolder, i, thumbnailWid, thumbnailHgt);
			cTh[i].start();			
		}

		for(int i = 0 ; i < totalPage ; i++)
		{
			try {
				cTh[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return totalPage; 
	}
	*/
}

