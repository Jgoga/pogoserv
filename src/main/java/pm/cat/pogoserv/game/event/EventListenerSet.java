package pm.cat.pogoserv.game.event;

import java.util.HashMap;
import java.util.Map;

import pm.cat.pogoserv.game.control.event.CatchPokemonEventHandler;
import pm.cat.pogoserv.game.control.event.DownloadItemTemplatesEventHandler;
import pm.cat.pogoserv.game.control.event.DownloadRemoteConfigVersionEventHandler;
import pm.cat.pogoserv.game.control.event.DownloadSettingsEventHandler;
import pm.cat.pogoserv.game.control.event.EncounterEventHandler;
import pm.cat.pogoserv.game.control.event.GetAssetDigestEventHandler;
import pm.cat.pogoserv.game.control.event.GetDownloadUrlsEventHandler;
import pm.cat.pogoserv.game.control.event.GetInventoryEventHandler;
import pm.cat.pogoserv.game.control.event.GetMapObjectsEventHandler;
import pm.cat.pogoserv.game.control.event.GetPlayerEventHandler;
import pm.cat.pogoserv.game.event.impl.CatchPokemonEvent;
import pm.cat.pogoserv.game.event.impl.DownloadItemTemplatesEvent;
import pm.cat.pogoserv.game.event.impl.DownloadRemoteConfigVersionEvent;
import pm.cat.pogoserv.game.event.impl.DownloadSettingsEvent;
import pm.cat.pogoserv.game.event.impl.EncounterEvent;
import pm.cat.pogoserv.game.event.impl.GetAssetDigestEvent;
import pm.cat.pogoserv.game.event.impl.GetDownloadUrlsEvent;
import pm.cat.pogoserv.game.event.impl.GetInventoryEvent;
import pm.cat.pogoserv.game.event.impl.GetMapObjectsEvent;
import pm.cat.pogoserv.game.event.impl.GetPlayerEvent;

public class EventListenerSet {
	
	private final Map<Class<? extends Event>, Listener<? extends Event>> listeners = new HashMap<>();
	
	public <T extends Event> void setListener(Class<T> c, Listener<T> l){
		listeners.put(c, l);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Event> boolean submit(T t){
		Listener<T> l = (Listener<T>) listeners.get(t.getClass());
		return l == null || l.on(t);
	}
	
	public void registerDefaults(){
		setListener(GetPlayerEvent.class, new GetPlayerEventHandler());
		setListener(DownloadSettingsEvent.class, new DownloadSettingsEventHandler());
		setListener(GetInventoryEvent.class, new GetInventoryEventHandler());
		setListener(DownloadRemoteConfigVersionEvent.class, new DownloadRemoteConfigVersionEventHandler());
		setListener(GetAssetDigestEvent.class, new GetAssetDigestEventHandler());
		setListener(GetMapObjectsEvent.class, new GetMapObjectsEventHandler());
		setListener(DownloadItemTemplatesEvent.class, new DownloadItemTemplatesEventHandler());
		setListener(GetDownloadUrlsEvent.class, new GetDownloadUrlsEventHandler());
		setListener(EncounterEvent.class, new EncounterEventHandler());
		setListener(CatchPokemonEvent.class, new CatchPokemonEventHandler());
	}
	
}
