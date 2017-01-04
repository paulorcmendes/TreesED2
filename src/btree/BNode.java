package btree;
import java.util.*;

public class BNode<T extends Comparable<T>> {//extende um tipo generico

		private T[] keys = null;//vetor de chaves do no
		private int keysSize = 0;//tamanho do vetor de chaves
		private BNode<T>[] children = null;//vetor de filhos
		private int childrenSize = 0;//tamanho do vetor de filhos
		protected Comparator<BNode<T>> comparator = new Comparator<BNode<T>>() {//comparador para tipos genericos
			@Override
			public int compare(BNode<T> arg0, BNode<T> arg1) {//metodo sobreescrito de comparação
				return arg0.getKey(0).compareTo(arg1.getKey(0));
			}
		};
		//getters e setters
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
		//fim dos getters e setters padrao
		protected BNode<T> parent = null;//referencia pro no pai

		@SuppressWarnings("unchecked")//suprime warnings por causa de cast para tipo generico
		protected BNode(BNode<T> parent, int maxKeySize, int maxChildrenSize) {//construtor do bnode
			this.parent = parent;
			this.keys = (T[]) new Comparable[maxKeySize + 1];
			this.keysSize = 0;
			this.children = new BNode[maxChildrenSize + 1];
			this.childrenSize = 0;
		}

		protected T getKey(int index) {//retorna a key em um index no vetor de chave
			return keys[index];
		}

		protected int indexOf(T value) {//retorna o index de tal key se ela estiver no no
			for (int i = 0; i < keysSize; i++) {
				if (keys[i].equals(value)) return i;
			}
			return -1;
		}

		protected void addKey(T value) {//adiciona uma key no vetor de keys do no
			keys[keysSize++] = value;
			Arrays.sort(keys, 0, keysSize);
		}

		
		protected T removeKey(T value) {//remove uma key do vetor de keys do no
			T removed = null;
			boolean found = false;
			if (keysSize == 0) return null;
			for (int i = 0; i < keysSize; i++) {
				if (keys[i].equals(value)) {
					found = true;
					removed = keys[i];
				} else if (found) {
					// move o resto delas pra baixo
					keys[i - 1] = keys[i];
				}
			}
			if (found) {
				keysSize--;
				keys[keysSize] = null;
			}
			return removed;
		}

		protected T removeKey(int index) {//remove uma key de acordo com o index dela
			if (index >= keysSize)
				return null;
			T value = keys[index];
			for (int i = index + 1; i < keysSize; i++) {
				keys[i - 1] = keys[i];
			}
			keysSize--;
			keys[keysSize] = null;
			return value;
		}

		protected int numberOfKeys() {//retorna o numero de chaves do no
			return keysSize;
		}
		
		int numberOfChildren() {//retorna o numero de filhos
			return childrenSize;
		}
		
		protected BNode<T> getChild(int index) {//retorna o filho naquele index
			if (index >= childrenSize)
				return null;
			return children[index];
		}

		protected int indexOf(BNode<T> child) {//o index daquele filho
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

		protected boolean removeChild(BNode<T> child) {//remove o filho solicitado
			boolean found = false;
			if (childrenSize == 0)
				return found;
			for (int i = 0; i < childrenSize; i++) {
				if (children[i].equals(child)) {
					found = true;
				} else if (found) {
					// muda o resto dos filhos uma chave abaixo
					children[i - 1] = children[i];
				}
			}
			if (found) {
				childrenSize--;
				children[childrenSize] = null;
			}
			return found;
		}

		protected BNode<T> removeChild(int index) {//remove o filho em tal index
			if (index >= childrenSize)
				return null;
			BNode<T> value = children[index];
			children[index] = null;
			for (int i = index + 1; i < childrenSize; i++) {
				children[i - 1] = children[i];
			}
			childrenSize--;
			children[childrenSize] = null;
			return value;
		}
	}
