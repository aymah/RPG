package unit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import event.Building;
import event.BuildingFactory;

public class Army implements Serializable {
	
	private Party party;
//	private Map<UnitFactory, Integer> numUnits;
	private Empire empire;
	private List<Unit> unitList;
//	private Map<String, Integer> defaultNumUnits;
//	
//	private static final int DEFAULT_MAN_AT_ARMS = 2;
//	private static final int DEFAULT_ARCHER = 2;
//	private static final int DEFAULT_CLERIC = 2;
//	private static final int DEFAULT_ELECTROMANCER = 2;
//	private static final int DEFAULT_KNIGHT = 1;
//	private static final int DEFAULT_DRAGOON = 1;
//	private static final String[] unitTypes = new String[] {"Man At Arms", "Archer", "Cleric", "Electromancer", "Knight", "Dragoon"};
//	private static final int[] defaultNums = new int[] {DEFAULT_MAN_AT_ARMS, DEFAULT_ARCHER, DEFAULT_CLERIC, DEFAULT_ELECTROMANCER, DEFAULT_KNIGHT, DEFAULT_DRAGOON};
	
	
	public Army(Party party) {
		party.setArmy(this);
		this.party = party;
		empire = party.getEmpire();
		unitList = new ArrayList<Unit>();
//		for (int i = 0; i < unitTypes.length; i++) {
//			numUnits.put(unitTypes[i], defaultNums[i]);
//		}
//		defaultNumUnits = new HashMap<String, Integer>();
//		for (int i = 0; i < unitTypes.length; i++) {
//			defaultNumUnits.put(unitTypes[i], defaultNums[i]);
//		}
	}
	
	public List<UnitFactory> getAvailableUnits() {
		return empire.getUnitsEnabled();
	}
	
	public List<BuildingFactory> getAvailableBuildings() {
		return empire.getUnbuiltBuildingsEnabled();
	}
	
	public List<Building> getConstructedBuildings() {
		return empire.getBuildings();
	}
	
//	public int getNumUnitByType(String type) {
//		int count = 0;
//		for (Unit unit: unitList) {
//			if (unit.getType().equals(type)) {
//				count++;
//			}
//		}
//		return count;
//	}
	
//	public int getAdditionalNumUnitByType(String type) {
//		return numUnits.get(type) - defaultNumUnits.get(type);
//	}
	
//	public void incrementUnit(UnitFactory factory) {
//		if (numUnits.get(factory) == null)
//			numUnits.put(factory, 1);
//		else
//			numUnits.put(factory, numUnits.get(factory) + 1);
//	}
	
//	public Set<String> getTypes() {
//		return numUnits.keySet();
//	}
	
//	public List<Unit> getUnitListNotInParty() {
//		Empire empire = party.getEmpire();
//		List<Unit> unitList = new ArrayList<Unit>();
//		for (String type: numUnits.keySet()) {
//			int numUnit = numUnits.get(type);
//			for (int i = party.getNumberOfType(type); i < numUnit; i++) {
//				unitList.add(empire.createUnit(type, i + 1));
//			}
//		}
//		return unitList;
//	}

	public List<UnitFactory> checkAvailableUnits(List<UnitFactory> units) {
		Set<UnitFactory> unitsEnabled = new HashSet(empire.getUnitsEnabled());
		unitsEnabled.retainAll(units);
		return new ArrayList<UnitFactory>(unitsEnabled);
	}
	
//	public void respec() {
//		numUnits = new HashMap<String, Integer>();
//		for (int i = 0; i < unitTypes.length; i++) {
//			numUnits.put(unitTypes[i], defaultNums[i]);
//		}
//	}
	
	public List<Unit> getUnitList() {
		return unitList;
	}

	public void addUnit(Unit unit) {
		unitList.add(unit);
	}

	public int getNumUnitsByType(String type) {
		int counter = 0;
		for (Unit unit: unitList) {
			if (unit.getType().equals(type))
				counter++;
		}
		return counter;
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
}
