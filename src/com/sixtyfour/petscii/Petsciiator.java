package com.sixtyfour.petscii;

import java.io.File;

/**
 * 
 * @author EgonOlsen
 *
 */
public class Petsciiator {

	public static void main(String[] args) {
		GraphicsDevice out = GraphicsDevice.openDevice();
		ColorMap colors = new Vic2Colors();

		File[] pics = new File("res/example/").listFiles();
		for (File pic : pics) {
			String picName = pic.getName().toLowerCase();
			if (picName.endsWith(".png") || picName.endsWith(".jpg")) {
				Logger.log("Processing " + pic);
				Bitmap bitmap = new Bitmap(pic.toString());
				// bitmap.load("res/example/c64.png");
				out.draw(bitmap);
				bitmap.reduceColors(colors, 1);
				out.draw(bitmap);
				bitmap.rasterize(8, colors);
				out.draw(bitmap);
				
				Petscii petscii = new Petscii(true);
				petscii.removeControlCodes();
				ConvertedData data = bitmap.convertToPetscii(8, false, petscii);
				
				//ConvertedData data = bitmap.convertToPetscii(8, false, false);
				out.draw(bitmap);

				Saver.savePetsciiImage(pic, bitmap);
				Saver.savePetsciiBasicCode(pic, data);
				Saver.savePetsciiBbs(pic, data);
			}
		}
		out.dispose();
	}
}
