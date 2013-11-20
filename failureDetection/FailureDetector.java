package failureDetection;

import UdpCommunicator.UdpFailureDetection;

public class FailureDetector implements Runnable {

	@Override
	public void run() {
		enum failureDetectionPorts = {6789, 6790, 6791};
		for(failureDetectionPorts : port) {
			UdpFailureDetection.failureDetection(port);
		}
	}
	
}
