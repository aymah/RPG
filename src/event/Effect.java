package event;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unit.Unit;

public class Effect {

	private Map<String, Object> params;
	private Ability ability;
	private Equipment equipment;
	
	public Effect(Map<String, Object> params) {
		this.params = params;
	}
	
	public String getKey() {
		return (String)params.get("Key");
	}

	public double modifyValue(Object object, Unit unit) {
		double value = (double)object;
		double modifyMultiplier = calculateMultiplier(unit);
		for (int i = 0; i < modifyMultiplier; i++) {
			if (params.get("Modify Type").equals("Additive")) {
				value += (double)params.get("Modify Value");
			} else if (params.get("Modify Type").equals("Multiplicative")) {
				value *= (double)params.get("Modify Value");
			}
		}
		return value;
	}

	private double calculateMultiplier(Unit unit) {
		switch ((String)params.get("Modify Multiplier")) {
			case "Spaces Moved":
				return unit.getMovement() - unit.getCurrMovement();
			case "Ability Level":
				return ability.getLevel();
			case "Equipment Level":
				return (int)equipment.get("Level");
			case "Current Stamina":
				return unit.getPotentialStamina();
			case "Current Strength":
				return unit.getPotentialStrength();
			case "Ability Level * Current Strength":
				return ability.getLevel() * unit.getPotentialStrength();
			case "Ability Level * Current Stamina":
				return ability.getLevel() * unit.getPotentialStamina();
			case "Apply Once":
				return 1;
			case "Ability Level * HP":
				return unit.getHP() * ability.getLevel();
			case "Current Magic":
				return unit.getPotentialMagic();
			case "Ability Level * Current Magic":
				return unit.getPotentialMagic() * ability.getLevel();
			default:
				return 0;
		}
	}
	
	public static Map<String, List<Effect>> getEffects(Map<String, List<Effect>> effects, String field, Object type) {
		Map<String, List<Effect>> effectMap = new HashMap<String, List<Effect>>();
		for (String key: effects.keySet()) {
			List<Effect> effectList = effects.get(key);
			List<Effect> newEffectList = new ArrayList<Effect>();
			for (Effect effect: effectList) {
				if (effect.hasValue(field, type)) {
					newEffectList.add(effect);
				}
			}
			if (newEffectList.size() > 0)
				effectMap.put(key, newEffectList);
		}
		return effectMap;
	}
	
	public static Comparator<Effect> getComparator() {
		Comparator<Effect> comparator = new Comparator<Effect>() {
			@Override
			public int compare(Effect o1, Effect o2) {
				return (int)o1.get("Modify Priority") - (int)o2.get("Modify Priority");
			}
		};
		return comparator;
	}
	
	public boolean hasValue(String field, Object value) {
		if (params.get(field) != null && params.get(field).equals(value)) return true;
		return false;
	}
	
	public Object get(String field) {
		return params.get(field);
	}
	
	public void set(String field, Object value) {
		params.put(field, value);
	}

	public double getModifyValue() {
		return (double)params.get("Modify Value");
	}
	
	public Ability getAbility() {
		return ability;
	}

	public void setAbility(Ability ability) {
		this.ability = ability;
	}
	
	public Equipment getEquipment() {
		return equipment;
	}
	
	public void setEquipment(Equipment equipment) {
		this.equipment = equipment;
	}

	public static boolean hasEffects(List<Effect> effectList, String field, String value) {
		for (Effect effect: effectList) {
			if (effect.hasValue(field, value)) return true;
		}
		return false;
	}

	public static void addEffects(Map<String, List<Effect>> reciever, Map<String, List<Effect>> giver) {
		for(String key: giver.keySet()) {
			List<Effect> effectList = new ArrayList<Effect>();
			if (reciever.containsKey(key)) {
				effectList = reciever.get(key);
			}
			effectList.addAll(giver.get(key));
			reciever.put(key, effectList);
		}
	}

	public boolean hasParam(String name) {
		if (params.get(name) != null) return true;
		return false;
	}

	public Map<String, Object> getParams() {
		return params;
	}
}
