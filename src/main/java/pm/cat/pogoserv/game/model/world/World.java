package pm.cat.pogoserv.game.model.world;

import java.util.concurrent.ConcurrentHashMap;

import com.google.common.geometry.S2CellId;

import pm.cat.pogoserv.Log;
import pm.cat.pogoserv.game.Game;
import pm.cat.pogoserv.game.access.FortMapper;
import pm.cat.pogoserv.game.control.PokemonGen;
import pm.cat.pogoserv.util.Locatable;

// TODO: Maybe use bigger/smaller cells than level 15 (like a quad tree)
// TODO: Dynamic cell loading/unloading
public class World {

	private PokemonGen pokegen;
	private FortMapper fortMapper;
	private Game game;
	
	// TODO Turn this into a LoadingCache
	private final ConcurrentHashMap<Long, WorldCell> cells = new ConcurrentHashMap<>();
	
	public World(FortMapper fortMapper){
		this.fortMapper = fortMapper;
	}
	
	public void init(Game game){
		this.game = game;
		pokegen = new PokemonGen(game.getUidGen());
	}
	
	public PokemonGen getPokegen(){
		return pokegen;
	}
	
	public WorldCell getCell(Locatable l){
		return getCell(l.getS2CellId());
	}
	
	public WorldCell getCell(S2CellId id){
		return getCell(id.parent(WorldCell.LEVEL).id());
	}
	
	public WorldCell getCell(long id){
		return cells.get(id);
	}
	
	public WorldCell getOrCreateCell(Locatable l){
		return getOrCreateCell(l.getS2CellId());
	}
	
	public WorldCell getOrCreateCell(S2CellId id){
		id = id.parent(WorldCell.LEVEL);
		WorldCell ret = cells.get(id.id());
		if(ret == null){
			ret = new WorldCell(id);
			cells.put(id.id(), ret);
		}
		return ret;
	}
	
	public <T extends UniqueLocatable> T add(T t){
		WorldCell cell = getOrCreateCell(t);
		Log.d("World", "%s: Spawning object: %s", cell.toString(), t.toString());
		cell.add(t);
		return t;
	}
	
	public void remove(UniqueLocatable mp){
		WorldCell cell = getCell(mp);
		if(cell == null){
			Log.w("World", "Attempt to remove object (%s), but cell doesn't exist!", mp.toString());
			return;
		}
		Log.d("World", "%s: Despawning object: %s", cell.toString(), mp.toString());
		cell.remove(mp.getUID());
	}
	
	public UniqueLocatable objectForStr(String s){
		long[] l = new long[2];
		UniqueLocatable.parseUniqueStr(l, s);
		WorldCell cell = getCell(l[0]);
		return cell == null ? null : cell.get(l[1]);
	}
	
}
