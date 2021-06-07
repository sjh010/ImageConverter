package com.mobileleader.image.exception;

public class ImageConvertException extends Exception {

	private static final long serialVersionUID = 1L;

	private String errorCode;

	public ImageConvertException(String errorCode) {
		super();
		this.errorCode = errorCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	
}
