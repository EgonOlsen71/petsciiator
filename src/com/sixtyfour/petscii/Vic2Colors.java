package com.sixtyfour.petscii;

/**
 * @author EgonOlsen
 * 
 */
public class Vic2Colors implements ColorMap {
	private final static int[] COLORS = new int[] { 0x000000, 0xFFFFFF, 0x813338, 0x75CEC8, 0x8E3C97, 0x56AC4D, 0x2E2C9B,
			0xEDF171, 0x8E5029, 0x553800, 0xC46C71, 0x4A4A4A, 0x7B7B7B, 0xA9FF9F, 0x706DEB, 0xB2B2B2 };

	@Override
	public int[] getColors() {
		return COLORS;
	}

}
