package distributed.nodes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

class pair2{
	public HashMap node_details;
	public HashMap node_neighbors;	
	public pair2( HashMap node_details1, HashMap node_neighbors1){
		 node_details=  node_details1;
		 node_neighbors=node_neighbors1;
	}
}

public class Node {
	
	static int noOfNeighbors;
	static int dNumber= 0;		//d acc to Peleg
	static int sendingUID;		//x acc to Peleg
	static ConcurrentHashMap<String, String> blockingMapCurrent = new ConcurrentHashMap<>();
	static ConcurrentHashMap<String, String> blockingMapNext = new ConcurrentHashMap<>();
	static int leaderCounter;			//c acc to Peleg
	static boolean isLeaderCandidate = true;		//b acc to Peleg
	static int pulse = 1;				//p acc to Peleg
	static String myUID = "";			//i acc to Peleg
	static boolean isProcessing = false;
	static boolean listenerSleeping = false;
	
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public static pair2 get_details(String key) throws IOException
	{
		
		  File file = new File("C:\\Users\\shahvicky1992\\Desktop\\DistributedProject\\dummy_copy.txt");
		  
		 
		  BufferedReader br1 = new BufferedReader(new FileReader(file)); 
		  br1.readLine();
		  
		  int count = Integer.parseInt(br1.readLine());
		 
		  String st;
		  br1.readLine();
		  br1.readLine();
		  
		  HashMap<String,ArrayList<String>>HS= new HashMap<String, ArrayList<String>>();
		  while (count>0)
		  {
			st=br1.readLine();
			String[] a= st.split(" +");
			HS.put(a[1],new ArrayList<String>());
			HS.get(a[1]).add(a[2]);
			HS.get(a[1]).add(a[3]);
		    count=count-1;
		  }		
		
		  br1.readLine();
		  br1.readLine();
		  HashMap<String,ArrayList<String>> HS_neighbor=new HashMap<String,ArrayList<String>>(); 
		  
		  st=br1.readLine();
		  while(st != null)
		  {
			String[] a= st.split(" +");
			HS_neighbor.put(a[1],new ArrayList<String>());
			int i=2;
			while(i<a.length)
			{
				HS_neighbor.get(a[1]).add(a[i]);
				i=i+1;
			}
			st=br1.readLine();
		  }
		  br1.close();
		
		pair2 s = new pair2(HS,HS_neighbor);
		return s;
	}
	
	
	public static void main(String[] args) throws IOException 
	{
		
		myUID=args[0];
		sendingUID = Integer.parseInt(myUID);
		pair2 a= get_details(myUID);
			
		HashMap node_details = a.node_details;
		HashMap node_neighbors= a.node_neighbors;
		
		ArrayList curr_node= (ArrayList) node_details.get(myUID);
		ArrayList node_neighbor= (ArrayList) node_neighbors.get(myUID);
		
		noOfNeighbors = node_neighbor.size();
		System.out.println("noOfNeighbors" + noOfNeighbors);
		System.out.println(node_neighbor);
		
		Listener listener = new Listener(Integer.parseInt((String) curr_node.get(1)));
		Thread thread = new Thread(listener);
		thread.start();	
		int[] neighbors = new int[noOfNeighbors];
		for(int i=0; i< noOfNeighbors; i++)
		{
			ArrayList temp= (ArrayList) node_details.get(node_neighbor.get(i));
			neighbors[i] = Integer.parseInt((String) temp.get(1));
		}
		Sender sender = new Sender(neighbors, sendingUID);
		sender.sendMessage(sendingUID, dNumber, pulse);
		
		
	}
}