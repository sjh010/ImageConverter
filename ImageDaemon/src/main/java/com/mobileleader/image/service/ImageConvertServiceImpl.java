package com.mobileleader.image.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mobileleader.image.exception.ImageConvertException;
import com.mobileleader.image.model.ConvertRequest;
import com.mobileleader.image.model.ConvertResponse;
import com.mobileleader.image.model.IcMaskingInfo;
import com.mobileleader.image.type.ConvertType;
import com.mobileleader.image.type.ResponseCodeType;
import com.mobileleader.image.type.ResultExtentionType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ImageConvertServiceImpl extends ImageConvertServiceAbstract {

	private static final Logger logger = LoggerFactory.getLogger(ImageConvertServiceImpl.class);
	
	@Override
	public ConvertResponse convert(ConvertRequest request) {
		
		String jobId = request.getJobId();							// JOB ID(unique)
		
		String srcPath = request.getSrcPath();						// 변환 대상 파일 경로
		
		String desDirPath = request.getDesRootPath();				// 변환 결과 파일 디렉터리 경로
		
		String desFileName = request.getDesFileName();				// 변환 결과 파일명
		
		String rstType = request.getRstType();						// 변환 결과 타입(확장자)
		
		List<IcMaskingInfo> maskingInfos = request.getMaskInfos();	// 마스킹 정보
		
		String responseCode = ResponseCodeType.SUCEESS.getCode();	// 변환서버로 응답할 변환결과 에러코드								
		
		List<String> desFileNames = new ArrayList<String>();		// 변환결과 파일명 목록(변환서버에 응답할 목록)
		
		List<String> removeTempPaths = new ArrayList<String>();		// 변환에 사용된 임시파일경로 목록(삭제 필요하여 저장)
		
		try {
			int totalCount = getPageTotalCount(srcPath);
			log.info("[{}] page count : {}", jobId, totalCount);
			
			String desPath = desDirPath + File.separator + request.getDesFileName(); // 변환 결과 파일 경로
			
			switch (ConvertType.getByCode(request.getConvType())) {
			case IMAGE_TO_PDF:
				log.info("[{}] convert {} to {}", jobId, FilenameUtils.getExtension(srcPath).toUpperCase(), ResultExtentionType.getByCode(rstType).getDescription());
				
				int pageDirection = getPageDirection(srcPath);
				
				if (totalCount == 1) {
					// convert format(to JPG)
					convertImageToImage(srcPath, desPath, ResultExtentionType.JPG.getCode(), maskingInfos);
					// IMAGE(JPG) -> PDF
					convertImageToPdf(desPath, desPath, pageDirection);
				} else if (totalCount > 1) {
					// MULTI TIFF -> PDF
					convertMultiTiffToPdf(totalCount, srcPath, desDirPath, desFileName, maskingInfos, removeTempPaths, pageDirection);
				}
				
				desFileNames.add(FilenameUtils.getName(desFileName));
				break;
			case PDF_TO_IMAGE:
				// TODO : 변환 타입이 BMP일 경우, 파일사이즈가 큼. 대책 필요
				// TODO : 변환 타입이 PNG일 경우, PtoI / ItoI 두번 수행되어 속도문제 발생. 대책 필요
				
				log.info("[{}] convert {} to {}", jobId, FilenameUtils.getExtension(srcPath).toUpperCase(), ResultExtentionType.getByCode(rstType).getDescription());
				
				// PDF -> IMAGE
				convertPdftoImage(srcPath, desDirPath, desFileName, rstType);
				
				if (totalCount == 1) {
					// JPG -> PNG
					convertAfterPdfToSingleImage(desDirPath, desFileName, rstType, maskingInfos, desFileNames);
				} else if (totalCount > 1) {
					convertAfterPdftoMultiImage(totalCount, desDirPath, desFileName, rstType, maskingInfos, desFileNames, removeTempPaths);
				}
				break;
			case IMAGE_TO_IMAGE :
				log.info("[{}] convert {} to {}", jobId, FilenameUtils.getExtension(srcPath).toUpperCase(), ResultExtentionType.getByCode(rstType).getDescription());
				if (totalCount == 1) {
					// IMAGE -> IMAGE
					convertImageToImage(srcPath, desPath, rstType, maskingInfos);
					desFileNames.add(FilenameUtils.getName(desPath));
				} else if (totalCount > 1) {
					// MULTI TIFF -> IMAGE LIST
					convertMultiImageToImageList(totalCount, srcPath, desDirPath, desFileName, rstType, maskingInfos, desFileNames, removeTempPaths);
				}
				break;
			}
		} catch (ImageConvertException e) {
			logger.error("ImageConvertException : {}", e.getErrorCode(), e);
			responseCode = e.getErrorCode();
		} catch (Exception e) {
			logger.error("Exception : {}", e.getMessage(), e);
			responseCode = ResponseCodeType.UNKNOWN.getCode();
		} finally {
			deleteTempFiles(removeTempPaths);
		}
		
		logger.info("[{}] responseCode : {}", jobId, responseCode);
		logger.info("[{}] desNames : {}", jobId, desFileNames.toString());

		ConvertResponse taskResult = new ConvertResponse();
		taskResult.setJobId(request.getJobId());
		taskResult.setDesNames(desFileNames);
		taskResult.setResponseCode(responseCode);

		return taskResult;
	}

	
	
}
