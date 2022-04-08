package tp3;

/*
 * TODO: this class is not thread-safe. Make the method add linearizable and wait-free!
 */

import TL2.AbortException;
import TL2.RegisterTL2;
import TL2.TransactionTL2;
import TL2.interfaces.Register;
import TL2.interfaces.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of a set of strings based on a dictionary. 
 * The strings of the set are kept sorted according to their lexicographic ordering and common prefixes of two strings in the set are only encoded once.
 * @author Matthieu Perrin
 */
public class Dictionary {

	/**
	 * A node of the dictionary data structure, representing one character.
	 * As a dictionary is a tree, a node can be only accessed by following one path from the root.
	 * The succession of the characters encoded by the nodes in the path leading to a node, including the node itself, forms a string, 
	 * that is considered present in the set if, and only if, the member "present" is set to true.
	 * 
	 * More formally, the path leading to a node is defined as such:
	 *   - the path leading to the first node is path(start) = "\0";
	 *   - if path(n) = s + n.character, then path(n.suffix) = s + n.character + n.suffix.character
	 *   - if path(n) = s + n.character, then path(n.next) = s + n.suffix.character
	 *   
	 * A word s is contained in the dictionary if there is a node n whose path is s
	 */
	private static class Node {

		// The character of the string encoded in this node of the dictionary
		char character;
		// True if the string leading to this node has already been inserted, false otherwise
		boolean absent = true;
		// Encodes the set of strings starting with the string leading to this word, 
		// including the character encoded by this node
		Register<Node> suffix = new RegisterTL2<>(null);
		// Encodes the set of strings starting with the string leading to this word, 
		// excluding the character encoded by this node, 
		// and whose next character is strictly greater than the character encoded by this node
		Register<Node> next;

		Node(char character, Register<Node> next) {
			this.character = character; 
			this.next = next; 
		}

		/**
		 * Adds the specified string to this set if it is not already present. 
		 * More formally, adds the specified string s to this set if the set contains no element s2 such that s.equals(s2). 
		 * If this set already contains the element, the call leaves the set unchanged and returns false. 
		 * @param s The string that is being inserted in the set
		 * @param depth The number of time the pointer "suffix" has been followed
		 * @return true if s was not already inserted, false otherwise
		 */
		boolean add(String s, int depth, Transaction t) throws AbortException {

			// First case: we are at the end of the string and this is the correct node
			if(depth >= s.length() || (s.charAt(depth) == character) && depth == s.length() - 1) {
				boolean result = absent;
				absent = false;
				return result;
			}

			// Second case: the next character in the string was found, but this is not the end of the string
			// We continue in member "suffix"
			if(s.charAt(depth) == character) {
				if (suffix == null || suffix.read(t).character > s.charAt(depth+1)) {
					suffix.write(t, new Node(s.charAt(depth+1), suffix));
				}

				return suffix.read(t).add(s, depth+1, t);
			}

			// Third case: the next character in the string was not found
			// We continue in member "next"
			// To maintain the order, we may have to add a new node before "next" first
			if (next == null || next.read(t).character > s.charAt(depth)) {
				next.write(t, new Node(s.charAt(depth), next));
			}

			return next.read(t).add(s, depth, t);
		}
	
	}

	// We start with a first node, to simplify the algorithm, that encodes the smallest non-empty string "\0".
	private final Register<Node> start = new RegisterTL2<>(new Node('\0', null));
	// The empty string is stored separately
	private boolean emptyAbsent = true;

	/**
	 * Adds the specified string to this set if it is not already present. 
	 * More formally, adds the specified string s to this set if the set contains no element s2 such that s.equals(s2). 
	 * If this set already contains the element, the call leaves the set unchanged and returns false. 
	 * @param s The string that is being inserted in the set
	 * @return true if s was not already inserted, false otherwise
	 */
	public boolean add(String s, Transaction t) {
		System.out.println(s);
		if (s != "") {
			try {
				Node node = start.read(t);
				boolean result = node.add(s, 0, t);
				start.write(t, node);
				return result;
			} catch (AbortException e) {
				e.printStackTrace();
			}
		}
		boolean result = emptyAbsent;
		emptyAbsent = false;
		return result;
	}

	/**
	 * Get all words contained in the Dictionary
	 * @return All words
	 */
	public List<String> getWords() {
		Transaction t = new TransactionTL2<Node>();
		while (!t.isCommited()) {
			t.begin();

			try {
				List<String> result =  getAllNodeWords(start, t);
				t.try_to_commit();
				return result;
			} catch (AbortException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	private List<String> getAllNodeWords(Register<Node> node, Transaction t) throws AbortException {
		List<String> words = new ArrayList<>();
		words.add("");

		// Get full word
		while (node != null) {
			// System.out.println(words.get(0));
			// System.out.println(node.read(t).character);
			words.set(0, words.get(0) + node.read(t).suffix);

			if (!node.read(t).absent && node.read(t).suffix != null) {
				words.add(words.get(0));
			}

			// Add all nexts
			Register<Node> next = node.read(t).next;
			while (next != null) {
				words.addAll(getAllNodeWords(next, t));
				next = node.read(t).next;
			}

			node = node.read(t).suffix;
		}

		return words;
	}
}