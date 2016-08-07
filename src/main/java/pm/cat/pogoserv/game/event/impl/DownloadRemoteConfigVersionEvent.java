package pm.cat.pogoserv.game.event.impl;

import POGOProtos.Enums.POGOProtosEnums.Platform;

public class DownloadRemoteConfigVersionEvent extends ConfigEvent {
	
	public long itemTemplatesTimestamp;
	public long assetDigestTimestamp;
	
	public DownloadRemoteConfigVersionEvent(Platform platform, String devManufactuarer,
			String devModel, String locale, int appVersion){
		super(platform, devManufactuarer, devModel, locale, appVersion);
	}
	
}
