package pm.cat.pogoserv.game.control.event;

import java.io.FileInputStream;
import java.io.InputStream;

import com.google.common.io.ByteStreams;

import pm.cat.pogoserv.Log;
import pm.cat.pogoserv.game.event.Listener;
import pm.cat.pogoserv.game.event.impl.DownloadItemTemplatesEvent;

public class DownloadItemTemplatesEventHandler implements Listener<DownloadItemTemplatesEvent> {
	
	private byte[] buffer = null;
	
	@Override
	public boolean on(DownloadItemTemplatesEvent t) {
		synchronized(this){
			if(buffer == null){
				String path = t.getGame().getSettings().dataPath + "/GAME_MASTER.protobuf";
				try(InputStream in = new FileInputStream(path)){
					buffer = ByteStreams.toByteArray(in);
					Log.d("ItemTemplates", "Cached templates: %s / %dK", path, buffer.length/1024);
				}catch(Exception e){
					Log.e("ItemTemplates", e);
					return false;
				}
			}
		}
		
		t.resp = buffer;
		return true;
	}

}
