//https://github.com/Arsenalist/Red-Black-Tree-Java-Implementation
package redBlackTree;

import java.util.ArrayList;
import java.util.List;

public class RedBlackTree<T extends Comparable<T>> {

	//defini��o de um n� nulo onde todas as folhas o possuem como filho.
	private RedBlackNode<T> nil = new RedBlackNode<T>();
	private RedBlackNode<T> root = nil;

    public RedBlackTree() {
        root.left = nil;
        root.right = nil;
        root.parent = nil;
    }

	//rota��o � esquerda em x
	private void leftRotate(RedBlackNode<T> x){
		//atualiza os valores de numLeft e numRight
		leftRotateFixup(x);

		//rota��o � esquerda
		RedBlackNode<T> y;
		y = x.right;
		x.right = y.left;

		//verifica a exist�ncia de y.left e faz altera��es nos ponteiros
		if (!isNil(y.left))
			y.left.parent = x;
		y.parent = x.parent;

		//se o pai de x for null..
		if (isNil(x.parent))
			root = y;

		//se x � um filho � esquerda
		else if (x.parent.left == x)
			x.parent.left = y;

		//se x � um filho � direita
		else
			x.parent.right = y;

		y.left = x;
		x.parent = y;
	}


	// x - n� em que a rota��o � esqueda est� sendo feita 
	//atualiza numleft e numRight que s�o alterados na rota��o
	private void leftRotateFixup(RedBlackNode<T> x){


		if (isNil(x.left) && isNil(x.right.left)){
			x.numLeft = 0;
			x.numRight = 0;
			x.right.numLeft = 1;
		}


		else if (isNil(x.left) && !isNil(x.right.left)){
			x.numLeft = 0;
			x.numRight = 1 + x.right.left.numLeft +
					x.right.left.numRight;
			x.right.numLeft = 2 + x.right.left.numLeft +
					  x.right.left.numRight;
		}


		else if (!isNil(x.left) && isNil(x.right.left)){
			x.numRight = 0;
			x.right.numLeft = 2 + x.left.numLeft + x.left.numRight;

		}

		else{
			x.numRight = 1 + x.right.left.numLeft +
				     x.right.left.numRight;
			x.right.numLeft = 3 + x.left.numLeft + x.left.numRight +
			x.right.left.numLeft + x.right.left.numRight;
		}

	}


	//rota��o � direita em x - sim�trica � rota��o � esquerda.
	private void rightRotate(RedBlackNode<T> y){

		
		rightRotateFixup(y);
        
        RedBlackNode<T> x = y.left;
        y.left = x.right;
        
        if (!isNil(x.right))
            x.right.parent = y;
        x.parent = y.parent;
        
        if (isNil(y.parent))
            root = x;
        
        else if (y.parent.right == y)
            y.parent.right = x;
     
        else
            y.parent.left = x;
        x.right = y;

        y.parent = x;
	}


	// y - n� em que a rota��o � direita est� sendo feita 
	//atualiza numleft e numRight que s�o alterados na rota��o - simetrico ao leftRotateFixUp
	private void rightRotateFixup(RedBlackNode<T> y){

		
		if (isNil(y.right) && isNil(y.left.right)){
			y.numRight = 0;
			y.numLeft = 0;
			y.left.numRight = 1;
		}

		else if (isNil(y.right) && !isNil(y.left.right)){
			y.numRight = 0;
			y.numLeft = 1 + y.left.right.numRight +
				  y.left.right.numLeft;
			y.left.numRight = 2 + y.left.right.numRight +
				  y.left.right.numLeft;
		}

		else if (!isNil(y.right) && isNil(y.left.right)){
			y.numLeft = 0;
			y.left.numRight = 2 + y.right.numRight +y.right.numLeft;

		}

		else{
			y.numLeft = 1 + y.left.right.numRight +
				  y.left.right.numLeft;
			y.left.numRight = 3 + y.right.numRight +
				  y.right.numLeft +
			y.left.right.numRight + y.left.right.numLeft;
		}

	}

	//m�todo p�blico para inser��o
    public void insert(T key) {
        insert(new RedBlackNode<T>(key));
    }

    //insere o n� z na �rvore, atualizando numLeft e numRight
	private void insert(RedBlackNode<T> z) {

			// Refer�ncia para a raiz e para o n� nulo
			RedBlackNode<T> y = nil;
			RedBlackNode<T> x = root;

			//encontrar onde o n� z deve ser inserido
			while (!isNil(x)){
				y = x;

				if (z.key.compareTo(x.key) < 0){
					x.numLeft++;
					x = x.left;
				}

				else{
					x.numRight++;
					x = x.right;
				}
			}
			// y ser� o pai de z
			z.parent = y;

			//dependendo do valor de y, z ser� um filho � esquerda ou � direita
			if (isNil(y))
				root = z;
			else if (z.key.compareTo(y.key) < 0)
				y.left = z;
			else
				y.right = z;

			//filhos de z ser�o nulos e sua cor ser� vermelha
			z.left = nil;
			z.right = nil;
			z.color = RedBlackNode.RED;

			//fixar viola��es na �rvore
			insertFixup(z);

	}

	// z - n� que foi inserido
	// ajuste de viola��es na �rvore
	private void insertFixup(RedBlackNode<T> z){

		RedBlackNode<T> y = nil;
		//enquanto o pai de z for vermelho..
		while (z.parent.color == RedBlackNode.RED){

			//se o pai de z � um filho � esquerda
			if (z.parent == z.parent.parent.left){

				// y � o tio de z
				y = z.parent.parent.right;

				// se y � vermelho, recolorir
				if (y.color == RedBlackNode.RED){
					z.parent.color = RedBlackNode.BLACK;
					y.color = RedBlackNode.BLACK;
					z.parent.parent.color = RedBlackNode.RED;
					z = z.parent.parent;
				}
				//se y � preto e z � filho � direita
				else if (z == z.parent.right){

					//rota��o � esquerda no pai de z
					z = z.parent;
					leftRotate(z);
				}

				//se y � preto e z � filho � esquerda
				else{
					//recolorir e rotazionar no avo de z
					z.parent.color = RedBlackNode.BLACK;
					z.parent.parent.color = RedBlackNode.RED;
					rightRotate(z.parent.parent);
				}
			}

			//se o pai de z � um filho � direita
			//sim�trico ao primeiro caso
			else{

				y = z.parent.parent.left;

				if (y.color == RedBlackNode.RED){
					z.parent.color = RedBlackNode.BLACK;
					y.color = RedBlackNode.BLACK;
					z.parent.parent.color = RedBlackNode.RED;
					z = z.parent.parent;
				}

				else if (z == z.parent.left){
					z = z.parent;
					rightRotate(z);
				}
				else{
					z.parent.color = RedBlackNode.BLACK;
					z.parent.parent.color = RedBlackNode.RED;
					leftRotate(z.parent.parent);
				}
			}
		}
	//sempre coloca a raiz como preta
	root.color = RedBlackNode.BLACK;

	}

	//menor n� de uma �rvore com raiz em n�
	public RedBlackNode<T> treeMinimum(RedBlackNode<T> node){

		while (!isNil(node.left))
			node = node.left;
		return node;
	}
	//retorna o sucesor de x
	public RedBlackNode<T> treeSuccessor(RedBlackNode<T> x){

		//se x tem filho a esquerda, retorna o menor valor da arvore direita de x
		if (!isNil(x.left) )
			return treeMinimum(x.right);

		RedBlackNode<T> y = x.parent;
		//enquanto x for um filho � direita
		while (!isNil(y) && x == y.right){
			x = y;
			y = y.parent;
		}
		//retorno do sucessor
		return y;
	}

	//z ser� o n� a ser removido da �rvore
	public void remove(RedBlackNode<T> v){
		//verificar se z est� na �rvore
		RedBlackNode<T> z = search(v.key);

		// Vari�veis
		RedBlackNode<T> x = nil;
		RedBlackNode<T> y = nil;

		// se um dos filhos de z for null, podemos remover z
		if (isNil(z.left) || isNil(z.right))
			y = z;

		//sen�o, removemos o sucessor
		else y = treeSuccessor(z);

		//x ser� o filho de y(esquerdo, direito, ou null)
		if (!isNil(y.left))
			x = y.left;
		else
			x = y.right;

		//o pai de x ser� o pai de y
		x.parent = y.parent;

		// se y era a raiz, x ser� a raiz
		if (isNil(y.parent))
			root = x;

		//se y � um filho � esquerda, y ser� filho � esquerda
		else if (!isNil(y.parent.left) && y.parent.left == y)
			y.parent.left = x;

		//se y � um filho � direita, y ser� filho � direita
		else if (!isNil(y.parent.right) && y.parent.right == y)
			y.parent.right = x;

		// se y for diferente de z, z receber� os dados de y
		if (y != z){
			z.key = y.key;
		}
		
		//atualizar numLeft e numRight que podem ter mudado
		//atualizar devido a remo��o de z.key
		fixNodeData(x,y);

		//se um n� preto foi removido, devemos consertar a �rvore
		if (y.color == RedBlackNode.BLACK)
			removeFixup(x);
	}


	//y - o n� que foi removido
	//key - o valor que estava em y
	private void fixNodeData(RedBlackNode<T> x, RedBlackNode<T> y){

		// Vari�veis
		RedBlackNode<T> current = nil;
		RedBlackNode<T> track = nil;


		//se x for nulo, atualizamos no pai de y
		if (isNil(x)){
			current = y.parent;
			track = y;
		}

		// sen�o, atualizamos no pai de x
		else{
			current = x.parent;
			track = x;
		}

		//enquanto n�o achamos a raiz
		while (!isNil(current)){
			//se o n� deletado tem uma chave diferente do n� atual
			if (y.key != current.key) {

				//verificamos se ele estaria � direita ou � esquerda e fazemos o ajuste
				if (y.key.compareTo(current.key) > 0)
					current.numRight--;

				if (y.key.compareTo(current.key) < 0)
					current.numLeft--;
			}

			//se o n� que deletamos � igual ao autal
			else{
				//se o n� atual tiver algum filho null, ajustamos
				if (isNil(current.left))
					current.numLeft--;
				else if (isNil(current.right))
					current.numRight--;

				// se ele tiver dois filhos, verificamos se track � o filho � esquerda ou � direita e atualizamos
				else if (track == current.right)
					current.numRight--;
				else if (track == current.left)
					current.numLeft--;
			}

			//atualizar track e current
			track = current;
			current = current.parent;

		}

	}


	//x - filho do n� removido
	//fixar as propriedas que podem ter sido afetas
	private void removeFixup(RedBlackNode<T> x){

		RedBlackNode<T> w;

		//enquanto n�o terminamos de consertar a �rvore
		while (x != root && x.color == RedBlackNode.BLACK){

			//se x � filho � esquerda
			if (x == x.parent.left){

				//w ser� irm�o de x
				w = x.parent.right;

				// se a cor de w for vermelga
				if (w.color == RedBlackNode.RED){
					w.color = RedBlackNode.BLACK;
					x.parent.color = RedBlackNode.RED;
					leftRotate(x.parent);
					w = x.parent.right;
				}

				// se os dois filhos de w s�o pretos
				if (w.left.color == RedBlackNode.BLACK &&
							w.right.color == RedBlackNode.BLACK){
					w.color = RedBlackNode.RED;
					x = x.parent;
				}
	
				else{
					// se o filho direito de w � preto
					if (w.right.color == RedBlackNode.BLACK){
						w.left.color = RedBlackNode.BLACK;
						w.color = RedBlackNode.RED;
						rightRotate(w);
						w = x.parent.right;
					}
					//w � preto e seu filho direito � vermelho
					w.color = x.parent.color;
					x.parent.color = RedBlackNode.BLACK;
					w.right.color = RedBlackNode.BLACK;
					leftRotate(x.parent);
					x = root;
				}
			}
			//sim�trico ao outro caso
			else{

				w = x.parent.left;

				if (w.color == RedBlackNode.RED){
					w.color = RedBlackNode.BLACK;
					x.parent.color = RedBlackNode.RED;
					rightRotate(x.parent);
					w = x.parent.left;
				}

				if (w.right.color == RedBlackNode.BLACK &&
							w.left.color == RedBlackNode.BLACK){
					w.color = RedBlackNode.RED;
					x = x.parent;
				}

				else{
					 if (w.left.color == RedBlackNode.BLACK){
						w.right.color = RedBlackNode.BLACK;
						w.color = RedBlackNode.RED;
						leftRotate(w);
						w = x.parent.left;
					}

					w.color = x.parent.color;
					x.parent.color = RedBlackNode.BLACK;
					w.left.color = RedBlackNode.BLACK;
					rightRotate(x.parent);
					x = root;
				}
			}
		}
		//colocar x como preto para ter certeza de que propriedades n�o foram violadas
		x.color = RedBlackNode.BLACK;
	}

	//pesquisa uma chave na �rvore
	public RedBlackNode<T> search(T key){

		RedBlackNode<T> current = root;
		//enquanto n�o chegamos no fim da �rvore
		while (!isNil(current)){

			if (current.key.equals(key))
				return current;
			else if (current.key.compareTo(key) < 0)
				current = current.right;
			else
				current = current.left;
		}

		//se n�o achamos..
		return null;


	}
	
	//verifica se node � nil
	private boolean isNil(RedBlackNode<T> node){
		return node == nil;
	}
	
	//retorna o tamanho da �rvore
	public int size(){
		return root.numLeft + root.numRight + 1;
	}
	
	//m�todo de visita��o utilizado para retornar os n�s em uma determinada ordem: PRE, IN ou POS ORDER
	private void visit(RedBlackNode<T> node, String type, ArrayList<T> list){
		if(!isNil(node)){
			if(type.equals("PRE")){
				list.add(node.key);
				visit(node.left, type, list);				
				visit(node.right, type, list);				
			}else if(type.equals("POS")){
				visit(node.left, type, list);
				visit(node.right, type, list);
				list.add(node.key);
			}else{
				visit(node.left, type, list);
				list.add(node.key);
				visit(node.right, type, list);				
			}
		}
	}
	//m�todo vis�vel em que o usu�rio informa o tipo de ordena��o que deseja: PRE, IN ou POS ORDER
	public ArrayList<T> toArrayList(String type){
		if(isNil(root)) return null;
		
		ArrayList<T> ret = new ArrayList<T>();
		
		visit(root, type, ret);
		
		return ret;
	}
}
