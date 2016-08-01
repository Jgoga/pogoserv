package pm.cat.pogoserv.game.control;

import pm.cat.pogoserv.Log;
import pm.cat.pogoserv.game.Game;
import pm.cat.pogoserv.game.model.world.MapObject;
import pm.cat.pogoserv.game.model.world.WorldCell;

public class WorldController {
	
	protected final Game game;
	
	public WorldController(Game game){
		this.game = game;
	}
	
	public <T extends MapObject> T addObject(T obj){
		WorldCell cell = game.world.getOrCreateCell(obj.getLatitude(), obj.getLongitude());
		cell.add(obj);
		obj.onAdd(game, cell);
		Log.d("WorldCtrlr", "Spawned %s", obj);
		return obj;
	}
	
	public void removeObject(MapObject obj){
		WorldCell cell = game.world.getCell(obj.getLatitude(), obj.getLongitude());
		if(cell == null){
			Log.w("WorldCtrlr", "Can't remove object: %s: Null cell", obj);
			return;
		}
		Log.d("WorldCtrlr", "Removing %s", obj);
		obj.onRemove(game, cell);
		cell.remove(obj.getUID());
	}
	
}
