package common;

import java.io.IOException;
import java.io.Serializable;

public interface FifoInterf {
	
	
	Object snd(Serializable s, String host, int port) throws IOException, ClassNotFoundException;
}
