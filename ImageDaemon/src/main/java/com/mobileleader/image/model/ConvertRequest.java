package com.mobileleader.image.model;

import java.util.List;

import com.mobileleader.image.type.JobType;

public class ConvertRequest implements Comparable<ConvertRequest> {

	private String jobId;		// 변환파일단위 고유아이디
	
	private String jobType; 	// 실시간/배치 여부(UvConst 참조)
	
	private String srcPath; 	// 변환할 파일 경로(fullPath)
	
	private String desRootPath; // 변환결과 저장폴더 경로
	
	private String desFileName; // 변환결과 파일명
	
	private String convType; 	// 변환방식(UvConst 참조)
	
	private String rstType; 	// 변환결과타입(UvConst 참조)
	
	private List<IcMaskingInfo> maskInfos; // 마스킹정보 목록(최대 5개)
	
	private String threadChangeYn = "N";
	
	private int threadChangeCount = 0;
	
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
		if (JobType.REALTIME.getCode().equals(this.jobType)) {
			if (JobType.REALTIME.getCode().equals(target.jobType)) { // 둘다 실시간
				return 0;
			} else { // 기존 배치, 신규 실시간
				return -1;
			}
		} else if (JobType.BATCH.getCode().equals(this.jobType)){
			if (JobType.REALTIME.getCode().equals(target.jobType)) { // 기존 실시간, 신규 배치
				return 1;
			} else { // 둘다 배치
				return 0;
			}
		} else {
			return 0;
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ConvertRequest [jobId=").append(jobId).append(", jobType=").append(jobType).append(", srcPath=")
				.append(srcPath).append(", desRootPath=").append(desRootPath).append(", desFileName=")
				.append(desFileName).append(", convType=").append(convType).append(", rstType=").append(rstType)
				.append(", maskInfos=").append(maskInfos).append(", threadChangeYn=").append(threadChangeYn)
				.append(", threadChangeCount=").append(threadChangeCount).append("]");
		return builder.toString();
	}
	
}
