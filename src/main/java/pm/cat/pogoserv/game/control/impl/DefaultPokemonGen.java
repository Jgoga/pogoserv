package pm.cat.pogoserv.game.control.impl;

import pm.cat.pogoserv.game.config.PokemonDef;
import pm.cat.pogoserv.game.control.PokemonGen;
import pm.cat.pogoserv.game.model.player.InventoryPokemon;
import pm.cat.pogoserv.game.model.player.Player;
import pm.cat.pogoserv.game.model.pokemon.Pokemon;
import pm.cat.pogoserv.game.model.world.MapPokemon;
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
		return ret;
	}

	@Override
	public InventoryPokemon createEncounter(MapPokemon src, Player p, long uid) {
		InventoryPokemon ret = new InventoryPokemon(src.pokemon.def, uid);
		ret.copyFrom(src.pokemon);
		ret.setCpMultiplier((float) (src.spawnParameter * Math.sqrt(p.getLevel())));
		ret.setAdditionalCpMultiplier(0);
		ret.creationTimestamp = System.currentTimeMillis();
		return ret;
	}
	
	
}
