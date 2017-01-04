package avlTree;

import java.util.ArrayList;

public class AVLTree<T extends Comparable<T>> {
	private AVLNode<T> root;
	
	public AVLTree(){
		this.setRoot(null);
	}
	public void insert(T key){
		this.setRoot(this.insert(key, root));		
	}
	private AVLNode<T> insert(T key, AVLNode<T> node){		
		if(node != null){
			if(key.compareTo(node.getKey())<0){
				node.setLeft(insert(key, node.getLeft()));
			}else{
				node.setRight(insert(key, node.getRight()));
			}
			node = adjustTree(node);
			return node;
		}
		AVLNode<T> newNode = new AVLNode<T>(key);
		return newNode;
	}
	public void remove(T key){
		root = remove(key, root);
	}
	private AVLNode<T> remove(T key, AVLNode<T> node){
		if(key.compareTo(node.getKey())<0){
			node.setLeft(remove(key, node.getLeft()));
			
		}else if(key.compareTo(node.getKey())>0){
			node.setRight(remove(key, node.getRight()));
			
		}else{
			if(node.getLeft() == null){
				node = node.getRight();
			}else if(node.getRight() == null){
				node = node.getLeft();
			}else{
				swap(node, minNode(node.getRight()));
				node.setRight(remove(key, node.getRight()));
			}
		}		
		if(node != null) node = adjustTree(node);
		return node;
	}
	private AVLNode<T> adjustTree(AVLNode<T> node){
		if(balancingFactor(node) > 1){
			if(balancingFactor(node.getRight()) < 0){
				node = doubleLeftRotation(node);
			}else{
				node = leftRotation(node);
			}
		}else if(balancingFactor(node)<-1){
			if(balancingFactor(node.getLeft()) > 0){
				node = doubleRightRotation(node);
			}else{
				node = rightRotation(node);
			}
		}
		return node;
	}
	
	private AVLNode<T> leftRotation(AVLNode<T> x){
		AVLNode<T> y = x.getRight();
		x.setRight(y.getLeft());
		y.setLeft(x);		
		return y;
	}
	private AVLNode<T> doubleLeftRotation(AVLNode<T> x){
		x.setRight(rightRotation(x.getRight()));
		x = leftRotation(x);
		return x;
	}
	
	private AVLNode<T> rightRotation(AVLNode<T> x){
		AVLNode<T> y = x.getLeft();
		x.setLeft(y.getRight());
		y.setRight(x);		
		return y;
	}
	private AVLNode<T> doubleRightRotation(AVLNode<T> x){
		x.setLeft(leftRotation(x.getLeft()));
		x = rightRotation(x);
		return x;
	}
	private void swap(AVLNode<T> node1, AVLNode<T> node2){
		T aux;
		if(node1 == null || node2 == null) return;
		aux = node1.getKey();
		node1.setKey(node2.getKey());
		node2.setKey(aux);
		
	}
	private AVLNode<T> minNode(AVLNode<T> node){
		AVLNode<T> aux = node;
		if(aux != null){
			while(aux.getLeft()!= null){
				aux = aux.getLeft();
			}
			return aux;
		}
		return null;
	}
	private int balancingFactor(AVLNode<T> node){
		return nodeHeight(node.getRight())-nodeHeight(node.getLeft());
	}
	private int nodeHeight(AVLNode<T> node){
		if(node == null) return 0;
		return max(nodeHeight(node.getLeft()),nodeHeight(node.getRight()))+1;
	}
	private int max(int a, int b){
		if(a>b) return a;
		return b;
	}
	public AVLNode<T> getRoot() {
		return root;
	}

	public void setRoot(AVLNode<T> root) {
		this.root = root;
	}
	
	private void visit(AVLNode<T> node, String type, ArrayList<T> list){
		if(node != null){
			if(type.equals("PRE")){
				list.add(node.getKey());
				visit(node.getLeft(), type, list);				
				visit(node.getRight(), type, list);				
			}else if(type.equals("POS")){
				visit(node.getLeft(), type, list);				
				visit(node.getRight(), type, list);
				list.add(node.getKey());
			}else{
				visit(node.getLeft(), type, list);	
				list.add(node.getKey());
				visit(node.getRight(), type, list);					
			}
		}
	}
	public ArrayList<T> toArrayList(String type){
		if(getRoot() == null) return null;
		
		ArrayList<T> ret = new ArrayList<T>();
		
		visit(root, type, ret);
		
		return ret;
	}
}
