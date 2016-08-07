package pm.cat.pogoserv.game.event.impl;

import POGOProtos.Data.Capture.POGOProtosDataCapture.CaptureProbability;
import POGOProtos.Networking.Responses.POGOProtosNetworkingResponses.EncounterResponse.Background;
import POGOProtos.Networking.Responses.POGOProtosNetworkingResponses.EncounterResponse.Status;
import pm.cat.pogoserv.game.event.Event;
import pm.cat.pogoserv.game.model.pokemon.InstancedPokemon;
import pm.cat.pogoserv.game.model.world.MapPokemon;

public class EncounterEvent extends Event {
	
	public final long encounterId;
	public final String spawnPointId;
	// lat & lng not stored since they are already in Player
	
	public Status status;
	public Background background;
	public CaptureProbability captureProbability;
	public MapPokemon mapPokemon;
	public InstancedPokemon instancedPokemon;
	
	public EncounterEvent(long encounterId, String spawnPointId){
		this.encounterId = encounterId;
		this.spawnPointId = spawnPointId;
	}
	
}
