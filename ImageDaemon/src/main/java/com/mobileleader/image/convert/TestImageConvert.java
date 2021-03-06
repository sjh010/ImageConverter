package com.mobileleader.image.convert;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.inzisoft.pdf2image.InziPDF;
import com.inzisoft.server.codec.ImageIOJNI;
import com.inzisoft.server.pdf.Image2PDFJNI;
import com.mobileleader.image.exception.ImageConvertException;
import com.mobileleader.image.model.ConvertRequest;
import com.mobileleader.image.model.ConvertResponse;
import com.mobileleader.image.model.IcMaskingInfo;

public class TestImageConvert {

	private static ImageIOJNI imageIOJNI = new ImageIOJNI();
	
	public static boolean deleteFilesFromPath(List<String> filePaths) {
		for (int i=0; i < filePaths.size(); i++) {
			File f = new File(filePaths.get(i));
			
			if (f.exists() && f.isFile()) {
				f.delete();
			}
		}
		
		return true;
	}
	
	// JNI errorCode to imgConServer errorCode
	public static String getErrorCode(int index) {
		String errorCode = UvConst.ERR_CD.UNKNOWN;
		
		if (UvConst.MAP_IMAGE_CON_ERROR.containsKey(index)) {
			errorCode = UvConst.MAP_IMAGE_CON_ERROR.get(index);
		}
		
		return errorCode;
	}
	
	// empty check for maskingInfos
	public static boolean isEmptyMaskingInfos(List<IcMaskingInfo> maskingInfos) {
		boolean isEmpty = true;
		
		if (maskingInfos != null && maskingInfos.size() > 0) {
			isEmpty = false;
		}
		
		return isEmpty;
	}
	
	// get fileName from FullPath
	public static String getFileNameFromDesPath(String fullPath, String rootPath) {
		return fullPath.replace(rootPath, "");
	}
	
	// get Image Page Count
	public static int getImageTotalCount(String src) throws ImageConvertException {
		int totalCount = 0;
		int fileType = imageIOJNI.getFileType_FILE(src);
		if (fileType == UvConst.FILE_TYPE.TIFF) {
			totalCount = imageIOJNI.getTIFFTotalPage_FILE(src);
		} else if (fileType == UvConst.FILE_TYPE.PDF) {
			throw new ImageConvertException(UvConst.ERR_CD.INVALID_FILE_TYPE);
		} else if (fileType > 0) {
			totalCount = 1;
		} else {
			throw new ImageConvertException(getErrorCode(fileType));
		}
		
		System.out.println("getImageTotalCount : " + totalCount);
		
		if (totalCount <= 0) {
			throw new ImageConvertException(getErrorCode(totalCount));
		}
		
		return totalCount;
	}
	
	// get PDF Page Count
	public static int getPdfTotalCount(String src) throws ImageConvertException {
		int totalCount = 0;
		int fileType = imageIOJNI.getFileType_FILE(src);
		if (fileType == UvConst.FILE_TYPE.PDF) {
			totalCount = InziPDF.getPDFPageCount(src);
		} else if (fileType > 0) {
			throw new ImageConvertException(UvConst.ERR_CD.INVALID_FILE_TYPE);
		} else {
			throw new ImageConvertException(getErrorCode(fileType));
		}
		
		System.out.println("getPdfTotalCount : " + totalCount);
		
		if (totalCount <= 0) {
			throw new ImageConvertException(getErrorCode(totalCount));
		}
		
		return totalCount;
	}
	
	// Image(or ImageList) to PDF
	public static int convertItoP(String src, String des) throws ImageConvertException {
		int result = Image2PDFJNI.convertImage2PDF_LE(src, UvConst.DELIMITER, 3, 1, 0, 0, des);
		if (result != 0) {
			throw new ImageConvertException(getErrorCode(result));
		}
		
		return result;
	}
	
	// Image to Image
	public static int convertItoI(String src, String des, String resultType) throws ImageConvertException {
		int result = -1;
		int fileType, compType;
		
		if (UvConst.RST_TYPE.BMP.equals(resultType)) {
			fileType = UvConst.FILE_TYPE.BMP;
			compType = UvConst.COMP_TYPE.NONE;
		} else if (UvConst.RST_TYPE.JPG.equals(resultType)) {
			fileType = UvConst.FILE_TYPE.JPEG;
			compType = UvConst.COMP_TYPE.NONE;
		} else if (UvConst.RST_TYPE.TIFF.equals(resultType)) {
			fileType = UvConst.FILE_TYPE.TIFF;
			compType = UvConst.COMP_TYPE.JPEG2000_IN_TIFF;
		} else if (UvConst.RST_TYPE.PNG.equals(resultType)) {
			fileType = UvConst.FILE_TYPE.PNG;
			compType = UvConst.COMP_TYPE.NONE;
		} else {
			throw new ImageConvertException(UvConst.ERR_CD.INVALID_RST_TYPE);
		}
		
		result = imageIOJNI.convertFormat_FILE(src, fileType, compType, 0, 1, 4, 0, 0, 0, des);
		
		if (result < 0) {
			throw new ImageConvertException(getErrorCode(result));
		}
		
		return result;
	}
	
	// Image to Image(with masking)
	public static int convertItoIWithMask(String src, String des, String resultType, List<IcMaskingInfo> maskingInfos) throws ImageConvertException {
		int result = -1;
		int fileType, compType;
		
		if (UvConst.RST_TYPE.BMP.equals(resultType)) {
			fileType = UvConst.FILE_TYPE.BMP;
			compType = UvConst.COMP_TYPE.NONE;
		} else if (UvConst.RST_TYPE.JPG.equals(resultType)) {
			fileType = UvConst.FILE_TYPE.JPEG;
			compType = UvConst.COMP_TYPE.NONE;
		} else if (UvConst.RST_TYPE.TIFF.equals(resultType)) {
			fileType = UvConst.FILE_TYPE.TIFF;
			compType = UvConst.COMP_TYPE.JPEG2000_IN_TIFF;
		} else if (UvConst.RST_TYPE.PNG.equals(resultType)) {
			fileType = UvConst.FILE_TYPE.PNG;
			compType = UvConst.COMP_TYPE.NONE;
		} else {
			throw new ImageConvertException(UvConst.ERR_CD.INVALID_RST_TYPE);
		}

		for (IcMaskingInfo maskInfo : maskingInfos) {
			result = imageIOJNI.maskImage_FILE(src, maskInfo.getX(), maskInfo.getY(), maskInfo.getWidth(), maskInfo.getHeight(), des, 50, fileType, compType);
			
			if (result < 0) {
				throw new ImageConvertException(getErrorCode(result));
			}
			
			src = des;
		}
		
		return result;	
	}
	
	
	// Image(Multi tiff) to ImageList
	public static int convertItoIMulti(int totalCount, String src, String desRoot, String baseFName, String dotExt, String resultType
			, List<IcMaskingInfo> maskingInfos, List<String> desNames) throws ImageConvertException {
		int result = -1;
		String strIndex, outputName = "";
		
		for (int i=0; i < totalCount; i++) {
			strIndex = String.format(UvConst.INDEX_FORMAT, (i+1));
			outputName = desRoot + baseFName + strIndex + dotExt;
			result = imageIOJNI.extractTIFF_FILE(src, i+1, outputName);
			
			if (result != 0) {
				throw new ImageConvertException(getErrorCode(result));
			}
			
			if (i == 0 && !isEmptyMaskingInfos(maskingInfos)) {
				result = convertItoIWithMask(outputName, outputName, resultType, maskingInfos);
			} else {
				result = convertItoI(outputName, outputName, resultType);
			}
			
			desNames.add(getFileNameFromDesPath(outputName, desRoot));
		}
		
		if (result < 0) {
			throw new ImageConvertException(getErrorCode(result));
		}
		
		return result;
	}
	
	// PDF to Image(or ImageList)
	public static int convertPtoI(String src, String desRoot, String desFName, String resultType) throws ImageConvertException {
		int result = -1;
		int fileType, compType;
		
		if (UvConst.RST_TYPE.BMP.equals(resultType)) {
			fileType = UvConst.FILE_TYPE.BMP;
			compType = UvConst.COMP_TYPE.NONE;
		} else if (UvConst.RST_TYPE.JPG.equals(resultType)) {
			fileType = UvConst.FILE_TYPE.JPEG;
			compType = UvConst.COMP_TYPE.NONE;
		} else if (UvConst.RST_TYPE.TIFF.equals(resultType)) {
			fileType = UvConst.FILE_TYPE.TIFF;
			compType = UvConst.COMP_TYPE.JPEG2000_IN_TIFF;
		} else if (UvConst.RST_TYPE.PNG.equals(resultType)) {
			fileType = UvConst.FILE_TYPE.JPEG; // pdf to image ???????????? PNG??? ???????????? ?????? JPEG??? ??????
			compType = UvConst.COMP_TYPE.NONE;
		} else {
			throw new ImageConvertException(UvConst.ERR_CD.INVALID_RST_TYPE);
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
	public static int convertAfterPtoISingle(String desRoot, String des, String baseFName, String dotExt, String resultType,
			List<IcMaskingInfo> maskingInfos, List<String> desNames) throws ImageConvertException {
		int result = -1;
		
		String strIndex = String.format(UvConst.INDEX_FORMAT, 1);
		String outputName = desRoot + baseFName + strIndex + dotExt;
		
		if (UvConst.RST_TYPE.PNG.equals(resultType)) {
			result = convertItoI(outputName, outputName, resultType);
		} else {
			result = 0;
		}
		
		if (!isEmptyMaskingInfos(maskingInfos)) {
			result = convertItoIWithMask(outputName, outputName, resultType, maskingInfos);
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
	public static int convertAfterPtoIMulti(int totalCount, String desRoot, String des, String baseFName, String dotExt, String resultType,
			List<IcMaskingInfo> maskingInfos, List<String> desNames, List<String> removePaths) throws ImageConvertException {
		int result = -1;
		String strIndex, outputName;
		
		if (UvConst.RST_TYPE.BMP.equals(resultType) || UvConst.RST_TYPE.JPG.equals(resultType)) {
			for (int i=0; i < totalCount; i++) {
				strIndex = String.format(UvConst.INDEX_FORMAT, (i+1));
				outputName = desRoot + baseFName + strIndex + dotExt;
				
				if (i == 0 && !isEmptyMaskingInfos(maskingInfos)) {
					result = convertItoIWithMask(outputName, outputName, resultType, maskingInfos);
				}
				
				desNames.add(getFileNameFromDesPath(outputName, desRoot));
			}
			
			result = 0;
		} else if (UvConst.RST_TYPE.TIFF.equals(resultType)) {
			String inputName = desRoot + baseFName + "_01" + dotExt;
			
			if (!isEmptyMaskingInfos(maskingInfos)) {
				result = convertItoIWithMask(inputName, inputName, resultType, maskingInfos);
			}
			
			for (int i=1; i < totalCount; i++) { // 01?????? ???????????? 02?????? ?????? ????????????
				strIndex = String.format(UvConst.INDEX_FORMAT, (i+1));
				outputName = desRoot + baseFName + strIndex + dotExt;
				
				result = imageIOJNI.mergeTIFF_FILE(inputName, outputName);
				
				if (result < 0) {
					throw new ImageConvertException(getErrorCode(result));
				}
				
				removePaths.add(outputName);
			}
			
			new File(inputName).renameTo(new File(des));
			desNames.add(getFileNameFromDesPath(des, desRoot));
		} else if (UvConst.RST_TYPE.PNG.equals(resultType)) {
			for (int i=0; i < totalCount; i++) {
				strIndex = String.format(UvConst.INDEX_FORMAT, (i+1));
				outputName = desRoot + baseFName + strIndex + dotExt;
				
				if (i == 0 && !isEmptyMaskingInfos(maskingInfos)) {
					result = convertItoIWithMask(outputName, outputName, resultType, maskingInfos);
				} else {
					result = convertItoI(outputName, outputName, resultType);
				}
				
				desNames.add(getFileNameFromDesPath(outputName, desRoot));
			}
		} else {
			throw new ImageConvertException(UvConst.ERR_CD.INVALID_RST_TYPE);
		}
		
		if (result < 0) {
			throw new ImageConvertException(getErrorCode(result));
		}
		
		return result;
	}
	
	// Image(Multi tiff) to tif list and make PDF
	public static int extractTiffNItoP(int totalCount, String src, String desRoot, String des, String baseFName, String dotExt,
			List<IcMaskingInfo> maskingInfos, List<String> removePaths) throws ImageConvertException {
		
		int result = -1;
		String strIndex, outputName, jSingleTiffLIst = "";
		
		for (int i=0; i < totalCount; i++) {
			strIndex = String.format(UvConst.INDEX_FORMAT, (i+1));
			outputName = desRoot + baseFName + strIndex + dotExt;
			jSingleTiffLIst += outputName + UvConst.DELIMITER;
			
			result = imageIOJNI.extractTIFF_FILE(src, i+1, outputName);
			
			if (result < 0) {
				throw new ImageConvertException(getErrorCode(result));
			}
			
			if (i == 0 && !isEmptyMaskingInfos(maskingInfos)) {
				result = convertItoIWithMask(outputName, outputName, UvConst.RST_TYPE.TIFF, maskingInfos);
			}
			removePaths.add(outputName);
		}
		
		result = convertItoP(jSingleTiffLIst, des);
		
		return result;
	}
	
	
	public static void main(String[] args) {
		// ???????????? -> ???????????? ???????????? ?????? start
		ConvertRequest request = new ConvertRequest();
		request.setConvType(UvConst.CONV_TYPE.ITP); // ?????? ??????
		request.setRstType(UvConst.RST_TYPE.PDF);	// ?????? ??????
		request.setJobId("jobId1");
		request.setSrcPath("D:\\convert\\sample_jpg.jpg");
		request.setDesFileName("converted.pdf");
		request.setDesRootPath("D:\\convert\\");
		request.setJobType(UvConst.JOB_TYPE.REALTIME); // ????????? ??? ?????? ??????
		
		// ???????????????(?????? 5???)
		List<IcMaskingInfo> maskingInfos = new ArrayList<IcMaskingInfo>();
		maskingInfos.add(new IcMaskingInfo(100, 100, 300, 50));
		maskingInfos.add(new IcMaskingInfo(100, 200, 300, 50));
		maskingInfos.add(new IcMaskingInfo(100, 300, 300, 50));
		maskingInfos.add(new IcMaskingInfo(100, 400, 300, 50));
		maskingInfos.add(new IcMaskingInfo(100, 500, 300, 50));
		
		request.setMaskInfos(maskingInfos);
		// ???????????? -> ???????????? ???????????? ?????? end
		
		// ??????????????? ????????? ???????????? ????????????
		String errorCode = UvConst.ERR_CD.SUCCESS;
		// JNI ???????????????????????? ?????? ?????????
		int result = -1;
		// ????????? ?????? ?????????(??? : filename.jpg ??? filename)
		String baseName = "";
		// dot ?????? ?????????(??? : filename.jpg ??? .jpg)
		String dotExt = "";
		// ?????????????????? full path
		String desPath = "";
		// ???????????? ????????? ??????(??????????????? ????????? ??????)
		List<String> desNames = new ArrayList<String>();
		// ????????? ????????? ?????????????????? ??????(?????? ???????????? ??????)
		List<String> removePaths = new ArrayList<String>();
		
		try {
			baseName = FilenameUtils.getBaseName(request.getDesFileName()); 
			dotExt = "." + FilenameUtils.getExtension(request.getDesFileName());
			desPath = request.getDesRootPath() + File.separator + request.getDesFileName();
			
			if (UvConst.CONV_TYPE.ITP.equals(request.getConvType())) {
				int totalCount = getImageTotalCount(request.getSrcPath());
				
				if (totalCount == 1) {
					if (!isEmptyMaskingInfos(request.getMaskInfos())) {
						result = convertItoIWithMask(request.getSrcPath(), desPath, UvConst.RST_TYPE.JPG, request.getMaskInfos());
					} else {
						result = convertItoI(request.getSrcPath(), desPath, UvConst.RST_TYPE.JPG);
					}
					
					result = convertItoP(desPath + UvConst.DELIMITER, desPath);
					
					desNames.add(getFileNameFromDesPath(desPath, request.getDesRootPath()));
				} else if (totalCount > 1) {
					result = extractTiffNItoP(totalCount, request.getSrcPath(), request.getDesRootPath(), desPath, baseName, dotExt, request.getMaskInfos(), removePaths);
					
					desNames.add(getFileNameFromDesPath(desPath, request.getDesRootPath()));
				}
			} else if (UvConst.CONV_TYPE.PTI.equals(request.getConvType())) {
				int totalCount = getPdfTotalCount(request.getSrcPath());
				
				if (totalCount == 1) {
					result = convertPtoI(request.getSrcPath(), request.getDesRootPath(), request.getDesFileName(), request.getRstType());
					result = convertAfterPtoISingle(request.getDesRootPath(), desPath, baseName, dotExt, request.getRstType(), request.getMaskInfos(), desNames);
				} else if (totalCount > 1) {
					// TODO : rst_type??? bmp??? ??????, ?????????????????? ???. ?????? ??????
					// TODO : rst_type??? png??? ??????, PtoI / ItoI ?????? ???????????? ???????????? ??????. ?????? ??????
					
					result = convertPtoI(request.getSrcPath(), request.getDesRootPath(), request.getDesFileName(), request.getRstType());
					result = convertAfterPtoIMulti(totalCount, request.getDesRootPath(), desPath, baseName, dotExt, request.getRstType(), request.getMaskInfos(), desNames, removePaths);
				}
			} else if (UvConst.CONV_TYPE.ITI.equals(request.getConvType())) {
				int totalCount = getImageTotalCount(request.getSrcPath());
				
				if (totalCount == 1) {
					if (!isEmptyMaskingInfos(request.getMaskInfos())) {
						result = convertItoIWithMask(request.getSrcPath(), desPath, request.getRstType(), request.getMaskInfos());
					} else {
						result = convertItoI(request.getSrcPath(), desPath, request.getRstType());
					}
					
					desNames.add(getFileNameFromDesPath(desPath, request.getDesRootPath()));
				} else if (totalCount > 1) {
					result = convertItoIMulti(totalCount, request.getSrcPath(), request.getDesRootPath(), baseName, dotExt, request.getRstType(), request.getMaskInfos(), desNames);
				}
			}
			
			if (result < 0) {
				throw new ImageConvertException(getErrorCode(result));
			}
		} catch (ImageConvertException e) {
			System.out.println("ImageConvertException error : " + e.getErrorCode());
			errorCode = e.getErrorCode();
		} catch (Exception e) {
			System.out.println("Exception error : " + e.getMessage());
			errorCode = UvConst.ERR_CD.UNKNOWN;
		} finally {
			deleteFilesFromPath(removePaths);
		}
		
		System.out.println("desNames : " + desNames.toString());
		System.out.println("removePaths : " + removePaths.toString());
		
		ConvertResponse taskResult = new ConvertResponse();
		taskResult.setJobId(request.getJobId());
		taskResult.setDesNames(desNames);
		taskResult.setResponseCode(errorCode);
	}
	

	
}
