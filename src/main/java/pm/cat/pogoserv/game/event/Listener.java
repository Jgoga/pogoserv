package pm.cat.pogoserv.game.event;

public interface Listener<T extends Event> {
	
	boolean on(T t);
	
	default Listener<T> then(Listener<T> l){
		return e -> on(e) && l.on(e);
	}
	
	default Listener<T> before(Listener<T> l){
		return e -> l.on(e) && on(e);
	}
	
}
