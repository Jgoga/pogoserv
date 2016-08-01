package pm.cat.pogoserv.util;

public interface Unique {
	
	long getUID();
	
	default String getUIDString(){
		return Long.toHexString(getUID());
	}
	
	static boolean equals(Unique u, Object o){
		return o instanceof Unique && u.getUID() == ((Unique)o).getUID();
	}
	
}
