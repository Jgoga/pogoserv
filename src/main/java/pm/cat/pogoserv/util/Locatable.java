package pm.cat.pogoserv.util;

import com.google.common.geometry.S2CellId;
import com.google.common.geometry.S2LatLng;

public interface Locatable {

	double getLatitude();
	double getLongitude();
	
	default S2LatLng s2LatLngPos(){
		return S2LatLng.fromDegrees(getLatitude(), getLongitude());
	}
	
	default S2CellId getS2CellId(){
		return S2CellId.fromLatLng(s2LatLngPos());
	}
	
	default double distanceTo(S2LatLng pos){
		return s2LatLngPos().getEarthDistance(pos);
	}

	default double distanceTo(Locatable l){
		return distanceTo(l.s2LatLngPos());
	}
}
