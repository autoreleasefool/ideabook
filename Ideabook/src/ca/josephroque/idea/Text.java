package ca.josephroque.idea;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class Text {
	
	public static final String regex_AlphaNumeric = "[- a-zA-Z0-9]*";
	public static final String regex_CommaSeparated = "[- a-zA-Z0-9!@#$%&.,+]*(, *[- a-zA-Z0-9!@#$%&.,+]*)*";
	public static final String regex_IdeaName = "[- a-zA-Z0-9!@#$%&.,+]*";
	
	public static final int IDEA_NAME_MAXLENGTH = 32;

	public static class PatternDocument extends PlainDocument {
		
		private static final long serialVersionUID = 1L;
		private int maxLength;
		private String pattern;
		
		public PatternDocument(String pattern) {
			this(pattern, Integer.MAX_VALUE);
		}
		
		public PatternDocument(String pattern, int maxLength) {
			super();
			this.pattern = pattern;
			this.maxLength = maxLength;
		}
		
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
			if (str == null)
				return;

			if (str.matches(pattern) && this.getLength() + str.length() < maxLength)
				super.insertString(offs, str, a);
		}
	}
	
	public static boolean isCommaSeparated(String str) {
		if (str != null)
			return str.matches(regex_CommaSeparated);
		else
			return true;
	}
}
