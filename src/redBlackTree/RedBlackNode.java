package redBlackTree;

// class RedBlackNode
class RedBlackNode<T extends Comparable<T>> {

    //definição da cor BLACK
    public static final int BLACK = 0;
  //definição da cor RED
    public static final int RED = 1;
	//chave do node
	public T key;

    //pai do nó
    RedBlackNode<T> parent;
    //filho esquerdo
    RedBlackNode<T> left;
    //filho direito
    RedBlackNode<T> right;
    // número de elementos da esquerda de cada nó
    public int numLeft = 0;
    // número de elementos da direita de cada nó
    public int numRight = 0;
    // cor de cada nó
    public int color;

    RedBlackNode(){
        color = BLACK;
        numLeft = 0;
        numRight = 0;
        parent = null;
        left = null;
        right = null;
    }

	//construtor com a Chave dada
	RedBlackNode(T key){
        this();
        this.key = key;
	}
}

