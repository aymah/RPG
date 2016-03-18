package event;

import map.GamePanel;
import map.RegionMap;

public class Encounter implements Event {

	private String destination;
	private int destIndexY;
	private int destIndexX;
	
	public Encounter(String destination, int destIndexY, int destIndexX) {
		this.destination = destination;
		this.destIndexY = destIndexY;
		this.destIndexX = destIndexX;
	}
	
	@Override
	public void execute(GamePanel panel) {
		RegionMap map = (RegionMap) panel;
		map.takeEncounter(this);
	}
	
	public String getDestination() {
		return destination;
	}
	
	public int getDestIndexY() {
		return destIndexY;
	}
	
	public int getDestIndexX() {
		return destIndexX;
	}
}
