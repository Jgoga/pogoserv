package pm.cat.pogoserv.game.model.pokemon;

import pm.cat.pogoserv.Log;
import pm.cat.pogoserv.game.config.PokemonDef;

// TODO: the cp/stat calculations here are probably wrong and absolutely broken
public class InstancedPokemon extends Pokemon {
	
	private int cp;
	private int attack, defence, stamina;
	private int currentStamina;
	
	// Maximum pokemon level is (I think) trainer level * 2
	// Each power up gives 1 pokemon level
	private int level, capturedLevel;
	
	public InstancedPokemon(PokemonDef def) {
		super(def);
	}
	
	public void copyFrom(InstancedPokemon other){
		super.copyFrom(other);
		cp = other.cp;
		attack = other.attack;
		defence = other.defence;
		stamina = other.stamina;
		currentStamina = other.currentStamina;
		level = other.level;
		capturedLevel = other.capturedLevel;
		recalcStats();
	}
	
	public float getCpMultiplier(){
		return (float) (0.095 * Math.sqrt(capturedLevel));
	}
	
	public float getEffectiveCpMultiplier(){
		return (float) (0.095 * Math.sqrt(level));
	}
	
	public float getAdditionalCpMultiplier(){
		return getEffectiveCpMultiplier() - getCpMultiplier();
	}
	
	public int getAttack(){
		return attack;
	}
	
	public int getDefence(){
		return defence;
	}
	
	public int getStamina(){
		return stamina;
	}
	
	public int getCP(){
		return cp;
	}
	
	public void setFullStamina(){
		currentStamina = stamina;
	}
	
	public void setCurrentStamina(int stam){
		if(stam > stamina)
			stam = stamina;
		currentStamina = stam;
	}
	
	public int getCurrentStamina(){
		return currentStamina;
	}
	
	public int getLevel(){
		return level;
	}
	
	public int getNumUpgrades(){
		return level - capturedLevel;
	}
	
	public void setCapturedLevel(int capturedLevel){
		this.capturedLevel = capturedLevel;
	}
	
	public void setLevel(int level){
		this.level = level;
		recalcStats();
	}
	
	private void recalcStats(){
		float ecpm = getEffectiveCpMultiplier();
		attack = (int) ((def.baseAtk + ivAtk) * ecpm);
		defence = (int) ((def.baseDef + ivDef) * ecpm);
		stamina = (int) ((def.baseSta + ivSta) * ecpm);
		cp = (int) Math.max(10, attack * Math.sqrt(defence) * Math.sqrt(stamina) / 10);
		Log.d("IP", "%s: Recalculated stats. Atk=%d, Def=%d, Sta=%d, CP=%d", this, attack, defence, stamina, cp);
	}

}
