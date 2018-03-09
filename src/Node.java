import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

public class Node {

	final static Logger logger = Logger.getLogger(Node.class);
	static ConcurrentLinkedQueue<Message> buffer = new ConcurrentLinkedQueue<>();	//queue
	static int dNumber=0;		//d for every round
	static String myUID = "";
	static AtomicInteger round = new AtomicInteger(0);
	static int noOfNeighbors;
	static int sendingUID;		//x
	static int leaderCounter;	//c
	static boolean isLeaderCandidate = true;	//b
	static HashMap<String, ObjectOutputStream> outputStreamsMap = new HashMap<>();
	
	public static Sender sender;

	public static void main(String[] args) throws IOException {
		
			myUID=args[0];
			sendingUID = Integer.parseInt(myUID);
			Pair2 a= Pair2.get_details(myUID);
				
			HashMap node_details = a.node_details;
			HashMap node_neighbors= a.node_neighbors;
			
			ArrayList curr_node= (ArrayList) node_details.get(myUID);
			ArrayList node_neighbor= (ArrayList) node_neighbors.get(myUID);
			
			noOfNeighbors = node_neighbor.size();
			logger.debug("noOfNeighbors" + noOfNeighbors);
			logger.debug(node_neighbor);
			
			int[] neighborPorts = new int[noOfNeighbors];
			String[] neighborHosts = new String[noOfNeighbors];
			for(int i=0; i< noOfNeighbors; i++)
			{
				ArrayList temp= (ArrayList) node_details.get(node_neighbor.get(i));
				neighborHosts[i] = (String) temp.get(0);
				neighborPorts[i] = Integer.parseInt((String) temp.get(1));
			}
			
			Listener listener = new Listener(Integer.parseInt((String) curr_node.get(1)));
			Thread thread = new Thread(listener);
			thread.start();	
			
			Sender sender = new Sender(neighborHosts,neighborPorts, sendingUID);
			Message firstMessage = new Message();
			firstMessage.setDistance(dNumber);
			firstMessage.setxUID(sendingUID);
			firstMessage.setRound(round.intValue());
			sender.sendReceive(firstMessage);
	}
	
}

	
