package pm.cat.pogoserv.game;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import pm.cat.pogoserv.Log;
import pm.cat.pogoserv.game.config.GameSettings;
import pm.cat.pogoserv.game.event.EventListenerSet;
import pm.cat.pogoserv.game.model.world.World;

public class Game {
	
	protected final GameSettings settings;
	protected final UidGen uidGen = new UidGen(1);
	protected final EventListenerSet listeners = new EventListenerSet();
	protected World world;
	
	private final ScheduledExecutorService executor;
	
	public Game(GameSettings settings, ScheduledExecutorService executor){
		this.executor = executor;
		this.settings = settings;
	}
	
	public void init(World world){
		this.world = world;
		world.init(this);
	}	
	
	public void setUidGenValue(long l){
		uidGen.set(l);
	}
	
	public UidGen getUidGen(){
		return uidGen;
	}
	
	public World getWorld(){
		return world;
	}
	
	public GameSettings getSettings(){
		return settings;
	}
	
	public EventListenerSet getEventListeners(){
		return listeners;
	}
	
	public ScheduledExecutorService getExecutor(){
		return executor;
	}
	
	public void shutdown(){
		Log.i("Game", "Shutting down executor.");
		executor.shutdown();
	}
	
	public Future<?> schedule(Runnable r){
		return executor.submit(r);
	}
	
	public ScheduledFuture<?> schedule(Runnable r, long delay){
		return executor.schedule(r, delay, TimeUnit.MILLISECONDS);
	}
	
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable r, long delay){
		return executor.scheduleAtFixedRate(r, 0, delay, TimeUnit.MILLISECONDS);
	}
	
	public ScheduledFuture<?> scheduleAt(Runnable r, long timestamp){
		return schedule(r, timestamp - System.currentTimeMillis());
	}
	
}
