package frontEnd;

import java.io.IOException;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface IORRepositoryInterface {

	@WebMethod
	public abstract String getIOR() throws IOException;

}