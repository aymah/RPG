package unit;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import misc.Factory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import unit.Unit.TempStats;
import event.Ability;
import event.Armor;
import event.Effect;
import event.Equipment;

public class UnitFactory extends Factory implements Serializable {
	
	protected Stats stats;
	protected Stats perLevelStats;
	protected int constitution;
	protected int leadership;
	protected int supplyCost;
	protected int level;
	protected List<Ability> abilities;
	protected Map<String, List<Effect>> statuses;
	protected Map<String, Equipment> equipment; //map of slot to equipment
	protected String name;
	protected String type;
	protected Behavior behavior;
	protected int movementCost = 5;
	protected boolean isFlying;
	protected String imageName;
	protected Party party;

	public UnitFactory(String filename) {
		this.statuses = new HashMap<String, List<Effect>>();
		readUnitFile("/units/" + filename);
		level = 0;
	}
	
	public UnitFactory(String filename, Party party) {
		this.statuses = new HashMap<String, List<Effect>>();
		this.party = party;
		readUnitFile("/heroes/" + filename);
	}
	
	public Unit createInstance(String faction, String name, int level) {
		return new Unit(this, faction, name, level);
	}
	
	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
	    return new String(encoded, encoding);
	}
	
	protected void readUnitFile(String filename) {
		JSONObject unitObject = getJSONObject(filename + ".json"); 
		
		type = unitObject.getString("Class");
//		level = unitObject.getInt("Level");
		constitution = unitObject.getInt("Constitution");
		supplyCost = unitObject.getInt("Supply Cost");
		behavior = makeBehavior(unitObject.getString("Behavior"));
		if (unitObject.has("Leadership"))
			leadership = unitObject.getInt("Leadership");
		else
			leadership = 3;
//		if (unitObject.has("Acquisition Range"))
//			acquisitionRange = unitObject.getInt("Acquisition Range");
		if (unitObject.has("Flying"))
			isFlying = unitObject.getBoolean("Flying");
		if (unitObject.has("Base Stats"))
			makeUnit((JSONObject)unitObject.get("Base Stats"));
		if (unitObject.has("Per Level Stats"))
			makePerLevel((JSONObject)unitObject.get("Per Level Stats"));
		if (unitObject.has("Equipment")) 
			makeEquipment((JSONObject)unitObject.get("Equipment"));
		if (unitObject.has("Abilities")) 
			makeAbilities((JSONArray)unitObject.get("Abilities"));
		if (unitObject.has("Image")) {
			imageName = unitObject.getString("Image");
//			try {
//				standingImage = ImageIO.read(getClass().getResourceAsStream("/testUnits/sprites/" + imageName + ".gif"));
//				moveAnimationImages = ImageIO.read(getClass().getResourceAsStream("/testUnits/sprites/" + imageName + "MoveAnimations.gif"));
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

		}
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
		this.stats = new Stats(HP, MP, strength, magic, movement, initiative, stamina);
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
		JSONObject itemObject = getJSONObject("/items/" + filename + ".json"); 
		Map<String, Object> params = new HashMap<String, Object>();
		for (String key: itemObject.keySet()) {
			if (!key.equals("Effects"))
				params.put(key, itemObject.get(key));
		}
		List<Effect> effects = new ArrayList<Effect>();
		JSONArray effectsList = (JSONArray) itemObject.get("Effects");
		effects = makeEffects(effectsList);
		if (params.get("Equipment Type").equals("Armor")) {
			Armor armor = new Armor(params, effects);
			return armor;
		} else {
			Equipment equipment = new Equipment(params, effects);
			return equipment;
		}
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
			if (!key.equals("Effects") && !key.equals("Area Of Effect"))
				params.put(key, abilityObject.get(key));
			if (key.equals("Area Of Effect")) {
				int aoeHeight = 0;
				int aoeWidth = 0;
				JSONArray jsonRows = (JSONArray) abilityObject.get(key);
				aoeHeight = jsonRows.length();
				aoeWidth = ((JSONArray)jsonRows.get(0)).length();
				int[][] aoe = new int[aoeHeight][aoeWidth];
				for (int y = 0; y < jsonRows.length(); y++) {
					JSONArray jsonRow = (JSONArray) jsonRows.get(y);
					for (int x = 0; x < jsonRow.length(); x++) {
						aoe[y][x] = jsonRow.getInt(x);
					}
				}
				params.put(key, aoe);
			}
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
		for (Ability passive: abilities) {
			Map<String, List<Effect>> newEffects = new HashMap<String, List<Effect>>();
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
			ability.addEffects(newEffects);
		}
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
		return Behavior.makeBehavior(type);
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
	
	private void equipItem(Equipment item, String slot) {
		equipment.put(slot, item);
	}

	public String getType() {
		return type;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void incrementLevel() {
		level++;
	}

	public Stats getStats() {
		return stats;
	}

	public Stats getPerLevelStats() {
		return perLevelStats;
	}

	public int getSupplyCost() {
		return supplyCost;
	}

	public int getConstitution() {
		return constitution;
	}

	public int getLeadership() {
		return leadership;
	}

	public List<Ability> getAbilities() {
		List<Ability> abilities = new ArrayList<Ability>();
		for (Ability ability: this.abilities) {
			abilities.add(ability.copy());
		}
		return abilities;
	}

	public Map<String, List<Effect>> getStatuses() {
		Map<String, List<Effect>> statuses = new HashMap<String, List<Effect>>(); 
		for(String key: this.statuses.keySet()) {
			List<Effect> effectList = new ArrayList<Effect>();
			if (statuses.containsKey(key)) {
				effectList = statuses.get(key);
			}
			for (Effect effect: this.statuses.get(key)) {
				effectList.add(effect.copy());
			}
			statuses.put(key, effectList);
		}
		return statuses;
	}
	
	public Map<String, Equipment> getEquipment() {
		return Equipment.clone(equipment);
	}

	public Behavior getBehavior() {
		return behavior;
	}

	public boolean isFlying() {
		return isFlying;
	}

	public String getImageName() {
		return imageName;
	}

	@Override
	public String getName() {
		return name;
	}


}
