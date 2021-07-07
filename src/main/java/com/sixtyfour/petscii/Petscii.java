package com.sixtyfour.petscii;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author EgonOlsen
 *
 */
public class Petscii {

	private List<int[]> chars = new ArrayList<>();
	private List<Value[]> values = new ArrayList<>();

	private float symbolBoost = 1;

	private final static int[] ALPHAS = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22,
			23, 24, 25, 26, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57 };

	private final static int[] CONTROL_CODES = { 34 };

	public Petscii(boolean lowerCase) {
		create(lowerCase);
	}

	public void excludeScreenCodes(int... screenCode) {
		if (screenCode == null) {
			return;
		}
		remove(screenCode);
	}

	public void excludeCharCodes(int... petsciiCode) {
		if (petsciiCode == null) {
			return;
		}
		for (int i = 0; i < petsciiCode.length; i++) {
			petsciiCode[i] = convert2ScreenCode(petsciiCode[i]);
		}
		remove(petsciiCode);
	}

	public float getSymbolBoost() {
		return symbolBoost;
	}

	public void setSymbolBoost(float symbolBoost) {
		this.symbolBoost = symbolBoost;
	}

	public void removeControlCodes() {
		excludeCharCodes(CONTROL_CODES);
	}

	public void removeAlphanumericChars() {
		remove(ALPHAS);
	}

	public void removeGraphicalChars() {
		List<Integer> nonAlphas = new ArrayList<>();
		for (int i = 0; i < 256; i++) {
			nonAlphas.add(i);
		}

		List<Integer> as = new ArrayList<>();
		for (int i = 0; i < ALPHAS.length; i++) {
			as.add(ALPHAS[i]);
		}
		as.add(32);
		as.add(160);

		nonAlphas.removeAll(as);
		int[] nas = new int[nonAlphas.size()];
		int pos = 0;
		for (Integer val : nonAlphas) {
			nas[pos++] = val;
		}
		remove(nas);
	}

	public static int convert2ScreenCode(int code) {
		if (code >= 0 && code <= 31) {
			return code + 128;
		}
		if (code >= 32 && code <= 63) {
			return code;
		}
		if (code >= 64 && code <= 95) {
			return code - 64;
		}
		if (code >= 96 && code <= 127) {
			return code - 32;
		}
		if (code >= 128 && code <= 159) {
			return code + 64;
		}
		if (code >= 160 && code <= 191) {
			return code - 64;
		}
		if (code >= 192 && code <= 223) {
			return code - 128;
		}
		if (code >= 224 && code <= 254) {
			return code - 128;
		}
		return 94;
	}

	public static int convert2CharCode(int code) {
		if (code == 94) {
			return 255;
		}
		if (code >= 128) {
			return convert2CharCode(code - 128);
		}
		if (code >= 32 && code <= 63) {
			return code;
		}
		if (code <= 31) {
			return code + 64;
		}
		if (code >= 64 && code <= 95) {
			return code + 32;
		}
		if (code >= 96 && code <= 127) {
			return code + 64;
		}
		throw new RuntimeException("Shouldn't be here...");
	}

	private void remove(int[] positions) {
		for (int pos : positions) {
			chars.set(pos, null);
			values.set(pos, null);
			if (pos < 128) {
				chars.set(pos + 128, null);
				values.set(pos + 128, null);
			}
		}
	}

	private void create(boolean lowerCase) {
		Bitmap bitmap = new Bitmap(lowerCase ? "/petscii_low.png" : "/petscii.png", false, 1);
		int[] pixels = bitmap.getPixels();
		int cnt = 0;
		for (int y = 0; y < 56 && cnt < 256; y += 8) {
			for (int x = 0; x < 320 && cnt < 256; x += 8) {
				int[] chary = new int[64];
				Value q1 = new Value();
				Value q2 = new Value();
				Value q3 = new Value();
				Value q4 = new Value();
				Value q5 = new Value();
				Value q6 = new Value();
				Value q7 = new Value();
				Value q8 = new Value();
				Value q9 = new Value();
				Value q10 = new Value();

				for (int xs = x; xs < x + 8; xs++) {
					for (int ys = y; ys < y + 8; ys++) {
						int pos = xs + ys * 320;
						int pc = pixels[pos];
						chary[(xs - x) + (ys - y) * 8] = (pc & 0x00ffffff);
						pc = pc & 1;
						if (pc == 0) {
							continue;
						}
						count(xs - x, ys - y, q1, q2, q3, q4, q5, q6, q7, q8, q9, q10);
					}
				}

				chars.add(chary);
				values.add(new Value[] { q1, q2, q3, q4, q5, q6, q7, q8, q9, q10 });
				cnt++;
			}
		}
	}

	public int getMatchingChar(Bitmap bitmap, int bgColor, int x, int y) {
		int width = bitmap.getImage().getWidth();
		int[] pixels = bitmap.getPixels();

		Value q1 = new Value();
		Value q2 = new Value();
		Value q3 = new Value();
		Value q4 = new Value();
		Value q5 = new Value();
		Value q6 = new Value();
		Value q7 = new Value();
		Value q8 = new Value();
		Value q9 = new Value();
		Value q10 = new Value();

		for (int xs = x; xs < x + 8; xs++) {
			for (int ys = y; ys < y + 8; ys++) {
				int pos = xs + ys * width;
				int pc = pixels[pos];
				if (pc == bgColor) {
					continue;
				}
				count(xs - x, ys - y, q1, q2, q3, q4, q5, q6, q7, q8, q9, q10);
			}
		}

		double dist = Double.MAX_VALUE;
		int idx = 0;
		for (int i = 0; i < values.size(); i++) {
			Value[] qs = values.get(i);
			if (qs != null) {
				float boost = 1;
				if ((i>=64 && i<= 127) || (i>=192 && i<= 255)) {
					boost=symbolBoost;
				}
				
				float d1 = qs[0].value - q1.value;
				float d2 = qs[1].value - q2.value;
				float d3 = qs[2].value - q3.value;
				float d4 = qs[3].value - q4.value;
				float d5 = qs[4].value - q5.value;
				float d6 = qs[5].value - q6.value;
				float d7 = qs[6].value - q7.value;
				float d8 = qs[7].value - q8.value;
				float d9 = qs[8].value - q9.value;
				float d10 = qs[9].value - q10.value;

				double delta = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3 + d4 * d4 + d5 * d5 + d6 * d6 + d7 * d7 + d8 * d8
						+ d9 * d9 + d10 * d10);
				delta/=boost;
				if (delta < dist) {
					idx = i;
					dist = delta;
				}
			}
		}

		return idx;
	}

	public int[] getCharAtIndex(int index) {
		return chars.get(index);
	}

	private void count(int x, int y, Value q1, Value q2, Value q3, Value q4, Value q5, Value q6, Value q7, Value q8,
			Value q9, Value q10) {
		if (x < 4 && y < 4) {
			q1.value++;
		}
		if (x > 3 && y < 4) {
			q2.value++;
		}
		if (x < 4 && y > 3) {
			q3.value++;
		}
		if (x > 3 && y > 3) {
			q4.value++;
		}
		if (x > 2 && x < 7) {
			if (y > 2 && y < 7) {
				q5.value++;
			}
		}
		if ((x == y) || ((7 - y) == x)) {
			q6.value++;
		}
		if (x == 0) {
			q7.value++;
		}
		if (x == 7) {
			q8.value++;
		}
		if (y == 0) {
			q9.value++;
		}
		if (y == 7) {
			q10.value++;
		}
	}

	private static class Value {
		public float value = 0;
	}

}
