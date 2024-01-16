package com.sixtyfour.petscii;

import java.io.BufferedOutputStream;
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

	public static String savePetsciiBasicCode(File pic, ConvertedData data, File targetFolder) {
		List<String> code = data.getCode();
		String codeName = pic.toString().replace("\\", "/");
		if (!codeName.contains("/")) {
			codeName = "/" + codeName;
		}
		int pos = codeName.lastIndexOf(".");
		int pos2 = codeName.lastIndexOf("/");
		if (pos != -1 && pos2 != -1) {
			codeName = codeName.substring(pos2 + 1, pos) + ".bas";
			File file = new File(targetFolder, codeName);
			file.delete();
			Logger.log("Writing BASIC code: " + file);
			try (PrintWriter pw = new PrintWriter(file)) {
				for (String line : code) {
					pw.println(line);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			return unify(file.getPath());
		}
		return null;
	}

	public static void saveByteArray(String target, byte[] bytes) {
		Logger.log("Writing byte array " + target);
		try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(target))) {
			bos.write(bytes);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String savePetsciiImage(File pic, Bitmap bitmap, File targetFolder) {
		String picName = pic.toString().replace("\\", "/");
		if (!picName.contains("/")) {
			picName = "/" + picName;
		}
		int pos = picName.lastIndexOf(".");
		int pos2 = picName.lastIndexOf("/");
		if (pos != -1 && pos2 != -1) {
			picName = picName.substring(pos2 + 1, pos) + "_petscii.png";
			File file = new File(targetFolder, picName);
			file.delete();
			Logger.log("Writing image: " + file);
			bitmap.save(file.getPath());
			return unify(file.getPath());
		}
		return null;
	}

	public static String[] savePetsciiBin(File pic, ConvertedData data, File targetFolder) {
		int[] screen = data.getScreenRam();
		int[] color = data.getColorRam();

		String picName = pic.toString().replace("\\", "/");
		if (!picName.contains("/")) {
			picName = "/" + picName;
		}
		int pos = picName.lastIndexOf(".");
		int pos2 = picName.lastIndexOf("/");

		if (pos != -1 && pos2 != -1) {
			picName = picName.substring(pos2 + 1, pos) + "_(?).seq";
		}

		File file1 = new File(targetFolder, picName.replace("(?)", "screen"));
		file1.delete();
		File file2 = new File(targetFolder, picName.replace("(?)", "color"));
		file2.delete();
		File file3 = new File(targetFolder, picName.replace("(?)", "bgcolor"));
		file3.delete();

		Logger.log("Writing char data file: " + file1);

		try (OutputStream os = new FileOutputStream(file1)) {
			for (int val : screen) {
				os.write(val);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		Logger.log("Writing color data file: " + file2);

		try (OutputStream os = new FileOutputStream(file2)) {
			for (int val : color) {
				os.write(val);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		Logger.log("Writing background color data file: " + file3);

		try (OutputStream os = new FileOutputStream(file3)) {
			os.write(data.getBackGroundColor());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return new String[] {unify(file1.getPath()), unify(file2.getPath()), unify(file3.getPath())};
	}

	public static String savePetsciiBbs(File pic, ConvertedData data, File targetFolder) {
		int[] screen = data.getScreenRam();
		int[] color = data.getColorRam();

		int lastCol = -1;
		boolean reverse = false;

		String picName = pic.toString().replace("\\", "/");
		if (!picName.contains("/")) {
			picName = "/" + picName;
		}
		int pos = picName.lastIndexOf(".");
		int pos2 = picName.lastIndexOf("/");

		if (pos != -1 && pos2 != -1) {
			picName = picName.substring(pos2 + 1, pos) + "_bbs.seq";
		}

		File file = new File(targetFolder, picName);
		file.delete();

		Logger.log("Writing BBS file: " + file);
		int cnt = 0;

		try (OutputStream os = new FileOutputStream(file)) {
			// os.write(147);
			for (int i = 0; i < screen.length; i++) {
				int chr = screen[i];
				int col = color[i];
				if (col != lastCol) {
					if (reverse) {
						os.write(146);
						reverse = false;
					}
					os.write(COLOR_CODES[col & 15]);
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
				int cc = Petscii.convert2CharCode(chr);
				os.write(cc);
				if (cnt == 39) {
					cnt = 0;
					os.write(13);
					reverse = false;
					lastCol = -1;
					i++;
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return unify(file.getPath());
	}
	
	private static String unify(String path) {
		return path.replace('\\', '/');
	}

}
