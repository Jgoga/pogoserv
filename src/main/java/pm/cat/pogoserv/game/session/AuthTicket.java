package pm.cat.pogoserv.game.session;

import com.google.protobuf.ByteString;

public class AuthTicket {
	
	private final ByteString start;
	private final ByteString end;
	private final long expirationTimestamp;
	
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
	
	public ByteString getStart(){
		return start;
	}
	
	public ByteString getEnd(){
		return end;
	}
	
	public long getExpirationTimestamp(){
		return expirationTimestamp;
	}
	
}
