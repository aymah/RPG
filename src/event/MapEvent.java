package event;

import map.GamePanel;
import map.RegionMap;

public abstract class MapEvent implements Event{

	protected String activationMethod;
	protected Switch s;
	protected MapEventManager mapEventManager;
	
	public MapEvent(String activationMethod, Switch s) {
		this.activationMethod = activationMethod;
		this.s = s;
	}
	
	public String getActivationMethod() {
		return activationMethod;
	}

	public void setEventManager(MapEventManager mapEventManager) {
		this.mapEventManager = mapEventManager;
	}
}
