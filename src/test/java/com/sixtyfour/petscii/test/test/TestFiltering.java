package com.sixtyfour.petscii.test.test;

import com.sixtyfour.petscii.Bitmap;
import com.sixtyfour.petscii.TargetDimensions;

public class TestFiltering {

	public static void main(String[] args) {
		
		/**
		 *    public static final int FILTER_POINT = 1;
  
			  public static final int FILTER_BOX = 2;
			  
			  public static final int FILTER_TRIANGLE = 3;
			  
			  public static final int FILTER_HERMITE = 4;
			  
			  public static final int FILTER_HANNING = 5;
			  
			  public static final int FILTER_HAMMING = 6;
			  
			  public static final int FILTER_BLACKMAN = 7;
			  
			  public static final int FILTER_GAUSSIAN = 8;
			  
			  public static final int FILTER_QUADRATIC = 9;
			  
			  public static final int FILTER_CUBIC = 10;
			  
			  public static final int FILTER_CATROM = 11;
			  
			  public static final int FILTER_MITCHELL = 12;
			  
			  public static final int FILTER_LANCZOS = 13;
			  
			  public static final int FILTER_BLACKMAN_BESSEL = 14;
			  
			  public static final int FILTER_BLACKMAN_SINC = 15;
		 */
		
		String file = "examples/koala/memotech.jpg";
		
		TargetDimensions td = new TargetDimensions();
		td.setKeepRatio(true);
		td.setWidth(320);
		td.setHeight(200);
		td.setPixelRatio(1);
		
		for (int i=1; i<16; i++) {
			System.out.println("Processing filter type "+i);
			Bitmap map = new Bitmap(file, td, 1, i);
			map.save("results/filtered/memotech_"+i+".png");
		}
		
		file = "examples/koala/lines.png";
		
		for (int i=1; i<16; i++) {
			System.out.println("Processing filter type "+i);
			Bitmap map = new Bitmap(file, td, 1, i);
			map.save("results/filtered/lines"+i+".png");
		}
		

	}

}
