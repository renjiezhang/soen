package frontEnd;

import idl.PoliceStation;
import idl.PoliceStationHelper;
import idl.PoliceStationPOA;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.xml.ws.Endpoint;

import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import server.UdpRecCountSvr;

import common.City;
import common.Fifo;
import common.LogTool;
import common.Message;
import common.MessageType;
import common.Record;
import common.RecordCriminal;
import common.RecordMissing;
import common.Storage;

public class FE extends PoliceStationPOA implements Runnable {
	final static int PORT = 7777;

	String ior;
	int id = 10001;

	static final String pathLog = "/tmp/policeStation";
	LogTool logtool;

	Document docPort;

	HashMap<City, ServerProcessInfo> serverInfos;

	Fifo fifo;

	@Override
	public boolean createCRecord(String firstName, String lastName,
			String description, String status, String badgeID) {
		this.log("createCRecord() get request from " + badgeID);

		RecordCriminal rec = new RecordCriminal(id++, firstName, lastName,
				description, Record.CRStatus.valueOf(status));

		Message m = new Message(MessageType.CreateCRecord, rec);

		fifo.snd(m);
		Message res = fifo.rcv();

		String msg = String.format(
				"[%s] create a criminal record: [%s %s - %s] %s", badgeID,
				firstName, lastName, status, description);
		this.log(msg);
		this.updateUI();
		return true;
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

		Message m = new Message(MessageType.CreateMRecord, new RecordMissing(
				idrec, firstName, lastName, address, cLastDate, lastLocation,
				Record.MRStatus.valueOf(status)));

		fifo.snd(m);
		fifo.rcv();

		String msg = String
				.format("[%s] create a missing record: [id: %s%05d][%s %s - %s] address: %s, last location: %s",
						badgeID, cityname, idrec, firstName, lastName, status,
						address, lastLocation);
		this.log(msg);
		this.updateUI();
		return true;
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

		msg = String.format("[%s] success to update a record: [%s - %s] %s",
				badgeID, recordID, lastName, newStatus);

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

	public void log(String msg) {
		this.logtool.log(msg);
	}

	JPanel jpanel;

	// JScrollPane spanel;

	FE() {

		/*
		 * add all police station leader information
		 */
		this.jpanel = new JPanel();
		this.jpanel.setLayout(new GridLayout(2, 1));

		JPanel jpServer = new JPanel();
		jpServer.setLayout(new GridLayout(City.values().length, 5));
		this.serverInfos = new HashMap<City, ServerProcessInfo>();
		for (City city : City.values()) {
			ServerProcessInfo info = new ServerProcessInfo(city);
			this.serverInfos.put(city, info);
			jpServer.add(info);
		}
		this.jpanel.add(jpServer);

		/*
		 * add the udp server( receive the new leader information) port
		 */

		this.jpanel.add(new JLabel(
				"UDP server receiving new Leader Process, Port: " + PORT));

		jpanel.setVisible(true);

		this.logtool = new LogTool(pathLog + "/city.log");

	}

	public static void main(String[] args) throws RemoteException, InvalidName,
			ServantAlreadyActive, WrongPolicy, ObjectNotActive,
			FileNotFoundException, AdapterInactive {
		JFrame fr = new JFrame();
		fr.setBounds(100, 100, 600, 600);
		fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container cpanel = fr.getContentPane();
		Container cp = fr.getContentPane();

		FE fe = new FE();

		fe.setJFrame(fr);
		fe.updateUI();
		cp.add(fe.jpanel);

		System.out.println(String.format("FrontEnd upd server is up"));

		new Thread(fe).start();

		// }
		fr.setVisible(true);

		DatagramSocket serverSocket = null;
		try {
			serverSocket = new DatagramSocket(PORT);
		} catch (SocketException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			return;
		}

		byte[] receiveData = new byte[2048];

		while (true) {
			DatagramPacket receivePacket = new DatagramPacket(receiveData,
					receiveData.length);
			try {
				serverSocket.receive(receivePacket);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			/*
			 * update the leader process information
			 */
			String sentence = new String(receivePacket.getData());

		}

	}

	public int secOfDay() {
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.SECOND) + c.get(Calendar.HOUR_OF_DAY) * 3600;
	}

	void msg(String msg) {
		JOptionPane.showMessageDialog(this.jpanel, msg);
	}

	public void updateUI() {

	}

	JFrame fr;

	public void setJFrame(JFrame fr) {
		this.fr = fr;
	}

	@Override
	public void run() {

		try {

			ORB orb = (ORB) ORB.init(new String[] {}, null);
			POA rootPOA;

			rootPOA = POAHelper.narrow(orb
					.resolve_initial_references("RootPOA"));

			byte[] id = rootPOA.activate_object(this);
			org.omg.CORBA.Object ref = rootPOA.id_to_reference(id);

			String ior = orb.object_to_string(ref);
			IORRepositoryInterface iorRepo = new IORRepository(ior);
			// iorRepo.saveIOR(city.name, ior);
			System.out
					.println(String
							.format("PoliceStation FrontEnd is up and running, ior: \n%s",
									ior));
			rootPOA.the_POAManager().activate();
			Endpoint.publish("http://localhost:" + IORRepository.PORT + "/ws",
					iorRepo);
			orb.run();

		} catch (InvalidName e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServantAlreadyActive e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WrongPolicy e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ObjectNotActive e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AdapterInactive e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public String getIDs(String lastname) {
		System.out.println("get request to getID, lastname:  " + lastname);
		//String ids = this.storage.getIDs(lastname);
		return "";
	}

}
