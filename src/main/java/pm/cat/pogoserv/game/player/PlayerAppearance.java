package pm.cat.pogoserv.game.player;

import POGOProtos.Enums.POGOProtosEnums.Gender;

public class PlayerAppearance {
	
	public int skin;
	public int hair;
	public int shirt;
	public int pants;
	public int hat;
	public int shoes;
	public Gender gender;
	public int eyes;
	public int backpack;
	
	public static void setDefaults(PlayerAppearance a){
		a.skin = 0;
		a.hair = 0;
		a.shirt = 0;
		a.pants = 0;
		a.hat = 0;
		a.shoes = 0;
		a.gender = Gender.FEMALE;
		a.eyes = 0;
		a.backpack = 0;
	}
	
}
