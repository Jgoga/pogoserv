package pm.cat.pogoserv.game.request;

import POGOProtos.Networking.Envelopes.POGOProtosNetworkingEnvelopes.Unknown6Response;
import pm.cat.pogoserv.util.Filter;

public class Unknown6Handler implements Filter<GameRequest> {
	
	private static final Unknown6Response RESPONSE = Unknown6Response.newBuilder()
		.setResponseType(1).setUnknown2(Unknown6Response.Unknown2.newBuilder().setUnknown1(1))
		.build();
	
	// TODO 5=IAP
	@Override
	public void run(GameRequest t) {
		for(int i=0;i<t.http.request.getUnknown6Count();i++)
			t.http.response.addUnknown6(RESPONSE);
	}

}
