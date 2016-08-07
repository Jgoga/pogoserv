package pm.cat.pogoserv.net;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

public interface Service {
		
	default void start(){ }
	Request process(HttpExchange he) throws IOException;
	default void shutdown(){ }
	
}
