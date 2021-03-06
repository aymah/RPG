package event;

import map.EmpireMap;
import map.GamePanel;
import map.RegionMap;

public class EnterBase extends MapEvent {

	Province province;
	
	public EnterBase(String activationMethod, Switch s, Province province) {
		super(activationMethod, s);
		this.province = province;
	}

	@Override
	public void execute(GamePanel panel) {
		EmpireMap map = (EmpireMap) panel;
		map.getParty().healUnits();
		map.openMenu("Base");
	}
	
	public Province getProvince() {
		return province;
	}

}
