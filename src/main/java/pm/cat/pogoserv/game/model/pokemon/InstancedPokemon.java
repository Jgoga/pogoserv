package pm.cat.pogoserv.game.model.pokemon;

import pm.cat.pogoserv.game.config.PokemonDef;

// TODO: the cp/stat calculations here are probably wrong and absolutely broken
public class InstancedPokemon extends Pokemon {
	
	private float cpMultiplier;
	private float additionalCpMultiplier = 0f;
	
	private int cp;
	private int attack, defence, stamina;
	private int currentStamina;
	
	public InstancedPokemon(PokemonDef def) {
		super(def);
	}
	
	public void copyFrom(InstancedPokemon other){
		super.copyFrom(other);
		this.cpMultiplier = other.cpMultiplier;
		this.additionalCpMultiplier = other.additionalCpMultiplier;
		cp = other.cp;
		attack = other.attack;
		defence = other.defence;
		stamina = other.stamina;
		currentStamina = other.currentStamina;
	}
	
	public float getCpMultiplier(){
		return cpMultiplier;
	}
	
	public void setCpMultiplier(float cpMultiplier){
		this.cpMultiplier = cpMultiplier;
	}
	
	public float getAdditionalCpMultiplier(){
		return additionalCpMultiplier;
	}
	
	public void setAdditionalCpMultiplier(float f){
		additionalCpMultiplier = f;
		recalcStats();
	}
	
	public float getEffectiveCpMultiplier(){
		return cpMultiplier + additionalCpMultiplier;
	}

	// Not sure how this is calculated but it's approx sqrt(trainerLevel)
	public float getMaxEffectiveCpMultiplier(int trainerLevel){
		return (float) (0.95 * Math.sqrt(trainerLevel));
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
	
	public void setCurrentStamina(int stam){
		if(stam > stamina)
			stam = stamina;
		currentStamina = stam;
	}
	
	public int getCurrentStamina(){
		return currentStamina;
	}
	
	private void recalcStats(){
		float ecpm = getEffectiveCpMultiplier();
		attack = (int) ((def.baseAtk + ivAtk) * ecpm);
		defence = (int) ((def.baseDef + ivDef) * ecpm);
		stamina = (int) ((def.baseSta + ivSta) * ecpm);
		cp = (int) Math.floor(attack * Math.sqrt(defence) * Math.sqrt(stamina) / 10);
	}

}
