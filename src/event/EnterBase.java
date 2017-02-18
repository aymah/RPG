package event;

import map.EmpireMap;
import map.GamePanel;
import map.RegionMap;

public class EnterBase extends MapEvent {

	
	public EnterBase(String activationMethod, Switch s) {
		super(activationMethod, s);
		
	}

	@Override
	public void execute(GamePanel panel) {
		EmpireMap map = (EmpireMap) panel;
		map.getParty().healUnits();
		map.openMenu("Base");
	}

}
