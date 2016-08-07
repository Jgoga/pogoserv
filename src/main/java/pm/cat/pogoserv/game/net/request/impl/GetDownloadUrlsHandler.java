package pm.cat.pogoserv.game.net.request.impl;

import java.io.IOException;

import POGOProtos.Data.POGOProtosData.DownloadUrlEntry;
import POGOProtos.Networking.Envelopes.POGOProtosNetworkingEnvelopes.RequestEnvelope;
import POGOProtos.Networking.Requests.POGOProtosNetworkingRequests.Request;
import POGOProtos.Networking.Requests.Messages.POGOProtosNetworkingRequestsMessages.GetDownloadUrlsMessage;
import POGOProtos.Networking.Responses.POGOProtosNetworkingResponses.GetDownloadUrlsResponse;
import pm.cat.pogoserv.Log;
import pm.cat.pogoserv.game.config.AssetDef;
import pm.cat.pogoserv.game.event.impl.GetDownloadUrlsEvent;
import pm.cat.pogoserv.game.net.request.RequestMapper;

public class GetDownloadUrlsHandler implements RequestMapper<GetDownloadUrlsEvent> {

	@Override
	public GetDownloadUrlsEvent parse(Request req, RequestEnvelope re) throws IOException {
		GetDownloadUrlsMessage m = GetDownloadUrlsMessage.parseFrom(req.getRequestMessage());
		GetDownloadUrlsEvent ret = new GetDownloadUrlsEvent(m.getAssetIdCount());
		for(int i=0;i<ret.size();i++){
			ret.assetIds[i] = m.getAssetId(i);
		}
		return ret;
	}

	@Override
	public Object write(GetDownloadUrlsEvent re) throws IOException {
		GetDownloadUrlsResponse.Builder resp = GetDownloadUrlsResponse.newBuilder();
		DownloadUrlEntry.Builder e = DownloadUrlEntry.newBuilder();
		
		for(int i=0;i<re.size();i++){
			e.clear();
			AssetDef a = re.assets[i];
			if(a != null){
				String url = re.urls[i];
				e.setAssetId(a.id)
					.setSize(a.size)
					.setChecksum(a.checksum)
					.setUrl(url);
			}else{
				Log.w("DownloadUrls", "Download url for asset not found: %s", re.assetIds[i]);
			}
			
			resp.addDownloadUrls(e);
		}
		
		return resp;
	}

}
