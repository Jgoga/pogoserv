package pm.cat.pogoserv.game.net.request.impl;

import java.io.IOException;

import com.google.protobuf.ByteString;

import POGOProtos.Data.POGOProtosData.AssetDigestEntry;
import POGOProtos.Networking.Envelopes.POGOProtosNetworkingEnvelopes.RequestEnvelope;
import POGOProtos.Networking.Requests.POGOProtosNetworkingRequests.Request;
import POGOProtos.Networking.Requests.Messages.POGOProtosNetworkingRequestsMessages.GetAssetDigestMessage;
import POGOProtos.Networking.Responses.POGOProtosNetworkingResponses.GetAssetDigestResponse;
import pm.cat.pogoserv.game.config.AssetDef;
import pm.cat.pogoserv.game.event.impl.GetAssetDigestEvent;
import pm.cat.pogoserv.game.net.request.RequestMapper;

public class GetAssetDigestHandler implements RequestMapper<GetAssetDigestEvent> {

	@Override
	public GetAssetDigestEvent parse(Request req, RequestEnvelope re) throws IOException {
		GetAssetDigestMessage m = GetAssetDigestMessage.parseFrom(req.getRequestMessage());
		return new GetAssetDigestEvent(
				m.getPlatform(),
				m.getDeviceManufacturer(),
				m.getDeviceModel(),
				m.getLocale(),
				m.getAppVersion());
	}

	@Override
	public Object write(GetAssetDigestEvent re) throws IOException {
		GetAssetDigestResponse.Builder resp = GetAssetDigestResponse.newBuilder()
				.setTimestampMs(re.timestamp);
		AssetDigestEntry.Builder e = AssetDigestEntry.newBuilder();
		while(re.assets.hasNext()){
			AssetDef a = re.assets.next();
			resp.addDigest(e.clear()
					.setAssetId(a.id)
					.setBundleName(a.bundleName)
					.setVersion(a.version)
					.setChecksum(a.checksum)
					.setSize(a.size)
					.setKey(ByteString.copyFrom(a.key)));
		}
		return resp;
	}
	

}
