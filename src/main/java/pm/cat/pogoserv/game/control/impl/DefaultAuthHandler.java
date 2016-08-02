package pm.cat.pogoserv.game.control.impl;

import java.util.concurrent.ConcurrentHashMap;

import com.google.protobuf.ByteString;

import pm.cat.pogoserv.Log;
import pm.cat.pogoserv.game.Game;
import pm.cat.pogoserv.game.control.AuthHandler;
import pm.cat.pogoserv.game.model.player.Player;
import pm.cat.pogoserv.game.net.request.AuthTicket;
import pm.cat.pogoserv.util.Random;

public class DefaultAuthHandler implements AuthHandler {
	
	private final ConcurrentHashMap<AuthTicket, Player> activeSessions = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<Player, AuthTicket> activeTokens = new ConcurrentHashMap<>();
	private final Game game;
	
	public DefaultAuthHandler(Game game){
		this.game = game;
	}
	
	@Override
	public synchronized AuthTicket newSession(Player p) {
		AuthTicket t = activeTokens.get(p);
		if(t != null){
			// Dude has an active session but he wants to create a new one
			// just return the old ticket
			return t;
		}
		
		for(;;){
			AuthTicket tick = generateAuthTicket();
			if(activeSessions.contains(tick)){
				Log.w("Auth", "This should not happen");
				continue;
			}
			
			Log.d("Auth", "Created a new session for player: %s (%s)", p.toString(), tick.toString());
			activeTokens.put(p, tick);
			activeSessions.put(tick, p);
			game.submit(g -> {
				game.objectController.reapPlayer(p);
				activeSessions.remove(tick);
				activeTokens.remove(p);
			}, game.settings.authDurationMs);
			return tick;
		}
	}

	@Override
	public Player getPlayer(AuthTicket auth) {
		return activeSessions.get(auth);
	}
	
	private AuthTicket generateAuthTicket(){
		byte[] start = new byte[80];
		byte[] end = new byte[16];
		Random.nextBytes(start);
		Random.nextBytes(end);
		
		return new AuthTicket(ByteString.copyFrom(start), ByteString.copyFrom(end),
				System.currentTimeMillis() + game.settings.authDurationMs);
	}

}
