package com.mobileleader.image.model;

public class IcMaskingInfo {

	private int x;
	
	private int y;
	
	private int width;
	
	private int height;
	
	public IcMaskingInfo() {
		super();
	}

	public IcMaskingInfo(int x, int y, int width, int height) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("IcMaskingInfo [x=").append(x).append(", y=").append(y).append(", width=").append(width)
				.append(", height=").append(height).append("]");
		return builder.toString();
	}
	
}
