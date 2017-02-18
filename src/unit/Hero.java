package unit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import event.Effect;
import event.Equipment;

public class Hero extends Unit {

	private Map<String, Integer> statToBonusLevel;
	private Stats baseStats;
	private int exp;
	private int totalExp;
	private int toNextLevel;
	private int statPoints;
	
	//hero should override some functions, esp those that set current stats. Instead the hero should calc his currStats values taking bonusStats into account
	public Hero(String filename, String faction, String name, Party party) {
		super(new UnitFactory(filename, party));
		this.name = name;
		this.faction = faction;
		level = 0;
		exp = 0;
		totalExp = 0;
		baseStats = new Stats(permStats);
		statToBonusLevel = new HashMap<String, Integer>();
		updateStats();
		toNextLevel = 1000;
	}
	
//	public Hero(UnitFactory unitFactory) {
//		super(unitFactory);
//		level = 0;
//		exp = 0;
//		totalExp = 0;
//		baseStats = new Stats(permStats);
//		statToBonusLevel = new HashMap<String, Integer>();
//		updateStats();
//		toNextLevel = 1000;
//	}

	public void addExp(int expReward) {
		exp += expReward;
		totalExp += expReward;
		checkLevelUp();
	}
	
	private void checkLevelUp() {
		while (exp >= toNextLevel) {
			levelUp();
		}
	}
	
	private void levelUp() {
		level++;
		statPoints += toNextLevel;
		exp -= toNextLevel;
		toNextLevel *= 1.25;
		updateStats();
		System.out.println("Hero Leveled to level " + level);
		System.out.println("EXP to next level " + toNextLevel);
	}

	private void calculatePermStats() {
		permStats = new Stats(baseStats);
		for (int i = 0; i < level; i++) {
			permStats.incrementStats(permStats, perLevelStats);
		}
		for (String stat: statToBonusLevel.keySet()) {
			permStats.add(stat, perLevelStats.get(stat)*statToBonusLevel.get(stat));
		}
		Map<String, List<Effect>> effects = Effect.getEffects(statuses, "Type", "Permanent Stat Self Modifier");
		for (String key: effects.keySet()) {
			List<Effect> effectList = effects.get(key);
			for (Effect effect: effectList) {
				permStats.set(key, (int)effect.modifyValue((double)permStats.get(key), this));
			}
		}
	}
	
	public Stats calculateNextLevelStats() {
		Stats tempStats = new Stats(baseStats);
		for (int i = 0; i < level + 1; i++) { //won't work if you divorce level up stats for paying for stats
			tempStats.incrementStats(tempStats, perLevelStats);
		}
		for (String stat: statToBonusLevel.keySet()) {
			tempStats.add(stat, perLevelStats.get(stat)*(statToBonusLevel.get(stat)));
		}
		Map<String, List<Effect>> effects = Effect.getEffects(statuses, "Type", "Permanent Stat Self Modifier");
		for (String key: effects.keySet()) {
			List<Effect> effectList = effects.get(key);
			for (Effect effect: effectList) {
				tempStats.set(key, (int)effect.modifyValue((double)tempStats.get(key), this));
			}
		}
		return tempStats;
	}
	
	public void updateStats() {
		calculatePermStats();
//		currStats = new Stats(permStats);
//		stats = new Stats(permStats);
	}
	
//	public void prepUnit() {
//		currStats = new Stats(permStats);
//		stats = new Stats(permStats);
//	}
	
	public int getToNextLevel() {
		return toNextLevel;
	}

	public int getStatPoints() {
		return statPoints;
	}
	
	public void subtractStatPoints(int cost) {
		statPoints -= cost;
	}

	public int getExp() {
//		updateStats(); //this is actually for reverting stats to permStats after a battle
		return exp;
	}
	
	public void levelStat(String type) {
		int level = 1;
		if (statToBonusLevel.get(type) != null)
			level += statToBonusLevel.get(type);
		System.out.println("level" + level);
		statToBonusLevel.put(type, level);
		updateStats();
	}
	
	public int getStatLevel(String stat) {
		if (statToBonusLevel.get(stat) != null)
			return statToBonusLevel.get(stat);
		return 0;
	}
	
//	public Hero respec() {
//		Hero hero = new Hero(filename, faction, name, party);
//		hero.addExp(totalExp);
//		return hero;
//	}
	
	public void setEquipment(Map<String, Equipment> equipment) {
		this.equipment = equipment;
	}
}
