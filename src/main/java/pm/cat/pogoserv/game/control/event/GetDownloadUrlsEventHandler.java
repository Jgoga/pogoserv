package pm.cat.pogoserv.game.control.event;

import pm.cat.pogoserv.Config;
import pm.cat.pogoserv.game.config.AssetDef;
import pm.cat.pogoserv.game.config.GameSettings;
import pm.cat.pogoserv.game.event.Listener;
import pm.cat.pogoserv.game.event.impl.GetDownloadUrlsEvent;

public class GetDownloadUrlsEventHandler implements Listener<GetDownloadUrlsEvent> {

	@Override
	public boolean on(GetDownloadUrlsEvent t) {
		GameSettings settings = t.getGame().getSettings();
		for(int i=0;i<t.size();i++){
			AssetDef as = settings.getAsset(t.assetIds[i]);
			t.assets[i] = as;
			t.urls[i] = settings.assetHostPrefix + "/" + Config.ASSET_VERSION + "/" 
					+ as.platform + "/" + as.id.replace('/', '-');
		}
		return true;
	}

}
