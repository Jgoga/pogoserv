package pm.cat.pogoserv.game.request;

import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;

import POGOProtos.Networking.Envelopes.POGOProtosNetworkingEnvelopes.RequestEnvelope;
import POGOProtos.Networking.Envelopes.POGOProtosNetworkingEnvelopes.ResponseEnvelope;
import POGOProtos.Networking.Requests.POGOProtosNetworkingRequests.Request;
import POGOProtos.Networking.Requests.POGOProtosNetworkingRequests.RequestType;
import pm.cat.pogoserv.Log;
import pm.cat.pogoserv.game.config.GameSettings;
import pm.cat.pogoserv.game.request.impl.DownloadItemTemplatesHandler;
import pm.cat.pogoserv.game.request.impl.DownloadRemoteConfigVersionHandler;
import pm.cat.pogoserv.game.request.impl.DownloadSettingsHandler;
import pm.cat.pogoserv.game.request.impl.GetAssetDigestHandler;
import pm.cat.pogoserv.game.request.impl.GetDownloadUrlsHandler;
import pm.cat.pogoserv.game.request.impl.GetInventoryHandler;
import pm.cat.pogoserv.game.request.impl.GetMapObjectsHandler;
import pm.cat.pogoserv.game.request.impl.GetPlayerHandler;
import pm.cat.pogoserv.util.Filter;

public class RequestDispatcher implements Filter<GameRequest> {
	
	private final Map<Integer, RequestHandler> handlers = new HashMap<>();
	
	@Override
	public void run(GameRequest req) {
		RequestEnvelope re = req.http.request;
		ResponseEnvelope.Builder resp = req.http.response;
		
		for(int i=0;i<re.getRequestsCount();i++){
			Request r = re.getRequests(i);
			RequestHandler handler = handlers.get(r.getRequestTypeValue());
			if(handler != null){
				try{
					MessageLiteOrBuilder mlb = handler.run(req, r);
					if(mlb != null){
						resp.addReturns( (mlb instanceof MessageLite ? (MessageLite)mlb :
							((MessageLite.Builder)mlb).build()).toByteString() );
						continue;
					}else{
						Log.e("Dispatcher", "Request didn't return anything (%s)", r.getRequestType().name());
					}
				}catch(InvalidProtocolBufferException ibe){
					Log.e("Dispatcher", "Invalid protobuf in request %d (%s)", i, r.getRequestType().name());
					Log.e("Dispatcher", ibe);
				}
			}else{
				Log.e("Dispatcher", "No handler for opcode: %s", r.getRequestType().name());
			}
			
			resp.addReturns(ByteString.EMPTY);
		}
	}
	
	public RequestHandler getHandler(int opcode){
		return handlers.get(opcode);
	}
	
	public RequestDispatcher setHandler(RequestType rt, RequestHandler g){
		return setHandler(rt.getNumber(), g);
	}
	
	public RequestDispatcher setHandler(int opcode, RequestHandler g){
		handlers.put(opcode, g);
		return this;
	}
	
	public RequestDispatcher registerDefaults(){
		setHandler(RequestType.GET_PLAYER, new GetPlayerHandler());
		setHandler(RequestType.DOWNLOAD_SETTINGS, new DownloadSettingsHandler());
		setHandler(RequestType.GET_INVENTORY, new GetInventoryHandler());
		setHandler(RequestType.DOWNLOAD_REMOTE_CONFIG_VERSION, new DownloadRemoteConfigVersionHandler());
		setHandler(RequestType.GET_ASSET_DIGEST, new GetAssetDigestHandler());
		setHandler(RequestType.GET_MAP_OBJECTS, new GetMapObjectsHandler());
		setHandler(RequestType.DOWNLOAD_ITEM_TEMPLATES, new DownloadItemTemplatesHandler());
		setHandler(RequestType.GET_DOWNLOAD_URLS, new GetDownloadUrlsHandler());
		return this;
	}

}
