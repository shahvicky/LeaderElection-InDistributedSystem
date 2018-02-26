package distributed.nodes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JComponent;

public class Sender {
	private int[] portArray;
	
	public Sender(int[] ports, int uid) {
		//System.out.println("Inside Sender");
		this.portArray = Arrays.copyOfRange(ports, 0, ports.length);
	}
	
	/*public void messageConstruction() {
		while(BlockingMap.blockingMapCurrent.size() != Node.noOfNeighbors);
		int maxUID = Integer.MIN_VALUE;
		int maxD = Integer.MIN_VALUE;
		Iterator it = BlockingMap.blockingMapCurrent.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, String> keyPair = (Map.Entry)it.next();
			if(Integer.parseInt(keyPair.getKey()) > maxUID){
				maxUID = Integer.parseInt(keyPair.getKey());
				maxD = Integer.parseInt(keyPair.getValue());
			}
			it.remove();
		}
		BlockingMap.blockingMapCurrent = BlockingMap.blockingMapNext;
		BlockingMap.blockingMapNext = new ConcurrentHashMap<>();
		
		if(maxUID > Integer.parseInt(Node.sendingUID)){
			Node.sendingUID = Integer.toString(maxUID);
			Node.dNumber = Integer.toString(maxD + 1);
			BlockingMap.leaderCounter = 0;
		} else if (maxUID == Integer.parseInt(Node.sendingUID)) {
			int temp = Integer.parseInt(Node.dNumber);
			Node.dNumber = Integer.toString(maxD > temp ? maxD : temp);
			BlockingMap.leaderCounter++;
		} else {
			BlockingMap.leaderCounter = 0;
		}
		
		System.out.println("LEADER COUNTER: " + BlockingMap.leaderCounter);
		System.out.println("ROUND NUMBER: " );
		
		if(BlockingMap.leaderCounter == 3) {
			System.out.println("************" + Node.sendingUID +" is the LEADER");
		}else {
			sendMessage();
		}
		
	}
	*/

	public void messageConstruction() {
		
		Node.pulse += 1;
		if(Node.dNumber == -1) {
			sendMessage(Node.sendingUID, -1, Node.pulse);
		} else {
			while(Node.blockingMapCurrent.size() != Node.noOfNeighbors);
			System.out.println("Received msg from all neighbors");
			/*Node.isProcessing = true;
			while(!Node.listenerSleeping);*/
			int maxUID = Integer.MIN_VALUE;
			int maxD = Integer.MIN_VALUE;
			Iterator it = Node.blockingMapCurrent.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry<String, String> keyPair = (Map.Entry)it.next();
				if(Integer.parseInt(keyPair.getKey()) > maxUID){
					maxUID = Integer.parseInt(keyPair.getKey());
					String[] tempValue = keyPair.getValue().split(":");
					maxD = Integer.parseInt(tempValue[0]);
				}
				it.remove();
			}
			Node.blockingMapCurrent = Node.blockingMapNext;
			Node.blockingMapNext = new ConcurrentHashMap<>();
			
			if(maxUID > Node.sendingUID) {			//*here found 1 error** was checking with myUID
				Node.isLeaderCandidate = false;
				Node.sendingUID = maxUID;
				Node.dNumber = Node.pulse;
			}
			if(!Node.isLeaderCandidate){
				sendMessage(Node.sendingUID, Node.dNumber, Node.pulse);
			} 
			if(maxUID < Node.sendingUID) {
				System.out.println("Leader counter initiated");
				Node.leaderCounter = 1;
				sendMessage(Node.sendingUID, Node.dNumber, Node.pulse);
			} else {
				
				//maxUID  == Node.sendingUID
				if(maxD > Node.dNumber) {
					System.out.println("Recieving same UID and increased d");
					Node.dNumber = maxD;
					Node.leaderCounter = 0;
					sendMessage(Node.sendingUID, Node.dNumber, Node.pulse);
				} else { 	//z=d ==> maxD == Node.dNumber
					System.out.println("Recieving same UID and same d");
					Node.leaderCounter += 1;
				}
				if(Node.leaderCounter <=2) {
					sendMessage(Node.sendingUID, Node.dNumber, Node.pulse);
				} else if(Node.leaderCounter == 3) {
					System.out.println("Yaaayyyyyayay.... I am the leader");
					sendMessage(Node.sendingUID, -1, Node.pulse);
				}
			}
			
		}
		
		
		
	}
	
	public void sendMessage(int x,int d, int round) {
		Node.isProcessing = false;
		
		System.out.println("******************sendingUID: "+Node.sendingUID);
		System.out.println("******************dNumber: "+Node.dNumber);
		System.out.println("******************leaderCounter:" + Node.leaderCounter);
		boolean scanning = true;
		PrintWriter writer = null;
		for(int port:portArray) {
			while(scanning){
				try	{
					
					System.out.println("Trying");
					// Create a client socket and connect to server at 127.0.0.1 port 5000
					Socket clientSocket = new Socket("localhost",port);
					scanning = false;
					/* Create BufferedReader to read messages from server. Input stream is in bytes. 
						They are converted to characters by InputStreamReader.
						Characters from the InputStreamReader are converted to buffered characters by BufferedReader.
						This is done for efficiency purpose.
					*/

					// PrintWriter is a bridge between character data and the socket's low-level output stream
					writer = new PrintWriter(clientSocket.getOutputStream(), true);
					
				} catch (ConnectException e) {
  	    			System.out.println("ConnectException: failed to establish connections with" + "localhost:" + " " + port + " and trying again");
  	    			try {
  	    				Thread.sleep(2000);// 2 seconds
    				} catch (InterruptedException ie) {
    					ie.printStackTrace();
    				}
  				}catch (UnknownHostException e){
  					  e.printStackTrace();
  			    } catch (IOException e) {
  			    }
			}
			writer.println(x+":"+d+":"+round);
			System.out.println("Inside scanning");
			scanning = true;
		}  
		//calling to update UID and pulse
		if(d== -1) {
			//TODO start MST construction
			System.exit(0);
		}
		messageConstruction();
	}

}
