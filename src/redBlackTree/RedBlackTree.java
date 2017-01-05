//https://github.com/Arsenalist/Red-Black-Tree-Java-Implementation
package redBlackTree;

import java.util.ArrayList;
import java.util.List;

public class RedBlackTree<T extends Comparable<T>> {

	//definição de um nó nulo onde todas as folhas o possuem como filho.
	private RedBlackNode<T> nil = new RedBlackNode<T>();
	private RedBlackNode<T> root = nil;

    public RedBlackTree() {
        root.left = nil;
        root.right = nil;
        root.parent = nil;
    }

	//rotação à esquerda em x
	private void leftRotate(RedBlackNode<T> x){
		//atualiza os valores de numLeft e numRight
		leftRotateFixup(x);

		//rotação à esquerda
		RedBlackNode<T> y;
		y = x.right;
		x.right = y.left;

		//verifica a existência de y.left e faz alterações nos ponteiros
		if (!isNil(y.left))
			y.left.parent = x;
		y.parent = x.parent;

		//se o pai de x for null..
		if (isNil(x.parent))
			root = y;

		//se x é um filho à esquerda
		else if (x.parent.left == x)
			x.parent.left = y;

		//se x é um filho à direita
		else
			x.parent.right = y;

		y.left = x;
		x.parent = y;
	}


	// x - nó em que a rotação à esqueda está sendo feita 
	//atualiza numleft e numRight que são alterados na rotação
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


	//rotação à direita em x - simétrica à rotação à esquerda.
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


	// y - nó em que a rotação à direita está sendo feita 
	//atualiza numleft e numRight que são alterados na rotação - simetrico ao leftRotateFixUp
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

	//método público para inserção
    public void insert(T key) {
        insert(new RedBlackNode<T>(key));
    }

    //insere o nó z na árvore, atualizando numLeft e numRight
	private void insert(RedBlackNode<T> z) {

			// Referência para a raiz e para o nó nulo
			RedBlackNode<T> y = nil;
			RedBlackNode<T> x = root;

			//encontrar onde o nó z deve ser inserido
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
			// y será o pai de z
			z.parent = y;

			//dependendo do valor de y, z será um filho à esquerda ou à direita
			if (isNil(y))
				root = z;
			else if (z.key.compareTo(y.key) < 0)
				y.left = z;
			else
				y.right = z;

			//filhos de z serão nulos e sua cor será vermelha
			z.left = nil;
			z.right = nil;
			z.color = RedBlackNode.RED;

			//fixar violações na árvore
			insertFixup(z);

	}

	// z - nó que foi inserido
	// ajuste de violações na árvore
	private void insertFixup(RedBlackNode<T> z){

		RedBlackNode<T> y = nil;
		//enquanto o pai de z for vermelho..
		while (z.parent.color == RedBlackNode.RED){

			//se o pai de z é um filho à esquerda
			if (z.parent == z.parent.parent.left){

				// y é o tio de z
				y = z.parent.parent.right;

				// se y é vermelho, recolorir
				if (y.color == RedBlackNode.RED){
					z.parent.color = RedBlackNode.BLACK;
					y.color = RedBlackNode.BLACK;
					z.parent.parent.color = RedBlackNode.RED;
					z = z.parent.parent;
				}
				//se y é preto e z é filho à direita
				else if (z == z.parent.right){

					//rotação à esquerda no pai de z
					z = z.parent;
					leftRotate(z);
				}

				//se y é preto e z é filho à esquerda
				else{
					//recolorir e rotazionar no avo de z
					z.parent.color = RedBlackNode.BLACK;
					z.parent.parent.color = RedBlackNode.RED;
					rightRotate(z.parent.parent);
				}
			}

			//se o pai de z é um filho à direita
			//simétrico ao primeiro caso
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

	//menor nó de uma árvore com raiz em nó
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
		//enquanto x for um filho à direita
		while (!isNil(y) && x == y.right){
			x = y;
			y = y.parent;
		}
		//retorno do sucessor
		return y;
	}

	//z será o nó a ser removido da árvore
	public void remove(RedBlackNode<T> v){
		//verificar se z está na árvore
		RedBlackNode<T> z = search(v.key);

		// Variáveis
		RedBlackNode<T> x = nil;
		RedBlackNode<T> y = nil;

		// se um dos filhos de z for null, podemos remover z
		if (isNil(z.left) || isNil(z.right))
			y = z;

		//senão, removemos o sucessor
		else y = treeSuccessor(z);

		//x será o filho de y(esquerdo, direito, ou null)
		if (!isNil(y.left))
			x = y.left;
		else
			x = y.right;

		//o pai de x será o pai de y
		x.parent = y.parent;

		// se y era a raiz, x será a raiz
		if (isNil(y.parent))
			root = x;

		//se y é um filho à esquerda, y será filho à esquerda
		else if (!isNil(y.parent.left) && y.parent.left == y)
			y.parent.left = x;

		//se y é um filho à direita, y será filho à direita
		else if (!isNil(y.parent.right) && y.parent.right == y)
			y.parent.right = x;

		// se y for diferente de z, z receberá os dados de y
		if (y != z){
			z.key = y.key;
		}
		
		//atualizar numLeft e numRight que podem ter mudado
		//atualizar devido a remoção de z.key
		fixNodeData(x,y);

		//se um nó preto foi removido, devemos consertar a árvore
		if (y.color == RedBlackNode.BLACK)
			removeFixup(x);
	}


	//y - o nó que foi removido
	//key - o valor que estava em y
	private void fixNodeData(RedBlackNode<T> x, RedBlackNode<T> y){

		// Variáveis
		RedBlackNode<T> current = nil;
		RedBlackNode<T> track = nil;


		//se x for nulo, atualizamos no pai de y
		if (isNil(x)){
			current = y.parent;
			track = y;
		}

		// senão, atualizamos no pai de x
		else{
			current = x.parent;
			track = x;
		}

		//enquanto não achamos a raiz
		while (!isNil(current)){
			//se o nó deletado tem uma chave diferente do nó atual
			if (y.key != current.key) {

				//verificamos se ele estaria à direita ou á esquerda e fazemos o ajuste
				if (y.key.compareTo(current.key) > 0)
					current.numRight--;

				if (y.key.compareTo(current.key) < 0)
					current.numLeft--;
			}

			//se o nó que deletamos é igual ao autal
			else{
				//se o nó atual tiver algum filho null, ajustamos
				if (isNil(current.left))
					current.numLeft--;
				else if (isNil(current.right))
					current.numRight--;

				// se ele tiver dois filhos, verificamos se track é o filho à esquerda ou à direita e atualizamos
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


	//x - filho do nó removido
	//fixar as propriedas que podem ter sido afetas
	private void removeFixup(RedBlackNode<T> x){

		RedBlackNode<T> w;

		//enquanto não terminamos de consertar a árvore
		while (x != root && x.color == RedBlackNode.BLACK){

			//se x é filho à esquerda
			if (x == x.parent.left){

				//w será irmão de x
				w = x.parent.right;

				// se a cor de w for vermelga
				if (w.color == RedBlackNode.RED){
					w.color = RedBlackNode.BLACK;
					x.parent.color = RedBlackNode.RED;
					leftRotate(x.parent);
					w = x.parent.right;
				}

				// se os dois filhos de w são pretos
				if (w.left.color == RedBlackNode.BLACK &&
							w.right.color == RedBlackNode.BLACK){
					w.color = RedBlackNode.RED;
					x = x.parent;
				}
	
				else{
					// se o filho direito de w é preto
					if (w.right.color == RedBlackNode.BLACK){
						w.left.color = RedBlackNode.BLACK;
						w.color = RedBlackNode.RED;
						rightRotate(w);
						w = x.parent.right;
					}
					//w é preto e seu filho direito é vermelho
					w.color = x.parent.color;
					x.parent.color = RedBlackNode.BLACK;
					w.right.color = RedBlackNode.BLACK;
					leftRotate(x.parent);
					x = root;
				}
			}
			//simétrico ao outro caso
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
		//colocar x como preto para ter certeza de que propriedades não foram violadas
		x.color = RedBlackNode.BLACK;
	}

	//pesquisa uma chave na árvore
	public RedBlackNode<T> search(T key){

		RedBlackNode<T> current = root;
		//enquanto não chegamos no fim da árvore
		while (!isNil(current)){

			if (current.key.equals(key))
				return current;
			else if (current.key.compareTo(key) < 0)
				current = current.right;
			else
				current = current.left;
		}

		//se não achamos..
		return null;


	}
	
	//verifica se node é nil
	private boolean isNil(RedBlackNode<T> node){
		return node == nil;
	}
	
	//retorna o tamanho da árvore
	public int size(){
		return root.numLeft + root.numRight + 1;
	}
	
	//método de visitação utilizado para retornar os nós em uma determinada ordem: PRE, IN ou POS ORDER
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
	//método visível em que o usuário informa o tipo de ordenação que deseja: PRE, IN ou POS ORDER
	public ArrayList<T> toArrayList(String type){
		if(isNil(root)) return null;
		
		ArrayList<T> ret = new ArrayList<T>();
		
		visit(root, type, ret);
		
		return ret;
	}
}
