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

public class Category {

	private static TreeSet<String> categoryNames = new TreeSet<String>();
	
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
			addCategoryName("Application", false);
			addCategoryName("Novel", false);
			addCategoryName("Other", false);
			addCategoryName("Poetry", false);
			addCategoryName("Short Story", false);
			
			saveCategoryNames();
		}
	}
	
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
	
	public static String[] getCategoryNamesArray() {
		String[] names = new String[categoryNames.size()];
		categoryNames.toArray(names);
		return names;
	}
	
	public static Iterator<String> getCategoryNamesIterator() {
		return categoryNames.iterator();
	}
	
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
		
		categoryNames.add(newCategory);
		
		if (shouldSave)
			saveCategoryNames();
		
		return true;
	}
	
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
