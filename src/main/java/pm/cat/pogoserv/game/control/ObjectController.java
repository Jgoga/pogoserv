package pm.cat.pogoserv.game.control;

import pm.cat.pogoserv.game.model.player.Player;
import pm.cat.pogoserv.game.net.request.AuthToken;

public interface ObjectController {
	
	Player newPlayer(AuthToken auth);
	void reapPlayer(Player p);
	
}
