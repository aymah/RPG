package unit;

import event.Ability;

public class ScoreKeeper {

	private static final int KILL_FACTOR = 100000000;
	private static final int DAMAGE_FACTOR = 100000;
	private static final int STAMINA_FACTOR = -100;
	private static final int DISTANCE_FACTOR = -1000;
	private static final int STAMINA_INVERSE_FACTOR = 10000;
	private static final int DISTANCE_INVERSE_FACTOR = 10000;
	
	//might want to have target threat as another factor entirely
	//might want to have some sort of factor for destroying armor too
	private int killScore; //whether or not you can kill, with maybe threat mixed in
	private int damageScore; //could be raw damage, or could be % damage, or a factor of %damage and threat
	private int distanceScore; //distance from enemies, would be related to target threat as well.
	private int staminaConsumed;
	private Ability ability;
	private Unit unit;
	private Unit target;
	
	public ScoreKeeper(Unit unit, int distanceScore, int movementConsumed) {
		this.unit = unit;
		int damage = 0;
		int killScore = 0;
		this.staminaConsumed = movementConsumed * unit.getMovementCost();
		this.distanceScore = distanceScore;
	}
	
	public ScoreKeeper(Unit unit, Ability ability, Unit target, int movementConsumed) {
		this.unit = unit;
		this.ability = ability;
		this.target = target;
		int damage = ability.calculateDamage(unit, target);
		this.damageScore = calcDamageScore(damage, target);
		if (damage >= target.getCurrHP())
			this.killScore = calcKillScore(target);
		else
			this.killScore = 0;
		this.staminaConsumed = movementConsumed * unit.getMovementCost() + ability.getStamCost(unit);
	}
		
	//this formula is currently shit, definitely needs some revision/fine tuning
	public int getScore() {
//		System.out.println("SCORE: " + (killScore() + damageScore() + staminaScore())/* * ability.getPriority()*/);
//		System.out.println("SCORE: " + (killScore() + damageScore() + staminaScore() + distanceScore()));
		return (killScore() + damageScore() + staminaScore() + distanceScore())/* * ability.getPriority()*/;
	}
	
	private int killScore() {
		return killScore * KILL_FACTOR;
	}
	
	private int damageScore() {
		return damageScore * DAMAGE_FACTOR;
	}
	
	private int staminaScore() {
//		if (staminaConsumed != 0)
//			return STAMINA_INVERSE_FACTOR/staminaConsumed;
//		return 0;
		return staminaConsumed * STAMINA_FACTOR;
	}
	
	private int distanceScore() {
//		if (distanceScore != 0)
//			return DISTANCE_INVERSE_FACTOR/distanceScore;
//		return 0;
		return distanceScore * DISTANCE_FACTOR;
	}
	
	public int getKillScore() {
		return killScore;
	}
	
	public int getStaminaConsumed() {
		return staminaConsumed;
	}

	public int getDistanceScore() {
		return distanceScore;
	}
	
	public Unit getUnit() {
		return unit;
	}
	
	public Unit getTarget() {
		return target;
	}
	
	public Ability getAbility() {
		return ability;
	}
	
	//target is passed for calculating score based on target priority
	private int calcDamageScore(int damage, Unit target) {
		return damage;
	}
	
	private int calcKillScore(Unit target) {
		return 1;
	}

}
