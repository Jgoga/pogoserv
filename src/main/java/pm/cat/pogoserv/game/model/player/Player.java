package pm.cat.pogoserv.game.model.player;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import POGOProtos.Enums.POGOProtosEnums.TeamColor;
import pm.cat.pogoserv.Config;
import pm.cat.pogoserv.Log;
import pm.cat.pogoserv.game.Game;
import pm.cat.pogoserv.game.config.GameSettings;
import pm.cat.pogoserv.game.model.world.Encounter;
import pm.cat.pogoserv.game.model.world.MapPokemon;
import pm.cat.pogoserv.game.session.AuthToken;
import pm.cat.pogoserv.util.Locatable;
import pm.cat.pogoserv.util.TimestampVarPool;
import pm.cat.pogoserv.util.TimestampVarPool.TSNode;
import pm.cat.pogoserv.util.Unique;

public class Player implements Unique, Locatable {
	
	private final long uid;
	private Game game;
	
	public String nickname;
	public AuthToken auth;
	public long creationTs;
	public TeamColor team;
	
	private double latitude, longitude;
	
	public Encounter currentEncounter;

	private final TimestampVarPool pool = new TimestampVarPool();
	
	public final TSNode<Currency> pokecoins = pool.allocate(new Currency(Config.POKECOINS));
	public final TSNode<Currency> stardust = pool.allocate(new Currency(Config.STARDUST));
	
	public final Inventory inventory;
	public final Pokedex pokedex;
	public final PlayerInfo stats;
	public final Appearance appearance;
	
	// TODO This is a quick&dirty implementation for playerEncounters and pokestop visits
	//      Guava caches have a big memory overhead. These should be properly done later
	private final Cache<Long, MapPokemon> encounters;
	
	public Player(long uid){
		this.uid = uid;
		inventory = new Inventory(pool);
		pokedex = new Pokedex(pool);
		stats = new PlayerInfo(pool);
		appearance = new Appearance();
		
		encounters = CacheBuilder.newBuilder()
				.weakValues() // entries get removed when MapPokemon gets GC'd
				.removalListener(r -> Log.d("Player", "%s: GC'd encounter: %d -> %s", toString(), r.getKey(), r.getValue().toString()))
				.build();
	}
	
	public void init(Game g){
		this.game = g;
		GameSettings settings = g.getSettings();
		inventory.maxItemStorage = settings.invBaseBagItems;
		inventory.maxPokemonStorage = settings.invBasePokemon;
		recalcLevel();
	}
	
	public Game getGame(){
		return game;
	}
	
	public TimestampVarPool getPool(){
		return pool;
	}
	
	public long getExp(){
		return stats.exp.read().value;
	}
	
	public int getLevel(){
		return stats.level.read().value;
	}
	
	public void addExp(long exp){
		// if lucky egg then 2*exp
		setExp(stats.exp.read().value + exp);
	}
	
	public void setExp(long exp){
		stats.exp.write().value = exp;
		recalcLevel();
	}
	
	public boolean hasEncountered(long uid){
		return encounters.getIfPresent(uid) != null;
	}
	
	public void setEncountered(MapPokemon mp){
		if(mp == null){
			Log.e("Player", "Attempt set a null encounter, this should not happen!");
			return;
		}
		encounters.put(mp.getUID(), mp);
	}
	
	private void recalcLevel(){
		long exp = getExp();
		GameSettings settings = game.getSettings();
		int level = settings.levelForExp(exp);
		if(level != getLevel()){
			stats.level.write().value = level;
		}
		stats.nextLevelExp.write().value = level == settings.maxLevel() ?
			0L : (settings.expForLevel(level+1) - exp);
	}
	
	@Override
	public int hashCode(){
		return (int) ((uid >>> 32) + uid + nickname.hashCode() * 37);
	}
	
	@Override
	public boolean equals(Object o){
		return Unique.equals(this, o);
	}

	@Override
	public long getUID() {
		return uid;
	}
	
	public void setPosition(double latitude, double longitude){
		this.latitude = latitude;
		this.longitude = longitude;
	}

	@Override
	public double getLatitude() {
		return latitude;
	}

	@Override
	public double getLongitude() {
		return longitude;
	}
	
}
