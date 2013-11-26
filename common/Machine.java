package common;

public class Machine {

	public final int detectionPort;
	public final int electionPort;
	public final String host;
	private int leaderId; 
	private int myId;
	private boolean isAlive;
	
	public Machine(final String host, final int detectionPort, final int electionPort, final int leaderId, final int myId) {
		this.detectionPort = detectionPort;
		this.electionPort = electionPort;
		this.host = host;
		this.leaderId = leaderId;
		this.myId = myId;
		this.isAlive = true;
	}
	
	public boolean isLeader() {
		return this.leaderId == this.myId;
	}
	
	public boolean isAlive() {
		return isAlive;
	}
	
	public void kill() {
		this.isAlive = false;
	}
}
