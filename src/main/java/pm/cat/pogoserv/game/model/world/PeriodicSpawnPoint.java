package pm.cat.pogoserv.game.model.world;

import java.util.Arrays;
import java.util.concurrent.Future;

import pm.cat.pogoserv.game.Game;
import pm.cat.pogoserv.game.GameRunnable;
import pm.cat.pogoserv.util.Random;

// These are the default spawn points (and the only kind that exist currently in pokemon go)
// They just spawn a pokemon at a fixed interval (usually 1 hour)
// TODO: Not all pokemon have the same chance, this needs to be fixed but it's not high priority
//       Also, offsets. Can be implemented by just adding an offset parameter to Game.submitFixed
public class PeriodicSpawnPoint extends SpawnPoint implements GameRunnable {

	private final long period;
	private final long spawnLength;
	private final int[] pokemon;
	private Future<?> future;
	
	public PeriodicSpawnPoint(long period, long spawnLenght, int[] pokemon, double latitude, double longitude, long uid) {
		super(latitude, longitude, uid);
		this.period = period;
		this.spawnLength = spawnLenght;
		this.pokemon = pokemon;
	}

	@Override
	public void onAdd(Game game, WorldCell cell) {
		future = game.submitFixed(this, period);
	}

	@Override
	public void onRemove(Game game, WorldCell cell) {
		if(future != null)
			future.cancel(false);
	}

	@Override
	public void run(Game game) {
		spawnPokemon(game, game.pokegen.createWild(
			game.settings.getPokemon(Random.nextElement(pokemon))),
			spawnLength);
	}
	
	@Override
	public String toString(){
		return super.toString() + " period=" + period + ", spawnLength=" + spawnLength + ", pokemon=" + Arrays.toString(pokemon);
	}

}
