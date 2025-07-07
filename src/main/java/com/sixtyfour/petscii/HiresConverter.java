package com.sixtyfour.petscii;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Converts a bitmap into Hires format. 
 * 
 * Does this work?...yes!
 * Is this efficient?...no!
 * 
 * Do I care?...no!
 * 
 * @author EgonOlsen
 *
 */
public class HiresConverter {

	private ColorMap colors = new Vic2VibrantColors();

	public HiresConverter() {
		//
	}

	public HiresConverter(ColorMap colors) {
		this.colors = colors;
	}

	public Bitmap convert(Bitmap bitmap) {
		if (bitmap.getHeight()!=200 || bitmap.getWidth()!=320) {
			bitmap.resize(320, 200);
		}
		
		DataBufferInt data = (DataBufferInt) bitmap.getImage().getRaster().getDataBuffer();
		int[] pixels = data.getData();
		int[] indexMap = new int[pixels.length];
		
		for (int y=0; y<200; y+=8) {
			for (int x=0; x<320; x+=8) {
				processBlock(x, y, pixels, indexMap, colors);
			}
		}
		
		BufferedImage target = new BufferedImage(320, 200, BufferedImage.TYPE_INT_RGB);
		DataBufferInt dataTarget = (DataBufferInt) target.getRaster().getDataBuffer();
		int[] pixelsTarget = dataTarget.getData();
		
		for (int i=0; i<pixelsTarget.length; i++) {
			//Logger.log(indexMap[i]+"");
			pixelsTarget[i] = colors.getColors()[indexMap[i]];
		}
		
		Bitmap bitty = new Bitmap(target);
		return bitty;
	}
	
	public byte[] createHiresImage(Bitmap bitmap) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bos.write(0x00);
			bos.write(0x20);
			
			int[] pixels = bitmap.getPixels();
			byte[] textRam = new byte[1024];
			for (int y=0; y<200; y+=8) {
				for (int x=0; x<320; x+=8) {
					processHiresBlock(x, y, bos, pixels, textRam, colors);
				}
			}
			bos.write(new byte[192]);
			bos.write(textRam);
			return bos.toByteArray();
		} catch(Exception e) {
			Logger.log(e);
			return null;
		}
		
	}


	private void processHiresBlock(int x, int y, ByteArrayOutputStream bos, int[] pixels, byte[] textRam, ColorMap colors) throws Exception {
		Map<Integer, Integer> cols = new HashMap<>();
		int ramPos = ((int) y/8)*40+(int)(x/8);
		for (int y2=y; y2<y+8; y2++) {
			BitSet bits = new BitSet(8);
			int bit = 7;
			for (int x2=x; x2<x+8; x2++) {
				int pos = y2*320+x2;
				int col = pixels[pos];
				int colIndex = colors.getClosestColorIndex(col);
				if (!cols.containsKey(colIndex)) {
					int size = cols.size();
					cols.put(colIndex, size);
					//System.out.println(ramPos+"/"+colIndex+"/"+size);
					switch(size) {
						case 0:
							textRam[ramPos]=(byte) ((textRam[ramPos] | ((colIndex & 0xff)<<4))  & 0xff);
							break;
						case 1: 
							textRam[ramPos]=(byte) ((textRam[ramPos] | (colIndex & 0xff)) & 0xff);
							break;
						}
				}
				int idx = cols.get(colIndex);
				if (idx==0) {
					bits.set(bit);
				}
				bit--;
			}
			if (bits.length()==0) {
				bos.write(0);
			} else {
				bos.write(bits.toByteArray());
			}
		}
	}

	private void processBlock(int x, int y, int[] pixels, int[] indexMap, ColorMap colors) {
		Map<Integer, Integer> colCnt = new HashMap<>();
		int i=0;
		for (int y2=y; y2<y+8; y2++) {
			for (int x2=x; x2<x+8; x2++) {
				int pos = y2*320+x2;
				int col = pixels[pos];
				int colIndex = colors.getClosestColorIndex(col);
				Integer cnt = colCnt.get(colIndex);
				if (cnt==null) {
					cnt = 0;
				}
				cnt++;
				colCnt.put(colIndex,  cnt);
			}
		}
		
		List<Integer> indices = new ArrayList<>(colCnt.keySet());
		Collections.sort(indices, (i0, i1) -> {
            Integer c0 = colCnt.get(i0);
            Integer c1 = colCnt.get(i1);
            return c1.compareTo(c0);
        });
		
		int[] colSet = new int[Math.min(2, indices.size()+1)];
		indices = indices.subList(0, Math.min(indices.size(),2));
		for (i=0; i<indices.size(); i++) {
			colSet[i]=colors.getColors()[indices.get(i)];
		}
		
		/*
		for (i=0; i<colSet.length; i++) {
			Logger.log("Color "+i+" => "+indices.get(i)+"/"+colSet[i]);
		}
		*/
		
		for (int y2=y; y2<y+8; y2++) {
			for (int x2=x; x2<x+8; x2++) {
				int pos = y2*320+x2;
				int col = pixels[pos];
				indexMap[pos] = colors.getClosestColorIndex(colors.getClosestColor(col, colSet));
			}
		}
	}
	
}
