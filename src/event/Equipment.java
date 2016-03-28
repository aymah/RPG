package event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unit.Hero;
import unit.Unit;

public class Equipment extends GenericMenuItem{

	Map<String, Object> params;
	Map<String, List<Effect>> effects;
	
	public Equipment(Map<String, Object> params, List<Effect> effects) {
		super((String) params.get("Name"));
		this.params = params;
		this.effects = new HashMap<String, List<Effect>>();
		for (Effect effect: effects) {
			List<Effect> effectList = new ArrayList<Effect>();
			if (this.effects.containsKey(effect.getKey())) {
				effectList = this.effects.get(effect.getKey());
			}
			effectList.add(effect);
			effect.setEquipment(this);
			this.effects.put(effect.getKey(), effectList);
		}	
	}

	public Equipment(Map<String, Object> params, Map<String, List<Effect>> effects) {
		super((String) params.get("Name"));
		this.params = params;
		this.effects = effects;
	}

	public String getEquipmentType() {
		return (String)params.get("Equipment Type");
	}

	public Map<String, List<Effect>> getEffects(String field, String type) {
		return Effect.getEffects(effects, field, type);
	}

	public Object get(String string) {
		return params.get(string);
	}
	
	public void set(String field, Object value) {
		params.put(field, value);
	}
	
	public boolean has(String field, Object value) {
		if (params.get(field) != null && params.get(field).equals(value)) return true;
		return false;
	}

	public int calculateUpgradeCost(int level) {
		int cost = (int)params.get("Base Gold Cost");
		double scalar = (double)params.get("Gold Cost Scalar");
		for (int i = 0; i < level; i++) {
			cost *= scalar;
		}
		return cost;
	}

	public void upgrade(Hero unit) {
		set("Level", (int)get("Level") + 1);
		unit.updateStats();
//		Equipment upgradedItem = new Equipment(params, effects);
//		unit.equip(upgradedItem);
	}
	
	public double getEffectParam(String keyName) {
		//finds all the effects with key, runs modifyvalue on them with value 0?
		//this will work for strength factor at least.
		Map<String, List<Effect>> effects = getEffects("Key", keyName);
		double value = 0;
		System.out.println(effects.size());
		for (String key: effects.keySet()) {
			List<Effect> effectList = effects.get(key);
			for (Effect effect: effectList) {
				value = effect.modifyValue(value, null);
			}
		}
		return value;
	}

	public boolean hasEffect(String field, String value) {
		if (getEffects(field, value).size() > 0) return true;
		return false;
	}
}
