package com.sixtyfour.petscii.test.test;

import com.sixtyfour.petscii.Bitmap;

public class TestCropping {

	public static void main(String[] args) {
		Bitmap map = new Bitmap("examples/dog.png", 0, 1);
		int height = map.getHeight();
		int width = map.getWidth();
		width=(int) ((4f/3f)*height);
		map.crop(width, height);
		map.save("results/cropped_dog.jpg");
		
		map = new Bitmap("examples/eisvogel.png", 0, 1);
		map.crop(1024, 768);
		map.save("results/cropped_eisvogel.jpg");

	}

}
