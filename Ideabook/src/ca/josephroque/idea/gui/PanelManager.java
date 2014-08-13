package ca.josephroque.idea.gui;

import java.awt.CardLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

/**
 * Manages the panels which the application switches between
 * as the user traverses the menus and features.
 * 
 * @author Joseph Roque
 * @since 2014-06-01
 *
 */
public class PanelManager extends JPanel {

	/** Default serialVersionUID */
	private static final long serialVersionUID = 1L;
	
	/** String to represent the main menu */
	public static final String MENU_MAIN = "Main Menu";
	/** String to represent the new idea menu */
	public static final String MENU_SUBMIT = "Submit";
	/** String to represent the panel which display's an <code>Idea</code> objects data */
	public static final String MENU_VIEW = "View";
	/** String to represent the edit idea menu */
	public static final String MENU_EDIT = "Edit";
	/** String to represent the settings menu */
	public static final String MENU_SETTINGS = "Settings";
	/** String to represent the search menu */
	public static final String MENU_SEARCH = "Search";
	
	/** <code>CardLayout</code> object which organizes the panels */
	private static CardLayout cardLayout = null;
	/** An instance of this class */
	private static PanelManager instance = null;
	
	/** The main menu panel */
	private static RefreshablePanel mainMenuPanel = null;
	/** The new idea panel */
	private static RefreshablePanel submitPanel = null;
	/** The view idea panel */
	private static RefreshablePanel viewPanel = null;
	/** The edit idea panel */
	private static RefreshablePanel editPanel = null;
	/** The settings menu panel */
	private static RefreshablePanel settingsPanel = null;
	/** The search menu panel */
	private static RefreshablePanel searchPanel = null;
	
	/** The most recently shown panel */
	private static String lastPanel = null;

	/**
	 * Initializes this object by creating new instances of all the
	 * panels and adding them to a <code>CardLayout</code>.
	 */
	private PanelManager() {
		super();
		instance = this;
		cardLayout = new CardLayout();
		
		setLayout(cardLayout);
		setPreferredSize(new Dimension(800, 600));
		
		mainMenuPanel = new MainMenuPanel();
		submitPanel = new SubmitPanel();
		viewPanel = new ViewPanel();
		editPanel = new EditPanel();
		settingsPanel = new SettingsPanel();
		searchPanel = new SearchPanel();
		
		add(mainMenuPanel, PanelManager.MENU_MAIN);
		add(submitPanel, PanelManager.MENU_SUBMIT);
		add(viewPanel, PanelManager.MENU_VIEW);
		add(editPanel, PanelManager.MENU_EDIT);
		add(settingsPanel, PanelManager.MENU_SETTINGS);
		add(searchPanel, PanelManager.MENU_SEARCH);
		
		show(PanelManager.MENU_MAIN);
	}
	
	/**
	 * Creates an instance of this object, if one does not already exist,
	 * and returns it.
	 * @return the value of <code>instance</code>
	 */
	public static PanelManager getInstance() {
		if (instance == null ) {
			instance = new PanelManager();
		}
		return instance;
	}
	
	/**
	 * Returns the String which represents the most recently shown panel.
	 * @return the value of <code>lastPanel</code>
	 */
	public static String getCurrentPanel() {
		return lastPanel;
	}
	
	/**
	 * Returns the panel which corresponds to the provided String.
	 * @param panelName the panel to return
	 * @return the panel which corresponds to <code>panelName</code>
	 */
	public static RefreshablePanel getPanel(String panelName) {
		if (panelName == MENU_MAIN)
			return mainMenuPanel;
		else if (panelName == MENU_SUBMIT)
			return submitPanel;
		else if (panelName == MENU_VIEW)
			return viewPanel;
		else if (panelName == MENU_EDIT)
			return editPanel;
		else if (panelName == MENU_SETTINGS)
			return settingsPanel;
		else if (panelName == MENU_SEARCH)
			return searchPanel;
		else
			return null;
	}
	
	/**
	 * Calls the close method on the panel represented by <code>lastPanel</code>,
	 * if there is one, then calls refresh on the panel represented by <code>panelName</code>
	 * and shows it.
	 * 
	 * @param panelName the panel to be shown
	 * @see ca.josephroque.idea.gui.RefreshablePanel#close()
	 * @see ca.josephroque.idea.gui.RefreshablePanel#refresh()
	 */
	public static void show(String panelName) {
		if (lastPanel == MENU_MAIN)
			mainMenuPanel.close();
		else if (lastPanel == MENU_SUBMIT)
			submitPanel.close();
		else if (lastPanel == MENU_VIEW)
			viewPanel.close();
		else if (lastPanel == MENU_EDIT)
			editPanel.close();
		else if (lastPanel == MENU_SETTINGS)
			settingsPanel.close();
		else if (lastPanel == MENU_SEARCH)
			searchPanel.close();
		
		if (MENU_MAIN == panelName) {
			mainMenuPanel.refresh();
		} else if (MENU_SUBMIT == panelName) {
			submitPanel.refresh();
		} else if (MENU_VIEW == panelName) {
			viewPanel.refresh();
		} else if (MENU_EDIT == panelName) {
			editPanel.refresh();
		} else if (MENU_SETTINGS == panelName) {
			settingsPanel.refresh();
		} else if (MENU_SEARCH == panelName) {
			searchPanel.refresh();
		}
		
		lastPanel = panelName;
		cardLayout.show(instance, panelName);
	}
}
