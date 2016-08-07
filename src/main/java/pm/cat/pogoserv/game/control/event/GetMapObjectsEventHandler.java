package pm.cat.pogoserv.game.control.event;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.geometry.S2LatLng;

import POGOProtos.Map.POGOProtosMap.MapObjectsStatus;
import jdk.net.SocketFlow.Status;
import pm.cat.pogoserv.Log;
import pm.cat.pogoserv.game.config.GameSettings;
import pm.cat.pogoserv.game.event.Listener;
import pm.cat.pogoserv.game.event.impl.GetMapObjectsEvent;
import pm.cat.pogoserv.game.event.impl.GetMapObjectsEvent.CellInfo;
import pm.cat.pogoserv.game.event.impl.GetMapObjectsEvent.MapObjectInfo;
import pm.cat.pogoserv.game.model.player.Player;
import pm.cat.pogoserv.game.model.world.MapPokemon;
import pm.cat.pogoserv.game.model.world.SpawnPoint;
import pm.cat.pogoserv.game.model.world.UniqueLocatable;
import pm.cat.pogoserv.game.model.world.World;
import pm.cat.pogoserv.game.model.world.WorldCell;

public class GetMapObjectsEventHandler implements Listener<GetMapObjectsEvent> {

	@Override
	public boolean on(GetMapObjectsEvent t) {
		// TODO if t.size() is too high just quit
		
		Player player = t.getPlayer();
		S2LatLng pos = player.s2LatLngPos();
		World world = t.getGame().getWorld();
		GameSettings settings = t.getGame().getSettings();
		long ts = System.currentTimeMillis();
		Function<UniqueLocatable, MapObjectInfo> f = u -> getMapObjectInfo(u, settings, ts, player, pos);
		
		for(int i=0;i<t.size();i++){
			WorldCell cell = world.getCell(t.cellIds[i]);
			if(cell == null)
				continue;
			
			// Don't let them scan cells too far away
			// TODO: Make the limit configurable!
			if(cell.distanceTo(pos) > 1000)
				continue;
			
			t.cells[i] = new CellInfo(ts, Iterators.filter(Iterators.transform(cell.objects().iterator(), f), e -> e != null));
		}
		
		t.status = MapObjectsStatus.SUCCESS;
		return true;
	}
	
	private MapObjectInfo getMapObjectInfo(UniqueLocatable u, GameSettings gs, long ts, Player p, S2LatLng playerPos){
		int type = classifyMapObject(u, gs, ts, p, playerPos);
		if(type == MapObjectInfo.SKIP)
			return null;
		return new MapObjectInfo(type, u);
	}
	
	private int classifyMapObject(UniqueLocatable u, GameSettings gs, long ts, Player p, S2LatLng playerPos){
		if(u instanceof SpawnPoint)
			return MapObjectInfo.SPAWNPOINT;
		if(u instanceof MapPokemon){
			MapPokemon mp = (MapPokemon) u;
			
			if(p.hasEncountered(mp.getUID()))
				return MapObjectInfo.SKIP;
			
			if(mp.disappearTimestamp <= ts){
				Log.w("GetMapObjects", "Found a zombie. This shouldn't happen: %s Disappears %d, now=%d", mp, mp.disappearTimestamp, ts);
				return MapObjectInfo.SKIP;
			}

			int ret = 0;
			double dist = mp.distanceTo(playerPos);

			if(dist < gs.mapEncounterRange)
				ret |= MapObjectInfo.CATCHABLE_POKEMON;
			
			if(dist < gs.mapPokemonVisibleRange)
				ret |= MapObjectInfo.WILD_POKEMON;
			
			if(dist < gs.mapPokeNavRange)
				ret |= MapObjectInfo.NEARBY_POKEMON;
			
			return ret;
		}
		
		return MapObjectInfo.SKIP;
	}

}
