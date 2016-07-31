package pm.cat.pogoserv.game.request.impl;

import com.google.common.geometry.S2LatLng;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLiteOrBuilder;

import POGOProtos.Data.POGOProtosData.PokemonData;
import POGOProtos.Map.POGOProtosMap.MapCell;
import POGOProtos.Map.POGOProtosMap.MapObjectsStatus;
import POGOProtos.Map.Pokemon.POGOProtosMapPokemon.NearbyPokemon;
import POGOProtos.Map.Pokemon.POGOProtosMapPokemon.WildPokemon;
import POGOProtos.Networking.Requests.POGOProtosNetworkingRequests.Request;
import POGOProtos.Networking.Requests.Messages.POGOProtosNetworkingRequestsMessages.GetMapObjectsMessage;
import POGOProtos.Networking.Responses.POGOProtosNetworkingResponses.GetMapObjectsResponse;
import pm.cat.pogoserv.game.request.GameRequest;
import pm.cat.pogoserv.game.request.RequestHandler;
import pm.cat.pogoserv.game.world.MapObject;
import pm.cat.pogoserv.game.world.MapPokemon;
import pm.cat.pogoserv.game.world.SpawnPoint;
import pm.cat.pogoserv.game.world.WorldCell;

public class GetMapObjectsHandler implements RequestHandler {
	
	@Override
	public MessageLiteOrBuilder run(GameRequest req, Request r) throws InvalidProtocolBufferException {
		GetMapObjectsMessage gm = GetMapObjectsMessage.parseFrom(r.getRequestMessage());
		GetMapObjectsResponse.Builder resp = GetMapObjectsResponse.newBuilder();
		long ts = System.currentTimeMillis();
		S2LatLng playerPos = S2LatLng.fromDegrees(req.latitude, req.longitude);
		
		MapCell.Builder mc = MapCell.newBuilder();
		WildPokemon.Builder wp = WildPokemon.newBuilder();
		POGOProtos.Map.Pokemon.POGOProtosMapPokemon.MapPokemon.Builder mp = 
				POGOProtos.Map.Pokemon.POGOProtosMapPokemon.MapPokemon.newBuilder();
		PokemonData.Builder pd = PokemonData.newBuilder();
		NearbyPokemon.Builder np = NearbyPokemon.newBuilder();
		POGOProtos.Map.POGOProtosMap.SpawnPoint.Builder sp =
				POGOProtos.Map.POGOProtosMap.SpawnPoint.newBuilder();
		
		int cellCount = gm.getCellIdCount();
		System.out.println("Request has " + cellCount + " cells");
		// TODO if cellCount > THRESHOLD, quit
		for(int i=0;i<cellCount;i++){
			long id = gm.getCellId(i);
			mc.clear().setS2CellId(id).setCurrentTimestampMs(ts);
			
			WorldCell cell = req.game.world.getCell(id);
			System.out.println(id + " => " + cell);
			
			if(cell != null){
				for(MapObject o : cell.objects()){
					System.out.println("\t* " + o);
					double lat = o.getLatitude();
					double lng = o.getLongitude();
					
					if(o instanceof SpawnPoint){
						sp.clear()
							.setLatitude(lat)
							.setLongitude(lng);
						mc.addSpawnPoints(sp);
						/*
						if(teknoIsmo7) mc.addSpawnPoints(sp);
						else mc.addDecimatedSpawnPoints(sp);
						teknoIsmo7 = !teknoIsmo7;
						*/
					}else if(o instanceof MapPokemon){
						MapPokemon p = (MapPokemon) o;
						double dist = playerPos.getEarthDistance(S2LatLng.fromDegrees(lat, lng));
						// TODO: idk if these values are correct. Probably not
						if(dist < 50){
							mc.addCatchablePokemons(mp.clear()
								.setSpawnPointId(WorldCell.uidString(cell, p))
								.setEncounterId(p.getUid())
								.setExpirationTimestampMs(p.disappearTimestamp)
								.setLatitude(lat)
								.setLongitude(lng));
						}
						if(dist < 100){
							mc.addWildPokemons(wp.clear()
								.setLastModifiedTimestampMs(p.appearTimestamp)
								.setLatitude(lat)
								.setLongitude(lng)
								.setSpawnPointId(WorldCell.uidString(cell, p))
								.setPokemonData(pd.clear().setPokemonIdValue(p.pokemon.def.id))
								.setTimeTillHiddenMs((int) (p.disappearTimestamp - ts)));
								//.setTimeTillHiddenMs(5000));
							System.out.println("now=" + ts + ", disappear=" + p.disappearTimestamp + ", TTH=" + ((int)(p.disappearTimestamp-ts)));
						}
						if(dist < 500){
							mc.addNearbyPokemons(np.clear()
								.setPokemonIdValue(p.pokemon.def.id)
								.setDistanceInMeters((float) dist)
								.setEncounterId(p.getUid()));
						}
						
						System.out.println("poke: " + o + " | dist=" + dist);
					}
				}
			}
			
			resp.addMapCells(mc);
		}
		
		/*
		for(long cellId : gm.getCellIdList()){
			mc.clear();
			WorldCell cell = req.game.world.cell(new S2CellId(cellId));
			mc.setS2CellId(cellId);
			mc.setCurrentTimestampMs(ts);
			
			// TODO forts
			// TODO spawnpoints
			// TODO deleted_objects ???
			// TODO is_truncated_list ???
			// TODO fort_summaries ???
			// TODO decimated_spawn_points ???
			
			for(MapObject o : cell.objects()){
				double lat = o.getLatitude();
				double lng = o.getLongitude();
				
				if(o instanceof MapPokemon){
					MapPokemon p = (MapPokemon) o;
					S2LatLng pokePos = S2LatLng.fromDegrees(p., p.longitude);
					double dist = playerPos.getEarthDistance(pokePos);
					if(dist < 50){
						mp.clear();
						mp.setSpawnPointId("") // TODO
							.setEncounterId(p.getUid())
							.setPokemonIdValue(p.def.id)
							.setExpirationTimestampMs(p.disappearTimestamp)
							.setLatitude(p.latitude)
							.setLongitude(p.longitude);
						mc.addCatchablePokemons(mp);
					}else if(dist < 100){
						wp.clear();
						wp.setEncounterId(p.getUid())
							// TODO last_modified_timestamp_ms
							.setLatitude(p.latitude)
							.setLongitude(p.longitude)
							.setSpawnPointId("") // TODO
							.setPokemonData(pd.clear().setPokemonIdValue(p.def.id))
							.setTimeTillHiddenMs((int) (p.disappearTimestamp - ts));
						mc.addWildPokemons(wp);
					}else if(dist < 1000){
						np.clear();
						np.setPokemonIdValue(p.def.id)
							.setDistanceInMeters((float) dist)
							.setEncounterId(p.getUid());
						mc.addNearbyPokemons(np);
					}
					System.out.println("D=" + dist);
				}
			}
			
			resp.addMapCells(mc);
		}
			*/
		
		resp.setStatus(MapObjectsStatus.SUCCESS);
		return resp;
	}

}
