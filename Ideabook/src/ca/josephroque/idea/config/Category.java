package ca.josephroque.idea.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.TreeSet;

import ca.josephroque.idea.Data;
import ca.josephroque.idea.gui.Notification;

/**
 * Static methods relevant to creating and loading categories
 * which organize the user's ideas.
 * 
 * @author Joseph Roque
 * @since 2014-07-26
 */
public class Category {

	/** The current list of category names used by the application */
	private static TreeSet<String> categoryNames = new TreeSet<String>();
	
	/** Maximum length of a category name */
	public static final int CATEGORY_MAX_LENGTH = 16;
	
	/**
	 * Loads existing categories from a file which lists their names. If the file
	 * does not exist, a default set of categories is generated for the user.
	 */
	public static void loadCategoryNames() {
		File fileCategory = new File(Data.getDefaultDirectory() + "/Ideabook/config/categories.inf");
		BufferedReader reader = null;
		
		if (fileCategory.exists()) {
			//Loads the categories in the 'category.inf' file
			//Handles any errors, which will not cause the program to end.
			try {
				reader = new BufferedReader(new FileReader(fileCategory));
				String line = null;
				String input = "";
				while ((line = reader.readLine()) != null)
					input += line + Data.LINE_SEPARATOR;
				
				String[] categoryList = input.split(Data.LINE_SEPARATOR);
				for (String category:categoryList) {
					if (category != null && category.length() > 0) {
						categoryNames.add(category);
					}
				}
			} catch (IOException io) {
				Data.printErrorMessage(io);
				Notification.queueErrorNotification("Error loading some files. Consider 'recovery' if data is missing");
			} finally {
				try {
					if (reader != null)
						reader.close();
				} catch (IOException | NullPointerException ex) {
					Data.printErrorMessage(ex);
					Notification.queueErrorNotification("Error loading some files. Consider 'recovery' if data is missing");
				}
			}
		} else {
			//Generates default categories and saves them
			addCategoryName("Miscellaneous", false);
			saveCategoryNames();
		}
	}
	
	/**
	 * Saves the current set of category names to a file so the application can
	 * easily recognize and load them the next time it is run.
	 */
	private static void saveCategoryNames() {
		File directory = new File(Data.getDefaultDirectory() + "/Ideabook/config");
		directory.mkdirs();
		directory = null;
		
		File fileCategory = new File(Data.getDefaultDirectory() + "/Ideabook/config/categories.inf");
		BufferedWriter writer = null;
		
		try {
			writer = new BufferedWriter(new FileWriter(fileCategory));
			
			for (String line:categoryNames) {
				writer.append(line);
				writer.newLine();
			}
		} catch (IOException io) {
			Data.printErrorMessage(io);
			Notification.queueErrorNotification("Error loading some files. Consider 'recovery' if data is missing");
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException | NullPointerException ex) {
				Data.printErrorMessage(ex);
				Notification.queueErrorNotification("Error loading some files. Consider 'recovery' if data is missing");
			}
		}
	}
	
	/**
	 * Creates an array of String objects from <code>categoryNames</code> and returns it.
	 * 
	 * @return an array of String objects containing the current category names
	 */
	public static String[] getCategoryNamesArray() {
		String[] names = new String[categoryNames.size()];
		categoryNames.toArray(names);
		return names;
	}
	
	/**
	 * Returns an iterator from <code>categoryNames</code>
	 * @return {@link java.util.TreeSet#iterator()} from <code>categoryNames</code>
	 */
	public static Iterator<String> getCategoryNamesIterator() {
		return categoryNames.iterator();
	}
	
	/**
	 * If the category does not already exist, then it is added to <code>categoryNames</code>
	 * and the method returns true, false otherwise. If <code>shouldSave</code> is true then
	 * the current set of categories is saved to a text file.
	 * 
	 * @param newCategory name of the new category to be added
	 * @param shouldSave indicates whether the new list of categories should be saved
	 * @return true if the category did not already exist and was added, false otherwise
	 * 
	 * @see ca.josephroque.idea.config.Category#saveCategoryNames()
	 */
	public static boolean addCategoryName(String newCategory, boolean shouldSave) {
		Iterator<String> categoryIterator = categoryNames.iterator();
		while (categoryIterator.hasNext()) {
			if (categoryIterator.next().equalsIgnoreCase(newCategory)) {
				Notification.queueInformationNotification("This category already exists");
				return false;
			}
		}
		
		File categoryFolder = new File(Data.getDefaultDirectory() + "/Ideabook/" + newCategory);
		if (!categoryFolder.exists()) {
			if (!categoryFolder.mkdirs()) {
				Notification.queueErrorNotification("An unknown error occurred creating category. Try again");
				return false;
			}
		}
		
		boolean success = categoryNames.add(newCategory);
		
		if (shouldSave && success) {
			saveCategoryNames();
		}
		
		return success;
	}
	
	/**
	 * If the category specified by <code>categoryToDelete</code> exists, then the category
	 * is removed from <code>categoryNames</code> and the corresponding directory in the
	 * application's data is cleared and deleted. This also deletes any subsequent ideas
	 * which are saved in the category.
	 * 
	 * @param categoryToDelete name of the category to be deleted
	 * @return true if the category was successfully deleted or did not exist, false otherwise
	 */
	public static boolean deleteCategoryName(String categoryToDelete) {
		Iterator<String> categoryIterator = categoryNames.iterator();
		String comparator;
		while (categoryIterator.hasNext()) {
			comparator = categoryIterator.next();
			if (comparator.equalsIgnoreCase(categoryToDelete)) {
				File file = new File(Data.getDefaultDirectory() + "/Ideabook/" + comparator);
				return Data.deleteAllFiles(file);
			}
		}
		return true;
	}
}
