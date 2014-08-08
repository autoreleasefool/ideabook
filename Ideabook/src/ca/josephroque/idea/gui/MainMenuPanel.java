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

public class MainMenuPanel extends RefreshablePanel {

	private static final long serialVersionUID = 1L;

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
	
	public void refresh() {
		
	}
	
	public void close() {
		
	}
	
	public void save() {
		
	}
}
