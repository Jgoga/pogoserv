package pm.cat.pogoserv.game.control.impl;

import POGOProtos.Enums.POGOProtosEnums.TeamColor;
import POGOProtos.Inventory.Item.POGOProtosInventoryItem.ItemId;
import pm.cat.pogoserv.Log;
import pm.cat.pogoserv.game.Game;
import pm.cat.pogoserv.game.control.ObjectController;
import pm.cat.pogoserv.game.model.player.Appearance;
import pm.cat.pogoserv.game.model.player.Player;
import pm.cat.pogoserv.game.model.player.PlayerInfo;
import pm.cat.pogoserv.game.net.request.AuthToken;

public class DefaultObjectController implements ObjectController {
	
	private final Game game;
	
	public DefaultObjectController(Game game){
		this.game = game;
	}
	
	@Override
	public Player newPlayer(AuthToken auth) {
		Player ret = game.objectLoader.loadPlayer(auth.content);
		if(ret != null)
			return ret;	
		
		Log.d("ObjCtrlr", "Creating new player from auth token: %s", auth.toString());
		ret = new Player(game.uidManager.next());
		ret.creationTs = System.currentTimeMillis();
		// TODO: Should ask for nick.
		ret.nickname = "PotofuIsCute";
		ret.team = TeamColor.NEUTRAL;
		
		ret.pokecoins.write().amt = 100;
		ret.stardust.write().amt = 1337;
		// Just some test pokeballs
		ret.inventory.item(game.settings.getItem(ItemId.ITEM_POKE_BALL_VALUE)).write().count = 50;
		ret.inventory.item(game.settings.getItem(ItemId.ITEM_GREAT_BALL_VALUE)).write().count = 25;
		ret.inventory.item(game.settings.getItem(ItemId.ITEM_MASTER_BALL_VALUE)).write().count = 10;

		// TODO: Database stuff goes here
		PlayerInfo.setDefaults(ret.stats);
		Appearance.setDefaults(ret.appearance);
		ret.attachTo(game);
		return ret;
	}

	@Override
	public void reapPlayer(Player p) {
		Log.d("ObjCtrlr", "Reaping player: %s", p.toString());
		game.objectLoader.savePlayer(p);
	}

}
