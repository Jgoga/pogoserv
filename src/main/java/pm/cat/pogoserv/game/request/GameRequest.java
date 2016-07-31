package pm.cat.pogoserv.game.request;

import pm.cat.pogoserv.core.net.HttpRequest;
import pm.cat.pogoserv.game.Game;
import pm.cat.pogoserv.game.player.Player;

public class GameRequest {
	
	public final Game game;
	public final HttpRequest http;
	public final Player player;
	public final double longitude, latitude, altitude;
	
	public GameRequest(Game game, HttpRequest re){
		this.game = game;
		this.http = re;
		this.player = game.playerController.player(re.request.getAuthInfo());
		this.longitude = re.request.getLongitude();
		this.latitude = re.request.getLatitude();
		this.altitude = re.request.getAltitude();
	}
	
}
