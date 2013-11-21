package failureDetection;

import common.City;
import udpCommunicator.*;

public class failureTester {
		public static void main(String[] args) {  
					
			launchMachines();
		}
		
		private static void launchMachines() {
			FailureTimeouts failureTimeouts = new FailureTimeouts(2000, 8000, 5000);
			
			for (City city : City.values()) {
				
				UdpFailureDetection failureDetection;
				
				if (city == City.QUEBEC) {
					failureDetection = new UdpFailureDetection(failureTimeouts, city.portUdp, MembershipState.LEADER);
				} else {
					failureDetection = new UdpFailureDetection(failureTimeouts, city.portUdp, MembershipState.MEMBER);					
				}
				Thread udpThread = new Thread(failureDetection);
				udpThread.start();
				
				//Endpoint.publish("http://localhost:8080/WS/" + city.name, new Object());
//				UdpListener udpListener = new UdpListener(city.portUdp, 8000); // This has to be greater than what is sent to udpFailure.initialize(5000);
//				Thread udpThread = new Thread(udpListener);
//				udpThread.start();			    
			}
		}
		
}
