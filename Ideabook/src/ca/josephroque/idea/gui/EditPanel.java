package ca.josephroque.idea.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import ca.josephroque.idea.Assets;
import ca.josephroque.idea.Ideabook;
import ca.josephroque.idea.Text;
import ca.josephroque.idea.config.Category;
import ca.josephroque.idea.config.Idea;
import ca.josephroque.idea.config.Tag;

public class EditPanel extends RefreshablePanel {
	
	private static final long serialVersionUID = 1L;
	
	private static final String UNAVAILABLE = "Information Unavailable";
	private static Idea currentIdea = null;
	
	private JTextField textFieldIdeaName = null;
	private JTextField textFieldIdeaTags = null;
	private JTextArea textAreaIdeaBody = null;
	private JComboBox<String> comboIdeaCategory = null;
	
	public EditPanel() {
		super();
		this.setBackground(Assets.backgroundPanelColor);
		this.setLayout(new BorderLayout());
		
		JPanel upperInputPanel = new JPanel();
		upperInputPanel.setBackground(Assets.backgroundPanelColor);
		upperInputPanel.setLayout(new BorderLayout());
		
		JPanel namePanel = new JPanel();
		namePanel.setBackground(Assets.backgroundPanelColor);
		namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));
		
		JLabel label = new JLabel("Idea:");
		label.setFont(Assets.fontGravityBook.deriveFont(Assets.FONT_SIZE_DEFAULT));
		namePanel.add(label);
		
		textFieldIdeaName = new JTextField();
		textFieldIdeaName.setDocument(new Text.PatternDocument(Text.regex_IdeaName, Text.IDEA_NAME_MAXLENGTH));
		textFieldIdeaName.setFont(Assets.fontRegencie.deriveFont(Assets.FONT_SIZE_DEFAULT));
		namePanel.add(textFieldIdeaName);
		
		comboIdeaCategory = new JComboBox<String>(new DefaultComboBoxModel<String>(Category.getCategoryNamesArray()));
		namePanel.add(Box.createRigidArea(new Dimension(5,0)));
		namePanel.add(comboIdeaCategory);
		
		upperInputPanel.add(namePanel, BorderLayout.NORTH);
		
		JPanel tagPanel = new JPanel();
		tagPanel.setBackground(Assets.backgroundPanelColor);
		tagPanel.setLayout(new BoxLayout(tagPanel, BoxLayout.X_AXIS));
		
		label = new JLabel("Tags:");
		label.setFont(Assets.fontGravityBook.deriveFont(Assets.FONT_SIZE_DEFAULT));
		tagPanel.add(label);
		
		textFieldIdeaTags = new JTextField();
		textFieldIdeaTags.setDocument(new Text.PatternDocument(Text.regex_CommaSeparatedAndLower));
		textFieldIdeaTags.setFont(Assets.fontRegencie.deriveFont(Assets.FONT_SIZE_DEFAULT));
		tagPanel.add(textFieldIdeaTags);
		
		upperInputPanel.add(tagPanel, BorderLayout.SOUTH);
		this.add(upperInputPanel, BorderLayout.NORTH);
		
		textAreaIdeaBody = new JTextArea();
		textAreaIdeaBody.setLineWrap(true);
		textAreaIdeaBody.setWrapStyleWord(true);
		textAreaIdeaBody.setFont(Assets.fontRegencie.deriveFont(Assets.FONT_SIZE_DEFAULT));
		this.add(new JScrollPane(textAreaIdeaBody), BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(Assets.backgroundPanelColor);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		
		JButton btnSave = new JButton("Save Changes");
		btnSave.setFont(Assets.fontCaviarDreams.deriveFont(Assets.FONT_SIZE_DEFAULT));
		btnSave.setFocusPainted(false);
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				saveIdea();
			}
		});
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(btnSave);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setFont(Assets.fontCaviarDreams.deriveFont(Assets.FONT_SIZE_DEFAULT));
		btnCancel.setFocusPainted(false);
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				PanelManager.show(PanelManager.MENU_SEARCH);
			}
		});
		buttonPanel.add(Box.createRigidArea(new Dimension(5,0)));
		buttonPanel.add(btnCancel);
		buttonPanel.add(Box.createHorizontalGlue());
		
		this.add(buttonPanel, BorderLayout.SOUTH);
	}
	
	private void saveIdea() {
		if (textFieldIdeaName.getText() == null || textFieldIdeaName.getText().length() == 0) {
			JOptionPane.showMessageDialog(Ideabook.getFrame(), "This idea must have a name.", "No name", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		if (textAreaIdeaBody.getText() == null || textAreaIdeaBody.getText().length() == 0) {
			int checkForBody = JOptionPane.showConfirmDialog(Ideabook.getFrame(), "You have not given this idea a body. A body allows you to elaborate on your idea. Are you sure this is correct?", "No body provided", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
			if (checkForBody == JOptionPane.CANCEL_OPTION || checkForBody == JOptionPane.CLOSED_OPTION)
				return;
		}
		
		if (textFieldIdeaTags.getText() == null || textFieldIdeaTags.getText().length() == 0) {
			int checkForTags = JOptionPane.showConfirmDialog(Ideabook.getFrame(), "You have not provided any tags. Providing tags will make this idea much easier to find later. Are you sure this is correct?", "No tags provided", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
			if (checkForTags == JOptionPane.CANCEL_OPTION || checkForTags == JOptionPane.CLOSED_OPTION)
				return;
		}
		
		Idea newIdea = new Idea(textFieldIdeaName.getText(),
								comboIdeaCategory.getItemAt(comboIdeaCategory.getSelectedIndex()), 
								textAreaIdeaBody.getText(),
								textFieldIdeaTags.getText().split(", *"),
								currentIdea.getDateCreated(),
								new Date());
		
		if (Idea.editIdea(currentIdea, newIdea)) {
			List<String> oldIdeaTags = currentIdea.getTags();
			List<String> newIdeaTags = newIdea.getTags();
			Iterator<String> tagIterator = null;
			String curTag = null;
			
			tagIterator = currentIdea.getTagsIterator();
			while (tagIterator.hasNext()) {
				curTag = tagIterator.next();
				if (!newIdeaTags.contains(curTag)) {
					Tag.removeIdeaFromTag(curTag, newIdea);
				}
			}
			
			tagIterator = newIdea.getTagsIterator();
			while (tagIterator.hasNext()) {
				curTag = tagIterator.next();
				if (!oldIdeaTags.contains(curTag)) {
					Tag.addIdeaToTag(curTag, newIdea);
				}
			}
			
			currentIdea = newIdea;
			refresh();
			Notification.queueInformationNotification("This idea has been successfully saved");
		}
	}

	public void refresh() {
		if (currentIdea == null) {
			return;
		}
		
		textFieldIdeaName.setText(currentIdea.getName());
		comboIdeaCategory.setSelectedItem(currentIdea.getCategory());
		textFieldIdeaTags.setText(currentIdea.getTagsCommaSeparated());
		textAreaIdeaBody.setText(currentIdea.getBody());
	}
	
	public void close() {
		textFieldIdeaName.setText(UNAVAILABLE);
		textFieldIdeaTags.setText(UNAVAILABLE);
		textAreaIdeaBody.setText(UNAVAILABLE);
		comboIdeaCategory.setSelectedIndex(0);
	}
	
	public static void setCurrentIdea(Idea idea) {
		currentIdea = idea;
	}
}
