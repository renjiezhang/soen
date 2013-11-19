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
import javax.swing.table.AbstractTableModel;
import javax.xml.ws.Endpoint;

import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import common.City;
import common.LogTool;
import common.Record;
import common.RecordCriminal;
import common.RecordMissing;
import common.Storage;

public class FE extends PoliceStationPOA implements Runnable {
	String ior;
	int id = 10001;

	@Override
	public boolean createCRecord(String firstName, String lastName,
			String description, String status, String badgeID) {
		this.log("createCRecord() get request from " + badgeID);
		// boolean isSuccess=false;
		boolean isSuccess = this.storage.addRecord(
				new RecordCriminal(id++, firstName, lastName, description,
						Record.CRStatus.valueOf(status)),
				Record.RecordType.CRIMINAL);
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
		Calendar cLastDate = Calendar.getInstance();
		cLastDate.setTimeInMillis(lastDate);
		boolean isSuccess = this.storage.addRecord(new RecordMissing(idrec,
				firstName, lastName, address, cLastDate, lastLocation,
				Record.MRStatus.valueOf(status)), Record.RecordType.MISSING);
		String msg = String
				.format("[%s] create a missing record: [id: %s%05d][%s %s - %s] address: %s, last location: %s",
						badgeID, this.city.name, idrec, firstName, lastName,
						status, address, lastLocation);
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

		return msg + "}";
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
		try {
			ORB orb = ORB.init(new String[] {}, null);
			IORRepositoryInterface iorRepo = new IORRepository(ior);
			String ior = iorRepo.getIOR();
			org.omg.CORBA.Object ref = orb.string_to_object(ior);
			PoliceStation anotherPS = PoliceStationHelper.narrow(ref);

			Record rec = this.storage.getRecord(recordID);

			if (rec.getClass() == RecordCriminal.class) {
				RecordCriminal crec = (RecordCriminal) rec;
				if (anotherPS.createCRecord(crec.fn, crec.ln, crec.desc,
						crec.getStatus(), badgeID)) {
					this.storage.removeRecord(recordID);
					String msg = String
							.format("Transfer criminal record (id: %s) to PoliceStation (%s): ",
									recordID, remoteStationServerName);
					this.log(msg);
					this.updateUI();
					return true;
				}
			} else if (rec.getClass() == RecordMissing.class) {
				RecordMissing mrec = (RecordMissing) rec;
				if (anotherPS.createMRecord(mrec.fn, mrec.ln,
						mrec.addrLastKnown,
						mrec.dateLastSeen.getTimeInMillis(),
						mrec.PlaceLastSeen, mrec.status.stauts, badgeID)) {
					this.storage.removeRecord(recordID);
					String msg = String
							.format("Transfer missing record (id: %s) to PoliceStation (%s): ",
									recordID, remoteStationServerName);
					this.log(msg);
					this.updateUI();
					return true;
				}
			} else {

			}

		} catch (IOException e) {

		}
		return false;
	}

	Storage storage;

	static final String pathLog = "/tmp/policeStation";
	LogTool logtool;

	public void log(String msg) {
		this.logtool.log(msg);
	}

	JPanel jpanel;
	JScrollPane spanel;

	// JTable tbl;

	City city;
	int port;

	FE() {
		this.jpanel = new JPanel();
		this.jpanel.setLayout(new GridLayout(2, 1));
		
		JPanel jpServer=new JPanel();
		jpServer.setLayout(new GridLayout(City.values().length, 5));
		this.serverInfos=new HashMap<City, ServerProcessInfo>();
		for (City city : City.values()) {
			ServerProcessInfo info = new ServerProcessInfo(city);
			this.serverInfos.put(city, info);
			jpServer.add(info);
		}
		this.jpanel.add(jpServer);
		this.city = City.MONTREAL;
		storage = new Storage();

		
		

		// fr.pack();

		jpanel.setVisible(true);

		this.logtool = new LogTool(pathLog + "/city.log");

	}

	HashMap<City, ServerProcessInfo>  serverInfos;

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

		UdpRecCountSvr svr = new UdpRecCountSvr(fe);
		Thread thSvr = new Thread(svr);
		thSvr.start();
		System.out.println(String.format("FrontEnd upd server is up"));

		new Thread(fe).start();

		// }
		fr.setVisible(true);

		while (true) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
			System.out.println(String.format(
					"PoliceStation[%s] is up and running, ior: \n%s",
					city.name, ior));
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
		String ids = this.storage.getIDs(lastname);
		return ids;
	}

}
