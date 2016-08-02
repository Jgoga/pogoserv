package pm.cat.pogoserv.game.net.request;

import pm.cat.pogoserv.core.net.HttpRequest;
import pm.cat.pogoserv.game.Game;
import pm.cat.pogoserv.game.model.player.Player;

public class GameRequest {
	
	public final Game game;
	public final HttpRequest http;
	public final Player player;
	
	public GameRequest(Game game, HttpRequest re){
		this.game = game;
		this.http = re;
		this.player = game.world.player(re.request.getAuthInfo());
		player.setPosition(re.request.getLatitude(), re.request.getLongitude());
	}
	
}
