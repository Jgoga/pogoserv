package pm.cat.pogoserv.game.event;

import pm.cat.pogoserv.game.Game;
import pm.cat.pogoserv.game.model.player.Player;

public class Event {
	
	private Player player;
	
	public void setPlayer(Player p){
		this.player = p;
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public Game getGame(){
		return player.getGame();
	}
	
}
