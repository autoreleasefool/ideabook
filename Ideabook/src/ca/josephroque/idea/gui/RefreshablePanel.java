package ca.josephroque.idea.gui;

import javax.swing.JPanel;

/**
 * A JPanel which declares three methods: refresh, close and save.
 * <p>
 * These methods allow the contents of the panel to be altered or
 * saved before they are shown, after they are closed, or when the
 * application exits.
 * 
 * @author Joseph Roque
 * @since 2014-06-19
 *
 */
public abstract class RefreshablePanel extends JPanel {

	/** Default serialVersionUID */
	private static final long serialVersionUID = 1L;

	/**
	 * Refreshes the contents of the panel, generally to default
	 * values before they it is displayed to the user.
	 */
	public abstract void refresh();
	
	/**
	 * Flushes the contents of the panel, generally to default
	 * values, after the panel is closed. Saves memory.
	 */
	public abstract void close();
	
	/**
	 * When the application is closed, the contents of the current
	 * panel being used by the user can be saved. This method is
	 * called during a Runtime ShutdownHook.
	 * 
	 * @see ca.josephroque.idea.Data#unloadProgram()
	 * @see java.lang.Runtime#addShutdownHook(Thread)
	 */
	public abstract void save();
}
