package com.sixtyfour.petscii;

/**
 * 
 * @author EgonOlsen
 *
 */
public interface ColorMap {

	int[] getColors();
	
	int getClosestColor(int color);
	
	int getClosestColor(int color, int[] colors);

	int getClosestColorIndex(int color, int[] colors);

	int getClosestColorIndex(int color);
	
}
