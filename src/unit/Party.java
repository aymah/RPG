package unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import event.Effect;

public class Party {
	
	List<Unit> unitList;
	int gold;
	int partySize;
	Map<String, Integer> typeToLevel;
	Map<String, Stats> typeToStats;
	Map<String, Stats> typeToStatsPerLevel;
	Map<String, List<Effect>> partyEffects;
	
	public Party() {
		unitList = new ArrayList<Unit>();
		typeToLevel = new HashMap<String, Integer>();
		typeToStats = new HashMap<String, Stats>();
		typeToStatsPerLevel = new HashMap<String, Stats>();
		partyEffects = new HashMap<String, List<Effect>>();
		partySize = 4;
		Unit unit = new Unit("testManAtArms1", "ALLY", "M", 0);
		loadUnitStats(unit.getType(), unit.permStats, unit.perLevelStats);
		unit = new Unit("testArcher1", "ALLY", "M", 0);
		loadUnitStats(unit.getType(), unit.permStats, unit.perLevelStats);
		unit = new Unit("testKnight1", "ALLY", "M", 0);
		loadUnitStats(unit.getType(), unit.permStats, unit.perLevelStats);
	}

	public List<Unit> getUnitList() {
		return unitList;
	}
	
	public List<Hero> getHeroList() {
		List<Hero> heroList = new ArrayList<Hero>();
		for (Unit unit: unitList) {
			if (Hero.class == unit.getClass())
				heroList.add((Hero)unit);
		}
		return heroList;
	}
	
	public void addUnit(Unit unit) {
		unitList.add(unit);
	}
	
	public void removeUnit(String type) {
		for (Unit unit: unitList) {
			if (unit.getName().equals(unit.getType().substring(0, 1) + getNumberOfType(type))) {
				unitList.remove(unit);
				return;
			}
		}
	}

	public Unit getUnit(int partyIndex) {
		if (partyIndex < unitList.size())
			return unitList.get(partyIndex);
		return null;
	}

	public void addGold(int goldReward) {
		gold += goldReward;
	}
	
	public void subtractGold(int goldReward) {
		gold -= goldReward;
	}
	
	public int getGold() {
		return gold;
	}
	
	public int getLevel(String type) {
		if (typeToLevel.get(type) != null)
			return typeToLevel.get(type);
		return 0;
	}
	
	public int getLevel(Unit unit) {
		return getLevel(unit.getType());
	}
	
	public void incrementLevel(String type) {
		int level = 1;
		if (typeToLevel.get(type) != null)
			level += typeToLevel.get(type); 
		typeToLevel.put(type, level);
	}
	
	public void incrementLevel(Unit unit) {
		incrementLevel(unit.getType());
	}
	
	public void loadUnitStats(String type, Stats stats, Stats statsPerLevel) {
		typeToStats.put(type, stats);
		typeToStatsPerLevel.put(type, statsPerLevel);
	}
	
	public Stats getStats(String type) {
		return typeToStats.get(type);
	}
	
	public Stats getStatsPerLevel(String type) {
		return typeToStatsPerLevel.get(type);
	}

	public int getMaxPartySize() {
		int size = partySize;
		if (partyEffects.get("Party Size") != null) {
			List<Effect> effectList = partyEffects.get("Party Size");
			effectList.sort(Effect.getComparator());
			for (Effect effect: effectList) {
				size = (int)(effect.modifyValue((double)size, null));
			}
		}
		return size;
	}
	
	public int getPartySize() {
		return unitList.size();
	}
	
	public int getNumberOfType(String type) {
		int counter = 0;
		for (Unit unit: unitList) {
			if (unit.getType().equals(type))
				counter++;
		}
		return counter;
	}
	
	public void addEffects(Map<String, List<Effect>> effects) {
		Effect.addEffects(partyEffects, effects);
	}

	public void updateUnitLevels() {
		List<Unit> removeUnitList = new ArrayList<Unit>();
		List<Unit> newUnitList = new ArrayList<Unit>();
		for (Unit unit: unitList) {
			if (!unit.getType().equals("Hero")) {
				removeUnitList.add(unit);
				Unit newUnit = new Unit(unit.getFilename(), unit.getFaction(), unit.getName(), getLevel(unit.getType()));
				newUnitList.add(newUnit);
			}
		}
		unitList.removeAll(removeUnitList);
		unitList.addAll(newUnitList);
	}
	
	public int getPartyValue() {
		int value = 0;
		for (Unit unit: unitList) {
			value += getUnitValue(unit.getType());
		}
		return value;
	}
	
	public int getUnitValue(String type) {
		switch (type) {
			case "Man At Arms":
				return 1;
			case "Archer":
				return 1;
			case "Knight":
				return 2;
			case "Hero":
				return 1;
			default:
				return 0;
		}
	}

	
	public void endBattleStatUpdates() {
		for (Unit unit: unitList) {
			unit.setDying(false);
			unit.updateStats();
		}
	}
}
