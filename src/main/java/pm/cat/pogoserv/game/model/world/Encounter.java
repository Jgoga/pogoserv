package pm.cat.pogoserv.game.model.world;

import pm.cat.pogoserv.game.model.player.InventoryPokemon;

public class Encounter {
	
	public final SpawnPoint spawn;
	public final InventoryPokemon pokemon;
	public final long sourceUid;
	
	public Encounter(SpawnPoint spawn, InventoryPokemon pokemon, long sourceUid){
		this.spawn = spawn;
		this.pokemon = pokemon;
		this.sourceUid = sourceUid;
	}
	
	public boolean isValid(){
		MapPokemon mp = spawn.getActivePokemon();
		return mp != null && sourceUid == mp.getUID();
	}
	
}
