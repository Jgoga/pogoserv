package pm.cat.pogoserv.game.net;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;

import com.sun.net.httpserver.HttpExchange;

import POGOProtos.Networking.Envelopes.POGOProtosNetworkingEnvelopes;
import POGOProtos.Networking.Envelopes.POGOProtosNetworkingEnvelopes.RequestEnvelope;
import POGOProtos.Networking.Envelopes.POGOProtosNetworkingEnvelopes.ResponseEnvelope;
import pm.cat.pogoserv.Log;
import pm.cat.pogoserv.game.Game;
import pm.cat.pogoserv.game.access.PlayerMapper;
import pm.cat.pogoserv.game.control.PlayerGen;
import pm.cat.pogoserv.game.model.player.Player;
import pm.cat.pogoserv.game.net.request.RequestDispatcher;
import pm.cat.pogoserv.game.session.AuthToken;
import pm.cat.pogoserv.game.session.Session;
import pm.cat.pogoserv.game.session.SessionManager;

public class GameService implements POGOService {
	
	private final Game game;
	private PlayerMapper playerMapper;
	private PlayerGen playerGen;
	private RequestDispatcher dispatcher;
	private SessionManager sessionManager;
	
	public GameService(Game game){
		this.game = game;
		
		playerGen = new PlayerGen(game);
		dispatcher = new RequestDispatcher(game);
		sessionManager = new SessionManager(this);
	}
	
	public void setPlayerMapper(PlayerMapper mapper){
		this.playerMapper = mapper;
	}
	
	public RequestDispatcher getRequestDispatcher(){
		return dispatcher;
	}
	
	public ScheduledExecutorService getExecutor(){
		return game.getExecutor();
	}

	@Override
	public void process(POGORequest re) throws IOException {
		Player p = doAuth(re);
		if(p != null){
			p.setPosition(re.req.getLatitude(), re.req.getLongitude());
			dispatcher.dispatch(re, p);
		}else{
			Log.w("GameService", "Failed to resolve player");
		}
	}
	
	private Player doAuth(POGORequest r){
		RequestEnvelope re = r.req;
		
		if(re.hasAuthTicket()){
			Session s = sessionManager.getSession(ProtobufReader.authTicket(re.getAuthTicket()));
			if(s != null)
				return s.player;
			
			Log.d("GameService", "Request has auth ticket but no session found. Maybe it has expired?");
			r.resp.setStatusCode(POGORequest.INVALID_TOKEN);
			return null;
		}
		
		if(re.hasAuthInfo()){
			AuthToken at = ProtobufReader.authToken(re.getAuthInfo());
			if(at.userInfo == null)
				return null;
			Session s = sessionManager.getSession(at.userInfo);
			if(s != null){
				r.resp.setAuthTicket(ProtobufMapper.authTicket(
						POGOProtosNetworkingEnvelopes.AuthTicket.newBuilder(), s.ticket));
				return s.player;
			}
			
			Player p = playerMapper.loadPlayer(at.userInfo);
			if(p == null){
				p = playerGen.newPlayer();
				Log.d("GameService", "Mapped returned null, creating new player: %s", p);
			}
			
			s = sessionManager.createSession(p, at);
			return s.player;
		}
		
		Log.w("GameService", "Failed to authenticate");
		r.setStatus(500);
		return null;
	}
	
	@Override
	public void shutdown(){
		game.shutdown();
		sessionManager.shutdown();
	}

}
