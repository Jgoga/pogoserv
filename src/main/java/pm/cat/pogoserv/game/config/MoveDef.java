package pm.cat.pogoserv.game.config;

import POGOProtos.Enums.POGOProtosEnums.PokemonMove;
import pm.cat.pogoserv.util.Util;

public class MoveDef {
	
	public final int id;
	public final String name;
	public int type;
	public float power;
	public float accuracy;
	public float criticalChance;
	public float heal;
	public float staLoss;
	public int trainerLevelMin, trainerLevelMax;
	public int durationMs;
	public int damageWindowStartMs, damageWindowEndMs;
	public int energyDelta;
	
	public MoveDef(PokemonMove id){
		this(id.getNumber(), Util.formatString(id.name()));
	}
	
	public MoveDef(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	@Override
	public String toString(){
		return "#" + id + "/" + name;
	}
	
}
