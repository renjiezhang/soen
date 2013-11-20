package udpCommunicator;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

// Class to be used by the leader.
public class UdpFailureDetection {
	private static int udpTimeout;
	
	public UdpFailureDetection(final int udpTimeout) {
		UdpFailureDetection.udpTimeout = udpTimeout;
	}
	
	public static void failureDetection(int port) {
		//String ack = new String();
		DatagramSocket clientSocket = null;
		try {
			clientSocket = new DatagramSocket();
			     
			InetAddress IPAddress = InetAddress.getByName("localhost");      
			byte[] sendData = new byte[1024];       
			byte[] receiveData = new byte[1024];      
			String sentence = "AreYouAlive?";     
			sendData = sentence.getBytes();     
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);  
			clientSocket.send(sendPacket);
			clientSocket.setSoTimeout(udpTimeout);
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);    
			clientSocket.receive(receivePacket);
			//ack = new String(receivePacket.getData()).substring(0, receivePacket.getLength()); 
		} catch (SocketTimeoutException ex) {
			//TODO Exclude given member
		} catch (Exception ex) {
			//TODO Exclude given member -- 
		} finally {
			if (clientSocket!=null)
				clientSocket.close();
		}
	}

}
