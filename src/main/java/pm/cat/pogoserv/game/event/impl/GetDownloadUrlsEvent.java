package pm.cat.pogoserv.game.event.impl;

import pm.cat.pogoserv.game.config.AssetDef;
import pm.cat.pogoserv.game.event.Event;

public class GetDownloadUrlsEvent extends Event {
	
	public final String[] assetIds;
	
	public final AssetDef[] assets;
	public final String[] urls;
	
	public GetDownloadUrlsEvent(int size){
		assetIds = new String[size];
		assets = new AssetDef[size];
		urls = new String[size];
	}
	
	public int size(){
		return assetIds.length;
	}
	
}
