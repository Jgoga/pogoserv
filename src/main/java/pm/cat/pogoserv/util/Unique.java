package pm.cat.pogoserv.util;

public interface Unique {
	
	long getUID();
	
	default String getUIDString(){
		return Long.toHexString(getUID());
	}
	
}
