package server;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import common.City;
import common.LogTool;
import common.Record;
import common.Record.RecordType;
import common.RecordCriminal;
import common.RecordMissing;
import common.Storage;

public class ServerProcess extends JFrame implements
		idl.PoliceStationOperations, ActionListener, Runnable {
	//City city;
	Storage storage;

	int id;

	void updateUI() {

	}

	void log(String msg) {

	}

	@Override
	public boolean createCRecord(String firstName, String lastName,
			String description, String status, String badgeID) {
		this.log("createCRecord() get request from " + badgeID);
		// boolean isSuccess=false;
		boolean isSuccess = this.storage.addRecord(
				new RecordCriminal(id++, firstName, lastName, description,
						common.Record.CRStatus.valueOf(status)),
				common.Record.RecordType.CRIMINAL);
		String msg = String.format(
				"[%s] create a criminal record: [%s %s - %s] %s", badgeID,
				firstName, lastName, status, description);
		this.log(msg);
		this.updateUI();
		return isSuccess;
	}

	@Override
	public boolean createMRecord(String firstName, String lastName,
			String address, long lastDate, String lastLocation, String status,
			String badgeID) {
		this.log("createMRecord() get request from " + badgeID);
		int idrec = id++;
		String cityname = badgeID.substring(2);
		Calendar cLastDate = Calendar.getInstance();
		cLastDate.setTimeInMillis(lastDate);
		boolean isSuccess = this.storage.addRecord(new RecordMissing(idrec,
				firstName, lastName, address, cLastDate, lastLocation,
				Record.MRStatus.valueOf(status)), Record.RecordType.MISSING);
		String msg = String
				.format("[%s] create a missing record: [id: %s%05d][%s %s - %s] address: %s, last location: %s",
						badgeID, cityname, idrec, firstName, lastName, status,
						address, lastLocation);
		this.log(msg);
		this.updateUI();
		return isSuccess;
	}

	@Override
	public String getRecordCounts(String recType) {
		this.log("getRecordCounts() get request");
		String msg = "{";
		for (City city : City.values()) {
			try {
				String _msg = UdpRecCountSvr.query("localhost", city.portUdp,
						recType) + ";\n";
				msg += _msg;
				this.log("get count, " + _msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return msg + "";
	}

	@Override
	public String editCRecord(String lastName, String recordID,
			String newStatus, String badgeID) {
		String msg = String.format("fail to update a record: [%s - %s] %s",
				recordID, lastName, newStatus);
		if (this.storage.updateRecord(recordID, lastName, newStatus)) {
			msg = String.format(
					"[%s] success to update a record: [%s - %s] %s", badgeID,
					recordID, lastName, newStatus);
		}
		this.log(msg);
		this.updateUI();
		return null;
	}

	@Override
	public boolean transferRecord(String recordID,
			String remoteStationServerName, String badgeID) {

		this.log("transferRecord() get request from " + badgeID
				+ ", record id: " + recordID + ", destination: "
				+ remoteStationServerName);
		System.out.println("transferRecord() get request from " + badgeID
				+ ", record id: " + recordID + ", destination: "
				+ remoteStationServerName);

		return false;
	}

	@Override
	public String getIDs(String lastname) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	City.CitiesComponent city;
	JButton btnStartUDP;
	public ServerProcess() {
		super();
		this.setLayout(new GridLayout(2, 1));
		this.storage = new Storage();
		
		
		this.city=new City.CitiesComponent();
		
		this.add(city);
		this.btnStartUDP=new JButton("Start UDP Server");
		this.btnStartUDP.addActionListener(this);
		this.add(this.btnStartUDP);
		
		
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(30, 30, 600, 400);
		this.setVisible(true);

	}

	static public void main(String[] args) {
		new ServerProcess();
	}

	Thread t;
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==this.btnStartUDP){
			this.btnStartUDP.setEnabled(false);
			this.t=new Thread(this);
			this.t.start();
		}
		
	}

	@Override
	public void run() {
		DatagramSocket serverSocket = null;
		try {
			serverSocket = new DatagramSocket(this.city.selection.portUdp);
		} catch (SocketException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			return;
		}
		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];
		ByteArrayInputStream bis=new ByteArrayInputStream(receiveData);
		
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
				count = this.storage.getCount(Record.RecordType.CRIMINAL);
			} else if (sentence
					.startsWith(Record.RecordType.MISSING.toString())) {
				count = this.storage.getCount(Record.RecordType.MISSING);
			} else {
				count = -1;
			}

			// System.out.println("RECEIVED: " + sentence);
			InetAddress IPAddress = receivePacket.getAddress();
			int port = receivePacket.getPort();
			String rtrMsg = String.format("%s: %d", this.city.selection.name, count);
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
