package com.sixtyfour.petscii;

/**
 * Converts any image into a Koala Painter one
 *
 * @author EgonOlsen
 */
public class KoalaConverter {
	
	/**
	 * 
	 * @param source
	 * @param target
	 * @param colors
	 * @param gamma
	 * @param ditherStrength
	 * @param savePngCopy
	 */
	public static void convert(String source, String target, ColorMap colors, float gamma, float ditherStrength, boolean savePngCopy) {
		convert(source, target, colors, gamma, ditherStrength, false, false, false, savePngCopy);
	}
	
	
	/**
	 * 
	 * @param source
	 * @param target
	 * @param colors
	 * @param gamma
	 * @param ditherStrength
	 * @param keepRatio
	 * @param savePngCopy
	 */
	public static void convert(String source, String target, ColorMap colors, float gamma, float ditherStrength, boolean keepRatio, boolean savePngCopy) {
		convert(source, target, colors, gamma, ditherStrength, keepRatio, false, false, savePngCopy);
	}

	/**
	 * 
	 * @param source
	 * @param target
	 * @param colors
	 * @param gamma
	 * @param ditherStrength
	 * @param keepRatio
	 * @param needsCropping
	 * @param d42Mode
	 * @param savePngCopy
	 */
	public static void convert(String source, String target, ColorMap colors, float gamma, float ditherStrength, boolean keepRatio, boolean needsCropping, boolean d42Mode, boolean savePngCopy) {
        long start = System.currentTimeMillis();
        ditherStrength = Math.min(1, ditherStrength);
        
        Bitmap image;
        if (needsCropping) {
        	image = new Bitmap(source, AspectRatios.RATIO_C64, new TargetDimensions(160, 200, keepRatio), 1);
        } else {
        	image = new Bitmap(source, 0, new TargetDimensions(160, 200, keepRatio), 1, d42Mode?96:-1);
        }
        
        if (gamma!=1) {
           image.enhanceColors(gamma);
        }
        ColorReducer dither = new ColorReducer();
        if (ditherStrength>0) {
            dither.reduce(image, colors, ditherStrength);
        } else {
            dither.reduce(image, colors, false);
        }
        Logger.log("Rescaling and color reduction completed in "+(System.currentTimeMillis()-start)+"ms");
        
        MulticolorConverter conv = new MulticolorConverter(colors);
        Bitmap mci = conv.convert(image);
        byte[] koala = conv.createKoalaImage(mci);
        Saver.saveByteArray(target, koala);
        if (savePngCopy) {
            mci.save(target+".png");
        }
        Logger.log("Conversion completed in "+(System.currentTimeMillis()-start)+"ms");
    }

}
