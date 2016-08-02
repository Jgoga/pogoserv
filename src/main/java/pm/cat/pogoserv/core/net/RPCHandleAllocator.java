package pm.cat.pogoserv.core.net;

import com.google.protobuf.ByteString;

import pm.cat.pogoserv.core.Constants;
import pm.cat.pogoserv.util.Filter;

public interface RPCHandleAllocator extends Filter<HttpRequest> {

	int getRpcIndex();
	String getRpcHost();
	
	@Override
	default void run(HttpRequest re) {
		// This is the interesting part
		re.response.setApiUrl(getRpcHost() + "/plfe/" + getRpcIndex());
		
		// Usually when authing, client sends 4 requests and server responds with 2 empty returns
		// no idea why.
		re.response.addReturns(ByteString.EMPTY);
		re.response.addReturns(ByteString.EMPTY);
		
		re.protoStatus = getStatusCode();
	}
	
	default int getStatusCode(){
		return Constants.SERVER_NEW_RPC_ENDPOINT;
	}

}
