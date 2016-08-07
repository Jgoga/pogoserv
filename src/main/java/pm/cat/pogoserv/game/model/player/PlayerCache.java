package pm.cat.pogoserv.game.model.player;

import java.util.concurrent.ConcurrentHashMap;

import pm.cat.pogoserv.game.session.AuthTicket;
import pm.cat.pogoserv.game.session.AuthToken;

public class PlayerCache {
	
	private final ConcurrentHashMap<AuthTicket, Player> activePlayers = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<AuthToken, AuthTicket> activeAuths = new ConcurrentHashMap<>();
	
	public Player getPlayer(AuthTicket auth){
		return activePlayers.get(auth);
	}
	
	public AuthTicket getTicket(AuthToken auth){
		return activeAuths.remove(auth);
	}
	 
}
