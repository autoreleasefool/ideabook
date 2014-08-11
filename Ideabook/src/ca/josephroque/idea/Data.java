package ca.josephroque.idea;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ca.josephroque.idea.config.Category;
import ca.josephroque.idea.gui.PanelManager;
import ca.josephroque.idea.gui.SubmitPanel;

public class Data {
	
	private static final boolean shouldPrintMessage = true;
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	
	
	public static void unloadProgram() {
		//At most, this method can run for about a second. Try to not use it.
		//And only call it from the shutdown hook. It isn't needed anywhere else.
		PanelManager.getPanel(PanelManager.getCurrentPanel()).save();
	}
	
	public static void loadData() {
		Category.loadCategoryNames();
	}
	
	public static void checkForUnsavedData() {
		File submitPanelSaveFile = new File(Data.getDefaultDirectory() + "/Ideabook/config/submit.dat");
		if (submitPanelSaveFile.exists()) {
			int responseToPrompt = JOptionPane.showConfirmDialog(Ideabook.getFrame(),
																"An unfinished idea was found. Would you like to load it and continue editing?",
																"Idea in progress!",
																JOptionPane.YES_NO_OPTION,
																JOptionPane.INFORMATION_MESSAGE);
			if (responseToPrompt != JOptionPane.YES_OPTION) {
				submitPanelSaveFile.delete();
				return;
			}
			
			try {
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser saxParser = factory.newSAXParser();
				
				PanelManager.show(PanelManager.MENU_SUBMIT);
				final SubmitPanel submitPanel = (SubmitPanel) PanelManager.getPanel(PanelManager.MENU_SUBMIT);
				
				DefaultHandler handler = new DefaultHandler() {
					boolean boolIdeaName = false;
					boolean boolIdeaTags = false;
					boolean boolIdeaBody = false;
					StringBuilder builder = new StringBuilder();
					
					public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
						if (qName.equalsIgnoreCase("ideaname")) {
							boolIdeaName = true;
							builder = new StringBuilder();
						} else if (qName.equalsIgnoreCase("ideatag")) {
							boolIdeaTags = true;
							builder = new StringBuilder();
						} else if (qName.equalsIgnoreCase("ideabody")) {
							boolIdeaBody = true;
							builder = new StringBuilder();
						}
					}
					
					public void endElement(String uri, String localName, String qName) throws SAXException {
						if (qName.equalsIgnoreCase("ideaname")) {
							boolIdeaName = false;
							if (builder.length() > 0)
								submitPanel.setIdeaName(builder.toString());
						} else if (qName.equalsIgnoreCase("ideatag")) {
							boolIdeaTags = false;
							if (builder.length() > 0)
								submitPanel.setIdeaTags(builder.toString());
						} else if (qName.equalsIgnoreCase("ideabody")) {
							boolIdeaBody = false;
							if (builder.length() > 0)
								submitPanel.setIdeaBody(builder.toString());
						}
					}
					
					public void characters(char[] ch, int start, int length) throws SAXException {
						if (boolIdeaName || boolIdeaTags || boolIdeaBody)
							builder.append(new String(ch, start, length));
					}
				};
				
				saxParser.parse(submitPanelSaveFile, handler);
			} catch (SAXException | ParserConfigurationException | IOException ex) {
				Data.printErrorMessage(ex);
			}
			
			submitPanelSaveFile.delete();
		}
	}
	
	public static void deleteAllData() {
		int deleteConfirmation = JOptionPane.showConfirmDialog(Ideabook.getFrame(), "Are you sure you want to delete all data?" + LINE_SEPARATOR + "This cannot be undone!", "Delete ALL data?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		if (deleteConfirmation == JOptionPane.OK_OPTION) {
			deleteConfirmation = JOptionPane.showConfirmDialog(Ideabook.getFrame(), "Are you 100% sure you want to delete ALL data?" + LINE_SEPARATOR + "This cannot be undone!", "Delete ALL data?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			if (deleteConfirmation == JOptionPane.OK_OPTION) {
				File mainDirectory = new File(getDefaultDirectory() + "/Ideabook");
				if (deleteAllFiles(mainDirectory)) {
					JOptionPane.showMessageDialog(Ideabook.getFrame(), "All data has been deleted." + LINE_SEPARATOR + "The program will now exit.", "Data successfully deleted", JOptionPane.INFORMATION_MESSAGE);
					System.exit(0);
				}
			}
		}
	}
	
	public static boolean deleteAllFiles(File file) {
		if (file == null)
			return true;
		if (file.exists()) {
			if (file.isDirectory()) {
				File[] subFiles = file.listFiles();
				for(File f:subFiles)
					deleteAllFiles(f);
			}
			return file.delete();
		}
		return true;
	}
	
	public static String getDefaultDirectory() {
		String OS = System.getProperty("os.name").toUpperCase();
		
		if (OS.contains("WIN")) {
			return System.getenv("APPDATA");
		} else if (OS.contains("MAC")) {
			return System.getProperty("user.home") + "/Library/Application Support";
		} else {
			return System.getProperty("user.dir");
		}
	}
	
	public static void recoverData() {
		//Attempt to recover lost categories, tags and ideas.
		//This is if something happens to a config file and these assets are no longer documented
	}
	
	public static boolean checkForDuplicateFilename(File file, String fileName) {
		if (file == null || !file.exists())
			return false;
		
		if (file.isDirectory()) {
			File[] listOfFiles = file.listFiles();
			for (File f:listOfFiles) {
				if (checkForDuplicateFilename(f, fileName))
					return true;
			}
			return false;
		} else {
			return file.getName().equalsIgnoreCase(fileName);
		}
	}

	public static void printErrorMessage(Exception ex) {
		if (shouldPrintMessage) {
			ex.printStackTrace();
		}
	}
}
