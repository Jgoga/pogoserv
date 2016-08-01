package pm.cat.pogoserv.game.model.player;

import POGOProtos.Enums.POGOProtosEnums.Gender;

public class Appearance {
	
	public int skin;
	public int hair;
	public int shirt;
	public int pants;
	public int hat;
	public int shoes;
	public Gender gender;
	public int eyes;
	public int backpack;
	
	public static void setDefaults(Appearance a){
		a.skin = 1;
		a.hair = 1;
		a.shirt = 1;
		a.pants = 1;
		a.hat = 1;
		a.shoes = 1;
		a.gender = Gender.forNumber(1);
		a.eyes = 1;
		a.backpack = 1;
	}
	
}
