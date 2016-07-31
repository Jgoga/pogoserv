package pm.cat.pogoserv.game.request.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLiteOrBuilder;

import POGOProtos.Networking.Requests.POGOProtosNetworkingRequests.Request;
import POGOProtos.Networking.Responses.POGOProtosNetworkingResponses.DownloadItemTemplatesResponse;
import pm.cat.pogoserv.Log;
import pm.cat.pogoserv.game.request.GameRequest;
import pm.cat.pogoserv.game.request.RequestHandler;
import pm.cat.pogoserv.util.Util;

public class DownloadItemTemplatesHandler implements RequestHandler {
	
	private DownloadItemTemplatesResponse resp = null;

	@Override
	public MessageLiteOrBuilder run(GameRequest req, Request r) throws InvalidProtocolBufferException {
		if(resp == null){
			Log.d("Templates", "Caching item templates response");
			try{
				byte[] b = Util.readFile(req.game.settings.dataPath + "/GAME_MASTER.protobuf");
				resp = DownloadItemTemplatesResponse.parseFrom(b);
				Log.d("Templates", "Cached item templates (%dK)", b.length/1000);
			}catch(Exception e){
				Log.e("Templates", e);
			}
		}
		
		return resp;
	}

}
