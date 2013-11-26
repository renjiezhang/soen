package server;

import java.io.IOException;
import java.util.Calendar;

import common.City;
import common.LogTool;
import common.Record;
import common.Record.RecordType;
import common.RecordCriminal;
import common.RecordMissing;
import common.Storage;

public class ServerProcess implements idl.PoliceStationOperations{
	 City city;
	 Storage storage;

	 int id;
	 
	 void updateUI(){
		 
	 }
	 void log(String msg){
		 
	 }
	 @Override
		public boolean createCRecord(String firstName, String lastName,
				String description, String status, String badgeID) {
			this.log("createCRecord() get request from " + badgeID);
			// boolean isSuccess=false;
			boolean isSuccess = this.storage.addRecord(
					new RecordCriminal(id++, firstName, lastName, description,
							common.Record.CRStatus.valueOf(status)),
					common.Record.RecordType.CRIMINAL);
			String msg = String.format(
					"[%s] create a criminal record: [%s %s - %s] %s", badgeID,
					firstName, lastName, status, description);
			this.log(msg);
			this.updateUI();
			return isSuccess;
		}

		@Override
		public boolean createMRecord(String firstName, String lastName,
				String address, long lastDate, String lastLocation, String status,
				String badgeID) {
			this.log("createMRecord() get request from " + badgeID);
			int idrec = id++;
			String cityname=badgeID.substring(2);
			Calendar cLastDate = Calendar.getInstance();
			cLastDate.setTimeInMillis(lastDate);
			boolean isSuccess = this.storage.addRecord(new RecordMissing(idrec,
					firstName, lastName, address, cLastDate, lastLocation,
					Record.MRStatus.valueOf(status)), Record.RecordType.MISSING);
			String msg = String
					.format("[%s] create a missing record: [id: %s%05d][%s %s - %s] address: %s, last location: %s",
							badgeID, cityname, idrec, firstName, lastName,
							status, address, lastLocation);
			this.log(msg);
			this.updateUI();
			return isSuccess;
		}

		@Override
		public String getRecordCounts(String recType) {
			this.log("getRecordCounts() get request");
			String msg = "{";
			for (City city : City.values()) {
				try {
					String _msg = UdpRecCountSvr.query("localhost", city.portUdp,
							recType) + ";\n";
					msg += _msg;
					this.log("get count, " + _msg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			return msg + "";
		}

		@Override
		public String editCRecord(String lastName, String recordID,
				String newStatus, String badgeID) {
			String msg = String.format("fail to update a record: [%s - %s] %s",
					recordID, lastName, newStatus);
			if (this.storage.updateRecord(recordID, lastName, newStatus)) {
				msg = String.format(
						"[%s] success to update a record: [%s - %s] %s", badgeID,
						recordID, lastName, newStatus);
			}
			this.log(msg);
			this.updateUI();
			return null;
		}

		@Override
		public boolean transferRecord(String recordID,
				String remoteStationServerName, String badgeID) {

			this.log("transferRecord() get request from " + badgeID
					+ ", record id: " + recordID + ", destination: "
					+ remoteStationServerName);
			System.out.println("transferRecord() get request from " + badgeID
					+ ", record id: " + recordID + ", destination: "
					+ remoteStationServerName);
			
			return false;
		}
		@Override
		public String getIDs(String lastname) {
			// TODO Auto-generated method stub
			return null;
		}
}
