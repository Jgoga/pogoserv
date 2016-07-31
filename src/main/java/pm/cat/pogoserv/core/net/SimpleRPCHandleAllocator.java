package pm.cat.pogoserv.core.net;

public class SimpleRPCHandleAllocator implements RPCHandleAllocator {
	
	private final int index;
	private final String host;
	
	public SimpleRPCHandleAllocator(String host, int index){
		this.host = host;
		this.index = index;
	}
	
	@Override
	public int getRpcIndex() {
		return index;
	}

	@Override
	public String getRpcHost() {
		return host;
	}

}
