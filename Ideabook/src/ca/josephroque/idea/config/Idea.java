package ca.josephroque.idea.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

public class Idea {
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	private String name;
	private String category;
	private String body;
	private String[] tags;
	
	private Date created;
	private Date modified;
	
	private boolean wasModified = false;
	
	public Idea(String name, String category, String body, String[] tags, Date created, Date modified) {
		this.name = name;
		this.category = category;
		this.body = body;
		this.tags = tags;
		this.created = created;
		this.modified = modified;
	}
	
	public Idea(String name, String category, String body, String[] tags, Date created) {
		this(name, category, body, tags, created, created);
	}
	
	public Idea(String name, String category, String body, String[] tags, String created, String modified) {
		this.name = name;
		this.category = category;
		this.tags = tags;
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
	
	public Date getDateCreated() {return created;}
	public Date getDateLastModified() {return modified;}
	public String getName() {return name;}
	public String getCategory() {return category;}
	public String getBody() {return body;}
	public String[] getTags() {return tags;}
	public boolean wasModified() {return wasModified;}
	
	public String getTagsCommaSeparated() {
		String tagsWithCommas = "";
		if (tags.length > 0) {
			tagsWithCommas = tags[0];
			for (int i = 1; i < tags.length; i++)
				tagsWithCommas += ", " + tags[i];
		}
		return tagsWithCommas;
	}
	
	public String getDateCreatedFormatted() {return dateFormat.format(created);}
	public String getDateLastModifiedFormatted() {return dateFormat.format(modified);}
	
	public void setName(String name) {
		this.name = name;
		modified();
	}
	
	public void setCategory(String category) {
		this.category = category;
		modified();
	}
	
	public void setBody(String body) {
		this.body = body;
		modified();
	}
	
	public void setTags(String[] tags) {
		this.tags = tags;
		modified();
	}
	
	public void setDateModified(String modified) {
		try {
			this.modified = dateFormat.parse(modified);
		} catch (ParseException ex) {
			this.modified = new Date();
			ex.printStackTrace();
		}
	}
	
	public void setDateCreated(String created) {
		try {
			this.created = dateFormat.parse(created);
		} catch (ParseException ex) {
			this.created = new Date();
			ex.printStackTrace();
		}
	}
	
	public void modified() {
		this.modified = new Date();
		wasModified = true;
	}
	
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
		if (saveFile.exists())
			saveFile.delete();
		
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
	
	public static Idea loadIdea(String name, String category) {
		final Idea idea = new Idea(name, category, null, null, null);
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
							builder = new StringBuilder();
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
							idea.setDateModified(builder.toString());
							boolModified = false;
						}
					}
					
					public void characters(char[] ch, int start, int length) throws SAXException {
						if (boolTags || boolBody || boolCreated || boolModified) {
							builder.append(new String(ch, start, length));
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
	
	public static void loadAllIdeaNames(TreeSet<String> ideaNameTree, TreeMap<String, String> ideaCategoryTree) {
		File root = new File(Data.getDefaultDirectory() + "/Ideabook");
		loadAllIdeaNames(root, ideaNameTree, ideaCategoryTree);
	}
	
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
