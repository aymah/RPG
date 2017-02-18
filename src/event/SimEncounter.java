package event;

import map.EmpireMap;
import map.GamePanel;
import map.RegionMap;

public class SimEncounter extends Encounter{

	public SimEncounter(String destination, String activationMethod, Switch s) {
		super(destination, activationMethod, s);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(GamePanel panel) {
		EmpireMap map = (EmpireMap) panel;
		if (!s.getType().equals("PERMANENT")) {
			if (s.getCondition().equals("COMPLETE") && s.getType().equals("CHANGE")) {
				changeOnComplete = true;
			}
		}
		map.takeSimEncounter(this);
	}	
}
