package pm.cat.pogoserv.game.model.player;

import POGOProtos.Inventory.Item.POGOProtosInventoryItem.ItemId;
import pm.cat.pogoserv.game.config.PokemonDef;
import pm.cat.pogoserv.game.model.Pokemon;

public class InventoryPokemon extends Pokemon {

	public final long uid;
	public int cp;
	public int stamina;
	public int maxStamina;
	public int origin;
	public ItemId pokeball;
	public long capturedCellId;
	public int battlesAttacked, battlesDefended;
	public long creationTimestamp;
	public int numUpgrades;
	public int favorite;
	public String nickname;
	//public EggIncubator incubator; // non-null if pokemon is an egg, TODO
	public int deployedFortId;
	public int fromFortId;
	
	public InventoryPokemon(PokemonDef def, long uid){
		super(def);
		this.uid = uid;
	}
	
}
