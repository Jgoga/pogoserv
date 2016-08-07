package pm.cat.pogoserv.game.control.event;

import pm.cat.pogoserv.game.event.Listener;
import pm.cat.pogoserv.game.event.impl.DownloadRemoteConfigVersionEvent;

public class DownloadRemoteConfigVersionEventHandler implements Listener<DownloadRemoteConfigVersionEvent> {

	private static final long ITEM_TEMPLATES_TS = 1468540960537L;
	private static final long ASSET_DIGEST_TS = 1467338276561000L; // Protos say it's milliseconds but clearly it's not
	
	@Override
	public boolean on(DownloadRemoteConfigVersionEvent t) {
		t.itemTemplatesTimestamp = ITEM_TEMPLATES_TS;
		t.assetDigestTimestamp = ASSET_DIGEST_TS;
		return true;
	}
}
