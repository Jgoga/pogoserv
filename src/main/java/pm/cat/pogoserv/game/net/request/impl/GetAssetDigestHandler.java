package pm.cat.pogoserv.game.net.request.impl;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLiteOrBuilder;

import POGOProtos.Data.POGOProtosData.AssetDigestEntry;
import POGOProtos.Networking.Requests.POGOProtosNetworkingRequests.Request;
import POGOProtos.Networking.Requests.Messages.POGOProtosNetworkingRequestsMessages.GetAssetDigestMessage;
import POGOProtos.Networking.Responses.POGOProtosNetworkingResponses.GetAssetDigestResponse;
import pm.cat.pogoserv.game.config.AssetDef;
import pm.cat.pogoserv.game.net.request.GameRequest;
import pm.cat.pogoserv.game.net.request.RequestHandler;

public class GetAssetDigestHandler implements RequestHandler {

	@Override
	public MessageLiteOrBuilder run(GameRequest req, Request r) throws InvalidProtocolBufferException {
		GetAssetDigestMessage m = GetAssetDigestMessage.parseFrom(r.getRequestMessage());
		GetAssetDigestResponse.Builder resp = GetAssetDigestResponse.newBuilder()
				.setTimestampMs(1467338276561000L);
		AssetDigestEntry.Builder e = AssetDigestEntry.newBuilder();
		int plat = m.getPlatformValue();
		for(AssetDef as : req.game.settings.getAssets()){
			if(as.platform != plat)
				continue;
			resp.addDigest(e.clear()
					.setAssetId(as.id)
					.setBundleName(as.bundleName)
					.setVersion(as.version)
					.setChecksum(as.checksum)
					.setSize(as.size)
					.setKey(ByteString.copyFrom(as.key)));
		}
		
		return resp;
	}
	

}
