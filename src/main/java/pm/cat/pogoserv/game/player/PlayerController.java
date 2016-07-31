package pm.cat.pogoserv.game.player;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import POGOProtos.Inventory.Item.POGOProtosInventoryItem.ItemId;
import POGOProtos.Networking.Envelopes.POGOProtosNetworkingEnvelopes.RequestEnvelope.AuthInfo;
import pm.cat.pogoserv.Log;
import pm.cat.pogoserv.game.Game;
import pm.cat.pogoserv.game.config.GameSettings;
import pm.cat.pogoserv.game.world.MapPokemon;
import pm.cat.pogoserv.util.Uid2;
import pm.cat.pogoserv.util.Util;

public class PlayerController {
	
	private final LoadingCache<AuthToken, Player> cache;
	// TODO This is a quick&dirt implementation for playerEncounters and pokestop visits
	//      Guava caches have a big memory overhead. These should be properly done later
	private final Cache<Uid2, MapPokemon> playerEncounters;
	//private final Cache<Uid2, Long> playerPokestopVisits;
	private final Game game;
	
	public PlayerController(Game game){
		this.game = game;
		GameSettings s = game.settings;
		
		cache = CacheBuilder.newBuilder()
			.softValues()
			.expireAfterAccess(s.playerCacheTime, TimeUnit.MINUTES)
			.removalListener(new PlayerReaper())
			.build(new PlayerLoader());
		
		playerEncounters = CacheBuilder.newBuilder()
			.weakValues() // entries get removed when MapPokemon gets GC'd
			// debug
			.removalListener(r -> Log.d("PlayerCtr", "GC'd encounter: %s -> %s", r.getKey().toString(), r.getValue().toString()))
			.build();
		
		// TODO pokestops
		//playerPokestopVisits = CacheBuilder.newBuilder()
		//	.expireAfterAccess(5, TimeUnit.MINUTES)
		//	.build();
	}
	
	public Player player(AuthInfo auth){
		return player(AuthToken.fromAuthInfo(auth));
	}
	
	public Player player(AuthToken token){
		try{
			return cache.get(token);
		}catch(ExecutionException e){
			Log.e("PlayerCtr", e);
			return null;
		}
	}
	
	public void setEncountered(Player p, MapPokemon mp){
		playerEncounters.put(new Uid2(p, mp), mp);
	}
	
	public boolean hasPlayerEncountered(Player p, MapPokemon mp){
		return playerEncounters.getIfPresent(new Uid2(p, mp)) != null;
	}
	
	public void updatePlayerEXP(Player p, long exp){
		p.stats.exp.write().value = exp;
		int[] xplevel = game.settings.playerRequiredExp;
		int maxlevel = game.settings.maxLevel();
		int level = p.stats.level.read().value;
		if(xplevel[level] >= exp && (level == maxlevel || exp < xplevel[level+1]))
			return;
		
		if(exp >= xplevel[level+1] && (level+1 == maxlevel || exp < xplevel[level+2])){
			// Got 1 level
			level++;
		}else{
			// Got multiple levels or something weird
			level = Util.insertionPoint(xplevel, (int)exp);
		}
		
		p.stats.level.write().value = level;
		p.stats.nextLevelExp.write().value = (long) game.settings.playerRequiredExp[level == maxlevel ? level : (level+1)];
	}
	
	private class PlayerLoader extends CacheLoader<AuthToken, Player> {

		@Override
		public Player load(AuthToken t) {
			// TODO
			String nick = "NanahiraIsCute";
			// Creates a new player every time (new UID)
			Player ret = new Player(game.uidManager.next(), t, nick, System.currentTimeMillis() - 1000*30*60);
			
			ret.pokecoins.write().amt = 100;
			ret.stardust.write().amt = 1337;
			ret.inventory.maxItemStorage = game.settings.invBaseBagItems;
			ret.inventory.maxPokemonStorage = game.settings.invBasePokemon;
			// Just some test pokeballs
			ret.inventory.item(game.settings.getItem(ItemId.ITEM_POKE_BALL_VALUE)).write().count = 50;
			ret.inventory.item(game.settings.getItem(ItemId.ITEM_GREAT_BALL_VALUE)).write().count = 25;
			ret.inventory.item(game.settings.getItem(ItemId.ITEM_MASTER_BALL_VALUE)).write().count = 10;

			// TODO: Database stuff goes here
			PlayerInfo.setDefaults(ret.stats);
			PlayerAppearance.setDefaults(ret.appearance);
			updatePlayerEXP(ret, game.settings.playerRequiredExp[20]);
			return ret;
		}
		
	}
	
	private class PlayerReaper implements RemovalListener<AuthToken, Player> {

		@Override
		public void onRemoval(RemovalNotification<AuthToken, Player> p) {
			Log.d("PlayerCtr", "Reaped %s", p.getValue());
		}
		
	}

}
