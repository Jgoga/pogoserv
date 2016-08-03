package pm.cat.pogoserv.game.model.world;

import pm.cat.pogoserv.game.Game;
import pm.cat.pogoserv.game.model.pokemon.Pokemon;
import pm.cat.pogoserv.util.Random;

// TODO: Official spawnpoint ids seem to be non-zero bits of level 15 s2cellid + something
public abstract class SpawnPoint extends MapObject {

	protected MapPokemon activePokemon = null;
	public final String spawnPointId;
	
	public SpawnPoint(double latitude, double longitude, long uid) {
		super(latitude, longitude, uid);
		this.spawnPointId = World.objidString(getS2CellId().parent(WorldCell.LEVEL).id(), uid);
	}
	
	public MapPokemon spawnPokemon(Game game, Pokemon p, long dur){
		return spawnPokemon(game, p, dur,
			latitude + game.settings.spawnOffsetStdDev * Random.nextGaussian(),
			longitude + game.settings.spawnOffsetStdDev * Random.nextGaussian());
	}
	
	public MapPokemon spawnPokemon(Game game, Pokemon p, long dur, double lat, double lng){
		if(activePokemon != null){
			despawnPokemon(game);
		}
		
		game.submit(this::despawnPokemon, dur);
		return activePokemon = game.world.addObject(
				new MapPokemon(this, p, Random.nextFloat(0.2f, 1.0f), dur, lat, lng, game.uidManager.next()));
	}
	
	public void despawnPokemon(Game game){
		game.world.removeObject(activePokemon);
		activePokemon = null;
	}
	
	public boolean hasPokemon(){
		return activePokemon != null;
	}
	
	public MapPokemon getActivePokemon(){
		return activePokemon;
	}
	
}
