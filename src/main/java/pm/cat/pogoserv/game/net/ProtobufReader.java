package pm.cat.pogoserv.game.net;

import POGOProtos.Networking.Envelopes.POGOProtosNetworkingEnvelopes;
import pm.cat.pogoserv.game.session.AuthTicket;
import pm.cat.pogoserv.game.session.AuthToken;

public class ProtobufReader {
	
	public static AuthTicket authTicket(POGOProtosNetworkingEnvelopes.AuthTicket src){
		return new AuthTicket(src.getStart(), src.getEnd(), src.getExpireTimestampMs());
	}
	
	public static AuthToken authToken(POGOProtosNetworkingEnvelopes.RequestEnvelope.AuthInfo src){
		return new AuthToken(src.getProvider(), src.getToken().getContents());
	}
	
}
