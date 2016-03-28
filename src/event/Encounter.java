package event;

import map.GamePanel;
import map.RegionMap;

public class Encounter implements Event {

	private String destination;
	private boolean active;

	public Encounter(String destination) {
		this.destination = destination;
		this.active = true;
	}
	
	@Override
	public void execute(GamePanel panel) {
		if (active) {
			RegionMap map = (RegionMap) panel;
			map.takeEncounter(this);
//			active = false;
		}
	}
	
	public String getDestination() {
		return destination;
	}
}
