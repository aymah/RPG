package unit;

import java.util.List;

import event.Ability;

public class Unit implements Comparable{

	OrderOfBattle orderOfBattle;
	Stats stats;
	Stats currStats;
	int posIndexX;
	int posIndexY;
	List<Ability> abilities;
	String faction;
	int ordering;
	
	
	public Unit(Stats stats, String faction) {
		this.stats = stats;
		this.currStats = new Stats(stats);
		this.faction = faction;
	}
	
	public void setOrderOfBattle(OrderOfBattle orderOfBattle) {
		this.orderOfBattle = orderOfBattle;
	}
	
	public int getPosIndexX() {
		return posIndexX;
	}
	
	public int getPosIndexY() {
		return posIndexY;
	}
	
	public void setPosition(int y, int x) {
		this.posIndexX = x;
		this.posIndexY = y;
	}
	
	public void setAbilities(List<Ability> abilities) {
		this.abilities = abilities;
	}
	
	public List<Ability> getAbilities() {
		return abilities;
	}
	
//	public int calculateDamage() {
//		return stats.getStrength();
//	}
	
	public int getCurrStrength() {
		return currStats.strength;
	}
	
	public int getCurrInitiative() {
		return currStats.initiative;
	}
	
	public void takeDamage(int damage) {
		currStats.HP -= damage;
		System.out.println("currentHP: " + currStats.HP);
		checkDeath();
	}

	public int getCurrHP() {
		return currStats.HP;
	}
	
	public int getCurrMovement() {
		return currStats.movement;
	}
	
	public void checkDeath() {
		if (currStats.HP <= 0) 
			die();
	}
	
	public void die() {
		System.out.println("died");
		orderOfBattle.removeUnit(this);
	}
	
//	public void attack(Unit unit, Ability ability) {
//		unit.takeDamage(ability.calculateDamage(this));
//		System.out.println("currentHP: " + unit.currHP);
//		unit.checkDeath();
//	}
	
	public String getFaction() {
		return faction;
	}
	
	public void endTurn() {
		currStats.movement = stats.movement;
		ordering += currStats.initiative;
	}

	public void subractMovement(int movementUsed) {
		currStats.movement -= movementUsed;
	}
	
	public int getOrdering() {
		return ordering;
	}

	@Override
	public int compareTo(Object o) {
		if (this == o) return 0;
		return -1;
	}
}
