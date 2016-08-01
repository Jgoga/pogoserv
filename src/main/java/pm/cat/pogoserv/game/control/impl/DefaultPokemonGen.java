package pm.cat.pogoserv.game.control.impl;

import pm.cat.pogoserv.game.config.PokemonDef;
import pm.cat.pogoserv.game.control.PokemonGen;
import pm.cat.pogoserv.game.model.Pokemon;
import pm.cat.pogoserv.util.Random;

public class DefaultPokemonGen implements PokemonGen {
	
	@Override
	public Pokemon createWild(PokemonDef def){
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
