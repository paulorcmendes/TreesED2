package app;

import java.util.ArrayList;
import java.util.Scanner;

import avlTree.AVLTree;
import btree.BTree;
import redBlackTree.RedBlackTree;

public class App<T> {
	private RedBlackTree<String> rbTree;
	private AVLTree<String> avlTree;
	private BTree<String> bTree;
	private Scanner sc;

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

	private void InterpretCommand(String[] command) throws Exception{
		switch(command[0]){
		case "VL":
			System.out.println("AVL");
			AVLOperation(command);
			break;
		case "BT":			
			System.out.println("BTree");
			BTOperation(command);
			break;
		case "RB":
			System.out.println("RedBlack");
			RBOperation(command);
			break;
		case "PRINT":
			System.out.println("Print");
			PrintOperation(command);
			break;
		case "COPY":
			System.out.println("COPY");
			CopyOperation(command);
			break;
		case "HELP":
			help();
			break;
		default:
			UnexpectedToken();
		}
	}

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

	private void printArrayList(ArrayList<String> arrayList) {
		if(arrayList == null){
			System.err.println("Ã�rvore Vazia");
			return;
		}
		for(String key : arrayList){
			System.out.println(key);
		}

	}
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
				"<number>: a natural number greater than 2\n\n";

		System.out.print(msg);
	}

	private void UnexpectedToken() {
		System.err.println("unexpected token - Type HELP to see the available commands");

	}

	private void CopyOperation(String[] command) throws Exception{
		switch(command[2]){
		case "AVL":
			if(command[3].equals("RB")){
				setRbTree(new RedBlackTree<String>());
				for (String string : getAvlTree().toArrayList(command[1])) {
					getRbTree().insert(string);
				}
				System.out.println("cp avl in rb");
			}else if(command[3].equals("BT")){
				setBTree(new BTree<String>());
				for (String string : getAvlTree().toArrayList(command[1])) {
					getBTree().insert(string);
				}
				System.out.println("cp avl in bt");
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
				System.out.println("cp rb in avl");
			}else if(command[3].equals("BT")){
				setBTree(new BTree<String>());
				for (String string : getRbTree().toArrayList(command[1])) {
					getBTree().insert(string);
				}
				System.out.println("cp rb in bt");
			}else{
				System.err.println("Select a valid type of tree to paste!");
			}
			
			break;
		default:
			UnexpectedToken();
		}

	}

	private void RBOperation(String[] command) throws Exception{
		switch(command[1]){
		case "NEW":
			System.out.println("new tree");
			setRbTree(new RedBlackTree<String>());
			break;
		case "I":
			getRbTree().insert(command[2]);
			System.out.println("insertion"+command[2]);
			break;
		case "R":
			try{
				getRbTree().remove(getRbTree().search(command[2]));
			}catch(NullPointerException e){
				System.err.println("Valor nÃ£o encontrado");
			}

			System.out.println("deletion"+command[2]);
			break;
		default:
			UnexpectedToken();
		}
	}

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
			System.out.println("insertion"+command[2]);
			getBTree().insert(command[2]);
			break;
		case "R":
			try{
				getBTree().remove(command[2]).length();
			}catch(NullPointerException e){
				System.err.println("Valor nÃ£o encontrado");
			}

			System.out.println("deletion"+command[2]);
			break;
		default:
			UnexpectedToken();
		}
	}

	private void AVLOperation(String[] command) throws Exception{
		switch(command[1]){
		case "NEW":
			System.out.println("new tree");
			this.setAvlTree(new AVLTree<String>());
			break;
		case "I":
			System.out.println("insertion"+command[2]);
			this.getAvlTree().insert(command[2]);
			break;
		case "R":
			try{
				System.out.println("deletion"+command[2]);
				this.getAvlTree().remove(command[2]);
			}catch(NullPointerException e){
				System.err.println("Valor nÃ£o encontrado");
			}
			break;
		default:
			UnexpectedToken();
		}		
	}

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
