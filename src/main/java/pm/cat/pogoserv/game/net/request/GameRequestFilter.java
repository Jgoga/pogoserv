package pm.cat.pogoserv.game.net.request;

import pm.cat.pogoserv.core.net.HttpRequest;
import pm.cat.pogoserv.game.Game;
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
		/* TODO auth, for now just copy the authticket (idk if that's correct) */
		t.response.setAuthTicket(t.request.getAuthTicket());
		f.run(new GameRequest(game, t));
	}

}
