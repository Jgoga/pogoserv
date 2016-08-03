package pm.cat.pogoserv;

import java.io.IOException;

import pm.cat.pogoserv.core.Constants;
import pm.cat.pogoserv.core.PSThreadPoolExecutor;
import pm.cat.pogoserv.core.net.AssetServer;
import pm.cat.pogoserv.core.net.Server;
import pm.cat.pogoserv.core.net.SimpleRPCHandleAllocator;
import pm.cat.pogoserv.db.Database;
import pm.cat.pogoserv.db.DatabaseObjectLoader;
import pm.cat.pogoserv.game.Game;
import pm.cat.pogoserv.game.config.GameSettings;
import pm.cat.pogoserv.game.control.ObjectLoader;
import pm.cat.pogoserv.game.model.world.PeriodicSpawnPoint;
import pm.cat.pogoserv.game.net.request.GameRequestFilter;
import pm.cat.pogoserv.game.net.request.RequestDispatcher;
import pm.cat.pogoserv.game.net.request.Unknown6Handler;
import pm.cat.pogoserv.util.Util;

public class Main {
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		int port = 8888;
		int gameThreads = 1;
		int dbCoreThreads = 1, dbMaxThreads = 1;
		String rpcHost = "pgorelease.nianticlabs.com";
		String dataPath = "data/";
		String database = "jdbc:sqlite:data/db.sqlite3/";
		
		for(int i=0;i<args.length;i++){
			String a = args[i];
			if(a.equals("-p") || a.equals("--port")){ port = Integer.parseInt(args[++i]); }
			else if(a.equals("-t") || a.equals("--threads")){ gameThreads = Integer.parseInt(args[++i]); }
			else if(a.equals("--db-core-threads")){ dbCoreThreads = Integer.parseInt(args[++i]); }
			else if(a.equals("--db-max-threads")){ dbMaxThreads = Integer.parseInt(args[++i]); }
			else if(a.equals("-d") || a.equals("--data")){ dataPath = args[++i]; }
			else if(a.equals("-p") || a.equals("--player-saves")){ database = args[++i]; }
			else if(a.equals("--rpc-host")){ rpcHost = args[++i]; }
		}
		
		Log.i("Main", "Initializing thread pools.");
		Log.i("Main", "Game threads: %d, Database threads: %d (%d)", gameThreads, dbCoreThreads, dbMaxThreads);
		PSThreadPoolExecutor e = new PSThreadPoolExecutor(gameThreads);
		Database.loadSqliteDriver();
		Database db = new Database(database, dbCoreThreads, dbMaxThreads, 300);
		ObjectLoader ol = new DatabaseObjectLoader(db);
		
		GameSettings gs = new GameSettings();
		gs.dataPath = dataPath;
		gs.assetHostPrefix = "https://" + rpcHost + "/assets/";
		gs.parseAll();
		
		Game g = new Game(gs, ol, e);
		
		Server s = new Server(e);
		Log.d("Main", "Using asset version: %d", Constants.ASSET_VERSION);
		s.createContext("/assets/", new AssetServer(dataPath));
		s.bind(port);
		
		int rpc = s.addRpcHandler(
			new GameRequestFilter(g,
					new RequestDispatcher().registerDefaults()
						.then(new Unknown6Handler()))
		);
		s.setRpcAllocator(new SimpleRPCHandleAllocator(rpcHost, rpc));
		
		s.listen();
		
		PeriodicSpawnPoint spawn = new PeriodicSpawnPoint(1000*120, 1000*90, new int[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 16, 17, 150, 144, 145, 146 },
				62.601, 29.7636, g.uidManager.next());
		g.world.addObject(spawn);
		
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run(){
				e.shutdown();
				db.shutdown();
				Log.i("Main", "Bye");
			}
		});
		
		Log.i("Main", "Init complete. Mem: %dM", Util.usedMemory() / (1000*1000));
	}
	
}
