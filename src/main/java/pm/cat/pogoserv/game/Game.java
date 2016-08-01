package pm.cat.pogoserv.game;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import pm.cat.pogoserv.Log;
import pm.cat.pogoserv.game.config.GameSettings;
import pm.cat.pogoserv.game.control.PlayerController;
import pm.cat.pogoserv.game.control.PokemonGen;
import pm.cat.pogoserv.game.control.WorldController;
import pm.cat.pogoserv.game.model.world.World;
import pm.cat.pogoserv.io.BinaryFilePlayerLoader;
import pm.cat.pogoserv.io.PlayerLoader;

public class Game {

	public World world;
	public WorldController worldController;
	public PlayerController playerController;
	public PlayerLoader playerLoader;
	public UidManager uidManager;
	public PokemonGen pokegen;

	public final GameSettings settings;
	private final ScheduledThreadPoolExecutor executor;
	
	public Game(GameSettings settings, ScheduledThreadPoolExecutor executor){
		this.executor = executor;
		this.settings = settings;
		
		pokegen = new PokemonGen(this);
		uidManager = new UidManager(1);
		worldController = new WorldController(this);
		world = new World(worldController);
		playerController = new PlayerController(this);
		playerLoader = new BinaryFilePlayerLoader(this);
	}
	
	public void shutdown(){
		Log.i("Game", "Shutting down executor.");
		executor.shutdown();
	}
	
	public Future<?> submit(GameRunnable r){
		return executor.submit(createRunnable(r));
	}
	
	public ScheduledFuture<?> submit(GameRunnable r, long delay){
		return executor.schedule(createRunnable(r), delay, TimeUnit.MILLISECONDS);
	}
	
	public ScheduledFuture<?> submitFixed(GameRunnable r, long delay){
		return executor.scheduleAtFixedRate(createRunnable(r), 0, delay, TimeUnit.MILLISECONDS);
	}
	
	private Runnable createRunnable(GameRunnable g){
		return () -> g.run(this);
	}
	
}
