package pm.cat.pogoserv.game.net;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

import POGOProtos.Networking.Envelopes.POGOProtosNetworkingEnvelopes.RequestEnvelope;
import pm.cat.pogoserv.game.model.player.Player;

public class GameRequest extends POGORequest {
	
	Player player;
	
	public GameRequest(HttpExchange e, RequestEnvelope req) throws IOException {
		super(e, req);
	}
	
	public Player getPlayer(){
		return player;
	}

}
