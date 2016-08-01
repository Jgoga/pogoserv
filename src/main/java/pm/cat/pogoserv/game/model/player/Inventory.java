package pm.cat.pogoserv.game.model.player;

import java.util.HashMap;

import pm.cat.pogoserv.game.config.ItemDef;
import pm.cat.pogoserv.game.config.PokemonDef;
import pm.cat.pogoserv.util.TimestampVarPool;
import pm.cat.pogoserv.util.TimestampVarPool.TSNode;

public class Inventory {
	
	private final HashMap<Integer, TSNode<Item>> items = new HashMap<>();
	private final HashMap<Long, TSNode<InventoryPokemon>> pokemon = new HashMap<>();
	
	private final TimestampVarPool pool;
	
	int maxItemStorage, maxPokemonStorage;
	
	Inventory(TimestampVarPool pool, int maxItemStorage, int maxPokemonStorage){
		this.pool = pool;
		this.maxItemStorage = maxItemStorage;
		this.maxPokemonStorage = maxPokemonStorage;
	}
	
	public TSNode<InventoryPokemon> addPokemon(PokemonDef def, long uid){
		TSNode<InventoryPokemon> ret = pool.allocate(new InventoryPokemon(def, uid));
		pokemon.put(uid, ret);
		return ret;
	}
	
	public TSNode<InventoryPokemon> getPokemon(long uid){
		return pokemon.get(uid);
	}
	
	public int uniquePokemonCount(){
		return pokemon.size();
	}
	
	public Iterable<TSNode<InventoryPokemon>> getAllPokemon(){
		return pokemon.values();
	}
	
	public TSNode<Item> item(ItemDef def){
		TSNode<Item> ret = items.get(def.id);
		if(ret == null){
			ret = pool.allocate(new Item(def));
			items.put(def.id, ret);
		}
		return ret;
	}
	
	public Iterable<TSNode<Item>> getAllItems(){
		return items.values();
	}
	
	public int uniqueItemCount(){
		return items.size();
	}
	
	public void removePokemon(long uid){
		throw new RuntimeException("TODO");
	}
	
	public int getMaxItemStorage(){
		return maxItemStorage;
	}
	
	public int getMaxPokemonStorage(){
		return maxPokemonStorage;
	}
	
}
