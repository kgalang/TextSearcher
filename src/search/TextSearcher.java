package search;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class TextSearcher {
	protected ArrayList<String> tokens;
	protected HashMap<String, ArrayList<Number>> tokenLocations;
	private TextTokenizer tokenizer;
	private String rePattern = "[a-zA-Z0-9']+";
	/**
	 * Initializes the text searcher with the contents of a text file.
	 * The current implementation just reads the contents into a string 
	 * and passes them to #init().  You may modify this implementation if you need to.
	 * 
	 * @param f Input file.
	 * @throws IOException
	 */
	public TextSearcher(File f) throws IOException {
		FileReader r = new FileReader(f);
		StringWriter w = new StringWriter();
		char[] buf = new char[4096];
		int readCount;

		while ((readCount = r.read(buf)) > 0) {
			w.write(buf,0,readCount);
		}
		
		init(w.toString());
	}
	
	/**
	 *  Initializes any internal data structures that are needed for
	 *  this class to implement search efficiently.
	 */
	protected void init(String fileContents) {
		this.tokens = new ArrayList<>();
		this.tokenLocations = new HashMap<>();
		this.tokenizer = new TextTokenizer(fileContents, this.rePattern);

//		Set up tokens list and locations map in one pass
		int i = 0;
		while (this.tokenizer.hasNext()) {
			String token = this.tokenizer.next();
			this.tokens.add(token);

			if (tokenizer.isWord(token)) {
				String formattedToken = token.toLowerCase();
				Boolean tokenExistsInMap = this.tokenLocations.containsKey(formattedToken);

				if (tokenExistsInMap) {
					this.tokenLocations.get(formattedToken).add(i);
				} else {
					ArrayList<Number> locations = new ArrayList<>();
					locations.add(i);
					this.tokenLocations.put(formattedToken, locations);
				}
			}

			i++;
		}
	}

	/**
	 * 
	 * @param queryWord The word to search for in the file contents.
	 * @param contextWords The number of words of context to provide on
	 *                     each side of the query word.
	 * @return One context string for each time the query word appears in the file.
	 */
	public String[] search(String queryWord,int contextWords) {
		// TODO -- fill in implementation
		return new String[0];
	}
}

// Any needed utility classes can just go in this file

