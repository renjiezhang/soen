package common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class Message implements Serializable {

	MessageType msgType;

	Object msg;

	public Message(MessageType type, Object msg) {
		this.msgType = type;
		this.msg = msg;
	}

	static public void main(String[] args) throws IOException,
			ClassNotFoundException {
		Object o = new String("");
		System.out.println(o.getClass().getCanonicalName());
		ObjectInputStream ois = null;
		Message m = (Message) ois.readObject();

		switch (m.msgType) {
		case CreateCRecord:
			RecordCriminal cr = (RecordCriminal) m.msg;
			break;
		case CreateMRecord:

			break;
		}

	}
	
}
