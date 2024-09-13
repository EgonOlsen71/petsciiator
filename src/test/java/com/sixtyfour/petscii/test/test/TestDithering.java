package com.sixtyfour.petscii.test.test;

import com.sixtyfour.petscii.Bitmap;
import com.sixtyfour.petscii.BlackWhiteColors;
import com.sixtyfour.petscii.ColorReducer;
import com.sixtyfour.petscii.GrayWhiteColors;
import com.sixtyfour.petscii.Vic2Colors;

/**
 * 
 * @author EgonOlsen
 *
 */
public class TestDithering {

	public static void main(String[] args) {
		
		ColorReducer dither = new ColorReducer();
		Bitmap image = new Bitmap("C:\\Users\\EgonOlsen\\Desktop\\Zelda\\zelda1.png", false, 1);
		dither.reduce(image, new BlackWhiteColors(), true, 0.3f);
		image.save("C:\\Users\\EgonOlsen\\Desktop\\Zelda\\zelda1_bw.png");
		
		image = new Bitmap("C:\\Users\\EgonOlsen\\Desktop\\Zelda\\zelda2.png", false, 1);
		dither.reduce(image, new BlackWhiteColors(), true, 0.2f);
		image.save("C:\\Users\\EgonOlsen\\Desktop\\Zelda\\zelda2_bw.png");
		
		image = new Bitmap("C:\\Users\\EgonOlsen\\Desktop\\Zelda\\zelda3.png", false, 1);
		dither.reduce(image, new BlackWhiteColors(), true, 0.2f);
		image.save("C:\\Users\\EgonOlsen\\Desktop\\Zelda\\zelda3_bw.png");
		
		image = new Bitmap("C:\\Users\\EgonOlsen\\Desktop\\Zelda\\zelda4.png", false, 1);
		dither.reduce(image, new BlackWhiteColors(), true, 0.2f);
		image.save("C:\\Users\\EgonOlsen\\Desktop\\Zelda\\zelda4_bw.png");
		
		image = new Bitmap("C:\\Users\\EgonOlsen\\Desktop\\Zelda\\zelda5.png", false, 1);
		dither.reduce(image, new BlackWhiteColors(), true, 0.2f);
		image.save("C:\\Users\\EgonOlsen\\Desktop\\Zelda\\zelda5_bw.png");
		
		image = new Bitmap("C:\\Users\\EgonOlsen\\Desktop\\Zelda\\zelda6.png", false, 1);
		dither.reduce(image, new BlackWhiteColors(), true, 0.7f);
		image.save("C:\\Users\\EgonOlsen\\Desktop\\Zelda\\zelda6_bw.png");
		
		image = new Bitmap("C:\\Users\\EgonOlsen\\Desktop\\Zelda\\zelda7.png", false, 1);
		dither.reduce(image, new BlackWhiteColors(), true, 0.7f);
		image.save("C:\\Users\\EgonOlsen\\Desktop\\Zelda\\zelda7_bw.png");
		
		image = new Bitmap("C:\\Users\\EgonOlsen\\Desktop\\Zelda\\zelda8.png", false, 1);
		dither.reduce(image, new BlackWhiteColors(), true, 0.2f);
		image.save("C:\\Users\\EgonOlsen\\Desktop\\Zelda\\zelda8_bw.png");
		
		/*
		Bitmap image = new Bitmap("examples/koala/test.jpg", false, 1);
		ColorReducer dither = new ColorReducer();
		dither.reduce(image, new Vic2Colors(), true);
		image.save("results/test1.png");
		
		image = new Bitmap("examples/koala/test.jpg", 160, 1);
		dither.reduce(image, new Vic2Colors(), true);
		image.resize(320, 200);
		image.save("results/test2.1.png");
		*/
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
