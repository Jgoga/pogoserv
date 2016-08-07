package pm.cat.pogoserv.game.event.impl;

import pm.cat.pogoserv.game.model.player.Award;

public interface AwardEvent {
	
	Award getAward();
	
	default boolean hasAward(){
		return getAward() != null;
	}
	
}
