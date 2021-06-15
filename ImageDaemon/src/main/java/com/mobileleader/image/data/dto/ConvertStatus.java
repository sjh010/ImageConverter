package com.mobileleader.image.data.dto;

import java.sql.Date;

public class ConvertStatus {

	private String jobId;
	
	private int status;
	
	private String filePath;
	
	private Date startTime;

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ConvertStatus [jobId=");
		builder.append(jobId);
		builder.append(", status=");
		builder.append(status);
		builder.append(", filePath=");
		builder.append(filePath);
		builder.append(", startTime=");
		builder.append(startTime);
		builder.append("]");
		return builder.toString();
	}
}
