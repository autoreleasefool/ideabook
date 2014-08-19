package ca.josephroque.idea.gui.components;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import ca.josephroque.idea.Assets;

/**
 * <code>Canvas</code> which the user is able to interact with
 * to create visual designs which help explain their idea.
 * 
 * @author Joseph Roque
 * @since 2014-08-11
 */
public class IdeaCanvas extends Canvas implements MouseListener, MouseMotionListener {

	/** Default serialVersionUID */
	private static final long serialVersionUID = 1L;
	/** The default width of the canvas */
	private static final int DEFAULT_SCREEN_WIDTH = 779;
	/** The default height of the canvas */
	private static final int DEFAULT_SCREEN_HEIGHT = 453;
	
	private static final Image IMAGE_ZOOM = Assets.loadImage("graphics/tools/zoom.png");
	private static final Image IMAGE_DRAW = Assets.loadImage("graphics/tools/draw.png");
	
	private BufferedImage imgIdea = null;

	private boolean editable;
	private boolean showTools;
	
	private int zoom = 100;

	private byte[][] colorOfPixel = null;
	private Rectangle areaToDraw = null;
	
	/**
	 * Default constructor.
	 */
	public IdeaCanvas(boolean editable) {
		super();
		
		this.editable = editable;
		showTools = false;
		
		this.setBackground(Color.white);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		draw(g);
	}
	
	private void draw(Graphics g) {
		if (colorOfPixel == null || imgIdea == null) {
			colorOfPixel = new byte[DEFAULT_SCREEN_WIDTH][DEFAULT_SCREEN_HEIGHT];
			imgIdea = new BufferedImage(DEFAULT_SCREEN_WIDTH, DEFAULT_SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
			Graphics g2 = imgIdea.getGraphics();
			g2.setColor(Color.white);
			g2.fillRect(0, 0, DEFAULT_SCREEN_WIDTH, DEFAULT_SCREEN_HEIGHT);
			g2.dispose();
			g2 = null;
		}
		
		if (areaToDraw == null) {
			return;
		}
		
		Graphics2D g2d = imgIdea.createGraphics();
		if (areaToDraw.x + areaToDraw.width < colorOfPixel.length && areaToDraw.y + areaToDraw.height < colorOfPixel[0].length) {
			for (int xx = areaToDraw.x; xx < areaToDraw.x + areaToDraw.width; xx++) {
				for (int yy = areaToDraw.y; yy < areaToDraw.y + areaToDraw.height; yy++) {
					switch(colorOfPixel[xx][yy]) {
					case 0:g2d.setColor(Color.white); break;
					case 1:g2d.setColor(Color.black); break;
					case 2:g2d.setColor(Color.red); break;
					case 3:g2d.setColor(Color.blue); break;
					}
					g2d.fillRect(xx, yy, 1, 1);
				}
			}
		}
		
		g2d.dispose();
		areaToDraw = null;
		
		g.drawImage(imgIdea, 0, 0, this.getWidth(), this.getHeight(), null);
		
		if (showTools) {
			g.drawImage(IMAGE_ZOOM, 1, 1, null);
			if (editable && this.getHeight() > 164) {
				g.drawImage(IMAGE_DRAW, 1, 49, null);
			}
		}
	}
	
	private void shouldShowTools(boolean tools) {
		System.out.println(showTools + " " + tools);
		if (this.showTools == tools) {
			return;
		}
		
		showTools = tools;
		areaToDraw = new Rectangle(1, 1, 24, 164);
		repaint();
	}
	
	public void mousePressed(MouseEvent me) {}
	public void mouseReleased(MouseEvent me) {}
	
	public void mouseMoved(MouseEvent me) {}
	public void mouseDragged(MouseEvent me) {}
	
	public void mouseEntered(MouseEvent me) {shouldShowTools(true);}
	public void mouseExited(MouseEvent me) {shouldShowTools(false);}
	public void mouseClicked(MouseEvent me) {}
}
