import java.util.NoSuchElementException;
import java.util.ArrayList;

/**
 *  The {@code ScapegoatTree} class represents an ordered symbol table of generic
 *  key-value pairs.
 *  It supports the usual <em>put</em>, <em>get</em>, <em>contains</em>,
 *  <em>size</em>, and <em>is-empty</em> methods. It does not support
 *  <em>delete</em>, however.
 *  It also provides a <em>keys</em> method for iterating over all of the keys.
 *  A symbol table implements the <em>associative array</em> abstraction:
 *  when associating a value with a key that is already in the symbol table,
 *  the convention is to replace the old value with the new value.
 *  <p>
 *  This implementation uses a scapegoat tree. It requires that
 *  the key type implements the {@code Comparable} interface and calls the
 *  {@code compareTo()} and method to compare two keys. It does not call either
 *  {@code equals()} or {@code hashCode()}.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 *  @author Nick Smallbone
 *  @author You!
 */
public class ScapegoatTree<Key extends Comparable<Key>, Value> {
    final double alpha = 2;        // how unbalanced the tree may become;
                                   // alpha must be greater than 1,
                                   // height is always <= alpha * lg size.

    private Node root;             // root of BST
    private class Node {
        private Key key;           // sorted by key
        private Value val;         // associated data
        private Node left, right;  // left and right subtrees
        private int size;          // number of nodes in subtree
        private int height;        // height of subtree

        public Node(Key key, Value val) {
            this.key = key;
            this.val = val;
            this.left = null;
            this.right = null;
            this.size = 1;
            this.height = 0;
        }
    }

    /**
     * Initializes an empty symbol table.
     */
    public ScapegoatTree() {
    }

    /**
     * Returns the number of key-value pairs in this symbol table.
     * @return the number of key-value pairs in this symbol table
     */
    public int size() {
        return size(root);
    }

    // return number of key-value pairs in BST rooted at x
    private int size(Node x) {
        if (x == null) return 0;
        else return x.size;
    }

    /**
     * Does this symbol table contain the given key?
     *
     * @param  key the key
     * @return {@code true} if this symbol table contains {@code key} and
     *         {@code false} otherwise
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public boolean contains(Key key) {
        if (key == null) throw new IllegalArgumentException("argument to contains() is null");
        return get(key) != null;
    }

    /**
     * Returns the value associated with the given key.
     *
     * @param  key the key
     * @return the value associated with the given key if the key is in the symbol table
     *         and {@code null} if the key is not in the symbol table
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public Value get(Key key) {
        return get(root, key);
    }

    private Value get(Node x, Key key) {
        if (key == null) throw new IllegalArgumentException("calls get() with a null key");
        if (x == null) return null;
        int cmp = key.compareTo(x.key);
        if      (cmp < 0) return get(x.left, key);
        else if (cmp > 0) return get(x.right, key);
        else              return x.val;
    }

    /**
     * Inserts the specified key-value pair into the symbol table, overwriting the old 
     * value with the new value if the symbol table already contains the specified key.
     * Deletes the specified key (and its associated value) from this symbol table
     * if the specified value is {@code null}.
     *
     * @param  key the key
     * @param  val the value
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public void put(Key key, Value val) {
        if (key == null) throw new IllegalArgumentException("calls put() with a null key");
        root = put(root, key, val);
        assert check();
    }

    private Node put(Node node, Key key, Value val) {
        if (node == null) return new Node(key, val);
        int cmp = key.compareTo(node.key);

        // TO DO: finish implementing put.
		// If you like you can start from the code for put in BST.java.
        // Read the lab instructions for more hints!
        if (cmp < 0) {
            // key is less than node.key
        } else if (cmp > 0) {
            // key is greater than node.key
        } else {
            // key is equal to node.key
        }

        throw new UnsupportedOperationException();
    }

	// Rebuild a tree to make it perfectly balanced.
	// You do not need to change this method, but need to define 'inorder'.
    private Node rebuild(Node node) {
        ArrayList<Node> nodes = new ArrayList<>();
        inorder(node, nodes);
        return makeBalancedBST(nodes, 0, nodes.size()-1);
    }

	// Perform an inorder traversal of the subtree rooted at 'node', storing
	// its nodes into the ArrayList 'nodes'.
    private void inorder(Node node, ArrayList<Node> nodes) {
        // TO DO: use in-order traversal to store 'node' and all
        // descendants into 'nodes' ArrayList
        throw new UnsupportedOperationException();
    }

	// Convert an array of nodes into a balanced BST.
	// You do not need to change this method.
    private Node makeBalancedBST(ArrayList<Node> nodes, int lo, int hi) {
        if (lo > hi) return null;
        int mid = (lo+hi)/2;
        Node result = new Node(nodes.get(mid).key, nodes.get(mid).val);
        result.left = makeBalancedBST(nodes, lo, mid-1);
        result.right = makeBalancedBST(nodes, mid+1, hi);
        result.size = 1 + size(result.left) + size(result.right);
        result.height = 1 + Math.max(height(result.left), height(result.right));
        return result;
    }

    // Returns log base 2 of a number.
    private int log2(int n) {
        if (n <= 0) return 0;
        return 32 - Integer.numberOfLeadingZeros(n-1);
    }

    /**
     * Returns the smallest key in the symbol table.
     *
     * @return the smallest key in the symbol table
     * @throws NoSuchElementException if the symbol table is empty
     */
    public Key min() {
        if (size() == 0) throw new NoSuchElementException("calls min() with empty symbol table");
        return min(root).key;
    } 

    private Node min(Node x) { 
        if (x.left == null) return x; 
        else                return min(x.left); 
    } 

    /**
     * Returns the largest key in the symbol table.
     *
     * @return the largest key in the symbol table
     * @throws NoSuchElementException if the symbol table is empty
     */
    public Key max() {
        if (size() == 0) throw new NoSuchElementException("calls max() with empty symbol table");
        return max(root).key;
    } 

    private Node max(Node x) {
        if (x.right == null) return x; 
        else                 return max(x.right); 
    } 

    /**
     * Returns all keys in the symbol table as an {@code Iterable}.
     * To iterate over all of the keys in the symbol table named {@code st},
     * use the foreach notation: {@code for (Key key : st.keys())}.
     *
     * @return all keys in the symbol table
     */
    public Iterable<Key> keys() {
        if (size() == 0) return new ArrayList<Key>();
        return keys(min(), max());
    }

    /**
     * Returns all keys in the symbol table in the given range,
     * as an {@code Iterable}.
     *
     * @param  lo minimum endpoint
     * @param  hi maximum endpoint
     * @return all keys in the symbol table between {@code lo} 
     *         (inclusive) and {@code hi} (inclusive)
     * @throws IllegalArgumentException if either {@code lo} or {@code hi}
     *         is {@code null}
     */
    public Iterable<Key> keys(Key lo, Key hi) {
        if (lo == null) throw new IllegalArgumentException("first argument to keys() is null");
        if (hi == null) throw new IllegalArgumentException("second argument to keys() is null");

        ArrayList<Key> queue = new ArrayList<Key>();
        keys(root, queue, lo, hi);
        return queue;
    } 

    private void keys(Node x, ArrayList<Key> queue, Key lo, Key hi) { 
        if (x == null) return; 
        int cmplo = lo.compareTo(x.key); 
        int cmphi = hi.compareTo(x.key); 
        if (cmplo < 0) keys(x.left, queue, lo, hi); 
        if (cmplo <= 0 && cmphi >= 0) queue.add(x.key); 
        if (cmphi > 0) keys(x.right, queue, lo, hi); 
    } 

    /**
     * Returns the height of the BST.
     *
     * @return the height of the BST (a 1-node tree has height 0)
     */
    public int height() {
        return height(root);
    }
    private int height(Node x) {
        if (x == null) return -1;
        return x.height;
    }

  /*************************************************************************
    *  Check integrity of BST data structure.
    ***************************************************************************/
    private boolean check() {
        if (!isBST())              System.out.println("Not in symmetric order");
        if (!isSizeConsistent())   System.out.println("Subtree counts not consistent");
        if (!isHeightConsistent()) System.out.println("Heights not consistent");
        if (!isBalanced())         System.out.println("Not balanced");
        return isBST() && isSizeConsistent() && isHeightConsistent() && isBalanced();
    }

    // does this binary tree satisfy symmetric order?
    // Note: this test also ensures that data structure is a binary tree since order is strict
    private boolean isBST() {
        return isBST(root, null, null);
    }

    // is the tree rooted at x a BST with all keys strictly between min and max
    // (if min or max is null, treat as empty constraint)
    // Credit: Bob Dondero's elegant solution
    private boolean isBST(Node x, Key min, Key max) {
        if (x == null) return true;
        if (min != null && x.key.compareTo(min) <= 0) return false;
        if (max != null && x.key.compareTo(max) >= 0) return false;
        return isBST(x.left, min, x.key) && isBST(x.right, x.key, max);
    } 

    // are the size fields correct?
    private boolean isSizeConsistent() { return isSizeConsistent(root); }
    private boolean isSizeConsistent(Node x) {
        if (x == null) return true;
        if (x.size != size(x.left) + size(x.right) + 1) return false;
        return isSizeConsistent(x.left) && isSizeConsistent(x.right);
    } 

    // are the height fields correct?
    private boolean isHeightConsistent() { return isHeightConsistent(root); }
    private boolean isHeightConsistent(Node x) {
        if (x == null) return true;
        if (x.height != 1 + Math.max(height(x.left), height(x.right))) return false;
        return isHeightConsistent(x.left) && isHeightConsistent(x.right);
    } 

    // is the tree sufficiently balanced?
    private boolean isBalanced() { return isBalanced(root); }
    private boolean isBalanced(Node x) {
        if (x == null) return true;
        if (x.height > alpha * log2(x.size)) return false;
        return isBalanced(x.left) && isBalanced(x.right);
    } 
}

/******************************************************************************
 *  Copyright 2002-2019, Robert Sedgewick and Kevin Wayne.
 *
 *  This file is part of algs4.jar, which accompanies the textbook
 *
 *      Algorithms, 4th edition by Robert Sedgewick and Kevin Wayne,
 *      Addison-Wesley Professional, 2011, ISBN 0-321-57351-X.
 *      http://algs4.cs.princeton.edu
 *
 *
 *  algs4.jar is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  algs4.jar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with algs4.jar.  If not, see http://www.gnu.org/licenses.
 ******************************************************************************/
