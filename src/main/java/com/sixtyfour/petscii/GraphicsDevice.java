package com.sixtyfour.petscii;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * 
 * @author EgonOlsen
 * 
 */
public class GraphicsDevice {

	private JFrame frame = null;
	private BufferedImage screen = null;
	private Graphics2D gscreen = null;
	private int width = 0;
	private int height = 0;
	private Color color = Color.white;
	private RenderingHints noAa = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_OFF);

	public static GraphicsDevice openDevice() {
		int x = 320;
		int y = 200;
		return new GraphicsDevice(x, y);
	}

	private GraphicsDevice(int x, int y) {
		System.setProperty("sun.java2d.d3d", "false");
		width = x;
		height = y;
		frame = new JFrame("Graphics " + x + "*" + y);
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		screen = new BufferedImage(x, y, BufferedImage.TYPE_INT_ARGB);
		gscreen = screen.createGraphics();
		gscreen.setColor(Color.BLACK);
		gscreen.fillRect(0, 0, x, y);
		gscreen.setColor(color);
		gscreen.setRenderingHints(noAa);

		JLabel label = new JLabel();
		label.setIcon(new ImageIcon(screen));
		label.setPreferredSize(new Dimension(x, y));
		frame.add(label, BorderLayout.CENTER);
		frame.pack();

		frame.setVisible(true);
	}
	
	public void draw(Bitmap img) {
		draw(img.getImage());
	}

	public void draw(BufferedImage img) {
		gscreen.drawImage(img, 0, 0, width, height, 0, 0, img.getWidth(), img.getHeight(), null);
		update();
	}
	
	
	public void dispose() {
		close();
	}

	/**
	 * Sets the current draw color.
	 * 
	 * @param r red
	 * @param g green
	 * @param b blue
	 * @param a alpha
	 */
	public void color(int r, int g, int b, int a) {
		color = new Color(r & 0xff, g & 0xff, b & 0xff, a & 0xff);
		getContext().setColor(color);
	}

	/**
	 * 
	 * @param x
	 * @param y
	 */
	public void plot(int x, int y) {
		if (x >= 0 && x < width && y >= 0 && y < height) {
			getScreen().setRGB(x, y, color.getRGB());
		}
		update();
	}

	/**
	 * Clears the screen.
	 */
	public void clear() {
		getContext().fillRect(0, 0, width, height);
		update();
	}

	private void update() {
		frame.repaint();
	}

	private Graphics2D getContext() {
		return gscreen;
	}

	private BufferedImage getScreen() {
		return screen;
	}

	private void close() {
		gscreen.dispose();
		frame.setVisible(false);
		frame.dispose();
	}
}
