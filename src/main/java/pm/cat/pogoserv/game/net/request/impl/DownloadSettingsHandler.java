package pm.cat.pogoserv.game.net.request.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLiteOrBuilder;

import POGOProtos.Networking.Requests.Messages.POGOProtosNetworkingRequestsMessages.DownloadSettingsMessage;
import POGOProtos.Networking.Requests.POGOProtosNetworkingRequests.Request;
import POGOProtos.Networking.Responses.POGOProtosNetworkingResponses.DownloadSettingsResponse;
import POGOProtos.Settings.POGOProtosSettings.FortSettings;
import POGOProtos.Settings.POGOProtosSettings.GlobalSettings;
import POGOProtos.Settings.POGOProtosSettings.InventorySettings;
import POGOProtos.Settings.POGOProtosSettings.MapSettings;
import pm.cat.pogoserv.game.config.GameSettings;
import pm.cat.pogoserv.game.net.request.GameRequest;
import pm.cat.pogoserv.game.net.request.RequestHandler;

public class DownloadSettingsHandler implements RequestHandler {

	@Override
	public MessageLiteOrBuilder run(GameRequest req, Request r) throws InvalidProtocolBufferException {
		DownloadSettingsMessage m = DownloadSettingsMessage.parseFrom(r.getRequestMessage());
		GameSettings gs = req.game.settings;
		DownloadSettingsResponse.Builder resp = DownloadSettingsResponse.newBuilder()
				.setHash(gs.clientSettingsHash);
		
		if(m.getHash() == null || !m.getHash().equals(gs.clientSettingsHash)){
			resp.setSettings(GlobalSettings.newBuilder()
				.setFortSettings(getFortSettings(gs))
				.setMapSettings(getMapSettings(gs))
				// Level settings seems to be currently unused ?
				.setInventorySettings(getInventorySettings(gs))
				.setMinimumClientVersion(gs.minClientVersion));
		}
		
		return resp;
	}
	
	protected FortSettings.Builder getFortSettings(GameSettings gs){
		return FortSettings.newBuilder()
				.setInteractionRangeMeters(gs.fortInteractionRange)
				.setMaxTotalDeployedPokemon(gs.fortMaxTotalDeployedPokemon)
				.setMaxPlayerDeployedPokemon(gs.fortMaxPlayerDeployedPokemon)
				.setDeployStaminaMultiplier(gs.fortDeployStaMultiplier)
				// Attack multiplier seems to be currently unused ?
				.setFarInteractionRangeMeters(gs.fortFarInterctionRange);
	}
	
	protected MapSettings.Builder getMapSettings(GameSettings gs){
		return MapSettings.newBuilder()
				.setPokemonVisibleRange(gs.mapPokemonVisibleRange)
				.setPokeNavRangeMeters(gs.mapPokeNavRange)
				.setEncounterRangeMeters(gs.mapEncounterRange)
				.setGetMapObjectsMinRefreshSeconds(gs.mapGetMapObjectsMinRefreshSeconds)
				.setGetMapObjectsMaxRefreshSeconds(gs.mapGetMapObjectsMaxRefreshSeconds)
				.setGetMapObjectsMinDistanceMeters(gs.mapGetMapObjectsMinDistance)
				.setGoogleMapsApiKey(gs.mapGoogleMapsApiKey);
	}
	
	protected InventorySettings.Builder getInventorySettings(GameSettings gs){
		return InventorySettings.newBuilder()
				.setMaxPokemon(gs.invMaxPokemon)
				.setMaxBagItems(gs.invMaxBagItems)
				.setBasePokemon(gs.invBasePokemon)
				.setBaseBagItems(gs.invBaseBagItems)
				.setBaseEggs(gs.invBaseEggs);
	}

}
