package pm.cat.pogoserv.game.net.request;

import java.io.IOException;

import POGOProtos.Networking.Envelopes.POGOProtosNetworkingEnvelopes.RequestEnvelope;
import POGOProtos.Networking.Requests.POGOProtosNetworkingRequests;

public interface RequestMapper<T> {
	
	T parse(POGOProtosNetworkingRequests.Request req, RequestEnvelope re) throws IOException;
	
	// Return something that can be transformed to ByteString
	// ie. return a MessageLite, MessageLite.Builder, byte[] or ByteString
	Object write(T re) throws IOException;
	
}
