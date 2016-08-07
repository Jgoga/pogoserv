package pm.cat.pogoserv.game.control.event;

import POGOProtos.Networking.Responses.POGOProtosNetworkingResponses.EncounterResponse.Background;
import POGOProtos.Networking.Responses.POGOProtosNetworkingResponses.EncounterResponse.Status;
import pm.cat.pogoserv.Log;
import pm.cat.pogoserv.game.event.Listener;
import pm.cat.pogoserv.game.event.impl.EncounterEvent;
import pm.cat.pogoserv.game.model.player.InventoryPokemon;
import pm.cat.pogoserv.game.model.player.Player;
import pm.cat.pogoserv.game.model.world.Encounter;
import pm.cat.pogoserv.game.model.world.MapPokemon;
import pm.cat.pogoserv.game.model.world.SpawnPoint;
import pm.cat.pogoserv.game.model.world.UniqueLocatable;

public class EncounterEventHandler implements Listener<EncounterEvent> {

	@Override
	public boolean on(EncounterEvent t) {
		UniqueLocatable spawnM = t.getGame().getWorld().objectForStr(t.spawnPointId);
		
		if(!(spawnM instanceof SpawnPoint)){
			Log.w("Encounter", "Not a spawn point: " + spawnM);
			t.status = Status.ENCOUNTER_NOT_FOUND;
			return false;
		}
		
		SpawnPoint spawn = (SpawnPoint) spawnM;
		MapPokemon mp = spawn.getActivePokemon();
		
		if(mp == null){
			Log.w("Encounter", "Null pokemon");
			t.status = Status.ENCOUNTER_NOT_FOUND;
			return false;
		}
			
		if(System.currentTimeMillis() >= mp.disappearTimestamp){
			Log.w("Encounter", "Requested already disappeared pokemon");
			t.status = Status.ENCOUNTER_CLOSED;
			return false;
		}
			
		long uid = mp.getUID();
		if(uid != t.encounterId){
			Log.w("Encounter", "Invalid encounter id. actual=%x, request=%x", uid, t.encounterId);
			t.status = Status.ENCOUNTER_NOT_FOUND;
			return false;
		}
		
		Player p = t.getPlayer();
		if(p.hasEncountered(uid)){
			Log.w("Encounter", "Encounter (%x, %x) already happened.", p.getUID(), uid);
			t.status = Status.ENCOUNTER_ALREADY_HAPPENED;
			return false;
		}
		
		if(p.distanceTo(mp) > t.getGame().getSettings().mapEncounterRange){
			Log.w("Encounter", "Too far. Distance=%.2f, max=%.2f", p.distanceTo(mp), t.getGame().getSettings().mapEncounterRange);
			t.status = Status.ENCOUNTER_NOT_IN_RANGE;
			return false;
		}
		
		if(p.inventory.uniqueItemCount() >= p.inventory.maxPokemonStorage){
			Log.w("Encounter", "No space (%d)", p.inventory.uniqueItemCount());
			t.status = Status.POKEMON_INVENTORY_FULL;
			return false;
		}
		
		InventoryPokemon poke = t.getGame().getWorld().getPokegen().createEncounter(mp, p);
		poke.setFullStamina();
		poke.capturedCellId = mp.getS2CellId().id();
		p.currentEncounter = new Encounter(spawn, poke, mp.getUID());
		
		t.status = Status.ENCOUNTER_SUCCESS;
		t.background = Background.PARK;
		t.mapPokemon = mp;
		t.instancedPokemon = poke;
		
		return true;
	}

}
