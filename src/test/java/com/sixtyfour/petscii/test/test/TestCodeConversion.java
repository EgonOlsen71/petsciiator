package com.sixtyfour.petscii.test.test;

import com.sixtyfour.petscii.Petscii;

/**
 * 
 * @author EgonOlsen
 *
 */
public class TestCodeConversion {

	public static void main(String[] args) {
		for (int i = 0; i < 256; i++) {
			int c1 = Petscii.convert2CharCode(i);
			int c2 = Petscii.convert2ScreenCode(c1);
			if (i>=128) {
				c2+=128;
			}
			if (c2!=i) {
				System.out.println("ERROR in conversion!");
			}
			System.out.println(i + "\t" + c1 + "\t" + c2);
		}
	}

}
