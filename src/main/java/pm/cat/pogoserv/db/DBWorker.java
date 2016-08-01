package pm.cat.pogoserv.db;

import java.sql.Connection;

import pm.cat.pogoserv.Log;

public class DBWorker extends Thread {
	
	final Connection connection;
	
	DBWorker(Connection conn, Runnable r){
		super(r);
		this.connection = conn;
	}
	
	@Override
	public void run(){
		Log.d("Database", "Starting worker thread %s", toString());
		super.run();
		try{
			Log.d("Database", "Shutting down worker thread %s", toString());
			connection.close();
		}catch(Exception e){
			Log.e("DBWorker", e);
		}
	}
	
	@Override
	public String toString(){
		return "DBWorker-" + super.toString();
	}
	
}
