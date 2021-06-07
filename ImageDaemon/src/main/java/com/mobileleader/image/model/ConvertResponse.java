package com.mobileleader.image.model;

import java.util.List;

public class ConvertResponse {

	private String jobId; // 변환파일 고유아이디
	
	private String errorCode; // 변환결과 에러코드
	
	private List<String> desNames; // 변환결과 파일명목록

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
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
		builder.append("ConvertResult [jobId=").append(jobId).append(", errorCode=").append(errorCode)
				.append(", desNames=").append(desNames).append("]");
		return builder.toString();
	}
	
}
