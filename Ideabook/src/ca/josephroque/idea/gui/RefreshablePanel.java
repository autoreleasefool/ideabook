package ca.josephroque.idea.gui;

import javax.swing.JPanel;

public abstract class RefreshablePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public abstract void refresh();
	public abstract void close();
}
