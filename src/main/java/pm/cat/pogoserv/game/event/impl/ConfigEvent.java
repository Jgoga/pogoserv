package pm.cat.pogoserv.game.event.impl;

import POGOProtos.Enums.POGOProtosEnums.Platform;
import pm.cat.pogoserv.game.event.Event;

public class ConfigEvent extends Event {
	
	public final Platform platform;
	public final String devManufacturer;
	public final String devModel;
	public final String locale;
	public final int appVersion;
	
	public ConfigEvent(Platform platform, String devManufactuarer,
			String devModel, String locale, int appVersion){
		this.platform = platform;
		this.devManufacturer = devManufactuarer;
		this.devModel = devModel;
		this.locale = locale;
		this.appVersion = appVersion;
	}
}
