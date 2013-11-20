package udpCommunicator;

import java.net.DatagramPacket;
import java.net.DatagramSocket;


// Class to be used by members.
public class UdpListener implements Runnable {

	private int listeningPort;
	
	public UdpListener(int listeningPort) {
		this.listeningPort = listeningPort;
	}
		
	@Override
	public void run() {
		DatagramSocket serverSocket = null;
        try {
        	serverSocket = new DatagramSocket(this.listeningPort);
        	byte[] sendData = new byte[1024];       
        	byte[] receiveData = new byte[1024];
        	while (true) {
        		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        		serverSocket.receive(receivePacket);                   
        		String reply = "I'm alive";
        		sendData = reply.getBytes();
        		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
        		serverSocket.send(sendPacket);
        	}
        } catch (Exception ex) {
        	System.out.println("Exception: " + ex.getMessage());
        } finally {
        	if (serverSocket != null)
        		serverSocket.close();
        }		
	}
}
