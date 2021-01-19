package com.sixtyfour.petscii;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

/**
 * 
 * @author EgonOlsen
 *
 */
public class Saver {

	private static final int[] COLOR_CODES = { 144, 5, 28, 159, 156, 30, 31, 158, 129, 149, 150, 151, 152, 153, 154,
			155 };

	public static void savePetsciiBasicCode(File pic, ConvertedData data) {
		List<String> code = data.getCode();
		String codeName = pic.toString().replace("\\", "/");
		int pos = codeName.lastIndexOf(".");
		int pos2 = codeName.lastIndexOf("/");
		if (pos != -1 && pos2 != -1) {
			String folder = codeName.substring(0, pos2 + 1) + "code/";
			new File(folder).mkdirs();
			codeName = folder + codeName.substring(pos2 + 1, pos) + ".bas";
			Logger.log("Writing " + codeName);
			try (PrintWriter pw = new PrintWriter(new File(codeName))) {
				for (String line : code) {
					pw.println(line);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

	}

	public static void savePetsciiImage(File pic, Bitmap bitmap) {
		String picName = pic.toString().replace("\\", "/");
		int pos = picName.lastIndexOf(".");
		int pos2 = picName.lastIndexOf("/");
		if (pos != -1 && pos2 != -1) {
			String folder = picName.substring(0, pos2 + 1) + "petscii/";
			new File(folder).mkdirs();
			picName = folder + picName.substring(pos2 + 1, pos) + "_petscii.png";
			Logger.log("Writing " + picName);
			bitmap.save(picName);
		}
	}

	public static void savePetsciiBbs(File pic, ConvertedData data) {
		int[] screen = data.getScreenRam();
		int[] color = data.getColorRam();

		int lastCol = -1;
		boolean reverse = false;

		String picName = pic.toString().replace("\\", "/");
		int pos = picName.lastIndexOf(".");
		int pos2 = picName.lastIndexOf("/");
		if (pos != -1 && pos2 != -1) {
			String folder = picName.substring(0, pos2 + 1) + "bbs/";
			new File(folder).mkdirs();
			picName = folder + picName.substring(pos2 + 1, pos) + "_bbs.seq";
		}

		Logger.log("Writing " + picName);
		int cnt=0;

		try (OutputStream os = new FileOutputStream(new File(picName))) {
			//os.write(147);
			for (int i = 0; i < screen.length; i++) {
				int chr = screen[i];
				int col = color[i];
				if (col != lastCol) {
					if (reverse) {
						os.write(146);
						reverse = false;
					}
					os.write(COLOR_CODES[col]);
					lastCol = col;
				}
				if (chr >= 128 && !reverse) {
					os.write(18);
					reverse = true;
				} 
				if (chr < 128 && reverse) {
					os.write(146);
					reverse = false;
				}
				cnt++;
				int cc=Petscii.convert2CharCode(chr);
				os.write(cc);
				if (cnt==39)  {
					cnt=0;
					os.write(13);
					reverse=false;
					lastCol=-1;
					i++;
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

}
