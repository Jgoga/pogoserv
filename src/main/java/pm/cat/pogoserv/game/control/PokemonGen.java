package pm.cat.pogoserv.game.control;

import pm.cat.pogoserv.game.UidGen;
import pm.cat.pogoserv.game.config.PokemonDef;
import pm.cat.pogoserv.game.model.player.InventoryPokemon;
import pm.cat.pogoserv.game.model.player.Player;
import pm.cat.pogoserv.game.model.pokemon.Pokemon;
import pm.cat.pogoserv.game.model.world.MapPokemon;
import pm.cat.pogoserv.util.Random;

public class PokemonGen {
	
	private final UidGen uidGen;
	
	public PokemonGen(UidGen uidGen){
		this.uidGen = uidGen;
	}
	
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
	
	public InventoryPokemon createEncounter(MapPokemon src, Player p) {
		InventoryPokemon ret = new InventoryPokemon(src.pokemon.def, uidGen.next());
		ret.copyFrom(src.pokemon);
		int pokemonLevel = (int) Math.max(1, 2 * src.spawnParameter * p.getLevel());
		ret.setCapturedLevel(pokemonLevel);
		ret.setLevel(pokemonLevel);
		ret.creationTimestamp = System.currentTimeMillis();
		ret.init(p);
		return ret;
	}
	
	
}
