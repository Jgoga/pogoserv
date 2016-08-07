package pm.cat.pogoserv.game.net.request.impl;

import java.io.IOException;

import POGOProtos.Networking.Envelopes.POGOProtosNetworkingEnvelopes.RequestEnvelope;
import POGOProtos.Networking.Requests.POGOProtosNetworkingRequests.Request;
import POGOProtos.Networking.Requests.Messages.POGOProtosNetworkingRequestsMessages.DownloadRemoteConfigVersionMessage;
import POGOProtos.Networking.Responses.POGOProtosNetworkingResponses.DownloadRemoteConfigVersionResponse;
import pm.cat.pogoserv.game.event.impl.DownloadRemoteConfigVersionEvent;
import pm.cat.pogoserv.game.net.request.RequestMapper;

public class DownloadRemoteConfigVersionHandler implements RequestMapper<DownloadRemoteConfigVersionEvent> {

	@Override
	public DownloadRemoteConfigVersionEvent parse(Request req, RequestEnvelope re) throws IOException {
		DownloadRemoteConfigVersionMessage m = DownloadRemoteConfigVersionMessage.parseFrom(req.getRequestMessage());
		return new DownloadRemoteConfigVersionEvent(
				m.getPlatform(),
				m.getDeviceManufacturer(),
				m.getDeviceModel(),
				m.getLocale(),
				m.getAppVersion());
	}

	@Override
	public Object write(DownloadRemoteConfigVersionEvent re) throws IOException {
		return DownloadRemoteConfigVersionResponse.newBuilder()
				.setResult(re.itemTemplatesTimestamp > 0 && re.assetDigestTimestamp > 0 ?
						DownloadRemoteConfigVersionResponse.Result.SUCCESS :
						DownloadRemoteConfigVersionResponse.Result.UNSET)
				.setItemTemplatesTimestampMs(re.itemTemplatesTimestamp)
				.setAssetDigestTimestampMs(re.assetDigestTimestamp);
	}

}
