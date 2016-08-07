package pm.cat.pogoserv.game.model.world;

import pm.cat.pogoserv.game.config.GameSettings;
import pm.cat.pogoserv.game.model.pokemon.Pokemon;
import pm.cat.pogoserv.util.Random;

public abstract class SpawnPoint extends MapObject {

	protected MapPokemon activePokemon = null;
	
	public SpawnPoint(long uid, double latitude, double longitude) {
		super(uid, latitude, longitude);
	}
	
	public MapPokemon spawnPokemon(Pokemon p, long dur){
		GameSettings settings = game.getSettings();
		return spawnPokemon(
			latitude + settings.spawnOffsetStdDev * Random.nextGaussian(),
			longitude + settings.spawnOffsetStdDev * Random.nextGaussian(),
			p, Random.nextFloat(0.2f, 1.0f), dur);
	}
	
	public MapPokemon spawnPokemon(double lat, double lng, Pokemon p, float parm, long dur){
		MapPokemon mp = new MapPokemon(
			game.getUidGen().next(), lat, lng, this, p, parm, dur);
		mp.init(game);
		return spawnPokemon(mp);
	}
	
	public synchronized MapPokemon spawnPokemon(MapPokemon mp){
		if(activePokemon != null)
			despawnPokemon();
		
		activePokemon = mp;
		game.scheduleAt(this::despawnPokemon, mp.disappearTimestamp);
		return game.getWorld().add(mp);
	}
	
	public synchronized void despawnPokemon(){
		game.getWorld().remove(activePokemon);
		activePokemon = null;
	}
	
	public boolean hasPokemon(){
		return activePokemon != null;
	}
	
	public MapPokemon getActivePokemon(){
		return activePokemon;
	}
	
}
