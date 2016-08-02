package pm.cat.pogoserv.game.model.world;

import java.util.concurrent.ConcurrentHashMap;

import com.google.common.geometry.S2CellId;

import pm.cat.pogoserv.Log;
import pm.cat.pogoserv.game.Game;
import pm.cat.pogoserv.util.Locatable;

// TODO: Maybe use bigger/smaller cells than level 15 (like a quad tree)
// TODO: Dynamic cell loading/unloading
public class World {
	
	// TODO Turn this into a LoadingCache
	private final ConcurrentHashMap<Long, WorldCell> cells = new ConcurrentHashMap<>();
	
	private final Game game;
	
	public World(Game game){
		this.game = game;
	}
	
	public WorldCell getCell(Locatable l){
		return getCell(l.getS2CellId());
	}
	
	public WorldCell getCell(S2CellId id){
		return getCell(id.parent(WorldCell.LEVEL).id());
	}
	
	public WorldCell getCell(long id){
		return cells.get(id);
	}
	
	public WorldCell getOrCreateCell(Locatable l){
		return getOrCreateCell(l.getS2CellId());
	}
	
	public WorldCell getOrCreateCell(S2CellId id){
		id = id.parent(WorldCell.LEVEL);
		WorldCell ret = cells.get(id.id());
		if(ret == null){
			ret = new WorldCell(id);
			cells.put(id.id(), ret);
		}
		return ret;
	}
	
	public <T extends MapObject> T addObject(T t){
		WorldCell cell = getOrCreateCell(t);
		Log.d("World", "%s: Spawning object: %s", cell.toString(), t.toString());
		cell.add(t);
		t.onAdd(game, cell);
		return t;
	}
	
	public void removeObject(MapObject mp){
		WorldCell cell = getCell(mp);
		if(cell == null){
			Log.w("World", "Attempt to remove object (%s), but cell doesn't exist!", mp.toString());
			return;
		}
		Log.d("World", "%s: Despawning object: %s", cell.toString(), mp.toString());
		cell.remove(mp.getUID());
		mp.onRemove(game, cell);
	}
	
	public MapObject objectForStr(String s){
		int dotIdx = s.indexOf('.');
		long cellid = Long.parseLong(s.substring(0, dotIdx), 16);
		long objid = Long.parseLong(s.substring(dotIdx + 1), 16);
		WorldCell cell = getCell(cellid);
		return cell != null ? cell.get(objid) : null;
	}
	
	public static String objidString(WorldCell cell, MapObject obj){
		return objidString(cell.getCellId().id(), obj.getUID());
	}
	
	public static String objidString(long cellId, long objectId){
		return Long.toHexString(cellId) + "." + Long.toHexString(objectId);
	}
	
}
