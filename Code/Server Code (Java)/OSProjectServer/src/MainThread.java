import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Enumeration;

public class MainThread extends Thread {
	private Thread        t;
	private String        threadName;        //thread name
	private int           totalChunks;       //total number of chunks
	private static String pathOfChunks;      //path where the chunks are stored
	private int           startPortNumber;   //Starting port number
	private static int    clientPortNumber;  //POrt number to be sent to client
	private static String ServerIP;          //IP address of the server
	private static int    ListenPORT = 9999; //default listening port
	
	private static ServerSocketChannel ssc;  //ServerSocketChannel object
	public static final String GREETING = "Hello I must be going.\r\n";
	
	private static int chunkNumeber = 1;
	
	private static TotalCharacterCount GlobalCount;
	

	// Constructor
	MainThread(String name, int count, String path, int port)
	{
		this.threadName = name;
		this.totalChunks = count;
		this.pathOfChunks = path;
		this.startPortNumber = port;
		GlobalCount = new TotalCharacterCount();
		
		System.out.println("--Creating " +  threadName );
		System.out.println(threadName +" will handle "+totalChunks +" chunks at " +pathOfChunks);
	}
	
	
	public void run() 
	{
		System.out.println("--Running " +  threadName );
	    try 
	    {    
	    	
	    	clientPortNumber = startPortNumber;
	    	
	    	//get the server IP address
	    	ServerIP = getFilterIPAddresses();
	    	
	    	//SAve the IP Address and the default port on the could
	    	saveServerIPToCloud();
	    	
	    	//start the Server for sending the IP and port to the client
	    	startServer();
	    	
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
	 * Get the Server IP Address 
	 */
	public static String getFilterIPAddresses() {
		String myIP = null;
		Enumeration<?> e;
		
		try {
			
			e = NetworkInterface.getNetworkInterfaces();
			
				//for each IP address check for the valid IP address
			while(e.hasMoreElements()) {
				NetworkInterface n = (NetworkInterface) e.nextElement();
				
				//get all the available IP addresses of the Server 
				Enumeration<?> ee = n.getInetAddresses();
				 
				//get the valid IP address 
				while (ee.hasMoreElements()) {
					InetAddress i = (InetAddress) ee.nextElement();	  
					if(i.getHostAddress().contains(".")) {
					  if(!i.getHostAddress().contains("127.0.")) {
						  myIP = i.getHostAddress();
						  System.out.println(myIP);
					  }
					}
				}
			}
		  } catch (SocketException e2) {
			// TODO Auto-generated catch block
				e2.printStackTrace();
		  }
		return myIP;
	}
	  
	/*
	 * save the IP Address and default port on the cloud
	 */
	private static void saveServerIPToCloud() throws IOException {
		// set up the command and parameter
		String Port = Integer.toString(ListenPORT);
		String pythonScriptPath = "/home/nishant/JavaWorkspace/OSProjectServer/src/SaveServerIP.py";
		String[] cmd = new String[6];
		cmd[0] = "python"; // check version of installed python: python -V
		cmd[1] = pythonScriptPath;
		cmd[2] = "NishantServer";
		cmd[3] = ServerIP;
		cmd[4] = Port; //port
		cmd[5] = "true"; //running
		 
		// create runtime to execute external command
		Runtime rt = Runtime.getRuntime();
		Process pr = rt.exec(cmd);
		 
		// retrieve output from python script
		BufferedReader bfr = new BufferedReader(new InputStreamReader(pr.getInputStream()));
		String line = "";
		
		while((line = bfr.readLine()) != null) {
		// display each output line form python script
			System.out.println(line);
		}
	}
	
	//Start server
	  public static void startServer() throws IOException, InterruptedException {
		  
		  ByteBuffer buffer = ByteBuffer.wrap(GREETING.getBytes());
	  
		  try {
			  //create a ServerSocketChannel connection using default port number 
			  ssc = ServerSocketChannel.open();
			  
			  //SErver is connected with the mobile using the default listening port
			  ssc.socket().bind(new InetSocketAddress(ListenPORT));
			  
			  //enable non blocking connection
			  ssc.configureBlocking(false);
			  
		  } catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		  }
		  
		  int chunkNumber2 = 1;
		  
		  while (true) {
		      System.out.println("Waiting for New mobile");
		      
		      //mobile accepts the connection
		      SocketChannel sc = ssc.accept();
		      
		      if (sc == null) {
		        Thread.sleep(2000);
		      } 
		      else {
		        System.out.println("Incoming connection from: " + sc.socket().getRemoteSocketAddress());
		        
		        //put the port number to be sent to mobile in the buffer
		        String PortNumberToSend = Integer.toString(clientPortNumber);
		        buffer = ByteBuffer.wrap(PortNumberToSend.getBytes());
		        
		        //create new thread for each mobile connection
		        new ClientThread("ClientThread",pathOfChunks,clientPortNumber,chunkNumber2,GlobalCount).start();
		        System.out.println("started");
		       
		        clientPortNumber++;
		        chunkNumeber++;
		        chunkNumber2++;
		        
		        buffer.rewind();

		        //send the new connection port number to the mobile
		        while(buffer.hasRemaining()) {
		            sc.write(buffer);
		        	System.out.println("Outgoing Buffer " +buffer);
		        }
		        
		        //close ServerSocketChannel
		        sc.close();
		      }
		  }
	}
}
