package pm.cat.pogoserv.game.player;

import java.util.HashMap;

import POGOProtos.Enums.POGOProtosEnums.PokemonId;
import pm.cat.pogoserv.util.TimestampVarPool;

public class Pokedex {
	
	private final HashMap<Integer, PokedexEntry> entries = new HashMap<>();
	private final TimestampVarPool pool;
	
	Pokedex(TimestampVarPool pool){
		this.pool = pool;
	}
	
	public static class PokedexEntry {
		public final PokemonId id;
		public int numEncounter, numCapture;
		public int evoStonePieces, evoStones;
		
		public PokedexEntry(PokemonId id){
			this.id = id;
		}
		
	}
	
}
