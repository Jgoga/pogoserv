package pm.cat.pogoserv.game.event.impl;

import java.util.Iterator;

import POGOProtos.Enums.POGOProtosEnums.Platform;
import pm.cat.pogoserv.game.config.AssetDef;

public class GetAssetDigestEvent extends ConfigEvent {

	public long timestamp;
	public Iterator<AssetDef> assets;
	
	public GetAssetDigestEvent(Platform platform, String devManufactuarer, String devModel, String locale,
			int appVersion) {
		super(platform, devManufactuarer, devModel, locale, appVersion);
	}

}
