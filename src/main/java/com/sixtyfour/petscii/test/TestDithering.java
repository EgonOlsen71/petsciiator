package com.sixtyfour.petscii.test;

import com.sixtyfour.petscii.Bitmap;
import com.sixtyfour.petscii.ColorReducer;
import com.sixtyfour.petscii.Vic2Colors;

/**
 * 
 * @author EgonOlsen
 *
 */
public class TestDithering {

	public static void main(String[] args) {
		Bitmap image = new Bitmap("E:/src/workspace2018/Petsciiator/examples/pet4032-12.jpg", false, 1);
		ColorReducer dither = new ColorReducer();
		dither.reduce(image, new Vic2Colors(), true);
		image.save("E:/src/workspace2018/Petsciiator/dither_test.png");
	}

}
