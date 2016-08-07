package pm.cat.pogoserv.game.net.request.impl;

import java.io.IOException;

import POGOProtos.Networking.Envelopes.POGOProtosNetworkingEnvelopes.RequestEnvelope;
import POGOProtos.Networking.Requests.POGOProtosNetworkingRequests.Request;
import pm.cat.pogoserv.game.event.impl.DownloadItemTemplatesEvent;
import pm.cat.pogoserv.game.net.request.RequestMapper;

public class DownloadItemTemplatesHandler implements RequestMapper<DownloadItemTemplatesEvent> {

	@Override
	public DownloadItemTemplatesEvent parse(Request req, RequestEnvelope re) throws IOException {
		return new DownloadItemTemplatesEvent();
	}

	@Override
	public Object write(DownloadItemTemplatesEvent re) throws IOException {
		return re.resp;
	}

}
