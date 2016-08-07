package pm.cat.pogoserv.game.net.request.impl;

import java.io.IOException;

import POGOProtos.Data.Capture.POGOProtosDataCapture.CaptureAward;
import POGOProtos.Networking.Envelopes.POGOProtosNetworkingEnvelopes.RequestEnvelope;
import POGOProtos.Networking.Requests.POGOProtosNetworkingRequests.Request;
import POGOProtos.Networking.Requests.Messages.POGOProtosNetworkingRequestsMessages.CatchPokemonMessage;
import POGOProtos.Networking.Responses.POGOProtosNetworkingResponses.CatchPokemonResponse;
import pm.cat.pogoserv.game.event.impl.CatchPokemonEvent;
import pm.cat.pogoserv.game.net.ProtobufMapper;
import pm.cat.pogoserv.game.net.request.RequestMapper;

public class CatchPokemonHandler implements RequestMapper<CatchPokemonEvent> {
	
	@Override
	public CatchPokemonEvent parse(Request req, RequestEnvelope re) throws IOException {
		CatchPokemonMessage m = CatchPokemonMessage.parseFrom(req.getRequestMessage());
		return new CatchPokemonEvent(
				m.getEncounterId(),
				m.getPokeball(),
				m.getNormalizedReticleSize(),
				m.getSpawnPointId(),
				m.getHitPokemon(),
				m.getSpinModifier(),
				m.getNormalizedHitPosition());
	}

	@Override
	public Object write(CatchPokemonEvent re) throws IOException {
		CatchPokemonResponse.Builder ret = CatchPokemonResponse.newBuilder()
				.setStatus(re.catchStatus);
		if(re.catchStatus == CatchPokemonResponse.CatchStatus.CATCH_SUCCESS){
			if(re.missPercent != 0)
				ret.setMissPercent(re.missPercent);
			ret.setCaptureAward(ProtobufMapper.captureAward(CaptureAward.newBuilder(), re.award));
		}
		
		return ret;
	}

}
