package pm.cat.pogoserv.db;

import java.lang.Thread.UncaughtExceptionHandler;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

import pm.cat.pogoserv.Log;

public class Database {
	
	final String db;
	final DBConnectionPool pool;
	
	public Database(String db, int corePoolSize, int maxPoolSize, int timeoutSeconds){
		this.db = db;
		this.pool = new DBConnectionPool(corePoolSize, maxPoolSize, timeoutSeconds);
	}
	
	public void shutdown(){
		pool.shutdown();
	}
	
	<T> Future<T> submit(Function<DBWorker, T> f){
		return pool.submit(() -> f.apply((DBWorker)Thread.currentThread()));
	}
	
	void submit(Consumer<DBWorker> f){
		pool.submit(() -> { f.accept((DBWorker) Thread.currentThread()); });
	}
	
	Connection connect(){
		try{
			return DriverManager.getConnection(db);
		}catch(Exception e){
			Log.e("Database", "Failed to connect to database");
			Log.e("Database", e);
			return null;
		}
	}
	
	public static void loadSqliteDriver(){
		Log.d("Database", "Loading sqlite3 driver");
		try{
			Class.forName("org.sqlite.JDBC");
		}catch(ClassNotFoundException e){
			Log.e("Database", "Failed to load sqlite driver, class not found.");
			Log.e("Database", e);
		}
	}
	
	private class DBConnectionPool extends ThreadPoolExecutor implements ThreadFactory, UncaughtExceptionHandler {
		
		public DBConnectionPool(int corePoolSize, int maximumPoolSize, int timeout) {
			super(corePoolSize, maximumPoolSize, timeout, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
			setThreadFactory(this);
		}
		
		@Override
		public void uncaughtException(Thread t, Throwable e) {
			Log.e("Database", "Uncaught exception in database thread: " + t);
			Log.e("Database", e);
		}

		@Override
		public Thread newThread(Runnable r) {
			Thread ret = new DBWorker(connect(), r);
			ret.setUncaughtExceptionHandler(this);
			return ret;
		}
		
	}
	
}
