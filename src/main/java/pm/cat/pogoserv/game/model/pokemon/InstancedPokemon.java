package pm.cat.pogoserv.game.model.pokemon;

import pm.cat.pogoserv.Log;
import pm.cat.pogoserv.game.config.PokemonDef;
import pm.cat.pogoserv.game.model.player.Player;

public class InstancedPokemon extends Pokemon {
	
	protected Player owner;
	
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
	
	public void init(Player owner){
		this.owner = owner;
		recalcStats();
	}
	
	public Player getOwner(){
		return owner;
	}
	
	public float getCpMultiplier(){
		return owner.getGame().settings.playerCpMultiplier[capturedLevel];
	}
	
	public float getEffectiveCpMultiplier(){
		return owner.getGame().settings.playerCpMultiplier[level];
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
		if(owner != null && level > 2*owner.getLevel()){
			Log.w("InstancedPokemon", "%s: %s: Attempted to set level higher than max %d>2*%d", this, owner, level, owner.getLevel());
			return;
		}
		
		this.level = level;
		
		if(owner != null)
			recalcStats();
	}
	
	private void recalcStats(){
		float ecpm = getEffectiveCpMultiplier();
		attack = (int) ((def.baseAtk + ivAtk) * ecpm);
		defence = (int) ((def.baseDef + ivDef) * ecpm);
		stamina = (int) ((def.baseSta + ivSta) * ecpm);
		cp = (int) Math.max(10, attack * Math.sqrt(defence) * Math.sqrt(stamina) / 10);
		Log.d("InstancedPokemon", "%s: Recalculated stats. Atk=%d, Def=%d, Sta=%d, CP=%d", this, attack, defence, stamina, cp);
	}

}
