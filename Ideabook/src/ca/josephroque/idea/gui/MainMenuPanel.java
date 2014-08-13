package ca.josephroque.idea.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ca.josephroque.idea.Assets;

/**
 * <code>RefreshablePanel</code> which displays a master set of options
 * available to a user.
 * <p>
 * These options include creating an idea, searching for an existing idea
 * and a settings menu.
 * 
 * @author Joseph Roque
 * @since 2014-05-30
 */
public class MainMenuPanel extends RefreshablePanel {

	/** Default serialVersionUID */
	private static final long serialVersionUID = 1L;

	/**
	 * Initializes the panel with a label to display the application's
	 * name and a list of buttons for the user to interact with.
	 */
	public MainMenuPanel() {
		super();
		this.setLayout(new BorderLayout());
		this.setBackground(Assets.backgroundPanelColor);
		
		JPanel innerPanel = new JPanel();
		innerPanel.setBackground(Assets.backgroundPanelColor);
		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
		
		innerPanel.add(Box.createVerticalGlue());
		
		JLabel label = new JLabel("ideabook");
		//TODO May want to change
		label.setFont(Assets.fontCaviarDreams.deriveFont(Assets.FONT_SIZE_TITLE));
		label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		innerPanel.add(label);
		innerPanel.add(Box.createRigidArea(new Dimension(0,5)));
		
		Dimension buttonSize = new Dimension(120, 30);
		
		JButton button = new JButton("create");
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		button.setFont(Assets.fontCaviarDreams.deriveFont(Assets.FONT_SIZE_DEFAULT));
		button.setFocusPainted(false);
		button.setMinimumSize(buttonSize);
		button.setMaximumSize(buttonSize);
		button.setPreferredSize(buttonSize);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				PanelManager.show(PanelManager.MENU_SUBMIT);
			}
		});
		innerPanel.add(button);
		
		button = new JButton("recall");
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		button.setFont(Assets.fontCaviarDreams.deriveFont(Assets.FONT_SIZE_DEFAULT));
		button.setFocusPainted(false);
		button.setMinimumSize(buttonSize);
		button.setMaximumSize(buttonSize);
		button.setPreferredSize(buttonSize);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				PanelManager.show(PanelManager.MENU_SEARCH);
			}
		});
		innerPanel.add(button);
		
		button = new JButton("settings");
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		button.setFont(Assets.fontCaviarDreams.deriveFont(Assets.FONT_SIZE_DEFAULT));
		button.setFocusPainted(false);
		button.setMinimumSize(buttonSize);
		button.setMaximumSize(buttonSize);
		button.setPreferredSize(buttonSize);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				PanelManager.show(PanelManager.MENU_SETTINGS);
			}
		});
		innerPanel.add(button);
		
		innerPanel.add(Box.createVerticalGlue());
		this.add(innerPanel, BorderLayout.CENTER);
	}
	
	/**
	 * Does nothing.
	 */
	@Override
	public void refresh() {
		
	}
	
	/**
	 * Does nothing.
	 */
	@Override
	public void close() {
		
	}
	
	/**
	 * Does nothing.
	 */
	@Override
	public void save() {
		
	}
}
