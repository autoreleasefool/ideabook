package ca.josephroque.idea;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import ca.josephroque.idea.gui.PanelManager;

public class Ideabook {
	
	private static JFrame frame = null;
	
	private Ideabook() {
		frame = new JFrame();
		frame.setTitle("ideabook");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(new PanelManager());
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		frame.setMinimumSize(new Dimension(300, 225));
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				Data.unloadProgram();
			}
		});
	}
	
	public static JFrame getFrame() {
		return frame;
	}
	
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
