package pm.cat.pogoserv.game.session;

import pm.cat.pogoserv.game.model.player.Player;

public class Session {
	
	public final Player player;
	public final AuthTicket ticket;
	public final String unique;
	
	public Session(Player player, AuthTicket ticket, String unique){
		this.player = player;
		this.ticket = ticket;
		this.unique = unique;
	}
	
}
