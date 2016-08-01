package pm.cat.pogoserv.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.NoSuchFileException;

import POGOProtos.Enums.POGOProtosEnums.Gender;
import POGOProtos.Enums.POGOProtosEnums.TeamColor;
import POGOProtos.Inventory.Item.POGOProtosInventoryItem.ItemId;
import pm.cat.pogoserv.Log;
import pm.cat.pogoserv.game.Game;
import pm.cat.pogoserv.game.config.GameSettings;
import pm.cat.pogoserv.game.model.player.Appearance;
import pm.cat.pogoserv.game.model.player.Inventory;
import pm.cat.pogoserv.game.model.player.InventoryPokemon;
import pm.cat.pogoserv.game.model.player.Item;
import pm.cat.pogoserv.game.model.player.Player;
import pm.cat.pogoserv.game.model.player.PlayerInfo;
import pm.cat.pogoserv.game.model.player.Pokedex;
import pm.cat.pogoserv.game.model.player.Pokedex.PokedexEntry;
import pm.cat.pogoserv.util.TimestampVarPool.TSNode;

/*
 * A simple binary format for player save files.
 * TODO: Eggs!
 * 
 * Structure:
 * Length    What
 * ----------------------
 * 4         Pokedex length
 * 4         Items length
 * 4         (Inventory) Pokemon length
 * 
 * 8         Uid
 * 8         creation timestamp
 * 16        nickname (no null terminator)
 * 8         exp
 * 4         pokecoins
 * 4         stardust
 * 9         appearance (1 byte each)
 * 1         team
 * 64        misc. stats (16 * 4)
 * 4         km walked
 * variable  pokedex entries
 * variable  item entries
 * variable  pokemon entries
 * 
 * Pokedex entry:
 * 2         Pokemon id
 * 4         Seen count
 * 4         Captured count
 * 
 * Item entry:
 * 4        Item id
 * 4        Count
 * 
 * Pokemon entry:
 * 8        uid
 * 2        Pokemon id
 * 8        creation timestamp
 * 16       nickname
 * 4        cp
 * 4        stamina
 * 4        max stamina // Does this need to be stored?
 * 4        pokeball id
 * 8        captured cell id
 * 4        battles attacked count
 * 4        battles defended count
 * 4        times upgraded
 * 1        is favorited?
 * 
 */
public class BinaryFilePlayerLoader implements PlayerLoader {
	
	private static final int HEADER_SIZE = 126;
	private static final int POKEDEX_ENTRY_SIZE = 10;
	private static final int ITEM_ENTRY_SIZE = 8;
	private static final int POKEMON_ENTRY_SIZE = 71;
	
	private final Game game;
	
	public BinaryFilePlayerLoader(Game game){
		this.game = game;
	}
	
	private File playerFile(String key) throws IOException {
		if(key == null)
			throw new NoSuchFileException("Player file with null key!");
		return new File(game.settings.playerDataPath + "/" + key);
	}
	
	@Override
	public Player loadPlayer(String key) throws IOException {
		Log.d("BinLoader", "Parsing player file");
		FileInputStream in = new FileInputStream(playerFile(key));
		Player ret = loadPlayer(in);
		in.close();
		return ret;
	}
	
	private Player loadPlayer(InputStream in) throws IOException {
		int pokedexLen = readInt(in);
		int itemLen = readInt(in);
		int pokemonLen = readInt(in);
		
		long uid = readLong(in);
		Player ret = new Player(game.playerController, uid);
		ret.creationTs = readLong(in);
		ret.nickname = readString(in, 16);
		ret.setEXP(readLong(in));
		ret.pokecoins.write().amt = readInt(in);
		ret.stardust.write().amt = readInt(in);
		
		Log.d("BinLoader", "%s: Pokedex: %d | items: %d | pokemon: %d", ret, pokedexLen, itemLen, pokemonLen);
		
		readAppearance(ret.appearance, in);
		ret.team = TeamColor.forNumber(readByte(in));
		readStats(ret.stats, in);
		ret.stats.kmWalked.write().value = readFloat(in);
		
		for(int i=0;i<pokedexLen;i++)
			readPokedexEntry(ret.pokedex, in);
		
		for(int i=0;i<itemLen;i++)
			readItem(ret.inventory, in);
		
		for(int i=0;i<pokemonLen;i++)
			readPokemon(ret.inventory, in);
		
		return ret;
	}
	
	private int readByte(InputStream in) throws IOException {
		return in.read() & 0xff;
	}
	
	private int readShort(InputStream in) throws IOException {
		return (in.read() & 0xff) << 8 | (in.read() & 0xff);
	}
	
	private int readInt(InputStream in) throws IOException {
		return readShort(in) << 16 | readShort(in);
	}
	
	private float readFloat(InputStream in) throws IOException {
		return Float.intBitsToFloat(readInt(in));
	}
	
	private long readLong(InputStream in) throws IOException {
		return readInt(in) << 32L | readInt(in);
	}
	
	private String readString(InputStream in, int length) throws IOException {
		StringBuilder ret = new StringBuilder();
		boolean seenZero = false;
		for(int i=0;i<length;i++){
			int j = in.read();
			if(j == 0)
				seenZero = true;
			if(seenZero)
				continue;
			ret.append((char) j);
		}
		return ret.toString();
	}

	private void readStats(PlayerInfo dest, InputStream in) throws IOException {
		dest.numPokemonEncountered.write().value = readInt(in);
		dest.numPokemonCaptured.write().value = readInt(in);
		dest.numEvolutions.write().value = readInt(in);
		dest.numPokestopVisits.write().value = readInt(in);
		dest.numPokeballsThrown.write().value = readInt(in);
		dest.numEggsHatched.write().value = readInt(in);
		readInt(in); // big magikarp
		readInt(in); // small rattata
		dest.numBattleAttackWon.write().value = readInt(in);
		dest.numBattleAttackTotal.write().value = readInt(in);
		dest.numBattleDefendWon.write().value = readInt(in);
		dest.numBattleTrainingWon.write().value = readInt(in);
		dest.numBattleTrainingTotal.write().value = readInt(in);
		dest.prestigeRaisedTotal.write().value = readInt(in);
		dest.prestigeDroppedTotal.write().value = readInt(in);
		dest.numPokemonDeployed.write().value = readInt(in);
	}
	
	private void readAppearance(Appearance dest, InputStream in) throws IOException {
		dest.skin = readByte(in);
		dest.hair = readByte(in);
		dest.shirt = readByte(in);
		dest.pants = readByte(in);
		dest.hat = readByte(in);
		dest.shoes = readByte(in);
		dest.gender = Gender.forNumber(readByte(in));
		dest.eyes = readByte(in);
		dest.backpack = readByte(in);
	}
	
	private void readPokedexEntry(Pokedex dest, InputStream in) throws IOException {
		int id = readShort(in);
		int seen = readInt(in);
		int captured = readInt(in);
		PokedexEntry e = dest.entry(id).write();
		e.numEncounter = seen;
		e.numCapture = captured;
	}
	
	private void readItem(Inventory dest, InputStream in) throws IOException {
		int id = readInt(in);
		int count = readInt(in);
		dest.item(game.settings.getItem(id)).write().count = count;
	}
	
	private void readPokemon(Inventory dest, InputStream in) throws IOException {
		long uid = readLong(in);
		int id = readShort(in);
		InventoryPokemon p = dest.addPokemon(game.settings.getPokemon(id), uid).write();
		p.creationTimestamp = readLong(in);
		p.nickname = readString(in, 16);
		p.cp = readInt(in);
		p.stamina = readInt(in);
		p.maxStamina = readInt(in);
		p.pokeball = ItemId.forNumber(readInt(in));
		p.capturedCellId = readLong(in);
		p.battlesAttacked = readInt(in);
		p.battlesDefended = readInt(in);
		p.numUpgrades = readInt(in);
		p.favorite = readByte(in);
	}

	@Override
	public void savePlayer(Player p) throws IOException {
		OutputStream out = new FileOutputStream(playerFile(p.auth.parseID()));
		savePlayer(out, p);
		out.close();
	}
	
	private void savePlayer(OutputStream out, Player p) throws IOException {
		writeInt(out, p.pokedex.count());
		writeInt(out, p.inventory.uniqueItemCount());
		writeInt(out, p.inventory.uniquePokemonCount());
		writeLong(out, p.getUID());
		writeLong(out, p.creationTs);
		writeString(out, p.nickname, 16);
		writeLong(out, p.getExp());
		writeInt(out, p.pokecoins.read().amt);
		writeInt(out, p.stardust.read().amt);
		writeAppearance(out, p.appearance);
		writeByte(out, p.team.getNumber());
		writeStats(out, p.stats);
		writeFloat(out, p.stats.kmWalked.read().value);
		
		for(TSNode<PokedexEntry> e : p.pokedex.entries())
			writePokedexEntry(out, e.read());
		
		for(TSNode<Item> i : p.inventory.getAllItems())
			writeItem(out, i.read());
		
		for(TSNode<InventoryPokemon> i : p.inventory.getAllPokemon())
			writePokemon(out, i.read());
	}
	
	private void writeByte(OutputStream out, int b) throws IOException {
		out.write(b);
	}
	
	private void writeShort(OutputStream out, int s) throws IOException {
		writeByte(out, (s >> 8) & 0xff);
		writeByte(out, s & 0xff);
	}
	
	private void writeInt(OutputStream out, int i) throws IOException {
		writeShort(out, (i >> 16) & 0xffff);
		writeShort(out, i & 0xffff);
	}
	
	private void writeFloat(OutputStream out, float f) throws IOException {
		writeInt(out, Float.floatToIntBits(f));
	}
	
	private void writeLong(OutputStream out, long l) throws IOException {
		writeInt(out, (int) ((l >> 32) & 0xffffffff));
		writeInt(out, (int) (l & 0xffffffff));
	}
	
	private void writeString(OutputStream out, String s, int n) throws IOException {
		char[] c = s.toCharArray();
		for(int i=0;i<n;i++)
			out.write(i<c.length?c[i]:0);
	}
	
	private void writeAppearance(OutputStream out, Appearance a) throws IOException {
		out.write(new byte[]{
				(byte)a.skin, (byte)a.hair, (byte)a.shirt, (byte)a.pants, 
				(byte)a.hat, (byte)a.shoes, (byte)a.gender.getNumber(), (byte)a.eyes, (byte)a.backpack 
		});
	}
	
	private void writeStats(OutputStream out, PlayerInfo stats) throws IOException {
		writeInt(out, stats.numPokemonEncountered.read().value);
		writeInt(out, stats.numPokemonCaptured.read().value);
		writeInt(out, stats.numEvolutions.read().value);
		writeInt(out, stats.numPokestopVisits.read().value);
		writeInt(out, stats.numPokeballsThrown.read().value);
		writeInt(out, stats.numEggsHatched.read().value);
		writeInt(out, 0); // big magikarp
		writeInt(out, 0); // small rattata
		writeInt(out, stats.numBattleAttackWon.read().value);
		writeInt(out, stats.numBattleAttackTotal.read().value);
		writeInt(out, stats.numBattleDefendWon.read().value);
		writeInt(out, stats.numBattleTrainingWon.read().value);
		writeInt(out, stats.numBattleTrainingTotal.read().value);
		writeInt(out, stats.prestigeRaisedTotal.read().value);
		writeInt(out, stats.prestigeDroppedTotal.read().value);
		writeInt(out, stats.numPokemonDeployed.read().value);
	}
	
	private void writePokedexEntry(OutputStream out, PokedexEntry e) throws IOException {
		writeShort(out, e.id);
		writeInt(out, e.numEncounter);
		writeInt(out, e.numCapture);
	}
	
	private void writeItem(OutputStream out, Item i) throws IOException {
		writeInt(out, i.def.id);
		writeInt(out, i.count);
	}
	
	private void writePokemon(OutputStream out, InventoryPokemon p) throws IOException {
		writeLong(out, p.uid);
		writeShort(out, p.def.id);
		writeLong(out, p.creationTimestamp);
		writeString(out, p.nickname, 16);
		writeInt(out, p.cp);
		writeInt(out, p.stamina);
		writeInt(out, p.maxStamina);
		writeInt(out, p.pokeball.getNumber());
		writeLong(out, p.capturedCellId);
		writeInt(out, p.battlesAttacked);
		writeInt(out, p.battlesDefended);
		writeInt(out, p.numUpgrades);
		writeByte(out, p.favorite);
	}
	
}
