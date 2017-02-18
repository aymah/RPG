package unit;

import org.json.*;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import javax.imageio.ImageIO;

import map.BattleMap;
import map.Coordinates;
import map.TileMap;
import event.Ability;
import event.Armor;
import event.Corridor;
import event.Effect;
import event.Encounter;
import event.Equipment;
import event.Event;
import event.Item;

public class Unit implements Serializable {

	private OrderOfBattle orderOfBattle;
	protected Stats permStats;
	protected Stats stats;
	protected Stats currStats;
	protected Stats potentialStats;
	protected Stats perLevelStats;
	protected int level;
	protected int constitution;
	protected int leadership;
	protected int posIndexX;
	protected int posIndexY;
	protected int supplyCost;
	protected List<Ability> abilities;
	protected Map<String, List<Effect>> statuses;
	protected Map<String, Equipment> equipment; //map of slot to equipment
	protected Ability currAbility;
	protected Ability potentialAbility;
	protected String faction;
//	protected int ordering;
	protected String name;
	protected String type;
	protected Behavior behavior;
	protected boolean takingDamage;
	protected boolean isDying;
	protected int movementCost = 5;
	protected Party party;
	protected int targetPriority;
	protected boolean isFlying;
	protected transient BufferedImage standingImage;
	protected transient BufferedImage moveAnimationImages;
	protected Squad squad;
	protected boolean hasRevert = false;
	protected TempStats tempStats;
	protected int simMeeleAttacks = 0;
	protected boolean simMeeleSpace = false;
	
	public Unit(UnitFactory unitFactory) {
		level = 0;
		manufacture(unitFactory);
	}
	
	public Unit(UnitFactory unitFactory, String faction, String name, int level) {
		this.faction = faction;
		this.name = name;
		this.level = level;
		manufacture(unitFactory);
	}
	
	private void manufacture(UnitFactory unitFactory) {
		if (level == 0)
			level = unitFactory.getLevel();
		perLevelStats = new Stats(unitFactory.getPerLevelStats());
		permStats = new Stats(unitFactory.getStats(), perLevelStats, level);
		stats = new Stats(permStats);
		currStats = new Stats(permStats);
		constitution = unitFactory.getConstitution();
		leadership = unitFactory.getLeadership();
		supplyCost = unitFactory.getSupplyCost();
		abilities = unitFactory.getAbilities(); //make sure this function returns a new copy
		statuses = unitFactory.getStatuses(); //make sure this function returns a new copy
		equipment = unitFactory.getEquipment();
		type = unitFactory.getType();
		behavior = unitFactory.getBehavior();
		isFlying = unitFactory.isFlying();
		String imageName = unitFactory.getImageName();
		try {
			String prefix = "";
			if (this.getClass() != Hero.class)
				imageName += faction;
			standingImage = ImageIO.read(getClass().getResourceAsStream("/testUnits/sprites/" + imageName + ".gif"));
			moveAnimationImages = ImageIO.read(getClass().getResourceAsStream("/testUnits/sprites/" + imageName + "MoveAnimations.gif"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	public Unit(String filename, String faction, String name, int level) {
//		this.filename = filename;
//		this.faction = faction;
//		this.name = name;
//		this.statuses = new HashMap<String, List<Effect>>();
//		this.level = level;
//		isFlying = false;
//		this.permStats = new Stats(permStats, perLevelStats, level);
//		this.stats = new Stats(permStats);
//		this.currStats = new Stats(permStats);
//		Random rand = new Random();
//		takingDamage = false;
//		isDying = false;
//	}
//	
//	public Unit(String filename, String faction, String name, Party party) {
//		this.filename = filename;
//		this.faction = faction;
//		this.name = name;
//		this.statuses = new HashMap<String, List<Effect>>();
//		this.party = party;
//		isFlying = false;
//		level = 0;
////		party.loadUnitStats(type, stats, perLevelStats);
//		this.permStats = new Stats(permStats, perLevelStats, level);
//		this.stats = new Stats(permStats);
//		this.currStats = new Stats(permStats);
//		Random rand = new Random();
//		takingDamage = false;
//		isDying = false;
//	}
	
//	public Unit(Stats stats, String faction, String name) {
//		this.stats = stats;
//		this.currStats = new Stats(stats);
//		this.faction = faction;
//		this.name = name;
//		this.ordering = stats.initiative;
//	}
	


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
		if (currStats.HP >= stats.HP)
			currStats.HP = stats.HP;
		checkDeath();
	}
	
	public void takeArmorDamage(int damage) {
		List<Armor> armors = getArmorList();
		for (Armor armor: armors) {
			armor.subtractDurability(damage);
		}
	}

	public int getCurrHP() {
		return currStats.HP;
	}
	
	public void setCurrHP(int hp) {
		currStats.HP = hp;
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
//		ordering += currStats.initiative;
		countDownTurnEffects();
	}
	
	public void beginSquadTurn(BattleMap map) {
		for (Unit unit: squad.getUnitList())
			unit.beginTurn(map);
	}
	
	private void beginTurn(BattleMap map) {
		regenStamina();
		applyCurrentEffects(map);
		if (isStunned()) {
			map.setDamageDealt(0);
			map.attackedAnimation(this);
			map.endTurn();
		}
	}
	
	public boolean isStunned() {
		if (Effect.getEffects(getAllEffects(), "Status", "Stunned").size() > 0)
			return true;
		return false;
	}

	private void regenStamina() {
		currStats.stamina += calculateStaminaRegen();
		if (currStats.stamina > stats.stamina)
			currStats.stamina = stats.stamina;
	}
	
	public int calculateStaminaRegen() {
		int staminaRegen = currStats.initiative;
		if (statuses.get("Stamina Regen") != null) {
			List<Effect> effectList = statuses.get("Stamina Regen");
			for (Effect effect: effectList) {
				staminaRegen = (int)(effect.modifyValue((double)staminaRegen, this));
			}
		}		
		return staminaRegen;
	}

	private void countDownTurnEffects() {
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

	public void subtractMovement(int movementUsed) {
		int movementStaminaCost = 5;
		if (statuses.get("Movement Stamina Cost") != null) {
			List<Effect> effectList = statuses.get("Movement Stamina Cost");
			effectList.sort(Effect.getComparator());
			for (Effect effect: effectList) {
				movementStaminaCost = (int)effect.modifyValue((double)movementStaminaCost, this);
			}
		}
		currStats.stamina -= movementUsed * movementStaminaCost;
		currStats.movement -= movementUsed;
	}
	
	public void tempSubtractMovement(int movementUsed) {
		currStats.movement -= movementUsed;
	}
	public void tempReverseMovement(int movementUsed) {
		currStats.movement += movementUsed;
	}
	
//	public int getOrdering() {
//		return ordering;
//	}
//	
//	public void setOrdering(int ordering) {
//		this.ordering = ordering;
//	}

	public String getName() {
		return name;
	}
	
//	public void markMovementPriority(int[][] movePriorityMap, final int[][] movementMap, TileMap tileMap) {
//		behavior.markMovementPriority(movePriorityMap, movementMap, tileMap, orderOfBattle, this);
//	}

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
		equipmentList.sort(Equipment.getComparator());
		return equipmentList;
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
				System.out.println("effect " + effect.get("Name") + " " + value);
				System.out.println("HP before " + currStats.getHP());
				currStats.set((String)effect.get("Key"), Math.round(value));
				System.out.println("HP after " + currStats.getHP());
				if (key.equals("HP") && prevValue != value) {
					checkDeath();
					map.setDamageDealt(prevValue - value);
					map.attackedAnimation(this);
				}
			}
		}
		if (currStats.HP >= stats.HP)
			currStats.HP = stats.HP;
		if (currStats.stamina >= stats.stamina)
			currStats.stamina = stats.stamina;
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
		if (currStats.HP >= stats.HP)
			currStats.HP = stats.HP;
		if (currStats.stamina >= stats.stamina)
			currStats.stamina = stats.stamina;
	}

	public void healUnit() {
		stats = new Stats(permStats);
		currStats = new Stats(permStats);
	}
	
	public void updateLevel(UnitFactory unitFactory) {
		level = unitFactory.getLevel();
		permStats = new Stats(unitFactory.getStats(), perLevelStats, level);
		healUnit();
	}
	
	public void clearEffects() {
		Map<String, List<Effect>> tempEffects = new HashMap<String, List<Effect>>();
		for(String key: statuses.keySet()) {
			List<Effect> effectList = statuses.get(key);
			List<Effect> removeList = new ArrayList<Effect>();
			for(Effect effect: effectList) {
				if (effect.hasParam("Modify Duration Type"))
					removeList.add(effect);
			}
			effectList.removeAll(removeList);
			tempEffects.put(key, effectList);
		}
		statuses = tempEffects;
	}

//	public void aggro() {
//		if (group != null && behavior.getName().equals("Defend")) {
//			group.setBehavior("Attack");
//		}
//	}
	
//	public void setBehavior(Behavior behavior) {
//		this.behavior = behavior;
//	}

	public Behavior getBehavior() {
		return behavior;
	}
	
	public int getTargetPriority() {
		return targetPriority;
	}

	public void setTargetPriority(int targetPriority) {
		this.targetPriority = targetPriority;
	}

	public boolean isFlying() {
		return isFlying;
	}

//	public void setRandomOrdering() {
//		Random random = new Random();
//		setOrdering(random.nextInt(getInitiative()));
//	}

	public BufferedImage getImage() {
		return standingImage;
	}
	
	public BufferedImage getMoveAnimationImages() {
		return moveAnimationImages;
	}

	public double getTotalAbsorption() {
		List<Armor> armors = getArmorList();
		double absorption = 0;
		for (Armor armor: armors) {
			absorption += armor.getAbsorption();
		}
		return absorption;
	}
	
	public int getDurability() {
		List<Armor> armors = getArmorList();
		int durability = 0;
		for (Armor armor: armors) {
			durability += armor.getDurability();
		}
		return durability;
	}
	
	public int getMaxDurability() {
		List<Armor> armors = getArmorList();
		int durability = 0;
		for (Armor armor: armors) {
			durability += armor.getMaxDurability();
		}
		return durability;
	}
	
	public List<Armor> getArmorList() {
		List<Armor> armors = new ArrayList<Armor>();
		for (String key: equipment.keySet()) {
			Equipment item = equipment.get(key);
			if (item.getClass() == Armor.class)
				armors.add((Armor)item);
		}
		armors.sort(new Comparator<Armor>() {
			@Override
			public int compare(Armor o1, Armor o2) {
				return o2.getMaxDurability() - o1.getMaxDurability();
			}
		});
		return armors;
	}
	
	public int getArmorScore() {
		List<Armor> armors = getArmorList();
		double armorScore = 0;
		for (Armor armor: armors) {
			armorScore += armor.getDurability() * (armor.getAbsorption() * 100);
		}
		return (int)armorScore;
	}
	
	public void restoreArmor() {
		List<Armor> armors = getArmorList();
		for (Armor armor: armors) {
			armor.resetDurability();
		}
	}
	
	public void setSquad(Squad squad) {
		this.squad = squad;
	}
	
	public Squad getSquad() {
		return squad;
	}
	
	public int getLeadership() {
		return leadership;
	}

	public boolean inLeadership() {
		Unit leader = squad.getLeader();
		return BattleMap.inRange(leader, leader.getLeadership(), posIndexY, posIndexX);
	}
	
	public static Unit cloneUnit(Unit unit) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(unit);
			oos.flush();
			oos.close();
			bos.close();
			byte[] byteData = bos.toByteArray();
			
			ByteArrayInputStream bais = new ByteArrayInputStream(byteData);
			return (Unit)new ObjectInputStream(bais).readObject();
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void saveCurrentStats() {
		if (!hasRevert) {
			hasRevert = true;
			TempStats tempStats = new TempStats(stats, currStats, posIndexY, posIndexX, abilities, statuses, equipment);
//			this.tempStats = tempStats.clone(); //NEED TO IMPLEMENT SOME SORT OF MANUAL CLONE THAT ISN"T SLOW AS FUCK AND HAS THE ABILITY TO REVERT
			this.tempStats = tempStats;
		}
	}
	
	public void revert() {
		if (hasRevert) {
//			System.out.println("reverting!");
			hasRevert = false;
			stats = tempStats.stats;
			currStats = tempStats.currStats;
//			posIndexY = tempStats.posIndexY;
//			posIndexX = tempStats.posIndexX;
			orderOfBattle.moveToPhantomPlane(this);
			orderOfBattle.movePhantomUnit(this, tempStats.posIndexY, tempStats.posIndexX);
			orderOfBattle.moveToRegularPlane(this);
//			abilities = tempStats.abilities;
//			statuses = tempStats.statuses;
			equipment = tempStats.equipment;
			isDying = false;
//			System.out.println("done reverting.");
		}
	}
	
	
	public void resetMeeleAttacks() {
		simMeeleAttacks = 0;
	}
	
	public void incrementMeeleAttacks() {
		simMeeleAttacks++;
	}
	
	public boolean canBeMeeleAttacked() {
		if (simMeeleAttacks >= 2)
			return false;
		return true;
	}
	
	public void setMeeleSpace(boolean bool) {
		simMeeleSpace = bool;
	}
	
	public boolean takesMeeleSpace() {
		return simMeeleSpace;
	}
	
	
	protected class TempStats implements Serializable {
		protected Stats stats;
		protected Stats currStats;
		protected int posIndexX;
		protected int posIndexY;
		protected List<Ability> abilities;
		protected Map<String, List<Effect>> statuses;
		protected Map<String, Equipment> equipment;
		
		public TempStats(Stats stats, Stats currStats, int posIndexY, int posIndexX, List<Ability> abilities, Map<String, List<Effect>> statuses, Map<String, Equipment> equipment) {
			this.stats = new Stats(stats);
			this.currStats = new Stats(currStats);
			this.posIndexY = posIndexY;
			this.posIndexX = posIndexX;
			this.abilities = abilities;
			this.statuses = statuses;
			this.equipment = Equipment.clone(equipment); //the clone function is not working properly
//			this.equipment = equipment;
		}
		
		public TempStats(Stats stats, Stats currStats, int posIndexY, int posIndexX) {
			this.stats = new Stats(stats);
			this.currStats = new Stats(currStats);
			this.posIndexY = posIndexY;
			this.posIndexX = posIndexX;
		}

		public Stats getStats() {
			return stats;
		}

		public Stats getCurrStats() {
			return currStats;
		}

		public int getPosIndexX() {
			return posIndexX;
		}

		public int getPosIndexY() {
			return posIndexY;
		}

		public List<Ability> getAbilities() {
			return abilities;
		}

		public Map<String, List<Effect>> getStatuses() {
			return statuses;
		}

		public Map<String, Equipment> getEquipment() {
			return equipment;
		}
		
		public TempStats clone() {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				ObjectOutputStream oos = new ObjectOutputStream(bos);
				oos.writeObject(this);
				oos.flush();
				oos.close();
				bos.close();
				byte[] byteData = bos.toByteArray();
				ByteArrayInputStream bais = new ByteArrayInputStream(byteData);
				return (TempStats) new ObjectInputStream(bais).readObject();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

	}


	public int getSupplyCost() {
		return supplyCost;
	}
	
	public Stats getPermStats() {
		return permStats;
	}

	public void useItem(Item item) {
		if (item.getType().equals("Consumable")) {
			if (item.get("Consumable Type").equals("Healing")) {
				currStats.HP += (int)item.get("Healing Amount");
				if (currStats.HP > permStats.HP)
					currStats.HP = permStats.HP;
			}
		}
	}
}
