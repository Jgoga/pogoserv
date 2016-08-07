package pm.cat.pogoserv.game.access;

import pm.cat.pogoserv.game.model.player.Player;

public interface PlayerMapper {
	
	void savePlayer(Player p);
	Player loadPlayer(String auth);
	
}
