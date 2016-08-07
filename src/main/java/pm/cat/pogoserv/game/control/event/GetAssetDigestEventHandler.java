package pm.cat.pogoserv.game.control.event;

import com.google.common.collect.Iterators;

import pm.cat.pogoserv.game.event.Listener;
import pm.cat.pogoserv.game.event.impl.GetAssetDigestEvent;

public class GetAssetDigestEventHandler implements Listener<GetAssetDigestEvent> {
	
	@Override
	public boolean on(GetAssetDigestEvent t) {
		t.timestamp = 1467338276561000L;
		t.assets = Iterators.filter(t.getGame().getSettings().getAssets().iterator(),
				a -> a.platform == t.platform.getNumber());
		return true;
	}

}
