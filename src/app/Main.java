package app;
//essa classe instanciará o terminal.
public class Main {
	private static App terminal;
	
	public static void main(String[] args) {
		terminal = new App();
		terminal.run();
	}
}
