package app;

import avlTree.AVLTree;
//essa classe serve apenas para testes
public class Teste {
	private static AVLTree<Integer> avlTree;
	public static void main(String[] args) {
		App app = new App();
		avlTree = new AVLTree<Integer>();		
		avlTree.insert(new Integer(6));
		avlTree.insert(new Integer(7));
		avlTree.insert(new Integer(8));
		avlTree.insert(new Integer(3));
		avlTree.remove(new Integer(7));
		avlTree.insert(new Integer(7));
		avlTree.insert(new Integer(20));
		avlTree.remove(new Integer(8));
		avlTree.insert(new Integer(15));
		avlTree.remove(new Integer(3));
		avlTree.insert(new Integer(10));
		avlTree.insert(new Integer(17));
		avlTree.insert(new Integer(2));
		avlTree.remove(new Integer(20));
		avlTree.remove(new Integer(20));
		
		for(Integer t : avlTree.toArrayList("PRE")){
			System.out.println(t);
		}
	}

}
