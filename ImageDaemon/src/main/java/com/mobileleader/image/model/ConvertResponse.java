package com.mobileleader.image.model;

import java.util.List;

/**
 * 이미지 변환 response
 */
public class ConvertResponse {

	private String jobId; // 변환파일 고유아이디
	
	private String responseCode; // 변환결과 에러코드
	
	private String desRootPath; // 변환결과 저장폴더 경로
	
	private List<String> desNames; // 변환결과 파일명목록
	
	public ConvertResponse() {
		super();
	}

	
	public ConvertResponse(String responseCode) {
		super();
		this.responseCode = responseCode;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String errorCode) {
		this.responseCode = errorCode;
	}

	public String getDesRootPath() {
		return desRootPath;
	}

	public void setDesRootPath(String desRootPath) {
		this.desRootPath = desRootPath;
	}

	public List<String> getDesNames() {
		return desNames;
	}

	public void setDesNames(List<String> desNames) {
		this.desNames = desNames;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ConvertResponse [jobId=").append(jobId).append(", responseCode=").append(responseCode)
				.append(", desRootPath=").append(desRootPath).append(", desNames=").append(desNames).append("]");
		return builder.toString();
	}

	
}
