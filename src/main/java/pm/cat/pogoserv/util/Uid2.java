package pm.cat.pogoserv.util;

public class Uid2 {
	public final long u1, u2;
	
	public Uid2(Unique u1, Unique u2){
		this(u1.getUID(), u2.getUID());
	}
	
	public Uid2(long u1, long u2){
		this.u1 = u1;
		this.u2 = u2;
	}
	
	@Override
	public String toString(){
		return "(0x" + Long.toHexString(u1) + ", 0x" + Long.toHexString(u2) + ")";
	}
	
	@Override
	public int hashCode(){
		return Long.hashCode(u1) * 37 + Long.hashCode(u2);
	}
	
}
