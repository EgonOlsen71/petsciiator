package com.sixtyfour.petscii;

/**
 * 
 * @author EgonOlsen
 *
 */
public interface ColorMap {

	int[] getColors();
	
	int getClosestColor(int color);
	
}
