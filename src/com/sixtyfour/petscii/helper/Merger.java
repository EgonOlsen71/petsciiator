package com.sixtyfour.petscii.helper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;

import com.sixtyfour.petscii.Logger;

/**
 * 
 * @author EgonOlsen
 *
 */
public class Merger {

	private final static String PATH = "C:\\Users\\EgonOlsen\\Desktop\\petscii\\done";

	private final static int ADDRESS = 40960;

	public static void main(String[] args) {
		File[] files = new File(PATH).listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File arg0, String name) {
				return name.endsWith("_screen.seq");
			}
		});

		File target = new File(PATH, "merged");
		target.mkdirs();

		for (File file : files) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bos.write(ADDRESS & 0xff);
			bos.write(ADDRESS >> 8);

			File bgColorFile = new File(file.getPath().replace("_screen.seq", "_bgcolor.seq"));
			fill(bos, bgColorFile);
			
			fill(bos, file);

			File colorFile = new File(file.getPath().replace("_screen.seq", "_color.seq"));
			fill(bos, colorFile);

			File targetFile = new File(target, file.getName().replace("_screen.seq", ".prg"));
			try (OutputStream os = new FileOutputStream(targetFile)) {
				Logger.log("Writing " + targetFile);
				os.write(bos.toByteArray());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

	}

	private static void fill(ByteArrayOutputStream bos, File file) {
		Logger.log("Reading " + file);
		try (InputStream fis = new FileInputStream(file)) {
			byte[] buffer = new byte[1024];
			int size = 0;
			do {
				size = fis.read(buffer);
				if (size > 0) {
					bos.write(buffer, 0, size);
				}
			} while (size != -1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

}
