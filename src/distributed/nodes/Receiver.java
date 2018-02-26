package distributed.nodes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Receiver implements Runnable {
	
	private Socket client;

	public Receiver(Socket client) {
		this.client = client;
	}

	public void run() {
		String line;
		BufferedReader in = null;
		PrintWriter out = null;

		try {
			/*while(Node.isProcessing) {
				try {
					Node.listenerSleeping = true;
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Node.listenerSleeping = false;*/
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			out = new PrintWriter(client.getOutputStream(), true);
			line = in.readLine();
			String[] messageReceived = line.split(":");
			System.out.println(messageReceived[0]+" : "+ messageReceived[1]+" : "+ messageReceived[2]);
			if(Node.blockingMapCurrent.containsKey(messageReceived[0])){
				String value = Node.blockingMapCurrent.get(messageReceived[0]);
				String[] valueArray = value.split(":");
				System.out.println("Current Round:" + valueArray[1]);
				System.out.println("Round received:" + messageReceived[2]);
				if(messageReceived[2] != valueArray[1]){
					System.out.println("*********Received Next Round Message");
					Node.blockingMapNext.put(messageReceived[0], messageReceived[1]+":"+messageReceived[2]);
				}
			} else {
				Node.blockingMapCurrent.put(messageReceived[0], messageReceived[1]+":"+messageReceived[2]);
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
