package ca.josephroque.idea;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Loads the images, fonts and other assets used by the application into
 * static memory.
 * 
 * @author Joseph Roque
 * @since 2014-06-04
 *
 */
public class Assets {
	
	/** Default font size for titles */
	public static final float FONT_SIZE_TITLE = 36f;
	/** Default font size for subtitles */
	public static final float FONT_SIZE_SUBTITLE = 24f;
	/** Default font size for general text */
	public static final float FONT_SIZE_DEFAULT = 14f;
	/** Default font size for small text */
	public static final float FONT_SIZE_SMALL = 11f;
	
	/** Caviar Dreams font: default style */
	public static Font fontCaviarDreams = null;
	/** Gravity font: light style */
	public static Font fontGravityLight = null;
	/** Gravity font: default style */
	public static Font fontGravityBook = null;
	/** Regencie font: default style */
	public static Font fontRegencie = null;
	
	/** Color of panels used by color scheme */
	public static final Color backgroundPanelColor = new Color(110, 150, 200);
	
	/**
	 * Loads any images, fonts and other assets used by the program into
	 * static memory.
	 */
	public static void loadAssets() {
		fontCaviarDreams = loadFont("CaviarDreams.ttf");
		fontGravityLight = loadFont("Gravity-Light.ttf");
		fontGravityBook = loadFont("Gravity-Book.ttf");
		fontRegencie = loadFont("RegencieLightAlt.ttf");
	}
	
	/**
	 * Creates a font object from a file with the specified filename in the relative
	 * directory <code>/ca/josephroque/resources/fonts/</code>
	 * 
	 * @param fileName the filename of the font
	 * @return a font object created from the filename provided
	 */
	private static Font loadFont(String fileName) {
		try {
			return Font.createFont(Font.TRUETYPE_FONT, Assets.class.getResourceAsStream("/ca/josephroque/resources/fonts/" + fileName));
		} catch (FontFormatException | IOException ex) {
			Data.printErrorMessage(ex);
		}
		return null;
	}
	
	/**
	 * Creates an image object from a file with the specified filename in the relative
	 * directory <code>/ca/josephroque/resources/images/</code>
	 * 
	 * @param fileName the filename of the font
	 * @return an image object created from the filename provided
	 */
	public static Image loadImage(String fileName) {
		Image image = null;
		try {
			image = ImageIO.read(Assets.class.getResource("/ca/josephroque/resources/images/" + fileName));
		} catch (IOException io) {
			Data.printErrorMessage(io);
		}
		return image;
	}
}
