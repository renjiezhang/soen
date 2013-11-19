package idl;


/**
* idl/PoliceStationPOA.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ./policestation.idl
* Tuesday, November 19, 2013 11:10:35 AM EST
*/

public abstract class PoliceStationPOA extends org.omg.PortableServer.Servant
 implements idl.PoliceStationOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("createCRecord", new java.lang.Integer (0));
    _methods.put ("createMRecord", new java.lang.Integer (1));
    _methods.put ("getRecordCounts", new java.lang.Integer (2));
    _methods.put ("editCRecord", new java.lang.Integer (3));
    _methods.put ("transferRecord", new java.lang.Integer (4));
    _methods.put ("getIDs", new java.lang.Integer (5));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {

  /**
  	 * When a police officer invokes this method from his/her station through a
  	 * client program called OfficerClient, the server associated with this
  	 * officer (determined by the BadgeID prefix) attempts to create a
  	 * CriminalRecord with the information passed, assigns a unique RecordID and
  	 * inserts the Record at the appropriate location in the hash table. The
  	 * server returns information to the officer whether the operation was
  	 * successful or not and both the server and the officer store this
  	 * information in their logs.
  	 */
       case 0:  // idl/PoliceStation/createCRecord
       {
         String firstName = in.read_string ();
         String lastName = in.read_string ();
         String description = in.read_string ();
         String status = in.read_string ();
         String badgeID = in.read_string ();
         boolean $result = false;
         $result = this.createCRecord (firstName, lastName, description, status, badgeID);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }


  /**
  	 * When a police officer invokes this method from a OfficerClient, the
  	 * server associated with this officer (determined by the BadgeID prefix)
  	 * attempts to create a MissingRecord with the information passed, assigns a
  	 * unique RecordID and inserts the Record at the appropriate location in the
  	 * hash table. The server returns information to the officer whether the
  	 * operation was successful or not and both the server and the officer store
  	 * this information in their logs.
  	 */
       case 1:  // idl/PoliceStation/createMRecord
       {
         String firstName = in.read_string ();
         String lastName = in.read_string ();
         String address = in.read_string ();
         long lastDate = in.read_longlong ();
         String lastLocation = in.read_string ();
         String status = in.read_string ();
         String badgeID = in.read_string ();
         boolean $result = false;
         $result = this.createMRecord (firstName, lastName, address, lastDate, lastLocation, status, badgeID);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }


  /**
  	 * A police officer invokes this method from his/her OfficerClient and the
  	 * server associated with that officer concurrently finds out the number of
  	 * records (both CR and MR) in the other stations using UDP/IP sockets and
  	 * returns the result to the officer. Please note that it only returns the
  	 * record counts (a number) and not the records themselves. For example if
  	 * SPVM has 6 records, SPL has 7 and SPB had 8, it should return the
  	 * following: SPVM 6, SPL 7, SPB 8.
  	 */
       case 2:  // idl/PoliceStation/getRecordCounts
       {
         String recType = in.read_string ();
         String $result = null;
         $result = this.getRecordCounts (recType);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }


  /**
  	 * When invoked by an officer, the server associated with this officer
  	 * (determined by the BadgeID) searches in the hash table to find the
  	 * recordID and changes the status of the record according to the newStatus
  	 * if it is found. Upon success or failure it returns a message to the
  	 * officer and the logs are updated with this information. If the status
  	 * does not match the RecordType, it is invalid. For example, if the found
  	 * Record is a CriminalRecord and the newStatus is for a MissingRecord, the
  	 * server shall return an error.
  	 */
       case 3:  // idl/PoliceStation/editCRecord
       {
         String lastName = in.read_string ();
         String recordID = in.read_string ();
         String newStatus = in.read_string ();
         String badgeID = in.read_string ();
         String $result = null;
         $result = this.editCRecord (lastName, recordID, newStatus, badgeID);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }


  /**
  	* When a police officer invokes this method from his/her station, the server associated
  	* with this officer (determined by the badgeID prefix) searches its hash table to find if
  	* the record with recordID exists. If it exists, the entire record is transferred to the
  	* remoteStationServer. Note that the record should be removed from the hash table of
  	* the initial server and should be added to the hash table of the remoteStationServer
  	* atomically. The server informs the officer whether the operation was successful or
  	* not and both the server and the officer store this information in their logs.
  	*/
       case 4:  // idl/PoliceStation/transferRecord
       {
         String recordID = in.read_string ();
         String remoteStationServerName = in.read_string ();
         String badgeID = in.read_string ();
         boolean $result = false;
         $result = this.transferRecord (recordID, remoteStationServerName, badgeID);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       case 5:  // idl/PoliceStation/getIDs
       {
         String lastname = in.read_string ();
         String $result = null;
         $result = this.getIDs (lastname);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:idl/PoliceStation:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public PoliceStation _this() 
  {
    return PoliceStationHelper.narrow(
    super._this_object());
  }

  public PoliceStation _this(org.omg.CORBA.ORB orb) 
  {
    return PoliceStationHelper.narrow(
    super._this_object(orb));
  }


} // class PoliceStationPOA