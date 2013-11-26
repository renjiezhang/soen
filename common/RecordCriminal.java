package common;

public class RecordCriminal extends Record{
	
	public String desc;
	public Record.CRStatus status;
	

	public RecordCriminal(int id, String fn, String ln, String desc, Record.CRStatus status) {
		super(id, fn, ln);
		this.desc=desc;
		this.status=status;		
	}

	@Override
	String getPrefix() {
		return Record.RecordType.CRIMINAL.prefix;
	}

	@Override
	public
	String getStatus() {
		return this.status.toString();
	}

	
	
}