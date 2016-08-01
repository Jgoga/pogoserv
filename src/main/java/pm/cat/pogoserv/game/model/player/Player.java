package pm.cat.pogoserv.game.model.player;

import POGOProtos.Enums.POGOProtosEnums.TeamColor;
import pm.cat.pogoserv.core.Constants;
import pm.cat.pogoserv.game.Game;
import pm.cat.pogoserv.game.request.AuthToken;
import pm.cat.pogoserv.util.Locatable;
import pm.cat.pogoserv.util.TimestampVarPool;
import pm.cat.pogoserv.util.TimestampVarPool.TSNode;
import pm.cat.pogoserv.util.Unique;
import pm.cat.pogoserv.util.Util;

public class Player implements Unique, Locatable {
	
	private final long uid;
	Game game;
	
	public String nickname;
	public AuthToken auth;
	public long creationTs;
	public TeamColor team;
	
	private double latitude, longitude;

	private final TimestampVarPool pool = new TimestampVarPool();
	
	public final TSNode<Currency> pokecoins = pool.allocate(new Currency(Constants.POKECOINS));
	public final TSNode<Currency> stardust = pool.allocate(new Currency(Constants.STARDUST));
	
	public final Inventory inventory;
	public final Pokedex pokedex;
	public final PlayerInfo stats;
	public final Appearance appearance;
	
	public Player(long uid){
		this.uid = uid;
		inventory = new Inventory(pool);
		pokedex = new Pokedex(pool);
		stats = new PlayerInfo(pool);
		appearance = new Appearance();
	}
	
	public void attachTo(Game g){
		this.game = g;
		inventory.maxItemStorage = g.settings.invBaseBagItems;
		inventory.maxPokemonStorage = g.settings.invBasePokemon;
		recalcLevel();
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
	
	public void setEXP(long exp){
		stats.exp.write().value = exp;
		recalcLevel();
	}
	
	private void recalcLevel(){
		int level = Util.insertionPoint(game.settings.playerRequiredExp, (int) getExp());
		if(level != getLevel()){
			stats.level.write().value = 0;
			stats.nextLevelExp.write().value = level == game.settings.maxLevel() ?
				0L : game.settings.playerRequiredExp[level+1];
		}
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
