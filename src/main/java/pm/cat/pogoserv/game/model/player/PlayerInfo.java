package pm.cat.pogoserv.game.model.player;

import POGOProtos.Data.Player.POGOProtosDataPlayer.PlayerStats;
import pm.cat.pogoserv.util.TimestampVarPool;
import pm.cat.pogoserv.util.TimestampVarPool.TSNode;

public class PlayerInfo {
	
	final TSNode<Stat<Long>> exp;
	final TSNode<Stat<Integer>> level;
	final TSNode<Stat<Long>> nextLevelExp;
	public final TSNode<Stat<Integer>> numPokemonEncountered;
	public final TSNode<Stat<Integer>> numPokemonCaptured;
	public final TSNode<Stat<Float>> kmWalked;
	public final TSNode<Stat<Integer>> numEvolutions;
	public final TSNode<Stat<Integer>> numPokestopVisits;
	public final TSNode<Stat<Integer>> numPokeballsThrown;
	public final TSNode<Stat<Integer>> numEggsHatched;
	public final TSNode<Stat<Integer>> numBigMagikarpCaught;
	public final TSNode<Stat<Integer>> numSmallRattataCaught;
	public final TSNode<Stat<Integer>> numBattleAttackWon;
	public final TSNode<Stat<Integer>> numBattleAttackTotal;
	public final TSNode<Stat<Integer>> numBattleDefendWon;
	public final TSNode<Stat<Integer>> numBattleTrainingWon;
	public final TSNode<Stat<Integer>> numBattleTrainingTotal;
	public final TSNode<Stat<Integer>> prestigeRaisedTotal;
	public final TSNode<Stat<Integer>> prestigeDroppedTotal;
	public final TSNode<Stat<Integer>> numPokemonDeployed;
	
	PlayerInfo(TimestampVarPool pool){
		exp = pool.allocate(new Stat<Long>(PlayerStats.EXPERIENCE_FIELD_NUMBER));
		level = pool.allocate(new Stat<Integer>(PlayerStats.LEVEL_FIELD_NUMBER));
		nextLevelExp = pool.allocate(new Stat<Long>(PlayerStats.NEXT_LEVEL_XP_FIELD_NUMBER));
		numPokemonEncountered = pool.allocate(new Stat<Integer>(PlayerStats.POKEMONS_ENCOUNTERED_FIELD_NUMBER));
		numPokemonCaptured = pool.allocate(new Stat<Integer>(PlayerStats.POKEMONS_CAPTURED_FIELD_NUMBER));
		kmWalked = pool.allocate(new Stat<Float>(PlayerStats.KM_WALKED_FIELD_NUMBER));
		numEvolutions = pool.allocate(new Stat<Integer>(PlayerStats.EVOLUTIONS_FIELD_NUMBER));
		numPokestopVisits = pool.allocate(new Stat<Integer>(PlayerStats.POKE_STOP_VISITS_FIELD_NUMBER));
		numPokeballsThrown = pool.allocate(new Stat<Integer>(PlayerStats.POKEBALLS_THROWN_FIELD_NUMBER));
		numEggsHatched = pool.allocate(new Stat<Integer>(PlayerStats.EGGS_HATCHED_FIELD_NUMBER));
		numBigMagikarpCaught = pool.allocate(new Stat<Integer>(PlayerStats.BIG_MAGIKARP_CAUGHT_FIELD_NUMBER));
		numSmallRattataCaught = pool.allocate(new Stat<Integer>(PlayerStats.SMALL_RATTATA_CAUGHT_FIELD_NUMBER));
		numBattleAttackWon = pool.allocate(new Stat<Integer>(PlayerStats.BATTLE_ATTACK_WON_FIELD_NUMBER));
		numBattleAttackTotal = pool.allocate(new Stat<Integer>(PlayerStats.BATTLE_ATTACK_TOTAL_FIELD_NUMBER));
		numBattleDefendWon = pool.allocate(new Stat<Integer>(PlayerStats.BATTLE_DEFENDED_WON_FIELD_NUMBER));
		numBattleTrainingWon = pool.allocate(new Stat<Integer>(PlayerStats.BATTLE_TRAINING_WON_FIELD_NUMBER));
		numBattleTrainingTotal = pool.allocate(new Stat<Integer>(PlayerStats.BATTLE_TRAINING_TOTAL_FIELD_NUMBER));
		prestigeRaisedTotal = pool.allocate(new Stat<Integer>(PlayerStats.PRESTIGE_RAISED_TOTAL_FIELD_NUMBER));
		prestigeDroppedTotal = pool.allocate(new Stat<Integer>(PlayerStats.PRESTIGE_DROPPED_TOTAL_FIELD_NUMBER));
		numPokemonDeployed = pool.allocate(new Stat<Integer>(PlayerStats.POKEMON_DEPLOYED_FIELD_NUMBER));
	}
	
	public static void setDefaults(PlayerInfo ps){
		ps.exp.write().value = 0L;
		ps.level.write().value = 1;
		//ps.nextLevelExp.write().value = 0L;
		ps.numPokemonEncountered.write().value = 0;
		ps.numPokemonCaptured.write().value = 0;
		ps.kmWalked.write().value = 0f;
		ps.numEvolutions.write().value = 0;
		ps.numPokestopVisits.write().value = 0;
		ps.numPokeballsThrown.write().value = 0;
		ps.numEggsHatched.write().value = 0;
		//ps.numBigMagikarpCaught.set(0);
		//ps.numSmallRattataCaught.set(0);
		ps.numBattleAttackWon.write().value = 0;
		ps.numBattleAttackTotal.write().value = 0;
		ps.numBattleDefendWon.write().value = 0;
		ps.numBattleTrainingWon.write().value = 0;
		ps.numBattleTrainingTotal.write().value = 0;
		ps.prestigeRaisedTotal.write().value = 0;
		ps.prestigeDroppedTotal.write().value = 0;
		ps.numPokemonDeployed.write().value = 0;
	}
	
	public static class Stat<T> {
		public final int id;
		public T value;
		
		private Stat(int id){
			this.id = id;
			this.value = null;
		}
		
	}
	
}
