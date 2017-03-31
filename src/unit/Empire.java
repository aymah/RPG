package unit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import map.Coordinates;
import misc.Factory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import event.Building;
import event.BuildingFactory;
import event.Date;
import event.ItemFactory;
import event.Province;
import event.Technology;

public class Empire implements Serializable {
	
	private static final int INCOME_TIMER = 30;
	private List<Technology> technologies;
//	private List<BuildingFactory> buildingFactories;
	private List<Building> buildings;
	private List<Unit> unitList;
	private List<Province> ownedProvinces;
	private List<Province> provinces;
	private int time;
	private int manpower;
	private Party party;
	
	public Empire(Party party) {
		this.party = party;
		this.unitList = new ArrayList<Unit>();
		ownedProvinces = new ArrayList<Province>();
		technologies = new ArrayList<Technology>();
		buildings = new ArrayList<Building>();
		manpower = 0;
		time = 1;
		readTechnologies();
		readResources();
		provinces = readProvinces(readAllProvinces());
		assignStartingProvinces();

	}
	
	private void assignStartingProvinces() {
		List<String> startingProvinceNames = readStartingProvinces();
		for (Province province: provinces) {
			if (startingProvinceNames.contains(province.getName()))
				ownedProvinces.add(province);
		}
	}

	private void readTechnologies() {
		List<String> technologyNames = readStartingTechnologies();
		for (String technologyName: technologyNames) {
			readTechnology(technologyName);
		}
	}


	private List<String> readStartingTechnologies() {
		List<String> technologyNames = new ArrayList<String>();
		JSONObject jsonObject = getJSONObject("/startingConfig/Starting Technology.json"); 
		JSONArray names = jsonObject.getJSONArray("Technologies");
		for (Object name: names) {
			technologyNames.add((String) name);
		}
		return technologyNames;
	}
	
	public void readTechnology(String technologyName) {
		JSONObject technologyObject = getJSONObject("/technologies/" + technologyName + ".json"); 
		String name = technologyObject.getString("Name");
		JSONArray units = new JSONArray();
		if (technologyObject.has("Units Enabled"))
			units = technologyObject.getJSONArray("Units Enabled");
		JSONArray buildings = new JSONArray();
		if (technologyObject.has("Buildings Enabled"))
			buildings = technologyObject.getJSONArray("Buildings Enabled");
		JSONArray items = new JSONArray();
		if (technologyObject.has("Items Enabled"))
			items = technologyObject.getJSONArray("Items Enabled");
		List<UnitFactory> unitFactories = buildUnitFactories(units);
		List<BuildingFactory> buildingFactories = buildBuildingFactories(buildings);
		List<ItemFactory> itemFactories = buildItemFactories(items);
		technologies.add(new Technology(buildingFactories, unitFactories, itemFactories));
	}

	private List<ItemFactory> buildItemFactories(JSONArray items) {
		List<ItemFactory> itemFactories = new ArrayList<ItemFactory>();
		for (Object item: items) {
			itemFactories.add(new ItemFactory((String) item));
		}
		return itemFactories;
	}

	private List<UnitFactory> buildUnitFactories(JSONArray units) {
		List<UnitFactory> unitFactories = new ArrayList<UnitFactory>();
		for (Object unit: units) {
			unitFactories.add(new UnitFactory((String) unit));
		}
		return unitFactories;
	}
	
	private void readResources() {
		List<String> resources = new ArrayList<String>();
		JSONObject jsonObject = getJSONObject("/startingConfig/Starting Resources.json"); 
		party.addGold(jsonObject.getInt("Gold"));
		manpower = jsonObject.getInt("Manpower");
	}
	
	private List<Province> readProvinces(List<String> provinceNames) {
		List<Province> provinces = new ArrayList<Province>();
		for (String provinceName: provinceNames) {
			provinces.add(readProvince(provinceName));
		}
		return provinces;
	}

	private List<String> readStartingProvinces() {
		List<String> provinceNames = new ArrayList<String>();
		JSONObject jsonObject = getJSONObject("/startingConfig/Starting Provinces.json"); 
		JSONArray names = jsonObject.getJSONArray("Provinces");
		for (Object name: names) {
			provinceNames.add((String) name);
		}
		return provinceNames;
	}
	
	private List<String> readAllProvinces() {
		List<String> provinceNames = new ArrayList<String>();
		JSONObject jsonObject = getJSONObject("/provinces/Province List.json"); 
		JSONArray names = jsonObject.getJSONArray("Province List");
		for (Object name: names) {
			provinceNames.add((String) name);
		}
		return provinceNames;
	}

	public Province readProvince(String provinceName) {
		JSONObject provinceObject = getJSONObject("/provinces/" + provinceName + ".json"); 
		String name = provinceObject.getString("Name");
		int goldIncome = provinceObject.getInt("Gold Income");
		int manpowerIncome = provinceObject.getInt("Manpower Income");
		String owner = "";
		JSONObject jsonCoordinates = (JSONObject) provinceObject.get("Coordinates");
		Coordinates coordinates = new Coordinates(jsonCoordinates.getInt("y"), jsonCoordinates.getInt("x"));
		JSONArray jsonArea = (JSONArray) provinceObject.get("Area");
		int[][] area = new int[jsonArea.length()][((JSONArray)jsonArea.get(0)).length()];

		for (int i = 0; i < jsonArea.length(); i++) {
			JSONArray jsonArr = (JSONArray) jsonArea.get(i);
			for (int j = 0; j < jsonArr.length(); j++) {
				area[i][j] = jsonArr.getInt(j);
			}
		}
		return new Province(name, goldIncome, manpowerIncome, owner, coordinates, area);
	}

	private List<BuildingFactory> buildBuildingFactories(JSONArray buildings) {
		List<BuildingFactory> buildingFactories = new ArrayList<BuildingFactory>();
		for (Object building: buildings) {
			buildingFactories.add(new BuildingFactory((String) building));
		}
		return buildingFactories;
	}

	private JSONObject getJSONObject(String path) {
		InputStream in = getClass().getResourceAsStream(path); 
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line;
		StringBuilder sb = new StringBuilder();
	    try {
			while ((line = br.readLine()) != null) {
			    sb.append(line);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(sb.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	

	public List<BuildingFactory> getBuildingsEnabled() {
		List<BuildingFactory> buildingTechnologies = new ArrayList<BuildingFactory>();
		for (Technology technology: technologies) {
			buildingTechnologies.addAll(technology.getBuildingsEnabled());
		}
		return buildingTechnologies;
	}
	
	public List<BuildingFactory> getUnbuiltBuildingsEnabled() {
		List<BuildingFactory> buildingTechnologies = new ArrayList<BuildingFactory>();
		for (Technology technology: technologies) {
			List<BuildingFactory> buildingFactories = technology.getBuildingsEnabled();
			for (BuildingFactory buildingFactory: buildingFactories) {
				if (buildingFactory.isUnbuilt()) buildingTechnologies.add(buildingFactory);
			}
		}
		return buildingTechnologies;
	}
	
	public List<UnitFactory> getUnitsEnabled() {
//		List<UnitFactory> unitTechnologies = new ArrayList<UnitFactory>();
//		for (Technology technology: technologies) {
//			unitTechnologies.addAll(technology.getUnitsEnabled());
//		}
//		return unitTechnologies;
		List<BuildingFactory> buildingFactories = getBuildingsEnabled();
		List<UnitFactory> unitTechnologies = new ArrayList<UnitFactory>();
		for (BuildingFactory buildingFactory: buildingFactories) {
			unitTechnologies.addAll(buildingFactory.getUnitsEnabled());
		}
		return unitTechnologies;
	}
	
	public List<UnitFactory> getBuildableUnits() { //should intersect with a list from technology list? see getIntersectionByName
		List<UnitFactory> unitTechnologies = new ArrayList<UnitFactory>();
		for (Building building: buildings) {
			unitTechnologies.addAll(building.getUnitsEnabled());
		}
		return unitTechnologies;
	}
	
	public List<Building> getBuildableUnitBuildings() { 
		List<Building> unitBuildings = new ArrayList<Building>();
		for (Building building: buildings) {
			if (building.getUnitsEnabled().size() > 0) {
				unitBuildings.add(building);
			}
		}
		return unitBuildings;
	}
	
	public List<ItemFactory> getItemsEnabledByTechnology() {
		List<ItemFactory> itemTechnologies = new ArrayList<ItemFactory>();
		for (Technology technology: technologies) {
			itemTechnologies.addAll(technology.getItemsEnabled());
		}
		return itemTechnologies;
	}
	
	public List<ItemFactory> getBuildableItems() {
		List<Building> buildings = getBuildingsByType("Merchant");
		List<ItemFactory> itemsEnabledByBuildings = new ArrayList<ItemFactory>();
		for (Building building: buildings) {
			itemsEnabledByBuildings.addAll(building.getItemsSold());
		}
		List<ItemFactory> itemsEnabledByTechnology = getItemsEnabledByTechnology();
		return (List<ItemFactory>)getIntersectionByName(itemsEnabledByTechnology, itemsEnabledByBuildings);
	}
	
	//Note: permanent info is stored in the factoriesEnabledByBuildings like building level etc. so the slot is important.
	private List<? extends Factory> getIntersectionByName(List<? extends Factory> factoriesEnabledByTechnology, List<? extends Factory> factoriesEnabledByBuildings) {
		List<Factory> intersection = new ArrayList<Factory>();
		for (Factory factory1: factoriesEnabledByTechnology) {
			for (Factory factory2: factoriesEnabledByBuildings) {
				if (factory1.getName().equals(factory2.getName()))
					intersection.add(factory2);
			}
		}
		return intersection;
	}

//	private List<String> getUnitsEnabledByBuildings() {
//		List<String> unitTechnologies = new ArrayList<String>();
//		for (Building building: buildings) {
//			unitTechnologies.addAll(building.getUnitsEnabled());
//		}
//		return unitTechnologies;
//	}
//	
//	public List<String> getUnitsEnabled() {
//		Set<String> unitNames1 = new HashSet(getUnitsEnabledByTechnology());
//		System.out.println("size " + unitNames1.size());
//		Set<String> unitNames2 = new HashSet(getUnitsEnabledByBuildings());
//		System.out.println("size " + unitNames2.size());
//		unitNames1.retainAll(unitNames2);
//		System.out.println("size " + unitNames1.size());
//		return new ArrayList<String>(unitNames1);
//	}
	
	public List<Building> getBuildings() {
		return buildings;
	}
	
	public List<Building> getBuildingsByType(String string) {
		List<Building> relevantBuildings = new ArrayList<Building>();
		for (Building building: buildings) {
			if (building.getType().equals(string))
				relevantBuildings.add(building);
		}
		return relevantBuildings;
	}

	public Unit createUnit(UnitFactory unitFactory) {
		Unit unit = unitFactory.createInstance("ALLY", unitFactory.getType(), unitFactory.getLevel());
		unitList.add(unit);
		return unit;
	}

	public int getBuildingLevel(String type) {
		int buildingLevel = 0;
		for (Building building: buildings) {
			List<UnitFactory> unitsEnabled = building.getUnitsEnabled();
			for (UnitFactory unitFactory: unitsEnabled) {
				if (unitFactory.getType().equals(type)) {
					buildingLevel = building.getLevel();
					break;
				}
			}
		}
		return buildingLevel;
	}
	
//	public List<BuildingFactory> getBuild
	//get buildingfactories
	//get buildings
	
	public List<Unit> getUnitList() {
		return unitList;
	}
	
	public int getNumUnitsByType(String type) {
		int count = 0;
		for (Unit unit: unitList) {
			if (unit.getType().equals(type))
				count++;
		}
		return count;
	}

	public int getManpower() {
		return manpower;
	}
	
	public void addManpower(int manpower) {
		this.manpower += manpower;
	}

	public void spendManpower(int manpower) {
		this.manpower -= manpower;
	}

	public void createBuilding(BuildingFactory availableBuilding) {
		Building building = availableBuilding.createInstance();
		buildings.add(building);
	}
	
	public int getTime() {
		return time;
	}
	
	public Date getDate() {
		return new Date(time);
	}

	public void incrementTime() {
		time++;
		if (time%INCOME_TIMER == 0) {
			applyGoldIncome();
			applyManpowerIncome();
		}
	}
	
	public int getManpowerIncome() {
		int manpowerIncome = 0;
		for (Province province: ownedProvinces) {
			manpowerIncome += province.getManpowerIncome();
		}
		return manpowerIncome;
	}
	
	public int getGoldIncome() {
		int goldIncome = 0;
		for (Province province: ownedProvinces) {
			goldIncome += province.getGoldIncome();
		}	
		return goldIncome;
	}

	private void applyManpowerIncome() {
		manpower += getManpowerIncome();
	}

	private void applyGoldIncome() {
		party.addGold(getGoldIncome());
	}

	public List<Province> getAllProvinces() {
		return provinces;
	}

	public Province getProvince(String provinceName) {
		for (Province province: provinces) {
			if (province.getName().equals(provinceName))
				return province;
		}
		return null;
	}






}
