package common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class Message implements Serializable {
	final static int MSG_createCRecord = 1, MSG_createMRecord = 2,
			MSG_getRecordCounts = 3, MSG_editCRecord = 4, MSG_deleteRecord = 5,
			MSG_getIDs = 6, MSG_newLeader = 7;
	int msgType;
	
	Object msg;

	Message(int type, Object msg) {
		this.msgType = type;
		this.msg = msg;
	}

	static public void	main(String[] args) throws IOException, ClassNotFoundException{
		Object o=new String("");
System.out.println(	o.getClass().getCanonicalName());
		ObjectInputStream ois = null;
		Message m = (Message) ois.readObject();

		switch (m.msgType) {
		case MSG_createCRecord:
			RecordCriminal cr = (RecordCriminal) m.msg;
			break;
		case MSG_getRecordCounts:

			break;
		}
		ProcessBuilder pb=null;
		
	}
}
