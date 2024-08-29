package com.sixtyfour.petscii;

/**
 * A B/W palette
 * 
 * 
 * @author EgonOlsen
 *
 */
public class BlackWhiteColors extends AbstractColors {

	private final static int[] COLORS = new int[] { 0x000000, 0xFFFFFF};
	
	@Override
	public int[] getColors() {
		return COLORS;
	}

}
