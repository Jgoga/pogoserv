package pm.cat.pogoserv.game.model.player;

import POGOProtos.Enums.POGOProtosEnums.TeamColor;
import pm.cat.pogoserv.core.Constants;
import pm.cat.pogoserv.game.control.PlayerController;
import pm.cat.pogoserv.game.request.AuthToken;
import pm.cat.pogoserv.util.TimestampVarPool;
import pm.cat.pogoserv.util.TimestampVarPool.TSNode;
import pm.cat.pogoserv.util.Unique;

public class Player implements Unique {
	
	private final long uid;
	
	public String nickname;
	public AuthToken auth;
	public long creationTs;
	public TeamColor team;

	private final TimestampVarPool pool = new TimestampVarPool();
	private final PlayerController controller;
	
	public final TSNode<Currency> pokecoins = pool.allocate(new Currency(Constants.POKECOINS));
	public final TSNode<Currency> stardust = pool.allocate(new Currency(Constants.STARDUST));
	
	public final Inventory inventory;
	public final Pokedex pokedex;
	public final PlayerInfo stats;
	public final Appearance appearance;
	
	public Player(PlayerController controller, long uid){
		this.controller = controller;
		this.uid = uid;
		inventory = new Inventory(pool, controller.defaultBagItems(), controller.defaultInvPokemon());
		pokedex = new Pokedex(pool);
		stats = new PlayerInfo(pool);
		appearance = new Appearance();
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
		int level = controller.levelForExp(exp);
		if(level != getLevel())
			stats.level.write().value = level;
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
	
}
