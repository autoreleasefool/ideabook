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

import org.jdom2.Attribute;
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

public class Tag implements Comparable<Tag> {
	
	private String id;
	private TreeSet<String> ideas;
	private TreeMap<String, String> ideaCategories;
	
	public Tag(String id) {
		this.id = id;
		ideas = new TreeSet<String>();
		ideaCategories = new TreeMap<String, String>();
	}
	
	public Iterator<String> iterator() {
		return ideas.iterator();
	}

	public String getID() {
		return id;
	}

	public int compareTo(Tag other) {
		return id.compareTo(other.id);
	}
	
	public boolean equals(Object other) {
		if (other != null && other instanceof Tag) {
			Tag o = (Tag) other;
			return id.equals(o.id);
		}
		return false;
	}
	
	public void addIdea(String idea, String category) {
		ideas.add(idea);
		ideaCategories.put(idea, category);
	}
	
	public String getCategory(String idea) {
		return ideaCategories.get(idea);
	}
	
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
		
		Iterator<String> ideaIterator = tag.iterator();
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
					if (ideaName.compareToIgnoreCase(idea.getName()) == 0) {
						content.removeContent(ideasInTag.get(i));
						
						if (totalNumberOfIdeas == 1) {
							tagFile.delete();
						} else {
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
	
	public static void loadAllTags(TreeSet<Tag> tagTree) {
		File directory = new File(Data.getDefaultDirectory() + "/Ideabook/config/tags");
		if (directory.exists()) {
			File[] listOfTags = directory.listFiles();
			for (File f:listOfTags) {
				tagTree.add(loadTag(f.getName().substring(0,f.getName().lastIndexOf("."))));
			}
		}
	}
	
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
