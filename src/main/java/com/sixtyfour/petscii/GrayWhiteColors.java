package com.sixtyfour.petscii;

/**
 * A B/W palette
 * 
 * 
 * @author EgonOlsen
 *
 */
public class GrayWhiteColors extends AbstractColors {

	private final static int[] COLORS = new int[] { 0xFFFFFF, 0x303030};
	
	@Override
	public int[] getColors() {
		return COLORS;
	}

}
