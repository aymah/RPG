package event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import map.BattleMenuPanel;
import map.GamePanel;
import unit.Unit;

public class Ability extends GenericMenuItem {

	private Map<String, Object> params;
	private Map<String, List<Effect>> effects;
	
//	public Ability(String name, int priority, int stamCost) {
//		super(name);
////		this.priority = priority;
////		this.stamCost = stamCost;
//	}

	public Ability(Map<String, Object> params, List<Effect> effects) {
		super((String) params.get("Name"));
		this.params = params;
		this.effects = new HashMap<String, List<Effect>>();
		for (Effect effect: effects) {
			effect.setAbility(this);
			List<Effect> effectList = new ArrayList<Effect>();
			if (this.effects.containsKey(effect.getKey())) {
				effectList = this.effects.get(effect.getKey());
			}
			effectList.add(effect);
			this.effects.put(effect.getKey(), effectList);
		}
	}

	@Override
	public String getName() {
		return name;
	}

	public String getType() {
		return (String)params.get("Type");
	}
	
	public String getDescription() {
		return (String)params.get("Description");
	}
	
	public int getPriority() {
		return (Integer)params.get("Priority");
	}
	
	public int getStamCost(Unit unit) {
		int cost = 0;
		if (params.get("Stamina Cost") != null) 
			cost = (int)params.get("Stamina Cost");
		if (effects.get("Stamina Cost") != null) {
			List<Effect> effectList = effects.get("Stamina Cost");
			effectList.sort(Effect.getComparator());
			for (Effect effect: effectList) {
				cost = (int)(effect.modifyValue((double)cost, unit));
			}
		}
		return cost;
	}
	
	public int getMPCost(Unit unit) {
		int cost = 0;
		if(params.get("MP Cost") != null)
			cost = (int)params.get("MP Cost");
		if (effects.get("MP Cost") != null) {
			List<Effect> effectList = effects.get("MP Cost");
			System.out.println(effectList.size());
			effectList.sort(Effect.getComparator());
			for (Effect effect: effectList) {
				cost = (int)(effect.modifyValue((double)cost, unit));
			}
		}
		return cost;
	}
	
//	public int getPotentialStamCost(Unit unit) {
//		int cost = (int)params.get("Stamina Cost");
//		if (effects.get("Stamina Cost") != null) {
//			Effect effect = effects.get("Stamina Cost");
//			cost = (int)(effect.modifyValue((double)cost, unit, this));
//			return cost;
//		}
//		return cost;
//	}
	
	public int getRange() {
		int range = (int)params.get("Range");
		if (effects.get("Range") != null) {
			List<Effect> effectList = effects.get("Range");
			effectList.sort(Effect.getComparator());
			for (Effect effect: effectList) {
				range = (int)effect.modifyValue((double)range, null);
			}
		}
		return range;
	}
	
	public int calculateDamage(Unit attacker) {
		return (int)Math.round((attacker.getCurrStrength() * getStrengthFactor(attacker)) +
								attacker.getCurrMagic() * getMagicFactor(attacker));
//		int damage = 0;
//		if (effects.containsKey("Damage")) {
//			List<Effect> effectList = effects.get("Damage");
//			effectList.sort(Effect.getComparator());
//			for (Effect effect: effectList) {
//				damage = (int)effect.modifyValue((double)damage, attacker);
//			}
//		}
//		return damage;
	}
	
	public int calculatePotentialDamage(Unit attacker) {
		return (int)Math.round((attacker.getPotentialStrength() * getStrengthFactor(attacker)) +
								attacker.getPotentialMagic() * getMagicFactor(attacker));

	}
	
	public int dealDamage(Unit attacker, Unit defender) {
		if (defender != null) {
			int damage = this.calculateDamage(attacker);
			defender.takeDamage(damage);
			return damage;
		}
		return 0;
	}
	
	public void applyEffects(Unit attacker, Unit defender) {
		Map<String, List<Effect>> newEffects = createNewEffects(attacker);
		if (defender != null) {
			Map<String, List<Effect>> effects = this.getEffects("Type", "Stat Enemy Modifier");
			Effect.addEffects(effects, newEffects);
			if (effects != null) {
				defender.addEffects(effects);
			}
		}
	}
	
	private Map<String, List<Effect>> createNewEffects(Unit attacker) {
		Map<String, List<Effect>> newEffects = new HashMap<String, List<Effect>>();
		Map<String, List<Effect>> effects = this.getEffects("Type", "Create Stat Enemy Modifier");
		for (String key: effects.keySet()) {
			System.out.println("creating effect...");
			List<Effect> effectList = effects.get(key);
			List<Effect> newEffectList = new ArrayList<Effect>();
			for (Effect effect: effectList) {
				Map<String, Object> effectParams = new HashMap<String, Object>();
				Map<String, Object> oldParams = effect.getParams();
				for (String objectKey: oldParams.keySet()) {
					Object param = oldParams.get(objectKey);
					if (objectKey.equals("Modify Value")) {
						param = 1.0;
						if (oldParams.get("Modify Type").equals("Additive"))
							param = 0.0;
						param = effect.modifyValue((double)param, attacker);
					}
					if (objectKey.equals("Modify Multiplier"))
						param = "Apply Once";
					if (objectKey.equals("Type"))
						param = "Stat Enemy Modifier"; //might be useful to change to stat self modifier and remove the enemy connotation when adding effects in general
					effectParams.put(objectKey, param);
				}
				Effect newEffect = new Effect(effectParams);
				System.out.println("effect created!");
				newEffectList.add(newEffect);
			}
			newEffects.put(key, newEffectList);
		}
		return newEffects;
	}
	
	@Override
	public void execute(GamePanel gamePanel) {
		BattleMenuPanel panel = (BattleMenuPanel) gamePanel;
		panel.useAbility(this);
	}

	public int getLevel() {
		return (int)params.get("Level");
	}
	
	public int getMaxLevel() {
		return (int)params.get("Max Level");
	}
	
	public boolean isSkill() {
		if (params.get("Level") == null)
			return false;
		return true;
	}

	public int calculateSkillCost(int level) {
		if (level < 0) return (int)params.get("Initial SP Cost");
		int cost = (int)params.get("Base SP Cost");
		double scalar = (double)params.get("SP Cost Scalar");
		for (int i = 0; i < level; i++) {
			cost *= scalar;
		}
		return cost;
	}

	public Map<String, List<Effect>> getEffects(String field, String type) {
		return Effect.getEffects(effects, field, type);
	}
	

	public Map<String, List<Effect>> getAllEffects() {
		return effects;
	}
	
	public double getStrengthFactor(Unit unit) {
		double strengthFactor = 0;
		if (params.containsKey("Strength Factor"))
			strengthFactor = (double)params.get("Strength Factor");
		if (effects.get("Strength Factor") != null) {
			List<Effect> effectList = effects.get("Strength Factor");
			effectList.sort(Effect.getComparator());
			for (Effect effect: effectList) {
				strengthFactor = (double)effect.modifyValue((double)strengthFactor, unit);
			}
		}
		return strengthFactor;
	}
	
	public double getMagicFactor(Unit unit) {
		double magicFactor = 0;
		if (params.containsKey("Magic Factor"))
			magicFactor = (double)params.get("Magic Factor");
		if (effects.get("Magic Factor") != null) {
			List<Effect> effectList = effects.get("Magic Factor");
			effectList.sort(Effect.getComparator());
			for (Effect effect: effectList) {
				magicFactor = (double)effect.modifyValue((double)magicFactor, unit);
			}
		}
		return magicFactor;
	}
	
	public boolean hasParam(String name) {
		if (params.get(name) != null) return true;
		return false;
	}
	
	public boolean hasEffect(String field, String value) {
		if (getEffects(field, value).size() > 0) return true;
		return false;
	}

	public void levelUp() {
		int level = (int)params.get("Level");
		params.put("Level", level+1);
	}

	public boolean isActive() {
		if (params.get("Type").equals("Active")) return true;
		return false;
	}
	
	public void addEffects(Map<String, List<Effect>> effects) {
		for(String key: effects.keySet()) {
			List<Effect> effectList = new ArrayList<Effect>();
			if (this.effects.containsKey(key)) {
				effectList = this.effects.get(key);
			}
			effectList.addAll(effects.get(key));
			this.effects.put(key, effectList);
		}
	}

	public String getEquipmentType() {
		if (params.containsKey("Equipment Type"))
				return (String)params.get("Equipment Type");
		return "";
	}
	
	public Object get(String field) {
		return params.get(field);
	}

	public int calculateAddedStamina(Unit unit) {
		List<Effect> effectList = effects.get("Stamina");
		effectList.sort(Effect.getComparator());
		double value = 0;
		for (Effect effect: effectList) {
			value = effect.modifyValue(value, unit);
		}
		return (int)value;
	}
}
