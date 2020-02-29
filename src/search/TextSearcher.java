package search;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TextSearcher {
	private ArrayList<String> tokens;
	private HashMap<String, ArrayList<Integer>> tokenLocations;
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

        // Set up tokens list and locations map in one pass
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
					ArrayList<Integer> locations = new ArrayList<>();
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
		// Can add extra validation and formatting here to guard against edge cases.
		String formattedQueryWord = queryWord.toLowerCase().trim();

		if (!this.tokenLocations.containsKey(formattedQueryWord)){
			return new String[0];
		}

		List<String> results = new ArrayList<>();
		ArrayList<Integer> matchedQueryWordIndices = this.tokenLocations.get(formattedQueryWord);

		matchedQueryWordIndices.forEach((i) -> results.add(
				_getPhrase(i, contextWords)
		));

		return results.toArray(new String[0]);
	}

	private String _getPhrase(int queryWordIndex, int contextWords) {
		StringBuilder phrase = new StringBuilder(this.tokens.get(queryWordIndex));

		if (contextWords <= 0) {
			return phrase.toString();
		}

		String leftContext = _getContext(queryWordIndex, "left", contextWords);
		String rightContext = _getContext(queryWordIndex, "right", contextWords);

		phrase.insert(0, leftContext);
		phrase.append(rightContext);

		return phrase.toString();
	}

	private String _getContext(int queryWordIndex, String direction, int contextWords) {
		StringBuilder context = new StringBuilder();

		int contextWordCount = 0;
		int iter = direction.equals("left") ? -1 : 1;
		int tokenIndex = queryWordIndex + iter;

		while (
				contextWordCount < contextWords
				&& tokenIndex >= 0
				&& tokenIndex < this.tokens.size()
		) {
			String token = this.tokens.get(tokenIndex);

			if (this.tokenizer.isWord(token)) { contextWordCount++; }

			if (direction.equals("left")) {
				context.insert(0, token);
			} else {
				context.append(token);
			}

			tokenIndex += iter;
		}

		return context.toString();
	}
}

// Any needed utility classes can just go in this file

