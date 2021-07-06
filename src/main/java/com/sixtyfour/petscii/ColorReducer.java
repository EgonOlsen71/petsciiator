package com.sixtyfour.petscii;

/**
 * 
 * @author EgonOlsen
 *
 */
public class ColorReducer {

	public void reduce(Bitmap input, ColorMap palette, boolean dither) {
		int[] pixels = input.getPixels();
		int width = input.getWidth();
		int height = input.getHeight();

		Error error = new Error();

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pos = y * width + x;
				int col = pixels[pos];
				int newCol = palette.getClosestColor(col);
				pixels[pos] = newCol;

				if (dither) {
					error.set(col, newCol);

					if (x < width - 1) {
						pixels[pos + 1] = error.quantColor(pixels[pos + 1], 7d / 16d);
					}
					if (x > 0 && y < height - 1) {
						pixels[pos - 1 + width] = error.quantColor(pixels[pos - 1 + width], 3d / 16d);
					}
					if (y < height - 1) {
						pixels[pos + width] = error.quantColor(pixels[pos + width], 5d / 16d);
					}
					if (x < width - 1 && y < height - 1) {
						pixels[pos + width + 1] = error.quantColor(pixels[pos + width + 1], 1d / 16d);
					}
				}
			}
		}
	}

	private static class Error {
		private double rd, gd, bd;

		public Error() {
			//
		}

		public void set(int color1, int color2) {
			int r1 = (color1 & 0x00ff0000) >> 16;
			int g1 = (color1 & 0x0000ff00) >> 8;
			int b1 = color1 & 0xff;

			int r2 = (color2 & 0x00ff0000) >> 16;
			int g2 = (color2 & 0x0000ff00) >> 8;
			int b2 = color2 & 0xff;

			rd = r1 - r2;
			gd = g1 - g2;
			bd = b1 - b2;
		}

		public int quantColor(int color, double mul) {
			int r1 = (color & 0x00ff0000) >> 16;
			int g1 = (color & 0x0000ff00) >> 8;
			int b1 = color & 0xff;

			double rr = Math.max(0, Math.min(255, (r1 + rd * mul)));
			double gr = Math.max(0, Math.min(255, (g1 + gd * mul)));
			double br = Math.max(0, Math.min(255, (b1 + bd * mul)));

			r1 = ((int) rr) << 16;
			g1 = ((int) gr) << 8;
			b1 = ((int) br);

			return r1 | g1 | b1;
		}
	}
}
