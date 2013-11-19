package frontEnd;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService(endpointInterface = "frontEnd.IORRepositoryInterface")
public class IORRepository implements IORRepositoryInterface {
	public final static int PORT=9999;
	
	/* (non-Javadoc)
	 * @see frontEnd.IORRepositoryInterface#getIOR()
	 */
	@Override
	public String getIOR() throws IOException{
		return this.ior;
	}
	
	String ior="";
	
	public IORRepository(String ior){
		this.ior=ior;
	}
	
}
