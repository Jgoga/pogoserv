package pm.cat.pogoserv.game.control;

import pm.cat.pogoserv.game.config.PokemonDef;
import pm.cat.pogoserv.game.model.player.InventoryPokemon;
import pm.cat.pogoserv.game.model.player.Player;
import pm.cat.pogoserv.game.model.pokemon.Pokemon;
import pm.cat.pogoserv.game.model.world.MapPokemon;

public interface PokemonGen {
	
	Pokemon createWild(PokemonDef def);
	InventoryPokemon createEncounter(MapPokemon src, Player p, long uid);
	
}
