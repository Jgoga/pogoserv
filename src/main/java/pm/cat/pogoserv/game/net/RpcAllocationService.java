package pm.cat.pogoserv.game.net;

import java.io.IOException;
import java.util.function.Function;

import com.google.protobuf.ByteString;

import pm.cat.pogoserv.Config;

public class RpcAllocationService implements POGOService {
	
	private final Function<POGORequest, String> hostSelector;
	private final Function<POGORequest, Integer> endpointSelector;
	
	public RpcAllocationService(Function<POGORequest, String> hostSelector, Function<POGORequest, Integer> endpointSelector){
		this.hostSelector = hostSelector;
		this.endpointSelector = endpointSelector;
	}
	
	@Override
	public void process(POGORequest re) throws IOException {
		String host = hostSelector.apply(re);
		Integer endpoint = endpointSelector.apply(re);
		
		if(host != null && endpoint != null){
			re.resp.setApiUrl(String.format(Config.RPC_ENDPOINT_FULL_URL, host, endpoint));
			
			// Usually when authing, client sends 4 requests and server responds with 2 empty returns
			// no idea why.
			re.resp.addReturns(ByteString.EMPTY);
			re.resp.addReturns(ByteString.EMPTY);
			
			re.resp.setStatusCode(POGORequest.NEW_RPC_ENDPOINT);
		}else{
			// TODO: what's the correct error code here?
		}
	}
	
	public static RpcAllocationService constantAllocator(String host, int endpoint){
		return new RpcAllocationService(__ -> host, __ -> endpoint);
	}
	
}
