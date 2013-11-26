package common;

public interface Fifo {
	public void snd(Message m);
	public Message rcv();
}
