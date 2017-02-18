package event;

import java.io.Serializable;
import java.util.List;

import unit.UnitFactory;

public class Technology implements Serializable {

	private String name;
	private List<BuildingFactory> buildingsEnabled;
	private List<UnitFactory> unitsEnabled;
	private List<ItemFactory> itemsEnabled;
	
	public Technology(List<BuildingFactory> buildingsEnabled, List<UnitFactory> unitsEnabled, List<ItemFactory> itemsEnabled) {
		this.buildingsEnabled = buildingsEnabled;
		this.unitsEnabled = unitsEnabled;
		this.itemsEnabled = itemsEnabled;
	}

	public String getName() {
		return name;
	}

	public List<BuildingFactory> getBuildingsEnabled() {
		return buildingsEnabled;
	}

//	public List<UnitFactory> getUnitsEnabled() {
//		return unitsEnabled;
//	}

	public List<ItemFactory> getItemsEnabled() {
		return itemsEnabled;
	}
}
