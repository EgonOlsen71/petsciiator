package com.sixtyfour.petscii;

/**
 * 
 * @author EgonOlsen
 *
 */
public class TedColors extends AbstractColors {

	private final static int[] COLORS = new int[128];
	static {
		Bitmap colMap = new Bitmap("/c264.png", false, 1);
		int cnt = 0;
		for (int y = 8; y <= 56; y += 16) {
			for (int x = 8; x <= 632; x += 16) {
				int pos = y * 640 + x;
				int col = colMap.getPixels()[pos];
				COLORS[cnt++] = col & 0x00ffffff;
				if (cnt == 128) {
					break;
				}
			}
		}
	}

	@Override
	public int[] getColors() {
		return COLORS;
	}

}
