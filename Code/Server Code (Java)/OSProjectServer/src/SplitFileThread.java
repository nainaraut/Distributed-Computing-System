import java.awt.Container;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;

public class SplitFileThread extends Thread {
	private Thread        t;
	private String        threadName;             //name of the thread
	private static String chunkPath;              //path of the chunk files stored
	private static int    totalChunks;            //total number of files splitted
	private static int    countMainThreads = 0;   //number of times this thread is called
	//List of object of MainThread class
	private static ArrayList<MainThread> listMainThreads = new ArrayList<MainThread>();
	private static int portStart = 10000;         //initialize the stating port
	
	
	// Constructor
	SplitFileThread(String name)
	{
		this.threadName = name;
		System.out.println("--Creating " +  threadName );
	}
	
	public void run() 
	{
		File selectedFile;
		System.out.println("--Running " +  threadName );
	    try 
	    {
	    	//Open UI for file chooser
	    	Container c = new Container();
	    	JFileChooser fileChooser = new JFileChooser();
	    	fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
	    	int result = fileChooser.showOpenDialog(c);
	    	
	    	//after file is selected
	    	if (result == JFileChooser.APPROVE_OPTION) {
	    		
	    		//get the selected file
	    		selectedFile = fileChooser.getSelectedFile();
	    		System.out.println("Selected file: " + selectedFile.getAbsolutePath());
	    		
	    		//split the file and get the count of total files created
	    		totalChunks = splitFile(selectedFile);
	    		System.out.println("Total " +  totalChunks +" chunks created at "
	    				+chunkPath );
	    		
	    		//count the number of times the main thread is called
	    		countMainThreads++;
	    		String MainThreadID = Integer.toString(countMainThreads);
	    		
	    		//calculate the starting port for each input file
	    		portStart = portStart + listMainThreads.size() * 2000;
	    		
	    		/*add the name,total number of files splitted, the path of the chunks and starting port
	    		 to the list of the MainThread
	    		 */
	    		listMainThreads.add(
	    				new MainThread(
	    						"Main_Thread-"+MainThreadID,
	    						totalChunks,
	    						chunkPath,
	    						portStart
	    						));
	    		
	    		//start the thread of the MainThread class
	    		listMainThreads.get(listMainThreads.size()-1).start();
	    	}
	    	
	    } 
	    catch (Exception e) 
	    {
	    	System.out.println("Exception: " +  e);
	    }
	    
	   
	    System.out.println("--Exiting " +  threadName);
	}
	
	@Override
	public void start ()
	{
		System.out.println("--Starting " +  threadName );
	    if (t == null)
	    {
	    	t = new Thread (this, threadName);
	        t.start ();
	     }
	}
	
	/*
	 * split the given file into smaller files
	 */
	private static int splitFile(File file){
		int count = 0;
		String line = null;
		int chunkNumer = 0;
		
		try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = 
                new FileReader(file);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);
            
            String chunkfileContent = "";
            
            //for each line in the input file
            while((line = bufferedReader.readLine()) != null) {
            	
            	//increment the count for each line
            	count++;
            	
            	//get the chunk number(20 line for each split file)
            	//chunkNumer = count;
            	
            	//initialize the path where we need to store the created chunk file
            	String extentionRemoved = file.getName().split("\\.")[0];
            	chunkPath = "/home/nishant/Documents/OS Project/chunks"+extentionRemoved+"/";
            	
            	File theDir = new File(chunkPath);
            	
            	// if the directory does not exist, create it
            	if (!theDir.exists()) {
            	    System.out.println("creating new directory: ");
            	    boolean result = false;

            	    try{
            	        theDir.mkdir();
            	        result = true;
            	    } 
            	    catch(SecurityException se){
            	        //handle it
            	    }        
            	    if(result) {    
            	        System.out.println("DIR created");  
            	    }
            	}
            	
            	//add each line to the variable chunkfileContent
            	chunkfileContent = chunkfileContent +"\n"+ line;
            	
            	//after 20 lines added to the chunkfileContent
            	if(count%20 == 0) {
            		
            		//write the lines to the chunk file 
            		writeLineToFile(chunkfileContent,chunkPath,chunkNumer);
            		chunkfileContent = "";
            		chunkNumer++;
            	}
            	
            }  
            
            //write the remaining lines to the last chunk file
            writeLineToFile(chunkfileContent,chunkPath,chunkNumer);
            
            // Always close files.
            bufferedReader.close();         
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                		file + "'");                
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + file + "'");                  
        }   
		
		return chunkNumer;
	}
	
	/*
	 * Write lines to the spit file
	 */
	private static void writeLineToFile(String line, String path, int chunkNumber) {
		
		String chunkNumberString = Integer.toString(chunkNumber);
		
		// The name of the file to be written to
        String fileName = path +chunkNumberString +".txt";
        
        //create the file object
        File file = new File(fileName);
        
		try {
            // Assume default encoding.
            FileWriter fileWriter =new FileWriter(file);

            // Always wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            
            // append a newline character.
            bufferedWriter.write(line);
            bufferedWriter.newLine();
            
            // Always close files.
            bufferedWriter.close();
        }
        catch(IOException ex) {
            System.out.println(
                "Error writing to file '"
                + fileName + "'");
        }
		
	}
	
	
}
