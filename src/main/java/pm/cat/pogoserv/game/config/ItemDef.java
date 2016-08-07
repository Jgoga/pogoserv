package pm.cat.pogoserv.game.config;

import com.google.common.primitives.Floats;

import POGOProtos.Enums.POGOProtosEnums.ItemEffect;
import POGOProtos.Enums.POGOProtosEnums.PokemonType;
import POGOProtos.Inventory.POGOProtosInventory.EggIncubatorType;
import POGOProtos.Inventory.POGOProtosInventory.InventoryUpgradeType;
import POGOProtos.Settings.Master.POGOProtosSettingsMaster.ItemSettings;
import POGOProtos.Settings.Master.Item.POGOProtosSettingsMasterItem.EggIncubatorAttributes;
import POGOProtos.Settings.Master.Item.POGOProtosSettingsMasterItem.ExperienceBoostAttributes;
import POGOProtos.Settings.Master.Item.POGOProtosSettingsMasterItem.FoodAttributes;
import POGOProtos.Settings.Master.Item.POGOProtosSettingsMasterItem.IncenseAttributes;
import POGOProtos.Settings.Master.Item.POGOProtosSettingsMasterItem.InventoryUpgradeAttributes;
import POGOProtos.Settings.Master.Item.POGOProtosSettingsMasterItem.PokeballAttributes;
import POGOProtos.Settings.Master.Item.POGOProtosSettingsMasterItem.PotionAttributes;
import POGOProtos.Settings.Master.Item.POGOProtosSettingsMasterItem.ReviveAttributes;

// The attribute thing isn't really beautiful, I will refactor it later
// maybe subclasses or something
public class ItemDef {
	
	public final int id;
	public final int type;
	public float dropFrequency;
	public int dropTrainerLevel;
	public Object attrs;
	
	ItemDef(int id, int type, Object attrs){
		this(id, type, 0, 0, attrs);
	}
	
	ItemDef(int id, int type, float dropFreq, int dropLevel, Object attrs){
		this.id = id;
		this.type = type;
		this.dropFrequency = dropFreq;
		this.dropTrainerLevel = dropLevel;
		this.attrs = attrs;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getAttrs(){
		return (T) attrs;
	}
	
	@Override
	public String toString(){
		return "#" + id + "/" + attrs;
	}
	
	static ItemDef parse(String id, ItemSettings is){
		Object attrs = null;
		// TODO: Tän vois oikeestaan tehä switchillä itemType
		if(id.endsWith("_BALL"))
			attrs = new PokeballAttrs(is.getPokeball());
		else if(id.endsWith("_BERRY"))
			attrs = new FoodAttrs(is.getFood());
		else if(id.endsWith("_POTION"))
			attrs = new PotionAttrs(is.getPotion());
		else if(id.startsWith("ITEM_INCENSE_"))
			attrs = new IncenseAttrs(is.getIncense());
		else if(id.startsWith("ITEM_INCUBATOR_"))
			attrs = new EggIncubatorAttrs(is.getEggIncubator());
		else if(id.endsWith("_UPGRADE"))
			attrs = new InventoryUpgradeAttrs(is.getInventoryUpgrade());
		else if(id.equals("ITEM_LUCKY_EGG"))
			attrs = new ExpBoostAttrs(is.getXpBoost());
		else if(id.endsWith("_REVIVE"))
			attrs = new ReviveAttrs(is.getRevive());
		if(attrs == null)
			return null;
		return new ItemDef(is.getItemIdValue(), is.getItemTypeValue(),
			is.getDropFreq(), is.getDropTrainerLevel(), attrs);
	}
	
	public static class PokeballAttrs {
		public ItemEffect effect;
		public float captureMulti;
		public float captureMultiEffect;
		public float itemEffectMod;
		
		public PokeballAttrs(PokeballAttributes src){
			this.effect = src.getItemEffect();
			this.captureMulti = src.getCaptureMulti();
			this.captureMultiEffect = src.getCaptureMultiEffect();
			this.itemEffectMod = src.getItemEffectMod();
		}
		
	}
	
	public static class PotionAttrs {
		public float staPct;
		public int staAmt;
		
		public PotionAttrs(PotionAttributes src){
			this.staPct = src.getStaPercent();
			this.staAmt = src.getStaAmount();
		}
	}
	
	public static class ReviveAttrs {
		public float staPct;
		
		public ReviveAttrs(ReviveAttributes src){
			this.staPct = src.getStaPercent();
		}
		
	}
	
	public static class BattleAttrs {
		public float staPct;
		
		public BattleAttrs(float sp){
			this.staPct = sp;
		}
	}
	
	public static class FoodAttrs {
		public ItemEffect[] effects;
		public float[] effectPct;
		public float growthPct;
		
		public FoodAttrs(FoodAttributes src){
			this.effects = src.getItemEffectList().toArray(new ItemEffect[src.getItemEffectCount()]);
			this.effectPct = Floats.toArray(src.getItemEffectPercentList());
			this.growthPct = src.getGrowthPercent();
		}
	}
	
	public static class InventoryUpgradeAttrs {
		public int additionalStorage;
		public InventoryUpgradeType type;
		
		public InventoryUpgradeAttrs(InventoryUpgradeAttributes src){
			this.additionalStorage = src.getAdditionalStorage();
			this.type = src.getUpgradeType();
		}
	}
	
	public static class ExpBoostAttrs {
		public float multiplier;
		public int durationMs;
		
		public ExpBoostAttrs(ExperienceBoostAttributes src){
			this.multiplier = src.getXpMultiplier();
			this.durationMs = src.getBoostDurationMs();
		}
	}
	
	public static class IncenseAttrs {
		public int lifetimeSeconds;
		public PokemonType[] pokemonTypes;
		public float prob;
		public int standingEncounterTime;
		public int movingEncounterTime;
		public int minShortDist;
		public int attractionLength;
		
		public IncenseAttrs(IncenseAttributes src){
			this.lifetimeSeconds = src.getIncenseLifetimeSeconds();
			this.pokemonTypes = src.getPokemonTypeList().toArray(new PokemonType[src.getPokemonTypeCount()]);
			this.prob = src.getPokemonIncenseTypeProbability();
			this.standingEncounterTime = src.getStandingTimeBetweenEncountersSeconds();
			this.movingEncounterTime = src.getMovingTimeBetweenEncounterSeconds();
			this.minShortDist = src.getDistanceRequiredForShorterIntervalMeters();
			this.attractionLength = src.getPokemonAttractedLengthSec();
		}
	}
	
	public static class EggIncubatorAttrs {
		public EggIncubatorType type;
		public int uses;
		public float distMultiplier;
		
		public EggIncubatorAttrs(EggIncubatorAttributes src){
			this.type = src.getIncubatorType();
			this.uses = src.getUses();
			this.distMultiplier = src.getDistanceMultiplier();
		}
	}
	
	public static class FortModifierAttrs {
		public int lifetimeSeconds;
		public int troyDiskNumPokemonSpawned;
		
		public FortModifierAttrs(int ls, int nps){
			this.lifetimeSeconds = ls;
			this.troyDiskNumPokemonSpawned = nps;
		}
		
	}
}
