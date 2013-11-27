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
	private String host;
	private int port;
	private City city;
	
	public int  getPort(){
		return this.port;
	}
	public String getHost(){
		return this.host;
	}

	Document docPort, docIP;
	JTextField txtIP, txtPort;	

	public ServerProcessInfo(City city) {
		super();
		this.city = city;
		this.setLayout(new GridLayout(1, 5));
		
		this.add(new JLabel("-["+city.name+"]-"));
		 txtIP = new JTextField(16);
		this.docIP = txtIP.getDocument();
		this.docIP.addDocumentListener(this);
		 txtPort = new JTextField(6);
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
				this.host = this.docIP.getText(0, this.docIP.getLength());
				this.host = this.host.trim();
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
	private void updateInfo(String ip, int port) {

		this.host=ip;
		this.port=port;
		this.txtIP.setText(ip);
		this.txtPort.setText(port+"");
		this.repaint();
	}
	

}
