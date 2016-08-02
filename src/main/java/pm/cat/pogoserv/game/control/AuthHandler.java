package pm.cat.pogoserv.game.control;

import pm.cat.pogoserv.game.model.player.Player;
import pm.cat.pogoserv.game.net.request.AuthTicket;

public interface AuthHandler {
	
	public AuthTicket newSession(Player p);
	public Player getPlayer(AuthTicket auth);
	
}
