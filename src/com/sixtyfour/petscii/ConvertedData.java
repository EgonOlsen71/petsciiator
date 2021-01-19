package com.sixtyfour.petscii;

import java.util.List;

/**
 * 
 * @author EgonOlsen
 *
 */
public class ConvertedData {

	private List<String> code;
	
	private int[] screenRam;
	
	private int[] colorRam;
	
	private int[] ascii;
	
	private int backGroundColor;

	public List<String> getCode() {
		return code;
	}

	public void setCode(List<String> code) {
		this.code = code;
	}

	public int[] getScreenRam() {
		return screenRam;
	}

	public void setScreenRam(int[] screenRam) {
		this.screenRam = screenRam;
	}

	public int[] getColorRam() {
		return colorRam;
	}

	public void setColorRam(int[] colorRam) {
		this.colorRam = colorRam;
	}

	public int[] getAscii() {
		return ascii;
	}

	public void setAscii(int[] ascii) {
		this.ascii = ascii;
	}

	public int getBackGroundColor() {
		return backGroundColor;
	}

	public void setBackGroundColor(int backGroundColor) {
		this.backGroundColor = backGroundColor;
	}
	
	
	
}
