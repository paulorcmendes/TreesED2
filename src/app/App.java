package app;
//essa é a classe terminal
import java.util.ArrayList;
import java.util.Scanner;

import avlTree.AVLTree;
import btree.BTree;
import redBlackTree.RedBlackTree;

public class App<T> {
	private RedBlackTree<String> rbTree; //árvore rubro negra
	private AVLTree<String> avlTree; //árvore avl
	private BTree<String> bTree; //árvore B
	private Scanner sc;
	//inicialização das árvores e controle dos próximos comandos
	public void run(){
		sc = new Scanner(System.in);
		setRbTree(new RedBlackTree<String>());
		setAvlTree(new AVLTree<String>());
		setBTree(new BTree<String>());
		while(sc.hasNextLine()){
			try{
				InterpretCommand(getNextCommand());
			}catch(Exception e){
				System.err.println("Please type a valid command! Type HELP to see the available commands");
			}
		}
	}
	//getters and setters
	public RedBlackTree<String> getRbTree() {
		return rbTree;
	}
	public void setRbTree(RedBlackTree<String> rbTree) {
		this.rbTree = rbTree;
	}
	public AVLTree<String> getAvlTree() {
		return avlTree;
	}
	public void setAvlTree(AVLTree<String> avlTree) {
		this.avlTree = avlTree;
	}
	public BTree<String> getBTree() {
		return bTree;
	}
	public void setBTree(BTree<String> bTree) {
		this.bTree = bTree;
	}
	//methods
	//verifica qual é o tipo de comanda e chama o método correspondente
	private void InterpretCommand(String[] command) throws Exception{
		switch(command[0]){
		case "VL":
			//System.out.println("AVL");
			AVLOperation(command);
			break;
		case "BT":			
			//System.out.println("BTree");
			BTOperation(command);
			break;
		case "RB":
			//System.out.println("RedBlack");
			RBOperation(command);
			break;
		case "PRINT":
			//System.out.println("Print");
			PrintOperation(command);
			break;
		case "COPY":
			//System.out.println("COPY");
			CopyOperation(command);
			break;
		case "HELP":
			help();
			break;
		default:
			UnexpectedToken();
		}
	}
	//caso seja print, verifica qual árvore e qual tipo de ordem se deseja imprimir
	private void PrintOperation(String[] command) throws Exception{
		switch(command[2]){
		case "AVL"/*"VL"*/:
			printArrayList(getAvlTree().toArrayList(command[1]));
			break;
		case "BT":
			printArrayList(getBTree().toArrayList(command[1]));
			break;
		case "RB":	
			printArrayList(getRbTree().toArrayList(command[1]));
			break;

		default:
			UnexpectedToken();
		}
	}
	//printa o conteúdo de um arraylist
	private void printArrayList(ArrayList<String> arrayList) {
		if(arrayList == null){
			System.err.println("Árvore Vazia");
			return;
		}
		for(String key : arrayList){
			System.out.println(key);
		}

	}
	//comando help: para ajudar o usuário foi criado esse minimanual.
	private void help() {
		String msg = "Insertion: <Tree> I <data>\n"+
				"Deletion:  <Tree> R <data>\n"+
				"Copy:      COPY <Type> <Tree> <Tree>\n"+
				"        PS: Can only be made from AVL or RB to the others\n"+
				"Creation:  <Tree> NEW\n"+
				"        PS: If the tree is a BTree: <Tree> NEW <number>\n"+
				"Print:     PRINT <Type> <Tree>\n\n"+
				"<Tree>: VL(for AVL); BT(for BTree); RB(for RedBlackTree)\n"+
				"<data>: an alphanumeric data\n"+
				"<Type>: IN(for InOrder); PRE(for PreOrder); POS(for PostOrder)\n"+
				"<number>: a natural number greater than 0\n\n";

		System.out.print(msg);
	}
	//quando um comando inexistente for chamado, o usuário é alertado
	private void UnexpectedToken() {
		System.err.println("Comando Inválido - Digite HELP para ver os comandos disponíveis");

	}
	//verifica qual árvore será copiada e em seguida para qual árvore será copiada. Chama o método que retorna um array de acordo com o tipo de ordem desejada e realiza a cópia para a outra árvore.
	private void CopyOperation(String[] command) throws Exception{
		switch(command[2]){
		case "AVL":
			if(command[3].equals("RB")){
				setRbTree(new RedBlackTree<String>());
				for (String string : getAvlTree().toArrayList(command[1])) {
					getRbTree().insert(string);
				}
				System.out.println("cp avl -> rb");
			}else if(command[3].equals("BT")){
				setBTree(new BTree<String>());
				for (String string : getAvlTree().toArrayList(command[1])) {
					getBTree().insert(string);
				}
				System.out.println("cp avl -> bt");
			}else{
				System.err.println("Select a valid type of tree to paste!");
			}
			break;
		case "RB":
			if(command[3].equals("AVL")){
				setAvlTree(new AVLTree<String>());
				for (String string : getRbTree().toArrayList(command[1])) {
					getAvlTree().insert(string);
				}
				System.out.println("cp rb -> avl");
			}else if(command[3].equals("BT")){
				setBTree(new BTree<String>());
				for (String string : getRbTree().toArrayList(command[1])) {
					getBTree().insert(string);
				}
				System.out.println("cp rb -> bt");
			}else{
				System.err.println("Select a valid type of tree to paste!");
			}
			
			break;
		default:
			UnexpectedToken();
		}

	}
	//verifica qual operação será realizada na árvore rubro negra
	private void RBOperation(String[] command) throws Exception{
		switch(command[1]){
		case "NEW":
			System.out.println("new tree ");
			setRbTree(new RedBlackTree<String>());
			break;
		case "I":
			getRbTree().insert(command[2]);
			System.out.println("insertion "+command[2]);
			break;
		case "R":
			try{
				getRbTree().remove(getRbTree().search(command[2]));
			}catch(NullPointerException e){
				System.err.println("Valor não encontrado");
			}

			System.out.println("deletion "+command[2]);
			break;
		default:
			UnexpectedToken();
		}
	}
	//verifica qual operação será realizada na árvore B
	private void BTOperation(String[] command) throws Exception{
		switch(command[1]){
		case "NEW":
			if(command[2] != null){
				setBTree(new BTree<String>(Integer.parseInt(command[2])));
				System.out.println("new tree");
			}else{
				System.err.println("Please type a valid command! Type HELP to see the available commands");
			}
			break;
		case "I":
			System.out.println("insertion "+command[2]);
			getBTree().insert(command[2]);
			break;
		case "R":
			try{
				getBTree().remove(command[2]).length();
			}catch(NullPointerException e){
				System.err.println("Valor não encontrado");
			}

			System.out.println("deletion "+command[2]);
			break;
		default:
			UnexpectedToken();
		}
	}
	//verifica qual operação será realizada na árvore AVL
	private void AVLOperation(String[] command) throws Exception{
		switch(command[1]){
		case "NEW":
			System.out.println("new tree");
			this.setAvlTree(new AVLTree<String>());
			break;
		case "I":
			System.out.println("insertion "+command[2]);
			this.getAvlTree().insert(command[2]);
			break;
		case "R":
			try{
				System.out.println("deletion "+command[2]);
				this.getAvlTree().remove(command[2]);
			}catch(NullPointerException e){
				System.err.println("Valor não encontrado");
			}
			break;
		default:
			UnexpectedToken();
		}		
	}
	//essa função quebra o comando em no máximo 4 partes, onde cada uma conterá uma palavra
	private String[] getNextCommand(){
		int index = 0;
		String[] msg = sc.nextLine().split(" ");
		String[] cmd = new String[4];
		for(int i = 0; i<msg.length; i++){
			if(!msg[i].equals("") && index<cmd.length){
				cmd[index++] = msg[i].toUpperCase();
			}
		}
		//System.out.println(cmd[0]+" "+cmd[1]+" "+cmd[2]);
		return cmd;
	}

}
