package common;

public abstract class Record {

	public static enum RecordType {
		
		CRIMINAL("CR"), MISSING("MR");
		public String prefix;
		private RecordType(String prefix){
			this.prefix=prefix;
		}
	
	}

	public static enum CRStatus {
		OnTheRun("On the run"), Captured("Captured");

		String status;

		private CRStatus(String status) {
			this.status = status;
		}
		
		static public boolean isCRStatus(String status){
			return status.equals(OnTheRun) || status.equals(Captured);
		}

	}

	public static enum MRStatus {
		Found("Found"), Missing("Missing");
		public String stauts;

		private MRStatus(String status) {
			this.stauts = status;
		}
		static public boolean isMRStatus(String status){
			return status.equals(Found.toString()) || status.equals(Missing.toString());
		}
	}

	int id;

	public String fn;

	public String ln;

	abstract String getPrefix();
	abstract String getStatus();

	String getId() {
		return this.getPrefix() + id;
	}
	
	public Record(int id, String fn, String ln){
		this.id=id;
		this.fn=fn;
		this.ln=ln;
	}

}