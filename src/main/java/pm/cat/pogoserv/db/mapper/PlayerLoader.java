package pm.cat.pogoserv.db.mapper;

import pm.cat.pogoserv.db.DBWorker;
import pm.cat.pogoserv.game.model.player.Player;

public class PlayerLoader extends Loader<Player> {
	
	private final String auth;
	
	public PlayerLoader(String auth){
		this.auth = auth;
	}
	
	@Override
	public Player apply(DBWorker w) {
		return null;
	}

}
