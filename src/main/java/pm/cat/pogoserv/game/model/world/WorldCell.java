package pm.cat.pogoserv.game.model.world;

import java.util.concurrent.ConcurrentHashMap;

import com.google.common.geometry.S2CellId;
import com.google.common.geometry.S2LatLng;

import pm.cat.pogoserv.Log;
import pm.cat.pogoserv.util.Locatable;

// A S2Cell wrapper class
public class WorldCell implements Locatable {
	static final int LEVEL = 15;
	
	private final S2CellId cellId;
	private final ConcurrentHashMap<Long, UniqueLocatable> objects = new ConcurrentHashMap<>();
	
	WorldCell(long s2cellid){
		this(new S2CellId(s2cellid));
	}
	
	WorldCell(S2CellId s2cellid){
		this.cellId = s2cellid;
	}
	
	public S2CellId getCellId(){
		return cellId;
	}
	
	public void add(UniqueLocatable obj){
		objects.put(obj.getUID(), obj);
		obj.onAdd(this);
	}
	
	public UniqueLocatable get(long uid){
		return objects.get(uid);
	}
	
	public void remove(long uid){
		UniqueLocatable ul = objects.remove(uid);
		if(ul == null){
			Log.e("WorldCell", "%s: Attempt to remove nonexisting object: %x", this, uid);
			return;
		}
		ul.onRemove(this);
	}
	
	public Iterable<UniqueLocatable> objects(){
		return objects.values();
	}
	
	@Override
	public String toString(){
		return "WorldCell[" + cellId.id() + "]";
	}

	@Override
	public double getLatitude() {
		return s2LatLngPos().latDegrees();
	}

	@Override
	public double getLongitude() {
		return s2LatLngPos().lngDegrees();
	}
	
	@Override
	public S2LatLng s2LatLngPos() {
		return cellId.toLatLng();
	}

}
