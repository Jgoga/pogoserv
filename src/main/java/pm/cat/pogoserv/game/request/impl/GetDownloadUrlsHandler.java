package pm.cat.pogoserv.game.request.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLiteOrBuilder;

import POGOProtos.Data.POGOProtosData.DownloadUrlEntry;
import POGOProtos.Networking.Requests.POGOProtosNetworkingRequests.Request;
import POGOProtos.Networking.Requests.Messages.POGOProtosNetworkingRequestsMessages.GetDownloadUrlsMessage;
import POGOProtos.Networking.Responses.POGOProtosNetworkingResponses.GetDownloadUrlsResponse;
import pm.cat.pogoserv.Log;
import pm.cat.pogoserv.core.Constants;
import pm.cat.pogoserv.game.config.AssetDef;
import pm.cat.pogoserv.game.request.GameRequest;
import pm.cat.pogoserv.game.request.RequestHandler;

public class GetDownloadUrlsHandler implements RequestHandler {
	
	@Override
	public MessageLiteOrBuilder run(GameRequest req, Request r) throws InvalidProtocolBufferException {
		GetDownloadUrlsMessage msg = GetDownloadUrlsMessage.parseFrom(r.getRequestMessage());
		GetDownloadUrlsResponse.Builder resp = GetDownloadUrlsResponse.newBuilder();
		DownloadUrlEntry.Builder dle = DownloadUrlEntry.newBuilder();
		
		for(String s : msg.getAssetIdList()){
			dle.clear();
			AssetDef ad = req.game.settings.getAsset(s);
			if(ad != null){
				dle.setAssetId(ad.id)
					.setSize(ad.size)
					.setChecksum(ad.checksum)
					.setUrl(req.game.settings.assetHostPrefix + "/" + Constants.ASSET_VERSION + "/" 
							+ ad.platform + "/" + ad.id.replace('/', '-'));
			}else{
				Log.w("DLUrls", "Requested asset not found: %s", s);
			}
				
			resp.addDownloadUrls(dle);
		}
		
		return resp;
	}

}
