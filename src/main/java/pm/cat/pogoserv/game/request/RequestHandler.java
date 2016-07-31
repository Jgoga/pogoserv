package pm.cat.pogoserv.game.request;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLiteOrBuilder;

import POGOProtos.Networking.Requests.POGOProtosNetworkingRequests.Request;

public interface RequestHandler {
	
	MessageLiteOrBuilder run(GameRequest req, Request r) throws InvalidProtocolBufferException;
	
}
