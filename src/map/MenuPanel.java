package map;

import java.util.List;

import event.GenericMenuItem;
import event.MenuItem;

public class MenuPanel extends LayeredPanel {
	
	protected List<MenuItem> menuItems;
	protected MenuPanel subMenu;
	protected MenuPanel superMenu;
	protected PanelManager manager;

	
	public MenuPanel(List<MenuItem> menuItems, int layer) {
		super(layer);
		this.menuItems =  menuItems;
		this.layer = layer;
	}
    
    public void removePanel() {
    	frame.remove(this);
    }
    
	public void closeMenu() {
		removePanel();
		if (superMenu != null) {
			superMenu.subMenu = null;
		} else {
			manager.changeDominantPanelToPrevious();
		}
		frame.repaint();
		frame.refresh();
	}
	
//	public void closeAllMenus() {
//		closeAllSubMenus();
//		closeMenu();
//	}
//	
	public void closeAllMenus() {
		closeMenu();
		if (superMenu != null) {
			superMenu.closeAllMenus();
		}
	}
	
	public MenuPanel closeAllSubMenus() {
		MenuPanel menu = this;
		if (superMenu != null) {
			menu = superMenu.closeAllSubMenus();
			closeMenu();
		}
		return menu;
	}

	
//	public void closeAllSubMenus() {
//		MenuPanel baseMenu = this;
//		while (baseMenu.superMenu != null) {
//			baseMenu = baseMenu.superMenu;
//		}
//		baseMenu.clearSubMenus();
//	}
    
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
    
    public PanelManager getManager() {
    	return manager;
    }
}
