package pm.cat.pogoserv.game.net.request.impl;

import java.io.IOException;

import POGOProtos.Data.POGOProtosData.PlayerData;
import POGOProtos.Data.Player.POGOProtosDataPlayer.Currency;
import POGOProtos.Data.Player.POGOProtosDataPlayer.PlayerAvatar;
import POGOProtos.Enums.POGOProtosEnums.TutorialState;
import POGOProtos.Networking.Envelopes.POGOProtosNetworkingEnvelopes.RequestEnvelope;
import POGOProtos.Networking.Requests.POGOProtosNetworkingRequests.Request;
import POGOProtos.Networking.Responses.POGOProtosNetworkingResponses.GetPlayerResponse;
import pm.cat.pogoserv.Config;
import pm.cat.pogoserv.game.event.impl.GetPlayerEvent;
import pm.cat.pogoserv.game.model.player.Appearance;
import pm.cat.pogoserv.game.model.player.Player;
import pm.cat.pogoserv.game.net.request.RequestMapper;

public class GetPlayerHandler implements RequestMapper<GetPlayerEvent> {

	@Override
	public GetPlayerEvent parse(Request req, RequestEnvelope re) throws IOException {
		return new GetPlayerEvent();
	}

	@Override
	public Object write(GetPlayerEvent re) throws IOException {
		Player p = re.getPlayer();
		PlayerData.Builder ret = PlayerData.newBuilder()
				.setCreationTimestampMs(p.creationTs)
				.setUsername(p.nickname)
				.setTeam(p.team)
				// TODO Actually implement tutorial. They are just skipped for now.
				.setAvatar(getAvatar(p.appearance))
				.setMaxPokemonStorage(p.inventory.getMaxPokemonStorage())
				.setMaxItemStorage(p.inventory.getMaxItemStorage())
				// TODO Daily bonus
				// TODO Equipped badge
				// TODO Contact settings
				.addCurrencies(Currency.newBuilder().setName(Config.STARDUST).setAmount(p.stardust.read().amt))
				.addCurrencies(Currency.newBuilder().setName(Config.POKECOINS).setAmount(p.pokecoins.read().amt));
		
		for(TutorialState ts : TutorialState.values()){
			if(ts != TutorialState.UNRECOGNIZED)
				ret.addTutorialState(ts);
		}
		
		return GetPlayerResponse.newBuilder()
			.setSuccess(true)
			.setPlayerData(ret);
	}
	
	protected PlayerAvatar.Builder getAvatar(Appearance a){
		return PlayerAvatar.newBuilder()
			.setSkin(a.skin)
			.setHair(a.hair)
			.setShirt(a.shirt)
			.setPants(a.pants)
			.setHat(a.hat)
			.setShoes(a.shoes)
			.setGender(a.gender)
			.setEyes(a.eyes)
			.setBackpack(a.backpack);
	}

}
