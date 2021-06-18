package com.mobileleader.edoc.sample;

import com.inzisoft.pdf2image.InziPDF;

public class ImageConverterSample {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String homePath = args[0];
				
		String pdfPath = homePath + "/sample/guide.pdf";
		String resultPath = homePath + "/sample/result";
		int resolution = 50;
		int comprate = 70;
		int filetype = 4;
		int comptype = 7;
		int binarize = 0;
		int threshold =0;
		int page = 0;
		
			
		/**
		 * 전체 페이지 수
		 */
		int count = InziPDF.getPDFPageCount(pdfPath);
		System.out.println("getPDFPageCount : "+ count);
		
		/**
		 * 전체 페이지를 이미지로 변환
		 */
		int result = InziPDF.convertPDF2Image(pdfPath, resultPath, resolution, comprate, filetype, comptype, binarize, threshold);
		System.out.println("convertPDF2Image : " + result);
		
		/**
		 * 특정 페이지를 이미지로 변환
		 */
		result = InziPDF.convertPDF2NamedImageOnePage(pdfPath, resultPath,"test.tif", resolution, comprate, filetype, comptype, binarize, threshold, page);
		System.out.println("convertPDF2ImageOnePage : " + result);
		
		/**
		 * 특정 페이지를 PDF로 추출
		 */
		result = InziPDF.extractPDFPage(pdfPath, resultPath+"/result.pdf", 1);
		System.out.println("extractPDFPage : " + result);
		
	}

}
