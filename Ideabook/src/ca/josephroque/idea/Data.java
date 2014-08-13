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

/**
 * General methods related to managing the data created and used
 * by the application.
 * 
 * @author Joseph Roque
 * @see ca.josephroque.idea.config.Category
 * @see ca.josephroque.idea.config.Idea
 * @see ca.josephroque.idea.config.Tag
 * @since 2014-06-04
 *
 */
public class Data {
	
	/**
	 * Indicates whether the application should print to the console.
	 * Expected to be disabled in the final build.
	 */
	private static final boolean bShouldPrintMessage = true;
	/** Default return character for the system */
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	/**
	 * Method called during the shutdown of the program which calls methods
	 * relevant to saving unsaved data, unloading assets, etc.
	 */
	public static void unloadProgram() {
		//At most, this method can run for about a second. Try to not use it.
		//And only call it from the shutdown hook. It isn't needed anywhere else.
		PanelManager.getPanel(PanelManager.getCurrentPanel()).save();
	}
	
	/**
	 * Calls methods to load general data used by the application.
	 * 
	 * @see ca.josephroque.idea.config.Category#loadCategoryNames()
	 */
	public static void loadData() {
		Category.loadCategoryNames();
	}
	
	/**
	 * Checks for unsaved data from the last time the application was closed.
	 * If any data is found, the user is prompted to reload it. Whether the user
	 * chooses to load the data or not, it is deleted afterward.
	 */
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
	
	/**
	 * Prompts the user twice delete all data relevant to the program. If the
	 * user follows through, all files from the main directory are deleted
	 * and the application exits. The next time the application is opened,
	 * default data will be created.
	 */
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
	
	/**
	 * If a file is specified by <code>file</code>, then the file is deleted.
	 * If it is a directory, any subdirectories and files are deleted first,
	 * then the directory is deleted.
	 * 
	 * @param file the file or folder to be deleted
	 * @return true if the file or directory is successfully deleted, false otherwise
	 */
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
	
	/**
	 * Gets a string which points to a default directory, dependent on the
	 * operating system.
	 * 
	 * @return a path to a default directory to save data to
	 */
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
	
	/**
	 * Searches the default directory for data which may not be accurately
	 * documented or inaccessible by the user. Attempts to rescue these files
	 * so the user may choose what to do with them.
	 */
	public static void recoverData() {
		//Attempt to recover lost categories, tags and ideas.
		//This is if something happens to a config file and these assets are no longer documented
	}
	
	/**
	 * If a file is specified by <code>file</code> then its filename is compared against
	 * <code>fileName</code>. If they match, the method returns <code>true</code>. If
	 * <code>file</code> specifies a directory, its subdirectories are checked for a duplicate
	 * filename as well.
	 * 
	 * @param file the file or directory to compare
	 * @param fileName the filename to compare
	 * @return true if the file or any subfile/directory and filename are equal, false otherwise
	 */
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

	/**
	 * If <code>shouldPrintMessage</code>, outputs the stack trace
	 * from the provided exception .
	 * 
	 * @param ex the exception to print the stack trace from
	 */
	public static void printErrorMessage(Exception ex) {
		if (bShouldPrintMessage) {
			ex.printStackTrace();
		}
	}
}
