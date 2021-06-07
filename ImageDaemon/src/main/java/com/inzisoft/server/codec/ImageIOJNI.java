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

package com.inzisoft.server.codec;

import lombok.extern.slf4j.Slf4j;

// 각 함수 설명은 InziImage2Image 3.0.7.1 이후 기준으로 작성 되었습니다.
@Slf4j
public class ImageIOJNI {
	
	/* load our native library */
	static {
		// 설치할 경로에 맞게 변경 필요
		try {
			System.loadLibrary("JNI_ImageIO");
		} catch (Exception e) {
			log.error("library load fail.", e);
		}
		
	}

	/**
	 * 하위 모듈의 경로 설정하는 함수(AIX에서사용, 다른 플랫폼은 사용 안해도 됨)
	 * 
	 * @param modulepath [in] 이미지 코덱 등이 있는 패스(풀경로)
	 * @return 1 : 성공 그외 : 실패
	 */
	public static native int setI2ILibraryPath(String modulepath);

	/**
	 * 로그 설정 파일 및 로그 생성 위치 설정
	 * 
	 * @param confpath     [in] 로그 설정 파일 경로
	 * @param conffilename [in] 로그 설정 파일 이름(일반적으로 InziCIP.cfg)
	 * @param logpath      [in] 로그 파일 생성 경로(로그가 생성될 폴더는 UNIX/LINUX(쓰기, 실행),
	 *                     Windows(쓰기, 읽기) 권한이 있어야 함)
	 * @return 0 : 성공 -30000 이하 : 실패
	 */
	public static native int setI2ILogPath(String confpath, String conffilename, String logpath);

	public static native String GetVersion();

	public static native int loadLicenseFile(String filename);

	/**
	 * (deprecated)
	 */
	public native int loadImage_FILE(String InputName, String OutputName);

	/**
	 * (deprecated)
	 */
	public native int makeSingleIZTWithNoResize(String InputName, String OutputName);

	/**
	 * (deprecated)
	 */
	public native int makeSingleIZTWithResize(String InputName, String OutputName, int width, int height);

	/**
	 * (deprecated)
	 */
	public native int getTIFFTagSize_FILE(String InputName, int tag, long[] aTagInfo);

	/**
	 * (deprecated)
	 */
	public native int getTIFFTagSize_Mem(byte[] InputData, int InputLength, int tag, long[] aTagInfo);

	/**
	 * (deprecated)
	 */
	public native int getTIFFTagValueByInfo(long[] aTagInfo, byte[] value);

	/* 여기서부터가 새로 정리한 부분 */

	/**
	 * 파일 타입 리턴
	 * 
	 * @param inputName [in] 파일 이름
	 * @return 1: BMP, 2: JPEG, 3: JPEG2000, 4: TIFF, 5: JBIG2, 6: IZT, 7: GIF, 8:
	 *         PNG, 11: PDF 0: 파일 이름이 유효하지 않음, 알 수 없는 파일형식. -6: DLL(so,sl) 로드 실패 -7:
	 *         DLL(so,sl) 모듈에 적절한 함수가 없음
	 */
	public native int getFileType_FILE(String inputName);

	/**
	 * 파일 타입 리턴
	 * 
	 * @param inputData   [in] 이미지 메모리 데이터
	 * @param inputLength [in] 이미지 메모리 데이터 크기
	 * @return 1: BMP, 2: JPEG, 3: JPEG2000, 4: TIFF, 5: JBIG2, 6: IZT, 7: GIF, 8:
	 *         PNG, 11: PDF 0: 메모리가 유효하지 않음, 알 수 없는 메모리 형식 -6: DLL(so,sl) 로드 실패 -7:
	 *         DLL(so,sl) 모듈에 적절한 함수가 없음
	 */
	public native int getFileType_Mem(byte[] inputData, int inputLength);

	/**
	 * Multi-TIFF 이미지에서 총 이미지의 개수를 리턴
	 * 
	 * @param inputName [in] Multi-TIFF 파일 이름
	 * @return 1~: Multi-TIFF 이미지의 개수 -1: 파일 이름이 유효하지 않음 -2: 이미지 개수를 받아오는 부분에서 에러가
	 *         발생했습니다. -3: 파일 이름이 유효하지 않음, DLL(so,sl) 로드 실패 -4: DLL(so,sl) 모듈에 적절한
	 *         함수가 없음 -6: DLL(so,sl) 로드 실패 -7: DLL(so,sl) 모듈에 적절한 함수가 없음 그외:
	 *         ImageIO, 변환서버 에러 코드 참조
	 */
	public native int getTIFFTotalPage_FILE(String inputName);

	/**
	 * Multi-TIFF 이미지에서 총 이미지의 개수를 리턴
	 * 
	 * @param inputData   [in] Multi-TIFF 이미지의 메모리
	 * @param inputLength [in] Multi-TIFF 이미지의 메모리의 길이
	 * @return 1~: Multi-TIFF 이미지의 개수 -1: 메모리가 유효하지 않음 -2: 이미지 크기는 0보다 커야함 -4:
	 *         DLL(so,sl) 로드 실패 -5: DLL(so,sl) 모듈에 적절한 함수가 없음 -6: DLL(so,sl) 로드 실패
	 *         -7: DLL(so,sl) 모듈에 적절한 함수가 없음 그외: ImageIO, 변환서버 에러 코드 참조
	 */
	public native int getTIFFTotalPage_Mem(byte[] inputData, int inputLength);

	/**
	 * 이미지 가로 세로를 추출하는 함수
	 * 
	 * @param inputName [in] 이미지의 파일이름
	 * @param pageNum   [in] 추출할 이미지의 번호(TIFF 파일만 사용됨, 1부터 시작됨)
	 * @param size      [out] size[0] : 이미지의 가로 size[1] : 이미지의 세로
	 * @return 0: 성공 -3: 파일 이름이 유효하지 않음 -6: DLL(so,sl) 로드 실패 -7: DLL(so,sl) 모듈에 적절한
	 *         함수가 없음 -15: 지원되지 않는 파일 타입 그외: ImageIO, 변환서버 에러 코드 참조
	 */
	public native int getPageSize_FILE(String inputName, int pageNum, long[] size);

	/**
	 * 이미지 가로 세로를 추출하는 함수
	 * 
	 * @param inputData   [in] 이미지 메모리 데이터
	 * @param inputLength [in] 이미지 메모리 데이터 크기
	 * @param pageNum     [in] 추출할 이미지의 번호(TIFF 파일만 사용됨, 1부터 시작됨)
	 * @param size        [out] size[0] : 이미지의 가로 size[1] : 이미지의 세로
	 * @return 0: 성공 -5: 메모리가 유효하지 않음 -6: DLL(so,sl) 로드 실패 -7: DLL(so,sl) 모듈에 적절한
	 *         함수가 없음 -15: 지원되지 않는 파일 타입 그외: ImageIO, 변환서버 에러 코드 참조
	 */
	public native int getPageSize_Mem(byte[] inputData, int inputLength, int pageNum, long[] size);

	/**
	 * (deprecated) PDF2Image 모듈 이용
	 */
	public native int getPDFTotalPage_FILE(String inputName);

	/**
	 * (deprecated) PDF2Image 모듈 이용
	 */
	public native int getPDFTotalPage_Mem(byte[] inputData, int inputLength);

	/**
	 * (deprecated) PDF2Image 모듈 이용
	 */
	public native int extractPDF_FILE(String InputName, int pageNum, String OutputName);

	/**
	 * (deprecated) PDF2Image 모듈 이용
	 */
	public native int extractPDF_Mem(byte[] InputData, int InputLength, int pageNum, long[] aImgInfo);

	/**
	 * TIFF 파일에서 지정한 페이지의 압축 방식 리턴
	 * 
	 * @param inputName [in] 이미지의 파일이름
	 * @param pageNum   [in] 페이지 번호 (1 부터 시작됨)
	 * @return 1:NONE, 2:RLE, 3:G3, 4:G4, 5:LZW, 7:JPEG in TIFF, 34712 or
	 *         34713:JPEG2000 in TIFF -1: 파일 이름이 유효하지 않음 -2: DLL(so,sl) 로드 실패 -3: 파일
	 *         이름이 유효하지 않음 -4: DLL(so,sl) 모듈에 적절한 함수가 없음, Tag값을 추출할 수 없음 -6:
	 *         DLL(so,sl) 로드 실패 -7: DLL(so,sl) 모듈에 적절한 함수가 없음 그외: ImageIO, 변환서버 에러
	 *         코드 참조
	 * 
	 */
	public native int getPageCompType_FILE(String inputName, int pageNum);

	/**
	 * TIFF 이미지 메모리 데이터에서 지정한 페이지의 압축 방식 리턴
	 * 
	 * @param inputData   [in] 이미지 메모리
	 * @param inputLength [in] 이미지 메모리의 길이
	 * @param pageNum     [in] 페이지 번호 (1 부터 시작됨)
	 * @return 1:NONE, 2:RLE, 3:G3, 4:G4, 5:LZW, 7:JPEG in TIFF, 34712 or
	 *         34713:JPEG2000 in TIFF -1: 메모리가 유효하지 않음 -2: 이미지 크기는 0보다 커야함 -3:
	 *         DLL(so,sl) 로드 실패 -4: DLL(so,sl) 모듈에 적절한 함수가 없음 -5: Tag값을 추출할 수 없음 -6:
	 *         DLL(so,sl) 로드 실패 -7: DLL(so,sl) 모듈에 적절한 함수가 없음 그외: ImageIO, 변환서버 에러
	 *         코드 참조
	 */
	public native int getPageCompType_Mem(byte[] inputData, int inputLength, int pageNum);

	/**
	 * TIFF, PNG 파일에서 DPI값을 추출하는 함수
	 * 
	 * @param inputname [in] 이미지의 파일이름
	 * @param pagenum   [in] 추출할 이미지의 번호(TIFF 파일만 사용됨, 1부터 시작됨)
	 * @param dpi       [out] dpi[0] : 이미지의 X축 해상도 dpi[1] : 이미지의 Y축 해상도
	 * @return 0 : 성공 -1: 파일 이름이 유효하지 않음 -2: DLL(so,sl) 로드 실패 -3: 파일 이름이 유효하지 않음 -4:
	 *         DLL(so,sl) 모듈에 적절한 함수가 없음, Tag값을 추출할 수 없음 -6: DLL(so,sl) 로드 실패 -7:
	 *         DLL(so,sl) 모듈에 적절한 함수가 없음 -15: 지원하지 않는 형식의 이미지 그외: ImageIO, 변환서버 에러
	 *         코드 참조
	 * 
	 */
	public native int getPageDPI_FILE(String inputname, int pagenum, long[] dpi);

	/**
	 * TIFF, PNG 메모리에서 DPI값을 추출하는 함수
	 * 
	 * @param inputData   [in] 이미지 메모리 데이터
	 * @param inputLength [in] 이미지 메모리 데이터 크기
	 * @param pageNum     [in] 추출할 이미지의 번호(TIFF 파일만 사용됨, 1부터 시작됨)
	 * @param dpi         [out] dpi[0] : 이미지의 X축 해상도 dpi[1] : 이미지의 Y축 해상도
	 * @return 0: 성공 -1: 메모리가 유효하지 않음 -2: 메모리가 유효하지 않음 -3: DLL(so,sl) 로드 실패 -4:
	 *         DLL(so,sl) 모듈에 적절한 함수가 없음 -5: 메모리가 유효하지 않음, Tag값을 추출할 수 없음 -6:
	 *         DLL(so,sl) 로드 실패 -7: DLL(so,sl) 모듈에 적절한 함수가 없음 -15: 지원하지 않는 형식의 이미지
	 *         그외: ImageIO, 변환서버 에러 코드 참조
	 */
	public native int getPageDPI_Mem(byte[] inputData, int inputLength, int pageNum, long[] dpi);

	/**
	 * TIFF, PNG 파일에 DPI값을 설정하는 함수
	 * 
	 * @param inputname   [in] 이미지의 파일이름
	 * @param xResolution [in] 설정할 x축 해상도 값
	 * @param yResolution [in] 설정할 y축 해상도 값
	 * @return 0 : 성공 -1: 파일 이름이 유효하지 않음 -2: DLL(so,sl) 로드 실패, 메모리 할당에 실패 -3:
	 *         DLL(so,sl) 로드 실패, 파일 이름이 유효하지 않음 -4: DLL(so,sl) 모듈에 적절한 함수가 없음 -6:
	 *         DLL(so,sl) 로드 실패 -7: DLL(so,sl) 모듈에 적절한 함수가 없음 -10: 파일 이름이 유효하지 않음
	 *         -15: 지원하지 않는 형식의 이미지 그외: ImageIO, 변환서버 에러 코드 참조
	 * 
	 */
	public native int setImageDPI_FILE(String inputName, int xResolution, int yResolution);

	/**
	 * TIFF, PNG 메모리에 DPI값을 설정하는 함수(getImageByInfo_Mem 함수로 메모리 추출)
	 * 
	 * @param inputData   [in] 이미지 메모리 데이터
	 * @param inputLength [in] 이미지 메모리 데이터 크기
	 * @param xResolution [in] 설정할 x축 해상도 값
	 * @param yResolution [in] 설정할 y축 해상도 값
	 * @param aImgInfo    [out] 변환된 이미지의 정보, 0: pointer 주소, 1: 메모리 크기, 2: filetype
	 * @return 0: 성공 -1: 메모리가 유효하지 않음 -2: 메모리가 유효하지 않음, 메모리 할당에 실패 -3: DLL(so,sl) 로드
	 *         실패 -4: DLL(so,sl) 모듈에 적절한 함수가 없음 -5: 메모리가 유효하지 않음, Tag값을 추출할 수 없음 -6:
	 *         DLL(so,sl) 로드 실패 -7: DLL(so,sl) 모듈에 적절한 함수가 없음 -15: 지원하지 않는 형식의 이미지
	 *         그외: ImageIO, 변환서버 에러 코드 참조
	 */
	public native int setImageDPI_Mem(byte[] inputData, int inputLength, int xResolution, int yResolution,
			long[] aImgInfo);

	/**
	 * 지정한 페이지의 컬러 포맷 리턴
	 * 
	 * @param inputName [in] 이미지의 파일이름
	 * @param pageNum   [in] 페이지 번호 (TIFF만 사용됨, 1 부터 시작됨)
	 * @param format    [out] format[0] = (0: UNKNOWN, 1: BW, 2: GRAY, 3: COLOR)
	 *                  format[1] = bit per pixel
	 * @return 0: 성공 -3: 파일 이름이 유효하지 않음 -6: DLL(so,sl) 로드 실패 -7: DLL(so,sl) 모듈에 적절한
	 *         함수가 없음 -15: 지원되지 않는 파일 타입 그외: ImageIO, 변환서버 에러 코드 참조
	 */
	public native int getPageColorFormat_FILE(String inputName, int pageNum, long[] format);

	/**
	 * 지정한 페이지의 컬러 포맷 리턴
	 * 
	 * @param inputData   [in] 이미지 메모리
	 * @param inputLength [in] 이미지 메모리의 길이
	 * @param pageNum     [in] 페이지 번호 (TIFF만 사용됨, 1 부터 시작됨)
	 * @param format      [out] format[0] = (0: UNKNOWN, 1: BW, 2: GRAY, 3: COLOR)
	 *                    format[1] = bit per pixel
	 * @return 0: 성공 -5: 메모리가 유효하지 않음 -6: DLL(so,sl) 로드 실패 -7: DLL(so,sl) 모듈에 적절한
	 *         함수가 없음 -15: 지원되지 않는 파일 타입 그외: ImageIO, 변환서버 에러 코드 참조
	 */
	public native int getPageColorFormat_Mem(byte[] inputData, int inputLength, int pageNum, long[] format);

	/**
	 * Multi-TIFF 이미지에서 한장의 TIFF 이미지만 추출하는 함수
	 * 
	 * @param InputName  [in] Multi-TIFF파일 이름
	 * @param pageNum    [in] 추출된 TIFF 파일의 페이지번호(1 부터 시작됨)
	 * @param OutputName [out] 추출된 TIFF파일의 저장될 이름
	 * @return 0: 성공 -1: 파일 이름이 유효하지 않음 -2: 페이지번호는 1이상이어야 함 -3: 파일 이름이 유효하지 않음,
	 *         DLL(so,sl) 로드 실패 -4: DLL(so,sl) 모듈에 적절한 함수가 없음 -6: DLL(so,sl) 로드 실패
	 *         -7: DLL(so,sl) 모듈에 적절한 함수가 없음 그외: ImageIO, 변환서버 에러 코드 참조
	 * 
	 */
	public native int extractTIFF_FILE(String InputName, int pageNum, String OutputName);

	/**
	 * Multi-TIFF 이미지에서 한장의 TIFF 이미지만 추출하는 함수(getImageByInfo_Mem 함수로 메모리 추출)
	 * 
	 * @param InputData   [in] TIFF 이미지 메모리
	 * @param InputLength [in] TIFF 이미지 메모리의 길이
	 * @param pageNum     [in] 추출된 TIFF 파일의 index(1 부터 시작됨)
	 * @param aImgInfo    [out] 변환된 이미지의 정보, 0: pointer 주소, 1: 메모리 크기, 2: filetype
	 * @return 0: 성공 -1: 메모리가 유효하지 않음 -2: 페이지번호는 1이상이어야 함 -3: DLL(so,sl) 로드 실패 -4:
	 *         DLL(so,sl) 모듈에 적절한 함수가 없음 -5: 출력 메모리가 유효하지 않음 -6: DLL(so,sl) 로드 실패,
	 *         이미지 추출 실패(상세정보는 통합로그를 이용하세요) -7: DLL(so,sl) 모듈에 적절한 함수가 없음 그외:
	 *         ImageIO, 변환서버 에러 코드 참조
	 */
	public native int extractTIFF_Mem(byte[] InputData, int InputLength, int pageNum, long[] aImgInfo);

	/**
	 * TIFF 이미지를 병합하는 함수(파일)
	 * 
	 * @param InputName     [in] TIFF파일 이름
	 * @param FileNameToAdd [in] InputName에 병합할 TIFF 파일 이름
	 * @return 0: 성공 -1: 파일 이름이 유효하지 않음 -2: 병합할 파일 이름이 유효하지 않음 -3: 파일 이름이 유효하지 않음,
	 *         DLL(so,sl) 로드 실패 -4: DLL(so,sl) 모듈에 적절한 함수가 없음 -5: TIFF 병합 실패(상세정보는
	 *         통합로그를 이용하세요) -6: DLL(so,sl) 로드 실패 -7: DLL(so,sl) 모듈에 적절한 함수가 없음 그외:
	 *         ImageIO, 변환서버 에러 코드 참조
	 */
	public native int mergeTIFF_FILE(String InputName, String FileNameToAdd);

	/**
	 * TIFF 이미지를 병합하는 함수(getImageByInfo_Mem 함수로 메모리 추출)
	 * 
	 * @param InputData   [in] TIFF 이미지의 메모리
	 * @param InputLength [in] TIFF 이미지의 메모리 크기
	 * @param AddData     [in] InputData에 병합할 TIFF 이미지의 메모리
	 * @param AddLen      [in] InputData에 병합할 TIFF 이미지의 메모리 크기
	 * @param aImgInfo    [out] 변환된 이미지의 정보, 0: pointer 주소, 1: 메모리 크기, 2: filetype
	 * @return 0: 성공 -1: 메모리가 유효하지 않음 -2: 병합할 메모리가 유효하지 않음 -3: DLL(so,sl) 로드 실패 -4:
	 *         DLL(so,sl) 모듈에 적절한 함수가 없음 -5: TIFF 병합 실패(상세정보는 통합로그를 이용하세요) -6:
	 *         DLL(so,sl) 로드 실패 -7: DLL(so,sl) 모듈에 적절한 함수가 없음 그외: ImageIO, 변환서버 에러
	 *         코드 참조
	 */
	public native int mergeTIFF_Mem(byte[] InputData, int InputLength, byte[] AddData, int AddLen, long[] aImgInfo);

	/**
	 * TIFF 이미지에 Tag를 추가하는 함수(파일)
	 * 
	 * @param InputName    [in] TIFF파일 이름
	 * @param TagID        [in] InputName에 추가할 TIFF Tag ID
	 * @param TagDataType  [in] InputName에 추가할 TIFF Tag Data Type
	 * @param TagDataCount [in] InputName에 추가할 TIFF Tag Data 개수
	 * @param TagData      [in] InputName에 추가할 TIFF Tag Data
	 * @return 0: 성공 -2: 메모리 할당 실패 -3: 파일 이름이 유효하지 않음, DLL(so,sl) 로드 실패 -6:
	 *         DLL(so,sl) 로드 실패 -7: DLL(so,sl) 모듈에 적절한 함수가 없음 -8: TIFF 파일 열기 실패,
	 *         Tag가 추가된 TIFF 파일 생성 실패 그외: ImageIO, 변환서버 에러 코드 참조
	 */
	public native int addTIFFTag_FILE(String InputName, int TagID, int TagDataType, int TagDataCount, byte[] TagData);

	/**
	 * TIFF 이미지에 Tag를 추가하는 함수(메모리, getImageByInfo_Mem 함수로 메모리 추출)
	 * 
	 * @param InputData    [in] TIFF 이미지의 메모리
	 * @param InputLength  [in] TIFF 이미지의 메모리 크기
	 * @param TagID        [in] InputData 추가할 TIFF Tag ID
	 * @param TagDataType  [in] InputData 추가할 TIFF Tag Data Type
	 * @param TagDataCount [in] InputData 추가할 TIFF Tag Data 개수
	 * @param TagData      [in] InputData 추가할 TIFF Tag Data
	 * @param aImgInfo     [out] Tag가 추가된 이미지의 정보, 0: pointer 주소, 1: 메모리 크기, 2:
	 *                     filetype
	 * @return 0: 성공 -5: 입력 이미지가 NULL -6: DLL(so,sl) 로드 실패 -7: DLL(so,sl) 모듈에 적절한
	 *         함수가 없음 그외: ImageIO, 변환서버 에러 코드 참조
	 */
	public native int addTIFFTag_Mem(byte[] InputData, int InputLength, int TagID, int TagDataType, int TagDataCount,
			byte[] TagData, long[] aImgInfo);

	/**
	 * TIFF 이미지에서 Tag를 삭제하는 함수(파일)
	 * 
	 * @param InputName [in] TIFF파일 이름
	 * @param TagID     [in] InputName에서 삭제할 TIFF Tag ID(Tag를 잘못 삭제하는 경우 비정상 TIFF가 될
	 *                  수 있습니다.)
	 * @return 0: 성공 -6: DLL(so,sl) 로드 실패 -7: DLL(so,sl) 모듈에 적절한 함수가 없음 그외: ImageIO,
	 *         변환서버 에러 코드 참조
	 */
	public native int removeTIFFTag_FILE(String InputName, int TagID);

	/**
	 * TIFF 이미지에서 Tag를 삭제하는 함수(메모리, getImageByInfo_Mem 함수로 메모리 추출)
	 * 
	 * @param InputData   [in] TIFF 이미지의 메모리
	 * @param InputLength [in] TIFF 이미지의 메모리 크기
	 * @param TagID       [in] InputData 삭제할 TIFF Tag ID(Tag를 잘못 삭제하는 경우 비정상 TIFF가 될
	 *                    수 있습니다.)
	 * @param aImgInfo    [out] Tag가 삭제된 이미지의 정보, 0: pointer 주소, 1: 메모리 크기, 2:
	 *                    filetype
	 * @return 0: 성공 -6: DLL(so,sl) 로드 실패 -7: DLL(so,sl) 모듈에 적절한 함수가 없음 그외: ImageIO,
	 *         변환서버 에러 코드 참조
	 */
	public native int removeTIFFTag_Mem(byte[] InputData, int InputLength, int TagID, long[] aImgInfo);

	/**
	 * 입력된 이미지 파일을 특정 압축 파일로 저장
	 * 
	 * @param inputName     [in] 변환될 이미지 (BMP, JPEG, JPEG2000, TIFF, PNG 지원)
	 * @param filetype      [in] 변환된 이미지의 파일 형식 1: BMP, 2: JPEG, 3: JPEG2000, 4:
	 *                      TIFF, 6: IZT, 8: PNG
	 * @param comptype      [in] 변환된 이미지의 압축 형식(filetype이 TIFF일때만 사용됨) 1:NONE,
	 *                      2:RLE, 3:G3, 4:G4, 5:LZW, 7:JPEG in TIFF,
	 *                      34712(34713):JPEG2000 in TIFF
	 * @param targetSize    [in] 결과 파일 크기 (byte). fileFormat=JPEG2000 ||
	 *                      (fileFormat=TIFF && compType=JPEG2000)인 경우만 사용됨.
	 * @param targetRate    [in] 결과 파일 압축률 (0~1). fileFormat=JPEG2000 ||
	 *                      (fileFormat=TIFF && compType=JPEG2000)인 경우만 사용됨.
	 *                      (targetSize=0 인 경우에 사용됨.)
	 * @param qualityLevel  [in] 결과 파일 품질 (1~8: 커질 수록 고품질). fileFormat=JPEG ||
	 *                      (fileFormat=TIFF && compType=JPEG in TIFF)인 경우만 사용됨.
	 *                      (3.0.18.2 버전 부터 9 ~ 100도 사용 가능, JPEG 기본 압축률인 1 ~ 100 값이
	 *                      그대로 적용됨)
	 * @param resizePercent [in] 축소할 비율 (0~100. 0: 원본 그대로)
	 * @param targetWidth   [in] 축소 후 이미지 폭 (resizePercent=0 인 경우에 사용됨. 0: 원본 그대로)
	 * @param targetHeight  [in] 축소 후 이미지 높이 (resizePercent=0 인 경우에 사용됨. 0: 원본 그대로)
	 * @param outputName    [in] 변환된 이미지의 파일이름
	 * @return 0: 성공 -2: 메모리 할당 실패 -3: 파일 이름이 유효하지 않음, DLL(so,sl) 로드 실패 -4:
	 *         DLL(so,sl) 모듈에 적절한 함수가 없음 -5: 이미지 디코딩 실패(상세정보는 통합로그를 이용하세요) -6:
	 *         DLL(so,sl) 로드 실패 -7: DLL(so,sl) 모듈에 적절한 함수가 없음 -15: 지원되지 않는 이미지 -17:
	 *         압축률이 유효하지 않음 -96: 이미지 확대,축소 실패(상세정보는 통합로그를 이용하세요) 그외: ImageIO, 변환서버
	 *         에러 코드 참조
	 */
	public native int convertFormat_FILE(String inputName, int filetype, int comptype, int targetSize,
			double targetRate, int qualityLevel, int resizePercent, int targetWidth, int targetHeight,
			String outputName);

	/**
	 * 입력된 이미지 파일을 특정 압축 파일로 저장
	 * 
	 * @brief 입력된 이미지 파일을 특정 압축 파일로 저장. convertFormat_FILE에 DecodeParam 추가됨.
	 * @param decodeParam [in] 디코딩 옵션
	 * @param 나머지         파라미터는 convertFormat_FILE와 동일
	 * @return convertFormat_FILE와 동일
	 */
	public native int convertFormat_FILEEx(String inputName, int filetype, int comptype, int targetSize,
			double targetRate, int qualityLevel, int resizePercent, int targetWidth, int targetHeight,
			String outputName, String decodeParam);

	/**
	 * 입력된 이미지의 메모리를 특정 압축 형태의 메모리로 저장(getImageByInfo_Mem 함수로 메모리 추출)
	 * 
	 * @param inputData     [in] 변환될 이미지 데이터 (BMP, JPEG, JPEG2000, TIFF, PNG 지원)
	 * @param inputLength   [in] 변환될 이미지 데이터 크기
	 * @param filetype      [in] 변환된 이미지의 파일 형식 1: BMP, 2: JPEG, 3: JPEG2000, 4:
	 *                      TIFF, 6: IZT, 8: PNG
	 * @param comptype      [in] 변환된 이미지의 압축 형식(filetype이 TIFF일때만 사용됨) 1:NONE,
	 *                      2:RLE, 3:G3, 4:G4, 5:LZW, 7:JPEG in TIFF,
	 *                      34712(34713):JPEG2000 in TIFF
	 * @param targetSize    [in] 결과 파일 크기 (byte). fileFormat=JPEG2000 ||
	 *                      (fileFormat=TIFF && compType=JPEG2000)인 경우만 사용됨.
	 * @param targetRate    [in] 결과 파일 압축률 (0~1). fileFormat=JPEG2000 ||
	 *                      (fileFormat=TIFF && compType=JPEG2000)인 경우만 사용됨.
	 *                      (targetSize=0 인 경우에 사용됨.)
	 * @param qualityLevel  [in] 결과 파일 품질 (1~8: 커질 수록 고품질). fileFormat=JPEG ||
	 *                      (fileFormat=TIFF && compType=JPEG)인 경우만 사용됨. (3.0.18.2
	 *                      버전 부터 9 ~ 100도 사용 가능, JPEG 기본 압축률인 1 ~ 100 값이 그대로 적용됨)
	 * @param resizePercent [in] 축소할 비율 (0~100. 0: 원본 그대로)
	 * @param targetWidth   [in] 축소 후 이미지 폭 (resizePercent=0 인 경우에 사용됨. 0: 원본 그대로)
	 * @param targetHeight  [in] 축소 후 이미지 높이 (resizePercent=0 인 경우에 사용됨. 0: 원본 그대로)
	 * @param aImgInfo      [out] 변환된 이미지의 정보, 0: pointer 주소, 1: 메모리 크기, 2: filetype
	 * @return convertFormat_FILE와 동일
	 */
	public native int convertFormat_Mem(byte[] inputData, int inputLength, int filetype, int comptype, int targetSize,
			double targetRate, int qualityLevel, int resizePercent, int targetWidth, int targetHeight, long[] aImgInfo);

	/**
	 * 생성된 이미지를 메모리 형태로 가져온다.(모듈에서 생성된 이미지는 메모리 해제됨.)
	 * 
	 * @param aImgInfo   [in] 변환된 이미지의 정보, 0: pointer 주소, 1: 메모리 크기, 2: filetype
	 * @param outputData [out] 변환된 이미지의 메모리 (JAVA에서 메모리를 할당한 것)
	 * @return 0 성공 -6: DLL(so,sl) 로드 실패 -7: DLL(so,sl) 모듈에 적절한 함수가 없음
	 */
	public native int getImageByInfo_Mem(long[] aImgInfo, byte[] outputData);

	/**
	 * 추출된 Tag Data를 byte 형식으로 가져온다.(모듈에서 생성된 Tag Data 메모리 해제됨.)
	 * 
	 * @param aImgInfo    [in] 추출된 Tag Data 정보, 0: pointer 주소, 1: 메모리 크기, 2: Tag
	 *                    Data Type, 3: Tag Data 개수
	 * @param byteTagData [out] byte 형식의 Tag Data 메모리 (JAVA에서 메모리를 할당한 것,
	 *                    aImgInfo[1]의 크기로 할당)
	 * @return 0 성공 -6: DLL(so,sl) 로드 실패 -7: DLL(so,sl) 모듈에 적절한 함수가 없음 -109:
	 *         aImgInfo 정보와 byteTagData 정보가 일치하지 않음
	 */
	public native int getTIFFTagByteByInfo_Mem(long[] aTagInfo, byte[] byteTagData);

	/**
	 * 추출된 Tag Data를 long 형식으로 가져온다.(모듈에서 생성된 Tag Data 메모리 해제됨.) native 모듈에서
	 * unsigned short, unsigned int 값이 long으로 저장됨
	 * 
	 * @param aImgInfo    [in] 추출된 Tag Data 정보, 0: pointer 주소, 1: 메모리 크기, 2: Tag
	 *                    Data Type, 3: Tag Data 개수
	 * @param longTagData [out] long 형식의 Tag Data 메모리 (JAVA에서 메모리를 할당한 것,
	 *                    aImgInfo[3]의 크기로 할당)
	 * @return 0 성공 -6: DLL(so,sl) 로드 실패 -7: DLL(so,sl) 모듈에 적절한 함수가 없음 -109:
	 *         aImgInfo 정보와 longTagData 정보가 일치하지 않음
	 */
	public native int getTIFFTagLongByInfo_Mem(long[] aTagInfo, long[] longTagData);

	/**
	 * 6번 및 34664번 Tag를 가진 JPEG in TIFF 이미지를 7번 Tag의 JPEG in TIFF 또는 JPEG 이미지로 변환
	 * (JPEG 변환은 ImageIO 1.6.0.1버전 이상 사용시 가능)
	 * 
	 * @param inputname  [in] 6번 및 34664번 Tag의 JPEG in TIFF 파일 이름
	 * @param quality    [in] 압축률(JPEG (in TIFF) : 1.0 ~ 100.0)
	 * @param outputname [in] 저장할 7번 Tag의 JPEG in TIFF 파일 이름, 확장자를 jpg로 지정시 JPEG으로
	 *                   저장됨
	 * @return 0 : 성공 -2: 파일 읽기 실패 -5: 이미지 디코딩 실패(상세정보는 통합로그를 이용하세요) -6: DLL(so,sl)
	 *         로드 실패, Multi-TIFF 이미지는 사용 불가 -7: DLL(so,sl) 모듈에 적절한 함수가 없음 -11: 유효하지
	 *         않은 TIFF 파일 -22: 메모리 할당 실패 -23: 입력 이미지가 조건에 맞지 않음 그외: ImageIO, 변환서버 에러
	 *         코드 참조
	 */
	public native int inziJITConverter_FILE(String inputname, int quality, String outputname);

	/**
	 * 6번 및 34664번 Tag를 가진 JPEG in TIFF 이미지를 다른 형태의 이미지로 변환
	 * 
	 * @param inputname  [in] 6번 및 34664번 Tag의 JPEG in TIFF 파일 이름
	 * @param outputname [in] 변환된 이미지 이름
	 * @param quality    [in] 압축률(JPEG (in TIFF) : 1.0 ~ 100.0, JPEG2000 (in TIFF) :
	 *                   0 ~ 1.0)
	 * @param filetype   [in] 출력 이미지의 파일 타입(0으로 지정시 7번 Tag의 JPEG in tiff로 변환) 1:
	 *                   BMP, 2: JPEG, 3: JPEG2000, 4: TIFF, 8: PNG
	 * @param comptype   [in] 출력 이미지의 압축 타입(TIFF 이미지 일때만 적용) 1:NONE, 2:RLE, 3:G3,
	 *                   4:G4, 5:LZW, 7:JPEG in TIFF, 34712(34713):JPEG2000 in TIFF
	 * @return 0 : 성공 -2: 파일 읽기 실패 -3: 입력 파일이 유효하지 않음 -4: 라이선스 체크 실패 -6: DLL(so,sl)
	 *         로드 실패, Multi-TIFF 이미지는 사용 불가 -7: DLL(so,sl) 모듈에 적절한 함수가 없음 -10: 입력 파일
	 *         이름이 유효하지 않음 -15: 출력 형식이 유효하지 않음 -22: 메모리 할당 실패 -23: 입력 이미지가 조건에 맞지 않음
	 *         -31: 파일 생성 실패 그외: ImageIO, 변환서버 에러 코드 참조
	 */
	public native int convertinziJITFormat_FILE(String inputname, String outputname, double quality, int filetype,
			int comptype);

	/**
	 * convertinziJITFormat_FILE 메모리 버전(getImageByInfo_Mem 함수로 메모리 추출)
	 * 
	 * @param inputData   [in] 6번 및 34664번 Tag의 JPEG in TIFF 이미지 데이터
	 * @param inputLength [in] 6번 및 34664번 Tag의 JPEG in TIFF 이미지 데이터 크기
	 * @param aImgInfo    [out] 변환된 이미지의 정보, 0: pointer 주소, 1: 메모리 크기, 2: filetype
	 * @param quality     [in] 압축률(JPEG (in TIFF) : 1.0 ~ 100.0, JPEG2000 (in TIFF)
	 *                    : 0 ~ 1.0)
	 * @param filetype    [in] 출력 이미지의 파일 타입(0으로 지정시 7번 Tag의 JPEG in tiff로 변환) 1:
	 *                    BMP, 2: JPEG, 3: JPEG2000, 4: TIFF, 8: PNG
	 * @param comptype    [in] 출력 이미지의 압축 타입(TIFF 이미지 일때만 적용) 1:NONE, 2:RLE, 3:G3,
	 *                    4:G4, 5:LZW, 7:JPEG in TIFF, 34712(34713):JPEG2000 in TIFF
	 * @return 0 : 성공 -2: 파일 읽기 실패 -6: DLL(so,sl) 로드 실패, Multi-TIFF 이미지는 사용 불가 -7:
	 *         DLL(so,sl) 모듈에 적절한 함수가 없음 -22: 메모리 할당 실패 -23: 입력 이미지가 조건에 맞지 않음 -29:
	 *         유효하지 않은 메모리 그외: ImageIO, 변환서버 에러 코드 참조
	 */
	public native int convertinziJITFormat_Mem(byte[] inputData, int inputLength, long[] aImgInfo, double quality,
			int filetype, int comptype);

	/**
	 * 이미지를 입력 받아 crop하는 함수(별도 라이선스)
	 * 
	 * @param inputname  [in] crop할 이미지 이름
	 * @param startx     [in] crop 영역의 좌상단 x 좌표
	 * @param startx     [in] crop 영역의 좌상단 y 좌표
	 * @param cropwidth  [in] crop 영역의 가로 길이
	 * @param cropheight [in] crop 영역의 세로 길이
	 * @param outputname [in] crop된 이미지 이름
	 * @param quality    [in] 압축률(JPEG (in TIFF) : 1.0 ~ 100.0, JPEG2000 (in TIFF) :
	 *                   0 ~ 1.0)
	 * @param filetype   [in] 출력 이미지의 파일 타입(입력 이미지와 같게 하려면 0) 1: BMP, 2: JPEG, 3:
	 *                   JPEG2000, 4: TIFF, 8: PNG
	 * @param comptype   [in] 출력 이미지의 압축 타입(TIFF 이미지 일때만 적용) 1:NONE, 2:RLE, 3:G3,
	 *                   4:G4, 5:LZW, 7:JPEG in TIFF, 34712(34713):JPEG2000 in TIFF
	 * @return 0 : 성공 -3: 파일 이름이 유효하지 않음, 메모리가 유효하지 않음 -6: DLL(so,sl) 로드 실패,
	 *         Multi-TIFF 이미지는 사용 불가 -7: DLL(so,sl) 모듈에 적절한 함수가 없음 -10: ImageIO에서 문제
	 *         발생(상세정보는 통합로그를 이용하세요) -15: 결과 포맷 설정이 유효하지 않음 그외: ImageIO, 변환서버 에러 코드
	 *         참조
	 */
	public native int cropImage_FILE(String inputname, int startx, int starty, int cropwidth, int cropheight,
			String outputname, double quality, int filetype, int comptype);

	/**
	 * cropImage 메모리 버전, cropImage_FILE 설명 참조(getImageByInfo_Mem 함수로 메모리 추출)
	 * 
	 * @param inputData   [in] crop할 이미지 데이터
	 * @param inputLength [in] crop할 이미지 데이터 크기
	 * @param aImgInfo    [out] 변환된 이미지의 정보, 0: pointer 주소, 1: 메모리 크기, 2: filetype
	 */
	public native int cropImage_Mem(byte[] inputData, int inputLength, int startx, int starty, int cropwidth,
			int cropheight, long[] aImgInfo, double quality, int filetype, int comptype);

	/**
	 * 이미지를 입력 받아 masking하는 함수(별도 라이선스)
	 * 
	 * @param inputname  [in] masking할 이미지 이름
	 * @param startx     [in] masking 영역의 좌상단 x 좌표
	 * @param startx     [in] masking 영역의 좌상단 y 좌표
	 * @param maskwidth  [in] masking 영역의 가로 길이
	 * @param maskheight [in] masking 영역의 세로 길이
	 * @param outputname [in] masking된 이미지 이름
	 * @param quality    [in] 압축률(JPEG (in TIFF) : 1.0 ~ 100.0, JPEG2000 (in TIFF) :
	 *                   0 ~ 1.0)
	 * @param filetype   [in] 출력 이미지의 파일 타입(입력 이미지와 같게 하려면 0) 1: BMP, 2: JPEG, 3:
	 *                   JPEG2000, 4: TIFF, 8: PNG
	 * @param comptype   [in] 출력 이미지의 압축 타입(TIFF 이미지 일때만 적용) 1:NONE, 2:RLE, 3:G3,
	 *                   4:G4, 5:LZW, 7:JPEG in TIFF, 34712(34713):JPEG2000 in TIFF
	 * @return 0 : 성공 -3: 파일 이름이 유효하지 않음, 메모리가 유효하지 않음 -6: DLL(so,sl) 로드 실패,
	 *         Multi-TIFF 이미지는 사용 불가 -7: DLL(so,sl) 모듈에 적절한 함수가 없음 -15: 결과 포맷 설정이
	 *         유효하지 않음 -94: ImageUtil에서 문제 발생(상세정보는 통합로그를 이용하세요) 그외: ImageIO, 변환서버
	 *         에러 코드 참조
	 */
	public native int maskImage_FILE(String inputname, int startx, int starty, int maskwidth, int maskheight,
			String outputname, double quality, int filetype, int comptype);

	/**
	 * maskImage 메모리 버전, maskImage_FILE 설명 참조(getImageByInfo_Mem 함수로 메모리 추출)
	 * 
	 * @param inputData   [in] masking할 이미지 데이터
	 * @param inputLength [in] masking할 이미지 데이터 크기
	 * @param aImgInfo    [out] 변환된 이미지의 정보, 0: pointer 주소, 1: 메모리 크기, 2: filetype
	 */
	public native int maskImage_Mem(byte[] inputData, int inputLength, int startx, int starty, int maskwidth,
			int maskheight, long[] aImgInfo, double quality, int filetype, int comptype);

	/**
	 * 이미지를 입력 받아 회전하는 함수(별도 라이선스)
	 * 
	 * @param inputname  [in] 회전할 이미지 이름
	 * @param angle      [in] 회전 각도(시계방향 기준)
	 * @param outputname [in] 회전된 이미지 이름
	 * @param quality    [in] 압축률(JPEG (in TIFF) : 1.0 ~ 100.0, JPEG2000 (in TIFF) :
	 *                   0 ~ 1.0)
	 * @param filetype   [in] 출력 이미지의 파일 타입(입력 이미지와 같게 하려면 0) 1: BMP, 2: JPEG, 3:
	 *                   JPEG2000, 4: TIFF, 8: PNG
	 * @param comptype   [in] 출력 이미지의 압축 타입(TIFF 이미지 일때만 적용) 1:NONE, 2:RLE, 3:G3,
	 *                   4:G4, 5:LZW, 7:JPEG in TIFF, 34712(34713):JPEG2000 in TIFF
	 * @return 0 : 성공 -3: 파일 이름이 유효하지 않음, 메모리가 유효하지 않음 -6: DLL(so,sl) 로드 실패,
	 *         Multi-TIFF 이미지는 사용 불가 -7: DLL(so,sl) 모듈에 적절한 함수가 없음 -10: ImageIO에서 문제
	 *         발생(상세정보는 통합로그를 이용하세요) -15: 결과 포맷 설정이 유효하지 않음 -94: ImageUtil에서 문제
	 *         발생(상세정보는 통합로그를 이용하세요) 그외: ImageIO, 변환서버 에러 코드 참조
	 */
	public native int rotateImage_FILE(String inputname, int angle, String outputname, double quality, int filetype,
			int comptype);

	/**
	 * rotateImage 메모리 버전, rotateImage_FILE 설명 참조(getImageByInfo_Mem 함수로 메모리 추출)
	 * 
	 * @param inputData   [in] 회전할 이미지 데이터
	 * @param inputLength [in] 회전할 이미지 데이터 크기
	 * @param aImgInfo    [out] 변환된 이미지의 정보, 0: pointer 주소, 1: 메모리 크기, 2: filetype
	 */
	public native int rotateImage_Mem(byte[] inputData, int inputLength, int angle, long[] aImgInfo, double quality,
			int filetype, int comptype);

	/**
	 * 이미지를 입력 받아 확대 축소 하는 함수(별도 라이선스)
	 * 
	 * @param inputname    [in] 확대 축소할 이미지 이름
	 * @param scaledwidth  [in] 확대 축소후 가로 길이
	 * @param scaledheight [in] 확대 축소후 세로 길이
	 * @param outputname   [in] 확대 축소된 이미지 이름
	 * @param quality      [in] 압축률(JPEG (in TIFF) : 1.0 ~ 100.0, JPEG2000 (in TIFF)
	 *                     : 0 ~ 1.0)
	 * @param filetype     [in] 출력 이미지의 파일 타입(입력 이미지와 같게 하려면 0) 1: BMP, 2: JPEG, 3:
	 *                     JPEG2000, 4: TIFF, 8: PNG
	 * @param comptype     [in] 출력 이미지의 압축 타입(TIFF 이미지 일때만 적용) 1:NONE, 2:RLE, 3:G3,
	 *                     4:G4, 5:LZW, 7:JPEG in TIFF, 34712(34713):JPEG2000 in
	 *                     TIFF
	 * @return 0 : 성공 -3: 파일 이름이 유효하지 않음, 메모리가 유효하지 않음 -6: DLL(so,sl) 로드 실패,
	 *         Multi-TIFF 이미지는 사용 불가 -7: DLL(so,sl) 모듈에 적절한 함수가 없음 -10: ImageIO에서 문제
	 *         발생(상세정보는 통합로그를 이용하세요) -15: 결과 포맷 설정이 유효하지 않음 -94: ImageUtil에서 문제
	 *         발생(상세정보는 통합로그를 이용하세요) 그외: ImageIO, 변환서버 에러 코드 참조
	 */
	public native int scaleImage_FILE(String inputname, int scaledwidth, int scaledheight, String outputname,
			double quality, int filetype, int comptype);

	/**
	 * scaleImage 메모리 버전, scaleImage_FILE 설명 참조(getImageByInfo_Mem 함수로 메모리 추출)
	 * 
	 * @param inputData   [in] 확대 축소할 이미지 데이터
	 * @param inputLength [in] 확대 축소할 이미지 데이터 크기
	 * @param aImgInfo    [out] 변환된 이미지의 정보, 0: pointer 주소, 1: 메모리 크기, 2: filetype
	 */
	public native int scaleImage_Mem(byte[] inputData, int inputLength, int scaledwidth, int scaledheight,
			long[] aImgInfo, double quality, int filetype, int comptype);

	/**
	 * 텍스트를 입력받아 이미지에 overlay하는 함수(별도 라이선스)
	 *
	 * @param inputname  [in] text가 overlay될 이미지 이름
	 * @param text       [in] 이미지에 overlay될 text
	 * @param fontpath   [in] text에 적용될 폰트 파일 경로
	 * @param fontindex  [in] 폰트 파일에 인덱스(ttf : 0, ttc : 0 ~ N)
	 * @param fontsize   [in] text 글자 크기
	 * @param charspace  [in] 자간(기본값 0, -30픽셀 ~ 300픽셀)
	 * @param horscale   [in] 장평(기본값 100, 10% ~ 300%)
	 * @param xpos       [in] 입력할 text의 좌하단이 위치할 x좌표
	 * @param ypos       [in] 입력할 text의 좌하단이 위치할 y좌표
	 * @param r          [in] text색의 r값
	 * @param g          [in] text색의 g값
	 * @param b          [in] text색의 b값
	 * @param alpha      [in] text의 알파값(0.0 : 완전 투명, 1.0 : 완전 불투명, 8bit 이상의 이미지만
	 *                   적용됨)
	 * @param resolution [in] text이미지의 dpi(resolution이 72이고, fontsize가 30이면 이미지가
	 *                   72dpi로 보여질때 text의 크기가 약30픽셀)
	 * @param outputname [in] text가 overlay된 이미지 이름
	 * @param quality    [in] 압축률(JPEG (in TIFF) : 1.0 ~ 100.0, JPEG2000 (in TIFF) :
	 *                   0 ~ 1.0)
	 * @param filetype   [in] 출력 이미지의 파일 타입(입력 이미지와 같게 하려면 0) 1: BMP, 2: JPEG, 3:
	 *                   JPEG2000, 4: TIFF, 8: PNG
	 * @param comptype   [in] 출력 이미지의 압축 타입(TIFF 이미지 일때만 적용) 1:NONE, 2:RLE, 3:G3,
	 *                   4:G4, 5:LZW, 7:JPEG in TIFF, 34712(34713):JPEG2000 in TIFF
	 * @return 0 : 성공 -3: 파일 이름이 유효하지 않음, 메모리가 유효하지 않음 -5: 입력텍스트 또는 폰트 경로가 유효하지 않음
	 *         -6: DLL(so,sl) 로드 실패, Multi-TIFF 이미지는 사용 불가 -7: DLL(so,sl) 모듈에 적절한
	 *         함수가 없음 -15: 결과 포맷 설정이 유효하지 않음 -95: IText에서 문제 발생(상세정보는 통합로그를 이용하세요)
	 *         그외: ImageIO, 변환서버 에러 코드 참조
	 */
	public native int addTexttoImage_FILE(String inputname, String text, String fontpath, int fontindex, int fontsize,
			int charspace, int horscale, int xpos, int ypos, int r, int g, int b, float alpha, int resolution,
			String outputname, double quality, int filetype, int comptype);

	/**
	 * addTexttoImage 메모리 버전, addTexttoImage_FILE 설명 참조(getImageByInfo_Mem 함수로 메모리
	 * 추출)
	 * 
	 * @param inputData   [in] Text가 overlay될 이미지 데이터
	 * @param inputLength [in] Text가 overlay될 이미지 데이터 크기
	 * @param aImgInfo    [out] 변환된 이미지의 정보, 0: pointer 주소, 1: 메모리 크기, 2: filetype
	 */
	public native int addTexttoImage_Mem(byte[] inputData, int inputLength, String text, String fontpath, int fontindex,
			int fontsize, int charspace, int horscale, int xpos, int ypos, int r, int g, int b, float alpha,
			int resolution, long[] aImgInfo, double quality, int filetype, int comptype);

	/**
	 * 이미지를 입력 받아 썸네일 이미지를 만드는 함수(별도 라이선스)
	 * 
	 * @param inputname   [in] 썸네일을 만들 이미지 이름 (JPEG만 지원)
	 * @param framewidth  [in] 썸네일이 로드될 프레임의 가로 길이
	 * @param frameheight [in] 썸네일이 로드될 프레임의 세로 길이
	 * @param framemode   [in] 썸네일 생성 방식, 0: 프레임 가로 및 세로에 이미지를 맞춤(가로 세로 비율 고정 되지
	 *                    않음), 1: 프레임 장축 기준으로 썸네일 생성, 2: 프레임 단축 기준으로 썸네일 생성, 3: 이미지
	 *                    장축 기준으로 썸네일 생성, 4: 이미지 단축 기준으로 썸네일 생성
	 * @param outputname  [in] 생성된 썸네일 이름
	 * @param quality     [in] 압축률(JPEG (in TIFF) : 1.0 ~ 100.0, JPEG2000 (in TIFF)
	 *                    : 0 ~ 1.0)
	 * @param filetype    [in] 출력 이미지의 파일 타입(입력 이미지와 같게 하려면 0) 1: BMP, 2: JPEG, 3:
	 *                    JPEG2000, 4: TIFF, 8: PNG
	 * @param comptype    [in] 출력 이미지의 압축 타입(TIFF 이미지 일때만 적용) 1:NONE, 2:RLE, 5:LZW,
	 *                    7:JPEG in TIFF, 34712(34713):JPEG2000 in TIFF
	 * @return 0 : 성공 -3: 파일 이름이 유효하지 않음, 메모리가 유효하지 않음 -6: DLL(so,sl) 로드 실패,
	 *         Multi-TIFF 이미지는 사용 불가 -7: DLL(so,sl) 모듈에 적절한 함수가 없음 -10: ImageIO에서 문제
	 *         발생(상세정보는 통합로그를 이용하세요) -15: 결과 포맷 설정이 유효하지 않음, 입력이미지가 JPEG이 아님 -94:
	 *         ImageUtil에서 문제 발생(상세정보는 통합로그를 이용하세요) 그외: ImageIO, 변환서버 에러 코드 참조
	 */
	public native int fastImagetoThumbnail_FILE(String inputname, int framewidth, int frameheight, int framemode,
			String outputname, double quality, int filetype, int comptype);

	/**
	 * fastImagetoThumbnail 메모리 버전, fastImagetoThumbnail_FILE 설명
	 * 참조(getImageByInfo_Mem 함수로 메모리 추출)
	 * 
	 * @param inputData   [in] 썸네일을 만들 이미지 데이터 (JPEG만 지원)
	 * @param inputLength [in] 썸네일을 만들 이미지 데이터 크기
	 * @param aImgInfo    [out] 썸네일 이미지의 정보, 0: pointer 주소, 1: 메모리 크기, 2: filetype
	 */
	public native int fastImagetoThumbnail_Mem(byte[] inputData, int inputLength, int framewidth, int frameheight,
			int framemode, long[] aImgInfo, double quality, int filetype, int comptype);

	/**
	 * (Multi)TIFF 이미지 데이터에서 특정 Tag를 추출하는 함수(getTIFFTagBytebyInfo_Mem,
	 * getTIFFTagLongbyInfo_Mem 함수로 메모리 추출)
	 * 
	 * @param inputData   [in] Tag를 추출할 TIFF 이미지 데이터
	 * @param inputLength [in] Tag를 추출할 TIFF 이미지 데이터 크기
	 * @param pageNum     [in] Tag를 추출할 TIFF 이미지 페이지 번호(1 부터 시작, 싱글 TIFF 인 경우 1로 설정)
	 * @param TagID       [in] 추출 할 Tag 번호
	 * @param aTagInfo    [out] 추출된 Tag Data 정보, 0: pointer 주소, 1: 메모리 크기, 2: Tag
	 *                    Data Type, 3: Tag Data 개수
	 * @return 0: 성공 -6: DLL(so,sl) 로드 실패 -7: DLL(so,sl) 모듈에 적절한 함수가 없음 -33: Tag가 없음
	 *         -109: 입력된 파라미터중 범위를 벗어나는 값이 있음 그외: ImageIO, 변환서버 에러 코드 참조
	 */
	public native int fastGetTIFFTag_Mem(byte[] inputData, int inputLength, int pageNum, int TagID, long[] aTagInfo);

	/**
	 * (Multi)TIFF 이미지 데이터에 특정 Tag를 추가하는 함수(메모리, getImageByInfo_Mem 함수로 메모리 추출) %
	 * Tag를 잘못 추가할 경우 이미지가 정상적으로 보이지 않을 수 있습니다.
	 * 
	 * @param inputData    [in] Tag를 추가할 TIFF 이미지의 메모리
	 * @param inputLength  [in] Tag를 추가할 TIFF 이미지의 메모리 크기
	 * @param pageNum      [in] Tag를 추가할 TIFF 이미지의 페이지 번호(1 부터 시작, 싱글 TIFF인 경우 1로
	 *                     설정)
	 * @param TagID        [in] inputData에 추가할 TIFF Tag ID
	 * @param TagDataType  [in] inputData에 추가할 TIFF Tag Data Type (1 : byte, 2 :
	 *                     ASCII, 3 : unsigned short, 4 : unsigned int) ASCII의 경우
	 *                     배열의 마지막 값이 NULL(0) 이어야 함
	 * @param TagDataCount [in] inputData에 추가할 TIFF Tag Data 개수
	 * @param TagData      [in] inputData에 추가할 TIFF Tag Data
	 * @param TagDataSize  [in] inputData에 추가할 TIFF Tag Data 크기
	 * @param aImgInfo     [out] Tag가 추가된 이미지의 정보, 0: pointer 주소, 1: 메모리 크기, 2:
	 *                     filetype
	 * @return 0: 성공 -6: DLL(so,sl) 로드 실패 -7: DLL(so,sl) 모듈에 적절한 함수가 없음 -28: 입력된 Tag
	 *         타입과 크기 등이 맞지 않음 -78: 입력된 버퍼와 버퍼 크기가 다른 경우 -109: 입력된 파라미터중 범위를 벗어나는 값이
	 *         있음 그외: ImageIO, 변환서버 에러 코드 참조
	 */
	public native int fastAddTIFFTag_Mem(byte[] inputData, int inputLength, int pageNum, int TagID, int TagDataType,
			int TagDataCount, byte[] TagData, int TagDataSize, long[] aImgInfo);

	/**
	 * (Multi)TIFF 이미지 데이터에 특정 Tag를 변경하는 함수(메모리) 입력 이미지 메모리에 변경된 데이터가 반영됨, 기존 Tag
	 * 데이타 보다 작거나 같은 경우만 가능 % Tag를 잘못 변경할 경우 이미지가 정상적으로 보이지 않을 수 있습니다.
	 * 
	 * @param inputData    [in] Tag를 변경할 TIFF 이미지의 메모리
	 * @param inputLength  [in] Tag를 변경할 TIFF 이미지의 메모리 크기
	 * @param pageNum      [in] Tag를 변경할 TIFF 이미지의 페이지 번호(1 부터 시작, 싱글 TIFF인 경우 1로
	 *                     설정)
	 * @param TagID        [in] inputData에서 변경할 TIFF Tag ID
	 * @param TagDataType  [in] inputData에서 변경할 TIFF Tag Data Type (1 : byte, 2 :
	 *                     ASCII, 3 : unsigned short, 4 : unsigned int) ASCII의 경우
	 *                     배열의 마지막 값이 NULL(0) 이어야 함
	 * @param TagDataCount [in] inputData에서 변경할 TIFF Tag Data 개수
	 * @param TagData      [in] inputData에서 변경할 TIFF Tag Data
	 * @param TagDataSize  [in] inputData에서 변경할 TIFF Tag Data 크기
	 * @return 0: 성공 -6: DLL(so,sl) 로드 실패 -7: DLL(so,sl) 모듈에 적절한 함수가 없음 -28: 입력된 Tag
	 *         타입과 크기 등이 맞지 않음 -78: 입력된 버퍼와 버퍼 크기가 다른 경우 -109: 입력된 파라미터중 범위를 벗어나는 값이
	 *         있음 그외: ImageIO, 변환서버 에러 코드 참조
	 */
	public native int fastReplaceTIFFTag_Mem(byte[] inputData, int inputLength, int pageNum, int TagID, int TagDataType,
			int TagDataCount, byte[] TagData, int TagDataSize);

	/**
	 * (Multi)TIFF 이미지 데이터에서 특정 Tag를 삭제하는 함수(메모리) 입력 이미지 메모리에 변경된 데이터가 반영됨 % Tag를 잘못
	 * 삭제할 경우 이미지가 정상적으로 보이지 않을 수 있습니다.
	 * 
	 * @param inputData   [in] Tag를 삭제할 TIFF 이미지의 메모리
	 * @param inputLength [in] Tag를 삭제할 TIFF 이미지의 메모리 크기
	 * @param pageNum     [in] Tag를 삭제할 TIFF 이미지의 페이지 번호(1 부터 시작, 싱글 TIFF인 경우 1로 설정)
	 * @param TagID       [in] inputData에서 삭제할 TIFF Tag ID
	 * @return 0: 성공 -6: DLL(so,sl) 로드 실패 -7: DLL(so,sl) 모듈에 적절한 함수가 없음 -109: 입력된
	 *         파라미터중 범위를 벗어나는 값이 있음 그외: ImageIO, 변환서버 에러 코드 참조
	 */
	public native int fastRemoveTIFFTag_Mem(byte[] inputData, int inputLength, int pageNum, int TagID);

	/**
	 * TIFF 파일의 Endian을 변경하는 함수(II -> MM, MM -> II)
	 *
	 * @param inputname  [in] TIFF 파일 이름(멀티 TIFF 가능)
	 * @param outputname [in] Endian이 변경된 TIFF 파일 이름
	 * @return 0 : 성공 -3: DLL(so,sl) 로드 실패 -4: DLL(so,sl) 모듈에 적절한 함수가 없음 -6:
	 *         DLL(so,sl) 로드 실패 -7: DLL(so,sl) 모듈에 적절한 함수가 없음 -10: 파일 이름이 유효하지 않음
	 *         그외: ImageIO, 변환서버 에러 코드 참조
	 */
	public native int changeEndian_FILE(String inputname, String outputname);

	/**
	 * TIFF 이미지 데이터의 Endian을 변경하는 함수(II -> MM, MM -> II), (getImageByInfo_Mem 함수로
	 * 메모리 추출)
	 *
	 * @param inputData   [in] TIFF 이미지 데이터(멀티 TIFF 가능)
	 * @param inputLength [in] TIFF 이미지 데이터 크기
	 * @param aImgInfo    [out] Endian이 변환된 이미지의 정보, 0: pointer 주소, 1: 메모리 크기, 2:
	 *                    filetype
	 * @return 0 : 성공 -3: DLL(so,sl) 로드 실패 -4: DLL(so,sl) 모듈에 적절한 함수가 없음 -6:
	 *         DLL(so,sl) 로드 실패 -7: DLL(so,sl) 모듈에 적절한 함수가 없음 -28: 메모리가 유효하지 않음 그외:
	 *         ImageIO, 변환서버 에러 코드 참조
	 */
	public native int changeEndian_Mem(byte[] inputData, int inputLength, long[] aImgInfo);

	/**
	 * 이미지 파일의 컬러수를 변경하는 함수
	 *
	 * @param inputname  [in] 이미지 파일 이름
	 * @param targetbpp  [in] 변경할 컬러수(1: 흑백, 8: 회색조, 24: 컬러)
	 * @param changemode [in] 변경 옵션(1: 속도 빠름 화질 보통, 2: 속도 보통 화질 좋음)
	 * @param outputname [in] 컬러수가 변경된 파일 이름
	 * @param quality    [in] 압축률(JPEG (in TIFF) : 1.0 ~ 100.0, JPEG2000 (in TIFF) :
	 *                   0 ~ 1.0)
	 * @param filetype   [in] 출력 이미지의 파일 타입(입력 이미지와 같게 하려면 0) 1: BMP, 2: JPEG, 3:
	 *                   JPEG2000, 4: TIFF, 8: PNG
	 * @param comptype   [in] 출력 이미지의 압축 타입(TIFF 이미지 일때만 적용) 1:NONE, 2:RLE, 3:G3,
	 *                   4:G4, 5:LZW, 7:JPEG in TIFF, 34712(34713):JPEG2000 in TIFF
	 * @return 0 : 성공 -3: DLL(so,sl) 로드 실패 -4: 라이선스 체크 실패, DLL(so,sl) 모듈에 적절한 함수가 없음
	 *         -6: DLL(so,sl) 로드 실패 -7: DLL(so,sl) 모듈에 적절한 함수가 없음 -15: 유효하지 않은 파일
	 *         타입, 결과 포맷 설정이 유효하지 않음 -76: 컬러수가 유효하지 않음 그외: ImageIO, 변환서버 에러 코드 참조
	 */
	public native int changeColor_FILE(String inputname, int targetbpp, int changemode, String outputname,
			double quality, int filetype, int comptype);

	/**
	 * 이미지 메모리의 컬러수를 변경하는 함수(getImageByInfo_Mem 함수로 메모리 추출)
	 *
	 * @param inputData   [in] 이미지 메모리
	 * @param inputLength [in] 이미지 메모리 크기
	 * @param targetbpp   [in] 변경할 컬러수(1: 흑백, 8: 회색조, 24: 컬러)
	 * @param changemode  [in] 변경 옵션(1: 속도 빠름 화질 보통, 2: 속도 보통 화질 좋음)
	 * @param aImgInfo    [out] 컬러수가 변경된 이미지의 정보, 0: pointer 주소, 1: 메모리 크기, 2:
	 *                    filetype
	 * @param quality     [in] 압축률(JPEG (in TIFF) : 1.0 ~ 100.0, JPEG2000 (in TIFF)
	 *                    : 0 ~ 1.0)
	 * @param filetype    [in] 출력 이미지의 파일 타입(입력 이미지와 같게 하려면 0) 1: BMP, 2: JPEG, 3:
	 *                    JPEG2000, 4: TIFF, 8: PNG
	 * @param comptype    [in] 출력 이미지의 압축 타입(TIFF 이미지 일때만 적용) 1:NONE, 2:RLE, 3:G3,
	 *                    4:G4, 5:LZW, 7:JPEG in TIFF, 34712(34713):JPEG2000 in TIFF
	 * @return 0 : 성공 -3: DLL(so,sl) 로드 실패 -4: 라이선스 체크 실패, DLL(so,sl) 모듈에 적절한 함수가 없음
	 *         -6: DLL(so,sl) 로드 실패 -7: DLL(so,sl) 모듈에 적절한 함수가 없음 -15: 유효하지 않은 파일
	 *         타입, 결과 포맷 설정이 유효하지 않음 -76: 컬러수가 유효하지 않음 그외: ImageIO, 변환서버 에러 코드 참조
	 */
	public native int changeColor_Mem(byte[] inputData, int inputLength, int targetbpp, int changemode, long[] aImgInfo,
			double quality, int filetype, int comptype);

	/**
	 * Fax 이미지를 생성하는 함수
	 *
	 * @param inputname     [in] fax 이미지로 변환 될 파일 이름
	 * @param faximagemode  [in] fax 이미지생성 모드, 0: standard, 1: fine
	 * @param binarymode    [in] 이진화 모드 ( 1 ~ 3)
	 * @param outputfaxname [in] 생성된 fax 이미지 파일 이름
	 * 
	 * @return 0 : 성공 -5 : 이미지 데이터 생성 실패 -6: DLL(so,sl) 로드 실패 -7: DLL(so,sl) 모듈에 적절한
	 *         함수가 없음 -97 : 이진화 과정 실패 -102 : 이미지 핸들이 유효하지 않음 -103 : 이미지 정보 추출 실패
	 *         -104 : 이미지 생성모드가 유효하지 않음 그외: ImageIO, 변환서버 에러 코드 참조
	 */
	public native int generateFaxImage_FILE(String inputname, int faximagemode, int binarymode, String outputfaxname);

	/**
	 * Fax 이미지를 생성하는 함수(getImageByInfo_Mem 함수로 메모리 추출)
	 *
	 * @param inputdata     [in] 이미지 메모리 데이타
	 * @param inputdatasize [in] 이미지 메모리 데이타 사이즈
	 * @param faximagemode  [in] fax 이미지생성 모드, 0: standard, 1: fine
	 * @param binarymode    [in] 이진화 모드 (1 ~ 3)
	 * @param aImgInfo      [out] Fax 이미지의 정보, 0: pointer 주소, 1: 메모리 크기, 2: filetype
	 * 
	 * @return generateFaxImage_FILE함수 참조
	 */
	public native int generateFaxImage_Mem(byte[] inputData, int inputLength, int faximagemode, int binarymode,
			long[] aImgInfo);

	/**
	 * 이미지를 입력받아 HDIB handle을 리턴하는 함수(사용후 freeI2IHDIB함수로 해제 필요)
	 *
	 * @param inputname [in] 이미지 파일 이름
	 * @return 0 : 실패 그외 : 성공(HDIB handle 리턴)
	 */
	public native long getHDIB_FILE(String inputname);

	/**
	 * getHDIB 메모리 버전, getHDIB_FILE 설명 참조(사용후 freeI2IHDIB함수로 해제 필요)
	 *
	 * @param inputData   [in] 이미지 데이터
	 * @param inputLength [in] 이미지 데이터 크기
	 * @return 0 : 실패 그외 : 성공(HDIB handle 리턴)
	 */
	public native long getHDIB_Mem(byte[] inputData, int inputLength);

	/**
	 * HDIB 데이터를 이미지로 압축하여 저장하는 함수
	 *
	 * @param inputValue [in] HDIB handle
	 * @param outputname [in] 저장될 이미지 파일 이름
	 * @param quality    [in] 압축률(JPEG (in TIFF) : 1.0 ~ 100.0, JPEG2000 (in TIFF) :
	 *                   0~ 1.0)
	 * @param filetype   [in] 출력 이미지의 파일 타입(입력 이미지와 같게 하려면 0) 1: BMP, 2: JPEG, 3:
	 *                   JPEG2000, 4: TIFF, 8: PNG
	 * @param comptype   [in] 출력 이미지의 압축 타입(TIFF 이미지 일때만 적용) 1:NONE, 2:RLE, 3:G3,
	 *                   4:G4, 5:LZW, 7:JPEG in TIFF, 34712(34713):JPEG2000 in TIFF
	 * @return 0 : 성공 -3: 파일 이름이 유효하지 않음 -5: HDIB handle이 NULL -6: DLL(so,sl) 로드 실패
	 *         -7: DLL(so,sl) 모듈에 적절한 함수가 없음 -15: 결과 포맷 설정이 유효하지 않음 그외 : ImageIO,
	 *         변환서버 에러 코드 참조
	 */
	public native int saveHDIB_FILE(long inputValue, String outputname, double quality, int filetype, int comptype);

	/**
	 * HDIB 데이터를 이미지로 압축하여 저장하는 함수, 메모리 버전(getImageByInfo_Mem 함수로 메모리 추출)
	 *
	 * @param inputValue [in] HDIB handle
	 * @param aImgInfo   [out] 변환된 이미지의 정보, 0: pointer 주소, 1: 메모리 크기, 2: filetype
	 * @param quality    [in] 압축률(JPEG (in TIFF) : 1.0 ~ 100.0, JPEG2000 (in TIFF) :
	 *                   0 ~ 1.0)
	 * @param filetype   [in] 출력 이미지의 파일 타입(입력 이미지와 같게 하려면 0) 1: BMP, 2: JPEG, 3:
	 *                   JPEG2000, 4: TIFF, 8: PNG
	 * @param comptype   [in] 출력 이미지의 압축 타입(TIFF 이미지 일때만 적용) 1:NONE, 2:RLE, 3:G3,
	 *                   4:G4, 5:LZW, 7:JPEG in TIFF, 34712(34713):JPEG2000 in TIFF
	 * @return 0 : 성공 -3: 메모리가 유효하지 않음 -5: HDIB handle이 NULL -6: DLL(so,sl) 로드 실패
	 *         -7: DLL(so,sl) 모듈에 적절한 함수가 없음 -15: 결과 포맷 설정이 유효하지 않음 그외 : ImageIO,
	 *         변환서버 에러 코드 참조
	 */
	public native int saveHDIB_Mem(long inputValue, long[] aImgInfo, double quality, int filetype, int comptype);

	/**
	 * HDIB 메모리를 해제하는 함수
	 *
	 * @param inputValue [in] HDIB handle
	 * @return 0 : 성공 -5: HDIB handle이 NULL -6: DLL(so,sl) 로드 실패 -7: DLL(so,sl) 모듈에
	 *         적절한 함수가 없음 그외 : ImageIO, 변환서버 에러 코드 참조
	 */
	public native int freeI2IHDIB(long inputValue);

	/**
	 * JPEG 디코딩시 EXIF 정보에서 Orientation 값을 사용 할지 설정하는 함수 전역 설정이므로 모든 쓰레드에 적용됨
	 * 
	 * @return 0 : 성공
	 */
	public native int onJPEGOrientation();

	public native int offJPEGOrientation();

	/**
	 * 비정상적으로 생성된 이미지를 표준 상태 이미지로 수정하는 함수 repairImage_FILE 함수는 단일 이미지만 수정 가능
	 * repairImage_FILEEx 함수는 단일 이미지 또는 Multi TIFF도 수정 가능 현재 TIFF의 2가지 경우에 대한 수정 가능
	 *
	 * @param inputname  [in] 비정상 이미지로 의심 되는 이미지 파일이름
	 * @param pagenum    [in] 수정할 이미지의 페이지 번호 TIFF : 1 부터 시작, 0일 경우 전체 이미지 수정, 단일
	 *                   이미지의 경우 1 입력된
	 * @param repairmode [in] 비정상 이미지 패턴(0 일 경우 모든 패턴을 조사) 1 : JPEG in TIFF 이미지중 그레이
	 *                   이미지 이면서 photometric 값이 RGB로 잘못 설정된 파일 2 : JPEG2000 in TIFF
	 *                   이미지중 1bit 무압축 형식으로 저장되는 파일
	 * @param outputname [in] 비정상 이미지가 수정되는 경우 저장되는 파일이름
	 * @return 양수 : 성공(repairmode 값이 리턴됨) -2: 메모리 할당 실패 -4: 라이선스 체크 실패 -5: HDIB
	 *         handle이 NULL -6: DLL(so,sl) 로드 실패 -7: DLL(so,sl) 모듈에 적절한 함수가 없음 -8:
	 *         파일 오픈 실패 -10: ImageIO에서 문제 발생(상세정보는 통합로그를 이용하세요) -107: 수정가능한 비정상 이미지
	 *         패턴을 포함하지 않은 이미지 그외 : ImageIO, 변환서버 에러 코드 참조
	 */
	public native int repairImage_FILE(String inputname, int repairmode, String outputname);

	public native int repairImage_FILEEx(String inputname, int pagenum, int repairmode, String outputname);

	/**
	 * 비정상적으로 생성된 이미지를 표준 상태 이미지로 수정하는 함수(getImageByInfo_Mem 함수로 메모리 추출)
	 * repairImage_Mem 함수는 단일 이미지만 수정 가능 repairImage_MemEx 함수는 단일 이미지 또는 Multi TIFF도
	 * 수정 가능 현재 TIFF의 2가지 경우에 대한 수정 가능
	 *
	 * @param inputData   [in] 비정상 이미지로 의심 되는 이미지 메모리 데이터
	 * @param inputLength [in] 비정상 이미지로 의심 되는 이미지 메모리 데이터 크기
	 * @param pagenum     [in] 수정할 이미지의 페이지 번호 TIFF : 1 부터 시작, 0일 경우 전체 이미지 수정, 단일
	 *                    이미지의 경우 1 입력된
	 * @param repairmode  [in] 비정상 이미지 패턴(0 일 경우 모든 패턴을 조사) 1 : JPEG in TIFF 이미지중
	 *                    그레이 이미지 이면서 photometric 값이 RGB로 잘못 설정된 파일 2 : JPEG2000 in
	 *                    TIFF 이미지중 1bit 무압축 형식으로 저장되는 파일
	 * @param aImgInfo    [out] 비정상 이미지가 수정되는 경우, 이미지의 정보, 0: pointer 주소, 1: 메모리 크기,
	 *                    2: filetype
	 * @return 양수 : 성공(repairmode 값이 리턴됨) -2: 메모리 할당 실패 -4: 라이선스 체크 실패 -5: HDIB
	 *         handle이 NULL -6: DLL(so,sl) 로드 실패 -7: DLL(so,sl) 모듈에 적절한 함수가 없음 -10:
	 *         ImageIO에서 문제 발생(상세정보는 통합로그를 이용하세요) -107: 수정가능한 비정상 이미지 패턴을 포함하지 않은
	 *         이미지 그외 : ImageIO, 변환서버 에러 코드 참조
	 */
	public native int repairImage_Mem(byte[] inputData, int inputLength, int repairmode, long[] aImgInfo);

	public native int repairImage_MemEx(byte[] inputData, int inputLength, int pagenum, int repairmode,
			long[] aImgInfo);

}
