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

		boolean tedMode = "264".equals(getArgument("platform"));
		ColorMap colors = tedMode ? new TedColors() : new Vic2Colors();

		Logger.log("Using color map for: " + (tedMode ? "TED" : "VIC II"));

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

		Integer sboost = this.getIntArgument("symbolboost");
		float symbolBoost=1f;
		if (sboost != null) {
			symbolBoost=(float) sboost/10;
		}

		Integer dither = this.getIntArgument("dither");
		if (dither==null) {
			// Support legacy parameter
			dither = this.getIntArgument("koaladither");
		}

		Integer bgColor = getIntArgument("background");
		if (bgColor != null) {
			bgColor = Math.max(0, Math.min(tedMode ? 127 : 15, bgColor));
		}

		Boolean lowerCase = Boolean.valueOf(getArgument("lowercase"));
		Boolean noAlpha = Boolean.valueOf(getArgument("noalpha"));

		String algorithm = getArgument("colormapper");
		Algorithm algo = Algorithm.COLORFUL;
		if (algorithm != null && !algorithm.isEmpty()) {
			if (algorithm.equals("soft")) {
				algo = Algorithm.SOFT;
			} else if (algorithm.startsWith("dither")) {
				algo = Algorithm.DITHERED;
			}
		}

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

				if (bgColor != null) {
					bitmap.setBackgroundColor(bgColor);
				}
				bitmap.preprocess(algo, colors, boost);

				Petscii petscii = new Petscii(lowerCase);
				if (noAlpha) {
					petscii.removeAlphanumericChars();
				}

				if (formats.contains("bbs")) {
					petscii.removeControlCodes();
				}
				petscii.setSymbolBoost(symbolBoost);
				
				if (!excludes.isEmpty()) {
					for (String exclude : excludes) {
						Integer exi = Integer.valueOf(exclude.trim());
						petscii.excludeCharCodes(exi);
					}
				}

				ConvertedData data = bitmap.convertToPetscii(8, false, petscii, tedMode);

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
				if (formats.contains("koala") || formats.contains("hires")) {
					String piccy = pic.toString();
					if (dither==null) {
						dither = 100;
					}
					dither = Math.max(0, Math.min(dither,100));
					if (formats.contains("koala")) {
						KoalaConverter.convert(piccy, Saver.createTempFileName(pic, folder, "koala.koa").toString(), new Vic2Colors(), 1, ((float) dither)/100f, false);
					}
					if (formats.contains("hires")) {
						HiEddiConverter.convert(piccy, Saver.createTempFileName(pic, folder, "hires.hed").toString(), new Vic2Colors(), 1, ((float) dither)/100f, false);
					}
				}

				Logger.log("Background color is: " + data.getBackGroundColor());

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
				"/format=<image,basic,bbs,bin,koala,hires> - the output format(s). Multiple formats can be specified by separating them by kommas. Default is image,basic.");
		System.out.println(
				"/prescale=<1-4> - scales the image down before generating the PETSCII from it. This can help to reduce artifacts in some cases. A value of 4 basically results in a 80*50 image. Default is 1.");
		System.out.println(
				"/exclude=<code1,code2,code3...> - excludes additional characters from the conversion. The codes have to be valid PETSCII char codes.");
		System.out.println(
				"/noalpha=<true|false> - if true, all alphanumerical characters will be excluded from the conversion. Default is false.");
		System.out.println(
				"/colormapper=<colorful|soft|dither> - sets the mapper that maps the source image's colors to the VIC II colors. Default is 'colorful'");
		System.out.println(
				"/colormode=<0|1|2> - sets the color conversion mode when using the soft color mapper. Usually, the impact of changing this isn't very huge. Default is 0.");
		System.out.println(
				"/lowercase=<true|false> - if true, the lower case PETSCII characters will be used for the conversion. Default is false.");
		System.out.println(
				"/background=<0-15> - overrides the auto detected background color. Can be useful to get more details in regions of the image, where the auto detected color isn't used much. Default is auto detect.");
		System.out.println(
				"/platform=<C64|264> - specifies the target platform, default is C64. The 264 platform (C16/C116/Plus4) offers more colors");
		System.out.println(
				"/symbolboost=<0-xxx> - values > 10 favour actual graphic symbols over other characters, values below favour other characters over symbols. Negative values will invert the image. Default is 10.");
		System.out.println(
				"/dither=<0-100> - Dithering strength for Koala Painter and Hires conversion. This doesn't affect the PETSCII conversion.");

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
