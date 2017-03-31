package event;

import map.EmpireMap;
import map.GamePanel;
import map.RegionMap;

public class EnterProvince extends MapEvent {

	Province province;
	
	public EnterProvince(String activationMethod, Switch s, Province province) {
		super(activationMethod, s);
		this.province = province;
	}

	@Override
	public void execute(GamePanel panel) {
		EmpireMap map = (EmpireMap) panel;
		map.openProvinceMenu(province);
	}
	
	public Province getProvince() {
		return province;
	}
}
