package event;

import map.GamePanel;
import map.RegionMap;

public class Encounter extends MapEvent {

	protected String destination;
	protected boolean changeOnComplete;

	public Encounter(String destination, String activationMethod, Switch s) {
		super(activationMethod, s);
		this.destination = destination;
		changeOnComplete = false;
	}
	
	@Override
	public void execute(GamePanel panel) {
		RegionMap map = (RegionMap) panel;
		if (!s.getType().equals("PERMANENT")) {
			if (s.getCondition().equals("COMPLETE") && s.getType().equals("CHANGE")) {
				changeOnComplete = true;
			}
		}
		map.takeEncounter(this);
	}
	
	public String getDestination() {
		return destination;
	}
	
	public void battleResult(String result) {
		System.out.println(changeOnComplete);
		if (changeOnComplete && result.equals("Win")) {
			this.mapEventManager.setEventIndex(s.getChangeIndex());
			changeOnComplete = false;
		} else
			changeOnComplete = false;
	}
	
}
