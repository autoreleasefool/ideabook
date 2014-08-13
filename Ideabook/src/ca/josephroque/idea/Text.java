package ca.josephroque.idea;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Methods and classes relevant to processing text in the application.
 * 
 * @author Joseph Roque
 * @since 2014-06-23
 */
public class Text {
	
	/** Regular expression matching an alphanumeric string */
	public static final String regex_AlphaNumeric = "[- a-zA-Z0-9]*";
	/** Regular expression matching a string which is comma separated */
	public static final String regex_CommaSeparated = "[- a-zA-Z0-9!@#$%&.,+]*(, *[- a-zA-Z0-9!@#$%&.,+]*)*";
	/** Regular expression matching an alphanumeric string with special characters */
	public static final String regex_IdeaName = "[- a-zA-Z0-9!@#$%&.,+]*";
	/** Regular expression matching a lowercase string which is comma separated */
	public static final String regex_CommaSeparatedAndLower = "[- a-z0-9!@#$%&.,+]*(, *[- a-z0-9!@#$%&.,+]*)*";
	
	/** The maximum length an idea name can be */
	public static final int IDEA_NAME_MAXLENGTH = 32;

	/**
	 * A {@link javax.swing.text.PlainDocument} of which the string it manages
	 * must match the provided regular expression.
	 * 
	 * @author Joseph Roque
	 * @since 2014-06-23
	 */
	public static class PatternDocument extends PlainDocument {
		
		/** Default serialVersionUID */
		private static final long serialVersionUID = 1L;
		/** Maximum length of the document's string */
		private int maxLength;
		/** Regular expression which the document's string must match */
		private String pattern;
		
		/**
		 * Calls constructor with provided pattern and Integer.MAX_VALUE
		 * 
		 * @param pattern regular expression which the document's string must match
		 */
		public PatternDocument(String pattern) {
			this(pattern, Integer.MAX_VALUE);
		}
		
		/**
		 * Sets the values of <code>maxLength</code> and <code>pattern</code>.
		 * 
		 * @param pattern regular expression which the document's string must match
		 * @param maxLength maximum length of the document's string
		 */
		public PatternDocument(String pattern, int maxLength) {
			super();
			this.pattern = pattern;
			this.maxLength = maxLength;
		}
		
		/**
		 * Compares <code>str</code> to the pattern
		 */
		@Override
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
			if (str == null)
				return;
			
			//TODO This needs to be fixed, it only checks the new string, not the entire string of the document
			if (str.matches(pattern) && this.getLength() + str.length() < maxLength)
				super.insertString(offs, str, a);
		}
	}
	
	/**
	 * Returns true if the provided string is comma separated or null, false otherwise.
	 * 
	 * @param str the string to be checked
	 * @return if str is not null, returns whether the string matches <code>regex_CommaSeparated</code>, true otherwise.
	 * @see java.lang.String#matches(String)
	 */
	public static boolean isCommaSeparated(String str) {
		if (str != null)
			return str.matches(regex_CommaSeparated);
		else
			return true;
	}
}
