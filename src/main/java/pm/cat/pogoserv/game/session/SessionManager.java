package pm.cat.pogoserv.game.session;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.google.protobuf.ByteString;

import pm.cat.pogoserv.Config;
import pm.cat.pogoserv.Log;
import pm.cat.pogoserv.game.model.player.Player;
import pm.cat.pogoserv.game.net.GameService;
import pm.cat.pogoserv.util.Random;

public class SessionManager {
	
	private final ConcurrentHashMap<AuthTicket, Session> activeSessions = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, Session> uniqueAuths = new ConcurrentHashMap<>();
	private final GameService service;
	
	public SessionManager(GameService service){
		this.service = service;
	}
	
	public Session getSession(AuthTicket token){
		return activeSessions.get(token);
	}
	
	public Session getSession(String auth){
		return uniqueAuths.get(auth);
	}
	
	public synchronized Session createSession(Player p, AuthToken at){
		Session s = getSession(at.userInfo);
		if(s != null){
			Log.w("SessionManager", "createSession: player already had an active session!");
			return s;
		}
		
		for(;;){
			AuthTicket tick = generateAuthTicket();
			if(activeSessions.get(tick) != null){
				Log.w("SessionManager", "Generated auth ticket already in use. There's something wrong with your random");
				Log.w("SessionManager", "%s", tick);
			}
			
			final Session ret = new Session(p, tick, at.userInfo);
			activeSessions.put(tick, ret);
			uniqueAuths.put(at.userInfo, ret);
			service.getExecutor().schedule(() -> deleteSession(ret), 
					Config.AUTH_TICKET_VALID_TIME, TimeUnit.MILLISECONDS);
			return ret;
		}
		
	}
	
	public synchronized void deleteSession(Session s){
		// TODO: Inform the GameService that the player was deleted
		//       So it can save the game/whatever
		activeSessions.remove(s.ticket);
		uniqueAuths.remove(s.unique);
	}
	
	public void shutdown(){
		for(Session s : activeSessions.values())
			deleteSession(s);
	}
	
	private AuthTicket generateAuthTicket(){
		byte[] start = new byte[80];
		byte[] end = new byte[16];
		long expire = System.currentTimeMillis() + Config.AUTH_TICKET_VALID_TIME;
		
		// Guaranteed NOT cryptographically secure but who would try
		// to hack a pokemon go private server account anyway
		Random.nextBytes(start);
		Random.nextBytes(end);
		return new AuthTicket(ByteString.copyFrom(start), ByteString.copyFrom(end), expire);
	}
	
}
