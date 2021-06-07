package com.mobileleader.image.service;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import com.inzisoft.pdf2image.InziPDF;
import com.inzisoft.server.codec.ImageIOJNI;
import com.inzisoft.server.pdf.Image2PDFJNI;
import com.mobileleader.image.convert.UvConst;
import com.mobileleader.image.exception.ImageConvertException;
import com.mobileleader.image.model.IcMaskingInfo;
import com.mobileleader.image.type.CompressionType;
import com.mobileleader.image.type.ConvertExtentionType;
import com.mobileleader.image.type.ErrorCodeType;
import com.mobileleader.image.type.FileType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ImageConvertServiceAbstract implements ImageConvertService {

	private static final Logger logger = LoggerFactory.getLogger(ImageConvertServiceAbstract.class);

	protected final char DELIMITER = '|';

	protected final String INDEX_FORMAT = "_%02d";

	private ImageIOJNI imageIOJNI = new ImageIOJNI();

	protected boolean deleteFilesFromPath(List<String> filePaths) {
		for (int i = 0; i < filePaths.size(); i++) {
			File f = new File(filePaths.get(i));

			if (f.exists() && f.isFile()) {
				f.delete();
			}
		}
		return true;
	}

	// JNI errorCode to imgConServer errorCode
	protected String getErrorCode(int index) {
		log.info("Module result : {}", index);
		
		String errorCode = ErrorCodeType.UNKNOWN.getCode();

		if (ErrorCodeType.MAP_IMAGE_CON_ERROR.containsKey(index)) {
			errorCode = ErrorCodeType.MAP_IMAGE_CON_ERROR.get(index);
		}

		log.info("Replace result : {}", errorCode);
		
		return errorCode;
	}

	// get fileName from FullPath
	protected String getFileNameFromDesPath(String fullPath, String rootPath) {
		return fullPath.replace(rootPath, "");
	}

	/**
	 * 이미지 페이지 수 확인
	 * 
	 * @param src 페이지 수 확인할 이미지 full path
	 * @return 페이지 수
	 * @throws ImageConvertException
	 */
	protected int getImageTotalCount(String src) throws ImageConvertException {
		int totalCount = 0;
		int fileType = imageIOJNI.getFileType_FILE(src);

		if (fileType == FileType.TIFF.getCode()) {
			totalCount = imageIOJNI.getTIFFTotalPage_FILE(src);
		} else if (fileType == FileType.PDF.getCode()) {
			throw new ImageConvertException(ErrorCodeType.INVALID_FILE_TYPE.getCode());
		} else if (fileType > 0) {
			// 파일 형식이 TIFF, PDF가 아닌경우 -> 페이지 수 1
			totalCount = 1;
		} else {
			throw new ImageConvertException(getErrorCode(fileType));
		}

		if (totalCount <= 0) {
			throw new ImageConvertException(getErrorCode(totalCount));
		}

		logger.info("getImageTotalCount : {}", totalCount);

		return totalCount;
	}

	/**
	 * PDF 페이지 수 확인
	 * 
	 * @param src 페이지 수 확인할 PDF full path
	 * @return 페이지 수
	 * @throws ImageConvertException
	 */
	protected int getPdfTotalCount(String src) throws ImageConvertException {
		int totalCount = 0;
		int fileType = imageIOJNI.getFileType_FILE(src);
		if (fileType == FileType.PDF.getCode()) {
			totalCount = InziPDF.getPDFPageCount(src);
		} else if (fileType > 0) {
			throw new ImageConvertException(ErrorCodeType.INVALID_FILE_TYPE.getCode());
		} else {
			throw new ImageConvertException(getErrorCode(fileType));
		}

		if (totalCount <= 0) {
			throw new ImageConvertException(getErrorCode(totalCount));
		}

		logger.info("getPdfPageCount : {}", totalCount);

		return totalCount;
	}

	/**
	 * 이미지(또는 이미지리스트) -> PDF 변환
	 * 
	 * @param src 변환할 이미지 경로
	 * @param des 변환 결과 이미지 저장 경로
	 * @return
	 * @throws ImageConvertException
	 */
	protected int convertImageToPdf(String src, String des) throws ImageConvertException {
		int result = Image2PDFJNI.convertImage2PDF_LE(src, UvConst.DELIMITER, 3, 1, 0, 0, des);

		if (result != 0) {
			throw new ImageConvertException(getErrorCode(result));
		}

		return result;
	}

	/**
	 * 이미지 -> 이미지 변환 마스킹 정보 있을 시, 마스킹 수행
	 * 
	 * @param src        변환할 이미지 경로
	 * @param des        변환 결과 이미지 저장 경로
	 * @param resultType 변환할 파일 확장자
	 * @return
	 * @throws ImageConvertException
	 */
	protected int convertImageToImage(String src, String des, String resultType, List<IcMaskingInfo> maskingInfos)
			throws ImageConvertException {
		int result = -1;
		int fileType, compType;

		ConvertExtentionType type = ConvertExtentionType.getByCode(resultType);

		switch (type) {
		case BMP:
			fileType = FileType.BMP.getCode();
			compType = CompressionType.NONE.getCode();
			break;
		case JPG:
			fileType = FileType.JPEG.getCode();
			compType = CompressionType.NONE.getCode();
			break;
		case TIFF:
			fileType = FileType.TIFF.getCode();
			compType = CompressionType.JPEG2000_IN_TIFF.getCode();
			break;
		case PNG:
			fileType = FileType.PNG.getCode();
			compType = CompressionType.NONE.getCode();
			break;
		default:
			throw new ImageConvertException(ErrorCodeType.INVALID_RST_TYPE.getCode());
		}

		if (!ObjectUtils.isEmpty(maskingInfos)) {
			for (IcMaskingInfo maskInfo : maskingInfos) {
				result = imageIOJNI.maskImage_FILE(src, maskInfo.getX(), maskInfo.getY(), maskInfo.getWidth(),
						maskInfo.getHeight(), des, 50, fileType, compType);

				if (result < 0) {
					throw new ImageConvertException(getErrorCode(result));
				}

				src = des;
			}
		} else {
			result = imageIOJNI.convertFormat_FILE(src, fileType, compType, 0, 1, 4, 0, 0, 0, des);
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
	 * @param src          변환할 이미지 경로
	 * @param desRoot      변환 결과 이미지 저장 경로
	 * @param baseFName    변환할 파일명 prefix
	 * @param dotExt       변환할 파일 확장자(ex : ".jpg")
	 * @param resultType   변환할 파일 확장자
	 * @param maskingInfos 마스킹 정보 리스트
	 * @param desNames     변환 결과 이미지 파일명 리스트
	 * @return
	 * @throws ImageConvertException
	 */
	protected int convertMultiImageToImageList(int totalCount, String src, String desRoot, String baseFName,
			String dotExt, String resultType, List<IcMaskingInfo> maskingInfos, List<String> desNames)
			throws ImageConvertException {
		int result = -1;
		String strIndex, outputName = "";

		for (int i = 0; i < totalCount; i++) {
			strIndex = String.format(UvConst.INDEX_FORMAT, (i + 1));
			outputName = desRoot + baseFName + strIndex + dotExt;
			result = imageIOJNI.extractTIFF_FILE(src, i + 1, outputName);

			if (result != 0) {
				throw new ImageConvertException(getErrorCode(result));
			}

			convertImageToImage(outputName, outputName, resultType, maskingInfos);

			desNames.add(getFileNameFromDesPath(outputName, desRoot));
		}

		if (result < 0) {
			throw new ImageConvertException(getErrorCode(result));
		}

		return result;
	}

	// PDF to Image(or ImageList)
	/**
	 * PDF -> 이미지(또는 이미지리스트) 변환
	 * 
	 * @param src        변환할 이미지 경로
	 * @param desRoot    변환 결과 이미지 저장 루트 경로
	 * @param desFName   변환 결과 이미지 파일명
	 * @param resultType 변환 파일 확장자
	 * @return
	 * @throws ImageConvertException
	 */
	protected int convertPtoI(String src, String desRoot, String desFName, String resultType)
			throws ImageConvertException {
		int result = -1;
		int fileType, compType;

		ConvertExtentionType type = ConvertExtentionType.getByCode(resultType);

		switch (type) {
		case BMP:
			fileType = FileType.BMP.getCode();
			compType = CompressionType.NONE.getCode();
			break;
		case JPG:
			fileType = FileType.JPEG.getCode();
			compType = CompressionType.NONE.getCode();
			break;
		case TIFF:
			fileType = FileType.TIFF.getCode();
			compType = CompressionType.JPEG2000_IN_TIFF.getCode();
			break;
		case PNG:
			fileType = FileType.PNG.getCode();
			compType = CompressionType.NONE.getCode();
			break;
		default:
			throw new ImageConvertException(ErrorCodeType.INVALID_RST_TYPE.getCode());
		}

		result = InziPDF.convertPDF2NamedImage(src, desRoot, desFName, 100, 50, fileType, compType, 0, 0);

		if (result < 1) {
			throw new ImageConvertException(getErrorCode(result));
		}

		return result;
	}

	/**
	 * convert after PDF to Image 
	 * 1. PDF to BMP/JPG/TIFF : save desNames only 
	 * 2. PDF to PNG : jpg to png
	 */
	protected int convertAfterPdfToSingleImage(String desRoot, String des, String baseFName, String dotExt,
			String resultType, List<IcMaskingInfo> maskingInfos, List<String> desNames) throws ImageConvertException {
		int result = -1;

		String strIndex = String.format(INDEX_FORMAT, 1);
		String outputName = desRoot + baseFName + strIndex + dotExt;

		if (ConvertExtentionType.PNG.getCode().equalsIgnoreCase(resultType)) {
			result = convertImageToImage(outputName, outputName, resultType, maskingInfos);
		} else {
			result = 0;
		}

		new File(outputName).renameTo(new File(des));
		desNames.add(getFileNameFromDesPath(des, desRoot));

		return result;
	}

	/**
	 * convert after PDF to ImageList 
	 * 1. PDF to BMP/JPG : save desNames only 
	 * 2. PDF to TIFF : merge to Multi tiff (ex : filename_02.tif, filename_03.tif -> merge to filename_01.tif) 
	 * 3. PDF to PNG : jpg list to png list
	 */
	protected int convertAfterPdftoMultiImage(int totalCount, String desRoot, String des, String baseFName,
			String dotExt, String resultType, List<IcMaskingInfo> maskingInfos, List<String> desNames,
			List<String> removePaths) throws ImageConvertException {
		int result = -1;
		String strIndex, outputName;

		ConvertExtentionType type = ConvertExtentionType.getByCode(resultType);

		switch (type) {
		case BMP:
		case JPG:
			for (int i = 0; i < totalCount; i++) {
				strIndex = String.format(UvConst.INDEX_FORMAT, (i + 1));
				outputName = desRoot + baseFName + strIndex + dotExt;

				if (i == 0 && !ObjectUtils.isEmpty(maskingInfos)) {
					result = convertImageToImage(outputName, outputName, resultType, maskingInfos);
				}

				desNames.add(getFileNameFromDesPath(outputName, desRoot));
			}

			result = 0;
			break;
		case TIFF:
			String inputName = desRoot + baseFName + "_01" + dotExt;

			if (!ObjectUtils.isEmpty(maskingInfos)) {
				result = convertImageToImage(inputName, inputName, resultType, maskingInfos);
			}

			for (int i = 1; i < totalCount; i++) { // 01번째 이미지에 02번째 부터 병합처리
				strIndex = String.format(INDEX_FORMAT, (i + 1));
				outputName = desRoot + baseFName + strIndex + dotExt;

				result = imageIOJNI.mergeTIFF_FILE(inputName, outputName);

				if (result < 0) {
					throw new ImageConvertException(getErrorCode(result));
				}

				removePaths.add(outputName);
			}

			new File(inputName).renameTo(new File(des));
			desNames.add(getFileNameFromDesPath(des, desRoot));
			break;
		case PNG:
			for (int i = 0; i < totalCount; i++) {
				strIndex = String.format(INDEX_FORMAT, (i + 1));
				outputName = desRoot + baseFName + strIndex + dotExt;

				if (i == 0 && !ObjectUtils.isEmpty(maskingInfos)) {
					result = convertImageToImage(outputName, outputName, resultType, maskingInfos);
				} else {
					result = convertImageToImage(outputName, outputName, resultType, null);
				}

				desNames.add(getFileNameFromDesPath(outputName, desRoot));
			}
			break;
		default:
			throw new ImageConvertException(ErrorCodeType.INVALID_RST_TYPE.getCode());
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
