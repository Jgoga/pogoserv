package pm.cat.pogoserv.game;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import pm.cat.pogoserv.Log;
import pm.cat.pogoserv.game.config.GameSettings;
import pm.cat.pogoserv.game.control.AuthHandler;
import pm.cat.pogoserv.game.control.ObjectController;
import pm.cat.pogoserv.game.control.ObjectLoader;
import pm.cat.pogoserv.game.control.PokemonGen;
import pm.cat.pogoserv.game.control.impl.DefaultAuthHandler;
import pm.cat.pogoserv.game.control.impl.DefaultObjectController;
import pm.cat.pogoserv.game.control.impl.DefaultPokemonGen;
import pm.cat.pogoserv.game.model.world.World;

public class Game {

	public World world;
	
	public UidManager uidManager;
	
	public ObjectLoader objectLoader;
	public ObjectController objectController;
	public PokemonGen pokegen;
	public AuthHandler authHandler;

	public final GameSettings settings;
	private final ScheduledThreadPoolExecutor executor;
	
	public Game(GameSettings settings, ObjectLoader objectLoader, ScheduledThreadPoolExecutor executor){
		this.executor = executor;
		this.settings = settings;
		this.objectLoader = objectLoader;
		
		pokegen = new DefaultPokemonGen();
		objectController = new DefaultObjectController(this);
		uidManager = new UidManager(1);
		world = new World(this);
		authHandler = new DefaultAuthHandler(this);
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
