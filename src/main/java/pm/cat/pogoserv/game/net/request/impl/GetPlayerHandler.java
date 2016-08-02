package pm.cat.pogoserv.game.net.request.impl;

import com.google.protobuf.MessageLiteOrBuilder;

import POGOProtos.Data.POGOProtosData.PlayerData;
import POGOProtos.Data.Player.POGOProtosDataPlayer.Currency;
import POGOProtos.Data.Player.POGOProtosDataPlayer.PlayerAvatar;
import POGOProtos.Enums.POGOProtosEnums.TutorialState;
import POGOProtos.Networking.Requests.POGOProtosNetworkingRequests.Request;
import POGOProtos.Networking.Responses.POGOProtosNetworkingResponses.GetPlayerResponse;
import pm.cat.pogoserv.core.Constants;
import pm.cat.pogoserv.game.model.player.Appearance;
import pm.cat.pogoserv.game.model.player.Player;
import pm.cat.pogoserv.game.net.request.GameRequest;
import pm.cat.pogoserv.game.net.request.RequestHandler;

public class GetPlayerHandler implements RequestHandler {

	@Override
	public MessageLiteOrBuilder run(GameRequest req, Request r) {
		Player p = req.player;
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
				.addCurrencies(Currency.newBuilder().setName(Constants.STARDUST).setAmount(p.stardust.read().amt))
				.addCurrencies(Currency.newBuilder().setName(Constants.POKECOINS).setAmount(p.pokecoins.read().amt));
		
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
