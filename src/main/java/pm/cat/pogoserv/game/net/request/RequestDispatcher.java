package pm.cat.pogoserv.game.net.request;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLite;

import POGOProtos.Networking.Envelopes.POGOProtosNetworkingEnvelopes.RequestEnvelope;
import POGOProtos.Networking.Envelopes.POGOProtosNetworkingEnvelopes.ResponseEnvelope;
import POGOProtos.Networking.Requests.POGOProtosNetworkingRequests.Request;
import POGOProtos.Networking.Requests.POGOProtosNetworkingRequests.RequestType;
import pm.cat.pogoserv.Log;
import pm.cat.pogoserv.game.Game;
import pm.cat.pogoserv.game.event.Event;
import pm.cat.pogoserv.game.event.impl.GetPlayerEvent;
import pm.cat.pogoserv.game.model.player.Player;
import pm.cat.pogoserv.game.net.POGORequest;
import pm.cat.pogoserv.game.net.request.impl.CatchPokemonHandler;
import pm.cat.pogoserv.game.net.request.impl.DownloadItemTemplatesHandler;
import pm.cat.pogoserv.game.net.request.impl.DownloadRemoteConfigVersionHandler;
import pm.cat.pogoserv.game.net.request.impl.DownloadSettingsHandler;
import pm.cat.pogoserv.game.net.request.impl.EncounterHandler;
import pm.cat.pogoserv.game.net.request.impl.GetAssetDigestHandler;
import pm.cat.pogoserv.game.net.request.impl.GetDownloadUrlsHandler;
import pm.cat.pogoserv.game.net.request.impl.GetInventoryHandler;
import pm.cat.pogoserv.game.net.request.impl.GetMapObjectsHandler;
import pm.cat.pogoserv.game.net.request.impl.GetPlayerHandler;

public class RequestDispatcher {
	
	private final Game game;
	private final Map<Integer, RequestMapper<? extends Event>> mappers = new HashMap<>();
	
	public RequestDispatcher(Game game){
		this.game = game;
	}
	
	public void dispatch(POGORequest re, Player p) throws IOException {
		RequestEnvelope req = re.req;
		ResponseEnvelope.Builder resp = re.resp;
		
		for(int i=0;i<req.getRequestsCount();i++){
			Request r = req.getRequests(i);
			RequestMapper<? extends Event> m = mappers.get(r.getRequestTypeValue());
			
			if(m != null){
				
				// This is a really ugly way to do it
				// but for replies with constant responses (like DownloadItemTemplates)
				// it's stupid to generate the byte buffer again every time
				// Also I don't want protobuf code in the game.control.event package
				// Why doesn't protobuf have a ByteStringable interface or something like that :(
				Object o = handleRequest(m, r, req, p);
				
				if(o instanceof MessageLite.Builder)
					o = ((MessageLite.Builder)o).build();
				if(o instanceof MessageLite)
					o = ((MessageLite)o).toByteString();
				if(o instanceof byte[])
					o = ByteString.copyFrom((byte[]) o);
				if(o instanceof ByteString){
					resp.addReturns((ByteString) o);
					continue;
				}else{
					Log.w("RequestDispatcher", "%s: Handler returned incompatible object: %s", m, o);
				}
				
			}else{
				Log.w("RequestDispatcher", "No handler found for request: %s", r.getRequestType());
			}
			resp.addReturns(ByteString.EMPTY);
		}
	}
	
	private <T extends Event> Object handleRequest(RequestMapper<T> m, Request r, RequestEnvelope re, Player p) throws IOException {
		T evt = m.parse(r, re);
		if(evt == null){
			Log.w("RequestDispatcher", "Null event received from handler %s", m);
			return null;
		}
		
		evt.setPlayer(p);
		game.getEventListeners().submit(evt);
		return m.write(evt);
	}
	
	public void setHandler(RequestType r, RequestMapper<? extends Event> m){
		setHandler(r.getNumber(), m);
	}
	
	public void setHandler(int opcode, RequestMapper<? extends Event> m){
		mappers.put(opcode, m);
	}
	
	public void registerDefaults(){
		setHandler(RequestType.GET_PLAYER, new GetPlayerHandler());
		setHandler(RequestType.DOWNLOAD_SETTINGS, new DownloadSettingsHandler());
		setHandler(RequestType.GET_INVENTORY, new GetInventoryHandler());
		setHandler(RequestType.DOWNLOAD_REMOTE_CONFIG_VERSION, new DownloadRemoteConfigVersionHandler());
		setHandler(RequestType.GET_ASSET_DIGEST, new GetAssetDigestHandler());
		setHandler(RequestType.GET_MAP_OBJECTS, new GetMapObjectsHandler());
		setHandler(RequestType.DOWNLOAD_ITEM_TEMPLATES, new DownloadItemTemplatesHandler());
		setHandler(RequestType.GET_DOWNLOAD_URLS, new GetDownloadUrlsHandler());
		setHandler(RequestType.ENCOUNTER, new EncounterHandler());
		setHandler(RequestType.CATCH_POKEMON, new CatchPokemonHandler());
	}
	
}
