package pm.cat.pogoserv.game.config;

import POGOProtos.Enums.POGOProtosEnums.PokemonFamilyId;
import POGOProtos.Enums.POGOProtosEnums.PokemonId;
import POGOProtos.Enums.POGOProtosEnums.PokemonMove;
import POGOProtos.Enums.POGOProtosEnums.PokemonMovementType;
import POGOProtos.Enums.POGOProtosEnums.PokemonRarity;
import pm.cat.pogoserv.util.Util;

public class PokemonDef {
	
	public final int id;
	public final String name;
	public int type1, type2;
	public int baseSta, baseAtk, baseDef;
	public int dodgeEnergyDelta;
	
	public PokemonMove[] quickMoves;
	public PokemonMove[] cinematicMoves;
	public int[] evolutionIds;
	public int evolutionPips;
	
	public float baseCaptureRate, baseFleeRate;
	public float collisionRadius, collisionHeight, collisionHeadRadius;
	public PokemonMovementType movementType;
	public float movementTime, jumpTime, attackTime;
	public float[] animationTime;
	public PokemonRarity rarity;
	public float pokedexHeight, pokedexWeight;
	public int parentId;
	public float heightStdDev, weightStdDev;
	public float kmDistanceToHatch;
	public PokemonFamilyId familyId;
	public int candyToEvolve;
	
	public PokemonDef(PokemonId id){
		this.id = id.getNumber();
		this.name = Util.formatString(id.name());
	}
	
	@Override
	public String toString(){
		return "#" + id + "/" + name;
	}
	
}
