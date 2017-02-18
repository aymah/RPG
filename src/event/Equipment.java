package event;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import unit.Hero;
import unit.Unit;

public class Equipment implements Item {

	Map<String, Object> params;
	Map<String, List<Effect>> effects;
	
	public Equipment(ItemFactory itemFactory) {
		this.params = itemFactory.getParams();
		JSONArray effectsList = (JSONArray) params.get("Effects");
		this.effects = new HashMap<String, List<Effect>>();
		for (Effect effect: makeEffects(effectsList)) {
			List<Effect> effectList = new ArrayList<Effect>();
			if (this.effects.containsKey(effect.getKey())) {
				effectList = this.effects.get(effect.getKey());
			}
			effectList.add(effect);
			effect.setEquipment(this);
			this.effects.put(effect.getKey(), effectList);
		}	
	}
	
	private List<Effect> makeEffects(JSONArray effectsList) {
		List<Effect> effects = new ArrayList<Effect>();
		for (Object effectObject: effectsList) {
			Effect effect = makeEffect((JSONObject) effectObject);
			effects.add(effect);
		}
		return effects;
	}
	
	private Effect makeEffect(JSONObject effectObject) {
		Map<String, Object> params = new HashMap<String, Object>();
		for (String key: effectObject.keySet()) {
			params.put(key, effectObject.get(key));
		}
		Effect effect = new Effect(params);
		return effect;
	}
	
	public Equipment(Map<String, Object> params, List<Effect> effects) {
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
		this.params = params;
		this.effects = effects;
	}

	public Equipment(Equipment equip) {
		params = new HashMap<String, Object>();
		for (String key: equip.getParams().keySet()) {
			params.put(key, equip.getParams().get(key)); //Should act as a deep copy because all of these objects should be immutable
		}
		this.effects = equip.getEffects();
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

	public void upgrade(Hero hero) {
		set("Level", (int)get("Level") + 1);
		hero.updateStats();
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
	
	public static Comparator getComparator() {
		Comparator<Equipment> comparator = new Comparator<Equipment>() {
			@Override
			public int compare(Equipment o1, Equipment o2) {
				return o1.getName().compareTo(o2.getName());
			}
		};
		return comparator;
	}

	public static Map<String, Equipment> clone(Map<String, Equipment> equipment) {
		Map<String, Equipment> clonedEquipment = new HashMap<String, Equipment>();
		for (String key: equipment.keySet()) {
			Equipment equip = equipment.get(key);
			Equipment newEquip = null;
			if (equip.getClass() == Armor.class) {
				newEquip = new Armor(equip);
			} else {
				newEquip = new Equipment(equip);
			}
			clonedEquipment.put(key, newEquip);
		}
		return clonedEquipment;
	}
	
	public Map<String, Object> getParams() {
		return params;
	}
	
	public Map<String, List<Effect>> getEffects() {
		return effects;
	}

	@Override
	public String getType() {
		return (String)params.get("Type");
	}

	@Override
	public String getName() {
		return (String)params.get("Name");
	}
}
