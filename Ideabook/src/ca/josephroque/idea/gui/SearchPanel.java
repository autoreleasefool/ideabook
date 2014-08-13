package ca.josephroque.idea.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ca.josephroque.idea.Assets;
import ca.josephroque.idea.Text;
import ca.josephroque.idea.config.Category;
import ca.josephroque.idea.config.Idea;
import ca.josephroque.idea.config.Tag;

/**
 * <code>RefreshablePanel</code> which provides methods for the
 * user to search the ideas they have created, to view or edit
 * their contents.
 * 
 * @author Joseph Roque
 * @since 2014-07-01
 *
 */
public class SearchPanel extends RefreshablePanel {

	/** Default serialVersionUID */
	private static final long serialVersionUID = 1L;
	/** String which represents a search of ideas in "Any Category" */
	private static final String STR_CATEGORY_ALL = "Any Category";
	
	/** Button which will open the currently selected idea in the view panel */
	private JButton btnViewIdea = null;
	/** Button which will open the currently selected idea in the edit panel */
	private JButton btnEditIdea = null;
	/** Button which will close the search panel and return to the main menu */
	private JButton btnCancel = null;
	/** Text field for the user to input their search terms */
	private JTextField textSearchTerms = null;
	/** Drop down menu of all the existing categories */
	private JComboBox<String> comboCategory = null;
	/** A list model for a <code>JList</code> which shows the results of the current search */
	private DefaultListModel<String> listSearchResults = null;
	
	/** A String which stores the user's most recent search terms */
	private String lastSearch = "";
	/** The currently selected index of the list of ideas */
	private int curSelectedIndex = -1;
	/** A set of results which match the user's search terms */
	private TreeSet<String> searchResultsTree = null;
	/** A set of all the idea names previously created by the user */
	private TreeSet<String> ideaNameTree = null;
	/** A set of all the categories which correspond to each idea name */
	private TreeMap<String, String> ideaCategoryTree = null;
	/** A set of all the tags the user has tagged their ideas with */
	private TreeSet<Tag> tagTree = null;
	
	/**
	 * Initializes the panel and displays an input field for the user's
	 * search terms, a <code>JList</code> object to display the results
	 * of the user's search and buttons for navigation.
	 */
	public SearchPanel() {
		super();
		this.setLayout(new BorderLayout());
		this.setBackground(Assets.backgroundPanelColor);
		
		searchResultsTree = new TreeSet<String>();
		
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.X_AXIS));
		innerPanel.setBackground(Assets.backgroundPanelColor);
		
		JLabel label = new JLabel("Search:");
		label.setFont(Assets.fontGravityBook.deriveFont(Assets.FONT_SIZE_DEFAULT));
		innerPanel.add(Box.createRigidArea(new Dimension(5,0)));
		innerPanel.add(label);
		
		textSearchTerms = new JTextField();
		textSearchTerms.setDocument(new Text.PatternDocument(Text.regex_IdeaName));
		textSearchTerms.setFont(Assets.fontRegencie.deriveFont(Assets.FONT_SIZE_DEFAULT));
		textSearchTerms.getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent event) {
				updateSearchResults();
			}
			public void insertUpdate(DocumentEvent event) {
				updateSearchResults();
			}
			public void changedUpdate(DocumentEvent event) {}
		});
		innerPanel.add(Box.createRigidArea(new Dimension(5,0)));
		innerPanel.add(textSearchTerms);
		
		comboCategory = new JComboBox<String>(new DefaultComboBoxModel<String>(Category.getCategoryNamesArray()));
		comboCategory.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					updateSearchResults();
				}
			}
		});
		innerPanel.add(Box.createRigidArea(new Dimension(5,0)));
		innerPanel.add(comboCategory);
		this.add(innerPanel, BorderLayout.NORTH);
		
		listSearchResults = new DefaultListModel<String>();
		JList<String> list = new JList<String>(listSearchResults);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ListSelectionModel lsm = list.getSelectionModel();
		lsm.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				ListSelectionModel lsm = (ListSelectionModel) e.getSource();
				if (lsm.isSelectionEmpty()) {
					setButtonsEnabled(false);
					curSelectedIndex = -1;
				} else {
					setButtonsEnabled(true);
					int selectedIndex = lsm.getMinSelectionIndex();
					if (lsm.isSelectedIndex(selectedIndex))
						curSelectedIndex = selectedIndex;
				}
			}
		});
		this.add(new JScrollPane(list), BorderLayout.CENTER);
		
		innerPanel = new JPanel();
		innerPanel.setBackground(Assets.backgroundPanelColor);
		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.X_AXIS));
		
		btnViewIdea = new JButton("view");
		btnViewIdea.setFont(Assets.fontCaviarDreams.deriveFont(Assets.FONT_SIZE_DEFAULT));
		btnViewIdea.setFocusPainted(false);
		btnViewIdea.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				viewIdea();
			}
		});
		innerPanel.add(Box.createHorizontalGlue());
		innerPanel.add(btnViewIdea);
		
		btnEditIdea = new JButton("edit");
		btnEditIdea.setFont(Assets.fontCaviarDreams.deriveFont(Assets.FONT_SIZE_DEFAULT));
		btnEditIdea.setFocusPainted(false);
		btnEditIdea.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				editIdea();
			}
		});
		innerPanel.add(Box.createRigidArea(new Dimension(5,0)));
		innerPanel.add(btnEditIdea);
		
		btnCancel = new JButton("exit");
		btnCancel.setFont(Assets.fontCaviarDreams.deriveFont(Assets.FONT_SIZE_DEFAULT));
		btnCancel.setFocusPainted(false);
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				PanelManager.show(PanelManager.MENU_MAIN);
			}
		});
		innerPanel.add(Box.createRigidArea(new Dimension(5,0)));
		innerPanel.add(btnCancel);
		
		innerPanel.add(Box.createHorizontalGlue());
		this.add(innerPanel, BorderLayout.SOUTH);
	}
	
	/**
	 * Sets an idea to be viewed by the user and displays
	 * the corresponding menu.
	 */
	private void viewIdea() {
		if (curSelectedIndex < 0) {
			setButtonsEnabled(false);
			return;
		}
		
		String ideaName = listSearchResults.getElementAt(curSelectedIndex);
		String ideaCategory = ideaCategoryTree.get(ideaName);
		Idea loadedIdea = Idea.loadIdea(ideaName, ideaCategory);
		ViewPanel.setCurrentIdea(loadedIdea);
		PanelManager.show(PanelManager.MENU_VIEW);
	}
	
	/**
	 * Sets an idea to be edited by the user and displays
	 * the corresponding menu.
	 */
	private void editIdea() {
		if (curSelectedIndex < 0) {
			setButtonsEnabled(false);
			return;
		}
		
		String ideaName = listSearchResults.getElementAt(curSelectedIndex);
		String ideaCategory = ideaCategoryTree.get(ideaName);
		Idea loadedIdea = Idea.loadIdea(ideaName, ideaCategory);
		EditPanel.setCurrentIdea(loadedIdea);
		PanelManager.show(PanelManager.MENU_EDIT);
	}
	
	/**
	 * Enables or disables <code>btnViewIdea</code> and <code>btnEditIdea</code>
	 * depending on <code>buttonSet</code>.
	 * 
	 * @param buttonSet whether the buttons should be enabled or disabled
	 */
	private void setButtonsEnabled(boolean buttonSet) {
		if (btnViewIdea.isEnabled() != buttonSet)
			btnViewIdea.setEnabled(buttonSet);
		if (btnEditIdea.isEnabled() != buttonSet)
			btnEditIdea.setEnabled(buttonSet);
	}
	
	/**
	 * Checks the contents of <code>textSearchTerms</code> and uses them
	 * to compare to the names of existing ideas and tags, building a list
	 * which matches the user's search. The results of the search are the names
	 * of all found ideas stored in <code>searchResultsTree</code> and displayed
	 * in the <code>JList</code> which utilizes <code>listSearchResults</code>.
	 */
	private void updateSearchResults() {
		Iterator<String> stringIterator = null;
		String searchTerms = textSearchTerms.getText().toUpperCase();
		String searchCategory = comboCategory.getItemAt(comboCategory.getSelectedIndex());
		String curIdea = null;
		String curIdeaUpperCase = null;
		Tag curTag = null;
		
		boolean searchAllCategories = (searchCategory == STR_CATEGORY_ALL);
		
		if (searchTerms == null || searchTerms.length() == 0) {
			searchResultsTree.clear();
			stringIterator = ideaNameTree.iterator();
			while (stringIterator.hasNext()) {
				curIdea = stringIterator.next();
				if (searchAllCategories || ideaCategoryTree.get(curIdea).equalsIgnoreCase(searchCategory))
					searchResultsTree.add(curIdea);
			}
		} else {
			boolean expandedLastSearch = searchTerms.startsWith(lastSearch);
			lastSearch = searchTerms;
			
			if (expandedLastSearch) {
				stringIterator = searchResultsTree.iterator();
				while (stringIterator.hasNext()) {
					curIdea = stringIterator.next().toUpperCase();
					if (!curIdea.contains(searchTerms))
						stringIterator.remove();
				}
				
				Iterator<Tag> tagIterator = tagTree.iterator();
				while (tagIterator.hasNext()) {
					curTag = tagIterator.next();
					if (curTag.getID().toUpperCase().contains(searchTerms)) {
						stringIterator = curTag.ideaIterator();
						while (stringIterator.hasNext())
							searchResultsTree.add(stringIterator.next());
					}
				}
			} else {
				searchResultsTree.clear();
				stringIterator = ideaNameTree.iterator();
				Iterator<Tag> tagIterator = tagTree.iterator();
				
				while (stringIterator.hasNext()) {
					curIdea = stringIterator.next();
					curIdeaUpperCase = curIdea.toUpperCase();
					if (curIdeaUpperCase.contains(searchTerms) && (searchAllCategories || ideaCategoryTree.get(curIdea).equalsIgnoreCase(searchCategory)))
						searchResultsTree.add(curIdea);
				}
				
				while (tagIterator.hasNext()) {
					curTag = tagIterator.next();
					if (curTag.getID().toUpperCase().contains(searchTerms)) {
						stringIterator = curTag.ideaIterator();
						while (stringIterator.hasNext())
							searchResultsTree.add(stringIterator.next());
					}
				}
			}
		}
		
		stringIterator = searchResultsTree.iterator();
		listSearchResults.clear();
		while (stringIterator.hasNext())
			listSearchResults.addElement(stringIterator.next());
	}

	/**
	 * Clears the search results and loads all the names of existing ideas
	 * and tags into <code>ideaNameTree</code> and <code>tagTree</code>
	 * respectively.
	 */
	@Override
	public void refresh() {
		Iterator<String> categoryIterator = Category.getCategoryNamesIterator();
		
		ideaNameTree = new TreeSet<String>();
		ideaCategoryTree = new TreeMap<String, String>();
		Idea.loadAllIdeaNames(ideaNameTree, ideaCategoryTree);
		
		tagTree = new TreeSet<Tag>();
		Tag.loadAllTags(tagTree);
		
		searchResultsTree = new TreeSet<String>();
		
		textSearchTerms.setText(null);
		
		comboCategory.removeAllItems();
		comboCategory.addItem(STR_CATEGORY_ALL);
		while(categoryIterator.hasNext())
			comboCategory.addItem(categoryIterator.next());
		comboCategory.setSelectedIndex(0);
		comboCategory.repaint();
		comboCategory.validate();
		
		setButtonsEnabled(false);
		updateSearchResults();
	}
	
	/**
	 * Sets the values of instance variables to null
	 * and clears <code>listSearchResults</code>.
	 */
	@Override
	public void close() {
		ideaNameTree = null;
		ideaCategoryTree = null;
		tagTree = null;
		searchResultsTree = null;
		listSearchResults.clear();
	}
	
	/**
	 * Does nothing.
	 */
	@Override
	public void save() {
		
	}
}
