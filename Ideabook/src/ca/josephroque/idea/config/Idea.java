package ca.josephroque.idea.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ca.josephroque.idea.Data;
import ca.josephroque.idea.gui.Notification;

/**
 * An Idea object is made up of a name, a category, a list of tags, and a plain text
 * description. The user can create, organize and store any number of ideas
 * using the ideabook application.
 * 
 * @author Joseph Roque
 * @since 2014-06-20
 * 
 */
public class Idea {
	
	/** A <code>SimpleDateFormat</code> object to parse a Date and display it */
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	/** The name of the idea */
	private String name;
	/** The category which the idea is saved under */
	private String category;
	/** The plain text description of the idea */
	private String body;
	/** A list of tags to describe the idea, used in searching */
	private List<String> tags;
	
	/** The date that the idea was initially created */
	private Date created;
	/** The most recent date that the idea was modified in any way */
	private Date modified;
	
	/** Indicates whether the idea has been modified since its initialization */
	private boolean wasModified = false;
	
	/**
	 * Initializes a new idea with the parameters as values for instance variables.
	 * 
	 * @param name the name of the idea
	 * @param category the category of the idea
	 * @param body the description of the idea
	 * @param tags a list of tags for indexing the idea
	 * @param created the date that the idea was created
	 * @param modified the date that the idea was last modified
	 */
	public Idea(String name, String category, String body, String[] tags, Date created, Date modified) {
		this.name = name;
		this.category = category;
		this.body = body;
		this.tags = tags == null ? new ArrayList<String>():Arrays.asList(tags);
		this.created = created;
		this.modified = modified;
	}
	
	/**
	 * Initializes a new idea with the parameters as values for instance variables.
	 * Uses the date that the idea was created for <code>modified</code> as well.
	 * 
	 * @param name the name of the idea
	 * @param category the category of the idea
	 * @param body the description of the idea
	 * @param tags a list of tags for indexing the idea
	 * @param created the date that the idea was created
	 */
	public Idea(String name, String category, String body, String[] tags, Date created) {
		this(name, category, body, tags, created, created);
	}
	
	/**
	 * Initializes a new idea with the parameters as values for instance variables.
	 * Parses the Strings <code>created</code> and <code>modified</code> as Date
	 * objects.
	 * 
	 * @param name the name of the idea
	 * @param category the category of the idea
	 * @param body the description of the idea
	 * @param tags a list of tags for indexing the idea
	 * @param created the date that the idea was created
	 * @param modified the date that the idea was last modified
	 */
	public Idea(String name, String category, String body, String[] tags, String created, String modified) {
		this.name = name;
		this.category = category;
		this.tags = tags == null ? new ArrayList<String>():Arrays.asList(tags);
		this.body = body;
		
		try {
			this.created = dateFormat.parse(created);
			this.modified = dateFormat.parse(modified);
		} catch (ParseException ex) {
			if (created == null)
				this.created = new Date();
			if (modified == null)
				this.modified = new Date();
			Notification.queueErrorNotification("Could not load date for an idea, today's date has been substituted");
		}
	}
	
	/**
	 * Initializes a new idea from a pre-existing idea. Only copies
	 * immutable instances of each instance variable, all Date and
	 * String objects.
	 * 
	 * @param ideaCopy the idea to copy
	 */
	public Idea(Idea ideaCopy) {
		this.name = ideaCopy.getName();
		this.category = ideaCopy.getCategory();
		this.body = ideaCopy.getBody();
		this.tags = Arrays.asList(ideaCopy.getTagsArray());
		
		this.created = ideaCopy.getDateCreated();
		this.modified = ideaCopy.getDateLastModified();
	}
	
	/**
	 * Returns a <code>Date</code> object of the time and day the idea was created.
	 * @return the date the idea was created
	 */
	public Date getDateCreated() {return created;}
	/**
	 * Returns a <code>Date</code> object of the time and day the idea was last modified.
	 * @return the date the idea was last modified
	 */
	public Date getDateLastModified() {return modified;}
	/**
	 * Returns the name of the idea.
	 * @return the value of <code>name</code>
	 */
	public String getName() {return name;}
	/**
	 * Returns the category of the idea.
	 * @return the value of <code>category</code>
	 */
	public String getCategory() {return category;}
	/**
	 * Returns the description of the idea.
	 * @return the value of <code>body</code>
	 */
	public String getBody() {return body;}
	/**
	 * Returns the list containing the tags of the idea.
	 * @return the value of <code>tags</code>
	 */
	public List<String> getTags() {return tags;}
	/**
	 * Returns whether the idea has been modified since it was initialized.
	 * @return the value of <code>wasModified</code>
	 */
	public boolean wasModified() {return wasModified;}
	
	/**
	 * Returns a String iterator of <code>tags</code>
	 * @return tags.iterator()
	 */
	public Iterator<String> getTagsIterator() {
		return tags.iterator();
	}
	
	/**
	 * Returns a String array of <code>tags</code>
	 * @return tags.toArray()
	 */
	public String[] getTagsArray() {
		return (String[]) tags.toArray();
	}
	
	/**
	 * Returns a String containing all the tags of the idea separated by commas.
	 * @return all tags of the idea, separated by commas in a String
	 */
	public String getTagsCommaSeparated() {
		String tagsWithCommas = "";
		if (tags.size() > 0) {
			tagsWithCommas = tags.get(0);
			for (int i = 1; i < tags.size(); i++)
				tagsWithCommas += ", " + tags.get(i);
		}
		return tagsWithCommas;
	}
	
	/**
	 * Returns a String of <code>created</code>, formatted according
	 * to <code>dateFormat</code>.
	 * @return the date the idea was created as a String
	 */
	public String getDateCreatedFormatted() {return dateFormat.format(created);}
	/**
	 * Returns a String of <code>modified</code>, formatted according
	 * to <code>dateFormat</code>.
	 * @return the date the idea was last modified as a String
	 */
	public String getDateLastModifiedFormatted() {return dateFormat.format(modified);}
	
	/**
	 * Sets the name of the idea and sets <code>wasModified</code> accordingly.
	 * @param name the new value for <code>name</code>
	 */
	public void setName(String name) {
		this.name = name;
		modified();
	}
	
	/**
	 * Sets the category of the idea and sets <code>wasModified</code> accordingly.
	 * @param category the new value for <code>category</code>
	 */
	public void setCategory(String category) {
		this.category = category;
		modified();
	}
	
	/**
	 * Sets the body of the idea and sets <code>wasModified</code> accordingly.
	 * @param body the new value for <code>body</code>
	 */
	public void setBody(String body) {
		this.body = body;
		modified();
	}
	
	/**
	 * Sets the tags of the idea and sets <code>wasModified</code> accordingly.
	 * @param tags the new value for <code>tags</code>
	 */
	public void setTags(String[] tags) {
		this.tags = Arrays.asList(tags);
		modified();
	}
	
	/**
	 * Sets the date the idea was last modified.
	 * @param modified the new value for <code>modified</code>
	 */
	public void setDateModified(String modified) {
		try {
			this.modified = dateFormat.parse(modified);
		} catch (ParseException ex) {
			this.modified = new Date();
			Data.printErrorMessage(ex);
		}
	}
	
	/**
	 * Sets the date the idea was created.
	 * @param created the new value for <code>created</code>
	 */
	public void setDateCreated(String created) {
		try {
			this.created = dateFormat.parse(created);
		} catch (ParseException ex) {
			this.created = new Date();
			Data.printErrorMessage(ex);
		}
	}
	
	/**
	 * Sets <code>modified</code> to the current date and time and
	 * sets <code>wasModified</code> to true.
	 */
	public void modified() {
		this.modified = new Date();
		wasModified = true;
	}
	
	/**
	 * Edits an idea by deleting the old data which stored it and saving
	 * the new idea to its own file. Returns false if the name of the new
	 * idea is taken or if the file is unsuccessfully saved, true otherwise.
	 * 
	 * @param oldIdea the original idea to be deleted
	 * @param newIdea the new idea to be saved
	 * @return true if the new idea is successfully saved, false otherwise
	 */
	public static boolean editIdea(Idea oldIdea, Idea newIdea) {
		File directory = new File(Data.getDefaultDirectory() + "/Ideabook/" + newIdea.getCategory());
		directory.mkdirs();
		
		directory = new File(Data.getDefaultDirectory() + "/Ideabook/");
		if (oldIdea.getName().equalsIgnoreCase(newIdea.getName())) {
			File oldSaveFile = new File(Data.getDefaultDirectory() + "/Ideabook/" + oldIdea.getCategory() + "/" + oldIdea.getName() + ".idea");
			oldSaveFile.delete();
		} else if (Data.checkForDuplicateFilename(directory, newIdea.getName() + ".idea")) {
			Notification.queueInformationNotification("An idea with this name already exists");
			return false;
		}
		directory = null;
		
		File saveFile = new File(Data.getDefaultDirectory() + "/Ideabook/" + newIdea.getCategory() + "/" + newIdea.getName() + ".idea");
		return saveIdeaToFile(newIdea, saveFile);
	}
	
	/**
	 * Saves an idea to a file. Returns false if the name of the idea
	 * is already take or if the idea was not successfully saved, true
	 * otherwise.
	 * 
	 * @param idea the idea to be saved
	 * @return true if the idea was successfully saved, false otherwise
	 */
	public static boolean saveIdea(Idea idea) {
		File directory = new File(Data.getDefaultDirectory() + "/Ideabook/" + idea.getCategory());
		directory.mkdirs();
		
		directory = new File(Data.getDefaultDirectory() + "/Ideabook/");
		if (Data.checkForDuplicateFilename(directory, idea.getName() + ".idea")) {
			Notification.queueInformationNotification("An idea with this name already exists");
			return false;
		}
		directory = null;
		
		File saveFile = new File(Data.getDefaultDirectory() + "/Ideabook/" + idea.getCategory() + "/" + idea.getName() + ".idea");
		return saveIdeaToFile(idea, saveFile);
	}
	
	/**
	 * Saves the given idea to the given file, formatted as an XML document.
	 * 
	 * @param idea the idea to be saved
	 * @param saveFile the file to save the idea to
	 * @return true if the idea was successfully saved, false otherwise
	 */
	private static boolean saveIdeaToFile(Idea idea, File saveFile) {
		if (saveFile.exists()) {
			saveFile.delete();
		}
		
		Element ideaElement = new Element("idea");
		Document doc = new Document(ideaElement);
		
		Element content = new Element("content");
		content.addContent(new Element("name").setText(idea.getName()));
		content.addContent(new Element("category").setText(idea.getCategory()));
		content.addContent(new Element("tags").setText(idea.getTagsCommaSeparated()));
		content.addContent(new Element("body").setText(idea.getBody()));
		content.addContent(new Element("created").setText(idea.getDateCreatedFormatted()));
		content.addContent(new Element("modified").setText(idea.getDateLastModifiedFormatted()));
		
		doc.getRootElement().addContent(content);
		
		XMLOutputter xmlOutput = new XMLOutputter();
		xmlOutput.setFormat(Format.getPrettyFormat());
		
		try {
			xmlOutput.output(doc, new FileWriter(saveFile));
		} catch (IOException io) {
			Data.printErrorMessage(io);
			Notification.queueErrorNotification("An unexpected error occurred and this idea was not saved");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Using the provided name and category, loads the remaining data
	 * from an XML file and creates a new <code>Idea</code> object
	 * and returns it.
	 * 
	 * @param name the name of the idea to load
	 * @param category the category of the idea to load
	 * @return a new Idea object with the data loaded from a file as its values
	 */
	public static Idea loadIdea(String name, String category) {
		final Idea idea = new Idea(name, category, null, null, null);
		final StringBuilder dateHolder = new StringBuilder();
		File loadFile = new File(Data.getDefaultDirectory() + "/Ideabook/" + category + "/" + name + ".idea");
		if (loadFile.exists()) {
			try {
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser saxParser = factory.newSAXParser();
				
				DefaultHandler handler = new DefaultHandler() {
					boolean boolTags, boolBody, boolCreated, boolModified;
					StringBuilder builder;
					
					public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
						if (qName.equalsIgnoreCase("TAGS")) {
							builder = new StringBuilder();
							boolTags = true;
						} else if (qName.equalsIgnoreCase("BODY")) {
							builder = new StringBuilder();
							boolBody = true;
						} else if (qName.equalsIgnoreCase("CREATED")) {
							builder = new StringBuilder();
							boolCreated = true;
						} else if (qName.equalsIgnoreCase("MODIFIED")) {
							//builder = new StringBuilder();
							boolModified = true;
						}
					}
					
					public void endElement(String uri, String localName, String qName) throws SAXException {
						if (qName.equalsIgnoreCase("TAGS")) {
							String tagsUnsplit = builder.toString();
							idea.setTags(tagsUnsplit.split(", +"));
							boolTags = false;
						} else if (qName.equalsIgnoreCase("BODY")) {
							idea.setBody(builder.toString());
							boolBody = false;
						} else if (qName.equalsIgnoreCase("CREATED")) {
							idea.setDateCreated(builder.toString());
							boolCreated = false;
						} else if (qName.equalsIgnoreCase("MODIFIED")) {
							idea.setDateModified(dateHolder.toString());
							boolModified = false;
						}
					}
					
					public void characters(char[] ch, int start, int length) throws SAXException {
						if (boolTags || boolBody || boolCreated) {
							builder.append(new String(ch, start, length));
						} else if (boolModified) {
							dateHolder.append(new String(ch, start, length));
						}
					}
				};
				
				saxParser.parse(loadFile, handler);
			} catch (SAXException | ParserConfigurationException | IOException ex) {
				Data.printErrorMessage(ex);
			}
		}
		
		return idea;
	}
	
	/**
	 * Calls a helper method to load the names of all files which end with the suffix ".idea" in the
	 * default directory and all of its subdirectories. The idea's name and its corresponding category
	 * are added to the provided TreeSet objects.
	 * 
	 * @param ideaNameTree lists the names of all the ideas found
	 * @param ideaCategoryTree lists the categories corresponding to each idea found
	 * @throws IllegalArgumentException if either parameter is null
	 */
	public static void loadAllIdeaNames(TreeSet<String> ideaNameTree, TreeMap<String, String> ideaCategoryTree) throws IllegalArgumentException {
		if (ideaNameTree == null || ideaCategoryTree == null) {
			throw new IllegalArgumentException("parameters cannot be null");
		}
		File root = new File(Data.getDefaultDirectory() + "/Ideabook");
		loadAllIdeaNames(root, ideaNameTree, ideaCategoryTree);
	}
	
	/**
	 * Recursive method which checks a file to see if it is an idea. If so, it and its category
	 * are added to the <code>TreeSet</code> and <code>TreeMap</code> objects, respectively. If
	 * the file is a directory, the method is recursively called on it so all subdirectories
	 * are searched as well.
	 * 
	 * @param file the file to check
	 * @param ideaNameTree lists the names of all the ideas found
	 * @param ideaCategoryTree lists the categories corresponding to each idea found
	 */
	private static void loadAllIdeaNames(File file, TreeSet<String> ideaNameTree, TreeMap<String, String> ideaCategoryTree) {
		if (file != null && file.exists()) {
			if (file.isDirectory()) {
				File[] listOfFiles = file.listFiles();
				for (File f:listOfFiles)
					loadAllIdeaNames(f, ideaNameTree, ideaCategoryTree);
			} else {
				String fileName = file.getName();
				String fileCategory = file.getParent();
				fileCategory = fileCategory.substring(fileCategory.lastIndexOf("/") + 1);
				if (fileName.endsWith(".idea")) {
					ideaNameTree.add(fileName.substring(0,fileName.lastIndexOf(".")));
					ideaCategoryTree.put(fileName.substring(0,fileName.lastIndexOf(".")), fileCategory);
				}
			}
		}
	}
}
