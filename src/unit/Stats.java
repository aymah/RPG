package unit;

public class Stats {



	protected int HP;
	protected int strength;
	protected int movement;
	protected int initiative;
	
	
	public Stats(int HP, int strength, int movement, int initiative) {
		this.HP = HP;
		this.strength = strength;
		this.movement = movement;
		this.initiative = initiative;
	}


	public Stats(Stats stats) {
		this.HP = stats.HP;
		this.strength = stats.strength;
		this.movement = stats.movement;
		this.initiative = stats.initiative;
	}
}
