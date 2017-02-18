package unit;

import event.Ability;

public class AIMove {
	private int y;
	private int x;
	private Ability ability;
	private Unit unit;
	private Unit target;
	
	public AIMove(Unit unit, int y, int x, Ability ability, Unit target) {
		this.unit = unit;
		this.y = y;
		this.x = x;
		this.ability = ability;
		this.target = target;
	}
	
	public AIMove(ScoreKeeper scoreKeeper, int y, int x) {
		this.unit = scoreKeeper.getUnit();
		this.y = y;
		this.x = x;
		this.ability = scoreKeeper.getAbility();
		this.target = scoreKeeper.getTarget();
	}
	
	public int getY() {
		return y;
	}
	
	public int getX() {
		return x;
	}
	
	public Ability getAbility() {
		return ability;
	}
	
	public Unit getTarget() {
		return target;
	}
	
	public Unit getUnit() {
		return unit;
	}
}
