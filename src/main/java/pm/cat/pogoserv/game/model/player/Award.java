package pm.cat.pogoserv.game.model.player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.ToIntFunction;

public class Award implements Iterable<Award.AwardEntry> {
	
	private final List<AwardEntry> awards;
	
	public Award(){
		this(4);
	}
	
	public Award(int len){
		awards = new ArrayList<>(len);
	}
	
	public void addEntry(int activity, int exp, int candy, int stardust){
		awards.add(new AwardEntry(activity, exp, candy, stardust));
	}
	
	public int totalExp(){
		return sum(e -> e.exp);
	}
	
	public int totalCandy(){
		return sum(e -> e.candy);
	}
	
	public int totalStardust(){
		return sum(e -> e.stardust);
	}
	
	private int sum(ToIntFunction<AwardEntry> f){
		return awards.stream().mapToInt(f).sum();
	}
	
	@Override
	public Iterator<AwardEntry> iterator() {
		return awards.iterator();
	}
	
	public static class AwardEntry {
		public final int activity;
		public final int exp;
		public final int candy;
		public final int stardust;
		
		public AwardEntry(int activity, int exp, int candy, int stardust){
			this.activity = activity;
			this.exp = exp;
			this.candy = candy;
			this.stardust = stardust;
		}
		
	}
	
}
