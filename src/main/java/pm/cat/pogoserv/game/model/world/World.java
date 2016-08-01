package pm.cat.pogoserv.game.model.world;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.geometry.S2CellId;
import com.google.common.geometry.S2LatLng;

import POGOProtos.Networking.Envelopes.POGOProtosNetworkingEnvelopes.RequestEnvelope.AuthInfo;
import pm.cat.pogoserv.Log;
import pm.cat.pogoserv.game.Game;
import pm.cat.pogoserv.game.model.player.Player;
import pm.cat.pogoserv.game.request.AuthToken;
import pm.cat.pogoserv.util.Locatable;
import pm.cat.pogoserv.util.Uid2;

// TODO: Maybe use bigger/smaller cells than level 15 (like a quad tree)
// TODO: Dynamic cell loading/unloading
public class World {
	
	// TODO Turn this into a LoadingCache
	private final ConcurrentHashMap<Long, WorldCell> cells = new ConcurrentHashMap<>();
	
	private final LoadingCache<AuthToken, Player> players;
	
	// TODO This is a quick&dirty implementation for playerEncounters and pokestop visits
	//      Guava caches have a big memory overhead. These should be properly done later
	private final Cache<Uid2, MapPokemon> playerEncounters;
	
	private final Game game;
	
	public World(Game game){
		this.game = game;
		
		players = CacheBuilder.newBuilder()
				.softValues()
				.expireAfterAccess(game.settings.playerCacheTime, TimeUnit.MINUTES)
				.removalListener(((RemovalListener<AuthToken, Player>)t -> game.objectController.reapPlayer(t.getValue())))
				.build(new PlayerLoader());
				
		playerEncounters = CacheBuilder.newBuilder()
				.weakValues() // entries get removed when MapPokemon gets GC'd
				// debug
				.removalListener(r -> Log.d("World", "GC'd encounter: %s -> %s", r.getKey().toString(), r.getValue().toString()))
				.build();
	}
	
	public WorldCell getCell(Locatable l){
		return getCell(l.getS2CellId());
	}
	
	public WorldCell getCell(S2CellId id){
		return getCell(id.parent(WorldCell.LEVEL).id());
	}
	
	public WorldCell getCell(long id){
		return cells.get(id);
	}
	
	public WorldCell getOrCreateCell(Locatable l){
		return getOrCreateCell(l.getS2CellId());
	}
	
	public WorldCell getOrCreateCell(S2CellId id){
		id = id.parent(WorldCell.LEVEL);
		WorldCell ret = cells.get(id.id());
		if(ret == null){
			ret = new WorldCell(id);
			cells.put(id.id(), ret);
		}
		return ret;
	}
	
	public Player player(AuthInfo auth){
		return player(AuthToken.fromAuthInfo(auth));
	}
	
	public Player player(AuthToken auth){
		try{
			return players.get(auth);
		}catch(Exception e){
			Log.e("World", "Error while getting player " + auth);
			Log.e("World", e);
			return null;
		}
	}
	
	public <T extends MapObject> T addObject(T t){
		WorldCell cell = getOrCreateCell(t);
		Log.d("World", "%s: Spawning object: %s", cell.toString(), t.toString());
		cell.add(t);
		t.onAdd(game, cell);
		return t;
	}
	
	public void removeObject(MapObject mp){
		WorldCell cell = getCell(mp);
		if(cell == null){
			Log.w("World", "Attempt to remove object (%s), but cell doesn't exist!", mp.toString());
			return;
		}
		Log.d("World", "%s: Despawning object: %s", cell.toString(), mp.toString());
		cell.remove(mp.getUID());
		mp.onRemove(game, cell);
	}
	
	public void setEncountered(Player p, MapPokemon mp){
		playerEncounters.put(new Uid2(p, mp), mp);
	}
	
	public boolean hasPlayerEncountered(Player p, MapPokemon mp){
		return playerEncounters.getIfPresent(new Uid2(p, mp)) != null;
	}
	
	private class PlayerLoader extends CacheLoader<AuthToken, Player> {

		@Override
		public Player load(AuthToken key) {
			return game.objectController.newPlayer(key);
		}
		
	}
	
}
