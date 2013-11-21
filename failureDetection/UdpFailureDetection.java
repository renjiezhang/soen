package failureDetection;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import common.City;

// Class to be used by the leader.
public class UdpFailureDetection implements Runnable {
	
	private final FailureTimeouts failureTimeouts;
	private final int listeningPort;
	private MembershipState membershipState;
	
	public UdpFailureDetection(final FailureTimeouts failureTimeouts, final int listeningPort, MembershipState membershipState) {
		this.failureTimeouts = failureTimeouts;
		this.listeningPort = listeningPort;
		this.membershipState = membershipState;
	}
	
	@Override
	public void run() {
		if (membershipState == MembershipState.LEADER) {
			memberFailureDetection();
		} else {
			udpListener();
		}
	}
		
	private void memberFailureDetection() {
		
		while (true) {
			failureDetection("localhost", City.MONTREAL, failureTimeouts.memberTimeout);
			failureDetection("localhost", City.TORONTO, failureTimeouts.memberTimeout);
			try {
				Thread.sleep(failureTimeouts.detectionCycleTimer);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	private void udpListener() {
		DatagramSocket serverSocket = null;
        try {
        	serverSocket = new DatagramSocket(this.listeningPort);
        	byte[] sendData = new byte[1024];       
        	byte[] receiveData = new byte[1024];
        	while (true) {
        		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        		serverSocket.receive(receivePacket);
        		serverSocket.setSoTimeout(failureTimeouts.leaderTimeout);
        		String reply = "I'm alive";
        		sendData = reply.getBytes();
        		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
        		serverSocket.send(sendPacket);
        		System.out.println("Leader is still alive!");
        	}
        } catch (Exception ex) {
        	//TODO Start election process
			System.out.println("Leader is DEAD!");
			if (serverSocket != null)
        		serverSocket.close();
        }
	}
	
	private void failureDetection(String hostname, City city, final int udpTimeout) {
		//String ack = new String();
		DatagramSocket clientSocket = null;
		try {
			clientSocket = new DatagramSocket();
			     
			InetAddress IPAddress = InetAddress.getByName(hostname);      
			byte[] sendData = new byte[1024];       
			byte[] receiveData = new byte[1024];      
			String sentence = "AreYouAlive?";     
			sendData = sentence.getBytes();     
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, city.portUdp);  
			clientSocket.send(sendPacket);
			clientSocket.setSoTimeout(udpTimeout);
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);    
			clientSocket.receive(receivePacket);
			//ack = new String(receivePacket.getData()).substring(0, receivePacket.getLength()); 
		//} catch (SocketTimeoutException ex) {			
		} catch (Exception ex) {
			//TODO Exclude given member
			System.out.println("Member "+city.name+" at port " + city.portUdp + " is DEAD! RIP " + city.portUdp);
			if (clientSocket!=null)
				clientSocket.close();
			return;
		}
		if (clientSocket!=null)
			clientSocket.close();
		System.out.println("Member "+city.name+" at port " + city.portUdp + " is STILL ALIVE!");
	}

}
