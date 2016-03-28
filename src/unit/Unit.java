package unit;

import org.json.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import map.BattleMap;
import map.Coordinates;
import map.TileMap;
import event.Ability;
import event.Corridor;
import event.Effect;
import event.Encounter;
import event.Equipment;
import event.Event;

public class Unit {

	OrderOfBattle orderOfBattle;
	Stats permStats;
	Stats stats;
	Stats currStats;
	Stats potentialStats;
	Stats perLevelStats;
	int level;
	int posIndexX;
	int posIndexY;
	int acquisitionRange;
	List<Ability> abilities;
	Map<String, List<Effect>> statuses;
	Map<String, Equipment> equipment; //map of slot to equipment
	Ability currAbility;
	Ability potentialAbility;
	String faction;
	int ordering;
	String name;
	String type;
	Behavior behavior;
	boolean takingDamage;
	boolean isDying;
	int movementCost = 5;
	Party party;
	String filename;
	
	public Unit(String filename, String faction, String name, int level) {
		this.filename = filename;
		this.faction = faction;
		this.name = name;
		this.statuses = new HashMap<String, List<Effect>>();
		this.level = level;
		readUnitFile(filename);
		this.permStats = new Stats(permStats, perLevelStats, level);
		this.stats = new Stats(permStats);
		this.currStats = new Stats(permStats);
		Random rand = new Random();
		takingDamage = false;
		isDying = false;
	}
	
	public Unit(String filename, String faction, String name, Party party) {
		this.filename = filename;
		this.faction = faction;
		this.name = name;
		this.statuses = new HashMap<String, List<Effect>>();
		this.party = party;
		readUnitFile(filename);
		level = party.getLevel(type);
//		party.loadUnitStats(type, stats, perLevelStats);
		this.permStats = new Stats(permStats, perLevelStats, level);
		this.stats = new Stats(permStats);
		this.currStats = new Stats(permStats);
		Random rand = new Random();
		takingDamage = false;
		isDying = false;
	}
	
//	public Unit(Stats stats, String faction, String name) {
//		this.stats = stats;
//		this.currStats = new Stats(stats);
//		this.faction = faction;
//		this.name = name;
//		this.ordering = stats.initiative;
//	}
	
	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
	    return new String(encoded, encoding);
	}
	
	private void readUnitFile(String filename) {
		URL url = getClass().getResource("/testUnits/" + filename + ".json");
		
		filename = url.getPath();
		JSONObject unitObject = null;
		try {
			unitObject = new JSONObject(readFile(filename, StandardCharsets.UTF_8));
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		type = unitObject.getString("Class");
		level = unitObject.getInt("Level");
		behavior = makeBehavior(unitObject.getString("Behavior"));
		if (unitObject.has("Acquisition Range"))
			acquisitionRange = unitObject.getInt("Acquisition Range");
		if (unitObject.has("Base Stats"))
			makeUnit((JSONObject)unitObject.get("Base Stats"));
		if (unitObject.has("Per Level Stats"))
			makePerLevel((JSONObject)unitObject.get("Per Level Stats"));
		if (unitObject.has("Equipment")) 
			makeEquipment((JSONObject)unitObject.get("Equipment"));
		if (unitObject.has("Abilities")) 
			makeAbilities((JSONArray)unitObject.get("Abilities"));
	}
	

	private void makeUnit(JSONObject baseStats) {
		int HP = baseStats.getInt("HP");
		int MP = 0;
		if (baseStats.has("MP"))
			MP = baseStats.getInt("MP");
		int strength = baseStats.getInt("Strength");
		int magic = 0;
		if (baseStats.has("Magic"))
			magic = baseStats.getInt("Magic");
		int movement = baseStats.getInt("Movement");
		int initiative = baseStats.getInt("Initiative");
		int stamina = baseStats.getInt("Stamina");
		this.permStats = new Stats(HP, MP, strength, magic, movement, initiative, stamina);
	}
	
	private void makePerLevel(JSONObject perLevelStats) {
		int HP = perLevelStats.getInt("HP");
		int MP = 0;
		if (perLevelStats.has("MP"))
			MP = perLevelStats.getInt("MP");		
		int strength = perLevelStats.getInt("Strength");
			int magic = 0;
		if (perLevelStats.has("Magic"))
			magic = perLevelStats.getInt("Magic");
		int movement = perLevelStats.getInt("Movement");
		int initiative = perLevelStats.getInt("Initiative");
		int stamina = perLevelStats.getInt("Stamina");
		this.perLevelStats = new Stats(HP, MP, strength, magic, movement, initiative, stamina);
	}
	
	private void makeEquipment(JSONObject equipmentMap) {
		equipment = new HashMap<String, Equipment>();
		for (String key: equipmentMap.keySet()) {
			String filename = (String) equipmentMap.get(key);
			Equipment item = makeItem(filename);
			equipment.put(key, item);
		}
		addEquipmentEffectsToUnit();
	}
	
	private Equipment makeItem(String filename) {
		URL url = getClass().getResource("/testItems/" + filename + ".json");
		filename = url.getPath();
		JSONObject itemObject = null;
		try {
			itemObject = new JSONObject(readFile(filename, StandardCharsets.UTF_8));
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String, Object> params = new HashMap<String, Object>();
		for (String key: itemObject.keySet()) {
			if (!key.equals("Effects"))
				params.put(key, itemObject.get(key));
		}
		List<Effect> effects = new ArrayList<Effect>();
		JSONArray effectsList = (JSONArray) itemObject.get("Effects");
		effects = makeEffects(effectsList);
		Equipment equipment = new Equipment(params, effects);
		return equipment;
	}

	private void makeAbilities(JSONArray abilityList) {
		abilities = new ArrayList<Ability>();
		for (Object ability: abilityList) {
			JSONObject abilityJSON = (JSONObject) ability;
			if (abilityJSON.getString("Type").equals("Passive"))
				makeAbility(abilityJSON);
		}
		for (Object ability: abilityList) {
			JSONObject abilityJSON = (JSONObject) ability;
			if (abilityJSON.getString("Type").equals("Active"))
				makeAbility((JSONObject) ability);
		}
	}
	
	private void makeAbility(JSONObject abilityObject) {
		Ability ability = null;
		List<Effect> effects = new ArrayList<Effect>();
		Map<String, Object> params = new HashMap<String, Object>();
		for (String key: abilityObject.keySet()) {
			if (!key.equals("Effects"))
				params.put(key, abilityObject.get(key));
		}
		JSONArray effectsList = (JSONArray) abilityObject.get("Effects");
		effects = makeEffects(effectsList);
		ability = new Ability(params, effects);
		if (ability.get("Type").equals("Passive")) {
			this.addEffects(ability.getEffects("Type", "Permanent Stat Self Modifier")); //adding ability effects to the unit
		}
		if (ability.hasEffect("Type", "Party Modifier"))
			party.addEffects(ability.getEffects("Type", "Party Modifier"));
		//adding equipment related effects to the ability and unit separately. Otherwise we could have duplicate effects in unit
		if (ability.get("Type").equals("Active")) {
			if (params.containsKey("Equipment Type"))
				addEquipmentEffectsToAbility(ability, (String)params.get("Equipment Type")); //adds equipment effects to ability
			addPassiveEffectsToAbility(ability);
		}
		abilities.add(ability);
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

	private void addEquipmentEffectsToAbility(Ability ability, String equipmentType) {
		for (String key: equipment.keySet()) {
			Equipment item = equipment.get(key);
			if (equipmentType.equals(item.getEquipmentType())) {
				ability.addEffects(item.getEffects("Type", "Ability Modifier"));
			}
			equipItem(item, key);
		}
	}
	
	private void addPassiveEffectsToAbility(Ability ability) {
		Map<String, List<Effect>> newEffects = new HashMap<String, List<Effect>>();
		for (Ability passive: abilities) {
			if (passive.getType().equals("Passive")) {
				Map<String, List<Effect>> passiveEffects = passive.getAllEffects();
				for(String key: passiveEffects.keySet()) {
					List<Effect> effectList = passiveEffects.get(key);
					List<Effect> newEffectList = new ArrayList<Effect>();
					for (Effect effect: effectList) {
						if (ability.hasParam("Equipment Type") && effect.hasParam("Equipment Type")) {
							if (ability.get("Equipment Type").equals(effect.get("Equipment Type"))) {
								newEffectList.add(effect);
							}
						}
						if (ability.hasParam("Element Type") && effect.hasParam("Element Type")) {
							if (ability.get("Element Type").equals(effect.get("Element Type"))) {
								newEffectList.add(effect);
							}
						}
					}
					newEffects.put(key, newEffectList);
				}
			}
		}
		ability.addEffects(newEffects);
		//for ability passive abilities
		//	if passive type = passive
		//		for each effect
		//			if ability has element type
		//				if ability.element type = passive.element type
		//					ability.addeffect
		//			if ability has equipment type
		//				if equipment type = passive.equipment type
		//					ability.addeffect

	}
	
	private void addEquipmentEffectsToUnit() {
		for (String key: equipment.keySet()) {
			Equipment item = equipment.get(key);
			addEffects(item.getEffects("Type", "Permanent Stat Self Modifier"));
		}	
	}
	
	private Behavior makeBehavior(String type) {
		return Behavior.makeBehavior(type, this);
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
	
	public List<Ability> getActiveAbilities() {
		List<Ability> leveledAbilities = new ArrayList<Ability>();
		for (Ability ability: abilities) {
			if (!ability.isSkill() || ability.getLevel() >= 0 && ability.isActive()) {
				leveledAbilities.add(ability);
			}
		}
		return leveledAbilities;
	}
	
//	public List<Ability> getPassiveAbilities() {
//		List<Ability> leveledAbilities = new ArrayList<Ability>();
//		for (Ability ability: abilities) {
//			if (!ability.isSkill() || ability.getLevel() >= 0 && ability.isPassive()) {
//				leveledAbilities.add(ability);
//			}
//		}
//		return leveledAbilities;
//	}
	
//	public int calculateDamage() {
//		return stats.getStrength();
//	}
	
	public int getCurrStrength() {
		return currStats.strength;
	}
	
	public int getCurrMagic() {
		return currStats.magic;
	}
	
	public int getPotentialStrength() {
		return potentialStats.strength;
	}

	public int getPotentialMagic() {
		return potentialStats.magic;
	}
	
	public int getCurrInitiative() {
		return currStats.initiative;
	}
	
	public void takeDamage(int damage) {
		currStats.HP -= damage;
		checkDeath();
	}

	public int getCurrHP() {
		return currStats.HP;
	}
	
	public int getPotentialHP() {
		return potentialStats.HP;
	}
	
	public int getCurrMP() {
		return currStats.MP;
	}

	public int getHP() {
		return stats.HP;
	}
	
	public int getMP() {
		return stats.MP;
	}
	
	public int getCurrMovement() {
		return currStats.movement;
	}

	public int getMovement() {
		return stats.movement;
	}
	
	public int getStrength() {
		return stats.strength;
	}

	public int getMagic() {
		return stats.magic;
	}
	
	public int getInitiative() {
		return stats.initiative;
	}
		
	public void checkDeath() {
		if (currStats.HP <= 0) 
			die();
	}
	
	public void die() {
		isDying = true;
	}
	
	public boolean willDie(int damage) {
		if (currStats.HP <= damage)
			return true;
		return false;
	}
	
	public String getFaction() {
		return faction;
	}
	
	public void endTurn() {
		currStats.movement = stats.movement;
		ordering += currStats.initiative;
		removeEffects();
	}
	
	public void beginTurn(BattleMap map) {
		int staminaRegen = currStats.initiative;
		if (statuses.get("Stamina Regen") != null) {
			List<Effect> effectList = statuses.get("Stamina Regen");
			for (Effect effect: effectList) {
				staminaRegen = (int)(effect.modifyValue((double)staminaRegen, this));
			}
		}		
		currStats.stamina += staminaRegen;
		if (currStats.stamina > stats.stamina)
			currStats.stamina = stats.stamina;
		applyCurrentEffects(map);
	}

	private void removeEffects() {
		Map<String, List<Effect>> tempEffects = new HashMap<String, List<Effect>>();
		for(String key: statuses.keySet()) {
			List<Effect> effectList = statuses.get(key);
			List<Effect> removeList = new ArrayList<Effect>();
			for(Effect effect: effectList) {
				if (effect.hasValue("Modify Duration Type", "Turn")) {
					int duration = (int)effect.get("Modify Duration");
					if (duration <= 1) {
						removeList.add(effect);
					} else {
						effect.set("Modify Duration", duration - 1);
					}
				}
			}
			effectList.removeAll(removeList);
//			if (effectList.size() == 0)
//				statuses.remove(key);
//			else 
			tempEffects.put(key, effectList); //could have weird empty list issues
		}
		statuses = tempEffects;
	}
	
	private void removeInstantEffects() {
		Map<String, List<Effect>> tempEffects = new HashMap<String, List<Effect>>();
		for(String key: statuses.keySet()) {
			List<Effect> effectList = statuses.get(key);
			List<Effect> removeList = new ArrayList<Effect>();
			for(Effect effect: effectList) {
				if (effect.hasValue("Modify Duration Type", "Turn")) {
					int duration = (int)effect.get("Modify Duration");
					if (duration < 1) {
						removeList.add(effect);
					}
				}
			}
			effectList.removeAll(removeList);
//			if (effectList.size() == 0)
//				statuses.remove(key);
//			else 
			tempEffects.put(key, effectList); //could have weird empty list issues
		}
		statuses = tempEffects;
	}

	public void subractMovement(int movementUsed) {
		currStats.stamina -= movementUsed * 5;
		currStats.movement -= movementUsed;
	}
	
	public int getOrdering() {
		return ordering;
	}
	
	public void setOrdering(int ordering) {
		this.ordering = ordering;
	}

	public String getName() {
		return name;
	}
	
	public void markMovementPriority(int[][] movePriorityMap, final int[][] movementMap, Unit unit) {
		behavior.markMovementPriority(movePriorityMap, movementMap, unit, abilities);
	}

	public int getCurrStamina() {
//		if (statuses.get("Current Stamina") != null) {
//			Effect effect = statuses.get("Current Stamina");
//			return (int)effect.modifyValue((double)currStats.stamina, this, currAbility);
//		}
		return currStats.stamina;
	}
	
	public int getPotentialStamina() {
		return potentialStats.stamina;
	}
	
	public int getPotentialMP() {
		return potentialStats.MP;
	}
	
	public int getStamina() {
		return stats.stamina;
	}

	public void useAbility(Ability ability) {
		this.setCurrAbility(ability);
//		this.setStatSelfModfierEffects(ability);
		currStats.stamina -= ability.getStamCost(this);
		currStats.MP -= ability.getMPCost(this);
	}
	
	public void toggleTakingDamage() {
		if (takingDamage) takingDamage = false;
		else takingDamage = true;
	}

	public boolean isTakingDamage() {
		return takingDamage;
	}

	public boolean isDying() {
		return isDying;
	}
	
	public void setDying(boolean bool) {
		isDying = bool;
	}

	public boolean isEnemy(Unit unit) {
		if (this.faction.equals(unit.faction)) return false;
		return true;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public Stats getPerLevelStats() {
		return perLevelStats;
	}
	
	public List<Ability> getSkillList() {
		List<Ability> skillList = new ArrayList<Ability>();
		for (Ability ability: abilities) {
			if (ability.isSkill())
				skillList.add(ability);
		}
		return skillList;
	}
	
	public List<Equipment> getEquipmentList() {
		List<Equipment> equipmentList = new ArrayList<Equipment>();
		for (String key: equipment.keySet()) {
			Equipment item = equipment.get(key);
			equipmentList.add(item);
		}
		return equipmentList;
	}

	public static void setOrdering(List<Unit> unitList) {
		Random random = new Random();
		for (Unit unit: unitList) {
			unit.setOrdering(random.nextInt(unit.getInitiative()));
		}
//		unitList.sort(UnitQueue.buildComparator());
//		for (int i = 0; i < unitList.size(); i++) {
//			unitList.get(i).setOrdering(i * (unit.));
//		}
	}
	
//	public void setStatSelfModfierEffects(Ability ability) {
//		Map<String, Effect> effects = ability.getEffects("Type", "Stat Self Modifier");
//		if (effects.size() > 0)
//			statuses.putAll(effects);
//		//maybe should just make it modify stats here?
//		//main issue is: stamina is capped but cost is not
//	}
	
	public void setCurrAbility(Ability ability) {
//		this.currAbility = ability;
		currAbility = potentialAbility;
		currStats = potentialStats;
	}

	public void setPotentialAbility(Ability ability) {
		this.potentialAbility = ability;
		this.potentialStats = new Stats(currStats);
		Map<String, List<Effect>> effects = ability.getEffects("Type", "Stat Self Modifier");		
		
		effects = Effect.getEffects(effects, "Subtype", "Current");
		for (String key: effects.keySet()) {
			List<Effect> effectList = effects.get(key);
//			if (effectList.size() <= 0) continue;
			int value = currStats.get((String)effectList.get(0).get("Key"));
			for (Effect effect: effectList) {
				value = (int)effect.modifyValue((double)value, this);
			}
			potentialStats.set((String)effectList.get(0).get("Key"), value);
			if (potentialStats.stamina >= stats.stamina) //this is "okay" because only some stats will get capped. probably will need to add for hp/mana as well
				potentialStats.stamina = stats.stamina;
		}
//		effects = ability.getEffects("Type", "Permanent Stat Self Modifier");
//		for (String key: effects.keySet()) {
//			List<Effect> effectList = effects.get(key);
//			int value = stats.get((String)effectList.get(0).get("Key"));
//			for (Effect effect: effectList) {
//				value = (int)effect.modifyValue((double)value, this);
//			}
//			potentialStats.set((String)effectList.get(0).get("Key"), value);
//			if (potentialStats.stamina >= stats.stamina) //this is "okay" because only some stats will get capped. probably will need to add for hp/mana as well
//				potentialStats.stamina = stats.stamina;
//		}
	}
	
	public void addEffects(Map<String, List<Effect>> effects) {
		for(String key: effects.keySet()) {
			List<Effect> effectList = new ArrayList<Effect>();
			if (statuses.containsKey(key)) {
				effectList = statuses.get(key);
			}
			effectList.addAll(effects.get(key));
			statuses.put(key, effectList);
		}
	}
	
	public void wipePotentialAbility() {
		potentialAbility = null;
		potentialStats = null;
	}

	public boolean hasPotentialStats() {
		if (potentialStats == null) return false;
		return true;
	}
	
	public void equip(Equipment item) {
		String slot = (String)item.get("Slot");
		Equipment oldItem = getItemEquipped(slot);
		unequip(slot);
		removeEquipmentEffects(oldItem);
		addEquipmentEffects(item);
//		if (equipmentType.equals(item.getEquipmentType())) {
//			ability.addEffects(item.getEffects("Type", "Ability Modifier"));
//		}
		equipItem(item, slot);
		//need to equip the item
	}

	private Equipment getItemEquipped(String slot) {
		return equipment.get(slot);
	}
	
	//ideally this should be reading the slot from the item
	private void equipItem(Equipment item, String slot) {
		equipment.put(slot, item);
	}
	
	private void unequip(String slot) {
		equipment.remove(slot);
	}
	
	private void removeEquipmentEffects(Equipment oldItem) {
		Map<String, List<Effect>> tempEffects = new HashMap<String, List<Effect>>();
		for (String key: statuses.keySet()) {
			List<Effect> effectList = statuses.get(key);
			List<Effect> removeList = new ArrayList<Effect>();
			for (Effect effect: effectList) {
				if (effect.getEquipment() != null && effect.getEquipment() == oldItem) {
					removeList.add(effect);
				}
			}
			effectList.removeAll(removeList);
			tempEffects.put(key, effectList);
		}
		statuses = tempEffects;
		for (Ability ability: abilities) {
			tempEffects = new HashMap<String, List<Effect>>();
			Map<String, List<Effect>> effects = ability.getAllEffects();
			for (String key: effects.keySet()) {
				List<Effect> effectList = effects.get(key);
				List<Effect> removeList = new ArrayList<Effect>();
				for (Effect effect: effectList) {
					if (effect.getEquipment() != null && effect.getEquipment() == oldItem) {
						removeList.add(effect);
					}
				}
				effectList.removeAll(removeList);
				tempEffects.put(key, effectList);
			}
			effects = tempEffects;
		}
	}
	
	private void addEquipmentEffects(Equipment item) {
//		for (String key: statuses.keySet()) {
//			List<Effect> effectList = statuses.get(key);
//			for (Effect effect: effectList) {
//				if (effect.hasValue("Type", "Permanent Stat Self Modifier")) {
//					effectList.add(effect);
//				}
//			}
//			statuses.put(key, effectList);
//		}
//		for (Ability ability: abilities) {
//			Map<String, List<Effect>> effects = ability.getAllEffects();
//			for (String key: effects.keySet()) {
//				List<Effect> effectList = effects.get(key);
//				for (Effect effect: effectList) {
//					if (effect.hasValue("Type", "Ability Modifier")) {
//						effectList.add(effect);
//					}
//				}
//				effects.put(key, effectList);
//			}
//		}
		addEffects(item.getEffects("Type", "Permanent Stat Self Modifier"));
		for (Ability ability: abilities) {
			if (ability.getEquipmentType().equals(item.getEquipmentType()))
				ability.addEffects(item.getEffects("Type", "Ability Modifier"));
		}
	}
	
	public int getMovementCost() {
		int cost = movementCost;
		if (statuses.get("Movement Stamina Cost") != null) {
			List<Effect> effectList = statuses.get("Movement Stamina Cost");
			for (Effect effect: effectList) {
				cost = (int)(effect.modifyValue((double)cost, this));
			}
		}		
		return cost;
	}

	public Map<String, List<Effect>> getAllEffects() {
		return statuses;	
	}
	
	public void applyCurrentEffects(BattleMap map) {
		Map<String, List<Effect>> effects = Effect.getEffects(statuses, "Type", "Stat Self Modifier");
		Effect.addEffects(effects, Effect.getEffects(statuses, "Type", "Stat Enemy Modifier"));
		for (String key: effects.keySet()) {
			List<Effect> effectList = effects.get(key);
			for (Effect effect: effectList) {
				final int prevValue = currStats.get((String)effect.get("Key"));
				int value = currStats.get((String)effect.get("Key"));
				value = (int)effect.modifyValue((double)value, this);
				currStats.set((String)effect.get("Key"), Math.round(value));
				if (key.equals("HP") && prevValue != value) {
					checkDeath();
					map.setDamageDealt(prevValue - value);
					map.attackedAnimation(this);
				}
			}
		}
	}
	
	public void applyInstantEffects() {
		Map<String, List<Effect>> effects = Effect.getEffects(statuses, "Type", "Stat Self Modifier");
		Effect.addEffects(effects, Effect.getEffects(statuses, "Type", "Stat Enemy Modifier"));
		effects = Effect.getEffects(effects, "Modify Duration", 0);
		for (String key: effects.keySet()) {
			List<Effect> effectList = effects.get(key);
			for (Effect effect: effectList) {
				int value = currStats.get((String)effect.get("Key"));
				value = (int)effect.modifyValue((double)value, this);
				currStats.set((String)effect.get("Key"), value);
			}
		}
		removeInstantEffects();
	}
	
	public String getFilename() {
		return filename;
	}
	
	public void updateStats() {
		stats = new Stats(permStats);
		currStats = new Stats(permStats);
	}

	public int getAcquisitionRange(String string) {
		return acquisitionRange;
	}
}
