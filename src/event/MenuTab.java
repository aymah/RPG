package event;

import java.util.Set;

import map.GamePanel;

public class MenuTab implements MenuItem {

	private String types; 
	
	public MenuTab(String types) {
		this.types = types;
	}
	
	@Override
	public void execute(GamePanel panel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public boolean hasType(String type) {
		return types.contains(type);
	}

}
