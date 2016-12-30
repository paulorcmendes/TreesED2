package avlTree;

public class AVLNode<T extends Comparable<T>> {
	private AVLNode<T> left;
	private AVLNode<T> right;
	private T key;
	
	public AVLNode(T key){
		this.setKey(key);
		this.setLeft(null);
		this.setRight(null);
	}

	public AVLNode<T> getLeft() {
		return this.left;
	}

	public void setLeft(AVLNode<T> left) {
		this.left = left;
	}

	public AVLNode<T> getRight() {
		return this.right;
	}

	public void setRight(AVLNode<T> right) {
		this.right = right;
	}

	public T getKey() {
		return key;
	}

	public void setKey(T key) {
		this.key = key;
	}
	
}
