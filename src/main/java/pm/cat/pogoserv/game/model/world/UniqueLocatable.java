package pm.cat.pogoserv.game.model.world;

import pm.cat.pogoserv.util.Locatable;
import pm.cat.pogoserv.util.Unique;

public interface UniqueLocatable extends Unique, Locatable {
	default void onAdd(WorldCell w){ }
	default void onRemove(WorldCell w){ }
	
	default String getUniqueStr(){
		return Long.toHexString(getS2CellId().parent(WorldCell.LEVEL).id()) + "." + Long.toHexString(getUID());
	}
	
	static void parseUniqueStr(long[] dest, String s){
		int dotIdx = s.indexOf('.');
		dest[0] = Long.parseLong(s.substring(0, dotIdx), 16);
		dest[1] = Long.parseLong(s.substring(dotIdx + 1), 16);
	}
}