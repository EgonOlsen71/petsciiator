package com.sixtyfour.petscii;

/**
 * @author EgonOlsen
 * 
 */
public class Vic2VibrantColors extends AbstractColors {
	
	private final static int[] COLORS = new int[] { 0x000000, 0xFFFFFF, 0xFF0000, 0x7FFFFF, 0xF10EF1, 0x00AD28,
			0x0018FF, 0xFFFF42, 0xF36919, 0x953C27, 0xFFA8AE, 0x4A4A4A, 0x7B7B7B, 0x96FF26, 0x3FCFFF, 0xBCBCBC };

	@Override
	public int[] getColors() {
		return COLORS;
	}

}
