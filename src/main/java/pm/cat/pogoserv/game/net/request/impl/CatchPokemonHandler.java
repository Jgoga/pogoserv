package pm.cat.pogoserv.game.net.request.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLiteOrBuilder;

import POGOProtos.Data.Capture.POGOProtosDataCapture.CaptureAward;
import POGOProtos.Enums.POGOProtosEnums.ActivityType;
import POGOProtos.Networking.Requests.POGOProtosNetworkingRequests.Request;
import POGOProtos.Networking.Requests.Messages.POGOProtosNetworkingRequestsMessages.CatchPokemonMessage;
import POGOProtos.Networking.Responses.POGOProtosNetworkingResponses.CatchPokemonResponse;
import pm.cat.pogoserv.Log;
import pm.cat.pogoserv.game.model.player.Player;
import pm.cat.pogoserv.game.model.world.Encounter;
import pm.cat.pogoserv.game.net.request.GameRequest;
import pm.cat.pogoserv.game.net.request.RequestHandler;

public class CatchPokemonHandler implements RequestHandler {

	@Override
	public MessageLiteOrBuilder run(GameRequest req, Request r) throws InvalidProtocolBufferException {
		CatchPokemonMessage m = CatchPokemonMessage.parseFrom(r.getRequestMessage());
		CatchPokemonResponse.Builder resp = CatchPokemonResponse.newBuilder();
		
		if(!m.getHitPokemon())
			return resp.setStatus(CatchPokemonResponse.CatchStatus.CATCH_MISSED);
		
		// TODO: Some calculations go here
		//       Now we just assume we get every pokemon
		//       Also fleeing should be implemented
		
		Player p = req.player;
		Encounter e = p.currentEncounter;
		// If some other thread was writing to currentEncounter and it gets nulled
		// it's the player's own fault for spamming requests
		p.currentEncounter = null;
		
		if(e == null || !e.isValid() || m.getEncounterId() != e.sourceUid){
			Log.w("Catch", "Invalid encounter: " + e + " (request uid: %x)", m.getEncounterId());
			return resp.setStatus(CatchPokemonResponse.CatchStatus.CATCH_FLEE);
		}
		
		p.setEncountered(e.spawn.getActivePokemon());
		e.pokemon.pokeball = m.getPokeball();
		p.inventory.addPokemon(e.pokemon).write();
		
		// TODO: ActivityType exp rewards
		
		return resp
			.setCaptureAward(CaptureAward.newBuilder()
				.addActivityType(ActivityType.ACTIVITY_CATCH_POKEMON)
				.addXp(100)
				.addCandy(3)
				.addStardust(0))
			.setCapturedPokemonId(e.pokemon.getUID())
			.setStatus(CatchPokemonResponse.CatchStatus.CATCH_SUCCESS);
	}

}
