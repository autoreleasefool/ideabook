package ca.josephroque.idea.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import ca.josephroque.idea.Assets;
import ca.josephroque.idea.config.Idea;

/**
 * <code>RefreshablePanel</code> which displays the contents of an
 * idea to the user in non-editable fields.
 * 
 * @author Joseph Roque
 * @since 2014-07-08
 */
public class ViewPanel extends RefreshablePanel {

	/** Default serialVersionUID */
	private static final long serialVersionUID = 1L;
	
	/** The <code>Idea</code> to be viewed by the user */
	private static Idea currentIdea = null;
	/** A String which represents unavailable information */
	private static final String UNAVAILABLE = "Information unavailable.";
	
	/** Label to display the <code>Idea</code> object's name */
	private JLabel labelIdeaName;
	/** Label to display the <code>Idea</code> object's category */
	private JLabel labelIdeaCategory;
	/** Label to display the date the <code>Idea</code> object was created */
	private JLabel labelIdeaDateCreated;
	/** Label to display the date the <code>Idea</code> object was last modified */
	private JLabel labelIdeaDateModified;
	/** Label to display the <code>Idea</code> object's tags */
	private JLabel labelIdeaTags;
	/** Text area to display the <code>Idea</code> object's description */
	private JTextArea textAreaIdeaBody;
	
	/**
	 * Default constructor. Creates a layout for the various labels
	 * to be displayed to the user, along with two buttons. One is 
	 * an 'edit' button and the other is a 'close' button.
	 */
	public ViewPanel() {
		super();
		this.setLayout(new BorderLayout());
		this.setBackground(Assets.backgroundPanelColor);
		
		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
		titlePanel.setBackground(Assets.backgroundPanelColor);
		
		labelIdeaName = new JLabel(UNAVAILABLE);
		labelIdeaName.setFont(Assets.fontGravityBook.deriveFont(Assets.FONT_SIZE_TITLE));
		labelIdeaName.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		titlePanel.add(Box.createRigidArea(new Dimension(0,5)));
		titlePanel.add(labelIdeaName);
		
		labelIdeaCategory = new JLabel(UNAVAILABLE);
		labelIdeaCategory.setFont(Assets.fontGravityBook.deriveFont(Assets.FONT_SIZE_SMALL));
		labelIdeaCategory.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		titlePanel.add(labelIdeaCategory);
		
		this.add(titlePanel, BorderLayout.NORTH);
		
		JPanel infoPanel = new JPanel();
		infoPanel.setBackground(Assets.backgroundPanelColor);
		infoPanel.setLayout(new BorderLayout());
		
		JPanel tagPanel = new JPanel();
		tagPanel.setBackground(Assets.backgroundPanelColor);
		tagPanel.setLayout(new BoxLayout(tagPanel, BoxLayout.X_AXIS));
		
		JLabel label = new JLabel("Tags:");
		label.setFont(Assets.fontGravityBook.deriveFont(Assets.FONT_SIZE_DEFAULT));
		tagPanel.add(label);
		
		labelIdeaTags = new JLabel();
		labelIdeaTags.setFont(Assets.fontGravityBook.deriveFont(Assets.FONT_SIZE_DEFAULT));
		labelIdeaTags.setText(UNAVAILABLE);
		tagPanel.add(labelIdeaTags);
		
		infoPanel.add(tagPanel, BorderLayout.NORTH);
		
		JPanel bodyPanel = new JPanel();
		bodyPanel.setBackground(Assets.backgroundPanelColor);
		bodyPanel.setLayout(new BorderLayout());
		
		textAreaIdeaBody = new JTextArea();
		textAreaIdeaBody.setFont(Assets.fontRegencie.deriveFont(Assets.FONT_SIZE_DEFAULT));
		textAreaIdeaBody.setEditable(false);
		textAreaIdeaBody.setLineWrap(true);
		textAreaIdeaBody.setWrapStyleWord(true);
		textAreaIdeaBody.setText(UNAVAILABLE);
		bodyPanel.add(new JScrollPane(textAreaIdeaBody));
		
		infoPanel.add(bodyPanel, BorderLayout.CENTER);
		this.add(infoPanel, BorderLayout.CENTER);
		
		JPanel lowerPanel = new JPanel();
		lowerPanel.setBackground(Assets.backgroundPanelColor);
		lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.Y_AXIS));
		
		JPanel datePanel = new JPanel();
		datePanel.setBackground(Assets.backgroundPanelColor);
		datePanel.setLayout(new BoxLayout(datePanel, BoxLayout.X_AXIS));
		datePanel.add(Box.createHorizontalGlue());
		
		label = new JLabel("Date created:");
		label.setFont(Assets.fontGravityBook.deriveFont(Assets.FONT_SIZE_DEFAULT).deriveFont(Font.BOLD));
		datePanel.add(label);
		
		labelIdeaDateCreated = new JLabel();
		labelIdeaDateCreated.setFont(Assets.fontGravityBook.deriveFont(Assets.FONT_SIZE_DEFAULT));
		labelIdeaDateCreated.setText(UNAVAILABLE);
		datePanel.add(Box.createRigidArea(new Dimension(5,0)));
		datePanel.add(labelIdeaDateCreated);
		
		label = new JLabel("Date modified:");
		label.setFont(Assets.fontGravityBook.deriveFont(Assets.FONT_SIZE_DEFAULT).deriveFont(Font.BOLD));
		datePanel.add(Box.createRigidArea(new Dimension(20,0)));
		datePanel.add(label);
		
		labelIdeaDateModified = new JLabel();
		labelIdeaDateModified.setFont(Assets.fontGravityBook.deriveFont(Assets.FONT_SIZE_DEFAULT));
		labelIdeaDateModified.setText(UNAVAILABLE);
		datePanel.add(Box.createRigidArea(new Dimension(5,0)));
		datePanel.add(labelIdeaDateModified);
		datePanel.add(Box.createHorizontalGlue());
		
		lowerPanel.add(Box.createRigidArea(new Dimension(0,5)));
		lowerPanel.add(datePanel);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(Assets.backgroundPanelColor);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		
		JButton btnClose = new JButton("close");
		btnClose.setFont(Assets.fontCaviarDreams.deriveFont(Assets.FONT_SIZE_DEFAULT));
		btnClose.setFocusPainted(false);
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				PanelManager.show(PanelManager.MENU_SEARCH);
			}
		});
		buttonPanel.add(btnClose);
		
		JButton btnEdit = new JButton("edit");
		btnEdit.setFont(Assets.fontCaviarDreams.deriveFont(Assets.FONT_SIZE_DEFAULT));
		btnEdit.setFocusPainted(false);
		btnEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				EditPanel.setCurrentIdea(currentIdea);
				PanelManager.show(PanelManager.MENU_EDIT);
			}
		});
		buttonPanel.add(Box.createRigidArea(new Dimension(5,0)));
		buttonPanel.add(btnEdit);
		buttonPanel.add(Box.createHorizontalGlue());
		
		lowerPanel.add(buttonPanel);
		
		this.add(lowerPanel, BorderLayout.SOUTH);
	}

	/**
	 * Sets the text of the labels to the relevant values provided
	 * by <code>currentIdea</code>
	 */
	@Override
	public void refresh() {
		if (currentIdea == null) {
			return;
		}
		
		labelIdeaName.setText(currentIdea.getName());
		labelIdeaCategory.setText(currentIdea.getCategory());
		labelIdeaDateCreated.setText(currentIdea.getDateCreatedFormatted());
		labelIdeaDateModified.setText(currentIdea.getDateLastModifiedFormatted());
		labelIdeaTags.setText(currentIdea.getTagsCommaSeparated());
		textAreaIdeaBody.setText(currentIdea.getBody());
	}
	
	/**
	 * Sets the text of the labels to <code>UNAVAILABLE</code>.
	 */
	@Override
	public void close() {
		labelIdeaName.setText(UNAVAILABLE);
		labelIdeaCategory.setText(UNAVAILABLE);
		labelIdeaDateCreated.setText(UNAVAILABLE);
		labelIdeaDateModified.setText(UNAVAILABLE);
		labelIdeaTags.setText(UNAVAILABLE);
		textAreaIdeaBody.setText(UNAVAILABLE);
		setCurrentIdea(null);
	}
	
	/**
	 * Does nothing.
	 */
	@Override
	public void save() {
		
	}
	
	/**
	 * Sets the current idea which the user will view.
	 * 
	 * @param idea the new value for <code>currentIdea</code>
	 */
	public static void setCurrentIdea(Idea idea) {
		currentIdea = idea;
	}
}
