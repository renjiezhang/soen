package common;

import java.util.Calendar;

public class RecordMissing extends Record{
	
	
	public RecordMissing(int id, String fn, String ln, String addr, Calendar dataLastSeen, String placeLastSeen, Record.MRStatus status) {
		super(id, fn, ln);
		this.addrLastKnown=addr;
		this.PlaceLastSeen=placeLastSeen;
		this.status=status;
		
	}




	public String addrLastKnown;
	public String PlaceLastSeen;
	public Calendar dateLastSeen;
	public Record.MRStatus status;
	

	

	@Override
	String getPrefix() {
		return Record.RecordType.MISSING.prefix;
	}




	@Override
	String getStatus() {
		return this.status.toString();
	}
	
}