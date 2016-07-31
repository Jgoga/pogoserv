package pm.cat.pogoserv.core.net;

import com.google.protobuf.ByteString;

import POGOProtos.Networking.Envelopes.POGOProtosNetworkingEnvelopes.AuthTicket;
import pm.cat.pogoserv.core.Constants;
import pm.cat.pogoserv.util.Filter;

public interface RPCHandleAllocator extends Filter<HttpRequest> {

	int getRpcIndex();
	String getRpcHost();
	
	@Override
	default void run(HttpRequest re) {
		// This is the interesting part
		re.response.setApiUrl(getRpcHost() + "/plfe/" + getRpcIndex());
		
		re.response.setAuthTicket(getAuthTicket(re));
		
		// Usually when authing, client sends 4 requests and server responds with 2 empty returns
		// no idea why.
		re.response.addReturns(ByteString.EMPTY);
		re.response.addReturns(ByteString.EMPTY);
		
		re.protoStatus = getStatusCode();
	}
	
	default AuthTicket getAuthTicket(HttpRequest re){
		AuthTicket.Builder auth = AuthTicket.newBuilder();
		auth.setStart(ByteString.EMPTY); // No idea what this does
		// Default seems to be half an hour
		auth.setExpireTimestampMs(System.currentTimeMillis() + 30*60*1000);
		auth.setEnd(ByteString.EMPTY); // No idea what this does either
		return auth.build();
	}
	
	default int getStatusCode(){
		return Constants.SERVER_NEW_RPC_ENDPOINT;
	}

}
