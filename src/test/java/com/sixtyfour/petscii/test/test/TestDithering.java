package com.sixtyfour.petscii.test.test;

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
		Bitmap image = new Bitmap("examples/koala/test.jpg", false, 1);
		ColorReducer dither = new ColorReducer();
		dither.reduce(image, new Vic2Colors(), true);
		image.save("results/test1.png");
		
		image = new Bitmap("examples/koala/test.jpg", 160, 1);
		dither.reduce(image, new Vic2Colors(), true);
		image.resize(320, 200);
		image.save("results/test2.1.png");
		/*
		MulticolorConverter conv = new MulticolorConverter();
		Bitmap mci = conv.convert(image);
		byte[] koala = conv.createKoalaImage(mci);
		Logger.log("Koala image size: "+koala.length);
		Saver.saveByteArray("E:/src/workspace2018/Petsciiator/results/test2.koa", koala);
		mci.save("E:/src/workspace2018/Petsciiator/results/test2.1-koala.png");
		
		image = new Bitmap("E:/src/workspace2018/Petsciiator/examples/test.jpg", 160, 1);
		dither.reduce(image, new Vic2Colors(), 0.5f);
		image.resize(320, 200);
		image.save("E:/src/workspace2018/Petsciiator/results/test2.05.png");
		
		conv = new MulticolorConverter();
		mci = conv.convert(image);
		mci.save("E:/src/workspace2018/Petsciiator/results/test2.05-koala.png");
		
		image = new Bitmap("E:/src/workspace2018/Petsciiator/examples/test.jpg", 160, 1);
		dither.reduce(image, new Vic2Colors(), 0.1f);
		image.resize(320, 200);
		image.save("E:/src/workspace2018/Petsciiator/results/test2.01.png");
		
		conv = new MulticolorConverter();
		mci = conv.convert(image);
		mci.save("E:/src/workspace2018/Petsciiator/results/test2.01-koala.png");
		
		image = new Bitmap("E:/src/workspace2018/Petsciiator/examples/test.jpg", 160, 1);
		dither.reduce(image, new Vic2Colors(), false);
		image.resize(320, 200);
		image.save("E:/src/workspace2018/Petsciiator/results/test3.png");
		
		conv = new MulticolorConverter();
		mci = conv.convert(image);
		mci.save("E:/src/workspace2018/Petsciiator/results/test3-koala.png");
		*/

	}

}
