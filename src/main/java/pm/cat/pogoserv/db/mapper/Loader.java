package pm.cat.pogoserv.db.mapper;

import java.util.function.Function;

import pm.cat.pogoserv.db.DBWorker;

public abstract class Loader<T> implements Function<DBWorker, T> {

}
