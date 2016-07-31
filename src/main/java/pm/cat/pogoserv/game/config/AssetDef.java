package pm.cat.pogoserv.game.config;

public class AssetDef {
	
	public final String id;
	public String bundleName;
	public long version;
	public int checksum;
	public int size;
	public byte[] key;
	public int platform;
	/* download_url will be generated elsewhere */
	
	public AssetDef(String id){
		this.id = id;
	}
	
	@Override
	public String toString(){
		return "id='" + id + "', checksum=0x" + Integer.toHexString(checksum) + ", plat=" + platform + ", size=" + size + ", version=" + version;
	}
	
}
