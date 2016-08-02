package pm.cat.pogoserv.game.net.request.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLiteOrBuilder;

import POGOProtos.Data.Capture.POGOProtosDataCapture.CaptureProbability;
import POGOProtos.Inventory.Item.POGOProtosInventoryItem.ItemId;
import POGOProtos.Map.Pokemon.POGOProtosMapPokemon.WildPokemon;
import POGOProtos.Networking.Requests.POGOProtosNetworkingRequests.Request;
import POGOProtos.Networking.Requests.Messages.POGOProtosNetworkingRequestsMessages.EncounterMessage;
import POGOProtos.Networking.Responses.POGOProtosNetworkingResponses.EncounterResponse;
import pm.cat.pogoserv.Log;
import pm.cat.pogoserv.game.model.player.InventoryPokemon;
import pm.cat.pogoserv.game.model.world.Encounter;
import pm.cat.pogoserv.game.model.world.MapObject;
import pm.cat.pogoserv.game.model.world.MapPokemon;
import pm.cat.pogoserv.game.model.world.SpawnPoint;
import pm.cat.pogoserv.game.net.ProtobufMapper;
import pm.cat.pogoserv.game.net.request.GameRequest;
import pm.cat.pogoserv.game.net.request.RequestHandler;

public class EncounterHandler implements RequestHandler {

	@Override
	public MessageLiteOrBuilder run(GameRequest req, Request r) throws InvalidProtocolBufferException {
		EncounterMessage m = EncounterMessage.parseFrom(r.getRequestMessage());
		EncounterResponse.Builder resp = EncounterResponse.newBuilder();
		
		String spawnPointId = m.getSpawnPointId();
		MapObject spawnM = req.game.world.objectForStr(spawnPointId);
		if(!(spawnM instanceof SpawnPoint)){
			Log.w("Encounter", "Not a spawn point: " + spawnM);
			return resp.setStatus(EncounterResponse.Status.ENCOUNTER_NOT_FOUND);
		}
		
		SpawnPoint spawn = (SpawnPoint) spawnM;
		MapPokemon mp = spawn.getActivePokemon();
		
		if(mp == null){
			Log.w("Encounter", "Null pokemon");
			return resp.setStatus(EncounterResponse.Status.ENCOUNTER_NOT_FOUND);
		}
			
		if(System.currentTimeMillis() >= mp.disappearTimestamp){
			Log.w("Encounter", "Requested already disappeared pokemon");
			return resp.setStatus(EncounterResponse.Status.ENCOUNTER_CLOSED);
		}
			
		long uid = mp.getUID();
		if(uid != m.getEncounterId()){
			Log.w("Encounter", "Invalid encounter id. actual=%x, request=%x", uid, m.getEncounterId());
			return resp.setStatus(EncounterResponse.Status.ENCOUNTER_NOT_FOUND);
		}
		
		if(req.player.hasEncountered(uid)){
			Log.w("Encounter", "Encounter (%x, %x) already happened.", req.player.getUID(), uid);
			return resp.setStatus(EncounterResponse.Status.ENCOUNTER_ALREADY_HAPPENED);
		}
		
		if(req.player.distanceTo(mp) > req.game.settings.mapEncounterRange){
			Log.w("Encounter", "Too far. Distance=%.2f, max=%.2f", req.player.distanceTo(mp), req.game.settings.mapEncounterRange);
			return resp.setStatus(EncounterResponse.Status.ENCOUNTER_NOT_IN_RANGE);
		}
		
		if(req.player.inventory.uniqueItemCount() >= req.player.inventory.maxPokemonStorage){
			Log.w("Encounter", "No space (%d)", req.player.inventory.uniqueItemCount());
			return resp.setStatus(EncounterResponse.Status.POKEMON_INVENTORY_FULL);
		}
		
		InventoryPokemon p = req.game.pokegen.createEncounter(mp, req.player, req.game.uidManager.next());
		p.capturedCellId = mp.getS2CellId().id();
		req.player.currentEncounter = new Encounter(spawn, p, mp.getUID());
		
		return resp.setStatus(EncounterResponse.Status.ENCOUNTER_SUCCESS)
			.setWildPokemon(ProtobufMapper.wildPokemon(WildPokemon.newBuilder(), mp, true))
			.setBackground(EncounterResponse.Background.PARK)
			.setCaptureProbability(getCaptureProbability(mp));
	}
	
	// TODO: This needs research. How is this calculated/determined?
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

}
