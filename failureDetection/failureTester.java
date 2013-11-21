package failureDetection;

import java.util.ArrayList;
import java.util.Iterator;

import common.City;
import common.Machine;

public class failureTester {
		public static void main(String[] args) {  
					
			launchSimulatedMachines();
		}
		
		private static void launchSimulatedMachines() {
			FailureTimeouts failureTimeouts = new FailureTimeouts(2000, 8000, 5000);
			
			//Launch 3 fake machines
			Machine[] machines = new Machine[]{	new Machine("localhost", 8000, 8001, 0, 0),
												new Machine("localhost", 8010, 8011, 0, 1),
												new Machine("localhost", 8020, 8021, 0, 2)};
			
			for(int x = 0; x < machines.length; x++) {
								
				UdpFailureDetection failureDetection;
				failureDetection = new UdpFailureDetection(failureTimeouts, machines, machines[x]);
//					failureDetection = new UdpFailureDetection(failureTimeouts, city.portUdp, MembershipState.MEMBER);
//					failureDetection = new UdpFailureDetection(failureTimeouts, city.portUdp, MembershipState.MEMBER);					
				
				Thread udpThread = new Thread(failureDetection);
				udpThread.start();
				
				//Endpoint.publish("http://localhost:8080/WS/" + city.name, new Object());
//				UdpListener udpListener = new UdpListener(city.portUdp, 8000); // This has to be greater than what is sent to udpFailure.initialize(5000);
//				Thread udpThread = new Thread(udpListener);
//				udpThread.start();			    
			}
		}
		
}
