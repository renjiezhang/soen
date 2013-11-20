package frontEnd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import common.Record;

public class UdpRecCountSvr implements Runnable {


	FE ps;

	static public String query(String host, int port, String recType) throws IOException {
		
		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName(host);
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];
		
		sendData = recType.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData,
				sendData.length, IPAddress, port);
		clientSocket.send(sendPacket);
		DatagramPacket receivePacket = new DatagramPacket(receiveData,
				receiveData.length);
		clientSocket.receive(receivePacket);
		String strCount = new String(receivePacket.getData());
		System.out.println("FROM SERVER:" + strCount);
		return strCount.trim();
	}

	public UdpRecCountSvr(FE ps) {
		this.ps = ps;
		
	}

	@Override
	public void run() {
		DatagramSocket serverSocket = null;
		try {
			serverSocket = new DatagramSocket(ps.city.portUdp);
		} catch (SocketException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			return;
		}
		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];
		while (true) {
			DatagramPacket receivePacket = new DatagramPacket(receiveData,
					receiveData.length);
			try {
				serverSocket.receive(receivePacket);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String sentence = new String(receivePacket.getData());
			int count = 0;
			if (sentence.startsWith(Record.RecordType.CRIMINAL.toString())) {
				count = ps.storage.getCount(Record.RecordType.CRIMINAL);
			} else if (sentence
					.startsWith(Record.RecordType.MISSING.toString())) {
				count = ps.storage.getCount(Record.RecordType.MISSING);
			} else {
				count = -1;
			}

			// System.out.println("RECEIVED: " + sentence);
			InetAddress IPAddress = receivePacket.getAddress();
			int port = receivePacket.getPort();
			String rtrMsg = String.format("%s: %d", ps.city.name, count);
			sendData = rtrMsg.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData,
					sendData.length, IPAddress, port);
			try {
				serverSocket.send(sendPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}// - See more at:
			// http://systembash.com/content/a-simple-java-udp-server-and-udp-client/#sthash.pCXtUpGB.dpuf

	}
}
