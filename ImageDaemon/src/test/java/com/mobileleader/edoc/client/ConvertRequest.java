package com.mobileleader.edoc.client;

import java.util.PriorityQueue;

public class ConvertRequest implements Comparable<ConvertRequest> {

	private String targetPath;
	
	private String destPath;
	
	private int mode;
	
	private String id;
	
	private String extention;

	public String getTargetPath() {
		return targetPath;
	}

	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}

	public String getDestPath() {
		return destPath;
	}

	public void setDestPath(String destPath) {
		this.destPath = destPath;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getExtention() {
		return extention;
	}

	public void setExtention(String extention) {
		this.extention = extention;
	}

	@Override
	public String toString() {
		return "ConvertRequest [targetPath=" + targetPath + ", destPath=" + destPath + ", mode=" + mode + ", id=" + id
				+ ", extention=" + extention + "]";
	}

	@Override
	public int compareTo(ConvertRequest target) { 
		return this.mode >= target.mode ? 1 : -1; 
	}
	
}
