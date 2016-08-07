package pm.cat.pogoserv.game.net.request.impl;

import java.io.IOException;

import com.google.common.geometry.S2LatLng;

import POGOProtos.Data.POGOProtosData.PokemonData;
import POGOProtos.Map.POGOProtosMap.MapCell;
import POGOProtos.Map.POGOProtosMap.MapObjectsStatus;
import POGOProtos.Map.Pokemon.POGOProtosMapPokemon.NearbyPokemon;
import POGOProtos.Map.Pokemon.POGOProtosMapPokemon.WildPokemon;
import POGOProtos.Networking.Envelopes.POGOProtosNetworkingEnvelopes.RequestEnvelope;
import POGOProtos.Networking.Requests.POGOProtosNetworkingRequests.Request;
import POGOProtos.Networking.Requests.Messages.POGOProtosNetworkingRequestsMessages.GetMapObjectsMessage;
import POGOProtos.Networking.Responses.POGOProtosNetworkingResponses.GetMapObjectsResponse;
import pm.cat.pogoserv.game.event.impl.GetMapObjectsEvent;
import pm.cat.pogoserv.game.event.impl.GetMapObjectsEvent.CellInfo;
import pm.cat.pogoserv.game.event.impl.GetMapObjectsEvent.MapObjectInfo;
import pm.cat.pogoserv.game.model.world.MapPokemon;
import pm.cat.pogoserv.game.model.world.UniqueLocatable;
import pm.cat.pogoserv.game.net.ProtobufMapper;
import pm.cat.pogoserv.game.net.request.RequestMapper;

public class GetMapObjectsHandler implements RequestMapper<GetMapObjectsEvent> {
	
	@Override
	public GetMapObjectsEvent parse(Request req, RequestEnvelope re) throws IOException {
		GetMapObjectsMessage m = GetMapObjectsMessage.parseFrom(req.getRequestMessage());
		GetMapObjectsEvent ret = new GetMapObjectsEvent(m.getCellIdCount());
		for(int i=0;i<ret.size();i++){
			ret.cellIds[i] = m.getCellId(i);
			ret.sinceTs[i] = m.getSinceTimestampMs(i);
		}
		
		return ret;
	}

	@Override
	public Object write(GetMapObjectsEvent re) throws IOException {
		GetMapObjectsResponse.Builder resp = GetMapObjectsResponse.newBuilder();
		if(re.status != MapObjectsStatus.SUCCESS)
			return resp.setStatus(re.status);
		
		long ts = System.currentTimeMillis();
		S2LatLng pos = re.getPlayer().s2LatLngPos();
		MapCell.Builder mc = MapCell.newBuilder();
		WildPokemon.Builder wp = WildPokemon.newBuilder();
		POGOProtos.Map.Pokemon.POGOProtosMapPokemon.MapPokemon.Builder mp = 
				POGOProtos.Map.Pokemon.POGOProtosMapPokemon.MapPokemon.newBuilder();
		NearbyPokemon.Builder np = NearbyPokemon.newBuilder();
		POGOProtos.Map.POGOProtosMap.SpawnPoint.Builder sp =
				POGOProtos.Map.POGOProtosMap.SpawnPoint.newBuilder();
		
		for(int i=0;i<re.size();i++){
			CellInfo ci = re.cells[i];
			mc.clear().setS2CellId(re.cellIds[i]);
			
			if(ci != null){
				mc.setCurrentTimestampMs(ci.ts);
				while(ci.objs.hasNext()){
					MapObjectInfo m = ci.objs.next();
					UniqueLocatable u = m.obj;
					if(m.type == MapObjectInfo.SPAWNPOINT){
						sp.clear().setLatitude(u.getLatitude()).setLongitude(u.getLongitude());
						mc.addSpawnPoints(sp);
					}else if((m.type & MapObjectInfo.POKEMON) != 0){
						MapPokemon p = (MapPokemon) u;
						if((m.type & MapObjectInfo.CATCHABLE_POKEMON) != 0)
							mc.addCatchablePokemons(ProtobufMapper.mapPokemon(mp, p));
						if((m.type & MapObjectInfo.WILD_POKEMON) != 0)
							mc.addWildPokemons(ProtobufMapper.wildPokemon(wp, p)
									.setPokemonData(PokemonData.newBuilder().setPokemonIdValue(p.pokemon.def.id)));
						if((m.type & MapObjectInfo.NEARBY_POKEMON) != 0)
							mc.addNearbyPokemons(ProtobufMapper.nearbyPokemon(np, p, pos));
					}
				}
				
			}else{
				mc.setCurrentTimestampMs(ts);
			}
			
			resp.addMapCells(mc);
		}
		
		return resp.setStatus(MapObjectsStatus.SUCCESS);
	}

}
