package pm.cat.pogoserv.game.model.world;

import java.util.concurrent.ConcurrentHashMap;

import com.google.common.geometry.S2CellId;
import com.google.common.geometry.S2LatLng;

import pm.cat.pogoserv.game.control.WorldController;

// TODO: Maybe use bigger/smaller cells than level 15 (like a quad tree)
// TODO: Dynamic cell loading/unloading
public class World {
	
	private final ConcurrentHashMap<Long, WorldCell> cells = new ConcurrentHashMap<>();
	private final WorldController controller;
	
	public World(WorldController controller){
		this.controller = controller;
	}
	
	public WorldCell getCell(double lat, double lng){
		return getCell(S2CellId.fromLatLng(S2LatLng.fromDegrees(lat, lng)));
	}
	
	public WorldCell getCell(S2CellId id){
		return getCell(id.parent(15).id());
	}
	
	public WorldCell getCell(long id){
		return cells.get(id);
	}
	
	public WorldCell getOrCreateCell(double lat, double lng){
		return getOrCreateCell(S2CellId.fromLatLng(S2LatLng.fromDegrees(lat, lng)));
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
	
}
