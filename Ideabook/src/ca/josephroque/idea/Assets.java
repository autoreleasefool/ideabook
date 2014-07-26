package ca.josephroque.idea;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Assets {
	
	public static final float FONT_SIZE_TITLE = 36f;
	public static final float FONT_SIZE_SUBTITLE = 24f;
	public static final float FONT_SIZE_DEFAULT = 14f;
	public static final float FONT_SIZE_SMALL = 11f;
	
	public static Font fontCaviarDreams = null;
	public static Font fontGravityLight = null;
	public static Font fontGravityBook = null;
	public static Font fontRegencie = null;
	
	public static final Color backgroundPanelColor = new Color(110, 150, 200);
	
	public static void loadAssets() {
		fontCaviarDreams = loadFont("CaviarDreams.ttf");
		fontGravityLight = loadFont("Gravity-Light.ttf");
		fontGravityBook = loadFont("Gravity-Book.ttf");
		fontRegencie = loadFont("RegencieLightAlt.ttf");
	}
	
	private static Font loadFont(String fileName) {
		try {
			return Font.createFont(Font.TRUETYPE_FONT, Assets.class.getResourceAsStream("/ca/josephroque/resources/fonts/" + fileName));
		} catch (FontFormatException | IOException ex) {
			Data.printErrorMessage(ex);
		}
		return null;
	}
	
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
