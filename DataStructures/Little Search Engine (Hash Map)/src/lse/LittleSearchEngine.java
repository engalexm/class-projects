package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		HashMap<String,Occurrence> kwsFromThisFile = new HashMap<String, Occurrence>();
		Scanner sc = new Scanner(new File(docFile));
		while(sc.hasNext()) {
			String kw = getKeyword(sc.next());
			if(kw != null) {
				if(kwsFromThisFile.containsKey(kw)) {   kwsFromThisFile.put(kw, new Occurrence(docFile, kwsFromThisFile.get(kw).frequency + 1));   } 
				else {   kwsFromThisFile.put(kw, new Occurrence(docFile, 1));   }
			}
		}
		sc.close();
		return kwsFromThisFile;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		Iterator<String> it = kws.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			ArrayList<Occurrence> occ = keywordsIndex.get(key);
			if(occ == null) {   occ = new ArrayList<Occurrence>();   }	
			occ.add(kws.get(key));
			ArrayList<Integer> throwaway = insertLastOccurrence(occ);
			keywordsIndex.put(key, occ);
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		String punctuation = ".,?:;!";  String cleaned = word;  int last = cleaned.length() - 1;
		// Trim trailing punctuation marks; return null if we've cut everything off (it was all punctuation marks)
		while(punctuation.indexOf(cleaned.charAt(last)) != -1)    {
			cleaned = cleaned.substring(0, cleaned.length() - 1);
			last = cleaned.length() - 1;
			if(last == -1) {   return null;   }
		}
		// Reject words with any non-alphabetic characters
		for(int i = 0; i < cleaned.length(); i++) {
			if(!Character.isLetter(cleaned.charAt(i))) {   return null;   }
		}
		// Make the keyword lower case
		cleaned = cleaned.toLowerCase();
		// Reject noise words, return non-noise words
		if(noiseWords.contains(cleaned)) {   return null;   } 
		else {   return cleaned;   }
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		// If input list has size 1, then it's already in order
		if(occs.size() == 1) {   return null;   }
		ArrayList<Integer> midpoints = new ArrayList<Integer>();
		Occurrence lastCopy = new Occurrence(occs.get(occs.size() - 1).document, occs.get(occs.size()-1).frequency);
		// Do ya thang binary search
		int left = 0;
		int right = occs.size() - 2;	
		int mid = (left + right) / 2; 
		while(left <= right) {
			mid = (left + right) / 2;
			midpoints.add(mid);		// Add that boi to the ArrayList
			if(lastCopy.frequency == occs.get(mid).frequency) {   break;   } 
			else if(lastCopy.frequency < occs.get(mid).frequency) {   left = mid + 1;   } 
			else {   right = mid - 1;   }
		}
		// If last should be added before mid, then do it. If not, then add it after mid.
		if(lastCopy.frequency > occs.get(mid).frequency) {   occs.add(mid, lastCopy);   } 
		else {   occs.add(mid + 1, lastCopy);   }
		// We added a copy of last where it belonged. So now we have to remove the original "last." 
		occs.remove(occs.size() - 1);
		return midpoints;
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, returns null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		ArrayList<String> docs = new ArrayList<String>(5);
		ArrayList<Occurrence> kw1occ = new ArrayList<Occurrence>(); ArrayList<Occurrence> kw2occ = new ArrayList<Occurrence>();
		if(keywordsIndex.containsKey(kw1)) {
			for(int i = 0; i < keywordsIndex.get(kw1).size(); i++) {
				kw1occ.add(new Occurrence (keywordsIndex.get(kw1).get(i).document, keywordsIndex.get(kw1).get(i).frequency));
			}
		}
		if(keywordsIndex.containsKey(kw2)) {
			for(int i = 0; i < keywordsIndex.get(kw2).size(); i++) {
				kw2occ.add(new Occurrence (keywordsIndex.get(kw2).get(i).document, keywordsIndex.get(kw2).get(i).frequency));
			}
		}
		// No match!
		if(kw1occ.isEmpty() && kw2occ.isEmpty()) {   return null;   }
		/* Both words matched.
		 * (1) Merge kw1occ and kw2occ such that if there's Occurrences in both (one and the other)
		 * 		with the same document title, the final Occurrences AL has the max. frequency for that doc. (Either if freqs are the same.)
		 * 		We don't duplicate that Occurrence key (doc name) if both keywords appear in the same doc.
		 * (2) If a given keyword from kw2occ has an Occurrence with document title not existing in kw1occ, 
		 * 		then insert that Occurrence in kw1occ in the right place (order) based on its frequency. 
		 * 		We make use of the insertLastOccurrence method written before, which will place docs matching kw2
		 * 		after docs matching kw1 in the case where kw1.freq = kw2.freq, as needed. 
		 */
		else if((!kw1occ.isEmpty()) && (!kw2occ.isEmpty())) {			
			boolean foundMatchingDocName = false;
			for(int i = 0; i < kw2occ.size(); i++) {
				for(int j = 0; j < kw1occ.size(); j++) {
					// If there is a matching doc name in both, add a new occ with the max freq at the end (delete the old occ).
					if(kw2occ.get(i).document.equals(kw1occ.get(j).document)) {
						kw1occ.add(new Occurrence(kw1occ.get(j).document, Math.max(kw1occ.get(j).frequency, kw2occ.get(i).frequency)));
						kw1occ.remove(j);
						foundMatchingDocName = true;
						break;
					}
				}
				// If there wasn't a matching doc name in kw1occ, then just add the new occ at the end. 
				if(!foundMatchingDocName) {   kw1occ.add(kw2occ.get(i));   } 
				// Either way, some new occ will be added at the end of kw1occ. Use insertLastOccurrence to place it correctly.
				ArrayList<Integer> saveMe = insertLastOccurrence(kw1occ);	
			}
			// Copy the top 5 document names into docs, now sorted by freq
			for(int k = 0; k < Math.min(5, kw1occ.size()); k++) {   docs.add(kw1occ.get(k).document);   }
		// Only the first keyword matched; return the top 5 names in its occurrence AL (already sorted by freq)
		} else if((!kw1occ.isEmpty()) && (kw2occ.isEmpty())) {
			for(int i = 0; i < Math.min(5, kw1occ.size()); i++) {   docs.add(kw1occ.get(i).document);   }
		// Only the second keyword matched; return the top 5 names in its occurrence AL (already sorted by freq)
		} else /* if((kw1occ.isEmpty()) && (!kw2occ.isEmpty()) */ {
			for(int i = 0; i < Math.min(5, kw2occ.size()); i++) {   docs.add(kw2occ.get(i).document);   }
		}
		return docs;	
	}
}
