package pm.cat.pogoserv.game.model.player;

import java.util.HashMap;

import pm.cat.pogoserv.util.TimestampVarPool;
import pm.cat.pogoserv.util.TimestampVarPool.TSNode;

public class Pokedex {
	
	private final HashMap<Integer, TSNode<PokedexEntry>> entries = new HashMap<>();
	private final TimestampVarPool pool;
	
	Pokedex(TimestampVarPool pool){
		this.pool = pool;
	}
	
	public TSNode<PokedexEntry> entry(int id){
		TSNode<PokedexEntry> ret = entries.get(id);
		if(ret == null){
			ret = pool.allocate(new PokedexEntry(id));
			entries.put(id, ret);
		}
		return ret;
	}
	
	public Iterable<TSNode<PokedexEntry>> entries(){
		return entries.values();
	}
	
	public int count(){
		return entries.size();
	}
	
	public static class PokedexEntry {
		public final int id;
		public int numEncounter, numCapture;
		public int evoStonePieces, evoStones;
		
		public PokedexEntry(int id){
			this.id = id;
		}
		
	}
	
}
