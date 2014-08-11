package ca.josephroque.idea.gui;

import java.awt.CardLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

import ca.josephroque.idea.Data;

public class PanelManager extends JPanel {

	private static final long serialVersionUID = 1L;
	
	public static final String MENU_MAIN = "Main Menu";
	public static final String MENU_SUBMIT = "Submit";
	public static final String MENU_VIEW = "View";
	public static final String MENU_EDIT = "Edit";
	public static final String MENU_SETTINGS = "Settings";
	public static final String MENU_SEARCH = "Search";
	
	private static CardLayout cardLayout = null;
	private static PanelManager instance = null;
	
	private static RefreshablePanel mainMenuPanel = null;
	private static RefreshablePanel submitPanel = null;
	private static RefreshablePanel viewPanel = null;
	private static RefreshablePanel editPanel = null;
	private static RefreshablePanel settingsPanel = null;
	private static RefreshablePanel searchPanel = null;
	
	private static String lastPanel = null;

	public PanelManager() {
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
		
		Data.checkForUnsavedData();
	}
	
	public static String getCurrentPanel() {
		return lastPanel;
	}
	
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
