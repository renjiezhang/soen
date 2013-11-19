package client;

import idl.PoliceStationHelper;
import idl.PoliceStationOperations;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.omg.CORBA.ORB;

import common.City;
import common.LogTool;
import common.Record;
import common.Record.RecordType;

import frontEnd.IORRepository;
import frontEnd.IORRepositoryInterface;

public class Officer extends JFrame implements ActionListener, DocumentListener {
	static final String pathLog = "/tmp/policeOfficer";
	LogTool logtool;

	static final int x = 300, y = 200, w = 300, h = 600;

	public Officer() throws HeadlessException {
		super("PoliceOfficer login");
		this.setBounds(x, y, w, h);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	public String getIOR() throws IOException {
		IORRepositoryInterface iorRepo = null;
		try {
			URL url = new URL(this.iorUrl);

			QName qname = new QName("http://frontEnd/", "IORRepositoryService");

			Service service = Service.create(url, qname);

			iorRepo = service.getPort(IORRepositoryInterface.class);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String tt = String.format("[PoliceStaiton: %s, Police Id: %s] logon",
				this.city.name, this.idPolice);
		this.setTitle(tt);

		return iorRepo.getIOR();
	}

	JButton btnLogin;

	Document docPoliceId, docIORUrl;

	String idPolice;

	City city;

	public void showLogin() {
		JTextField txtPoliceId, txtIORUrl;
		Container cp = this.getContentPane();
		cp.setLayout(new GridLayout(3, 2, 30, 60));
		JLabel lbId = new JLabel("Police Id");

		txtPoliceId = new JTextField("Montreal888");
		docPoliceId = txtPoliceId.getDocument();
		docPoliceId.addDocumentListener(this);

		txtIORUrl = new JTextField("http://localhost:" + IORRepository.PORT
				+ "/ws");
		docIORUrl = txtIORUrl.getDocument();
		docIORUrl.addDocumentListener(this);

		btnLogin = new JButton("Login");
		btnLogin.addActionListener(this);

		cp.add(lbId);
		cp.add(txtPoliceId);
		cp.add(new JLabel("IOR URL: "));
		cp.add(txtIORUrl);

		cp.add(btnLogin);
		this.setBounds(80, 80, 600, 400);
		
		//this.repaint();
		this.setTitle("PoliceOfficer login");

		this.setVisible(true);
		this.validateLogin();

	}

	JTextArea msgArea;
	JTextField fn, ln, desc, addr, loc, year, month, day, idRecord, lnUpdate,
			idRecordForTransfer, psTo, lnToQry;
	JComboBox recStatus_create, recStatus_update, recType_create,
			recType_count;
	JButton btnSubmit, btnLogout;
	JPanel jpCreateRec, jpEdit, jpGetCount, jpTransfer, jpQry;

	public void fillPaneCreateRecHead(JPanel jpCreateRec) {
		// GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.PAGE_START;
		jpCreateRec.add(new JLabel("Frist Name"), c);
		if (fn == null)
			fn = new JTextField("Tom");
		jpCreateRec.add(fn);

		jpCreateRec.add(new JLabel("Last Name"), c);
		if (ln == null)
			ln = new JTextField("Simpson");
		jpCreateRec.add(ln);
		jpCreateRec.add(new JLabel("Record Type"), c);
		if (recType_create == null) {
			recType_create = new JComboBox(Record.RecordType.values());
			recType_create.addActionListener(this);
			recType_create.setSelectedIndex(0);
		}
		jpCreateRec.add(recType_create);

	}

	JTabbedPane tp;

	@SuppressWarnings("unchecked")
	public void showControlPanel() {
		tp = new JTabbedPane();

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.PAGE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		jpCreateRec = new JPanel(new GridLayout(4, 2));
		this.fillPaneCreateRecHead(jpCreateRec);

		jpGetCount = new JPanel(new GridLayout(2, 2));
		jpGetCount.add(new JLabel("Record Type"));
		this.recType_count = new JComboBox(Record.RecordType.values());
		jpGetCount.add(this.recType_count);

		jpEdit = new JPanel(new GridLayout(5, 2));
		jpEdit.add(new JLabel("Last Name"));
		this.lnUpdate = new JTextField();
		jpEdit.add(lnUpdate);
		jpEdit.add(new JLabel("Record ID"), c);
		this.idRecord = new JTextField();
		jpEdit.add(this.idRecord);

		jpEdit.add(new JLabel("new status: "));
		this.recStatus_update = new JComboBox(new String[] {
				Record.MRStatus.Found.toString(),
				Record.MRStatus.Missing.toString(),
				Record.CRStatus.Captured.toString(),
				Record.CRStatus.OnTheRun.toString() });
		jpEdit.add(this.recStatus_update);

		jpTransfer = new JPanel(new GridLayout(2, 2));
		jpTransfer.add(new JLabel("Record ID"), c);
		this.idRecordForTransfer = new JTextField();
		jpTransfer.add(this.idRecordForTransfer);

		jpTransfer.add(new JLabel("Destionation PoliceStation: "));
		this.psTo = new JTextField("Quebec");
		jpTransfer.add(this.psTo);

		jpQry = new JPanel(new GridLayout(2, 2));
		jpQry.add(new JLabel("last name"));
		this.lnToQry = new JTextField("Simpson");
		jpQry.add(this.lnToQry);

		tp.addTab("Create Record", jpCreateRec);
		tp.addTab("get count", jpGetCount);
		tp.addTab("edit", jpEdit);
		tp.addTab("Transfer", jpTransfer);
		tp.addTab("Query", jpQry);

		JPanel jpBottom = new JPanel(new GridLayout(3, 1));
		btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(this);
		msgArea = new JTextArea();
		btnLogout = new JButton("Logout");
		btnLogout.addActionListener(this);

		jpBottom.add(btnSubmit);
		jpBottom.add(msgArea);
		jpBottom.add(btnLogout);

		this.setLayout(new GridLayout(2, 2));
		this.getContentPane().add(tp);
		this.getContentPane().add(jpBottom);

		this.pack();
		this.setBounds(80, 80, 600, 400);
		this.repaint();

	}

	public static void main(String[] args) throws InterruptedException {

		Officer officer = new Officer();
		officer.showLogin();
		while (true) {
			Thread.sleep(2000);
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if (src == this.btnLogin) {

			if (!this.getService()) {
				this.msg("can't find the PoliceStation");
				return;
			}
			if (this.logtool != null)
				this.logtool.close();
			this.logtool = new LogTool(pathLog + "/" + this.idPolice + ".log");
			this.logtool.log("Officer logon");
			this.getContentPane().removeAll();
			this.showControlPanel();
		} else if (src == this.recType_create) {
			this.fillPaneCreateRec(
					(RecordType) this.recType_create.getSelectedItem(),
					this.jpCreateRec);
			this.repaint();
		} else if (src == this.btnSubmit) {
			String msg = "";
			try {

				Component tab = this.tp.getSelectedComponent();
				if (tab == this.jpCreateRec)

					msg = this.createRecord();

				else if (tab == this.jpEdit) {
					msg = this.updateRecord();
				} else if (tab == this.jpGetCount) {
					msg = this.getRecCount();
				} else if (tab == this.jpTransfer) {
					msg = this.transferRecord();
				} else if (tab == this.jpQry) {
					msg = this.queryIDs();
				} else {
					msg = "unknow action";
				}
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			this.msgArea.setText(msg);
		} else if (src == this.btnLogout) {
			this.ps = null;
			this.city = null;
			this.getContentPane().removeAll();
			this.showLogin();
		}
	}

	public String createRecord() throws RemoteException {
		Record.RecordType typ = (RecordType) (this.recType_create
				.getSelectedItem());
		String msg = "";
		boolean isSuccess = false;
		if (typ == Record.RecordType.CRIMINAL) {
			isSuccess = this.ps.createCRecord(this.fn.getText(),
					this.ln.getText(), this.desc.getText(),
					this.recStatus_create.getSelectedItem().toString(),
					idPolice);
			msg = "success creating criminal record";
		} else if (typ == Record.RecordType.MISSING) {
			isSuccess = this.ps.createMRecord(this.fn.getText(), this.ln
					.getText(), this.addr.getText(), Calendar.getInstance()
					.getTimeInMillis(), this.loc.getText(),
					this.recStatus_create.getSelectedItem().toString(),
					idPolice);
			msg = "success creating missing record";
		} else {
			msg = "unknow record";
		}
		this.logtool.log(String.format(msg + " (%s %s)", this.fn.getText(),
				this.ln.getText()));
		return msg;

	}

	public String getRecCount() throws RemoteException {

		String msg = this.ps.getRecordCounts(((RecordType) this.recType_count
				.getSelectedItem()).toString());
		this.logtool.log("get record(" + this.recType_count.getSelectedItem()
				+ msg);
		return msg;

	}

	public String updateRecord() {
		String msg = this.ps.editCRecord(this.lnUpdate.getText(), this.idRecord
				.getText(), this.recStatus_update.getSelectedItem().toString(),
				idPolice);
		this.logtool.log(msg);
		return msg;
	}

	public String transferRecord() {
		System.out.print(String.format(
				"Transfering record (id: %s) to PoliceStation (%s)",
				this.idRecordForTransfer.getText(), this.psTo.getText()));
		boolean rslt = this.ps.transferRecord(
				this.idRecordForTransfer.getText(), this.psTo.getText(),
				this.idPolice);
		String msg = String.format(
				"Transfer record (id: %s) to PoliceStation (%s), result: "
						+ rslt, this.idRecordForTransfer.getText(),
				this.psTo.getText());
		this.logtool.log(msg);

		return msg;
	}

	public String queryIDs() {
		System.out
				.println("query the ids, lastname: " + this.lnToQry.getText());
		return this.ps.getIDs(this.lnToQry.getText());
	}

	public void fillPaneCreateRec(Record.RecordType type, JPanel jp) {
		jp.removeAll();
		GridBagConstraints c = new GridBagConstraints();
		this.fillPaneCreateRecHead(jp);
		c.anchor = GridBagConstraints.LINE_START;

		if (type == Record.RecordType.CRIMINAL) {

			jp.add(new JLabel("Description"), c);
			if (this.desc == null)
				this.desc = new JTextField("tom stole a cat......");
			c.anchor = GridBagConstraints.LINE_END;
			jp.add(this.desc, c);
			c.anchor = GridBagConstraints.LINE_START;
			jp.add(new JLabel("Record Status: "), c);
			this.recStatus_create = new JComboBox(Record.CRStatus.values());
			c.anchor = GridBagConstraints.LINE_END;
			jp.add(this.recStatus_create, c);

		} else {
			jp.add(new JLabel("Address"), c);
			if (this.addr == null)
				this.addr = new JTextField("1450 masonneuve, Montreal, QC");
			jp.add(this.addr);
			c.anchor = GridBagConstraints.LINE_START;
			jp.add(new JLabel("Date Last Seen"), c);
			Calendar cal = Calendar.getInstance();
			if (this.year == null)
				this.year = new JTextField("" + cal.get(Calendar.YEAR));
			if (this.month == null)
				this.month = new JTextField("" + (cal.get(Calendar.MONTH) + 1));
			if (this.day == null)
				this.day = new JTextField("" + cal.get(Calendar.DAY_OF_MONTH));
			jp.add(year);
			jp.add(month);
			jp.add(day);
			c.anchor = GridBagConstraints.LINE_START;
			jp.add(new JLabel("Location last Seen"), c);
			if (this.loc == null)
				this.loc = new JTextField("H-832 Concordia Compus");
			jp.add(loc);

			jp.add(new JLabel("Status"));
			this.recStatus_create = new JComboBox(Record.MRStatus.values());
			jp.add(this.recStatus_create);
		}

	}

	public boolean getService() {
		String nameCity = this.idPolice.replaceAll("([a-zA-Z]+)(\\d+)", "$1")
				.toUpperCase();
		if (!City.isCity(nameCity)) {
			this.msg(nameCity + " is not a city");
			return false;
		}
		this.city = City.valueOf(nameCity);

		ORB orb = ORB.init(new String[] {}, null);
		String ior = "";
		try {
			ior = this.getIOR();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("get the ior: \n" + ior);
		org.omg.CORBA.Object ref = orb.string_to_object(ior);

		this.ps = PoliceStationHelper.narrow(ref);
		if (this.ps != null)
			System.out.println("connect to the police station: ");

		String tt = String.format("[PoliceStaiton: %s, Police Id: %s] logon",
				this.city.name, this.idPolice);
		this.setTitle(tt);

		return true;
	}

	void msg(String msg) {
		JOptionPane.showMessageDialog(this, msg);
	}

	PoliceStationOperations ps;

	public void startCtrlPanel() {
		Container cp = this.getContentPane();

	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		if (e.getDocument() == this.docPoliceId
				|| e.getDocument() == this.docIORUrl) {
			this.validateLogin();
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		if (e.getDocument() == this.docPoliceId) {
			this.validateLogin();
		}
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		if (e.getDocument() == this.docPoliceId) {
			this.validateLogin();
		}

	}

	String iorUrl;

	public void validateLogin() {

		try {
			this.idPolice = this.docPoliceId.getText(0,
					this.docPoliceId.getLength());
			this.iorUrl = this.docIORUrl.getText(0, this.docIORUrl.getLength());
			if (this.idPolice.length() > 0 && this.iorUrl.length() > 0) {
				this.btnLogin.setEnabled(true);

			} else {
				this.btnLogin.setEnabled(false);
			}
		} catch (BadLocationException e) {
			this.msg(e.getMessage());
		}

	}

}
