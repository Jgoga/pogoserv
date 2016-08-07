package pm.cat.pogoserv.game.net.request.impl;

import java.io.IOException;

import POGOProtos.Data.POGOProtosData.PokemonData;
import POGOProtos.Map.Pokemon.POGOProtosMapPokemon.WildPokemon;
import POGOProtos.Networking.Envelopes.POGOProtosNetworkingEnvelopes.RequestEnvelope;
import POGOProtos.Networking.Requests.POGOProtosNetworkingRequests.Request;
import POGOProtos.Networking.Requests.Messages.POGOProtosNetworkingRequestsMessages.EncounterMessage;
import POGOProtos.Networking.Responses.POGOProtosNetworkingResponses.EncounterResponse;
import pm.cat.pogoserv.game.event.impl.EncounterEvent;
import pm.cat.pogoserv.game.net.ProtobufMapper;
import pm.cat.pogoserv.game.net.request.RequestMapper;

public class EncounterHandler implements RequestMapper<EncounterEvent> {

	@Override
	public EncounterEvent parse(Request req, RequestEnvelope re) throws IOException {
		EncounterMessage m = EncounterMessage.parseFrom(req.getRequestMessage());
		return new EncounterEvent(m.getEncounterId(), m.getSpawnPointId());
	}

	@Override
	public Object write(EncounterEvent re) throws IOException {
		EncounterResponse.Builder resp = EncounterResponse.newBuilder()
				.setStatus(re.status);
		if(re.status == EncounterResponse.Status.ENCOUNTER_SUCCESS){
			resp.setWildPokemon(ProtobufMapper.wildPokemon(WildPokemon.newBuilder(), re.mapPokemon)
					.setPokemonData(ProtobufMapper.instancedPokemon(PokemonData.newBuilder(), re.instancedPokemon)))
				.setBackground(re.background);
				// TODO .setCaptureProbability
		}
		return resp;
	}
	
	/* TODO - is this needed
	private CaptureProbability.Builder getCaptureProbability(MapPokemon mp){
		CaptureProbability.Builder ret = CaptureProbability.newBuilder();
		ret.addPokeballType(ItemId.ITEM_POKE_BALL);
		ret.addPokeballType(ItemId.ITEM_GREAT_BALL);
		ret.addPokeballType(ItemId.ITEM_ULTRA_BALL);
		ret.addCaptureProbability(1.0f);
		ret.addCaptureProbability(1.0f);
		ret.addCaptureProbability(1.0f);
		return ret;
	}
	*/

}
