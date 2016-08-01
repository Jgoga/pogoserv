package pm.cat.pogoserv.db.mapper;

import java.util.function.Consumer;

import pm.cat.pogoserv.db.DBWorker;
import pm.cat.pogoserv.util.Unique;

public abstract class Saver<T extends Unique> implements Consumer<DBWorker> {
	
	final T obj;
	
	public Saver(T t){
		this.obj = t;
	}
	
	@Override
	public int hashCode(){
		return Unique.hashCode(obj);
	}

}
