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
import java.util.concurrent.atomic.AtomicBoolean;

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
		final char character;
		// True if the string leading to this node has already been inserted, false otherwise
		AtomicBoolean absent;
		// Encodes the set of strings starting with the string leading to this word, 
		// including the character encoded by this node
		volatile Register<Node> suffix;
		// Encodes the set of strings starting with the string leading to this word, 
		// excluding the character encoded by this node, 
		// and whose next character is strictly greater than the character encoded by this node
		volatile Register<Node> next;

		Node(char character, Node next) {
			this.character = character;
			this.absent = new AtomicBoolean(true);
			// No null
			this.next = new RegisterTL2<>(next);
			this.suffix = new RegisterTL2<>(null);
		}

		/**
		 * Adds the specified string to this set if it is not already present. 
		 * More formally, adds the specified string s to this set if the set contains no element s2 such that s.equals(s2). 
		 * If this set already contains the element, the call leaves the set unchanged and returns false. 
		 * @param s The string that is being inserted in the set
		 * @param depth The number of time the pointer "suffix" has been followed
		 * @return true if s was not already inserted, false otherwise
		 */
		boolean add(String s, int depth, Transaction<Node> t) throws AbortException {
			//System.out.println(s.charAt(depth));

			// First case: we are at the end of the string and this is the correct node
			if(depth >= s.length() || (s.charAt(depth) == character) && depth == s.length() - 1) {
				boolean result = absent.get();
				absent.set(false);
				return result;
			}

			// Second case: the next character in the string was found, but this is not the end of the string
			// We continue in member "suffix"
			Node nodeSuffix = suffix.read(t);
			if(s.charAt(depth) == character) {
				if (nodeSuffix == null || nodeSuffix.character > s.charAt(depth+1)) {
					suffix.write(t, new Node(s.charAt(depth+1), nodeSuffix));
				}

				return suffix.read(t).add(s, depth+1, t);
			}

			// Third case: the next character in the string was not found
			// We continue in member "next"
			// To maintain the order, we may have to add a new node before "next" first
			Node nodeNext = next.read(t);
			if (nodeNext == null || nodeNext.character > s.charAt(depth)) {
				next.write(t, new Node(s.charAt(depth), nodeNext));
			}

			return next.read(t).add(s, depth, t);
		}

/*		public String prettyPrint(String prefix, Transaction<Node> t) throws AbortException {
			String futurePrefix = String.format("%s\t", prefix);
			return String.format("%schar : %s%n%sabsent : %s%n%ssuffix : %s%n%snext : %s",
					prefix, character, prefix, absent, prefix,
					suffix.read(t) != null ? String.format("%n%s",
							suffix.read(t).prettyPrint(futurePrefix, t)) : "null",
					prefix,
					next.read(t) != null ? String.format("%n%s",
							next.read(t).prettyPrint(futurePrefix, t)) : "null");
		}*/

		public void printNode(String s, Transaction<Node> t) throws AbortException {
			if (!this.absent.get()) {
				System.out.println(s + this.character + "|");
			} else
				System.out.println(s + this.character);
			if (this.suffix.read(t) != null) {
				this.suffix.read(t).printNode(s + " ", t);

			}
			if (this.next.read(t) != null) {
				this.next.read(t).printNode(s + "", t);
			}

		}

/*
		@Override
		public String toString() {
			Transaction t = new TransactionTL2<>();
			String result = null;
			while (!t.isCommited()) {
				try {
					t.begin();
					result =  prettyPrint("", t);
					t.try_to_commit();
				} catch (AbortException e) {
					e.printStackTrace();
				}
			}
			return result;
		}*/
	}

	// We start with a first node, to simplify the algorithm, that encodes the smallest non-empty string "\0".
	private final Node start = new Node('\0', null);
	// The empty string is stored separately
	private boolean emptyAbsent = true;

	/**
	 * Adds the specified string to this set if it is not already present. 
	 * More formally, adds the specified string s to this set if the set contains no element s2 such that s.equals(s2). 
	 * If this set already contains the element, the call leaves the set unchanged and returns false. 
	 * @param s The string that is being inserted in the set
	 * @return true if s was not already inserted, false otherwise
	 */
	public boolean add(String s) throws AbortException {
		boolean result = false;
		if (s != "") {
			TransactionTL2<Node> t = new TransactionTL2<>();
			while (!t.isCommited()) {
				try {
					t.begin();
					result = start.add(s, 0, t);
					t.try_to_commit();
				}
				catch (AbortException e) {
					e.printStackTrace();
				}
			}

			return result;
		}
		result = emptyAbsent;
		emptyAbsent = false;
		return result;
	}

	public void print()
	{
		TransactionTL2<Node> t = new TransactionTL2<>();
		while (!t.isCommited()) {
			try {
				t.begin();
				start.printNode("", t);
				t.try_to_commit();
			}
			catch (AbortException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Get all words contained in the Dictionary
	 * @return All words
	 */
/*	public List<String> getWords() {
		Transaction t = new TransactionTL2<Node>();
		while (!t.isCommited()) {


			try {
				t.begin();
				List<String> result =  getAllNodeWords(start, t);
				t.try_to_commit();
				System.out.println("get words return : " + result);
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
		System.out.println("call get all node words");

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

		System.out.println("get all node words return : " + words);
		return words;
	}*/
}