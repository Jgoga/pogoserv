package pm.cat.pogoserv.game.net.request;

import pm.cat.pogoserv.core.Constants;
import pm.cat.pogoserv.core.net.HttpRequest;
import pm.cat.pogoserv.game.Game;
import pm.cat.pogoserv.game.model.player.Player;
import pm.cat.pogoserv.util.Filter;

public class GameRequestFilter implements Filter<HttpRequest> {
	
	private final Game game;
	private final Filter<GameRequest> f;
	
	public GameRequestFilter(Game game, Filter<GameRequest> f){
		this.game = game;
		this.f = f;
	}
	
	@Override
	public void run(HttpRequest t){
		Player player = null;
		if(t.request.hasAuthInfo()){
			player = game.objectController.newPlayer(AuthToken.fromAuthInfo(t.request.getAuthInfo()));
			if(player != null){
				AuthTicket at = game.authHandler.newSession(player);
				t.response.setAuthTicket(at.toProtobuf());
				
				// For some reason the client gets angry if i send this
				//t.protoStatus = Constants.SERVER_AUTH_OK;
			}
		}else if(t.request.hasAuthTicket()){
			player = game.authHandler.getPlayer(AuthTicket.fromProtobuf(t.request.getAuthTicket()));
		}
		
		if(player == null){
			t.protoStatus = Constants.SERVER_INVALID_TOKEN;
			return;
		}

		player.setPosition(t.request.getLatitude(), t.request.getLongitude());
		f.run(new GameRequest(game, player, t));
	}

}
