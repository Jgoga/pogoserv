package pm.cat.pogoserv.util;

public interface Filter<T> {
	
	void run(T t);
	
	default Filter<T> then(Filter<T> f){
		return t -> { run(t); f.run(t); };
	}
	
	default Filter<T> before(Filter<T> f){
		return t -> { f.run(t); run(t); };
	}
	
}
