package com.mobileleader.edoc.sample;

import java.io.File;

import com.inzisoft.server.codec.ImageIOJNI;

public class I2ISample {
	protected static native int loadLicenseFile(String filename);

	public static void main(String[] args) {
		String homePath = args[0];// "D:/workspace/InziImgConverterSample";

		// 윈도우에서는 LoadLicense 필요 없음
//		int retval = ImageIOJNI.loadLicenseFile(filename);
//		System.out.println("InziI2I loadLicense result : " + retval);

		String tiff1 = homePath + "/sample/sample_01.tif";
		String tiff2 = homePath + "/sample/sample_02.tif";

		ImageIOJNI inziI2I = new ImageIOJNI();

		System.out.println("### Image type ###");
		int result = 0;
		result = inziI2I.getFileType_FILE(tiff1);
		System.out.println("tiff1 file type: " + result);
		result = inziI2I.getFileType_FILE(tiff2);
		System.out.println("tiff2 file type: " + result);

		/**
		 * MERGE TIFF
		 */
		System.out.println("### Merge TIFF ###");
		result = inziI2I.mergeTIFF_FILE(tiff1, tiff2);
		System.out.println("Merge tiff1 and 2.tiff result: " + result);

		int tiff1Page = inziI2I.getTIFFTotalPage_FILE(tiff1);
		System.out.println("Merged tiff page count: " + tiff1Page);

		/**
		 * EXTRACT TIFF
		 */
		System.out.println("### Extract Tiff page(1) ###");
		result = inziI2I.extractTIFF_FILE(tiff1, 1, homePath + "/sample/extractedTiff_1page.tiff");
		System.out.println("Extract result:" + result);
		System.out.println("Extract file path:" + homePath + "/sample/extractedTiff_1page.tiff");

		/**
		 * JPEG to TIFF
		 */
		System.out.println("### JPEG to TIFF ###");
		String jpg1 = homePath + "/sample/sample_jpg.jpg";
		File jpg1File = new File(jpg1);
		String fileName = jpg1File.getName();

		int fileType = inziI2I.getFileType_FILE(jpg1);
		System.out.println(fileName + " file type: " + fileType);

		result = inziI2I.convertFormat_FILE(jpg1, 4, // Tiff
				7, // Tiff Compress type
				0, // result file size
				0, // compress ratio (0~1)
				5, // quality level (5: highest)
				0, 0, 0, // image resize
				jpg1File.getParentFile().getPath() + File.separator + "result_convert.tiff");

		System.out.println("sample_jpg.jpg convert tiff result: " + result);
		System.out.println(
				"tiff file path: " + jpg1File.getParentFile().getPath() + File.separator + "result_convert.tiff");

		fileType = inziI2I.getFileType_FILE(homePath + "/sample/CF202012181656350b");
		System.out.println("fileType : " + fileType);
	}
}