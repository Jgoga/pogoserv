package pm.cat.pogoserv.game.control;

import pm.cat.pogoserv.game.model.player.Player;

public interface ObjectLoader {
	
	void savePlayer(Player p);
	Player loadPlayer(String auth);
	
}
