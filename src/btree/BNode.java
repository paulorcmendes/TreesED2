package btree;
import java.util.*;
@SuppressWarnings("unused")
public class BNode<T extends Comparable<T>> {

		private T[] keys = null;
		private int keysSize = 0;
		private BNode<T>[] children = null;
		private int childrenSize = 0;
		protected Comparator<BNode<T>> comparator = new Comparator<BNode<T>>() {
			@Override
			public int compare(BNode<T> arg0, BNode<T> arg1) {
				return arg0.getKey(0).compareTo(arg1.getKey(0));
			}
		};

		protected T[] getKeys() {
			return keys;
		}

		protected void setKeys(T[] keys) {
			this.keys = keys;
		}

		protected int getKeysSize() {
			return keysSize;
		}

		protected void setKeysSize(int keysSize) {
			this.keysSize = keysSize;
		}

		protected BNode<T>[] getChildren() {
			return children;
		}

		protected void setChildren(BNode<T>[] children) {
			this.children = children;
		}

		protected int getChildrenSize() {
			return childrenSize;
		}

		protected void setChildrenSize(int childrenSize) {
			this.childrenSize = childrenSize;
		}

		protected Comparator<BNode<T>> getComparator() {
			return comparator;
		}

		protected void setComparator(Comparator<BNode<T>> comparator) {
			this.comparator = comparator;
		}

		protected BNode<T> getParent() {
			return parent;
		}

		protected void setParent(BNode<T> parent) {
			this.parent = parent;
		}

		protected BNode<T> parent = null;

		@SuppressWarnings("unchecked")
		protected BNode(BNode<T> parent, int maxKeySize, int maxChildrenSize) {
			this.parent = parent;
			this.keys = (T[]) new Comparable[maxKeySize + 1];
			this.keysSize = 0;
			this.children = new BNode[maxChildrenSize + 1];
			this.childrenSize = 0;
		}

		protected T getKey(int index) {
			return keys[index];
		}

		protected int indexOf(T value) {
			for (int i = 0; i < keysSize; i++) {
				if (keys[i].equals(value)) return i;
			}
			return -1;
		}

		protected void addKey(T value) {
			keys[keysSize++] = value;
			Arrays.sort(keys, 0, keysSize);
		}

		
		protected T removeKey(T value) {
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

		protected T removeKey(int index) {
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

		protected int numberOfKeys() {
			return keysSize;
		}

		protected BNode<T> getChild(int index) {
			if (index >= childrenSize)
				return null;
			return children[index];
		}

		protected int indexOf(BNode<T> child) {
			for (int i = 0; i < childrenSize; i++) {
				if (children[i].equals(child))
					return i;
			}
			return -1;
		}

		protected boolean addChild(BNode<T> child) {
			child.parent = this;
			children[childrenSize++] = child;
			Arrays.sort(children, 0, childrenSize, comparator);
			return true;
		}

		protected boolean removeChild(BNode<T> child) {
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

		protected BNode<T> removeChild(int index) {
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

		int numberOfChildren() {
			return childrenSize;
		}

		/**
		 * {@inheritDoc}
		 */
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
