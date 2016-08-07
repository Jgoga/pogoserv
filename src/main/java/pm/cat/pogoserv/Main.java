package pm.cat.pogoserv;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import pm.cat.pogoserv.db.Database;
import pm.cat.pogoserv.db.DatabaseObjectMapper;
import pm.cat.pogoserv.game.Game;
import pm.cat.pogoserv.game.config.GameSettings;
import pm.cat.pogoserv.game.model.world.PeriodicSpawnPoint;
import pm.cat.pogoserv.game.model.world.World;
import pm.cat.pogoserv.game.net.GameService;
import pm.cat.pogoserv.game.net.RpcAllocationService;
import pm.cat.pogoserv.net.Server;
import pm.cat.pogoserv.net.util.FileService;
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
		ScheduledExecutorService exec = new PSThreadPool(gameThreads);
		Database.loadSqliteDriver();
		Database db = new Database(database, dbCoreThreads, dbMaxThreads, 300);
		DatabaseObjectMapper dbmapper = new DatabaseObjectMapper(db);
		
		GameSettings settings = new GameSettings();
		settings.dataPath = dataPath;
		settings.assetHostPrefix = "https://" + rpcHost + "/assets/";
		settings.parseAll();
		
		Game g = new Game(settings, exec);
		g.getEventListeners().registerDefaults();
		g.init(new World(dbmapper));
		
		GameService gs = new GameService(g);
		gs.getRequestDispatcher().registerDefaults();
		gs.setPlayerMapper(dbmapper);
		
		Server s = new Server(exec);

		Log.d("Main", "Using asset version: %d", Config.ASSET_VERSION);
		s.addService("/assets/", new FileService(dataPath));
		s.addService(Config.NEW_RPC_ENDPOINT, RpcAllocationService.constantAllocator(rpcHost, 1));
		s.addService(String.format(Config.RPC_ENDPOINT, 1) + "/rpc", gs);
		
		s.bind(port);
		s.listen();
		
		PeriodicSpawnPoint spawn = new PeriodicSpawnPoint(g.getUidGen().next(), 62.601, 29.7636,
				1000*120, 1000*90, new int[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 16, 17, 150, 144, 145, 146 });
		spawn.init(g);
		g.getWorld().add(spawn);
		
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run(){
				exec.shutdown();
				db.shutdown();
				gs.shutdown();
				Log.i("Main", "Bye");
			}
		});
		
		Log.i("Main", "Init complete. Mem: %dM", Util.usedMemory() / (1000*1000));
	}
	
}
