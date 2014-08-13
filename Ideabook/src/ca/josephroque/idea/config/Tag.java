package ca.josephroque.idea.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ca.josephroque.idea.Data;

/**
 * A Tag object is made up of a name and a set of Idea objects. These
 * Idea objects are ideas which the user has created and 'tagged' with
 * the corresponding name of this tag.
 * 
 * @author Joseph Roque
 * @since 2014-06-20
 */
public class Tag implements Comparable<Tag> {
	
	/** An id to identify the Tag */
	private String id;
	/** The set of ideas which are 'tagged' with the id */
	private TreeSet<String> ideas;
	/** The corresponding category of each idea */
	private TreeMap<String, String> ideaCategories;
	
	/**
	 * Initializes a new Tag with the provided id
	 * @param id the id of the tag
	 */
	public Tag(String id) {
		this.id = id;
		ideas = new TreeSet<String>();
		ideaCategories = new TreeMap<String, String>();
	}
	
	/**
	 * Returns an iterator for the set of ideas
	 * @return ideas.iterator()
	 */
	public Iterator<String> ideaIterator() {
		return ideas.iterator();
	}

	/**
	 * Returns the id of the Tag
	 * @return the value of <code>id</code>
	 */
	public String getID() {
		return id;
	}

	/**
	 * Compares the ids of both Tag objects
	 */
	@Override
	public int compareTo(Tag other) {
		return id.compareTo(other.id);
	}
	
	/**
	 * Returns true if <code>other</code> represents a Tag
	 * and the ids are equal, false otherwise.
	 */
	@Override
	public boolean equals(Object other) {
		if (other != null && other instanceof Tag) {
			Tag o = (Tag) other;
			return id.equals(o.id);
		}
		return false;
	}
	
	/**
	 * Adds an idea to <code>ideas</code> and its category to <code>ideaCategories</code>
	 * 
	 * @param idea the idea to be added
	 * @param category the category of the idea being added
	 * @throws IllegalArgumentException if either parameter is null
	 */
	public void addIdea(String idea, String category) throws IllegalArgumentException {
		if (idea == null || category == null) {
			throw new IllegalArgumentException("idea and category cannot be null");
		}
		ideas.add(idea);
		ideaCategories.put(idea, category);
	}
	
	/**
	 * Returns the category corresponding to the given idea, from <code>ideaCategories</code>.
	 * @param idea the idea to get the category for
	 * @return ideaCategories.get(idea);
	 */
	public String getCategory(String idea) {
		return ideaCategories.get(idea);
	}
	
	/**
	 * Saves the given Tag to a file, formatted as an XML document. The document contains
	 * all of the Tag's ideas and categories.
	 * 
	 * @param tag the tag to be saved
	 * @return true if the tag was successfully saved, false otherwise
	 */
	public static boolean saveTag(Tag tag) {
		File directory = new File(Data.getDefaultDirectory() + "/Ideabook/config/tags");
		directory.mkdirs();
		directory = null;
		
		File saveFile = new File(Data.getDefaultDirectory() + "/Ideabook/config/tags/" + tag.getID() + ".tag");
		if (saveFile.exists())
			saveFile.delete();
		
		Element tagElement = new Element("tag");
		Document doc = new Document(tagElement);
		
		Element content = new Element("content");
		Element idea;
		
		Iterator<String> ideaIterator = tag.ideaIterator();
		while (ideaIterator.hasNext()) {
			String ideaName = ideaIterator.next();
			
			idea = new Element("idea");
			idea.setText(ideaName + ":" + tag.getCategory(ideaName));
			content.addContent(idea);
		}
		
		doc.getRootElement().addContent(content);
		
		XMLOutputter xmlOutput = new XMLOutputter();
		xmlOutput.setFormat(Format.getPrettyFormat());
		
		try {
			xmlOutput.output(doc, new FileWriter(saveFile));
		} catch (IOException io) {
			Data.printErrorMessage(io);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Loads a Tag from a file corresponding to the given name of the tag. Creates
	 * and returns a Tag object from the file.
	 * 
	 * @param tagName the tag to be loaded
	 * @return a new Tag object with the ideas and categories listed in the file
	 */
	public static Tag loadTag(String tagName) {
		final Tag tag = new Tag(tagName);
		File loadFile = new File(Data.getDefaultDirectory() + "/Ideabook/config/tags/" + tagName + ".tag");
		
		if (loadFile.exists()) {
			try {
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser saxParser = factory.newSAXParser();
				
				DefaultHandler handler = new DefaultHandler() {
					boolean boolIdea;
					StringBuilder builder = new StringBuilder();
					
					public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
						if (qName.equalsIgnoreCase("idea")) {
							boolIdea = true;
						}
					}
					
					public void endElement(String uri, String localName, String qName) throws SAXException {
						if (qName.equalsIgnoreCase("idea")) {
							String ideaAndCategory[] = builder.toString().split(":");
							tag.addIdea(ideaAndCategory[0], ideaAndCategory[1]);
							boolIdea = false;
						}
					}
					
					public void characters(char[] ch, int start, int length) throws SAXException {
						if (boolIdea) {
							builder.append(new String(ch, start, length));
						}
					}
				};
				
				saxParser.parse(loadFile, handler);
			} catch (SAXException | ParserConfigurationException | IOException ex) {
				Data.printErrorMessage(ex);
			}
		}
		
		return tag;
	}
	
	/**
	 * Loads the names of all the tags created by the user and adds them to
	 * the given <code>TreeSet</code> object.
	 * 
	 * @param tagTree lists the names of all of the tags found
	 * @see ca.josephroque.idea.Data#getDefaultDirectory()
	 */
	public static void loadAllTags(TreeSet<Tag> tagTree) {
		File directory = new File(Data.getDefaultDirectory() + "/Ideabook/config/tags");
		if (directory.exists()) {
			File[] listOfTags = directory.listFiles();
			for (File f:listOfTags) {
				if (f.getName().endsWith(".tag")) {
					tagTree.add(loadTag(f.getName().substring(0,f.getName().lastIndexOf("."))));
				}
			}
		}
	}
	
	/**
	 * Removes the element from the Tag's XML document which corresponds to the
	 * given idea.
	 * 
	 * @param tagName the tag to delete the idea from
	 * @param idea the idea to be deleted
	 */
	public static void removeIdeaFromTag(String tagName, Idea idea) {
		File tagFile = new File(Data.getDefaultDirectory() + "/Ideabook/config/tags/" + tagName + ".tag");
		if (tagFile.exists()) {
			System.out.println("here?");
			try {
				String ideaName = null;
				SAXBuilder builder = new SAXBuilder();
				Document doc = (Document) builder.build(tagFile);
				Element rootNode = doc.getRootElement();
				Element content = rootNode.getChild("content");
				
				List<Element> ideasInTag = content.getChildren("idea");
				int totalNumberOfIdeas = ideasInTag.size();
				for (int i = 0; i<totalNumberOfIdeas; i++) {
					ideaName = ideasInTag.get(i).getText().split(":")[0];
					if (ideaName.equalsIgnoreCase(idea.getName())) {
						if (totalNumberOfIdeas == 1) {
							tagFile.delete();
						} else {
							content.removeContent(ideasInTag.get(i));
							XMLOutputter xmlOutput = new XMLOutputter();
							xmlOutput.setFormat(Format.getPrettyFormat());
							xmlOutput.output(doc, new FileWriter(tagFile));
						}
						
						return;
					}
				}
			} catch (IOException | JDOMException ex) {
				Data.printErrorMessage(ex);
			}
		}
	}
	
	/**
	 * Adds an element to the Tag's XML document with the given Idea object's name
	 * value and category.
	 * 
	 * @param tagName the tag to add the idea to
	 * @param idea the idea to be added
	 */
	public static void addIdeaToTag(String tagName, Idea idea) {
		File directory = new File(Data.getDefaultDirectory() + "/Ideabook/config/tags");
		directory.mkdirs();
		directory = null;
		
		File tagFile = new File(Data.getDefaultDirectory() + "/Ideabook/config/tags/" + tagName + ".tag");
		if (tagFile.exists()) {
			try {
				SAXBuilder builder = new SAXBuilder();
				Document doc = (Document) builder.build(tagFile);
				Element rootNode = doc.getRootElement();
				Element content = rootNode.getChild("content");
				
				List<Element> ideasInTag = content.getChildren("idea");
				for (int i = 0; i<ideasInTag.size(); i++) {
					if (ideasInTag.get(i).getText().compareToIgnoreCase(idea.getName()) == 0) {
						return;
					}
				}
				Element tempElement = new Element("idea");
				tempElement.setText(idea.getName() + ":" + idea.getCategory());
				content.addContent(tempElement);
				
				XMLOutputter xmlOutput = new XMLOutputter();
				xmlOutput.setFormat(Format.getPrettyFormat());
				xmlOutput.output(doc, new FileWriter(tagFile));
			} catch (IOException | JDOMException ex) {
				Data.printErrorMessage(ex);
			}
		} else {
			Tag tag = new Tag(tagName);
			tag.addIdea(idea.getName(), idea.getCategory());
			saveTag(tag);
		}
	}
}
