package pm.cat.pogoserv.game.net.request.impl;

import com.google.protobuf.MessageLiteOrBuilder;

import POGOProtos.Networking.Requests.POGOProtosNetworkingRequests.Request;
import POGOProtos.Networking.Responses.POGOProtosNetworkingResponses.DownloadRemoteConfigVersionResponse;
import pm.cat.pogoserv.game.net.request.GameRequest;
import pm.cat.pogoserv.game.net.request.RequestHandler;

public class DownloadRemoteConfigVersionHandler implements RequestHandler {
	
	private static final long ITEM_TEMPLATES_TS = 1468540960537L;
	private static final long ASSET_DIGEST_TS = 1467338276561000L; // Protos say it's milliseconds but clearly it's not
	
	@Override
	public MessageLiteOrBuilder run(GameRequest req, Request r) {
		// TODO: Actually implement this
		// this is a dummy atm
		return DownloadRemoteConfigVersionResponse.newBuilder()
				.setItemTemplatesTimestampMs(ITEM_TEMPLATES_TS)
				.setAssetDigestTimestampMs(ASSET_DIGEST_TS)
				.setResult(DownloadRemoteConfigVersionResponse.Result.SUCCESS);
	}

}
