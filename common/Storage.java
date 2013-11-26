package common;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import common.Record.RecordType;

public class Storage {

	/*
	 * all the Records with the last name starting with an “A” will belong to
	 * the same list and will be stored in a hash table (acting as the database)
	 * and the key will be “A”. We do not distinguish between criminal records
	 * and missing records when inserting them into the hash table (i.e. a list
	 * may contain both criminal records and missing records).
	 */
	private Hashtable<String, List<Record>> recs;

	public Storage() {
		recs = new Hashtable<String, List<Record>>();
		for (char c = 'A'; c <= 'Z'; c++) {
			recs.put("" + c, new ArrayList<Record>());
		}
	}

	public int getCount(Record.RecordType type) {
		int count = 0;
		for (char c = 'A'; c <= 'Z'; c++) {
			for (Record rec : recs.get("" + c)) {
				if (rec.getPrefix().equals(type.prefix))
					count++;
			}
		}
		return count;
	}

	public Record getRecord(String id) {
		//System.out.println("finding the record, id: " + id);
		for (List<Record> lrec : recs.values()) {

			for (Record rec : lrec) {
				//System.out.println("comparing record, id: "+id);
				if (String.format("%s%05d", rec.getPrefix(), rec.id).equals(id)) {

					return rec;
				}

			}
		}
		return null;
	}

	public synchronized boolean addRecord(Record rec, Record.RecordType type) {
		if ((type == Record.RecordType.CRIMINAL && rec.getClass() == RecordCriminal.class)
				|| (type == Record.RecordType.MISSING && rec.getClass() == RecordMissing.class)) {
			String c = rec.ln.substring(0, 1);
			recs.get(c.toUpperCase()).add(rec);

		} else
			return false;

		return true;
	}

	public synchronized boolean removeRecord(String id) {

		for (List<Record> lrec : recs.values()) {

			for (Record rec : lrec) {
				try {
					if (String.format("%s%05d", rec.getPrefix(), rec.id)
							.equals(id)) {
						lrec.remove(rec);
						return true;
					}
				} catch (Exception e) {
					return false;
				}
			}
		}
		return false;

	}

	public synchronized boolean updateRecord(String id, String ln, String status) {
		String c = ln.substring(0, 1);
		List<Record> lrec = recs.get(c.toUpperCase());
		for (Record rec : lrec) {
			try {
				if (String.format("%s%05d", rec.getPrefix(), rec.id).equals(id)) {
					if ((rec.getClass() == RecordMissing.class && Record.MRStatus
							.isMRStatus(status))) {
						((RecordMissing) rec).status = Record.MRStatus
								.valueOf(status);
						return true;
					} else if ((rec.getClass() == RecordCriminal.class && Record.CRStatus
							.isCRStatus(status))) {
						((RecordCriminal) rec).status = Record.CRStatus
								.valueOf(status);
						return true;
					}
				}
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	public String[][] getRecordInfo() {
		String[][] strRecs = new String[this.getTotalCounts()][4];
		List<Record> lRec = new ArrayList<Record>();
		for (List<Record> rec : this.recs.values()) {
			lRec.addAll(rec);
		}

		for (int i = 0; i < lRec.size(); i++) {
			Record rec = lRec.get(i);
			strRecs[i] = new String[4];
			strRecs[i][0] = String.format("%s%d", rec.getPrefix(), rec.id);
			strRecs[i][1] = rec.ln;
			strRecs[i][2] = rec.fn;
			strRecs[i][3] = rec.getStatus();
		}
		return strRecs;
	}

	public String[] getRecordInfoAttrName() {
		return new String[] { "Record ID", "Last Name", "FirstName", "Status" };
	}

	public int getTotalCounts() {
		return this.getCount(RecordType.CRIMINAL)
				+ this.getCount(RecordType.MISSING);
	}
	public String getIDs(String nameLast){
		String ids="{";
		for (List<Record> lrec : recs.values()) {

			for (Record rec : lrec) {
				//System.out.println("comparing record, id: "+id);
				if (rec.ln.equals(nameLast)) {
					ids+=String.format("%s%05d", rec.getPrefix(), rec.id)+",";
				}

			}
		}
		return ids+"}";
	}
}
