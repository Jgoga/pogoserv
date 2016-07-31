package pm.cat.pogoserv.game.request.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLiteOrBuilder;

import POGOProtos.Networking.Requests.POGOProtosNetworkingRequests.Request;
import POGOProtos.Networking.Requests.Messages.POGOProtosNetworkingRequestsMessages.EncounterMessage;
import pm.cat.pogoserv.game.request.GameRequest;
import pm.cat.pogoserv.game.request.RequestHandler;
import pm.cat.pogoserv.game.world.MapObject;
import pm.cat.pogoserv.game.world.WorldCell;

public class EncounterHandler implements RequestHandler {

	@Override
	public MessageLiteOrBuilder run(GameRequest req, Request r) throws InvalidProtocolBufferException {
		EncounterMessage m = EncounterMessage.parseFrom(r.getRequestMessage());
		long[] l = WorldCell.parseUid(m.getSpawnPointId());
		WorldCell cell = req.game.world.getCell(l[0]);
		
		if(cell == null){
			// Does not exist
		}
		
		MapObject spawn = cell.get(l[1]);
		if(spawn == null){
			// Does not exist
		}
		
		// TODO: player should remember used spawns!
		
		return null;
	}

}
