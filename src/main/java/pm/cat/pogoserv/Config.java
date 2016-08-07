package pm.cat.pogoserv;

public interface Config {
	
	/* Server constants */
	public static final String VERSION = "0.0.1";
	public static final String SERVER = "pogoserv/" + VERSION;
	public static final boolean HTTP_USE_CHUNKED = true;
	public static final boolean HTTP_USE_GZIP = true;
	public static final String NEW_RPC_ENDPOINT = "/plfe/rpc";
	public static final String RPC_ENDPOINT = "/plfe/%d";
	public static final String RPC_ENDPOINT_FULL_URL = "%s" + RPC_ENDPOINT;
	public static final int SERVER_BACKLOG = 5;
	
	public static final long AUTH_TICKET_VALID_TIME = 1000 * 60 * 30; // 30 minutes
	
	/* Game constants */
	public static final int NUM_TYPES = 18;
	public static final String POKECOINS = "POKECOIN";
	public static final String STARDUST = "STARDUST";
	
	public static final int ASSET_VERSION = 2903;
	
}
