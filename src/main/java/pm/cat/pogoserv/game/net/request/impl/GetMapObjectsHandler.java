package pm.cat.pogoserv.game.net.request.impl;

import com.google.common.geometry.S2LatLng;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLiteOrBuilder;

import POGOProtos.Map.POGOProtosMap.MapCell;
import POGOProtos.Map.POGOProtosMap.MapObjectsStatus;
import POGOProtos.Map.Pokemon.POGOProtosMapPokemon.NearbyPokemon;
import POGOProtos.Map.Pokemon.POGOProtosMapPokemon.WildPokemon;
import POGOProtos.Networking.Requests.POGOProtosNetworkingRequests.Request;
import POGOProtos.Networking.Requests.Messages.POGOProtosNetworkingRequestsMessages.GetMapObjectsMessage;
import POGOProtos.Networking.Responses.POGOProtosNetworkingResponses.GetMapObjectsResponse;
import pm.cat.pogoserv.Log;
import pm.cat.pogoserv.game.config.GameSettings;
import pm.cat.pogoserv.game.model.world.MapObject;
import pm.cat.pogoserv.game.model.world.MapPokemon;
import pm.cat.pogoserv.game.model.world.SpawnPoint;
import pm.cat.pogoserv.game.model.world.WorldCell;
import pm.cat.pogoserv.game.net.ProtobufMapper;
import pm.cat.pogoserv.game.net.request.GameRequest;
import pm.cat.pogoserv.game.net.request.RequestHandler;

public class GetMapObjectsHandler implements RequestHandler {
	
	@Override
	public MessageLiteOrBuilder run(GameRequest req, Request r) throws InvalidProtocolBufferException {
		GetMapObjectsMessage gm = GetMapObjectsMessage.parseFrom(r.getRequestMessage());
		GetMapObjectsResponse.Builder resp = GetMapObjectsResponse.newBuilder();
		long ts = System.currentTimeMillis();
		S2LatLng playerPos = req.player.s2LatLngPos();
		
		MapCell.Builder mc = MapCell.newBuilder();
		WildPokemon.Builder wp = WildPokemon.newBuilder();
		POGOProtos.Map.Pokemon.POGOProtosMapPokemon.MapPokemon.Builder mp = 
				POGOProtos.Map.Pokemon.POGOProtosMapPokemon.MapPokemon.newBuilder();
		NearbyPokemon.Builder np = NearbyPokemon.newBuilder();
		POGOProtos.Map.POGOProtosMap.SpawnPoint.Builder sp =
				POGOProtos.Map.POGOProtosMap.SpawnPoint.newBuilder();
		
		int cellCount = gm.getCellIdCount();
		GameSettings settings = req.game.settings;
		//System.out.println("Request has " + cellCount + " cells");
		// TODO if cellCount > THRESHOLD, quit
		for(int i=0;i<cellCount;i++){
			long id = gm.getCellId(i);
			mc.clear().setS2CellId(id).setCurrentTimestampMs(ts);
			
			WorldCell cell = req.game.world.getCell(id);
			//System.out.println(id + " => " + cell);
			
			if(cell != null){
				for(MapObject o : cell.objects()){
					//System.out.println("\t* " + o);
					double lat = o.getLatitude();
					double lng = o.getLongitude();
					
					if(o instanceof SpawnPoint){
						sp.clear()
							.setLatitude(lat)
							.setLongitude(lng);
						
						// Official servers don't seem to send all spawn points
						// TODO: When to send spawnpoints?
						
						//if(((SpawnPoint)o).hasPokemon())
						//	mc.addSpawnPoints(sp);
						
						// TODO: What is a decimated spawn point ???
						//       Official server never seems to send them, maybe it's unused
						
						//else
						//	mc.addDecimatedSpawnPoints(sp);
					}else if(o instanceof MapPokemon){
						MapPokemon p = (MapPokemon) o;
						double dist = p.distanceTo(playerPos);
						
						if(req.player.hasEncountered(p.getUID()))
							continue;
						
						// XXX: pokemon has timed out but not cleaned up yet
						//      (This should not happen)
						if(p.disappearTimestamp <= ts){
							Log.w("MapUpdate", "Found zombie: " + p);
							continue;
						}
						
						if(dist < settings.mapEncounterRange)
							mc.addCatchablePokemons(ProtobufMapper.mapPokemon(mp, p));
						
						if(dist < settings.mapPokemonVisibleRange)
							mc.addWildPokemons(ProtobufMapper.wildPokemon(wp, p, false));
							
						if(dist < settings.mapPokeNavRange)
							mc.addNearbyPokemons(ProtobufMapper.nearbyPokemon(np, p, playerPos));
						
						//System.out.println("poke: " + o + " | dist=" + dist);
					}
				}
			}
			
			resp.addMapCells(mc);
		}
		
		resp.setStatus(MapObjectsStatus.SUCCESS);
		return resp;
	}

}
