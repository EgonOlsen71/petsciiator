package com.sixtyfour.petscii;

/**
 * 
 * @author EgonOlsen
 */
public class TargetDimensions {
	
	private int width;
	
	private int height;
	
	private boolean keepRatio;
	
	private float pixelRatio;
	
	public TargetDimensions(int width, int height, boolean keepRatio) {
		this(width, height, keepRatio, 2);
	}
	
	public TargetDimensions(int width, int height, boolean keepRatio, float pixelRatio) {
		this.width = width;
		this.height = height;
		this.keepRatio = keepRatio;
		this.pixelRatio = pixelRatio;
	}
	
	public TargetDimensions() {
		//
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
	public boolean isKeepRatio() {
		return keepRatio;
	}
	public void setKeepRatio(boolean keepRatio) {
		this.keepRatio = keepRatio;
	}

	public float getPixelRatio() {
		return pixelRatio;
	}

	public void setPixelRatio(float pixelRatio) {
		this.pixelRatio = pixelRatio;
	}
	
	
}
