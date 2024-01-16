package com.sixtyfour.petscii;

public abstract class AbstractColors implements ColorMap {

	@Override
	public int getClosestColor(int color) {
		return getClosestColor(color, getColors());
	}
	
	@Override
	public int getClosestColor(int color, int[] colors) {
		return colors[getClosestColorIndex(color, colors)];
	}
	
	@Override
	public int getClosestColorIndex(int color) {
		return getClosestColorIndex(color, getColors());
	}
	
	@Override
	public int getClosestColorIndex(int color, int[] colors) {
		int rc = (color & 0x00ff0000) >> 16;
		int gc = (color & 0x0000ff00) >> 8;
		int bc = color & 0xff;

		int idx = 0;
		int minIdx = 0;
		float minVal = Float.MAX_VALUE;

		for (int col : colors) {
			int rd = (col & 0x00ff0000) >> 16;
			int gd = (col & 0x0000ff00) >> 8;
			int bd = col & 0xff;

			float dr = rd - rc;
			float dg = gd - gc;
			float db = bd - bc;

			float dist = (float) Math.sqrt(dr * dr + dg * dg + db * db);
			if (dist < minVal) {
				minIdx = idx;
				minVal = dist;
			}
			idx++;
		}
		return minIdx;
	}
	

}
