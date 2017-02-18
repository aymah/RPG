package unit;

import java.io.Serializable;

public class Stats implements Serializable {


	protected int HP;
	protected int MP;
	protected int strength;
	protected int magic;
	protected int movement;
	protected int initiative;
	protected int stamina;
	
	
	public Stats(int HP, int MP, int strength, int magic, int movement, int initiative, int stamina) {
		this.HP = HP;
		this.MP = MP;
		this.strength = strength;
		this.magic = magic;
		this.movement = movement;
		this.initiative = initiative;
		this.stamina = stamina;
	}


	public Stats(Stats stats) {
		this.HP = stats.HP;
		this.MP = stats.MP;
		this.strength = stats.strength;
		this.magic = stats.magic;
		this.movement = stats.movement;
		this.initiative = stats.initiative;
		this.stamina = stats.stamina;
	}
	
	public Stats(Stats stats, Stats perLevelStats, int level) {
		this.HP = stats.HP + (level * perLevelStats.HP);
		this.MP = stats.MP + (level * perLevelStats.MP);
		this.strength = stats.strength + (level * perLevelStats.strength);
		this.magic = stats.magic + (level * perLevelStats.magic);
		this.movement = stats.movement + (level * perLevelStats.movement);
		this.initiative = stats.initiative + (level * perLevelStats.initiative);
		this.stamina = stats.stamina + (level * perLevelStats.stamina);
	}
	
	public void incrementStats(Stats stats, Stats perLevelStats) {
		this.HP = stats.HP + perLevelStats.HP;
		this.MP = stats.MP + perLevelStats.MP;
		this.strength = stats.strength + perLevelStats.strength;
		this.magic = stats.magic + perLevelStats.magic;
		this.movement = stats.movement + perLevelStats.movement;
		this.initiative = stats.initiative + perLevelStats.initiative;
		this.stamina = stats.stamina + perLevelStats.stamina;
	}


	public int getHP() {
		return HP;
	}
	
	public int getMP() {
		return MP;
	}

	public int getStrength() {
		return strength;
	}

	public int getMagic() {
		return magic;
	}

	public int getMovement() {
		return movement;
	}

	public int getInitiative() {
		return initiative;
	}

	public int getStamina() {
		return stamina;
	}
	
	public int get(String str) {
		switch(str) {
			case "HP":
				return HP;
			case "MP":
				return MP;
			case "Strength":
				return strength;
			case "Magic":
				return magic;
			case "Movement":
				return movement;
			case "Initiative":
				return initiative;
			case "Stamina":
				return stamina;
			default:
				return -1;
		}
	}
	
	public void set(String str, int value) {
		switch(str) {
			case "HP":
				this.HP = value;
				break;
			case "MP":
				this.MP = value;
				break;
			case "Strength":
				this.strength = value;
				break;
			case "Magic":
				this.magic = value;
				break;
			case "Movement":
				this.movement = value;
				break;
			case "Initiative":
				this.initiative = value;
				break;
			case "Stamina":
				this.stamina = value;
				break;
		}
	}
	
	public void add(String str, int value) {
		switch(str) {
			case "HP":
				this.HP += value;
				break;
			case "MP":
				this.MP += value;
				break;
			case "Strength":
				this.strength += value;
				break;
			case "Magic":
				this.magic += value;
				break;
			case "Movement":
				this.movement += value;
				break;
			case "Initiative":
				this.initiative += value;
				break;
			case "Stamina":
				this.stamina += value;
				break;
		}
	}
}
