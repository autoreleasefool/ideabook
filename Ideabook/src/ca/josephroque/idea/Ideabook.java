package ca.josephroque.idea;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import ca.josephroque.idea.gui.PanelManager;

/**
 * Manages the creation of the application window and offers a static method
 * to access the <code>JFrame</code> containing the application.
 * 
 * @author Joseph Roque
 * @since 2014-05-30
 *
 */
public class Ideabook {
	
	/**
	 * A static reference to the window containing the application
	 */
	private static JFrame frame = null;
	
	/**
	 * Default constructor. Builds the application window and panels. Also
	 * adds a runtime ShutdownHook to handle the program closing event.
	 * 
	 * @see {@link ca.josephroque.idea.Data#unloadProgram()}
	 */
	private Ideabook() {
		frame = new JFrame();
		frame.setTitle("ideabook");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(PanelManager.getInstance());
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setMinimumSize(new Dimension(300, 225));
		
		Data.checkForUnsavedData();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				Data.unloadProgram();
			}
		});
	}
	
	/**
	 * Returns an instance of the <code>JFrame</code> containing the application.
	 * 
	 * @return the object <code>frame</code>
	 */
	public static JFrame getFrame() {
		return frame;
	}
	
	/**
	 * Main method. Loads assets into memory then builds the application window.
	 * 
	 * @param args not applicable
	 */
	public static void main(String[] args) {
		Assets.loadAssets();
		Data.loadData();
		//NotificationThread.beginThread();
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Ideabook();
			}
		});
	}
}
