package pm.cat.pogoserv.game.session;

import java.util.Base64;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import pm.cat.pogoserv.Log;

public class AuthToken {
	
	public final String provider;
	public final String userInfo;
	
	public AuthToken(String provider, String content){
		this.provider = provider;
		this.userInfo = parseJWT(provider, content);
	}
	
	// TODO These should also be verified. Now anyone can login as anyone.
	private static String parseJWT(String provider, String content){
		if(provider.equals("google"))
			return parseGoogleToken(content);
		else
			Log.w("AuthToken", "Unimplemented: %s", provider);
		
		return null;
	}
	
	private static String parseGoogleToken(String token){
		String data = token.split("\\.")[1];
		data = new String(Base64.getDecoder().decode(data));
		JsonObject j = Json.parse(data).asObject();
		return j.get("email").asString();
	}
	
	@Override
	public int hashCode(){
		return provider.hashCode() * 37 + userInfo.hashCode();
	}
	
	@Override
	public String toString(){
		return provider + "/" + userInfo;
	}
	
}
