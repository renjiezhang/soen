package idl;

/**
* idl/PoliceStationHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ./policestation.idl
* Tuesday, November 19, 2013 11:10:35 AM EST
*/

public final class PoliceStationHolder implements org.omg.CORBA.portable.Streamable
{
  public idl.PoliceStation value = null;

  public PoliceStationHolder ()
  {
  }

  public PoliceStationHolder (idl.PoliceStation initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = idl.PoliceStationHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    idl.PoliceStationHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return idl.PoliceStationHelper.type ();
  }

}
