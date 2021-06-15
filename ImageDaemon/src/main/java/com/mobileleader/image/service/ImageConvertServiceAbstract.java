package com.mobileleader.image.service;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.springframework.util.ObjectUtils;

import com.inzisoft.pdf2image.InziPDF;
import com.inzisoft.server.codec.ImageIOJNI;
import com.inzisoft.server.pdf.Image2PDFJNI;
import com.mobileleader.image.convert.UvConst;
import com.mobileleader.image.exception.ImageConvertException;
import com.mobileleader.image.model.IcMaskingInfo;
import com.mobileleader.image.type.CompressionType;
import com.mobileleader.image.type.ConvertExtentionType;
import com.mobileleader.image.type.FileType;
import com.mobileleader.image.type.ResponseCodeType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ImageConvertServiceAbstract implements ImageConvertService {

	protected final char DELIMITER = '|';

	protected final String INDEX_FORMAT = "_%02d";

	private ImageIOJNI imageIOJNI = new ImageIOJNI();

	/**
	 * 파일 리스트 삭제
	 */
	protected boolean deleteFilesFromPath(List<String> filePaths) {
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

		log.info("Image page count : {}", totalCount);

		return totalCount;
	}

	/**
	 * 이미지(또는 이미지리스트) -> PDF 변환
	 * 
	 * @param srcPath 변환할 이미지 경로
	 * @param desPath 변환 결과 이미지 저장 경로
	 * @return
	 * @throws ImageConvertException
	 */
	protected int convertImageToPdf(String srcPath, String desPath) throws ImageConvertException {
		int result = Image2PDFJNI.convertImage2PDF_LE(srcPath, DELIMITER, 3, 1, 0, 0, desPath);

		if (result != 0) {
			throw new ImageConvertException(getErrorCode(result));
		}

		return result;
	}

	/**
	 * 이미지(Single) -> 이미지(Single) 변환 (마스킹 정보 있을 시, 마스킹 수행)
	 * 
	 * @param srcPath        변환할 이미지 경로
	 * @param desPath        변환 결과 이미지 저장 경로
	 * @param resultExt 	 변환할 파일 확장자
	 * @return
	 * @throws ImageConvertException
	 */
	protected int convertImageToImage(String srcPath, String desPath, String resultExt, List<IcMaskingInfo> maskingInfos)
			throws ImageConvertException {
		int result = -1;
		int fileType = FileType.NONE.getCode();
		int compType = CompressionType.NONE.getCode();

		switch (ConvertExtentionType.getByCode(resultExt)) {
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
				result = imageIOJNI.maskImage_FILE(srcPath, maskInfo.getX(), maskInfo.getY(), maskInfo.getWidth(),
						maskInfo.getHeight(), desPath, 50, fileType, compType);

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
	 * @param totalCount   	변환할 총 이미지(페이지) 수
	 * @param srcPath       변환할 이미지 경로
	 * @param desRootPath	변환 결과 이미지 저장 경로
	 * @param prefix		변환할 파일명 prefix
	 * @param dotExt       	변환할 파일 확장자(ex : ".jpg")
	 * @param resultExt   	변환할 파일 확장자
	 * @param maskingInfos 	마스킹 정보 리스트
	 * @param desNames     	변환 결과 이미지 파일명 리스트
	 * @return
	 * @throws ImageConvertException
	 */
	protected int convertMultiImageToImageList(int totalCount, String srcPath, String desRootPath, String prefix, String dotExt, String resultExt, 
			List<IcMaskingInfo> maskingInfos, List<String> desNames) throws ImageConvertException {
		int result = -1;
		String strIndex, desFilePath, desFileName = "";

		for (int i = 0; i < totalCount; i++) {
			strIndex = String.format(INDEX_FORMAT, (i+1));
			desFileName = prefix + strIndex + dotExt;
			desFilePath = desRootPath + desFileName;
			result = imageIOJNI.extractTIFF_FILE(srcPath, i + 1, desFilePath);

			if (result != 0) {
				throw new ImageConvertException(getErrorCode(result));
			}

			// 개별 이미지 변환
			convertImageToImage(desFilePath, desFilePath, resultExt, maskingInfos);

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
	 * @param srcPath       변환할 이미지 경로
	 * @param desRootPath   변환 결과 이미지 저장 루트 경로
	 * @param desFileName   변환 결과 이미지 파일명
	 * @param resultExt 	변환 파일 확장자
	 * @return
	 * @throws ImageConvertException
	 */
	protected int convertPdftoImage(String srcPath, String desRootPath, String desFileName, String resultExt) throws ImageConvertException {
		int result = -1;
		int fileType = FileType.NONE.getCode();
		int compType = CompressionType.NONE.getCode();

		switch (ConvertExtentionType.getByCode(resultExt)) {
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

		// PDF to Image : PDF가 여러장일 경우, desFileName_01, desFileName_02, ... 형태로 파일 생성
		// TODO : 여러개의 이미지가 생성되었을 경우, 어떻게 응답?
		result = InziPDF.convertPDF2NamedImage(srcPath, desRootPath, desFileName, 100, 50, fileType, compType, 0, 0);

		if (result < 1) {
			throw new ImageConvertException(getErrorCode(result));
		}

		return result;
	}

	/**
	 * PDF -> PNG 변환
	 * 	- PNG의 경우, 원본 PDF에서 직접 PNG로 변환이 불가함. 따라서 다른 이미지 확장자로 변환 후, PNG로 변환함. 
	 * @param desRootPath	변환 결과 이미지 저장 루트 경로
	 * @param desFileName	변환 결과 이미지 파일명
	 * @param prefix		변환할 파일명 prefix
	 * @param dotExt       	변환할 파일 확장자(ex : ".jpg")
	 * @param resultExt	변환 파일 확장자
	 * @param maskingInfos	마스킹 정보 리스트
	 * @param desNames     	변환 결과 이미지 파일명 리스트
	 * @return
	 * @throws ImageConvertException
	 */
	protected int convertAfterPdfToSingleImage(String desRootPath, String desFileName, String prefix, String dotExt,
			String resultExt, List<IcMaskingInfo> maskingInfos, List<String> desNames) throws ImageConvertException {
		int result = -1;

		String strIndex = String.format(INDEX_FORMAT, 1);
		String outputName = desRootPath + prefix + strIndex + dotExt;

		if (ConvertExtentionType.PNG.getCode().equalsIgnoreCase(resultExt)) {
			result = convertImageToImage(outputName, outputName, resultExt, maskingInfos);
		} else {
			result = 0;
		}

		new File(outputName).renameTo(new File(desFileName));
		desNames.add(FilenameUtils.getName(desFileName));

		return result;
	}

	/**
	 * convert after PDF to ImageList 
	 * 1. PDF to BMP/JPG : save desNames only 
	 * 2. PDF to TIFF : merge to Multi tiff (ex : filename_02.tif, filename_03.tif -> merge to filename_01.tif) 
	 * 3. PDF to PNG : jpg list to png list
	 */
	/**
	 * 
	 * PDF -> PNG list 변환
	 * 	- PNG의 경우, 원본 PDF에서 직접 PNG로 변환이 불가함. 따라서 다른 이미지 확장자로 변환 후, PNG로 변환함. 
	 * @param totalCount
	 * @param desRootPath
	 * @param desFileName
	 * @param prefix
	 * @param dotExt
	 * @param resultExt
	 * @param maskingInfos
	 * @param desNames
	 * @param removePaths
	 * @return
	 * @throws ImageConvertException
	 */
	protected int convertAfterPdftoMultiImage(int totalCount, String desRootPath, String desFileName, String prefix,
			String dotExt, String resultExt, List<IcMaskingInfo> maskingInfos, List<String> desNames,
			List<String> removePaths) throws ImageConvertException {
		int result = -1;
		String strIndex, outputName;

		ConvertExtentionType type = ConvertExtentionType.getByCode(resultExt);

		switch (type) {
		case BMP:
		case JPG:
			for (int i = 0; i < totalCount; i++) {
				strIndex = String.format(UvConst.INDEX_FORMAT, (i + 1));
				outputName = desRootPath + prefix + strIndex + dotExt;

				if (i == 0 && !ObjectUtils.isEmpty(maskingInfos)) {
					result = convertImageToImage(outputName, outputName, resultExt, maskingInfos);
				}

				desNames.add(FilenameUtils.getName(outputName));
			}

			result = 0;
			break;
		case TIFF:
			String inputName = desRootPath + prefix + "_01" + dotExt;

			if (!ObjectUtils.isEmpty(maskingInfos)) {
				result = convertImageToImage(inputName, inputName, resultExt, maskingInfos);
			}

			for (int i = 1; i < totalCount; i++) { // 01번째 이미지에 02번째 부터 병합처리
				strIndex = String.format(INDEX_FORMAT, (i+1));
				outputName = desRootPath + prefix + strIndex + dotExt;

				result = imageIOJNI.mergeTIFF_FILE(inputName, outputName);

				if (result < 0) {
					throw new ImageConvertException(getErrorCode(result));
				}

				removePaths.add(outputName);
			}

			new File(inputName).renameTo(new File(desFileName));
			desNames.add(FilenameUtils.getName(desFileName));
			break;
		case PNG:
			for (int i = 0; i < totalCount; i++) {
				strIndex = String.format(INDEX_FORMAT, (i + 1));
				outputName = desRootPath + prefix + strIndex + dotExt;

				if (i == 0 && !ObjectUtils.isEmpty(maskingInfos)) {
					result = convertImageToImage(outputName, outputName, resultExt, maskingInfos);
				} else {
					result = convertImageToImage(outputName, outputName, resultExt, null);
				}

				desNames.add(FilenameUtils.getName(outputName));
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

	// Image(Multi tiff) to tif list and make PDF
	protected int extractTiffNItoP(int totalCount, String src, String desRoot, String des, String baseFName,
			String dotExt, List<IcMaskingInfo> maskingInfos, List<String> removePaths) throws ImageConvertException {

		int result = -1;
		String strIndex, outputName, jSingleTiffLIst = "";

		for (int i = 0; i < totalCount; i++) {
			strIndex = String.format(INDEX_FORMAT, (i + 1));
			outputName = desRoot + baseFName + strIndex + dotExt;
			jSingleTiffLIst += outputName + DELIMITER;

			result = imageIOJNI.extractTIFF_FILE(src, i + 1, outputName);

			if (result < 0) {
				throw new ImageConvertException(getErrorCode(result));
			}

			if (i == 0 && !ObjectUtils.isEmpty(maskingInfos)) {
				result = convertImageToImage(outputName, outputName, ConvertExtentionType.TIFF.getCode(), maskingInfos);
			}
			removePaths.add(outputName);
		}

		result = convertImageToPdf(jSingleTiffLIst, des);

		return result;
	}
}
