package pm.cat.pogoserv.util;

// Everything that implements Unique should have a distinct uid
// ie. (u1 instanceof Unique && u2 instanceof Unique) => (u1.getUID() != u2.getUID())
public interface Unique {
	
	long getUID();
	
	static boolean equals(Unique u, Object o){
		return o instanceof Unique && u.getUID() == ((Unique)o).getUID();
	}
	
	static int hashCode(Unique u){
		return Long.hashCode(u.getUID());
	}
	
}
