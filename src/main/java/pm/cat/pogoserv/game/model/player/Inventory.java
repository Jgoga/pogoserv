package pm.cat.pogoserv.game.model.player;

import java.util.HashMap;

import pm.cat.pogoserv.Log;
import pm.cat.pogoserv.game.config.ItemDef;
import pm.cat.pogoserv.util.TimestampVarPool;
import pm.cat.pogoserv.util.TimestampVarPool.TSNode;

public class Inventory {
	
	private final HashMap<Integer, TSNode<Item>> items = new HashMap<>();
	private final HashMap<Long, TSNode<InventoryPokemon>> pokemon = new HashMap<>();
	
	private final TimestampVarPool pool;
	
	public int maxItemStorage, maxPokemonStorage;
	
	Inventory(TimestampVarPool pool){
		this.pool = pool;
	}
	
	public TSNode<InventoryPokemon> addPokemon(InventoryPokemon p){
		TSNode<InventoryPokemon> ret = pool.allocate(p);
		pokemon.put(p.getUID(), ret);
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
			ret.write().count = 0;
			items.put(def.id, ret);
		}
		return ret;
	}
	
	public TSNode<Item> getItem(int id){
		return items.get(id);
	}
	
	public int getCount(int id){
		TSNode<Item> i = items.get(id);
		return i == null ? 0 : i.read().count;
	}
	
	public boolean containsItem(int id){
		return getCount(id) != 0;
	}
	
	public void addItems(ItemDef def, int count){
		item(def).write().count += count;
	}
	
	public void removeItems(int id, int count){
		TSNode<Item> i = items.get(id);
		if(i == null){
			Log.w("Inventory", "Attempted to remove nonexisting item (%d)", id);
			return;
		}
		
		int newCount = i.read().count - count;
		if(newCount <= 0){
			// !! Deleted items don't actually get "deleted", as in sending deleted_item_id
			// their count just goes to 0 (which is logical since they dont have an uid anyway)
			// (I think only deleted pokemon get deleted_item_key)
			newCount = 0;
		}
		
		i.write().count = newCount;
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
