package pm.cat.pogoserv.game.control;

import pm.cat.pogoserv.game.Game;
import pm.cat.pogoserv.game.config.PokemonDef;
import pm.cat.pogoserv.game.model.Pokemon;
import pm.cat.pogoserv.util.Random;

// Utility class for generating pokemon
// Extend for fun and profit
public class PokemonGen {
	
	protected final Game game;
	
	public PokemonGen(Game game){
		this.game = game;
	}
	
	public Pokemon createRandom(PokemonDef def){
		Pokemon ret = new Pokemon(def);
		ret.move1 = Random.nextElement(def.quickMoves);
		ret.move2 = Random.nextElement(def.chargeMoves);
		// TODO Maybe cap height & weight
		ret.height = (float) (def.pokedexHeight + def.heightStdDev * Random.nextGaussian());
		ret.weight = (float) (def.pokedexWeight + def.weightStdDev * Random.nextGaussian());
		ret.ivAtk = Random.nextInt(16);
		ret.ivDef = Random.nextInt(16);
		ret.ivSta = Random.nextInt(16);
		// TODO What is this ???
		ret.cpMultiplier = 1.0f;
		return ret;
	}
	
	
}
