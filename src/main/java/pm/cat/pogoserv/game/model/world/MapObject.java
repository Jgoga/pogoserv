package pm.cat.pogoserv.game.model.world;

import pm.cat.pogoserv.game.Game;
import pm.cat.pogoserv.util.Locatable;
import pm.cat.pogoserv.util.Unique;

public class MapObject implements Unique, Locatable {

	private final long uid;
	protected double latitude, longitude;
	
	public MapObject(double latitude, double longitude, long uid){
		this.latitude = latitude;
		this.longitude = longitude;
		this.uid = uid;
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
	
	public void onAdd(Game game, WorldCell cell){ }
	public void onRemove(Game game, WorldCell cell){ }
	
}
