package pm.cat.pogoserv.game.player;

import java.util.Base64;
import java.util.HashMap;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import POGOProtos.Data.POGOProtosData.PlayerData;
import POGOProtos.Data.Player.POGOProtosDataPlayer.Currency;
import POGOProtos.Data.Player.POGOProtosDataPlayer.PlayerAvatar;
import POGOProtos.Enums.POGOProtosEnums.Gender;
import POGOProtos.Enums.POGOProtosEnums.TeamColor;
import POGOProtos.Inventory.Item.POGOProtosInventoryItem.ItemId;
import POGOProtos.Networking.Envelopes.POGOProtosNetworkingEnvelopes.RequestEnvelope.AuthInfo;
import pm.cat.pogoserv.game.Game;
import pm.cat.pogoserv.util.Util;

public class PlayerController {
	
	private final HashMap<String, Player> players = new HashMap<>();
	private final Game game;
	
	public PlayerController(Game game){
		this.game = game;
	}
	
	public Player getPlayer(AuthInfo auth) {
		String jwt = auth.getToken().getContents();
		Player ret = players.get(jwt);
		if(ret == null){
			ret = newPlayer(auth.getProvider(), jwt);
			players.put(jwt, ret);
		}
		return ret;
	}
	
	private Player newPlayer(String provider, String jwt){
		String auth = parseToken(provider, jwt);
		String nick = "NanahiraIsCute";
		Player ret = new Player(provider, auth, nick, System.currentTimeMillis() - 1000*30*60);
		
		ret.pokecoins.write().amt = 100;
		ret.stardust.write().amt = 1337;
		ret.inventory.maxItemStorage = game.settings.invBaseBagItems;
		ret.inventory.maxPokemonStorage = game.settings.invBasePokemon;
		// Just some test pokeballs
		ret.inventory.item(game.settings.getItem(ItemId.ITEM_POKE_BALL_VALUE)).write().count = 50;
		ret.inventory.item(game.settings.getItem(ItemId.ITEM_GREAT_BALL_VALUE)).write().count = 25;
		ret.inventory.item(game.settings.getItem(ItemId.ITEM_MASTER_BALL_VALUE)).write().count = 10;

		// TODO: Database stuff goes here
		PlayerInfo.setDefaults(ret.stats);
		PlayerAppearance.setDefaults(ret.appearance);
		updatePlayerEXP(ret, game.settings.playerRequiredExp[20]);
		return ret;
	}
	
	public void updatePlayerEXP(Player p, long exp){
		p.stats.exp.write().value = exp;
		int[] xplevel = game.settings.playerRequiredExp;
		int maxlevel = game.settings.maxLevel();
		int level = p.stats.level.read().value;
		if(xplevel[level] >= exp && (level == maxlevel || exp < xplevel[level+1]))
			return;
		
		if(exp >= xplevel[level+1] && (level+1 == maxlevel || exp < xplevel[level+2])){
			// Got 1 level
			level++;
		}else{
			// Got multiple levels or something weird
			level = Util.insertionPoint(xplevel, (int)exp);
		}
		
		p.stats.level.write().value = level;
		p.stats.nextLevelExp.write().value = (long) game.settings.playerRequiredExp[level == maxlevel ? level : (level+1)];
	}
	
	private static String parseToken(String provider, String token){
		if(provider.equals("google"))
			return parseGoogleToken(token);
		// TODO PTC
		return null;
	}
	
	private static String parseGoogleToken(String token){
		String data = token.split("\\.")[1];
		data = new String(Base64.getDecoder().decode(data));
		JsonObject j = Json.parse(data).asObject();
		return j.get("email").asString();
	}

}
