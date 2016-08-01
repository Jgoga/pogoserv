package pm.cat.pogoserv.io;

import java.io.IOException;

import pm.cat.pogoserv.game.model.player.Player;

/* An sql implementation would be nice. */
// Also, TODO: Partial loading
public interface PlayerLoader {
	
	Player loadPlayer(String key) throws IOException;
	void savePlayer(Player p) throws IOException;
	
}
