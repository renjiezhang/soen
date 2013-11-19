package frontEnd;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import common.City;

public class ServerProcessInfo extends JComponent implements DocumentListener {
	private String ip;
	private int port;
	private City city;

	Document docPort, docIP;

	public ServerProcessInfo(City city) {
		super();
		this.city = city;
		this.setLayout(new GridLayout(1, 5));
		
		this.add(new JLabel("-["+city.name+"]-"));
		JTextField txtIP = new JTextField(16);
		this.docIP = txtIP.getDocument();
		this.docIP.addDocumentListener(this);
		JTextField txtPort = new JTextField(6);
		this.docPort = txtPort.getDocument();
		this.docPort.addDocumentListener(this);
		this.add(new JLabel("Host : "));
		this.add(txtIP);
		this.add(new JLabel("Port: "));
		this.add(txtPort);
		
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		this.updateInfo();

	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		this.updateInfo();

	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		this.updateInfo();

	}

	private void updateInfo() {

		try {

			if (this.docIP.getLength() > 0) {
				this.ip = this.docIP.getText(0, this.docIP.getLength());
				this.ip = this.ip.trim();
			}
			if (this.docPort.getLength() > 0) {
				String strPort = this.docPort.getText(0,
						this.docPort.getLength());
				strPort = strPort.trim();
				if (strPort.length() > 0)
					this.port = Integer.parseInt(strPort);
			}
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (NumberFormatException e){
			
		}

	}

}
