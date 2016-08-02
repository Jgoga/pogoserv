package pm.cat.pogoserv.game.net.request;

import com.google.protobuf.ByteString;

import POGOProtos.Networking.Envelopes.POGOProtosNetworkingEnvelopes;

public class AuthTicket {
	
	final ByteString start;
	final ByteString end;
	final long expirationTimestamp;
	
	public AuthTicket(ByteString start, ByteString end, long expirationTimestamp){
		this.start = start;
		this.end = end;
		this.expirationTimestamp = expirationTimestamp;
	}
	
	@Override
	public int hashCode(){
		return start.hashCode() * 37 + end.hashCode();
	}
	
	@Override
	public boolean equals(Object o){
		return o instanceof AuthTicket &&
				((AuthTicket)o).start.equals(start) &&
				((AuthTicket)o).end.equals(end);
	}
	
	POGOProtosNetworkingEnvelopes.AuthTicket toProtobuf(){
		return POGOProtosNetworkingEnvelopes.AuthTicket.newBuilder()
				.setStart(start)
				.setEnd(end)
				.setExpireTimestampMs(expirationTimestamp)
				.build();
	}
	
	static AuthTicket fromProtobuf(POGOProtosNetworkingEnvelopes.AuthTicket src){
		return new AuthTicket(src.getStart(), src.getEnd(), src.getExpireTimestampMs());
	}
	
	
	
}
