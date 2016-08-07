package pm.cat.pogoserv.game.event.impl;

import pm.cat.pogoserv.game.event.Event;
import pm.cat.pogoserv.util.TimestampVarPool;

public class GetInventoryEvent extends Event {
	
	public final long lastTimestamp;
	// int32 item_been_seen, what does this do?
	
	public TimestampVarPool inventory;
	
	public GetInventoryEvent(long lastTimestamp){
		this.lastTimestamp = lastTimestamp;
	}
	
}
