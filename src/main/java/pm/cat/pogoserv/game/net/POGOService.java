package pm.cat.pogoserv.game.net;

import java.io.IOException;
import java.io.InputStream;

import com.sun.net.httpserver.HttpExchange;

import POGOProtos.Networking.Envelopes.POGOProtosNetworkingEnvelopes.RequestEnvelope;
import pm.cat.pogoserv.net.Request;
import pm.cat.pogoserv.net.Service;

public interface POGOService extends Service {

	default POGORequest parse(HttpExchange he) throws IOException {
		InputStream in = he.getRequestBody();
		RequestEnvelope re = RequestEnvelope.parseFrom(in);
		in.close();
		return new POGORequest(he, re);
	}
	
	@Override
	default Request process(HttpExchange he) throws IOException {
		POGORequest re = parse(he);
		process(re);
		return re;
	}
	
	void process(POGORequest re) throws IOException; 

}
