package pm.cat.pogoserv.game.control.event;

import POGOProtos.Enums.POGOProtosEnums.ActivityType;
import POGOProtos.Networking.Responses.POGOProtosNetworkingResponses.CatchPokemonResponse.CatchStatus;
import pm.cat.pogoserv.Log;
import pm.cat.pogoserv.game.event.Listener;
import pm.cat.pogoserv.game.event.impl.CatchPokemonEvent;
import pm.cat.pogoserv.game.model.player.Award;
import pm.cat.pogoserv.game.model.player.Player;
import pm.cat.pogoserv.game.model.world.Encounter;

public class CatchPokemonEventHandler implements Listener<CatchPokemonEvent> {

	@Override
	public boolean on(CatchPokemonEvent t) {
		Player p = t.getPlayer();
		int pokeball = t.pokeballType.getNumber();
		
		if(!p.inventory.containsItem(pokeball)){
			Log.w("Catch", "Dude doesn't have a pokeball (%s)", t.pokeballType);
			t.catchStatus = CatchStatus.CATCH_ERROR;
			return false;
		}
		
		p.inventory.removeItems(pokeball, 1);
		
		if(!t.hitPokemon){
			t.catchStatus = CatchStatus.CATCH_MISSED;
			return false;
		}
		
		// TODO: Some calculations go here
		//       Now we just assume we get every pokemon
		//       Also fleeing should be implemented
		
		Encounter e = p.currentEncounter;
		// If some other thread was writing to currentEncounter and it gets nulled
		// it's the player's own fault for spamming requests
		p.currentEncounter = null;
		
		if(e == null || !e.isValid() || t.encounterId != e.sourceUid){
			Log.w("Catch", "Invalid encounter: " + e + " (request uid: %x)", t.encounterId);
			t.catchStatus = CatchStatus.CATCH_FLEE;
			return false;
		}
		
		p.setEncountered(e.spawn.getActivePokemon());
		e.pokemon.pokeball = t.pokeballType;
		p.inventory.addPokemon(e.pokemon).write();
		
		Log.d("Catch", "Succesfully caught pokemon %s with %s", e.pokemon, t.pokeballType);
		
		t.catchStatus = CatchStatus.CATCH_SUCCESS;
		t.award = new Award();
		t.award.addEntry(ActivityType.ACTIVITY_CATCH_POKEMON_VALUE, 100, 3, 420);
		
		// TODO: ActivityType exp rewards
		
		return true;
	}

}
