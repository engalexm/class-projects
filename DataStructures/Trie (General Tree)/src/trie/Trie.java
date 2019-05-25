package trie;

import java.util.ArrayList;

/**
 * This class implements a Trie. 
 * 
 * @author Sesh Venugopal
 *
 */
public class Trie {
	
	// prevent instantiation
	private Trie() { }
	
	/**
	 * Builds a trie by inserting all words in the input array, one at a time,
	 * in sequence FROM FIRST TO LAST. (The sequence is IMPORTANT!)
	 * The words in the input array are all lower case.
	 * 
	 * @param allWords Input array of words (lowercase) to be inserted.
	 * @return Root of trie with all words inserted from the input array
	 */
	public static TrieNode buildTrie(String[] allWords) {
		// Create an empty root; the root is always empty.
		TrieNode root = new TrieNode(null, null, null);
		// Add the first word to the Trie
		root.firstChild = new TrieNode(new Indexes (0, (short) 0, (short) (allWords[0].length()-1)), null, null);
		// Iterate through the rest of the Strings in the String[] array
		for(int i = 1; i < allWords.length; i++) {
			String word = allWords[i];
			TrieNode ptr = new TrieNode(null, null, null);
			short matchingEndofPrefix = -1;
			String piece = "!";
			/* SEARCHING FOR THE LONGEST MATCHING PREFIX.
			 * Iterate through every substring of the word, starting from the beginning to the end, e.g.
			 * for "artichoke" check "artichoke", "artichok", "articho", ... , "ar", "a"
			 * Then, grab a ptr to the largest possible prefix which is contained by the word.
			 * We do this because we want to add the new TrieNode to the longest possible prefix node.
			 * (1) Search the tree for each piece.
			 * (2) If the search function returns root, it means there is no longest prefix node for that piece. 
			 *     But you still have to check the rest of the smaller pieces. 
			 *     So continue until the search function doesn't return root. 
			 * (3) Break when ptr points to the largest possible prefix which is contained by the word.
			 * (4) Set "matchingEndofPrefix" s.t. it denotes the index at which the prefix ends within the word.
			 */
			for(int j = word.length(); j >= 1; j--) {
				piece = word.substring(0, j);
				// Search and get a ptr to the topmost TrieNode containing the target substring
				ptr = search(allWords, root, piece);
				// Below are the cases when ptr points to a "real" node (not the root)
				if(ptr != root && ptr != null) {   matchingEndofPrefix = (short) (j - 1); break;   }
			}
			/* If the search function returned null after checking every piece of the word,
			 * then we need to add that word to the root. 
			 */
			if (ptr == null) {   ptr = root;   }
			/* INSERTING THE WORD IN THE RIGHT PLACE, IN THE RIGHT WAY: 3 CASES.
			 * CASE 1: ptr is the root, i.e. 
			 * The word has no prefix in the tree; we need to add it at the root. Do:
			 * (1) Index the node we want to add based on the entire word (since there's no prefixes).
			 * (2) Go to the first child of the root. There exists at least one since we manually added the first word above.
			 * (3) Add the new node to the end of the LL of siblings.
			 */
			if(ptr == root) {
				TrieNode toAdd = new TrieNode(new Indexes(i, (short) 0, (short) (word.length() - 1)), null, null);
				TrieNode pointer = ptr.firstChild;   TrieNode prev = null;
				while(pointer != null) {
					prev = pointer;
					pointer = pointer.sibling;
				}
				prev.sibling = toAdd;
			/* CASE 2: ptr is a purely prefix node, i.e.
			 * It must have some children, so we need to add our new node as a sibling. 
			 * Suprise! There's another two cases here.
			 * (2A) The entire prefix is contained by the word, e.g. the prefix is "ba" and the word is "band".
			 * (2B) A prefix of the prefix (not the entire prefix) is contained by the word, e.g. the prefix is "be" and the word is "band."
			 */ 	
			} else if(ptr.firstChild != null) {
				String prefix = allWords[ptr.substr.wordIndex].substring(0, ptr.substr.endIndex + 1);
				
				/* CASE 2A: The entire prefix is contained by the word. Do:
				 * (1) Index the node we want to add based on where the prefix ends.
				 * (2) Add it to the end of that prefix's LL of children, as a sibling.
				 */
				if(piece.equals(prefix)) {
					TrieNode toAdd = new TrieNode(new Indexes(i, (short) ((short) matchingEndofPrefix + 1), (short) ((short) word.length() - 1)), null, null);
					TrieNode pointer = ptr.firstChild;   TrieNode prev = null;
					while(pointer != null) {
						prev = pointer;
						pointer = pointer.sibling;
					}
					prev.sibling = toAdd;
				}
				/* CASE 2B: A prefix of the prefix is contained by the word. Do:
				 * (1) Index the node we want to add based on where the REAL prefix ends.
				 * (2) Make a temp TrieNode, that will be the secondary prefix under the REAL prefix,
				 * 		which copies the FAKE (old) prefix node's indices, children, and siblings, except:
				 * 			The start index is one after where the REAL prefix ends.
				 * 			The sibling is the node we want to add.
				 * (3) Change the REAL prefix (ptr) end index to be where the REAL prefix ends.
				 * (4) Set temp from (2) to be the child of ptr, our new, REAL prefix node.
				 */
				else {
					TrieNode toAdd = new TrieNode(new Indexes(i,(short) ((short) matchingEndofPrefix + 1), (short) ((short) word.length() - 1)), null, null);
					TrieNode temp = new TrieNode(new Indexes(ptr.substr.wordIndex, (short) ((short) matchingEndofPrefix + 1), ptr.substr.endIndex), ptr.firstChild, toAdd);
					ptr.substr.endIndex = matchingEndofPrefix;			
					ptr.firstChild = temp;
				}
			
			/* CASE 3: ptr is a leaf node, i.e. 
			 * It has no first child.
			 * Then, we need to make both the existing leaf node, and the node we want to add, sibling children of a new prefix node. Do:
			 * (1) Index the node we want to add based on where the prefix ends.
			 * (2) Backup the old leaf node's indices substring into a temp.
			 * (3) Reindex the ptr to make it a purely prefix node, based on where the prefix starts (unchanged) & ends (similar to (1)).
			 * (4) Index the old leaf node, using (2), as a "suffix" node child of the new prefix node we made in (3).
			 * (5) Link the new "suffix" node as the first child of the prefix node we made in (3), linking the node we wanted to add as its sibling. 
			 */
			} else {
				TrieNode toAdd = new TrieNode(new Indexes(i, (short) (matchingEndofPrefix + 1), (short) (word.length() - 1)), null, null);
				Indexes temp = new Indexes(ptr.substr.wordIndex, ptr.substr.startIndex, ptr.substr.endIndex);
				ptr.substr.endIndex = matchingEndofPrefix;
				temp.startIndex = (short) (matchingEndofPrefix + 1);
				ptr.firstChild = new TrieNode (temp, null, toAdd);
				
			}
		}
		return root;
	}
	
	/**
	 * Given a trie, returns the "completion list" for a prefix, i.e. all the leaf nodes in the 
	 * trie whose words start with this prefix. 
	 * For instance, if the trie had the words "bear", "bull", "stock", and "bell",
	 * the completion list for prefix "b" would be the leaf nodes that hold "bear", "bull", and "bell"; 
	 * for prefix "be", the completion would be the leaf nodes that hold "bear" and "bell", 
	 * and for prefix "bell", completion would be the leaf node that holds "bell". 
	 * (The last example shows that an input prefix can be an entire word.) 
	 * The order of returned leaf nodes DOES NOT MATTER. So, for prefix "be",
	 * the returned list of leaf nodes can be either hold [bear,bell] or [bell,bear].
	 *
	 * @param root Root of Trie that stores all words to search on for completion lists
	 * @param allWords Array of words that have been inserted into the trie
	 * @param prefix Prefix to be completed with words in trie
	 * @return List of all leaf nodes in trie that hold words that start with the prefix, 
	 * 			order of leaf nodes does not matter.
	 *         If there is no word in the tree that has this prefix, null is returned.
	 */
	public static ArrayList<TrieNode> completionList(TrieNode root,
										String[] allWords, String prefix) {
		// If the Trie is empty, return nothing.
		if(root.firstChild == null) { return null; }
		ArrayList<TrieNode> list = new ArrayList<TrieNode>(); 
		addToList(list, allWords, root.firstChild, prefix);
		if(list.isEmpty()) {   return null;   }
		else {   return list;   }	
	}
	
	public static void print(TrieNode root, String[] allWords) {
		System.out.println("\nTRIE\n");
		print(root, 1, allWords);
	}
	
	private static void print(TrieNode root, int indent, String[] words) {
		if (root == null) {
			return;
		}
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		
		if (root.substr != null) {
			String pre = words[root.substr.wordIndex]
							.substring(0, root.substr.endIndex+1);
			System.out.println("      " + pre);
		}
		
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		if (root.substr == null) {
			System.out.println("root");
		} else {
			System.out.println(root.substr);
		}
		
		for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
			for (int i=0; i < indent-1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent+1, words);
		}
	}
	
	private static TrieNode search(String[] allWords, TrieNode root, String target) {
		if(root.substr == null) {   
			if(search(allWords, root.firstChild, target) != null) {   return search(allWords, root.firstChild, target);   }
			else {   return root;  }
		}
		if(allWords[root.substr.wordIndex].substring(0, (int) root.substr.endIndex + 1).startsWith(target)) {
			return root;
		}
		/* for(int i = root.substr.endIndex + 1; i >= 1; i--) {
			String piece = allWords[root.substr.wordIndex].substring(0, i);
			if(piece.equals(target)) {   return root;   }
		} */
		if(root.sibling != null) {
			if(search(allWords, root.sibling, target) != null) {  return search(allWords, root.sibling, target);   }
		}
		if(root.firstChild != null) {
			if(search(allWords, root.firstChild, target) != null) {   return search(allWords, root.firstChild, target);   }
		}
		return null;
	}
	
	
	private static void addToList (ArrayList<TrieNode> list, String[] allWords, TrieNode root, String prefix) {
		String piece = allWords[root.substr.wordIndex].substring(0, root.substr.endIndex+1);
		TrieNode ptr = root;
		/* Recurisvely add the siblings or children to the list based on whether or not
		 * (1) they exist
		 * (2) the current piece is a prefix of prefix, or prefix is a prefix of the current piece.
		 */
		if(prefix.equals(piece)) {
			if(ptr.firstChild == null) {
				list.add(ptr);
				if(ptr.sibling != null) {
					addToList(list, allWords, ptr.sibling, prefix);
				}
				return;
			} else {
				addToList(list, allWords, ptr.firstChild, prefix);
				return;
			}
		} else if(prefix.startsWith(piece)) {
			if(ptr.firstChild == null) {
				/* list.add(ptr);
				if(ptr.sibling != null) {
					addToList(list, allWords, ptr.sibling, prefix);
				} */
				return;
			} else {
				addToList(list, allWords, ptr.firstChild, prefix);
				return;
			}
		} else if(piece.startsWith(prefix)) {
			if(ptr.firstChild == null) {
				list.add(ptr);
				if(ptr.sibling != null) {
					addToList(list, allWords, ptr.sibling, prefix);
				}
				return;
			} else {
				addToList(list, allWords, ptr.firstChild, prefix);
				if(ptr.sibling != null) {
					addToList(list, allWords, ptr.sibling, prefix);
				}
				return;
			}	
		} else {
			if(ptr.sibling != null) {
				addToList(list, allWords, ptr.sibling, prefix);
			}
			return;
		}
	}
	
 }
