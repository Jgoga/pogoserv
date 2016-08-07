package pm.cat.pogoserv.game.event.impl;

import pm.cat.pogoserv.game.config.GameSettings;
import pm.cat.pogoserv.game.event.Event;

public class DownloadSettingsEvent extends Event {
	
	public final String hash;
	
	public String newHash;
	public GameSettings settings;
	
	public DownloadSettingsEvent(String hash){
		this.hash = hash;
	}
	
}
