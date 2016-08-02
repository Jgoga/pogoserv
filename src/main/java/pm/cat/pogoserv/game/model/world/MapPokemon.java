package pm.cat.pogoserv.game.model.world;

import pm.cat.pogoserv.game.model.pokemon.Pokemon;

// TODO: MapPokemon in the game seem static. Test if moving pokemon would work.
public class MapPokemon extends MapObject {
	
	//public final long encounterId; // super.uid
	//public String spawnPointId; // source.uid
	
	public final Pokemon pokemon;
	public final SpawnPoint source;
	public long appearTimestamp, disappearTimestamp;
	public float spawnParameter;
	
	public MapPokemon(SpawnPoint source, Pokemon pokemon, float parm, long dts, double lat, double lng, long uid) {
		super(lat, lng, uid);
		this.pokemon = pokemon;
		this.source = source;
		this.spawnParameter = parm;
		this.appearTimestamp = System.currentTimeMillis();
		this.disappearTimestamp = this.appearTimestamp + dts;
	}
	
	@Override
	public String toString(){
		return super.toString() + ": " + pokemon.toString() + " (" + source.getUIDString() + ")";
	}

}
