package ca.josephroque.idea.gui;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import ca.josephroque.idea.Assets;
import ca.josephroque.idea.Data;
import ca.josephroque.idea.Ideabook;
import ca.josephroque.idea.Text;
import ca.josephroque.idea.config.Category;
import ca.josephroque.idea.config.Idea;
import ca.josephroque.idea.config.Tag;
import ca.josephroque.idea.gui.components.IdeaCanvas;

public class SubmitPanel extends RefreshablePanel {

	private static final long serialVersionUID = 1L;
	private static final String STR_CATEGORY_NEW = "New Category...";
	private static final int CATEGORY_MAX_LENGTH = 16;
	
	private JTextField textIdeaName = null;
	private JTextField textIdeaTags = null;
	private JTextArea textAreaIdeaBody = null;
	private JComboBox<String> comboCategory = null;
	
	private int oldSelectedIndex = 0;
	private boolean shouldPromptNewCategory = true;
	
	public SubmitPanel() {
		super();
		this.setLayout(new BorderLayout());
		this.setBackground(Assets.backgroundPanelColor);
		
		JPanel upperInfoPanel = new JPanel();
		upperInfoPanel.setBackground(Assets.backgroundPanelColor);
		upperInfoPanel.setLayout(new BorderLayout());
		
		JPanel ideaNameAndCategoryPanel = new JPanel();
		ideaNameAndCategoryPanel.setBackground(Assets.backgroundPanelColor);
		ideaNameAndCategoryPanel.setLayout(new BoxLayout(ideaNameAndCategoryPanel, BoxLayout.X_AXIS));
		
		JLabel label = new JLabel("Idea:");
		label.setFont(Assets.fontGravityBook.deriveFont(Assets.FONT_SIZE_DEFAULT));
		ideaNameAndCategoryPanel.add(Box.createRigidArea(new Dimension(5,0)));
		ideaNameAndCategoryPanel.add(label);
		
		textIdeaName = new JTextField();
		textIdeaName.setDocument(new Text.PatternDocument(Text.regex_IdeaName, Text.IDEA_NAME_MAXLENGTH));
		textIdeaName.setFont(Assets.fontRegencie.deriveFont(Assets.FONT_SIZE_DEFAULT));
		ideaNameAndCategoryPanel.add(Box.createRigidArea(new Dimension(5,0)));
		ideaNameAndCategoryPanel.add(textIdeaName);
		
		comboCategory = new JComboBox<String>(new DefaultComboBoxModel<String>(Category.getCategoryNamesArray()));
		comboCategory.addItem(SubmitPanel.STR_CATEGORY_NEW);
		comboCategory.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					if (comboCategory.getSelectedIndex() + 1 == comboCategory.getItemCount()) {
						if (shouldPromptNewCategory)
							promptNewCategory();
					} else {
						oldSelectedIndex = comboCategory.getSelectedIndex();
					}
				}
			}
		});
		ideaNameAndCategoryPanel.add(Box.createRigidArea(new Dimension(5,0)));
		ideaNameAndCategoryPanel.add(comboCategory);
		
		upperInfoPanel.add(ideaNameAndCategoryPanel, BorderLayout.NORTH);
		ideaNameAndCategoryPanel = null;
		
		JPanel ideaTagsPanel = new JPanel();
		ideaTagsPanel.setBackground(Assets.backgroundPanelColor);
		ideaTagsPanel.setLayout(new BoxLayout(ideaTagsPanel, BoxLayout.X_AXIS));
		
		label = new JLabel("Tags:");
		label.setFont(Assets.fontGravityBook.deriveFont(Assets.FONT_SIZE_DEFAULT));
		ideaTagsPanel.add(Box.createRigidArea(new Dimension(5,0)));
		ideaTagsPanel.add(label);
		
		textIdeaTags = new JTextField();
		textIdeaTags.setDocument(new Text.PatternDocument(Text.regex_CommaSeparatedAndLower));
		textIdeaTags.setFont(Assets.fontRegencie.deriveFont(Assets.FONT_SIZE_DEFAULT));
		ideaTagsPanel.add(Box.createRigidArea(new Dimension(5,0)));
		ideaTagsPanel.add(textIdeaTags);
		
		upperInfoPanel.add(ideaTagsPanel, BorderLayout.SOUTH);
		ideaTagsPanel = null;
		
		this.add(upperInfoPanel, BorderLayout.NORTH);
		upperInfoPanel = null;
		
		JPanel lowerConfigPanel = new JPanel();
		lowerConfigPanel.setBackground(Assets.backgroundPanelColor);
		lowerConfigPanel.setLayout(new BorderLayout());
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		
		/*JPanel ideaBodyPanel = new JPanel();
		ideaBodyPanel.setBackground(Assets.backgroundPanelColor);
		ideaBodyPanel.setBorder(new EmptyBorder(0,5, 0, 5));*/
		
		textAreaIdeaBody = new JTextArea();
		textAreaIdeaBody.setLineWrap(true);
		textAreaIdeaBody.setWrapStyleWord(true);
		textAreaIdeaBody.setFont(Assets.fontRegencie.deriveFont(Assets.FONT_SIZE_DEFAULT));
		//ideaBodyPanel.add(new JScrollPane(textAreaIdeaBody), BorderLayout.CENTER);
		
		tabbedPane.addTab("Text", null, new JScrollPane(textAreaIdeaBody), "Plain text to describe the idea");
		
		Canvas ideaGraphicsCanvas = new IdeaCanvas();
		tabbedPane.addTab("Graphics", null, ideaGraphicsCanvas, "Visuals to illustrate the idea");
		
		lowerConfigPanel.add(tabbedPane, BorderLayout.CENTER);
		tabbedPane = null;
		
		JPanel controlButtonPanel = new JPanel();
		controlButtonPanel.setBackground(Assets.backgroundPanelColor);
		
		ActionListener controlListener = new ControlActionListener();
		
		JButton button = new JButton("create");
		button.setFont(Assets.fontCaviarDreams.deriveFont(Assets.FONT_SIZE_DEFAULT));
		button.setFocusPainted(false);
		button.setActionCommand("Create");
		button.addActionListener(controlListener);
		controlButtonPanel.add(button);
		
		button = new JButton("cancel");
		button.setFont(Assets.fontCaviarDreams.deriveFont(Assets.FONT_SIZE_DEFAULT));
		button.setFocusPainted(false);
		button.setActionCommand("Cancel");
		button.addActionListener(controlListener);
		controlButtonPanel.add(button);
		
		lowerConfigPanel.add(controlButtonPanel, BorderLayout.SOUTH);
		controlButtonPanel = null;
		
		this.add(lowerConfigPanel, BorderLayout.CENTER);
		lowerConfigPanel = null;
	}
	
	private void promptNewCategory() {
		String input = null;
		do {
			input = JOptionPane.showInputDialog(Ideabook.getFrame(), "Category name:", "Create new category", JOptionPane.PLAIN_MESSAGE);
			if (input == null || input.length() == 0)
				break;
			input = input.trim();
			if (input.equalsIgnoreCase("CONFIG")) {
				Notification.queueInformationNotification("You cannot use " + input + " as a category name");
			} else if (input.length() > CATEGORY_MAX_LENGTH) {
				Notification.queueInformationNotification("New category names must be 16 characters or less");
			} else if (!input.matches(Text.regex_AlphaNumeric)) {
				Notification.queueInformationNotification("Category names must be alphanumeric");
			}else if (Category.addCategoryName(input, true)) {
				Notification.queueInformationNotification("Success! Category added!");
				comboCategory.removeItem(SubmitPanel.STR_CATEGORY_NEW);
				comboCategory.addItem(input);
				comboCategory.addItem(SubmitPanel.STR_CATEGORY_NEW);
				comboCategory.setSelectedIndex(comboCategory.getItemCount() - 2);
				comboCategory.repaint();
				comboCategory.validate();
				return;
			}
		} while(true);
		
		comboCategory.setSelectedIndex(oldSelectedIndex);
	}
	
	public void refresh() {
		shouldPromptNewCategory = false;
		String[] categoryNames = Category.getCategoryNamesArray();
		
		textIdeaName.setText(null);
		textIdeaTags.setText(null);
		textAreaIdeaBody.setText(null);
		
		comboCategory.removeAllItems();
		for (String category:categoryNames) {
			comboCategory.addItem(category);
		}
		comboCategory.addItem(SubmitPanel.STR_CATEGORY_NEW);
		comboCategory.setSelectedIndex(0);
		comboCategory.repaint();
		comboCategory.validate();
		shouldPromptNewCategory = true;
	}
	
	public void close() {
		
	}
	
	public void save() {
		String ideaNameText = textIdeaName.getText().trim();
		String ideaTagText = textIdeaTags.getText().trim();
		String ideaBodyText = textAreaIdeaBody.getText().trim();
		
		boolean saveIdeaName = ideaNameText != null && ideaNameText.length() > 0;
		boolean saveIdeaTag = ideaTagText != null && ideaTagText.length() > 0;
		boolean saveIdeaBody = ideaBodyText != null && ideaBodyText.length() > 0;
		
		if (saveIdeaName || saveIdeaTag || saveIdeaBody) {
			File directory = new File(Data.getDefaultDirectory() + "/Ideabook/config");
			directory.mkdirs();
			directory = null;
			
			File saveFile = new File(Data.getDefaultDirectory() + "/Ideabook/config/submit.dat");
			if (saveFile.exists())
				saveFile.delete();
			
			Element dataElement = new Element("data");
			Document doc = new Document(dataElement);
			
			Element content = new Element("content");
			Element saveElement;
			
			if (saveIdeaName) {
				saveElement = new Element("ideaname");
				saveElement.setText(ideaNameText);
				content.addContent(saveElement);
			}
			
			if (saveIdeaTag) {
				saveElement = new Element("ideatag");
				saveElement.setText(ideaTagText);
				content.addContent(saveElement);
			}
			
			if (saveIdeaBody) {
				saveElement = new Element("ideabody");
				saveElement.setText(ideaBodyText);
				content.addContent(saveElement);
			}
			
			doc.getRootElement().addContent(content);
			XMLOutputter xmlOutput = new XMLOutputter();
			xmlOutput.setFormat(Format.getPrettyFormat());
			
			try {
				xmlOutput.output(doc, new FileWriter(saveFile));
			} catch (IOException io) {
				Data.printErrorMessage(io);
			}
		}
	}
	
	private class ControlActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			if ("Create".equals(event.getActionCommand())) {
				textIdeaName.setText(textIdeaName.getText().trim());
				textAreaIdeaBody.setText(textAreaIdeaBody.getText().trim());
				textIdeaTags.setText(textIdeaTags.getText().trim());
				
				if (textIdeaName.getText() == null || textIdeaName.getText().length() == 0) {
					JOptionPane.showMessageDialog(Ideabook.getFrame(), "This idea must have a name.", "No name", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				if (textAreaIdeaBody.getText() == null || textAreaIdeaBody.getText().length() == 0) {
					int checkForBody = JOptionPane.showConfirmDialog(Ideabook.getFrame(), "You have not given this idea a body. A body allows you to elaborate on your idea. Are you sure this is correct?", "No body provided", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
					if (checkForBody == JOptionPane.CANCEL_OPTION || checkForBody == JOptionPane.CLOSED_OPTION)
						return;
				}
				
				if (textIdeaTags.getText() == null || textIdeaTags.getText().length() == 0) {
					int checkForTags = JOptionPane.showConfirmDialog(Ideabook.getFrame(), "You have not provided any tags. Providing tags will make this idea much easier to find later. Are you sure this is correct?", "No tags provided", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
					if (checkForTags == JOptionPane.CANCEL_OPTION || checkForTags == JOptionPane.CLOSED_OPTION)
						return;
				}
				
				createNewIdea();
			} else if ("Cancel".equals(event.getActionCommand())) {
				if (textIdeaName.getText().length() > 0 || textIdeaTags.getText().length() > 0 || textAreaIdeaBody.getText().length() > 0) {
					int confirmCancel = JOptionPane.showConfirmDialog(Ideabook.getFrame(), "By cancelling this submission, you will lose any information entered above. Are you sure you want to do this?", "Warning - Cancelling Submission", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
					if (confirmCancel == JOptionPane.CANCEL_OPTION || confirmCancel == JOptionPane.CLOSED_OPTION)
						return;
				}
				
				PanelManager.show(PanelManager.MENU_MAIN);
			}
		}
	}
	
	public void setIdeaName(String name) {
		this.textIdeaName.setText(name);
	}
	
	public void setIdeaTags(String tags) {
		this.textIdeaTags.setText(tags);
	}
	
	public void setIdeaBody(String body) {
		this.textAreaIdeaBody.setText(body);
	}
	
	private void createNewIdea() {
		Idea newIdea = new Idea(textIdeaName.getText(), comboCategory.getItemAt(comboCategory.getSelectedIndex()), textAreaIdeaBody.getText(), textIdeaTags.getText().split(", *"), new java.util.Date());
		
		if (Idea.saveIdea(newIdea)) {
			Iterator<String> tags = newIdea.getTagsIterator();
			while(tags.hasNext())
				Tag.addIdeaToTag(tags.next(), newIdea);
			PanelManager.show(PanelManager.MENU_MAIN);
			Notification.queueInformationNotification("Success! New idea saved!");
		}
	}
}
