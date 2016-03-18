package map;

import java.util.List;

import event.GenericMenuItem;
import event.MenuItem;

public class MenuPanel extends GamePanel{
	
	protected GameFrame frame;
	protected List<MenuItem> menuItems;
	protected MenuPanel subMenu;
	protected MenuPanel superMenu;
	protected Integer layer;
	protected PanelManager manager;

	
	public MenuPanel(List<MenuItem> menuItems, int layer) {
		this.menuItems =  menuItems;
		this.layer = layer;
	}
	
    public void displayPanel() {
		frame.add(this, layer, 0);
    }
    
    public void removePanel() {
    	frame.remove(this);
    }
    
	public void closeMenu() {
		removePanel();
		if (superMenu != null) {
			superMenu.subMenu = null;
		} else {
			manager.changeDominantPanel(manager.getLastPanel());
		}
		frame.repaint();
		frame.refresh();
	}
    
    protected static MenuItem exitMenuItem() {
    	MenuItem item = new MenuItem() {
			private String name = "Exit Menu";
			
			@Override
			public void execute(GamePanel panel) {
				MenuPanel menuPanel = (MenuPanel) panel;
				menuPanel.closeMenu();
			}

			@Override
			public String getName() {
				return name;
			}
		};
		return item;
    }
    
    public void clearSubMenus() {
    	while (subMenu != null) {
    		subMenu.clearSubMenus();
    		subMenu = null;
    		superMenu = null;
    	}
    }
}
