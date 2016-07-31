package pm.cat.pogoserv.core;

public interface Constants {
	
	/* Server constants */
	public static final String VERSION = "0.0.1";
	// Change to nginx/1.11.1 to spoof official servers. Though I doubt they check it.
	public static final String SERVER = "pogoserv/" + VERSION;
	
	/* RequestEnvelope status_code values (not sure if these are correct) */
	public static final int SERVER_OK = 1;
	public static final int SERVER_NEW_RPC_ENDPOINT = 53;
	public static final int SERVER_BUSY = 102;
	
	/* Game constants */
	public static final int NUM_TYPES = 18;
	public static final String POKECOINS = "POKECOIN";
	public static final String STARDUST = "STARDUST";
	
	public static final int ASSET_VERSION = 2903;
	
}
