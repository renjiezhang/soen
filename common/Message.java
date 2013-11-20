package common;

import java.io.Serializable;

public class Message implements Serializable{
	final static int MSG_createCRecord=1, MSG_createMRecord=2, MSG_getRecordCounts=3, MSG_editCRecord=4, MSG_deleteRecord=5, MSG_getIDs=6, MSG_newLeader=7;
	int msgType;
	Object msg;
	Message(int type, Object msg){
		this.msgType=type;
		this.msg=msg;
	}
	
	
	
}
