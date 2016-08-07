package pm.cat.pogoserv.game.net;

import java.io.IOException;
import java.io.OutputStream;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.sun.net.httpserver.HttpExchange;

import POGOProtos.Networking.Envelopes.POGOProtosNetworkingEnvelopes.RequestEnvelope;
import POGOProtos.Networking.Envelopes.POGOProtosNetworkingEnvelopes.ResponseEnvelope;
import pm.cat.pogoserv.net.Request;

public class POGORequest extends Request {
	
	public static final int OK = 1;
	public static final int AUTH_OK = 2;
	public static final int NEW_RPC_ENDPOINT = 53;
	public static final int INVALID_TOKEN = 102;
	
	private static final FieldDescriptor STATUS_FIELD = 
			ResponseEnvelope.getDescriptor().findFieldByNumber(ResponseEnvelope.STATUS_CODE_FIELD_NUMBER);
	
	public final RequestEnvelope req;
	public final ResponseEnvelope.Builder resp = ResponseEnvelope.newBuilder();
	
	public POGORequest(HttpExchange e, RequestEnvelope req) throws IOException {
		super(e);
		this.req = req;
		resp.setRequestId(req.getRequestId());
	}
	
	@Override
	public void writeReplyTo(OutputStream out) throws IOException {
		if(!resp.hasField(STATUS_FIELD))
			resp.setStatusCode(OK);
		resp.build().writeTo(out);
	}

}
