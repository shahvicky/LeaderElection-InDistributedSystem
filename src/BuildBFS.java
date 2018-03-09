import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

public class BuildBFS {

	private HashMap<String,ArrayList<String>> uidHostMap = new HashMap<>();
	private HashMap<String,ArrayList<String>> uidNeighborMap = new HashMap<>();
	private HashMap<String,ArrayList<String>> uidHostMapWithoutParent = new HashMap<>();
	private ArrayList<String> neighborList = new ArrayList<>();
	private String[] hostArray;
	private int[] portArray;
	final static Logger logger = Logger.getLogger(Sender.class);
	private int noOfAck = 0;
	

	
	public BuildBFS(HashMap<String,ArrayList<String>> uidHostMap, HashMap<String,ArrayList<String>> uidNeighborMap, String[] hosts, int[] ports) {
		this.uidHostMap = uidHostMap;
		this.uidNeighborMap = uidNeighborMap;
		neighborList = uidNeighborMap.get(Node.myUID);
	}
	
	public void startBFS() {
		if(Node.leader == Integer.parseInt(Node.myUID) && Node.parentUID == -1) {
			Node.parentUID = Integer.parseInt(Node.myUID);
			Node.dNumber = 0;
			Node.isMarked = true;
			sendSearchMsg();
		} else {
			while(!Node.isMarked) {
				if(!Node.buffer.isEmpty()) {
					for(Message msg: Node.buffer) {
						if(Node.isMarked) {
							break;
						}
						if(msg.getMsgType() == "SEARCH") {
							Node.parentUID = msg.getxUID();
							Node.depth = msg.getDistance()+1;
							Node.isMarked = true;
							sendSearchMsg();
							Node.buffer.remove(msg);
						} else {
							Node.buffer.offer(Node.buffer.poll());
						}
					}
				}
				//wait for some message to come or before going to the next step
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					logger.debug(e);
				}
			}
		}
		
		while(noOfAck != Node.noOfAckMsg) {
			if(!Node.buffer.isEmpty()) {
				for(Message msg: Node.buffer) {
					if(msg.getMsgType() == "SEARCH") {
						updateParentAndSendAck(msg);
						Node.buffer.remove(msg);
					} else if(msg.getMsgType() == "NEGATIVEACK") {
						noOfAck++;
						Node.buffer.remove(msg);
					} else if(msg.getMsgType() == "POSITIVEACK") {
						noOfAck++;
						updateChildList(msg);
						Node.buffer.remove(msg);
					}
				}
			}
		}
		
		
	}
	
	private void updateParentAndSendAck(Message msg){
		/*
		 * if parent is changing, send the NEGATIVEACK to the existing parent
		 * this will happen if I receive a message with distance less than (Node.depth-1),
		 * else send NEGATIVEACK to the one from which we received search*/
	}
	
	public void updateChildList(Message msg) {
		Node.childList.add(msg.getxUID());
	}
	
	private void sendSearchMsg() {
		String msgType = "SEARCH";
		Message msg = composeMsg(msgType);
		sendSearchMsgHelper(msg);
	}
	
	private void sendRejectMsg(){
		String msgType = "REJECT";
		Message msg = composeMsg(msgType);
		//sendRejectMessage(msg);
	}
	
	private void sendAckMsg() {
		String msgType = "ACK";
		Message msg = composeMsg(msgType);
		//sendMessage(msg);
	}
	
	private Message composeMsg(String msgType){
		Message msg = new Message();
		msg.setDistance(Node.dNumber);
		msg.setxUID(Integer.parseInt(Node.myUID));
		msg.setMsgType(msgType);
		return msg;
	}
	
	private void sendSearchMsgHelper(Message msg) {
		ObjectOutputStream outputStream = null;
		boolean scanning = true;
		for(int i=0; i< Node.noOfNeighbors; i++) {
			String key = null;
			while(scanning){
				try	{
					
					Socket clientSocket = new Socket(hostArray[i], portArray[i]);
					key = hostArray[i]+portArray[i];
					scanning = false;
					outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
				} catch (ConnectException e) {
  	    			logger.error("ConnectException: failed with" + hostArray[i] + " " + portArray[i]);
  	    			try {
  	    				Thread.sleep(2000);// 2 seconds
    				} catch (InterruptedException ie) {
    					ie.printStackTrace();
    				}
  				} catch (UnknownHostException e){
  					  logger.error("UnknownHostException"+ e);
  			    } catch (IOException e) {
  			    	logger.error("IOException" + e);
  			    }
			}
			try {
				outputStream.writeObject(msg);
				logger.debug(hostArray[i]+portArray[i]+ " sent "+ msg.toString());
			} catch (IOException e) {
				logger.error("IOException"+e);
			}
			//logger.debug("Inside scanning");
			scanning = true;
		}
	}
	
	
	
	
	
	
	
}
