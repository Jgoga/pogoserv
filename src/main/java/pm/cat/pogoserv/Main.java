package pm.cat.pogoserv;

import java.io.IOException;

import pm.cat.pogoserv.core.Constants;
import pm.cat.pogoserv.core.PSThreadPoolExecutor;
import pm.cat.pogoserv.core.net.AssetServer;
import pm.cat.pogoserv.core.net.Server;
import pm.cat.pogoserv.core.net.SimpleRPCHandleAllocator;
import pm.cat.pogoserv.game.Game;
import pm.cat.pogoserv.game.config.GameSettings;
import pm.cat.pogoserv.game.request.GameRequestFilter;
import pm.cat.pogoserv.game.request.RequestDispatcher;
import pm.cat.pogoserv.game.request.Unknown6Handler;
import pm.cat.pogoserv.game.world.PeriodicSpawnPoint;
import pm.cat.pogoserv.util.Util;

public class Main {
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		int port = 8888;
		int threads = 1;
		String rpcHost = "pgorelease.nianticlabs.com";
		String dataPath = "data/";
		
		for(int i=0;i<args.length;i++){
			String a = args[i];
			if(a.equals("-p") || a.equals("--port")){ port = Integer.parseInt(args[++i]); }
			else if(a.equals("-t") || a.equals("--threads")){ threads = Integer.parseInt(args[++i]); }
			else if(a.equals("-d") || a.equals("--data")){ dataPath = args[++i]; }
			else if(a.equals("--rpc-host")){ rpcHost = args[++i]; }
		}
		
		Log.i("Main", "Initializing thread pool, threads = %d", threads);
		
		PSThreadPoolExecutor e = new PSThreadPoolExecutor(threads);
		
		GameSettings gs = new GameSettings();
		gs.dataPath = dataPath;
		gs.assetHostPrefix = "https://" + rpcHost + "/assets/";
		gs.parseAll();
		
		Game g = new Game(gs, e);
		
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
		
		PeriodicSpawnPoint spawn = new PeriodicSpawnPoint(1000*60, 1000*30, new int[]{ /*1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 16, 17,*/ 150, 144, 145, 146 }, 62.601, 29.7636, g.uidManager.next());
		g.worldController.addObject(spawn);
		
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run(){
				Log.i("Main", "Exiting");
				e.shutdown();
			}
		});
		
		Log.i("Main", "Init complete. Mem: %dM", Util.usedMemory() / (1000*1000));
	}
	
}
