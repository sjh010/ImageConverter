package com.mobileleader.image.service;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.springframework.util.ObjectUtils;

import com.inzisoft.pdf2image.InziPDF;
import com.inzisoft.server.codec.ImageIOJNI;
import com.inzisoft.server.pdf.Image2PDFJNI;
import com.mobileleader.image.exception.ImageConvertException;
import com.mobileleader.image.model.IcMaskingInfo;
import com.mobileleader.image.type.CompressionType;
import com.mobileleader.image.type.FileType;
import com.mobileleader.image.type.ResponseCodeType;
import com.mobileleader.image.type.ResultExtentionType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ImageConvertServiceAbstract implements ImageConvertService {

	protected final char DELIMITER = '|';

	protected final String INDEX_FORMAT = "_%02d";

	protected ImageIOJNI imageIOJNI = new ImageIOJNI();

	/**
	 * 파일 리스트 삭제
	 */
	protected boolean deleteTempFiles(List<String> filePaths) {
		int length = filePaths.size();

		for (int i = 0; i < length; i++) {
			File f = new File(filePaths.get(i));

			if (f.exists() && f.isFile()) {
				f.delete();
			}
		}
		return true;
	}

	/**
	 * JNI 에러코드 -> 변환 데몬 에러코드
	 */
	protected String getErrorCode(int index) {
		log.info("Module result code : {}", index);

		String errorCode = ResponseCodeType.UNKNOWN.getCode();

		if (ResponseCodeType.MAP_IMAGE_CON_ERROR.containsKey(index)) {
			errorCode = ResponseCodeType.MAP_IMAGE_CON_ERROR.get(index);
		}

		log.info("Replace result code : {}", errorCode);

		return errorCode;
	}

	/**
	 * 이미지 페이지 수 확인
	 * 
	 * @param srcPath 페이지 수 확인할 이미지 full path
	 * @return 페이지 수
	 * @throws ImageConvertException
	 */
	protected int getPageTotalCount(String srcPath) throws ImageConvertException {
		int totalCount = 0;
		int fileType = imageIOJNI.getFileType_FILE(srcPath);

		if (FileType.TIFF.getCode() == fileType) {
			totalCount = imageIOJNI.getTIFFTotalPage_FILE(srcPath);
		} else if (FileType.PDF.getCode() == fileType) {
			totalCount = InziPDF.getPDFPageCount(srcPath);
		} else if (fileType > 0) {
			// 파일 형식이 TIFF, PDF가 아닌경우 -> 페이지 수 1
			totalCount = 1;
		} else {
			throw new ImageConvertException(getErrorCode(fileType));
		}

		if (totalCount <= 0) {
			throw new ImageConvertException(getErrorCode(totalCount));
		}

		return totalCount;
	}
	
	/**
	 * 이미지 방향 확인
	 * 
	 * @param srcPath 이미지 full path
	 * @return
	 * @throws ImageConvertException
	 */
	protected int getPageDirection(String srcPath) throws ImageConvertException {
		int pageDirection = 0;
		long[] size = new long[2];
		int result = imageIOJNI.getPageSize_FILE(srcPath, 1, size);
		
		if (result != 0) {
			throw new ImageConvertException(getErrorCode(result));
		}
		
		long width = size[0];
		long height = size[1];
		
		if (width > height) {
			pageDirection = 1;
		}
		
		return pageDirection;
	}

	/**
	 * 이미지(또는 이미지리스트) -> PDF 변환
	 * 
	 * @param srcPath 변환할 이미지 경로
	 * @param desPath 변환 결과 이미지 저장 경로
	 * @return
	 * @throws ImageConvertException
	 */
	protected int convertImageToPdf(String srcPath, String desPath, int pageDirection) throws ImageConvertException {
		int result = Image2PDFJNI.convertImage2PDF_LE(srcPath, DELIMITER, 3, pageDirection, 0, 0, desPath);

		if (result != 0) {
			throw new ImageConvertException(getErrorCode(result));
		}

		return result;
	}

	/**
	 * 이미지(Single) -> 이미지(Single) 변환 (마스킹 정보 있을 시, 마스킹 수행)
	 * 
	 * @param srcPath   변환할 이미지 경로
	 * @param desPath   변환 결과 이미지 저장 경로
	 * @param resultExt 변환할 파일 확장자
	 * @return
	 * @throws ImageConvertException
	 */
	protected int convertImageToImage(String srcPath, String desPath, String rstType, List<IcMaskingInfo> maskingInfos) 
			throws ImageConvertException {
		int result = -1;
		int fileType = FileType.NONE.getCode();
		int compType = CompressionType.NONE.getCode();
		
		switch (ResultExtentionType.getByCode(rstType)) {
		case BMP:
			fileType = FileType.BMP.getCode();
			break;
		case JPG:
			fileType = FileType.JPEG.getCode();
			break;
		case TIFF:
			fileType = FileType.TIFF.getCode();
			compType = CompressionType.JPEG2000_IN_TIFF.getCode();
			break;
		case PNG:
			fileType = FileType.PNG.getCode();
			break;
		default:
			throw new ImageConvertException(ResponseCodeType.INVALID_RST_TYPE.getCode());
		}
		
		if (!ObjectUtils.isEmpty(maskingInfos)) { // 이미지 마스킹
			for (IcMaskingInfo maskInfo : maskingInfos) {
				result = imageIOJNI.maskImage_FILE(srcPath, maskInfo.getX(), maskInfo.getY(), maskInfo.getWidth(), maskInfo.getHeight(), 
						desPath, 50, fileType, compType);

				if (result < 0) {
					throw new ImageConvertException(getErrorCode(result));
				}

				srcPath = desPath;
			}
		} else {
			result = imageIOJNI.convertFormat_FILE(srcPath, fileType, compType, 0, 1, 4, 0, 0, 0, desPath);
		}

		if (result < 0) {
			throw new ImageConvertException(getErrorCode(result));
		}

		return result;
	}

	/**
	 * 이미지(Multi Tiff) -> 이미지(Single) 리스트 변환
	 * 
	 * @param totalCount   변환할 총 이미지(페이지) 수
	 * @param srcPath      변환할 이미지 경로
	 * @param desRootPath  변환 결과 이미지 저장 경로
	 * @param prefix       변환할 파일명 prefix
	 * @param dotExt       변환할 파일 확장자(ex : ".jpg")
	 * @param resultExt    변환할 파일 확장자
	 * @param maskingInfos 마스킹 정보 리스트
	 * @param desNames     변환 결과 이미지 파일명 리스트
	 * @return
	 * @throws ImageConvertException
	 */
	protected int convertMultiImageToImageList(int totalCount, String srcPath, String desDirPath, String desFileName, String rstType, 
			List<IcMaskingInfo> maskingInfos, List<String> desNames, List<String> removePaths) throws ImageConvertException {
		int result = -1;

		String prefix = FilenameUtils.getBaseName(desFileName);
		String extention = FilenameUtils.getExtension(desFileName);
		StringBuilder outputName = null;

		for (int i=0; i < totalCount; i++) {
			outputName = new StringBuilder();
			outputName.append(desDirPath);
			outputName.append(prefix);
			outputName.append(String.format(INDEX_FORMAT, (i + 1)));
			outputName.append(FilenameUtils.EXTENSION_SEPARATOR_STR);
			outputName.append(extention);

			result = imageIOJNI.extractTIFF_FILE(srcPath, i + 1, outputName.toString());

			if (result != 0) {
				throw new ImageConvertException(getErrorCode(result));
			}

			// 개별 이미지 변환
			convertImageToImage(outputName.toString(), outputName.toString(), rstType, maskingInfos);

			desNames.add(FilenameUtils.getName(outputName.toString()));
		}
		
		
		
		if (ResultExtentionType.TIFF.getCode().equalsIgnoreCase(rstType)) {
			StringBuilder inputName = new StringBuilder();
			inputName.append(desDirPath);
			inputName.append(prefix);
			inputName.append("_01");
			inputName.append(FilenameUtils.EXTENSION_SEPARATOR_STR);
			inputName.append(extention);
			
			for (int i=1; i < totalCount; i++) {
				outputName = new StringBuilder();
				outputName.append(desDirPath);
				outputName.append(prefix);
				outputName.append(String.format(INDEX_FORMAT, (i + 1)));
				outputName.append(FilenameUtils.EXTENSION_SEPARATOR_STR);
				outputName.append(extention);
				
				result = imageIOJNI.mergeTIFF_FILE(inputName.toString(), outputName.toString());
				
				if (result < 0) {
					throw new ImageConvertException(getErrorCode(result));
				}

				removePaths.add(outputName.toString());
			}
			
			new File(inputName.toString()).renameTo(new File(desDirPath + File.separator + desFileName));
			desNames.clear();
			desNames.add(desFileName);
		}

		if (result < 0) {
			throw new ImageConvertException(getErrorCode(result));
		}

		return result;
	}

	/**
	 * PDF -> 이미지(또는 이미지리스트) 변환
	 * 
	 * @param srcPath     변환할 이미지 경로
	 * @param desRootPath 변환 결과 이미지 저장 루트 경로
	 * @param desFileName 변환 결과 이미지 파일명
	 * @param resultExt   변환 파일 확장자
	 * @return
	 * @throws ImageConvertException
	 */
	protected int convertPdftoImage(String srcPath, String desDirPath, String desFileName, String rstType) throws ImageConvertException {
		int result = -1;
		int fileType = FileType.NONE.getCode();
		int compType = CompressionType.NONE.getCode();

		switch (ResultExtentionType.getByCode(rstType)) {
		case BMP:
			fileType = FileType.BMP.getCode();
			break;
		case JPG:
			fileType = FileType.JPEG.getCode();
			break;
		case TIFF:
			fileType = FileType.TIFF.getCode();
			compType = CompressionType.JPEG2000_IN_TIFF.getCode();
			break;
		case PNG:
			fileType = FileType.JPEG.getCode(); // PNG는 지원하지 않아 JPEG로 변환
			break;
		default:
			throw new ImageConvertException(ResponseCodeType.INVALID_RST_TYPE.getCode());
		}

		// PDF to Image : PDF가 여러장일 경우, desFileName_01, desFileName_02, ... 형태로 파일 생성
		result = InziPDF.convertPDF2NamedImage(srcPath, desDirPath, desFileName, 200, 80, fileType, compType, 0, 0);

		if (result < 1) {
			throw new ImageConvertException(getErrorCode(result));
		}

		return result;
	}

	/**
	 * PDF -> PNG 변환 - PNG의 경우, 원본 PDF에서 직접 PNG로 변환이 불가함. 따라서 다른 이미지 확장자로 변환 후, PNG로
	 * 변환함.
	 * 
	 * @param desRootPath  변환 결과 이미지 저장 루트 경로
	 * @param desFileName  변환 결과 이미지 파일명
	 * @param prefix       변환할 파일명 prefix
	 * @param dotExt       변환할 파일 확장자(ex : ".jpg")
	 * @param resultExt    변환 파일 확장자
	 * @param maskingInfos 마스킹 정보 리스트
	 * @param desNames     변환 결과 이미지 파일명 리스트
	 * @return
	 * @throws ImageConvertException
	 */
	protected int convertAfterPdfToSingleImage(String desDirPath, String desFileName, String resultType, List<IcMaskingInfo> maskingInfos, List<String> desNames) throws ImageConvertException {
		int result = -1;

		StringBuilder outputName = new StringBuilder();
		outputName.append(desDirPath);
		outputName.append(FilenameUtils.getBaseName(desFileName));
		outputName.append(String.format(INDEX_FORMAT, 1));
		outputName.append(FilenameUtils.EXTENSION_SEPARATOR_STR);
		outputName.append(FilenameUtils.getExtension(desFileName));

		if (ResultExtentionType.PNG.getCode().equalsIgnoreCase(resultType)) {
			result = convertImageToImage(outputName.toString(), outputName.toString(), resultType, maskingInfos);
		} else {
			if (!ObjectUtils.isEmpty(maskingInfos)) {
				result = convertImageToImage(outputName.toString(), outputName.toString(), resultType, maskingInfos);
			} else {
				result = 0;
			}
		}
		
		boolean isSuccess = new File(outputName.toString()).renameTo(new File(desDirPath + desFileName));
		
		if (isSuccess) {
			desNames.add(FilenameUtils.getName(desFileName));
		} else {
			desNames.add(FilenameUtils.getName(outputName.toString()));
		}
		

		return result;
	}

	/**
	 * 
	 * PDF -> Image list 변환
	 * 
	 * @param totalCount
	 * @param desRootPath
	 * @param desFileName
	 * @param prefix
	 * @param dotExt
	 * @param rstType
	 * @param maskingInfos
	 * @param desNames
	 * @param removePaths
	 * @return
	 * @throws ImageConvertException
	 */
	protected int convertAfterPdftoMultiImage(int totalCount, String desDirPath, String desFileName, String rstType, List<IcMaskingInfo> maskingInfos, 
			List<String> desNames, List<String> removePaths) throws ImageConvertException {
		int result = -1;

		String prefix = FilenameUtils.getBaseName(desFileName);
		String resultExt = FilenameUtils.getExtension(desFileName);

		StringBuilder outputName = null;

		switch (ResultExtentionType.getByCode(rstType)) {
		case BMP:
		case JPG:
			for (int i = 0; i < totalCount; i++) {
				outputName = new StringBuilder();
				outputName.append(desDirPath);
				outputName.append(prefix);
				outputName.append(String.format(INDEX_FORMAT, (i+1)));
				outputName.append(FilenameUtils.EXTENSION_SEPARATOR_STR);
				outputName.append(resultExt);

				if (i == 0 && !ObjectUtils.isEmpty(maskingInfos)) {
					result = convertImageToImage(outputName.toString(), outputName.toString(), rstType, maskingInfos);
				}

				desNames.add(FilenameUtils.getName(outputName.toString()));
			}

			result = 0;
			break;
		case TIFF:
			StringBuilder inputName = new StringBuilder();
			inputName.append(desDirPath).append(prefix).append("_01.").append(resultExt);
			
			if (!ObjectUtils.isEmpty(maskingInfos)) {
				result = convertImageToImage(inputName.toString(), inputName.toString(), rstType, maskingInfos);
			}

			for (int i = 1; i < totalCount; i++) { // 01번째 이미지에 02번째 부터 병합처리
				outputName = new StringBuilder();
				outputName.append(desDirPath);
				outputName.append(prefix);
				outputName.append(String.format(INDEX_FORMAT, (i + 1)));
				outputName.append(FilenameUtils.EXTENSION_SEPARATOR_STR);
				outputName.append(resultExt);
				
				result = imageIOJNI.mergeTIFF_FILE(inputName.toString(), outputName.toString());

				if (result < 0) {
					throw new ImageConvertException(getErrorCode(result));
				}

				removePaths.add(outputName.toString());
			}

			boolean isSuccess = new File(inputName.toString()).renameTo(new File(desDirPath + desFileName));
			
			if (isSuccess) {
				desNames.add(FilenameUtils.getName(desFileName));
			} else {
				desNames.add(FilenameUtils.getName(inputName.toString()));
			}

			break;
		case PNG:
			for (int i = 0; i < totalCount; i++) {
				outputName = new StringBuilder();
				outputName.append(desDirPath);
				outputName.append(prefix);
				outputName.append(String.format(INDEX_FORMAT, (i + 1)));
				outputName.append(FilenameUtils.EXTENSION_SEPARATOR_STR);
				outputName.append(resultExt);

				if (i == 0 && !ObjectUtils.isEmpty(maskingInfos)) {
					result = convertImageToImage(outputName.toString(), outputName.toString(), rstType, maskingInfos);
				} else {
					result = convertImageToImage(outputName.toString(), outputName.toString(), rstType, null);
				}

				desNames.add(FilenameUtils.getName(outputName.toString()));
			}
			break;
		default:
			throw new ImageConvertException(ResponseCodeType.INVALID_RST_TYPE.getCode());
		}

		if (result < 0) {
			throw new ImageConvertException(getErrorCode(result));
		}

		return result;
	}
	
	/**
	 * TIFF(Multi) -> PDF 변환
	 * 
	 * - Multi tiff 이미지에서 각각 개별 tiff 추출 - 개별 tiff 리스트를 통해 PDF 생성
	 * 
	 * @param totalCount   변환할 총 이미지(페이지) 수
	 * @param srcPath      변환할 이미지 경로
	 * @param desRootPath  변환 결과 이미지 루트 경로
	 * @param desPath      변환 결과 이미지 경로
	 * @param prefix       변환할 파일명 prefix
	 * @param dotExt       변환할 파일 확장자(ex : ".jpg")
	 * @param maskingInfos 마스킹 정보 리스트
	 * @param removePaths  삭제할 파일경로 리스트
	 * @return
	 * @throws ImageConvertException
	 */
	protected int convertMultiTiffToPdf(int totalCount, String srcPath, String desDirPath, String desFileName, 
			List<IcMaskingInfo> maskingInfos, List<String> removePaths, int pageDirection) throws ImageConvertException {

		int result = -1;;

		String prefix = FilenameUtils.getBaseName(desFileName);
		String extention = FilenameUtils.getExtension(desFileName);
		
		StringBuilder outputName = null;
		StringBuilder jSingleTiffList = new StringBuilder();

		for (int i = 0; i < totalCount; i++) {
			outputName = new StringBuilder();
			outputName.append(desDirPath);
			outputName.append(prefix);
			outputName.append(String.format(INDEX_FORMAT, (i+1)));
			outputName.append(FilenameUtils.EXTENSION_SEPARATOR_STR);
			outputName.append(extention);
		
			result = imageIOJNI.extractTIFF_FILE(srcPath, i + 1, outputName.toString());

			if (result < 0) {
				throw new ImageConvertException(getErrorCode(result));
			}

			if (i == 0 && !ObjectUtils.isEmpty(maskingInfos)) {
				result = convertImageToImage(outputName.toString(), outputName.toString(), ResultExtentionType.TIFF.getCode(), maskingInfos);
			}

			removePaths.add(outputName.toString());

			jSingleTiffList.append(outputName.toString()).append(DELIMITER);
		}

		result = convertImageToPdf(jSingleTiffList.toString(), desDirPath + File.separator + desFileName, pageDirection);

		return result;
	}

}
