package pm.cat.pogoserv.game.world;

import java.util.concurrent.ConcurrentHashMap;

import com.google.common.geometry.S2Cell;
import com.google.common.geometry.S2CellId;

// A S2Cell wrapper class
public class WorldCell {
	static final int LEVEL = 15;
	
	private final S2CellId cellId;
	private final ConcurrentHashMap<Long, MapObject> objects = new ConcurrentHashMap<>();
	
	WorldCell(long s2cellid){
		this(new S2CellId(s2cellid));
	}
	
	WorldCell(S2CellId s2cellid){
		this.cellId = s2cellid;
	}
	
	public void add(MapObject obj){
		objects.put(obj.getUid(), obj);
	}
	
	public MapObject get(long uid){
		return objects.get(uid);
	}
	
	public void remove(long uid){
		objects.remove(uid);
	}
	
	public Iterable<MapObject> objects(){
		return objects.values();
	}
	
	@Override
	public String toString(){
		long id = cellId.id();
		String binary = Long.toBinaryString(id);
		while(binary.length() < 64)
			binary = "0" + binary;
		return String.format("%-20d:%s:%s", id, binary, cellId.toString());
	}	
	
	public static String uidString(WorldCell cell, MapObject obj){
		return uidString(cell.cellId.id(), obj.getUid());
	}
	
	public static String uidString(WorldCell cell, long objectId){
		return uidString(cell.cellId.id(), objectId);
	}
	
	public static String uidString(long cellId, long objectId){
		return Long.toHexString(cellId) + "." + Long.toHexString(objectId);
	}
	
	public static long[] parseUid(String uidString){
		int idx = uidString.indexOf('.');
		return new long[] {
			Long.parseLong(uidString.substring(0, idx), 16),
			Long.parseLong(uidString.substring(idx+1), 16)
		};
	}

}