package pm.cat.pogoserv.game.world;

public class DynamicMapObject extends MapObject {

	public DynamicMapObject(double latitude, double longitude, long uid) {
		super(latitude, longitude, uid);
	}
	
	public void setLatitude(double latitude){
		this.latitude = latitude;
	}
	
	public void setLongitude(double longitude){
		this.longitude = longitude;
	}

}
