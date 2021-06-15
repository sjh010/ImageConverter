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
import com.mobileleader.image.type.ResponseCodeType;

@Service
public class ImageConvertServiceImpl extends ImageConvertServiceAbstract {

	private static final Logger logger = LoggerFactory.getLogger(ImageConvertServiceImpl.class);
	
	@Override
	public ConvertResponse convert(ConvertRequest request) {
		
		String responseCode = ResponseCodeType.SUCEESS.getCode();	// 변환서버로 응답할 변환결과 에러코드
		
		int result = -1;											// JNI 변환모듇로부터 받은 응답값
		
		String baseName = "";										// 확장자 제외 파일명(예 : filename.jpg 중 filename)
		
		String dotExt = "";											// dot 포함 확장자(예 : filename.jpg 중 .jpg)
		
		String desFilePath = "";									// 변환결과파일 full path
		
		List<String> desFileNames = new ArrayList<String>();		// 변환결과 파일명 목록(변환서버에 응답할 목록)
		
		List<String> removeTempPaths = new ArrayList<String>();		// 변환에 사용된 임시파일경로 목록(삭제 필요하여 저장)

		try {
			baseName = FilenameUtils.getBaseName(request.getDesFileName());
			dotExt = "." + FilenameUtils.getExtension(request.getDesFileName());
			desFilePath = request.getDesRootPath() + File.separator + request.getDesFileName();

			ConvertType compressionType = ConvertType.getByCode(request.getConvType());
			
			int totalCount = getPageTotalCount(request.getSrcPath());
			
			switch (compressionType) {
			case IMAGE_TO_PDF:
				if (totalCount == 1) {
					result = convertImageToImage(request.getSrcPath(), desFilePath, ConvertExtentionType.JPG.getCode(), request.getMaskInfos());
					result = convertImageToPdf(desFilePath + DELIMITER, desFilePath);

					desFileNames.add(FilenameUtils.getName(desFilePath));
				} else if (totalCount > 1) {
					result = extractTiffNItoP(totalCount, request.getSrcPath(), request.getDesRootPath(),
							desFilePath, baseName, dotExt, request.getMaskInfos(), removeTempPaths);

					desFileNames.add(FilenameUtils.getName(desFilePath));
				}
				break;
			case PDF_TO_IMAGE:
				if (totalCount == 1) {
					result = convertPdftoImage(request.getSrcPath(), request.getDesRootPath(),
							request.getDesFileName(), request.getRstType());
					result = convertAfterPdfToSingleImage(request.getDesRootPath(), desFilePath, baseName, dotExt,
							request.getRstType(), request.getMaskInfos(), desFileNames);
				} else if (totalCount > 1) {
					// TODO : rst_type이 bmp일 경우, 파일사이즈가 큼. 대책 필요
					// TODO : rst_type이 png일 경우, PtoI / ItoI 두번 수행되어 속도문제 발생. 대책 필요

					result = convertPdftoImage(request.getSrcPath(), request.getDesRootPath(),
							request.getDesFileName(), request.getRstType());
					result = convertAfterPdftoMultiImage(totalCount, request.getDesRootPath(), desFilePath, baseName, dotExt,
							request.getRstType(), request.getMaskInfos(), desFileNames, removeTempPaths);
				}
				break;
			case IMAGE_TO_IMAGE :
				if (totalCount == 1) {
					result = convertImageToImage(request.getSrcPath(), desFilePath, request.getRstType(), request.getMaskInfos());
					desFileNames.add(FilenameUtils.getName(desFilePath));
				} else if (totalCount > 1) {
					result = convertMultiImageToImageList(totalCount, request.getSrcPath(), request.getDesRootPath(),
							baseName, dotExt, request.getRstType(), request.getMaskInfos(), desFileNames);
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
			responseCode = e.getErrorCode();
		} catch (Exception e) {
			logger.error("Exception error : {}", e.getMessage());
			responseCode = UvConst.ERR_CD.UNKNOWN;
		} finally {
			deleteFilesFromPath(removeTempPaths);
		}
		
		logger.info("responseCode : {}", responseCode);
		logger.info("desNames : {}", desFileNames.toString());
		logger.info("removePaths : {}", removeTempPaths.toString());

		ConvertResponse taskResult = new ConvertResponse();
		taskResult.setJobId(request.getJobId());
		taskResult.setDesNames(desFileNames);
		taskResult.setResponseCode(responseCode);

		return taskResult;
	}

}
