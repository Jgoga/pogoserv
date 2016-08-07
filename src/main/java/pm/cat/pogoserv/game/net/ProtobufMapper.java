package pm.cat.pogoserv.game.net;

import com.google.common.geometry.S2LatLng;
import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;

import POGOProtos.Data.POGOProtosData.PokemonData;
import POGOProtos.Data.Capture.POGOProtosDataCapture.CaptureAward;
import POGOProtos.Inventory.Item.POGOProtosInventoryItem.ItemData;
import POGOProtos.Map.Pokemon.POGOProtosMapPokemon;
import POGOProtos.Map.Pokemon.POGOProtosMapPokemon.NearbyPokemon;
import POGOProtos.Map.Pokemon.POGOProtosMapPokemon.WildPokemon;
import POGOProtos.Networking.Envelopes.POGOProtosNetworkingEnvelopes;
import pm.cat.pogoserv.game.model.player.Award;
import pm.cat.pogoserv.game.model.player.InventoryPokemon;
import pm.cat.pogoserv.game.model.player.Item;
import pm.cat.pogoserv.game.model.player.Award.AwardEntry;
import pm.cat.pogoserv.game.model.pokemon.InstancedPokemon;
import pm.cat.pogoserv.game.model.pokemon.Pokemon;
import pm.cat.pogoserv.game.model.world.MapPokemon;
import pm.cat.pogoserv.game.session.AuthTicket;

public class ProtobufMapper {
	
	public static PokemonData.Builder pokemon(PokemonData.Builder dest, Pokemon p){
		return dest
			// ID is only for existing pokemon.
			.setPokemonIdValue(p.def.id)
			// cp is for instanced pokemon
			// stamina is for instanced pokemon
			// max stamina is for instanced pokemon
			.setMove1(p.move1)
			.setMove2(p.move2)
			// deployed_fort_id is only for inventory pokemon
			// owner_name is only for (I think) gym pokemon?
			// is_egg only for eggs
			// egg_km_walked_start only for eggs
			// egg_km_walked_end only for eggs
			// origin is for inventory pokemon (?)
			.setHeightM(p.height)
			.setWeightKg(p.weight)
			.setIndividualAttack(p.ivAtk)
			.setIndividualDefense(p.ivDef)
			.setIndividualStamina(p.ivSta)
			// cp multiplier is for instancedpokemon
			// pokeball is for inventory & gym pokemon
			// captured_cell_id is for inventory pokemon (?)
			// battles_attacked for inventory & gym pokemon
			// battles_defended for inventory & gym pokemon
			// egg_incubator_id for eggs
			// creation timestamp is for existing pokemon
			// num upgrades is for existing pokemon
			// additional cp multiplier is for instanced pokemon
			// favorite is for inventory pokemon
			// nickname is for inventory pokemon
			// from_fort is boolean, set for gym pokemon
			;
	}
	
	public static PokemonData.Builder instancedPokemon(PokemonData.Builder dest, InstancedPokemon p){
		return pokemon(dest, p)
			.setCp(p.getCP())
			.setStamina(p.getCurrentStamina())
			.setStaminaMax(p.getStamina())
			.setCpMultiplier(p.getCpMultiplier())
			.setAdditionalCpMultiplier(p.getAdditionalCpMultiplier());
	}
	
	public static PokemonData.Builder inventoryPokemon(PokemonData.Builder dest, InventoryPokemon p){
		instancedPokemon(dest, p)
			.setId(p.getUID())
			// TODO: deployed_fort_id
			.setPokeball(p.pokeball)
			.setCapturedCellId(p.capturedCellId)
			.setCreationTimeMs(p.creationTimestamp)
			.setNumUpgrades(p.numUpgrades)
			// TODO favorite
			// TODO nickname
			;
		if(p.battlesAttacked > 0)
			dest.setBattlesAttacked(p.battlesAttacked);
		if(p.battlesDefended > 0)
			dest.setBattlesDefended(p.battlesDefended);
		return dest;
	}
	
	public static ItemData.Builder item(ItemData.Builder dest, Item i){
		return dest.setItemIdValue(i.def.id).setCount(i.count);
	}
	
	public static WildPokemon.Builder wildPokemon(WildPokemon.Builder dest, MapPokemon mp){
		dest.setEncounterId(mp.getUID())
			// TODO: This usually seems to increase per request, but idk how it's determined
			.setLastModifiedTimestampMs(System.currentTimeMillis()) 
			.setLatitude(mp.getLatitude())
			.setLongitude(mp.getLongitude())
			.setSpawnPointId(mp.source.getUniqueStr())
			.setTimeTillHiddenMs((int) (mp.disappearTimestamp - System.currentTimeMillis()));
		
		return dest;
	}
	
	public static NearbyPokemon.Builder nearbyPokemon(NearbyPokemon.Builder dest, MapPokemon mp, S2LatLng pos){
		return dest
			.setPokemonIdValue(mp.pokemon.def.id)
			.setDistanceInMeters((float) pos.getEarthDistance(mp.s2LatLngPos()))
			.setEncounterId(mp.getUID());
	}
	
	public static POGOProtosMapPokemon.MapPokemon.Builder mapPokemon(POGOProtosMapPokemon.MapPokemon.Builder dest, MapPokemon mp){
		return dest
			.setSpawnPointId(mp.source.getUniqueStr())
			.setEncounterId(mp.getUID())
			.setPokemonIdValue(mp.pokemon.def.id)
			.setExpirationTimestampMs(mp.disappearTimestamp)
			.setLatitude(mp.getLatitude())
			.setLongitude(mp.getLongitude());
	}
	
	public static POGOProtosNetworkingEnvelopes.AuthTicket.Builder authTicket(POGOProtosNetworkingEnvelopes.AuthTicket.Builder dest, AuthTicket src){
		return dest
				.setStart(src.getStart())
				.setEnd(src.getEnd())
				.setExpireTimestampMs(src.getExpirationTimestamp());
	}
	
	public static CaptureAward.Builder captureAward(CaptureAward.Builder dest, Award a){
		for(AwardEntry e : a){
			dest.addActivityTypeValue(e.activity)
				.addXp(e.exp)
				.addCandy(e.candy)
				.addStardust(e.stardust);
		}
		return dest;
	}
	
}
