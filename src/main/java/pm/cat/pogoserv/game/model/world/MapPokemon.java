package pm.cat.pogoserv.game.model.world;

import pm.cat.pogoserv.game.model.Pokemon;

public class MapPokemon extends DynamicMapObject {
	
	//public final long encounterId; // super.uid
	//public String spawnPointId; // source.uid
	
	public final Pokemon pokemon;
	public final SpawnPoint source;
	public long appearTimestamp, disappearTimestamp;
	
	public MapPokemon(SpawnPoint source, Pokemon pokemon, long dts, double lat, double lng, long uid) {
		super(lat, lng, uid);
		this.pokemon = pokemon;
		this.source = source;
		this.appearTimestamp = System.currentTimeMillis();
		this.disappearTimestamp = this.appearTimestamp + dts;
	}
	
	@Override
	public String toString(){
		return super.toString() + ": " + pokemon.toString() + " (" + source.toString() + ")";
	}

}
