package pm.cat.pogoserv.game.net.request.impl;

import java.io.IOException;

import POGOProtos.Data.POGOProtosData.PokemonData;
import POGOProtos.Data.Player.POGOProtosDataPlayer.PlayerStats;
import POGOProtos.Inventory.POGOProtosInventory.InventoryDelta;
import POGOProtos.Inventory.POGOProtosInventory.InventoryItem;
import POGOProtos.Inventory.POGOProtosInventory.InventoryItemData;
import POGOProtos.Inventory.Item.POGOProtosInventoryItem.ItemData;
import POGOProtos.Networking.Envelopes.POGOProtosNetworkingEnvelopes.RequestEnvelope;
import POGOProtos.Networking.Requests.POGOProtosNetworkingRequests.Request;
import POGOProtos.Networking.Requests.Messages.POGOProtosNetworkingRequestsMessages.GetInventoryMessage;
import POGOProtos.Networking.Responses.POGOProtosNetworkingResponses.GetInventoryResponse;
import pm.cat.pogoserv.game.event.impl.GetInventoryEvent;
import pm.cat.pogoserv.game.model.player.InventoryPokemon;
import pm.cat.pogoserv.game.model.player.Item;
import pm.cat.pogoserv.game.model.player.PlayerInfo;
import pm.cat.pogoserv.game.net.ProtobufMapper;
import pm.cat.pogoserv.game.net.request.RequestMapper;
import pm.cat.pogoserv.util.TimestampVarPool;
import pm.cat.pogoserv.util.TimestampVarPool.TSNode;

public class GetInventoryHandler implements RequestMapper<GetInventoryEvent> {

	@Override
	public GetInventoryEvent parse(Request req, RequestEnvelope re) throws IOException {
		return new GetInventoryEvent(GetInventoryMessage.parseFrom(req.getRequestMessage()).getLastTimestampMs());
	}

	@Override
	public Object write(GetInventoryEvent re) throws IOException {
		long since = re.lastTimestamp;
		TimestampVarPool pool = re.inventory;
		
		GetInventoryResponse.Builder resp = GetInventoryResponse.newBuilder();
		if(pool == null)
			return resp.setSuccess(false);
		TSNode<?> cur = pool.getTail();
		if(cur == null)
			return null;
		
		InventoryDelta.Builder delta = InventoryDelta.newBuilder();
		InventoryItem.Builder item = InventoryItem.newBuilder();
		InventoryItemData.Builder idata = InventoryItemData.newBuilder();
		PlayerStats.Builder statsBuilder = null;
		
		
		for(;cur!=null&&cur.getTimestamp()>=since;cur=cur.getPrev()){
			Object ref = cur.read();
			
			// Pokemon added
			if(ref instanceof InventoryPokemon)
				idata.setPokemonData(ProtobufMapper.inventoryPokemon(PokemonData.newBuilder(), (InventoryPokemon) ref));
			else if(ref instanceof Item)
				idata.setItem(ProtobufMapper.item(ItemData.newBuilder(), (Item) ref));
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
		
		return resp
				.setSuccess(true)
				.setInventoryDelta(delta);
	}
	
	protected void addPlayerStat(PlayerStats.Builder destBuilder, PlayerInfo.Stat<?> s){
		//System.out.println("addPlayerStat: " + s.id + ": " + s.value);
		if(s.value != null)
			destBuilder.setField(PlayerStats.getDescriptor().findFieldByNumber(s.id), s.value);
	}
	
}
