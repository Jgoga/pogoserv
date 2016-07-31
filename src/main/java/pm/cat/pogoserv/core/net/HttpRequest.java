package pm.cat.pogoserv.core.net;

import java.io.IOException;
import java.io.InputStream;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import POGOProtos.Networking.Envelopes.POGOProtosNetworkingEnvelopes.RequestEnvelope;
import POGOProtos.Networking.Envelopes.POGOProtosNetworkingEnvelopes.ResponseEnvelope;
import pm.cat.pogoserv.core.Constants;

public class HttpRequest {
	
	public final Server server;
	public final RequestEnvelope request;
	public final ResponseEnvelope.Builder response;
	public int status = 200;
	public int protoStatus = Constants.SERVER_OK;
	
	public HttpRequest(Server server, RequestEnvelope request){
		this.server = server;
		this.request = request;
		response = ResponseEnvelope.newBuilder();
		response.setRequestId(request.getRequestId());
	}
	
	public byte[] toByteArray(){
		response.setStatusCode(protoStatus);
		return response.build().toByteArray();
	}
	
	public void writeResponseHeaders(Headers h){
		h.add("Server", Constants.SERVER);
		h.add("Cache-Control", "no-cache");
	}
	
	static HttpRequest parse(Server server, HttpExchange he) throws IOException {
		InputStream in = he.getRequestBody();
		HttpRequest ret = new HttpRequest(server, RequestEnvelope.parseFrom(in));
		in.close();
		return ret;
	}
	
}
