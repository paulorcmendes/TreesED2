package btree;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;

import btree.BNode;

/**
 * B-tree is a tree data structure that keeps data sorted and allows searches,
 * sequential access, insertions, and deletions in logarithmic time. The B-tree
 * is a generalization of a binary search tree in that a BNode can have more than
 * two children. Unlike self-balancing binary search trees, the B-tree is
 * optimized for systems that read and write large blocks of data. It is
 * commonly used in databases and file-systems.
 * 
 * http://en.wikipedia.org/wiki/B-tree
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
@SuppressWarnings("unchecked")
public class BTree<T extends Comparable<T>>{

	// Default to 2-3 Tree
	private int minKeySize = 1;
	private int minChildrenSize = minKeySize + 1; // 2
	private int maxKeySize = 2 * minKeySize; // 2
	private int maxChildrenSize = maxKeySize + 1; // 3

	private BNode<T> root = null;
	private int size = 0;

	/**
	 * Constructor for B-Tree which defaults to a 2-3 B-Tree.
	 */
	public BTree() { }

	/**
	 * Constructor for B-Tree of ordered parameter. Order here means minimum 
	 * number of keys in a non-root BNode. 
	 * 
	 * @param order
	 *            of the B-Tree.
	 */
	public BTree(int order) {
		this.minKeySize = order;
		this.minChildrenSize = minKeySize + 1;
		this.maxKeySize = 2 * minKeySize;
		this.maxChildrenSize = maxKeySize + 1;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean insert(T value) {
		if (root == null) {
			root = new BNode<T>(null, maxKeySize, maxChildrenSize);
			root.addKey(value);
		} else {
			BNode<T> BNode = root;
			while (BNode != null) {
				if (BNode.numberOfChildren() == 0) {
					BNode.addKey(value);
					if (BNode.numberOfKeys() <= maxKeySize) {
						// A-OK
						break;
					}                         
					// Need to split up
					split(BNode);
					break;
				}
				// Navigate

				// Lesser or equal
				T lesser = BNode.getKey(0);
				if (value.compareTo(lesser) <= 0) {
					BNode = BNode.getChild(0);
					continue;
				}

				// Greater
				int numberOfKeys = BNode.numberOfKeys();
				int last = numberOfKeys - 1;
				T greater = BNode.getKey(last);
				if (value.compareTo(greater) > 0) {
					BNode = BNode.getChild(numberOfKeys);
					continue;
				}

				// Search internal BNodes
				for (int i = 1; i < BNode.numberOfKeys(); i++) {
					T prev = BNode.getKey(i - 1);
					T next = BNode.getKey(i);
					if (value.compareTo(prev) > 0 && value.compareTo(next) <= 0) {
						BNode = BNode.getChild(i);
						break;
					}
				}
			}
		}

		size++;

		return true;
	}

	/**
	 * The BNode's key size is greater than maxKeySize, split down the middle.
	 * 
	 * @param BNode
	 *            to split.
	 */
	private void split(BNode<T> BNodeToSplit) {
		BNode<T> BNode = BNodeToSplit;
		int numberOfKeys = BNode.numberOfKeys();
		int medianIndex = numberOfKeys / 2;
		T medianValue = BNode.getKey(medianIndex);

		BNode<T> left = new BNode<T>(null, maxKeySize, maxChildrenSize);
		for (int i = 0; i < medianIndex; i++) {
			left.addKey(BNode.getKey(i));
		}
		if (BNode.numberOfChildren() > 0) {
			for (int j = 0; j <= medianIndex; j++) {
				BNode<T> c = BNode.getChild(j);
				left.addChild(c);
			}
		}

		BNode<T> right = new BNode<T>(null, maxKeySize, maxChildrenSize);
		for (int i = medianIndex + 1; i < numberOfKeys; i++) {
			right.addKey(BNode.getKey(i));
		}
		if (BNode.numberOfChildren() > 0) {
			for (int j = medianIndex + 1; j < BNode.numberOfChildren(); j++) {
				BNode<T> c = BNode.getChild(j);
				right.addChild(c);
			}
		}

		if (BNode.parent == null) {
			// new root, height of tree is increased
			BNode<T> newRoot = new BNode<T>(null, maxKeySize, maxChildrenSize);
			newRoot.addKey(medianValue);
			BNode.parent = newRoot;
			root = newRoot;
			BNode = root;
			BNode.addChild(left);
			BNode.addChild(right);
		} else {
			// Move the median value up to the parent
			BNode<T> parent = BNode.parent;
			parent.addKey(medianValue);
			parent.removeChild(BNode);
			parent.addChild(left);
			parent.addChild(right);

			if (parent.numberOfKeys() > maxKeySize) split(parent);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public T remove(T value) {
		T removed = null;
		BNode<T> BNode = this.getBNode(value);
		removed = remove(value,BNode);
		return removed;
	}

	/**
	 * Remove the value from the BNode and check invariants
	 * 
	 * @param value
	 *            T to remove from the tree
	 * @param BNode
	 *            BNode to remove value from
	 * @return True if value was removed from the tree.
	 */
	private T remove(T value, BNode<T> BNode) {
		if (BNode == null) return null;

		T removed = null;
		int index = BNode.indexOf(value);
		removed = BNode.removeKey(value);
		if (BNode.numberOfChildren() == 0) {
			// leaf BNode
			if (BNode.parent != null && BNode.numberOfKeys() < minKeySize) {
				this.combined(BNode);
			} else if (BNode.parent == null && BNode.numberOfKeys() == 0) {
				// Removing root BNode with no keys or children
				root = null;
			}
		} else {
			// internal BNode
			BNode<T> lesser = BNode.getChild(index);
			BNode<T> greatest = this.getGreatestBNode(lesser);
			T replaceValue = this.removeGreatestValue(greatest);
			BNode.addKey(replaceValue);
			if (greatest.parent != null && greatest.numberOfKeys() < minKeySize) {
				this.combined(greatest);
			}
			if (greatest.numberOfChildren() > maxChildrenSize) {
				this.split(greatest);
			}
		}

		size--;

		return removed;
	}

	/**
	 * Remove greatest valued key from BNode.
	 * 
	 * @param BNode
	 *            to remove greatest value from.
	 * @return value removed;
	 */
	private T removeGreatestValue(BNode<T> BNode) {
		T value = null;
		if (BNode.numberOfKeys() > 0) {
			value = BNode.removeKey(BNode.numberOfKeys() - 1);
		}
		return value;
	}


	public void clear() {
		root = null;
		size = 0;
	}

	public boolean contains(T value) {
		BNode<T> BNode = getBNode(value);
		return (BNode != null);
	}
	
	public BNode<T> search(T value) {
		return getBNode(value);
	}
	
	/**
	 * Get the BNode with value.
	 * 
	 * @param value
	 *            to find in the tree.
	 * @return BNode<T> with value.
	 */
	private BNode<T> getBNode(T value) {
		BNode<T> BNode = root;
		while (BNode != null) {
			T lesser = BNode.getKey(0);
			if (value.compareTo(lesser) < 0) {
				if (BNode.numberOfChildren() > 0)
					BNode = BNode.getChild(0);
				else
					BNode = null;
				continue;
			}

			int numberOfKeys = BNode.numberOfKeys();
			int last = numberOfKeys - 1;
			T greater = BNode.getKey(last);
			if (value.compareTo(greater) > 0) {
				if (BNode.numberOfChildren() > numberOfKeys)
					BNode = BNode.getChild(numberOfKeys);
				else
					BNode = null;
				continue;
			}

			for (int i = 0; i < numberOfKeys; i++) {
				T currentValue = BNode.getKey(i);
				if (currentValue.compareTo(value) == 0) {
					return BNode;
				}

				int next = i + 1;
				if (next <= last) {
					T nextValue = BNode.getKey(next);
					if (currentValue.compareTo(value) < 0 && nextValue.compareTo(value) > 0) {
						if (next < BNode.numberOfChildren()) {
							BNode = BNode.getChild(next);
							break;
						}
						return null;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Get the greatest valued child from BNode.
	 * 
	 * @param BNodeToGet
	 *            child with the greatest value.
	 * @return BNode<T> child with greatest value.
	 */
	private BNode<T> getGreatestBNode(BNode<T> BNodeToGet) {
		BNode<T> BNode = BNodeToGet;
		while (BNode.numberOfChildren() > 0) {
			BNode = BNode.getChild(BNode.numberOfChildren() - 1);
		}
		return BNode;
	}

	/**
	 * Combined children keys with parent when size is less than minKeySize.
	 * 
	 * @param BNode
	 *            with children to combined.
	 * @return True if combined successfully.
	 */
	private boolean combined(BNode<T> BNode) {
		BNode<T> parent = BNode.parent;
		int index = parent.indexOf(BNode);
		int indexOfLeftNeighbor = index - 1;
		int indexOfRightNeighbor = index + 1;

		BNode<T> rightNeighbor = null;
		int rightNeighborSize = -minChildrenSize;
		if (indexOfRightNeighbor < parent.numberOfChildren()) {
			rightNeighbor = parent.getChild(indexOfRightNeighbor);
			rightNeighborSize = rightNeighbor.numberOfKeys();
		}

		// Try to borrow neighbor
		if (rightNeighbor != null && rightNeighborSize > minKeySize) {
			// Try to borrow from right neighbor
			T removeValue = rightNeighbor.getKey(0);
			int prev = getIndexOfPreviousValue(parent, removeValue);
			T parentValue = parent.removeKey(prev);
			T neighborValue = rightNeighbor.removeKey(0);
			BNode.addKey(parentValue);
			parent.addKey(neighborValue);
			if (rightNeighbor.numberOfChildren() > 0) {
				BNode.addChild(rightNeighbor.removeChild(0));
			}
		} else {
			BNode<T> leftNeighbor = null;
			int leftNeighborSize = -minChildrenSize;
			if (indexOfLeftNeighbor >= 0) {
				leftNeighbor = parent.getChild(indexOfLeftNeighbor);
				leftNeighborSize = leftNeighbor.numberOfKeys();
			}

			if (leftNeighbor != null && leftNeighborSize > minKeySize) {
				// Try to borrow from left neighbor
				T removeValue = leftNeighbor.getKey(leftNeighbor.numberOfKeys() - 1);
				int prev = getIndexOfNextValue(parent, removeValue);
				T parentValue = parent.removeKey(prev);
				T neighborValue = leftNeighbor.removeKey(leftNeighbor.numberOfKeys() - 1);
				BNode.addKey(parentValue);
				parent.addKey(neighborValue);
				if (leftNeighbor.numberOfChildren() > 0) {
					BNode.addChild(leftNeighbor.removeChild(leftNeighbor.numberOfChildren() - 1));
				}
			} else if (rightNeighbor != null && parent.numberOfKeys() > 0) {
				// Can't borrow from neighbors, try to combined with right neighbor
				T removeValue = rightNeighbor.getKey(0);
				int prev = getIndexOfPreviousValue(parent, removeValue);
				T parentValue = parent.removeKey(prev);
				parent.removeChild(rightNeighbor);
				BNode.addKey(parentValue);
				for (int i = 0; i < rightNeighbor.getKeysSize(); i++) {
					T v = rightNeighbor.getKey(i);
					BNode.addKey(v);
				}
				for (int i = 0; i < rightNeighbor.getChildrenSize(); i++) {
					BNode<T> c = rightNeighbor.getChild(i);
					BNode.addChild(c);
				}

				if (parent.parent != null && parent.numberOfKeys() < minKeySize) {
					// removing key made parent too small, combined up tree
					this.combined(parent);
				} else if (parent.numberOfKeys() == 0) {
					// parent no longer has keys, make this BNode the new root
					// which decreases the height of the tree
					BNode.parent = null;
					root = BNode;
				}
			} else if (leftNeighbor != null && parent.numberOfKeys() > 0) {
				// Can't borrow from neighbors, try to combined with left neighbor
				T removeValue = leftNeighbor.getKey(leftNeighbor.numberOfKeys() - 1);
				int prev = getIndexOfNextValue(parent, removeValue);
				T parentValue = parent.removeKey(prev);
				parent.removeChild(leftNeighbor);
				BNode.addKey(parentValue);
				for (int i = 0; i < leftNeighbor.getKeysSize(); i++) {
					T v = leftNeighbor.getKey(i);
					BNode.addKey(v);
				}
				for (int i = 0; i < leftNeighbor.getChildrenSize(); i++) {
					BNode<T> c = leftNeighbor.getChild(i);
					BNode.addChild(c);
				}

				if (parent.parent != null && parent.numberOfKeys() < minKeySize) {
					// removing key made parent too small, combined up tree
					this.combined(parent);
				} else if (parent.numberOfKeys() == 0) {
					// parent no longer has keys, make this BNode the new root
					// which decreases the height of the tree
					BNode.parent = null;
					root = BNode;
				}
			}
		}

		return true;
	}

	/**
	 * Get the index of previous key in BNode.
	 * 
	 * @param BNode
	 *            to find the previous key in.
	 * @param value
	 *            to find a previous value for.
	 * @return index of previous key or -1 if not found.
	 */
	private int getIndexOfPreviousValue(BNode<T> BNode, T value) {
		for (int i = 1; i < BNode.numberOfKeys(); i++) {
			T t = BNode.getKey(i);
			if (t.compareTo(value) >= 0)
				return i - 1;
		}
		return BNode.numberOfKeys() - 1;
	}

	/**
	 * Get the index of next key in BNode.
	 * 
	 * @param BNode
	 *            to find the next key in.
	 * @param value
	 *            to find a next value for.
	 * @return index of next key or -1 if not found.
	 */
	private int getIndexOfNextValue(BNode<T> BNode, T value) {
		for (int i = 0; i < BNode.numberOfKeys(); i++) {
			T t = BNode.getKey(i);
			if (t.compareTo(value) >= 0)
				return i;
		}
		return BNode.numberOfKeys() - 1;
	}


	public int size() {
		return size;
	}

	public boolean validate() {
		if (root == null) return true;
		return validateBNode(root);
	}

	/**
	 * Validate the BNode according to the B-Tree invariants.
	 * 
	 * @param BNode
	 *            to validate.
	 * @return True if valid.
	 */
	private boolean validateBNode(BNode<T> BNode) {
		int keySize = BNode.numberOfKeys();
		if (keySize > 1) {
			// Make sure the keys are sorted
			for (int i = 1; i < keySize; i++) {
				T p = BNode.getKey(i - 1);
				T n = BNode.getKey(i);
				if (p.compareTo(n) > 0)
					return false;
			}
		}
		int childrenSize = BNode.numberOfChildren();
		if (BNode.parent == null) {
			// root
			if (keySize > maxKeySize) {
				// check max key size. root does not have a min key size
				return false;
			} else if (childrenSize == 0) {
				// if root, no children, and keys are valid
				return true;
			} else if (childrenSize < 2) {
				// root should have zero or at least two children
				return false;
			} else if (childrenSize > maxChildrenSize) {
				return false;
			}
		} else {
			// non-root
			if (keySize < minKeySize) {
				return false;
			} else if (keySize > maxKeySize) {
				return false;
			} else if (childrenSize == 0) {
				return true;
			} else if (keySize != (childrenSize - 1)) {
				// If there are chilren, there should be one more child then
				// keys
				return false;
			} else if (childrenSize < minChildrenSize) {
				return false;
			} else if (childrenSize > maxChildrenSize) {
				return false;
			}
		}

		BNode<T> first = BNode.getChild(0);
		// The first child's last key should be less than the BNode's first key
		if (first.getKey(first.numberOfKeys() - 1).compareTo(BNode.getKey(0)) > 0)
			return false;

		BNode<T> last = BNode.getChild(BNode.numberOfChildren() - 1);
		// The last child's first key should be greater than the BNode's last key
		if (last.getKey(0).compareTo(BNode.getKey(BNode.numberOfKeys() - 1)) < 0)
			return false;

		// Check that each BNode's first and last key holds it's invariance
		for (int i = 1; i < BNode.numberOfKeys(); i++) {
			T p = BNode.getKey(i - 1);
			T n = BNode.getKey(i);
			BNode<T> c = BNode.getChild(i);
			if (p.compareTo(c.getKey(0)) > 0)
				return false;
			if (n.compareTo(c.getKey(c.numberOfKeys() - 1)) < 0)
				return false;
		}

		for (int i = 0; i < BNode.getChildrenSize(); i++) {
			BNode<T> c = BNode.getChild(i);
			boolean valid = this.validateBNode(c);
			if (!valid)
				return false;
		}

		return true;
	}


	public java.util.Collection<T> toCollection() {
		return (new JavaCompatibleBTree<T>(this));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return TreePrinter.getString(this);
	}
	/*
	private static class BNode<T extends Comparable<T>> {

		private T[] keys = null;
		private int keysSize = 0;
		private BNode<T>[] children = null;
		private int childrenSize = 0;
		private Comparator<BNode<T>> comparator = new Comparator<BNode<T>>() {
			@Override
			public int compare(BNode<T> arg0, BNode<T> arg1) {
				return arg0.getKey(0).compareTo(arg1.getKey(0));
			}
		};

		protected BNode<T> parent = null;

		private BNode(BNode<T> parent, int maxKeySize, int maxChildrenSize) {
			this.parent = parent;
			this.keys = (T[]) new Comparable[maxKeySize + 1];
			this.keysSize = 0;
			this.children = new BNode[maxChildrenSize + 1];
			this.childrenSize = 0;
		}

		private T getKey(int index) {
			return keys[index];
		}

		private int indexOf(T value) {
			for (int i = 0; i < keysSize; i++) {
				if (keys[i].equals(value)) return i;
			}
			return -1;
		}

		private void addKey(T value) {
			keys[keysSize++] = value;
			Arrays.sort(keys, 0, keysSize);
		}

		private T removeKey(T value) {
			T removed = null;
			boolean found = false;
			if (keysSize == 0) return null;
			for (int i = 0; i < keysSize; i++) {
				if (keys[i].equals(value)) {
					found = true;
					removed = keys[i];
				} else if (found) {
					// shift the rest of the keys down
					keys[i - 1] = keys[i];
				}
			}
			if (found) {
				keysSize--;
				keys[keysSize] = null;
			}
			return removed;
		}

		private T removeKey(int index) {
			if (index >= keysSize)
				return null;
			T value = keys[index];
			for (int i = index + 1; i < keysSize; i++) {
				// shift the rest of the keys down
				keys[i - 1] = keys[i];
			}
			keysSize--;
			keys[keysSize] = null;
			return value;
		}

		private int numberOfKeys() {
			return keysSize;
		}

		private BNode<T> getChild(int index) {
			if (index >= childrenSize)
				return null;
			return children[index];
		}

		private int indexOf(BNode<T> child) {
			for (int i = 0; i < childrenSize; i++) {
				if (children[i].equals(child))
					return i;
			}
			return -1;
		}

		private boolean addChild(BNode<T> child) {
			child.parent = this;
			children[childrenSize++] = child;
			Arrays.sort(children, 0, childrenSize, comparator);
			return true;
		}

		private boolean removeChild(BNode<T> child) {
			boolean found = false;
			if (childrenSize == 0)
				return found;
			for (int i = 0; i < childrenSize; i++) {
				if (children[i].equals(child)) {
					found = true;
				} else if (found) {
					// shift the rest of the keys down
					children[i - 1] = children[i];
				}
			}
			if (found) {
				childrenSize--;
				children[childrenSize] = null;
			}
			return found;
		}

		private BNode<T> removeChild(int index) {
			if (index >= childrenSize)
				return null;
			BNode<T> value = children[index];
			children[index] = null;
			for (int i = index + 1; i < childrenSize; i++) {
				// shift the rest of the keys down
				children[i - 1] = children[i];
			}
			childrenSize--;
			children[childrenSize] = null;
			return value;
		}

		private int numberOfChildren() {
			return childrenSize;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();

			builder.append("keys=[");
			for (int i = 0; i < numberOfKeys(); i++) {
				T value = getKey(i);
				builder.append(value);
				if (i < numberOfKeys() - 1)
					builder.append(", ");
			}
			builder.append("]\n");

			if (parent != null) {
				builder.append("parent=[");
				for (int i = 0; i < parent.numberOfKeys(); i++) {
					T value = parent.getKey(i);
					builder.append(value);
					if (i < parent.numberOfKeys() - 1)
						builder.append(", ");
				}
				builder.append("]\n");
			}

			if (children != null) {
				builder.append("keySize=").append(numberOfKeys()).append(" children=").append(numberOfChildren()).append("\n");
			}

			return builder.toString();
		}
	}
	*/
	private static class TreePrinter {

		public static <T extends Comparable<T>> String getString(BTree<T> tree) {
			if (tree.root == null) return "Tree has no Nodes.";
			return getString(tree.root, "", true);
		}

		private static <T extends Comparable<T>> String getString(BNode<T> BNode, String prefix, boolean isTail) {
			StringBuilder builder = new StringBuilder();

			builder.append(prefix).append((isTail ? "└── " : "├── "));
			for (int i = 0; i < BNode.numberOfKeys(); i++) {
				T value = BNode.getKey(i);
				builder.append(value);
				if (i < BNode.numberOfKeys() - 1)
					builder.append(", ");
			}
			builder.append("\n");

			if (BNode.getChildren() != null) {
				for (int i = 0; i < BNode.numberOfChildren() - 1; i++) {
					BNode<T> obj = BNode.getChild(i);
					builder.append(getString(obj, prefix + (isTail ? "    " : "│   "), false));
				}
				if (BNode.numberOfChildren() >= 1) {
					BNode<T> obj = BNode.getChild(BNode.numberOfChildren() - 1);
					builder.append(getString(obj, prefix + (isTail ? "    " : "│   "), true));
				}
			}

			return builder.toString();
		}
	}

	public static class JavaCompatibleBTree<T extends Comparable<T>> extends java.util.AbstractCollection<T> {

		private BTree<T> tree = null;

		public JavaCompatibleBTree(BTree<T> tree) {
			this.tree = tree;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean add(T value) {
			return tree.insert(value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean remove(Object value) {
			return (tree.remove((T)value)!=null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean contains(Object value) {
			return tree.contains((T)value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return tree.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public java.util.Iterator<T> iterator() {
			return (new BTreeIterator<T>(this.tree));
		}

		private static class BTreeIterator<C extends Comparable<C>> implements java.util.Iterator<C> {

			private BTree<C> tree = null;
			private BNode<C> lastBNode = null;
			private C lastValue = null;
			private int index = 0;
			private Deque<BNode<C>> toVisit = new ArrayDeque<BNode<C>>();

			protected BTreeIterator(BTree<C> tree) {
				this.tree = tree;
				if (tree.root!=null && tree.root.getKeysSize()>0) {
					toVisit.add(tree.root);
				}
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean hasNext() {
				if ((lastBNode!=null && index<lastBNode.getKeysSize())||(toVisit.size()>0)) return true; 
				return false;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public C next() {
				if (lastBNode!=null && (index < lastBNode.getKeysSize())) {
					lastValue = lastBNode.getKey(index++);
					return lastValue;
				}
				while (toVisit.size()>0) {
					// Go thru the current BNodes
					BNode<C> n = toVisit.pop();

					// Add non-null children
					for (int i=0; i<n.getChildrenSize(); i++) {
						toVisit.add(n.getChild(i));
					}

					// Update last BNode (used in remove method)
					index = 0;
					lastBNode = n;
					lastValue = lastBNode.getKey(index++);
					return lastValue;
				}
				return null;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void remove() {
				if (lastBNode!=null && lastValue!=null) {
					// On remove, reset the iterator (very inefficient, I know)
					tree.remove(lastValue,lastBNode);

					lastBNode = null;
					lastValue = null;
					index = 0;
					toVisit.clear();
					if (tree.root!=null && tree.root.getKeysSize()>0) {
						toVisit.add(tree.root);
					}
				}
			}
		}
	}
}