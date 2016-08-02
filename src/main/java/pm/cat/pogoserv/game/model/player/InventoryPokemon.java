package pm.cat.pogoserv.game.model.player;

import POGOProtos.Inventory.Item.POGOProtosInventoryItem.ItemId;
import pm.cat.pogoserv.game.config.PokemonDef;
import pm.cat.pogoserv.game.model.pokemon.InstancedPokemon;
import pm.cat.pogoserv.game.model.pokemon.Pokemon;
import pm.cat.pogoserv.util.Unique;

public class InventoryPokemon extends InstancedPokemon implements Unique {

	private final long uid;
	
	public int origin;
	public ItemId pokeball;
	public long capturedCellId;
	public int battlesAttacked, battlesDefended;
	public long creationTimestamp;
	public int numUpgrades;
	public boolean favorite;
	public String nickname;
	//public EggIncubator incubator; // non-null if pokemon is an egg, TODO
	public int deployedFortId;
	
	public InventoryPokemon(PokemonDef def, long uid){
		super(def);
		this.uid = uid;
	}

	@Override
	public long getUID() {
		return uid;
	}
	
}
