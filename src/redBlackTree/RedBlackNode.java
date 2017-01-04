package redBlackTree;

// class RedBlackNode
class RedBlackNode<T extends Comparable<T>> {

    //defini��o da cor BLACK
    public static final int BLACK = 0;
  //defini��o da cor RED
    public static final int RED = 1;
	//chave do node
	public T key;

    //pai do n�
    RedBlackNode<T> parent;
    //filho esquerdo
    RedBlackNode<T> left;
    //filho direito
    RedBlackNode<T> right;
    // n�mero de elementos da esquerda de cada n�
    public int numLeft = 0;
    // n�mero de elementos da direita de cada n�
    public int numRight = 0;
    // cor de cada n�
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

