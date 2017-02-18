package event;

import java.util.Collection;
import java.util.List;

import unit.UnitFactory;

public class Building {

	private String name;
	private String type;
	private int level;
	private boolean upgradeable;
	private int maxLevel;
	private int baseCost;
	private double costScalar;
	private List<ItemFactory> itemsSold;
	private List<UnitFactory> unitsEnabled;
	
	public Building(BuildingFactory buildingFactory) {
		this.name = buildingFactory.getName();
		this.type = buildingFactory.getType();
		this.upgradeable = buildingFactory.isUpgradeable();
		this.maxLevel = buildingFactory.getMaxLevel();
		this.baseCost = buildingFactory.getBaseCost();
		this.costScalar = buildingFactory.getCostScalar();
		this.level = 1;
		this.itemsSold = buildingFactory.getItemsSold();
		this.unitsEnabled = buildingFactory.getUnitsEnabled();
	}
	
	public List<UnitFactory> getUnitsEnabled() {
		return unitsEnabled;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}
	
	public void incrementLevel() {
		level++;
	}

	public int getLevel() {
		return level;
	}

	public int calculateCost() {
		int cost = baseCost;
		for (int i = 0; i < level; i++) {
			cost *= costScalar;
		}
		return cost;	
	}

	public boolean isUpgradeable() {
		return upgradeable;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public int getBaseCost() {
		return baseCost;
	}

	public double getCostScalar() {
		return costScalar;
	}

	public List<ItemFactory> getItemsSold() {
		return itemsSold;
	}

}
