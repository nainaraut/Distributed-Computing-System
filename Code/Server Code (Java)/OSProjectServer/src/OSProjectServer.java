
public class OSProjectServer {

	public static void main(String[] args) {
		
		//new thread for splitting the user input file
		SplitFileThread T1 = new SplitFileThread("File Split Thread");
		T1.start();
	}
}
