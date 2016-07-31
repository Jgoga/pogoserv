package pm.cat.pogoserv.game;

import java.util.concurrent.atomic.AtomicLong;

public class UidManager {
	
	private final AtomicLong currentUid;
	
	public UidManager(long cur){
		currentUid = new AtomicLong(cur);
	}
	
	public long next(){
		return currentUid.getAndIncrement();
	}
	
}
