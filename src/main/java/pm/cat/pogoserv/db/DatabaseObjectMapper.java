package pm.cat.pogoserv.db;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import pm.cat.pogoserv.Log;
import pm.cat.pogoserv.db.mapper.Loader;
import pm.cat.pogoserv.db.mapper.PlayerLoader;
import pm.cat.pogoserv.db.mapper.PlayerSaver;
import pm.cat.pogoserv.db.mapper.Saver;
import pm.cat.pogoserv.game.access.FortMapper;
import pm.cat.pogoserv.game.access.PlayerMapper;
import pm.cat.pogoserv.game.model.player.Player;

// TODO: Maybe cache statements if needed?
// TODO: Cache objects to write and dump when enough!
public class DatabaseObjectMapper implements PlayerMapper, FortMapper {
	
	private final Database db;
	
	public DatabaseObjectMapper(Database db){
		this.db = db;
	}
	
	@Override
	public void savePlayer(Player p){
		save(new PlayerSaver(p));
	}
	
	@Override
	public Player loadPlayer(String auth) {
		return loadAndWait(new PlayerLoader(auth));
	}
	
	private void save(Saver<?> m){
		// TODO: Tähän se listaan tunkeminen
		db.submit(m);
	}
	
	private <T> T loadAndWait(Loader<T> m){
		try {
			return load(m).get();
		} catch (InterruptedException e) {
			Log.e("Database", "Interrupted while loading " + m);
			Log.e("Database", e);
		} catch (ExecutionException e) {
			Log.e("Database", "Error while loading " + m);
			Log.e("Database", e);
		}
		return null;
	}
	
	private <T> Future<T> load(Loader<T> m){
		return db.submit(m);
	}
	
}
