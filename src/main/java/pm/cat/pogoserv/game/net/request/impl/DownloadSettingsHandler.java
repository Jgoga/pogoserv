package pm.cat.pogoserv.game.net.request.impl;

import java.io.IOException;

import POGOProtos.Networking.Envelopes.POGOProtosNetworkingEnvelopes.RequestEnvelope;
import POGOProtos.Networking.Requests.POGOProtosNetworkingRequests.Request;
import POGOProtos.Networking.Requests.Messages.POGOProtosNetworkingRequestsMessages.DownloadSettingsMessage;
import POGOProtos.Networking.Responses.POGOProtosNetworkingResponses.DownloadSettingsResponse;
import POGOProtos.Settings.POGOProtosSettings.FortSettings;
import POGOProtos.Settings.POGOProtosSettings.GlobalSettings;
import POGOProtos.Settings.POGOProtosSettings.InventorySettings;
import POGOProtos.Settings.POGOProtosSettings.MapSettings;
import pm.cat.pogoserv.game.config.GameSettings;
import pm.cat.pogoserv.game.event.impl.DownloadSettingsEvent;
import pm.cat.pogoserv.game.net.request.RequestMapper;

public class DownloadSettingsHandler implements RequestMapper<DownloadSettingsEvent> {
	
	@Override
	public DownloadSettingsEvent parse(Request req, RequestEnvelope re) throws IOException {
		return new DownloadSettingsEvent(
				DownloadSettingsMessage.parseFrom(req.getRequestMessage()).getHash());
	}

	@Override
	public Object write(DownloadSettingsEvent re) throws IOException {
		DownloadSettingsResponse.Builder resp = DownloadSettingsResponse.newBuilder();
		if(re.newHash != null){
			GameSettings gs = re.settings;
			resp.setHash(re.newHash)
				.setSettings(GlobalSettings.newBuilder()
						.setFortSettings(getFortSettings(gs))
						.setMapSettings(getMapSettings(gs))
						// Level settings seems to be currently unused ?
						.setInventorySettings(getInventorySettings(gs))
						.setMinimumClientVersion(gs.minClientVersion));
		}else{
			resp.setHash(re.hash);
		}
		return resp;
	}
	
	private FortSettings.Builder getFortSettings(GameSettings gs){
		return FortSettings.newBuilder()
				.setInteractionRangeMeters(gs.fortInteractionRange)
				.setMaxTotalDeployedPokemon(gs.fortMaxTotalDeployedPokemon)
				.setMaxPlayerDeployedPokemon(gs.fortMaxPlayerDeployedPokemon)
				.setDeployStaminaMultiplier(gs.fortDeployStaMultiplier)
				// Attack multiplier seems to be currently unused ?
				.setFarInteractionRangeMeters(gs.fortFarInterctionRange);
	}
	
	private MapSettings.Builder getMapSettings(GameSettings gs){
		return MapSettings.newBuilder()
				.setPokemonVisibleRange(gs.mapPokemonVisibleRange)
				.setPokeNavRangeMeters(gs.mapPokeNavRange)
				.setEncounterRangeMeters(gs.mapEncounterRange)
				.setGetMapObjectsMinRefreshSeconds(gs.mapGetMapObjectsMinRefreshSeconds)
				.setGetMapObjectsMaxRefreshSeconds(gs.mapGetMapObjectsMaxRefreshSeconds)
				.setGetMapObjectsMinDistanceMeters(gs.mapGetMapObjectsMinDistance)
				.setGoogleMapsApiKey(gs.mapGoogleMapsApiKey);
	}
	
	private InventorySettings.Builder getInventorySettings(GameSettings gs){
		return InventorySettings.newBuilder()
				.setMaxPokemon(gs.invMaxPokemon)
				.setMaxBagItems(gs.invMaxBagItems)
				.setBasePokemon(gs.invBasePokemon)
				.setBaseBagItems(gs.invBaseBagItems)
				.setBaseEggs(gs.invBaseEggs);
	}

}
