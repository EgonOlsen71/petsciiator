package com.sixtyfour.petscii;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 
 * @author EgonOlsen
 *
 */
public class Petsciiator {

	private Map<String, String> arguments;
	private String source;

	public static void main(String[] args) {
		new Petsciiator(args).run();
	}

	public Petsciiator(String[] args) {
		System.out.println("\n***** Petsciiator - an image to PETSCII converter");
		System.out.println("(w) by EgonOlsen - https://github.com/EgonOlsen71");
		System.out.println("-------------------------------------------------");

		arguments = new HashMap<>();
		for (String arg : args) {
			if (arg.startsWith("-") || arg.startsWith("/")) {
				arg = arg.substring(1);
				if (arg.contains("=")) {
					String[] parts = arg.split("=");
					if (parts.length == 2) {
						arguments.put(parts[0].trim().toLowerCase(Locale.ENGLISH), parts[1].trim());
					}
				} else {
					arguments.put(arg.trim().toLowerCase(Locale.ENGLISH), null);
				}
			} else {
				source = arg.trim();
			}
		}

		System.out.println("type /? for more information\n\n");
	}

	public void run() {
		if (hasArgument("?")) {
			showHelp();
			exit(0);
		}
		
		if (source == null) {
			System.out.println("No input file specified - aborting!");
			exit(1);
		}
		
		convert();
	}

	private void convert() {
		File[] files = null;
		File src = new File(source);
		if (src.isDirectory()) {
			// Folder given
			files = src.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File file, String name) {
					name = name.toLowerCase(Locale.ENGLISH);
					return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png");
				}
			});
		} else {
			// One file given
			files = new File[] { src };
		}

		ColorMap colors = new Vic2Colors();

		int boost = 1;
		Integer boostArg = getIntArgument("colormode");
		if (boostArg != null) {
			boost = boostArg;
		}

		String targetFolder = getArgument("target");
		List<String> formats = getListArgument("format");
		if (formats.isEmpty()) {
			formats.add("image");
			formats.add("basic");
		}

		List<String> excludes = getListArgument("exclude");
		Integer scale = this.getIntArgument("prescale");
		if (scale == null) {
			scale = 1;
		}
		scale = Math.max(1, Math.min(4, scale));
		Boolean lowerCase = Boolean.valueOf(getArgument("lowercase"));
		Boolean noAlpha = Boolean.valueOf(getArgument("noalpha"));

		File folder = null;
		if (targetFolder != null && !targetFolder.isEmpty()) {
			folder = new File(targetFolder);
			if (folder.exists() && !folder.isDirectory()) {
				Logger.log(folder + " isn't a directory!");
				exit(2);
			}
			folder.mkdirs();
		}

		for (File pic : files) {
			try {
				String picName = pic.getName().toLowerCase();
				if (!picName.endsWith(".jpg") && !picName.endsWith(".jpeg") && !picName.endsWith(".png")) {
					Logger.log("Unsupported file format and/or extension: " + pic.getName());
					continue;
				}
				long start = System.currentTimeMillis();
				Logger.log("Converting " + pic);
				Bitmap bitmap = new Bitmap(pic.toString(), scale);

				bitmap.reduceColors(colors, boost);
				bitmap.rasterize(8, colors);

				Petscii petscii = new Petscii(lowerCase);
				if (noAlpha) {
					petscii.removeAlphanumericChars();
				}

				if (formats.contains("bbs")) {
					petscii.removeControlCodes();
				}
				if (!excludes.isEmpty()) {
					for (String exclude : excludes) {
						Integer exi = Integer.valueOf(exclude.trim());
						petscii.excludeCharCodes(exi);
					}
				}

				ConvertedData data = bitmap.convertToPetscii(8, false, petscii);

				if (formats.contains("image")) {
					Saver.savePetsciiImage(pic, bitmap, folder);
				}
				if (formats.contains("basic")) {
					Saver.savePetsciiBasicCode(pic, data, folder);
				}
				if (formats.contains("bbs")) {
					Saver.savePetsciiBbs(pic, data, folder);
				}
				if (formats.contains("bin")) {
					Saver.savePetsciiBin(pic, data, folder);
				}

				Logger.log("Conversion done in " + (System.currentTimeMillis() - start) + "ms!");
			} catch (Exception e) {
				Logger.log("Failed to process " + pic + ": " + e.getMessage());
			}
		}
	}

	private static void exit(int i) {
		System.out.println("\nREADY.");
		System.exit(i);
	}

	private void showHelp() {
		String appx = ".sh";
		if (System.getProperty("os.name").toLowerCase().contains("windows")) {
			appx = ".cmd";
		}
		System.out.println("Basic usage: petscii" + appx + " <source file or folder>");
		System.out.println(
				"\nThis will load the image(s), convert it/them into PETSCII and save the result as a PNG file as well as a BASIC program in ASCII format.\n");
		System.out.println("The target files' names will be derived from the original file's name.\n");
		System.out.println("All existing files of the same name will be overwritten.\n\n");
		System.out.println("Optional parameters (either with / or - as prefix):\n");
		System.out.println(
				"/target=<target folder> - the target folder in which the generated files will be written. Default is the current work directory.");
		System.out.println(
				"/format=<image,basic,bbs,bin> - the output format(s). Multiple formats can be specified by separating them by kommas. Default is image,basic.");
		System.out.println(
				"/prescale=<1-4> - scales the image down before generating the PETSCII from it. This can help to reduce artifacts in some cases. A value of 4 basically results in a 80*50 image. Default is 1.");
		System.out.println(
				"/exclude=<code1,code2,code3...> - excludes additional characters from the conversion. The codes have to be a valid PETSCII char codes.");
		System.out.println(
				"/noalpha=<true|false> - if true, all alphanumerical characters will be excluded from the conversion. Default is false.");
		System.out.println(
				"/colormode=<0|1|2> - sets the mode used for color conversions. Usually, the impact of changing this isn't very huge. Default is 0.");
		System.out.println(
				"/lowercase=<true|false> - if true, the lower case PETSCII characters will be used for the conversion. Default is false.");
	}

	private boolean hasArgument(String arg) {
		return arguments.containsKey(arg.toLowerCase(Locale.ENGLISH));
	}

	private String getArgument(String arg) {
		return arguments.get(arg.toLowerCase(Locale.ENGLISH));
	}

	private Integer getIntArgument(String arg) {
		String argy = arguments.get(arg.toLowerCase(Locale.ENGLISH));
		if (argy != null && !argy.isEmpty()) {
			try {
				return Integer.valueOf(argy);
			} catch (Exception e) {
				//
			}
		}
		return null;
	}

	private List<String> getListArgument(String arg) {
		String argy = arguments.get(arg.toLowerCase(Locale.ENGLISH));
		if (argy != null && !argy.isEmpty()) {
			argy = argy.toLowerCase(Locale.ENGLISH);
			String[] parts = argy.split(",");
			return Arrays.asList(parts);
		}
		return new ArrayList<>();
	}

	/*
	 * 
	 * public static void main(String[] args) { GraphicsDevice out =
	 * GraphicsDevice.openDevice(); ColorMap colors = new Vic2Colors();
	 * 
	 * File[] pics = new File("res/example/").listFiles(); for (File pic : pics) {
	 * String picName = pic.getName().toLowerCase(); if (picName.endsWith(".png") ||
	 * picName.endsWith(".jpg")) { Logger.log("Processing " + pic); Bitmap bitmap =
	 * new Bitmap(pic.toString()); // bitmap.load("res/example/c64.png");
	 * out.draw(bitmap); bitmap.reduceColors(colors, 1); out.draw(bitmap);
	 * bitmap.rasterize(8, colors); out.draw(bitmap);
	 * 
	 * Petscii petscii = new Petscii(true); petscii.removeControlCodes();
	 * ConvertedData data = bitmap.convertToPetscii(8, false, petscii);
	 * 
	 * //ConvertedData data = bitmap.convertToPetscii(8, false, false);
	 * out.draw(bitmap);
	 * 
	 * Saver.savePetsciiImage(pic, bitmap); Saver.savePetsciiBasicCode(pic, data);
	 * Saver.savePetsciiBbs(pic, data); } } out.dispose(); }
	 */
}
