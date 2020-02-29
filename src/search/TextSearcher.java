package search;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TextSearcher {
	protected ArrayList<String> tokens;
	protected HashMap<String, ArrayList<Integer>> tokenLocations;
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
		// TODO -- fill in implementation
		List<String> results = new ArrayList<>();
//		format query word to lowercase
		String formattedWord = queryWord.toLowerCase().trim();

//		if not in token locations, return empty array
		if (!this.tokenLocations.containsKey(formattedWord)){
			return results.toArray(new String[0]);
		}

//		else, get token locations
		ArrayList<Integer> locations = this.tokenLocations.get(formattedWord);
//		for each location
//		get and add context for phrase
//		add phrase to results array
		locations.forEach((i) -> results.add(
				_getPhrase(i, contextWords)
		));

		//		return results array
		return results.toArray(new String[0]);
	}

	private String _getPhrase(int queryWordIndex, int contextWords) {
		StringBuilder phrase = new StringBuilder(this.tokens.get(queryWordIndex));
		String leftContext = _getContext(queryWordIndex, "left", contextWords);
		String rightContext = _getContext(queryWordIndex, "right", contextWords);

		phrase.insert(0, leftContext);
		phrase.append(rightContext);

		return phrase.toString();
	}

	private String _getContext(int queryWordIndex, String direction, int contextWords) {
		StringBuilder context = new StringBuilder();
		int moveOver = direction == "left" ? -1 : 1;
		int contextWordCount = 0;
		int tokenIndex = queryWordIndex + moveOver;

		while (
				contextWordCount < contextWords
				&& tokenIndex >= 0
				&& tokenIndex < this.tokens.size()
		) {
			String token = this.tokens.get(tokenIndex);
			if (this.tokenizer.isWord(token)) {
				contextWordCount++;
			}

			if (direction == "left") {
				context.insert(0, token);
			} else {
				context.append(token);
			}

			tokenIndex += moveOver;
		}

		return context.toString();
	}
}

// Any needed utility classes can just go in this file

