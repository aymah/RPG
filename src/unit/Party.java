package unit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import map.GameStateManager;
import event.Effect;
import event.Item;
import event.ItemFactory;

public class Party implements Serializable {
	
	transient List<Unit> unitList;
	transient Army army;
	Empire empire;
	int gold;
	int totalGold;
	int partySize;
	Map<String, List<Effect>> partyEffects;
	Map<ItemFactory, Integer> itemBag;
	int avatarIndexX;
	int avatarIndexY;
	String mapName;
	GameStateManager gameStateManager;
	
	public Party() {
		gold = 0;
		totalGold = 0;
		unitList = new ArrayList<Unit>();
		partyEffects = new HashMap<String, List<Effect>>();
		itemBag = new HashMap<ItemFactory, Integer>();
		empire = new Empire(this);
		army = new Army(this);
		partySize = 4;
		mapName = "";
	}

	public List<Unit> getUnitList() {
		return unitList;
	}
	
	public List<Squad> getSquadList() {
		List<Squad> squadList = new ArrayList<Squad>();
		for (Hero hero: getHeroList()) {
			squadList.add(new Squad(hero));
		}
		for (Unit unit: unitList) {
			if (unit.getClass() != Hero.class) {
				for (Squad squad: squadList) {
					if (squad.getLeader().getName().equals("Renalt"))
						squad.addUnit(unit);
				}
			}
		}
		return squadList;
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
	
	public Unit removeUnit(String type) {
		for (Unit unit: unitList) {
			if (unit.getType().equals(type)) {
				unitList.remove(unit);
				return unit;
			}
		}
		return null;
	}

	public Unit getUnit(int partyIndex) {
		if (partyIndex < unitList.size())
			return unitList.get(partyIndex);
		return null;
	}

	public void addGold(int goldReward) {
		gold += goldReward;
		totalGold += goldReward;
	}
	
	public void subtractGold(int goldReward) {
		gold -= goldReward;
	}
	
	public int getGold() {
		return gold;
	}
	
//	public int getLevel(String type) {
//		if (typeToLevel.get(type) != null)
//			return typeToLevel.get(type);
//		return 0;
//	}
	
//	public int getLevel(Unit unit) {
//		return getLevel(unit.getType());
//	}
//	
//	public void incrementLevel(String type) {
//		int level = 1;
//		if (typeToLevel.get(type) != null)
//			level += typeToLevel.get(type); 
//		typeToLevel.put(type, level);
//	}
	
//	public void incrementLevel(Unit unit) {
//		incrementLevel(unit.getType());
//	}
//	
//	public void loadUnitStats(String type, Stats stats, Stats statsPerLevel, int supplyCost) {
//		typeToStats.put(type, stats);
//		typeToStatsPerLevel.put(type, statsPerLevel);
//		typeToSupplyCost.put(type, supplyCost);
//	}
	
//	public Stats getStats(String type) {
//		return typeToStats.get(type);
//	}
//	
//	public Stats getStatsPerLevel(String type) {
//		return typeToStatsPerLevel.get(type);
//	}

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
	
	public int getNumUnitsByType(String type) {
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

//	public void updateUnitLevels() {
//		List<Unit> removeUnitList = new ArrayList<Unit>();
//		List<Unit> newUnitList = new ArrayList<Unit>();
//		for (Unit unit: unitList) {
//			if (!unit.getType().equals("Hero")) {
//				removeUnitList.add(unit);
//				Unit newUnit = new Unit(unit.getFilename(), unit.getFaction(), unit.getName(), getLevel(unit.getType()));
//				newUnitList.add(newUnit);
//			}
//		}
//		unitList.removeAll(removeUnitList);
//		unitList.addAll(newUnitList);
//	}
	
	public int getPartyValue() {
		int value = 0;
		for (Unit unit: unitList) {
			value += unit.getSupplyCost();
		}
		return value;
	}
	
//	public int getSupplyCost(String type) {
//		return typeToSupplyCost.get(type);
//	}

	
	public void endBattleStatUpdates() {
//		System.out.println("endBattleStatUpdates");
		List<Unit> tempUnitList = new ArrayList<Unit>(unitList);
		for (Unit unit: tempUnitList) {
			unit.setDying(false);
			if (unit.getCurrHP() <= 0 && unit.getClass() == Hero.class) {
				unit.setCurrHP(1);
			} else if (unit.getCurrHP() <= 0) {
				unitList.remove(unit);
				empire.getUnitList().remove(unit);
			}
//			unit.updateStats(); //causes units to heal after battle
			unit.clearEffects();
		}
	}
	
	public void healUnits() {
		for (Unit unit: unitList) {
			unit.healUnit();
		}
	}

	public int getAvatarIndexX() {
		return avatarIndexX;
	}

	public void setAvatarIndexX(int avatarIndexX) {
		this.avatarIndexX = avatarIndexX;
	}

	public int getAvatarIndexY() {
		return avatarIndexY;
	}

	public void setAvatarIndexY(int avatarIndexY) {
		this.avatarIndexY = avatarIndexY;
	}

	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public GameStateManager getGameStateManager() {
		return gameStateManager;
	}

	public void setGameStateManager(GameStateManager gameStateManager) {
		this.gameStateManager = gameStateManager;
	}
	
	public void resetGold() {
		gold = totalGold;
	}

//	public void respecStatAndGold() {
//		partyEffects = new HashMap<String, List<Effect>>();
//		List<Unit> unitList = new ArrayList<Unit>();
//		for (Hero hero: getHeroList()) {
//			unitList.add(hero.respec());
//		}
//		this.unitList = unitList;
//		typeToLevel = new HashMap<String, Integer>();
////		army.respec();
//		resetGold();
//	}

	public void setArmy(Army army) {
		this.army = army;
	}

	public Army getArmy() {
		return army;
	}

//	public Unit createUnit(String type, int num) {
//		Unit unit = new Unit(type, "ALLY", type + " " + num, getLevel(type));
//		return unit;
//	}

	public Empire getEmpire() {
		return empire;
	}

	public void restoreArmor() {
		for (Unit unit: unitList) {
			unit.restoreArmor();
		}
	}

	public void updateUnitLevels(UnitFactory unitFactory) {
		for (Unit unit: unitList) {
			if (unitFactory.getType().equals(unit.getType())) {
				unit.updateLevel(unitFactory);
			}
		}
	}
	
	public boolean hasItem(ItemFactory item) {
		return itemBag.containsKey(item);
	}

	public Item getItem(ItemFactory itemFactory) {
		int num = itemBag.get(itemFactory);
		num--;
		Item item = itemFactory.createInstance();
		if (num == 0)
			itemBag.remove(itemFactory);
		else
			itemBag.put(itemFactory, num);
		return item;
	}
	
	public int getItemCount(ItemFactory itemFactory) {
		if (itemBag.containsKey(itemFactory))
			return itemBag.get(itemFactory);
		else 
			return 0;
	}
	
	public void addItem(ItemFactory item) {
		int num = 0;
		if (itemBag.containsKey(item))
			num = itemBag.get(item);
		itemBag.put(item, num + 1);
	}
	
	public Map<ItemFactory, Integer> getItemBag() {
		return itemBag;
	}
	
	public Set<ItemFactory> getItemFactories() {
		return itemBag.keySet();
	}
}
