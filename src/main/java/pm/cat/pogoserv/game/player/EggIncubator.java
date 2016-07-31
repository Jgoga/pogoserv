package pm.cat.pogoserv.game.player;

import POGOProtos.Inventory.POGOProtosInventory.EggIncubatorType;
import POGOProtos.Inventory.Item.POGOProtosInventoryItem.ItemId;

public class EggIncubator {
	
	public String id;
	public ItemId itemId;
	public EggIncubatorType type;
	public int usesRemaining;
	public long pokemonId;
	public double startKmWalked, targetKmWalked;
	
}
