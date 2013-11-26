package failureDetection;

public class FailureTimeouts {

	public final int memberTimeout;
	public final int leaderTimeout;
	public final int detectionCycleTimer;
	
	public FailureTimeouts(final int memberTimeout, final int leaderTimeout, final int detectionCycleTimer) {
		this.memberTimeout = memberTimeout;
		this.detectionCycleTimer = detectionCycleTimer;
		if (detectionCycleTimer > (leaderTimeout + 1000)) {
			this.leaderTimeout = leaderTimeout;
			return;
		}
		this.leaderTimeout = detectionCycleTimer + 2000;
	}
	
}
