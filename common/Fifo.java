package common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Fifo implements FifoInterf{

	@Override
	public Object snd(Serializable s, String host, int port) throws IOException, ClassNotFoundException {
		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName(host);
		
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		ObjectOutputStream oos=new ObjectOutputStream(bos);
		oos.writeObject(s);
		oos.close();
		DatagramPacket sendPacket = new DatagramPacket(bos.toByteArray(),
				bos.size(), IPAddress, port);
		
		clientSocket.send(sendPacket);
		
		ByteArrayInputStream bis=new ByteArrayInputStream(new byte[2048]);
		DatagramPacket receivePacket = new DatagramPacket(bos.toByteArray(),
				bos.size());
		clientSocket.receive(receivePacket);
		return new ObjectInputStream(bis).readObject(); 
		
		
	}

	

}
