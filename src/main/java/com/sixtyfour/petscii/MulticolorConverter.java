package com.sixtyfour.petscii;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Converts a bitmap into Koala Painter format. 
 * 
 * Does this work?...yes!
 * Is this efficient?...no!
 * 
 * Do I care?...no!
 * 
 * @author EgonOlsen
 *
 */
public class MulticolorConverter {

	public Bitmap convert(Bitmap bitmap) {
		if (bitmap.getHeight()!=200 || bitmap.getWidth()!=160) {
			bitmap.resize(160, 200);
		}
		
		DataBufferInt data = (DataBufferInt) bitmap.getImage().getRaster().getDataBuffer();
		int[] pixels = data.getData();
		int[] indexMap = new int[pixels.length];
		
		ColorMap colors = new Vic2Colors();
		
		int background = findBackgroundColor(pixels, colors);
		for (int y=0; y<200; y+=8) {
			for (int x=0; x<160; x+=4) {
				processBlock(x, y, pixels, indexMap, colors, background);
			}
		}
		
		BufferedImage target = new BufferedImage(160, 200, BufferedImage.TYPE_INT_RGB);
		DataBufferInt dataTarget = (DataBufferInt) target.getRaster().getDataBuffer();
		int[] pixelsTarget = dataTarget.getData();
		
		for (int i=0; i<pixelsTarget.length; i++) {
			//Logger.log(indexMap[i]+"");
			pixelsTarget[i] = colors.getColors()[indexMap[i]];
		}
		
		Bitmap bitty = new Bitmap(target);
		bitty.setBackgroundColor(background);
		return bitty;
	}
	
	public byte[] createKoalaImage(Bitmap bitmap) {
		try {
			ColorMap colors = new Vic2Colors();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bos.write(0x00);
			bos.write(0x60);
			
			int background = bitmap.getBackgroundColor();
			
			int[] pixels = bitmap.getPixels();
			byte[] textRam = new byte[1000];
			byte[] colorRam = new byte[1000];
			for (int y=0; y<200; y+=8) {
				for (int x=0; x<160; x+=4) {
					processKoalaBlock(x, y, bos, pixels, textRam, colorRam, colors, background);
				}
			}
			bos.write(textRam);
			bos.write(colorRam);
			bos.write(background);
			/**
			 * The Commodore 64 version of KoalaPainter used a fairly simple file format corresponding directly to the way 
			 * bitmapped graphics are handled on the computer: 
			 * A two-byte load address, followed immediately by 8,000 bytes of raw bitmap data, 
			 * 1,000 bytes of raw "Video Matrix" data, 
			 * 1,000 bytes of raw "Color RAM" data, 
			 * and a one-byte Background Color field.
			 */
			
			return bos.toByteArray();
		} catch(Exception e) {
			Logger.log(e);
			return null;
		}
		
	}


	private void processKoalaBlock(int x, int y, ByteArrayOutputStream bos, int[] pixels, byte[] textRam,
			byte[] colorRam, ColorMap colors, int background) throws Exception {
		Map<Integer, Integer> cols = new HashMap<>();
		int ramPos = ((int) y/8)*40+(int)(x/4);
		for (int y2=y; y2<y+8; y2++) {
			BitSet bits = new BitSet(8);
			int bit = 7;
			for (int x2=x; x2<x+4; x2++) {
				int pos = y2*160+x2;
				int col = pixels[pos];
				int colIndex = colors.getClosestColorIndex(col);
				if (colIndex == background) {
					bit-=2;
					continue;
				}
				if (!cols.containsKey(colIndex)) {
					int size = cols.size();
					cols.put(colIndex, size);
					System.out.println(ramPos+"/"+colIndex+"/"+size);
					switch(size) {
						case 0:
								colorRam[ramPos]=(byte) (colIndex & 0xff);
								break;
						case 2: 
								textRam[ramPos]=(byte) ((textRam[ramPos] | (colIndex & 0xff)) & 0xff);
								break;
						case 1: 
								textRam[ramPos]=(byte) ((textRam[ramPos] | ((colIndex & 0xff)<<4))  & 0xff);
								break;
					}
					
				}
				int idx = cols.get(colIndex);
				switch (idx) {
					case 0:
						bits.set(bit);
						bits.set(bit-1);
						break;
					case 1:
						bits.set(bit-1);
						break;
					case 2:
						bits.set(bit);
						break;
				}
				bit-=2;
			}
			if (bits.length()==0) {
				bos.write(0);
			} else {
				bos.write(bits.toByteArray());
			}
		}
	}

	private void processBlock(int x, int y, int[] pixels, int[] indexMap, ColorMap colors, int background) {
		Map<Integer, Integer> colCnt = new HashMap<>();
		int i=0;
		for (int y2=y; y2<y+8; y2++) {
			for (int x2=x; x2<x+4; x2++) {
				int pos = y2*160+x2;
				int col = pixels[pos];
				int colIndex = colors.getClosestColorIndex(col);
				if (colIndex == background) {
					continue;
				}
				Integer cnt = colCnt.get(colIndex);
				if (cnt==null) {
					cnt = 0;
				}
				cnt++;
				colCnt.put(colIndex,  cnt);
			}
		}
		
		List<Integer> indices = new ArrayList<>(colCnt.keySet());
		Collections.sort(indices, new Comparator<Integer>() {
			@Override
			public int compare(Integer i0, Integer i1) {
				Integer c0 = colCnt.get(i0);
				Integer c1 = colCnt.get(i1);
				return c1.compareTo(c0);
			}
		});
		
		int[] colSet = new int[Math.min(4, indices.size()+1)];
		indices = indices.subList(0, Math.min(indices.size(),3));
		indices.add(background);
		for (i=0; i<indices.size(); i++) {
			colSet[i]=colors.getColors()[indices.get(i)];
		}
		
		/*
		for (i=0; i<colSet.length; i++) {
			Logger.log("Color "+i+" => "+indices.get(i)+"/"+colSet[i]);
		}
		*/
		
		for (int y2=y; y2<y+8; y2++) {
			for (int x2=x; x2<x+4; x2++) {
				int pos = y2*160+x2;
				int col = pixels[pos];
				indexMap[pos] = colors.getClosestColorIndex(colors.getClosestColor(col, colSet));
			}
		}
	}

	private int findBackgroundColor(int[] pixels, ColorMap colors) {
		int background = -1;
		Map<Integer, Integer> colCnt = new HashMap<>();
		for (int i=0; i<pixels.length; i++) {
			Integer colIndex = colors.getClosestColorIndex(pixels[i]);
			Integer cnt = colCnt.get(colIndex);
			if (cnt==null) {
				cnt=0;
			}
			cnt++;
			colCnt.put(colIndex, cnt);
		}
		
		int maxCnt=-1;
		for (Entry<Integer, Integer> colEntry:colCnt.entrySet()) {
			if (colEntry.getValue()>maxCnt) {
				background = colEntry.getKey();
				maxCnt=colEntry.getValue();
			}
		}
		
		Logger.log("Background color is: "+background+"/"+maxCnt);
		return background;
	}
	
}
