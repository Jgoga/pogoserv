package pm.cat.pogoserv.game.model;

import POGOProtos.Enums.POGOProtosEnums.PokemonMove;
import pm.cat.pogoserv.game.config.PokemonDef;

public class Pokemon {
	
	public final PokemonDef def;
	public PokemonMove move1;
	public PokemonMove move2;
	public float height;
	public float weight;
	public int ivAtk, ivDef, ivSta;
	public float cpMultiplier;
	
	public Pokemon(PokemonDef def){
		this.def = def;
	}
	
	public void copyFrom(Pokemon other){
		this.move1 = other.move1;
		this.move2 = other.move2;
		this.height = other.height;
		this.weight = other.weight;
		this.ivAtk = other.ivAtk;
		this.ivDef = other.ivDef;
		this.ivSta = other.ivSta;
		this.cpMultiplier = other.cpMultiplier;
	}
	
	@Override
	public String toString(){
		return def.toString() + " [move1=" + move1 + ", move2=" + move2 + ", ivs=(" + ivAtk + "," + ivDef + "," + ivSta + ")]";
	}
	
}
