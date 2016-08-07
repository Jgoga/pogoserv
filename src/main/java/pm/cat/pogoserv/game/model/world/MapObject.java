package pm.cat.pogoserv.game.model.world;

import pm.cat.pogoserv.game.Game;

public class MapObject implements UniqueLocatable {

	private final long uid;
	protected double latitude, longitude;
	protected Game game;
	
	public MapObject(long uid, double latitude, double longitude){
		this.latitude = latitude;
		this.longitude = longitude;
		this.uid = uid;
	}
	
	public void init(Game game){
		this.game = game;
	}
	
	@Override
	public double getLatitude(){
		return latitude;
	}
	
	@Override
	public double getLongitude(){
		return longitude;
	}
	
	@Override
	public long getUID(){
		return uid;
	}
	
	@Override
	public int hashCode(){
		return (int) ((uid >>> 32) + uid);
	}
	
	@Override
	public String toString(){
		return String.format("(%.4f, %.4f): 0x%x", latitude, longitude, uid);
	}
	
}
