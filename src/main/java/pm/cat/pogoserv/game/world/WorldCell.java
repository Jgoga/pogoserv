package pm.cat.pogoserv.game.world;

import java.util.concurrent.ConcurrentHashMap;

import com.google.common.geometry.S2Cell;
import com.google.common.geometry.S2CellId;

// A S2Cell wrapper class
public class WorldCell {
	static final int LEVEL = 15;
	
	private final S2Cell cell;
	private final ConcurrentHashMap<Long, MapObject> objects = new ConcurrentHashMap<>();
	
	WorldCell(long s2cellid){
		this(new S2CellId(s2cellid));
	}
	
	WorldCell(S2CellId s2cellid){
		this.cell = new S2Cell(s2cellid);
	}
	
	S2Cell getS2Cell(){
		return cell;
	}
	
	public void add(MapObject obj){
		objects.put(obj.getUid(), obj);
	}
	
	public void remove(long uid){
		objects.remove(uid);
	}
	
	public Iterable<MapObject> objects(){
		return objects.values();
	}
	
	@Override
	public String toString(){
		long id = cell.id().id();
		String binary = Long.toBinaryString(id);
		while(binary.length() < 64)
			binary = "0" + binary;
		return String.format("%-20d:%s:%s", id, binary, cell.toString());
	}

}
