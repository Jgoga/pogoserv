package pm.cat.pogoserv.game.control.event;

import pm.cat.pogoserv.game.event.Listener;
import pm.cat.pogoserv.game.event.impl.GetPlayerEvent;

public class GetPlayerEventHandler implements Listener<GetPlayerEvent> {

	@Override
	public boolean on(GetPlayerEvent t) {
		// No need to do anything here
		return true;
	}

}
