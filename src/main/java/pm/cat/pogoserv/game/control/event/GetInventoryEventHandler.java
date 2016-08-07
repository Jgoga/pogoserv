package pm.cat.pogoserv.game.control.event;

import pm.cat.pogoserv.game.event.Listener;
import pm.cat.pogoserv.game.event.impl.GetInventoryEvent;

public class GetInventoryEventHandler implements Listener<GetInventoryEvent> {

	@Override
	public boolean on(GetInventoryEvent t) {
		t.inventory = t.getPlayer().getPool();
		return true;
	}

}
