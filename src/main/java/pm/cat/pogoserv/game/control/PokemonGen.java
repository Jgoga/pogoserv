package pm.cat.pogoserv.game.control;

import pm.cat.pogoserv.game.config.PokemonDef;
import pm.cat.pogoserv.game.model.Pokemon;

public interface PokemonGen {
	
	Pokemon createWild(PokemonDef def);
	
}
