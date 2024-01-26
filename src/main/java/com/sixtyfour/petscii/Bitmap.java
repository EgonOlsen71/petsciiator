package com.sixtyfour.petscii;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.RenderedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

/**
 * 
 * @author EgonOlsen
 *
 */
public class Bitmap {

	private BufferedImage img;
	private int[] pixels;
	private int[] foregroundColors;
	private int[] colorCodes;
	private int backgroundColor;
	private Integer overriddenBackgroundColor;
	private int backgroundColorIndex;

	public Bitmap(BufferedImage img) {
		this.img = img;
		grabPixels();
	}
	
	public Bitmap(String fileName, int scale) {
		TargetDimensions td = new TargetDimensions(320, 200, false);
		load(fileName, td, scale);
	}

	public Bitmap(String fileName, boolean fitTo320, int scale) {
		TargetDimensions td = fitTo320?new TargetDimensions(320, 200, false):null;
		load(fileName, td, scale);
	}
	
	public Bitmap(String fileName, int fitTo, int scale) {
		TargetDimensions td = null;
		if (fitTo>0) {
			td = new TargetDimensions(320, 200, false);
		}
		load(fileName, td, scale);
	}
	
	public Bitmap(String fileName, TargetDimensions fitTo, int scale) {
		load(fileName, fitTo, scale);
	}

	public BufferedImage getImage() {
		return img;
	}

	public int[] getPixels() {
		return pixels;
	}

	public int getWidth() {
		return img.getWidth();
	}

	public int getHeight() {
		return img.getHeight();
	}

	public void setBackgroundColor(int colorIndex) {
		this.overriddenBackgroundColor = colorIndex;
	}
	
	public int getBackgroundColor() {
		return this.overriddenBackgroundColor;
	}

	public void enhanceColors(float gamma) {
		for (int i=0; i<pixels.length; i++) {
			int color = pixels[i];
			float r = (color & 0x00ff0000) >> 16;
			float g = (color & 0x0000ff00) >> 8;
			float b = color & 0xff;
			r=(float) Math.pow(r, gamma);
			g=(float) Math.pow(g, gamma);
			b=(float) Math.pow(b, gamma);
			r=Math.min(255,r);
			g=Math.min(255,g);
			b=Math.min(255,b);
			color = ((int) r)<<16 | ((int) g)<<8 | ((int) b);
			pixels[i] = color;
		}
	}

	public ConvertedData convertToPetscii(int size, boolean raster, boolean lowerCase, boolean tedMode) {
		return convertToPetscii(size, raster, new Petscii(lowerCase), tedMode);
	}

	public ConvertedData convertToPetsciiNonAlpha(int size, boolean raster, boolean lowerCase, boolean tedMode) {
		Petscii petscii = new Petscii(lowerCase);
		petscii.removeAlphanumericChars();
		return convertToPetscii(size, raster, petscii, tedMode);
	}

	public ConvertedData convertToPetsciiOnlyAlpha(int size, boolean raster, boolean lowerCase, boolean tedMode) {
		Petscii petscii = new Petscii(lowerCase);
		petscii.removeGraphicalChars();
		return convertToPetscii(size, raster, petscii, tedMode);
	}

	public ConvertedData convertToPetscii(int size, boolean raster, Petscii petscii, boolean tedMode) {
		Logger.log("Converting to PETSCII...");

		List<String> code = new ArrayList<>();
		ConvertedData conv = new ConvertedData();
		conv.setCode(code);
		int vRam = 1024;
		int cRam = 55296;

		if (tedMode) {
			vRam = 3072;
			cRam = 2048;
			int color = 1 + backgroundColorIndex & 15;
			int intensity = (int) (backgroundColorIndex / 16);
			code.add("60000 color0," + color + "," + intensity + ":color4," + color + "," + intensity
					+ ":printchr$(147);");
		} else {
			code.add("60000 poke53280," + backgroundColorIndex + ":poke53281," + backgroundColorIndex
					+ ":printchr$(147);");
		}

		code.add("60010 fori=0to999:readp,c:poke" + vRam + "+i,p:poke" + cRam + "+i,c:next");
		code.add("60020 geta$:ifa$=\"\"then60020:end");

		int width = img.getWidth();
		int height = img.getHeight();

		int[] screenRam = new int[colorCodes.length];
		int[] colorRam = new int[colorCodes.length];

		int fgIdx = 0;
		int lineNumber = 60030;
		StringBuilder line = new StringBuilder();

		for (int y = 0; y < height; y += size) {
			for (int x = 0; x < width; x += size) {
				int idx = petscii.getMatchingChar(this, backgroundColor, x, y);
				int[] chary = petscii.getCharAtIndex(idx);

				for (int ys = 0; ys < 8; ys++) {
					for (int xs = 0; xs < 8; xs++) {
						int pc = chary[xs + 8 * ys];
						int pos = x + xs + (y + ys) * width;
						if (pc == 0) {
							pixels[pos] = backgroundColor;
						} else {
							pixels[pos] = foregroundColors[fgIdx];
						}
					}
				}

				if (raster) {
					for (int xs = x; xs < x + 8; xs++) {
						pixels[xs + y * width] = 0x00ffffff;
						pixels[xs + (y + 7) * width] = 0x00ffffff;
					}
					for (int ys = y; ys < y + 8; ys++) {
						pixels[x + ys * width] = 0x00ffffff;
						pixels[x + 7 + ys * width] = 0x00ffffff;
					}
				}

				if (line.length() == 0) {
					line.append(lineNumber++).append(" data ");
				} else {
					line.append(",");
				}
				if (idx > 255) {
					throw new RuntimeException("Char > 255 selected!");
				}
				line.append(idx).append(",").append(colorCodes[fgIdx]);
				if (line.length() > 70 || fgIdx == foregroundColors.length - 1) {
					code.add(line.toString());
					line.setLength(0);
				}
				screenRam[fgIdx] = idx;
				colorRam[fgIdx] = colorCodes[fgIdx];

				fgIdx++;
			}
		}

		conv.setScreenRam(screenRam);
		conv.setColorRam(colorRam);
		conv.setBackGroundColor(backgroundColorIndex);
		return conv;
	}

	public void preprocess(Algorithm colorAlgorithm, ColorMap colors, int boost) {
		if (colorAlgorithm == Algorithm.SOFT) {
			Logger.log("Using soft color reduction algorithm!");
			reduceColors(colors, boost);

		} else {
			Logger.log("Using " + ((colorAlgorithm == Algorithm.DITHERED) ? "dithered" : "colorful")
					+ " color reduction algorithm!");
			ColorReducer reducer = new ColorReducer();
			reducer.reduce(this, colors, colorAlgorithm == Algorithm.DITHERED);
		}
		rasterize(8, colors);
	}

	public void rasterize(int size, ColorMap colorMap) {
		Logger.log("Analyzing image...");

		int colorCount = colorMap.getColors().length;
		Map<Integer, Integer> color2Index = new HashMap<>();
		foregroundColors = new int[img.getWidth() / size * img.getHeight() / size];
		colorCodes = new int[foregroundColors.length];
		int idx = 0;
		for (int col : colorMap.getColors()) {
			color2Index.put(col, idx++);
		}

		int[] cnts = new int[colorCount];
		int bgColor = 0;

		for (int i = 0; i < pixels.length; i++) {
			int pc = pixels[i];
			idx = color2Index.get(pc);
			cnts[idx]++;
		}

		int maxIdx = 0;
		int maxCnt = 0;
		idx = 0;
		for (int cnt : cnts) {
			if (cnt > maxCnt) {
				maxCnt = cnt;
				maxIdx = idx;
			}
			idx++;
		}

		if (overriddenBackgroundColor != null) {
			Logger.log("Background color overridden: " + overriddenBackgroundColor);
			maxIdx = overriddenBackgroundColor;
		}
		bgColor = colorMap.getColors()[maxIdx];
		backgroundColorIndex = maxIdx;

		int width = img.getWidth();
		int fgIdx = 0;

		for (int y = 0; y < img.getHeight(); y += size) {
			for (int x = 0; x < width; x += size) {
				cnts = new int[colorCount];
				for (int xs = x; xs < x + size; xs++) {
					for (int ys = y; ys < y + size; ys++) {
						int pc = pixels[xs + ys * width];
						if (pc != bgColor) {
							cnts[color2Index.get(pc)]++;
						}
					}
				}

				maxIdx = 0;
				maxCnt = 0;
				idx = 0;
				for (int cnt : cnts) {
					if (cnt > maxCnt) {
						maxCnt = cnt;
						maxIdx = idx;
					}
					idx++;
				}

				int newColor = colorMap.getColors()[maxIdx];
				colorCodes[fgIdx] = maxIdx;
				foregroundColors[fgIdx++] = newColor;
			}
		}
		backgroundColor = bgColor;
	}

	public void reduceColors(ColorMap colorMap, int boost) {
		Logger.log("Reducing color depth...");
		int[] colorArray = colorMap.getColors();
		int colors = colorArray.length;
		int[] pix = new int[pixels.length];
		Map<Integer, Integer> cols = countColors(boost, pix, 224);
		List<ColorEntry> allColors = convertColors(colors, cols);

		Logger.log("Converting to VIC2 colors...");
		mapToColorValues(colorArray, allColors);
		assignColors(pix, allColors);

		System.arraycopy(pix, 0, pixels, 0, pix.length);
	}

	public void resize(int width, int height) {
		BufferedImage target = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		target.getGraphics().drawImage(img, 0, 0, width, height, null);
		img = target;
	}
	
	public void save(String name) {
		Logger.log("Writing image " + name);
		try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(name));
				ImageOutputStream ios = ImageIO.createImageOutputStream(bos)) {
			Iterator<ImageWriter> itty = ImageIO.getImageWritersBySuffix("png");
			if (itty.hasNext()) {
				ImageWriter iw = (ImageWriter) itty.next();
				ImageWriteParam iwp = iw.getDefaultWriteParam();
				iw.setOutput(ios);
				iw.write(null, new IIOImage((RenderedImage) img, null, null), iwp);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	private void load(String imgName, TargetDimensions td, int scale) {
		try {
			InputStream is = this.getClass().getResourceAsStream(imgName);
			if (is != null) {
				// Logger.log("Loading bitmap " + imgName + " from input stream...");
				img = ImageIO.read(is);
			} else {
				Logger.log("Loading bitmap " + imgName + " from file...");
				img = ImageIO.read(new File(imgName));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		if (img==null) {
			throw new RuntimeException("Failed to process image!");
		}
		BufferedImage target = null;
		if (td!=null) {
			int dif = scale;
			int width = td.getWidth();
			int height = td.getHeight();
			
			int owidth = width;
			int oheight = height;
			
			if (!td.isKeepRatio()) {
				// Scale to fit...
				BufferedImage target2 = new BufferedImage(width / dif, height / dif, BufferedImage.TYPE_INT_RGB);
				target2.getGraphics().drawImage(img, 0, 0, width / dif, height / dif, null);
	
				target = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				target.getGraphics().drawImage(target2, 0, 0, width, height, null);
			} else {
				float ratio = (float) img.getWidth()/(float) img.getHeight();
				float targetRatio = (float) td.getWidth()*td.getPixelRatio()/(float) td.getHeight();
				int offsetX=0;
				int offsetY=0;
				Logger.log("Image ratio: "+ratio+" / target ratio: "+targetRatio);
				if (ratio>=targetRatio) {
					height = (int) ((float) height / (ratio/targetRatio));
					offsetY = oheight/2-height/2;
				} else {
					width = (int) ((float) width / (targetRatio/ratio));
					offsetX = owidth/2-width/2;
				}
				Logger.log("New image dimensions are: "+width+"*"+height+" - "+offsetX+"/"+offsetY);
				// Keep aspect ration of original image...
				BufferedImage target2 = new BufferedImage(width / dif, height / dif, BufferedImage.TYPE_INT_RGB);
				target2.getGraphics().drawImage(img, 0, 0, width / dif, height / dif, null);
	
				target = new BufferedImage(owidth, oheight, BufferedImage.TYPE_INT_RGB);
				target.getGraphics().drawImage(target2, offsetX, offsetY, width, height, null);
			}
			
		} else {
			target = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
			target.getGraphics().drawImage(img, 0, 0, null);
		}
		img = target;
		grabPixels();
	}

	private void grabPixels() {
		DataBufferInt data = (DataBufferInt) img.getRaster().getDataBuffer();
		pixels = data.getData();
	}

	private Map<Integer, Integer> countColors(int boost, int[] pix, int colorClamp) {
		Map<Integer, Integer> cols = new HashMap<>();
		int width = img.getWidth();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				int pos = x + y * width;
				int pc = pixels[pos];
				pix[pos] = pc;
				int avgr = (pc >> 16) & 0xff;
				int avgg = (pc >> 8) & 0xff;
				int avgb = pc & 0xff;

				int key = (avgb & colorClamp) | ((avgg & colorClamp) << 8) | ((avgr & colorClamp) << 16);
				Integer cnt = cols.get(key);
				if (cnt == null) {
					cnt = 1;
				} else {
					cnt += 1;
					if (boost == 1) {
						cnt += (int) Math.sqrt((avgr >> 5) + (avgg >> 5) + (avgb >> 5));
					} else if (boost == 2) {
						cnt += (int) Math.log((avgr >> 4) + (avgg >> 4) + (avgb >> 4));
					}
				}
				cols.put(key, cnt);
			}
		}
		return cols;
	}

	private List<ColorEntry> convertColors(int colors, Map<Integer, Integer> cols) {
		List<ColorEntry> allColors = new ArrayList<>();
		for (Entry<Integer, Integer> entry : cols.entrySet()) {
			int color = entry.getKey();
			int count = entry.getValue();
			int sb = color & 255;
			int sg = (color >> 8) & 255;
			int sr = (color >> 16) & 255;
			ColorEntry c = new ColorEntry();
			c.b = sb;
			c.r = sr;
			c.g = sg;
			c.color = color;
			c.count = count;
			allColors.add(c);
		}

		Collections.sort(allColors);
		allColors = allColors.subList(0, Math.min(colors, allColors.size()));
		return allColors;
	}

	private void assignColors(int[] pix, List<ColorEntry> allColors) {
		for (int i = 0; i < pix.length; i++) {
			int dist = Integer.MAX_VALUE;
			int finalColor = 0;

			for (ColorEntry c : allColors) {
				int pc = pix[i];
				int ar = (pc >> 16) & 0xff;
				int ag = (pc >> 8) & 0xff;
				int ab = pc & 0xff;

				int dr = c.r - ar;
				int dg = c.g - ag;
				int db = c.b - ab;
				dr *= dr;
				dg *= dg;
				db *= db;
				int di = dr + dg + db;
				if (di < dist) {
					dist = di;
					finalColor = c.color;
				}
			}
			pix[i] = finalColor;
		}
	}

	private void mapToColorValues(int[] colorArray, List<ColorEntry> allColors) {
		for (ColorEntry c : allColors) {
			int dist = Integer.MAX_VALUE;
			int finalColor = 0;
			int asr = 0;
			int asb = 0;
			int asg = 0;

			for (int cmc : colorArray) {
				int ar = (cmc >> 16) & 0xff;
				int ag = (cmc >> 8) & 0xff;
				int ab = cmc & 0xff;

				int dr = c.r - ar;
				int dg = c.g - ag;
				int db = c.b - ab;

				dr *= dr;
				dg *= dg;
				db *= db;
				int di = dr + dg + db;
				if (di < dist) {
					dist = di;
					finalColor = cmc;

					asb = ab;
					asg = ag;
					asr = ar;
				}
			}

			c.color = finalColor;
			c.r = asr;
			c.g = asg;
			c.b = asb;
		}
	}

	private static class ColorEntry implements Comparable<ColorEntry> {

		public int count;
		public int color;
		public int r, g, b;

		@Override
		public int compareTo(ColorEntry b) {
			return b.count - this.count;
		}

	}

}
