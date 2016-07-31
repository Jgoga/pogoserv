package pm.cat.pogoserv.game.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.InvalidProtocolBufferException;

import POGOProtos.Data.POGOProtosData.AssetDigestEntry;
import POGOProtos.Enums.POGOProtosEnums.PokemonMove;
import POGOProtos.Networking.Responses.POGOProtosNetworkingResponses.DownloadItemTemplatesResponse;
import POGOProtos.Networking.Responses.POGOProtosNetworkingResponses.DownloadItemTemplatesResponse.ItemTemplate;
import POGOProtos.Networking.Responses.POGOProtosNetworkingResponses.DownloadSettingsResponse;
import POGOProtos.Networking.Responses.POGOProtosNetworkingResponses.GetAssetDigestResponse;
import POGOProtos.Settings.POGOProtosSettings.FortSettings;
import POGOProtos.Settings.POGOProtosSettings.GlobalSettings;
import POGOProtos.Settings.POGOProtosSettings.InventorySettings;
import POGOProtos.Settings.POGOProtosSettings.LevelSettings;
import POGOProtos.Settings.POGOProtosSettings.MapSettings;
import POGOProtos.Settings.Master.POGOProtosSettingsMaster.EncounterSettings;
import POGOProtos.Settings.Master.POGOProtosSettingsMaster.GymBattleSettings;
import POGOProtos.Settings.Master.POGOProtosSettingsMaster.GymLevelSettings;
import POGOProtos.Settings.Master.POGOProtosSettingsMaster.ItemSettings;
import POGOProtos.Settings.Master.POGOProtosSettingsMaster.MoveSettings;
import POGOProtos.Settings.Master.POGOProtosSettingsMaster.PlayerLevelSettings;
import POGOProtos.Settings.Master.POGOProtosSettingsMaster.PokemonSettings;
import POGOProtos.Settings.Master.POGOProtosSettingsMaster.PokemonUpgradeSettings;
import POGOProtos.Settings.Master.POGOProtosSettingsMaster.TypeEffectiveSettings;
import POGOProtos.Settings.Master.Pokemon.POGOProtosSettingsMasterPokemon.EncounterAttributes;
import POGOProtos.Settings.Master.Pokemon.POGOProtosSettingsMasterPokemon.StatsAttributes;
import pm.cat.pogoserv.Log;
import pm.cat.pogoserv.core.Constants;
import pm.cat.pogoserv.util.Util;

public class GameSettings {
	
	// All ranges are meters
	
	//
	// Server settings
	//
	public String assetHostPrefix;
	public String dataPath;
	// Time to keep players cached (in minutes)
	public int playerCacheTime = 10;
	
	//
	// (unofficial) game settings
	//
	public double spawnOffsetStdDev = 0.0001;
	
	//
	// GAME_MASTER settings
	//
	
	/* Gym settings */
	public int[] gymRequiredExp;
	public int[] gymLeaderSlots;
	public int[] gymTrainerSlots;
	
	/* Battle settings */
	public float battleEnergyPerSec;
	public float battleDodgeEnergyCost;
	public float battleRetargetSeconds;
	public float battleEnemyAttackInterval;
	public float battleAttackServerInterval;
	public float battleRoundDurationSeconds;
	public float battleBonusTimePerAllySeconds;
	public int battleMaxAttackersPerBattle;
	public float battleSTAB;
	public int battleMaxEnergy;
	public float battleEnergyDeltaPerHpLost;
	public int battleDodgeDurationMs;
	public int battleMinimumPlayerLevel;
	public int battleSwapDurationMs;
	
	/* Encounter settings */
	public float spinBonusThreshold;
	public float excellentThrowThreshold;
	public float greatThrowThreshold;
	public float niceThrowThreshold;
	public int milestoneThreshold;
	
	/* Item settings */
	private final Map<Integer, ItemDef> itemSettings = new HashMap<>();
	
	/* Player level settings */
	public int[] playerRankNum;
	public int[] playerRequiredExp;
	public float[] playerCpMultiplier;
	public int playerMaxEggLevel;
	public int playerMaxEncounterLevel;
	
	/* Type settings (0 = none) */
	public final float[][] typeEffectiveness = new float[Constants.NUM_TYPES+1][Constants.NUM_TYPES+1];
	
	/* Evolution ("upgrade") settings */
	public int upgradesPerLevel;
	public int upgradeAllowedLevelsAbovePlayer;
	public int[] upgradeCandyCost;
	public int[] upgradeStardustCost;
	
	/* Pokemon settings */
	private final ArrayList<PokemonDef> pokemon = new ArrayList<>();
	
	/* Move settings */
	private final Map<Integer, MoveDef> moves = new HashMap<>();
	
	/* Asset definitions */
	private final Map<String, AssetDef> assets = new HashMap<>();
	
	//
	// Client settings
	//
	
	public String clientSettingsHash;
	
	/* Fort settings */
	public double fortInteractionRange;
	public int fortMaxTotalDeployedPokemon;
	public int fortMaxPlayerDeployedPokemon;
	public double fortDeployStaMultiplier;
	public double fortDeployAtkMultiplier;
	public double fortFarInterctionRange;
	
	/* Map settings */
	public double mapPokemonVisibleRange;
	public double mapPokeNavRange;
	public double mapEncounterRange;
	public float mapGetMapObjectsMinRefreshSeconds;
	public float mapGetMapObjectsMaxRefreshSeconds;
	public float mapGetMapObjectsMinDistance;
	public String mapGoogleMapsApiKey;
	
	/* Level settings */
	public double trainerCpModifier;
	public double trainerDifficultyModifier;
	
	/* Inventory settings */
	public int invMaxPokemon;
	public int invMaxBagItems;
	public int invBasePokemon;
	public int invBaseBagItems;
	public int invBaseEggs;
	
	/* Other settings */
	public String minClientVersion;
	
	public void parseAll() throws InvalidProtocolBufferException, IOException {
		File dir = new File(dataPath);
		for(File f : dir.listFiles()){
			String name = f.getName();
			
			if(name.equals("GAME_MASTER.protobuf"))
				parseGameMaster(Util.readFile(f));
			else if(name.equals("CLIENT_SETTINGS.protobuf"))
				parseClientSettings(Util.readFile(f));
			else if(name.startsWith("ASSET_DIGEST")){
				String[] s = name.split("\\.");
				int plat = Integer.parseInt(s[2]);
				parseAssetDigests(plat, Util.readFile(f));
			}
		}
	}
	
	public void parseGameMaster(byte[] in) throws InvalidProtocolBufferException {
		itemSettings.clear();
		pokemon.clear();
		moves.clear();
		
		Log.d("Settings", "Parsing game master file %d bytes", in.length);
		DownloadItemTemplatesResponse gm = DownloadItemTemplatesResponse.parseFrom(in);
		int num = gm.getItemTemplatesCount();
		for(int i=0;i<num;i++){
			ItemTemplate it = gm.getItemTemplates(i);
			String id = it.getTemplateId();
			
			if(id.equals("GYM_LEVEL_SETTINGS")){
				GymLevelSettings gls = it.getGymLevel();
				gymRequiredExp = Util.toIntArray(gls.getRequiredExperienceList());
				gymLeaderSlots = Util.toIntArray(gls.getRequiredExperienceList());
				gymTrainerSlots = Util.toIntArray(gls.getTrainerSlotsList());
				Log.d("Settings", "Loaded gym settings: %d required exps, %d leader slots, %d trainer slots",
						gymRequiredExp.length, gymLeaderSlots.length, gymTrainerSlots.length);
			}
			
			if(id.equals("BATTLE_SETTINGS")){
				GymBattleSettings gbs = it.getBattleSettings();
				battleEnergyPerSec = gbs.getEnergyPerSec();
				battleDodgeEnergyCost = gbs.getDodgeEnergyCost();
				battleRetargetSeconds = gbs.getRetargetSeconds();
				battleEnemyAttackInterval = gbs.getEnemyAttackInterval();
				battleAttackServerInterval = gbs.getAttackServerInterval();
				battleRoundDurationSeconds = gbs.getRoundDurationSeconds();
				battleBonusTimePerAllySeconds = gbs.getBonusTimePerAllySeconds();
				battleMaxAttackersPerBattle = gbs.getMaximumAttackersPerBattle();
				battleMaxEnergy = gbs.getMaximumEnergy();
				battleEnergyDeltaPerHpLost = gbs.getEnergyDeltaPerHealthLost();
				battleDodgeDurationMs = gbs.getDodgeDurationMs();
				battleMinimumPlayerLevel = gbs.getMinimumPlayerLevel();
				battleSwapDurationMs = gbs.getSwapDurationMs();
				Log.d("Settings", "Loaded battle settings");
			}
			
			if(id.equals("ENCOUNTER_SETTINGS")){
				EncounterSettings es = it.getEncounterSettings();
				spinBonusThreshold = es.getSpinBonusThreshold();
				excellentThrowThreshold = es.getExcellentThrowThreshold();
				greatThrowThreshold = es.getGreatThrowThreshold();
				niceThrowThreshold = es.getNiceThrowThreshold();
				milestoneThreshold = es.getMilestoneThreshold();
				Log.d("Settings", "Loaded encounter settings");
			}
			
			if(id.startsWith("ITEM_")){
				ItemSettings is = it.getItemSettings();
				ItemDef ic = ItemDef.parse(id, it.getItemSettings());
				if(ic != null){
					Log.d("Settings", "Loaded item %s", id);
					itemSettings.put(is.getItemIdValue(), ic);
				}
			}
			
			if(id.equals("PLAYER_LEVEL_SETTINGS")){
				PlayerLevelSettings ls = it.getPlayerLevel();
				playerRankNum = Util.toIntArray(ls.getRankNumList());
				playerRequiredExp = Util.toIntArray(ls.getRequiredExperienceList());
				playerCpMultiplier = Util.toFloatArray(ls.getCpMultiplierList());
				playerMaxEggLevel = ls.getMaxEggPlayerLevel();
				playerMaxEncounterLevel = ls.getMaxEncounterPlayerLevel();
				Log.d("Settings", "Loaded player level settings");
			}
			
			if(id.startsWith("POKEMON_TYPE")){
				TypeEffectiveSettings te = it.getTypeEffective();
				int type = te.getAttackTypeValue();
				typeEffectiveness[type][0] = 1; // Type --> None
				for(int j=0;j<Constants.NUM_TYPES;j++){
					typeEffectiveness[type][j+1] = te.getAttackScalar(j);
				}
				Log.d("Settings", "Loaded type %s, attack scalar: %s", id, Arrays.toString(typeEffectiveness[type]));
			}
			
			if(id.equals("POKEMON_UPGRADE_SETTINGS")){
				PokemonUpgradeSettings us = it.getPokemonUpgrades();
				upgradesPerLevel = us.getUpgradesPerLevel();
				upgradeAllowedLevelsAbovePlayer = us.getAllowedLevelsAbovePlayer();
				upgradeCandyCost = Util.toIntArray(us.getCandyCostList());
				upgradeStardustCost = Util.toIntArray(us.getStardustCostList());
				Log.d("Settings", "Loaded upgrade settings (max level: %d)", upgradeCandyCost.length);
			}
			
			String idss = id.substring("V0000_".length());
			if(idss.startsWith("POKEMON_")){
				PokemonSettings ps = it.getPokemonSettings();
				PokemonDef p = new PokemonDef(ps.getPokemonId());
				p.type1 = ps.getTypeValue();
				p.type2 = ps.getType2Value();
				EncounterAttributes ea = ps.getEncounter();
				p.baseCaptureRate = ea.getBaseCaptureRate();
				p.baseFleeRate = ea.getBaseFleeRate();
				p.collisionRadius = ea.getCollisionHeadRadiusM();
				p.collisionHeight = ea.getCollisionHeightM();
				p.collisionHeadRadius = ea.getCollisionHeadRadiusM();
				p.movementType = ea.getMovementType();
				p.jumpTime = ea.getJumpTimeS();
				p.attackTime = ea.getAttackTimerS();
				StatsAttributes s = ps.getStats();
				p.baseSta = s.getBaseStamina();
				p.baseAtk = s.getBaseAttack();
				p.baseDef = s.getBaseDefense();
				p.dodgeEnergyDelta = s.getDodgeEnergyDelta();
				p.quickMoves = ps.getQuickMovesList().toArray(new PokemonMove[ps.getQuickMovesCount()]);
				p.chargeMoves = ps.getCinematicMovesList().toArray(new PokemonMove[ps.getCinematicMovesCount()]);
				p.animationTime = Util.toFloatArray(ps.getAnimationTimeList());
				p.evolutionIds = new int[ps.getEvolutionIdsCount()];
				for(int j=0;j<p.evolutionIds.length;j++)
					p.evolutionIds[j] = ps.getEvolutionIdsValue(j);
				p.evolutionPips = ps.getEvolutionPips();
				p.rarity = ps.getRarity();
				p.pokedexHeight = ps.getPokedexHeightM();
				p.pokedexWeight = ps.getPokedexWeightKg();
				p.parentId = ps.getParentPokemonIdValue();
				p.heightStdDev = ps.getHeightStdDev();
				p.weightStdDev = ps.getWeightStdDev();
				p.kmDistanceToHatch = ps.getKmDistanceToHatch();
				p.familyId = ps.getFamilyId();
				p.candyToEvolve = ps.getCandyToEvolve();
				
				//pokemon.ensureCapacity(p.id+1);
				//pokemon.set(p.id, p);
				Util.randomAccessSet(pokemon, p.id, p);
				Log.d("Settings", "Loaded pokemon #%d: %s", p.id, p.name);
			}
			
			if(idss.startsWith("MOVE_")){
				MoveSettings ms = it.getMoveSettings();
				String name = Util.formatString(idss.substring("MOVE_".length()));
				int moveid = Integer.parseInt(id.substring(1, 5));
				MoveDef m = new MoveDef(ms.getMovementId());
				//m.moveId = ms.getMovementId();
				m.type = ms.getPokemonTypeValue();
				m.power = ms.getPower();
				m.accuracy = ms.getAccuracyChance();
				m.criticalChance = ms.getCriticalChance();
				m.heal = ms.getHealScalar();
				m.staLoss = ms.getStaminaLossScalar();
				m.trainerLevelMin = ms.getTrainerLevelMin();
				m.trainerLevelMax = ms.getTrainerLevelMax();
				m.durationMs = ms.getDurationMs();
				m.damageWindowStartMs = ms.getDamageWindowStartMs();
				m.damageWindowEndMs = ms.getDamageWindowEndMs();
				m.energyDelta = ms.getEnergyDelta();
				
				Log.d("Settings", "Loaded move #%d: %s (movement=%s)", moveid, name, ms.getMovementId().toString());
			}
			
		}
		
		for(int i=0;i<Constants.NUM_TYPES+1;i++)
			typeEffectiveness[0][i] = 0;
		
		Log.d("Settings", "Parsed game master!");
	}
	
	public void parseAssetDigests(int platform, byte[] in) throws InvalidProtocolBufferException {
		int oldsize = assets.size();
		GetAssetDigestResponse adr = GetAssetDigestResponse.parseFrom(in);
		for(AssetDigestEntry e : adr.getDigestList()){
			AssetDef ad = new AssetDef(e.getAssetId());
			ad.bundleName = e.getBundleName();
			ad.checksum = e.getChecksum();
			ad.size = e.getSize();
			ad.version = e.getVersion();
			ad.platform = platform;
			ad.key = e.getKey().toByteArray();
			Log.d("Settings", "%s", ad.toString());
			if(ad.checksum == 0)
				Log.e("Settings", "%s: Missing checksum!!", ad.id);
			assets.put(ad.id, ad);
		}
		Log.d("Settings", "Parsed %d assets (platform: %d)", assets.size() - oldsize, platform);
	}
	
	public void parseClientSettings(byte[] in) throws InvalidProtocolBufferException {
		DownloadSettingsResponse s = DownloadSettingsResponse.parseFrom(in);
		clientSettingsHash = s.getHash();
		
		GlobalSettings gs = s.getSettings();
		
		FortSettings fs = gs.getFortSettings();
		fortInteractionRange = fs.getInteractionRangeMeters();
		fortMaxTotalDeployedPokemon = fs.getMaxTotalDeployedPokemon();
		fortMaxPlayerDeployedPokemon = fs.getMaxPlayerDeployedPokemon();
		fortDeployStaMultiplier = fs.getDeployStaminaMultiplier();
		fortDeployAtkMultiplier = fs.getDeployAttackMultiplier();
		fortFarInterctionRange = fs.getFarInteractionRangeMeters();
		
		MapSettings ms = gs.getMapSettings();
		mapPokemonVisibleRange = ms.getPokemonVisibleRange();
		mapPokeNavRange = ms.getPokeNavRangeMeters();
		mapEncounterRange = ms.getEncounterRangeMeters();
		mapGetMapObjectsMinRefreshSeconds = ms.getGetMapObjectsMinRefreshSeconds();
		mapGetMapObjectsMaxRefreshSeconds = ms.getGetMapObjectsMaxRefreshSeconds();
		mapGetMapObjectsMinDistance = ms.getGetMapObjectsMinDistanceMeters();
		mapGoogleMapsApiKey = ms.getGoogleMapsApiKey();
		
		LevelSettings ls = gs.getLevelSettings();
		trainerCpModifier = ls.getTrainerCpModifier();
		trainerDifficultyModifier = ls.getTrainerDifficultyModifier();
		
		InventorySettings is = gs.getInventorySettings();
		invMaxPokemon = is.getMaxPokemon();
		invMaxBagItems = is.getMaxBagItems();
		invBasePokemon = is.getBasePokemon();
		invBaseBagItems = is.getBaseBagItems();
		invBaseEggs = is.getBaseEggs();
		
		minClientVersion = gs.getMinimumClientVersion();
		
		Log.d("Settings", "Parsed client settings");
	}
	
	public PokemonDef getPokemon(int id){
		return pokemon.get(id);
	}
	
	public ItemDef getItem(int id){
		return itemSettings.get(id);
	}
	
	public MoveDef getMove(int id){
		return moves.get(id);
	}
	
	public int maxLevel(){
		return playerRequiredExp.length - 1;
	}
	
	public Iterable<AssetDef> getAssets(){
		return assets.values();
	}
	
	public AssetDef getAsset(String id){
		return assets.get(id);
	}
	
}
