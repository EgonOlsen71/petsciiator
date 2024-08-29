package com.sixtyfour.petscii.test.test;

import com.sixtyfour.petscii.AspectRatios;
import com.sixtyfour.petscii.Bitmap;

public class TestCropping {

	public static void main(String[] args) {
		Bitmap map = new Bitmap("examples/dog.png", AspectRatios.RATIO_C64, null, 1);
		map.save("results/cropped_dog.jpg");
		
		map = new Bitmap("examples/eisvogel.png", AspectRatios.RATIO_C64, null, 1);
		map.save("results/cropped_eisvogel.jpg");

	}

}
