package pm.cat.pogoserv.game.request.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLiteOrBuilder;

import POGOProtos.Data.POGOProtosData.PokemonData;
import POGOProtos.Data.Player.POGOProtosDataPlayer.PlayerStats;
import POGOProtos.Inventory.POGOProtosInventory.InventoryDelta;
import POGOProtos.Inventory.POGOProtosInventory.InventoryItem;
import POGOProtos.Inventory.POGOProtosInventory.InventoryItemData;
import POGOProtos.Inventory.Item.POGOProtosInventoryItem.ItemData;
import POGOProtos.Networking.Requests.POGOProtosNetworkingRequests.Request;
import POGOProtos.Networking.Requests.Messages.POGOProtosNetworkingRequestsMessages.GetInventoryMessage;
import POGOProtos.Networking.Responses.POGOProtosNetworkingResponses.GetInventoryResponse;
import pm.cat.pogoserv.game.player.InventoryPokemon;
import pm.cat.pogoserv.game.player.Item;
import pm.cat.pogoserv.game.player.PlayerInfo;
import pm.cat.pogoserv.game.request.GameRequest;
import pm.cat.pogoserv.game.request.RequestHandler;
import pm.cat.pogoserv.util.TimestampVarPool;
import pm.cat.pogoserv.util.TimestampVarPool.TSNode;

public class GetInventoryHandler implements RequestHandler {
	
	@Override
	public MessageLiteOrBuilder run(GameRequest req, Request r) throws InvalidProtocolBufferException {
		GetInventoryMessage gim = GetInventoryMessage.parseFrom(r.getRequestMessage());
		long since = gim.getLastTimestampMs();
		InventoryDelta.Builder delta = InventoryDelta.newBuilder();
		TimestampVarPool pool = req.player.getPool();
		InventoryItem.Builder item = InventoryItem.newBuilder();
		InventoryItemData.Builder idata = InventoryItemData.newBuilder();
		PlayerStats.Builder statsBuilder = null;
		
		TSNode<?> cur = pool.getTail();
		if(cur == null)
			return null;
		
		for(;cur!=null&&cur.getTimestamp()>=since;cur=cur.getPrev()){
			Object ref = cur.read();
			
			// Pokemon added
			if(ref instanceof InventoryPokemon)
				idata.setPokemonData(getPokemonData((InventoryPokemon) ref));
			else if(ref instanceof Item)
				idata.setItem(getItemData((Item) ref));
			// TODO PokedexEntry
			else if(ref instanceof PlayerInfo.Stat<?>){
				if(statsBuilder == null){
					statsBuilder = PlayerStats.newBuilder();
				}
				addPlayerStat(statsBuilder, (PlayerInfo.Stat<?>) ref);
			}
				
			// TODO PlayerCurrency
			// TODO PlayerCamera
			// TODO InventoryUpgrades
			// TODO AppliedItems
			// TODO EggIncubators
			// TODO Candy
			else
				continue;
			
			// TODO: joissakin on modified_timestamp_ms ja deleted_item_key
			//       Pitää selvittää tarkemmin missä
			item.setModifiedTimestampMs(cur.getTimestamp());
			delta.addInventoryItems(item.setInventoryItemData(idata));
			
			item.clear();
			idata.clear();
		}
		
		if(statsBuilder != null){
			idata.setPlayerStats(statsBuilder);
			item.setModifiedTimestampMs(pool.getTail().getTimestamp()); // Not correct but (hopefully) doesn't matter
			item.setInventoryItemData(idata);
			delta.addInventoryItems(item);
		}
		
		delta.setOriginalTimestampMs(since);
		delta.setNewTimestampMs(pool.getTail().getTimestamp() + 1);
		
		return GetInventoryResponse.newBuilder()
			.setSuccess(true)
			.setInventoryDelta(delta);
	}
	
	protected void addPlayerStat(PlayerStats.Builder destBuilder, PlayerInfo.Stat<?> s){
		//System.out.println("addPlayerStat: " + s.id + ": " + s.value);
		if(s.value != null)
			destBuilder.setField(PlayerStats.getDescriptor().findFieldByNumber(s.id), s.value);
	}
	
	protected ItemData.Builder getItemData(Item i){
		return ItemData.newBuilder()
			.setItemIdValue(i.def.id)
			.setCount(i.count);
			// TODO bool unseen
	}
	
	protected PokemonData.Builder getPokemonData(InventoryPokemon p){
		PokemonData.Builder ret = PokemonData.newBuilder();
		ret.setId(p.uid)
			.setPokemonIdValue(p.def.id)
			.setCp(p.cp)
			.setStamina(p.stamina)
			.setStaminaMax(p.maxStamina)
			.setMove1(p.move1)
			.setMove2(p.move2)
			// deployed_fort_id
			// owner_name
			// is_egg
			// egg -> egg_km_walked_target
			// egg -> egg_km_walked_start
			// origin
			.setHeightM(p.height)
			.setWeightKg(p.weight)
			.setIndividualAttack(p.ivAtk)
			.setIndividualDefense(p.ivDef)
			.setIndividualStamina(p.ivSta)
			.setCpMultiplier(p.cpMultiplier)
			.setPokeball(p.pokeball)
			.setCapturedCellId(p.capturedCellId)
			.setBattlesAttacked(p.battlesAttacked)
			.setBattlesDefended(p.battlesDefended)
			// egg -> egg_incubator_id
			.setCreationTimeMs(p.creationTimestamp)
			.setNumUpgrades(p.numUpgrades)
			// TODO: additional_cp_multiplier
			// TODO: favorite
			// TODO: nickname
			// TODO: from_fort
			;
		return ret;
	}
	
}
