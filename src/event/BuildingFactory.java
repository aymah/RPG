package event;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import misc.Factory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import unit.Unit;
import unit.UnitFactory;

public class BuildingFactory extends Factory implements Serializable {

	private String name;
	private String type;
	private boolean upgradeable;
	private int maxLevel;
	private int baseCost;
	private double costScalar;
	private List<UnitFactory> unitsEnabled;
	private List<ItemFactory> itemsSold;
	private boolean isBuilt;
	
	public BuildingFactory(String filename) {
		unitsEnabled = new ArrayList<UnitFactory>();
		itemsSold = new ArrayList<ItemFactory>();
		isBuilt = false;
		loadBuilding(filename);
	}
	
	private void loadBuilding(String filename) {
		JSONObject buildingObject = getJSONObject("/buildings/" + filename + ".json"); 
		name = buildingObject.getString("Name");
		type = buildingObject.getString("Type");
		baseCost = buildingObject.getInt("Base Cost");
		upgradeable = buildingObject.getBoolean("Upgradeable");
		if (upgradeable) {
			maxLevel = buildingObject.getInt("Max Level");
			costScalar = buildingObject.getDouble("Cost Scalar");
		}
		if (type.equals("Recruiter")) {
			JSONArray unitsEnabledArr = (JSONArray) buildingObject.get("Recruits");
			unitsEnabled.addAll(buildUnitFactories((unitsEnabledArr)));
		}
		if (type.equals("Merchant")) {
			JSONArray itemsSoldArr = (JSONArray) buildingObject.get("Sells");
			itemsSold.addAll(buildItemFactories(itemsSoldArr));
		}
	}
	
	public Building createInstance() {
		Building building = new Building(this);
		isBuilt = true;
		return building;
	}
	
	private List<UnitFactory> buildUnitFactories(JSONArray units) {
		List<UnitFactory> unitFactories = new ArrayList<UnitFactory>();
		for (Object unit: units) {
			unitFactories.add(new UnitFactory((String) unit));
		}
		return unitFactories;
	}
	
	private List<ItemFactory> buildItemFactories(JSONArray items) {
		List<ItemFactory> itemFactories = new ArrayList<ItemFactory>();
		for (Object item: items) {
			itemFactories.add(new ItemFactory((String) item));
		}
		return itemFactories;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public List<UnitFactory> getUnitsEnabled() {
		return unitsEnabled;
	}

	public boolean isUnbuilt() {
		return !isBuilt;
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
