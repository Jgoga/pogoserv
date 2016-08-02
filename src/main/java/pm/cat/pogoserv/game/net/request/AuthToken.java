package pm.cat.pogoserv.game.net.request;

import java.util.Base64;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import POGOProtos.Networking.Envelopes.POGOProtosNetworkingEnvelopes.RequestEnvelope.AuthInfo;

public class AuthToken {
	
	public final String provider;
	public final String content;
	
	AuthToken(String provider, String content){
		this.provider = provider;
		this.content = content;
	}
	
	public String parseID(){
		if(provider.equals("google"))
			return parseGoogleToken(content);
		
		return null;
	}
	
	private static String parseGoogleToken(String token){
		String data = token.split("\\.")[1];
		data = new String(Base64.getDecoder().decode(data));
		JsonObject j = Json.parse(data).asObject();
		return j.get("email").asString();
	}
	
	public static AuthToken fromAuthInfo(AuthInfo ai){
		return new AuthToken(ai.getProvider(), ai.getToken().getContents());
	}
	
	@Override
	public int hashCode(){
		return provider.hashCode() * 37 + content.hashCode();
	}
	
	@Override
	public String toString(){
		return provider + "/" + content;
	}
	
}
