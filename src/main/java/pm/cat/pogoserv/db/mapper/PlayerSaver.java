package pm.cat.pogoserv.db.mapper;

import pm.cat.pogoserv.db.DBWorker;
import pm.cat.pogoserv.game.model.player.Player;

public class PlayerSaver extends Saver<Player> {

	public PlayerSaver(Player t) {
		super(t);
	}

	@Override
	public void accept(DBWorker t) {
		System.out.println("TODO:Saving player");
	}

}
