package btree;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;

import avlTree.AVLNode;
import btree.BNode;

@SuppressWarnings("unchecked")//suprime warnings de casting
public class BTree<T extends Comparable<T>>{

	// nó padrão de 2-3
	private int minKeySize = 1;
	private int minChildrenSize = minKeySize + 1; // 2
	private int maxKeySize = 2 * minKeySize; // 2
	private int maxChildrenSize = maxKeySize + 1; // 3

	private BNode<T> root = null;
	private int size = 0;

	//construtor padrao que seta a arvore pra 2-3
	public BTree() { }
	//construtor que cria uma arvore de acordo com a ordem informada
	public BTree(int order) {
		this.minKeySize = order;
		this.minChildrenSize = minKeySize + 1;
		this.maxKeySize = 2 * minKeySize;
		this.maxChildrenSize = maxKeySize + 1;
	}
	
	public boolean insert(T value) {//metodo de inserir
		if (root == null) {
			root = new BNode<T>(null, maxKeySize, maxChildrenSize);
			root.addKey(value);
		} else {
			BNode<T> BNode = root;
			while (BNode != null) {
				if (BNode.numberOfChildren() == 0) {
					BNode.addKey(value);
					if (BNode.numberOfKeys() <= maxKeySize) {
						//tudo OK
						break;
					}                         
					// precisa separar o no
					split(BNode);
					break;
				}
				//navegando na arvore
				T lesser = BNode.getKey(0);//menor ou igual
				if (value.compareTo(lesser) <= 0) {
					BNode = BNode.getChild(0);
					continue;
				}

				int numberOfKeys = BNode.numberOfKeys();//maior
				int last = numberOfKeys - 1;
				T greater = BNode.getKey(last);
				if (value.compareTo(greater) > 0) {
					BNode = BNode.getChild(numberOfKeys);
					continue;
				}

				// procura em nos internos
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

		size++;//aumenta o numero de nos

		return true;
	}
	private void visit(BNode<T> node, String type, ArrayList<T> list){//metodo de visita
		if(node != null){
			if(type.equals("PRE")){//percebemos que nao deu certo visitar em outras ordens
				/*for (i=0; i < node.getKeysSize(); i++) {
					list.add(node.getKey(i));
					visit(node.getChild(i), type, list);
				}
				visit(node.getChild(i), type, list);*/
				System.err.println("You can only print B trees in order!");
			}else if(type.equals("POS")){
				/*visit(node.getLeft(), type, list);				
				visit(node.getRight(), type, list);
				list.add(node.getKey());*/
				System.err.println("You can only print B trees in order!");
			}else{//visita em ordem
				visit(node.getChild(0), type, list);
				for (int i = 0; i < node.getKeysSize(); i++) {
					list.add(node.getKey(i));
					visit(node.getChild(i+1), type, list);
				}					
			}
		}
	}
	public ArrayList<T> toArrayList(String type){//cria o array list e retorna ele preeenchido com as chaves visitadas
		if(root == null) return null;
		
		ArrayList<T> ret = new ArrayList<T>();
		
		visit(root, type, ret);
		
		return ret;
	}
	//quando o numero de keys é maior que o max e precisa partir o no em dois
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
			// nova raiz, a altura da arvore aumentou
			BNode<T> newRoot = new BNode<T>(null, maxKeySize, maxChildrenSize);
			newRoot.addKey(medianValue);
			BNode.parent = newRoot;
			root = newRoot;
			BNode = root;
			BNode.addChild(left);
			BNode.addChild(right);
		} else {
			// moveo a key do meio pro pai
			BNode<T> parent = BNode.parent;
			parent.addKey(medianValue);
			parent.removeChild(BNode);
			parent.addChild(left);
			parent.addChild(right);

			if (parent.numberOfKeys() > maxKeySize) split(parent);
		}
	}

	public T remove(T value) {//remove a key informada e retorna ela
		T removed = null;
		BNode<T> BNode = this.getBNode(value);
		removed = remove(value,BNode);
		return removed;
	}

	//remove a key informada do no
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
	//remove o maior valor do no e retorna ele
	private T removeGreatestValue(BNode<T> BNode) {
		T value = null;
		if (BNode.numberOfKeys() > 0) {
			value = BNode.removeKey(BNode.numberOfKeys() - 1);
		}
		return value;
	}


	public void clear() {//reseta a arvore
		root = null;
		size = 0;
	}

	public boolean contains(T value) {//testa se essa key esa na arvore
		BNode<T> BNode = getBNode(value);
		return (BNode != null);
	}
	
	public BNode<T> search(T value) {//procura um valor e retorna o no que contem ele
		return getBNode(value);
	}
	//mesma coisa do acima
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

	//retorna o no filho com o maior valor
	private BNode<T> getGreatestBNode(BNode<T> BNodeToGet) {
		BNode<T> BNode = BNodeToGet;
		while (BNode.numberOfChildren() > 0) {
			BNode = BNode.getChild(BNode.numberOfChildren() - 1);
		}
		return BNode;
	}

	//combina as keys com o no pai quando o numero de key é menor que o minimo 
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

		//tenta com o vizinho
		if (rightNeighbor != null && rightNeighborSize > minKeySize) {
			// vizinho da direita
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
				//vizinho da esquerda
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
				// nao pode pedir emprestado dos vizinhos
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
					// remover do pai tornou ele muito pequeno
					this.combined(parent);
				} else if (parent.numberOfKeys() == 0) {
					//combinou dois nos, diminiu a altura da arvore 
					BNode.parent = null;
					root = BNode;
				}
			} else if (leftNeighbor != null && parent.numberOfKeys() > 0) {
				// pede emprestado do filho esquerdo
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
					// remover do pai fez ele muito pequeno, combina
					this.combined(parent);
				} else if (parent.numberOfKeys() == 0) {
					BNode.parent = null;
					root = BNode;
				}
			}
		}

		return true;
	}
	//retorna o valor anterior a esse valor no no
	private int getIndexOfPreviousValue(BNode<T> BNode, T value) {
		for (int i = 1; i < BNode.numberOfKeys(); i++) {
			T t = BNode.getKey(i);
			if (t.compareTo(value) >= 0)
				return i - 1;
		}
		return BNode.numberOfKeys() - 1;
	}

	//pega o index do proximo valor
	private int getIndexOfNextValue(BNode<T> BNode, T value) {
		for (int i = 0; i < BNode.numberOfKeys(); i++) {
			T t = BNode.getKey(i);
			if (t.compareTo(value) >= 0)
				return i;
		}
		return BNode.numberOfKeys() - 1;
	}

	//tamanho da arvore
	public int size() {
		return size;
	}
	//valida a arvore
	public boolean validate() {
		if (root == null) return true;
		return validateBNode(root);
	}

	//valida o no
	private boolean validateBNode(BNode<T> BNode) {
		int keySize = BNode.numberOfKeys();
		if (keySize > 1) {
			// garante que as chaves estao organizadas
			for (int i = 1; i < keySize; i++) {
				T p = BNode.getKey(i - 1);
				T n = BNode.getKey(i);
				if (p.compareTo(n) > 0)
					return false;
			}
		}
		int childrenSize = BNode.numberOfChildren();
		if (BNode.parent == null) {
			// raiz
			if (keySize > maxKeySize) {
				// checa o numero maximo de keys
				return false;
			} else if (childrenSize == 0) {
				//se for raiz,sem filhos,e chaves invalidas
				return true;
			} else if (childrenSize < 2) {
				// raiz deve ter zero ou no minimo 2
				return false;
			} else if (childrenSize > maxChildrenSize) {
				return false;
			}
		} else {
			// nao-raiz
			if (keySize < minKeySize) {
				return false;
			} else if (keySize > maxKeySize) {
				return false;
			} else if (childrenSize == 0) {
				return true;
			} else if (keySize != (childrenSize - 1)) {
				// se tem filhos,deve ter 1 filho a mais que chaves 
				return false;
			} else if (childrenSize < minChildrenSize) {
				return false;
			} else if (childrenSize > maxChildrenSize) {
				return false;
			}
		}

		BNode<T> first = BNode.getChild(0);
		// a ultima chave do primeiro filho nao deve ser maior que o primeira chave 
		if (first.getKey(first.numberOfKeys() - 1).compareTo(BNode.getKey(0)) > 0)
			return false;

		BNode<T> last = BNode.getChild(BNode.numberOfChildren() - 1);
		// a primeira chave do ultimo filho tem que ser amior que a ultima chave do no
		if (last.getKey(0).compareTo(BNode.getKey(BNode.numberOfKeys() - 1)) < 0)
			return false;

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
}