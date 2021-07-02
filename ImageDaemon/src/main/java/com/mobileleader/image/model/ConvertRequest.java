package com.mobileleader.image.model;

import java.util.List;

import com.mobileleader.image.type.JobType;

/**
 * 이미지 변환 request
 */
public class ConvertRequest implements Comparable<ConvertRequest> {
	
	private long id;

	private String jobId;		// 변환파일단위 고유아이디
	
	private String jobType; 	// 실시간/배치 여부(JobType ENUM 참조)
	
	private String srcPath; 	// 변환할 파일 경로(fullPath)
	
	private String desRootPath; // 변환결과 저장폴더 경로
	
	private String desFileName; // 변환결과 파일명
	
	private String convType; 	// 변환방식(ConvertType ENUM 참조)
	
	private String rstType; 	// 변환결과타입(ResultExtectionType ENUM 참조)
	
	private List<IcMaskingInfo> maskInfos; // 마스킹정보 목록(최대 5개)
	
	private String threadChangeYn = "N";
	
	private int threadChangeCount = 0;
	
	public ConvertRequest() {
		this.id = System.currentTimeMillis();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public String getSrcPath() {
		return srcPath;
	}

	public void setSrcPath(String srcPath) {
		this.srcPath = srcPath;
	}

	public String getDesRootPath() {
		return desRootPath;
	}

	public void setDesRootPath(String desRootPath) {
		this.desRootPath = desRootPath;
	}

	public String getDesFileName() {
		return desFileName;
	}

	public void setDesFileName(String desFileName) {
		this.desFileName = desFileName;
	}

	public String getConvType() {
		return convType;
	}

	public void setConvType(String convType) {
		this.convType = convType;
	}

	public String getRstType() {
		return rstType;
	}

	public void setRstType(String rstType) {
		this.rstType = rstType;
	}

	public List<IcMaskingInfo> getMaskInfos() {
		return maskInfos;
	}

	public void setMaskInfos(List<IcMaskingInfo> maskInfos) {
		this.maskInfos = maskInfos;
	}
	
	public String getThreadChangeYn() {
		return threadChangeYn;
	}

	public void setThreadChangeYn(String threadChangeYn) {
		this.threadChangeYn = threadChangeYn;
	}

	public int getThreadChangeCount() {
		return threadChangeCount;
	}

	public void setThreadChangeCount(int threadChangeCount) {
		this.threadChangeCount = threadChangeCount;
	}

	/**
	 * 우선순위 큐에 삽입하기 위한 compareble 구현
	 */
	@Override
	public int compareTo(ConvertRequest target) { 
		int result = 0;
		
		if (this.jobType.equals(target.getJobType())) {
			if (this.id <= target.getId()) {
				result = -1;
			} else {
				result = 1;
			}
		} else if (!this.jobType.equals(target.getJobType())) {
			if (JobType.REALTIME.getCode().equalsIgnoreCase(target.getJobType())) {
				result = 1;
			} else if (JobType.BATCH.getCode().equalsIgnoreCase(target.getJobType())) {
				result = -1;
			}
		}

		return result;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ConvertRequest [jobId=");
		builder.append(jobId);
		builder.append(", jobType=");
		builder.append(jobType);
		builder.append(", srcPath=");
		builder.append(srcPath);
		builder.append(", desRootPath=");
		builder.append(desRootPath);
		builder.append(", desFileName=");
		builder.append(desFileName);
		builder.append(", convType=");
		builder.append(convType);
		builder.append(", rstType=");
		builder.append(rstType);
		builder.append(", maskInfos=");
		builder.append(maskInfos);
		builder.append(", threadChangeYn=");
		builder.append(threadChangeYn);
		builder.append(", threadChangeCount=");
		builder.append(threadChangeCount);
		builder.append("]");
		return builder.toString();
	}

}
