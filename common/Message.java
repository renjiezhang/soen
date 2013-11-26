package common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;


public class Message implements Serializable{
	MessageType msgType;
	Object msg;
	public Message(MessageType type, Object msg){
		this.msgType=type;
		this.msg=msg;

	}
}
