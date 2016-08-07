package pm.cat.pogoserv.game;

import java.util.concurrent.atomic.AtomicLong;

public class UidGen {
	
	private final AtomicLong currentUid;
	
	public UidGen(){
		currentUid = new AtomicLong();
	}
	
	public UidGen(long cur){
		currentUid = new AtomicLong(cur);
	}
	
	public void set(long l){
		currentUid.set(l);
	}
	
	public long next(){
		return currentUid.getAndIncrement();
	}
	
}
