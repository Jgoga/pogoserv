package pm.cat.pogoserv.game.control;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import POGOProtos.Enums.POGOProtosEnums.TeamColor;
import POGOProtos.Inventory.Item.POGOProtosInventoryItem.ItemId;
import POGOProtos.Networking.Envelopes.POGOProtosNetworkingEnvelopes.RequestEnvelope.AuthInfo;
import pm.cat.pogoserv.Log;
import pm.cat.pogoserv.game.Game;
import pm.cat.pogoserv.game.config.GameSettings;
import pm.cat.pogoserv.game.model.player.Appearance;
import pm.cat.pogoserv.game.model.player.Player;
import pm.cat.pogoserv.game.model.player.PlayerInfo;
import pm.cat.pogoserv.game.model.world.MapPokemon;
import pm.cat.pogoserv.game.request.AuthToken;
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
			Player ret = cache.get(token);
			System.out.println(ret);
			return ret;
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
	
	public int levelForExp(long exp){
		return Util.insertionPoint(game.settings.playerRequiredExp, (int) exp);
	}
	
	public int defaultBagItems(){
		return game.settings.invBaseBagItems;
	}
	
	public int defaultInvPokemon(){
		return game.settings.invBasePokemon;
	}
	
	private Player newPlayer(){
		Player ret = new Player(this, game.uidManager.next());
		ret.creationTs = System.currentTimeMillis();
		// TODO: Should ask for nick.
		ret.nickname = "PotofuIsCute";
		ret.team = TeamColor.NEUTRAL;
		
		ret.pokecoins.write().amt = 100;
		ret.stardust.write().amt = 1337;
		// Just some test pokeballs
		ret.inventory.item(game.settings.getItem(ItemId.ITEM_POKE_BALL_VALUE)).write().count = 50;
		ret.inventory.item(game.settings.getItem(ItemId.ITEM_GREAT_BALL_VALUE)).write().count = 25;
		ret.inventory.item(game.settings.getItem(ItemId.ITEM_MASTER_BALL_VALUE)).write().count = 10;

		// TODO: Database stuff goes here
		PlayerInfo.setDefaults(ret.stats);
		Appearance.setDefaults(ret.appearance);
		return ret;
	}
	
	private class PlayerLoader extends CacheLoader<AuthToken, Player> {

		@Override
		public Player load(AuthToken t) {
			try{
				return game.playerLoader.loadPlayer(t.parseID());
			}catch(IOException e){
				Log.w("PlayerCtr", "Error loading player data: %s (%s)", e.toString(), t);
				return newPlayer();
			}
		}
		
	}
	
	private class PlayerReaper implements RemovalListener<AuthToken, Player> {

		@Override
		public void onRemoval(RemovalNotification<AuthToken, Player> p) {
			Log.d("PlayerCtr", "Reaped %s", p.getValue());
		}
		
	}

}
