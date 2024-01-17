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
     * @param ditherStrength
     * @param savePngCopy
     */
    public static void convert(String source, String target, ColorMap colors, float gamma, float ditherStrength, boolean savePngCopy) {
        long start = System.currentTimeMillis();
        ditherStrength = Math.min(1, ditherStrength);
        Bitmap image = new Bitmap(source, 160, 1);
       if (gamma!=1) {
           image.enhanceColors(gamma);
       }
        ColorReducer dither = new ColorReducer();
        if (ditherStrength>0) {
            dither.reduce(image, colors, ditherStrength);
        } else {
            dither.reduce(image, colors, false);
        }

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
