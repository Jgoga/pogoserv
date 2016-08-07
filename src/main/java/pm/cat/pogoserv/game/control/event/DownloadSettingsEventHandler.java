package pm.cat.pogoserv.game.control.event;

import pm.cat.pogoserv.game.config.GameSettings;
import pm.cat.pogoserv.game.event.Listener;
import pm.cat.pogoserv.game.event.impl.DownloadSettingsEvent;

public class DownloadSettingsEventHandler implements Listener<DownloadSettingsEvent> {

	@Override
	public boolean on(DownloadSettingsEvent t) {
		GameSettings gs = t.getGame().getSettings();
		String hash = gs.clientSettingsHash;
		if(hash.equals(t.hash))
			return false;
		t.newHash = hash;
		t.settings = gs;
		return true;
	}

}
