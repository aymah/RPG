package map;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import unit.Party;

public class GameStateManager implements Serializable {

	private Map<String, GenericMap> regions; //map of mapname to mapsave
	
	public GameStateManager() {
		regions = new HashMap<String, GenericMap>();
	}
	
	public GenericMap getMap(String mapName) {
		return (regions.get(mapName));
	}
	
	public void add(GenericMap map) {
		regions.put(map.getName(), map);
	}
}
