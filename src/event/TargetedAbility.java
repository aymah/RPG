package event;

import unit.Unit;
import map.BattleMenuPanel;
import map.GamePanel;

public class TargetedAbility extends Ability {

	int range;
	double strengthFactor;
	
	public TargetedAbility(String name, int range, double strengthFactor) {
		super(name);
		this.range = range;
		this.strengthFactor = strengthFactor;
	}
		
	public int getRange() {
		return range;
	}
	
	public void setRange(int range) {
		this.range = range;
	}

	public void setStrengthFactor(double strengthFactor) {
		this.strengthFactor = strengthFactor;
	}
	
	public int calculateDamage(int strength) {
		return (int)(strength * strengthFactor);
	}
	
	public void dealDamage(Unit attacker, Unit defender, TargetedAbility ability) {
		int damage = ability.calculateDamage(attacker.getCurrStrength());
		defender.takeDamage(damage);
	}
	
	@Override
	public void execute(GamePanel gamePanel) {
		BattleMenuPanel panel = (BattleMenuPanel) gamePanel;
		panel.useTargetedAbility(this);
	}
}
