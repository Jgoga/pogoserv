package pm.cat.pogoserv.game.control;

import POGOProtos.Enums.POGOProtosEnums.TeamColor;
import POGOProtos.Inventory.Item.POGOProtosInventoryItem.ItemId;
import pm.cat.pogoserv.Log;
import pm.cat.pogoserv.game.Game;
import pm.cat.pogoserv.game.config.GameSettings;
import pm.cat.pogoserv.game.model.player.Appearance;
import pm.cat.pogoserv.game.model.player.Player;
import pm.cat.pogoserv.game.model.player.PlayerInfo;

public class PlayerGen {
	
	private final Game game;
	
	public PlayerGen(Game game){
		this.game = game;
	}
	
	public Player newPlayer(){
		long uid = game.getUidGen().next();
		Log.d("PlayerGen", "Creating new player: %d", uid);
		Player ret = new Player(uid);
		ret.creationTs = System.currentTimeMillis();
		// TODO: New players don't actually have a nick, you need to ask it!
		ret.nickname = "PotofuIsCute";
		ret.team = TeamColor.NEUTRAL;
		
		ret.pokecoins.write().amt = 100;
		ret.stardust.write().amt = 1337;
		
		// Just some test pokeballs
		GameSettings settings = game.getSettings();
		ret.inventory.addItems(settings.getItem(ItemId.ITEM_POKE_BALL_VALUE), 50);
		ret.inventory.addItems(settings.getItem(ItemId.ITEM_GREAT_BALL_VALUE), 5);
		ret.inventory.addItems(settings.getItem(ItemId.ITEM_MASTER_BALL_VALUE), 2);
		
		PlayerInfo.setDefaults(ret.stats);
		Appearance.setDefaults(ret.appearance);
		ret.init(game);
		ret.setExp(12345);
		return ret;
	}
	
}
