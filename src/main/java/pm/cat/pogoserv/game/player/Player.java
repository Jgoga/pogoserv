package pm.cat.pogoserv.game.player;

import POGOProtos.Enums.POGOProtosEnums.TeamColor;
import pm.cat.pogoserv.core.Constants;
import pm.cat.pogoserv.util.TimestampVarPool;
import pm.cat.pogoserv.util.TimestampVarPool.TSNode;
import pm.cat.pogoserv.util.Unique;

public class Player implements Unique {
	
	private final AuthToken auth;
	public final long uid;
	public final String nickname;
	public final long creationTs;
	
	private TeamColor team;

	private final TimestampVarPool pool = new TimestampVarPool();
	
	public final TSNode<Currency> pokecoins = pool.allocate(new Currency(Constants.POKECOINS));
	public final TSNode<Currency> stardust = pool.allocate(new Currency(Constants.STARDUST));
	
	public final Inventory inventory = new Inventory(pool);
	public final Pokedex pokedex = new Pokedex(pool);
	public final PlayerInfo stats = new PlayerInfo(pool);
	public final PlayerAppearance appearance = new PlayerAppearance();
	
	Player(long uid, AuthToken auth, String nick, long creationTs){
		this.uid = uid;
		this.auth = auth;
		this.nickname = nick;
		this.creationTs = creationTs;
	}
	
	public TeamColor getTeam(){
		return TeamColor.NEUTRAL; // TODO
	}
	
	public TimestampVarPool getPool(){
		return pool;
	}
	
	@Override
	public int hashCode(){
		return auth.hashCode();
	}
	
	@Override
	public boolean equals(Object o){
		return o instanceof Player && ((Player)o).auth.equals(auth);
	}
	
	@Override
	public String toString(){
		return nickname + " (" + auth + ")";
	}

	@Override
	public long getUID() {
		return uid;
	}
	
}
