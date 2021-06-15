package com.mobileleader.image.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mobileleader.image.convert.UvConst;
import com.mobileleader.image.data.dto.ConvertStatus;
import com.mobileleader.image.data.mapper.ConvertStatusMapper;
import com.mobileleader.image.exception.ImageConvertException;
import com.mobileleader.image.model.ConvertRequest;
import com.mobileleader.image.model.ConvertResponse;
import com.mobileleader.image.type.ConvertExtentionType;
import com.mobileleader.image.type.ConvertType;
import com.mobileleader.image.type.ErrorCodeType;

@Service
public class ImageConvertServiceImpl extends ImageConvertServiceAbstract {

	private static final Logger logger = LoggerFactory.getLogger(ImageConvertServiceImpl.class);
	
	@Override
	public ConvertResponse convert(ConvertRequest request) {
		
		// 변환서버로 응답할 변환결과 에러코드
		String errorCode = ErrorCodeType.SUCEESS.getCode();
		// JNI 변환모듇ㄹ로부터 받은 응답값
		int result = -1;
		// 확장자 제외 파일명(예 : filename.jpg 중 filename)
		String baseName = "";
		// dot 포함 확장자(예 : filename.jpg 중 .jpg)
		String dotExt = "";
		// 변환결과파일 full path
		String desPath = "";
		// 변환결과 파일명 목록(변환서버에 응답할 목록)
		List<String> desNames = new ArrayList<String>();
		// 변환에 사용된 임시파일경로 목록(삭제 필요하여 저장)
		List<String> removePaths = new ArrayList<String>();

		try {
			baseName = FilenameUtils.getBaseName(request.getDesFileName());
			dotExt = "." + FilenameUtils.getExtension(request.getDesFileName());
			desPath = request.getDesRootPath() + File.separator + request.getDesFileName();

			ConvertType compressionType = ConvertType.getByCode(request.getConvType());
			
			int totalCount = getPageTotalCount(request.getSrcPath());
			
			switch (compressionType) {
			case IMAGE_TO_PDF:
//				totalCount = getImageTotalCount(request.getSrcPath());

				if (totalCount == 1) {
					result = convertImageToImage(request.getSrcPath(), desPath, ConvertExtentionType.JPG.getCode(), request.getMaskInfos());
					result = convertImageToPdf(desPath + DELIMITER, desPath);

					desNames.add(getFileNameFromDesPath(desPath, request.getDesRootPath()));
				} else if (totalCount > 1) {
					result = extractTiffNItoP(totalCount, request.getSrcPath(), request.getDesRootPath(),
							desPath, baseName, dotExt, request.getMaskInfos(), removePaths);

					desNames.add(getFileNameFromDesPath(desPath, request.getDesRootPath()));
				}
				break;
			case PDF_TO_IMAGE:
//				totalCount = getPdfTotalCount(request.getSrcPath());

				if (totalCount == 1) {
					result = convertPdftoImage(request.getSrcPath(), request.getDesRootPath(),
							request.getDesFileName(), request.getRstType());
					result = convertAfterPdfToSingleImage(request.getDesRootPath(), desPath, baseName, dotExt,
							request.getRstType(), request.getMaskInfos(), desNames);
				} else if (totalCount > 1) {
					// TODO : rst_type이 bmp일 경우, 파일사이즈가 큼. 대책 필요
					// TODO : rst_type이 png일 경우, PtoI / ItoI 두번 수행되어 속도문제 발생. 대책 필요

					result = convertPdftoImage(request.getSrcPath(), request.getDesRootPath(),
							request.getDesFileName(), request.getRstType());
					result = convertAfterPdftoMultiImage(totalCount, request.getDesRootPath(), desPath, baseName, dotExt,
							request.getRstType(), request.getMaskInfos(), desNames, removePaths);
				}
				break;
			case IMAGE_TO_IMAGE :
//				totalCount = getImageTotalCount(request.getSrcPath());

				if (totalCount == 1) {
					result = convertImageToImage(request.getSrcPath(), desPath, request.getRstType(), request.getMaskInfos());
					desNames.add(getFileNameFromDesPath(desPath, request.getDesRootPath()));
				} else if (totalCount > 1) {
					result = convertMultiImageToImageList(totalCount, request.getSrcPath(), request.getDesRootPath(),
							baseName, dotExt, request.getRstType(), request.getMaskInfos(), desNames);
				}
				break;
			default:
				if (result < 0) {
					throw new ImageConvertException(getErrorCode(result));
				}
				break;
			}
		} catch (ImageConvertException e) {
			logger.error("ImageConvertException error : {}", e.getErrorCode());
			errorCode = e.getErrorCode();
		} catch (Exception e) {
			logger.error("Exception error : {}", e.getMessage());
			errorCode = UvConst.ERR_CD.UNKNOWN;
		} finally {
			deleteFilesFromPath(removePaths);
		}
		
		logger.info("desNames : {}", desNames.toString());
		logger.info("removePaths : {}", removePaths.toString());

		ConvertResponse taskResult = new ConvertResponse();
		taskResult.setJobId(request.getJobId());
		taskResult.setDesNames(desNames);
		taskResult.setErrorCode(errorCode);

		return taskResult;
	}

}
