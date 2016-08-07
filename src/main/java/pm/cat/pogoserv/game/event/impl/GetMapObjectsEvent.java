package pm.cat.pogoserv.game.event.impl;

import java.util.Iterator;

import POGOProtos.Map.POGOProtosMap.MapObjectsStatus;
import pm.cat.pogoserv.game.event.Event;
import pm.cat.pogoserv.game.model.world.UniqueLocatable;

public class GetMapObjectsEvent extends Event {
	
	public final long[] cellIds;
	public final long[] sinceTs;
	// lat & lng not used because they are in Player
	
	public final CellInfo[] cells;
	public MapObjectsStatus status;
	
	public GetMapObjectsEvent(int size){
		this.cellIds = new long[size];
		this.sinceTs = new long[size];
		this.cells = new CellInfo[size];
	}
	
	public int size(){
		return cellIds.length;
	}
	
	public static class CellInfo {
		public final long ts;
		public final Iterator<MapObjectInfo> objs;
		
		public CellInfo(long ts, Iterator<MapObjectInfo> objs){
			this.ts = ts;
			this.objs = objs;
		}
		
	}
	
	public static class MapObjectInfo {
		public static final int SKIP = 0;
		public static final int GYM = 0x1;
		public static final int POKESTOP = 0x2;
		public static final int SPAWNPOINT = 0x4;
		public static final int WILD_POKEMON = 0x8;
		public static final int CATCHABLE_POKEMON = 0x10;
		public static final int NEARBY_POKEMON = 0x20;
		public static final int POKEMON = WILD_POKEMON | CATCHABLE_POKEMON | NEARBY_POKEMON;
		
		public final int type;
		public final UniqueLocatable obj;
		
		public MapObjectInfo(int type, UniqueLocatable obj){
			this.type = type;
			this.obj = obj;
		}
		
	}
	
}
