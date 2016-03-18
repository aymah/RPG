package event;

import java.util.List;

import map.GamePanel;

public class GenericMenuItem implements MenuItem {

	public List<MenuItem> subMenu;
	public String name;
	
	public GenericMenuItem(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void execute(GamePanel panel) {
		System.out.println("Sorry, GenericMenuItems have no execute function");
	}

}
