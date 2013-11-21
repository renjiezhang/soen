package failureDetection;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import common.City;
import common.Machine;

// Class to be used by the leader.
public class UdpFailureDetection implements Runnable {
	
	private final FailureTimeouts failureTimeouts;
	private final Machine[] allMachines;
	private final Machine machine;
	
	public UdpFailureDetection(final FailureTimeouts failureTimeouts, final Machine[] machines, final Machine thisMachine) {
		this.failureTimeouts = failureTimeouts;
		this.allMachines = machines;
		this.machine = thisMachine;
	}
	
	@Override
	public void run() {
		if (this.machine.isLeader()) {
			memberFailureDetection();
		} else {
			udpListener();
		}
	}
		
	private void memberFailureDetection() {
		
		while (true) {
			for(int x = 0; x < this.allMachines.length; x++) {
				if (allMachines[x].isLeader() || !allMachines[x].isAlive()) {
					continue;
				}
				failureDetection(allMachines[x], failureTimeouts.memberTimeout);
			}
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
        	serverSocket = new DatagramSocket(this.machine.detectionPort);
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
	
	private void failureDetection(Machine machine, final int udpTimeout) {
		//String ack = new String();
		DatagramSocket clientSocket = null;
		try {
			clientSocket = new DatagramSocket();
			     
			InetAddress IPAddress = InetAddress.getByName(machine.host);      
			byte[] sendData = new byte[1024];       
			byte[] receiveData = new byte[1024];      
			String sentence = "AreYouAlive?";     
			sendData = sentence.getBytes();     
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, machine.detectionPort);  
			clientSocket.send(sendPacket);
			clientSocket.setSoTimeout(udpTimeout);
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);    
			clientSocket.receive(receivePacket);
			//ack = new String(receivePacket.getData()).substring(0, receivePacket.getLength()); 
		//} catch (SocketTimeoutException ex) {			
		} catch (Exception ex) {
			//Exclude given member
			machine.kill();
			System.out.println("Member at "+machine.host+" at port " + machine.detectionPort+ " is DEAD! RIP " + machine.host);
			if (clientSocket!=null)
				clientSocket.close();
			return;
		}
		if (clientSocket!=null)
			clientSocket.close();
		System.out.println("Member at "+machine.host+" at port " + machine.detectionPort+ " is STILL ALIVE!");
	}

}
