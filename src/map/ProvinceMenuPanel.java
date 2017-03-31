package map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import unit.Unit;
import event.Ability;
import event.MenuItem;
import event.Province;

public class ProvinceMenuPanel extends MenuPanel {
	
//	private Province province;
	private int selectorIndexX = 0;
	private int selectorIndexY = 0;

	public ProvinceMenuPanel(String name, GameFrame frame, EmpirePanelManager manager, List<MenuItem> menuItems, int layer) {
		super(menuItems, layer);
		this.manager = manager;
		if (layer < 2) manager.setProvinceMenuPanel(this);
		this.setBounds(GraphicsConstants.PROVINCE_MENU_X, GraphicsConstants.PROVINCE_MENU_Y, 
					   GraphicsConstants.PROVINCE_MENU_WIDTH, GraphicsConstants.PROVINCE_MENU_HEIGHT);
		this.name = name;
		this.frame = frame;
		this.selectorIndexX = 0;
		this.selectorIndexY = 0;
//		this.province = null;
	}

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        Color bgColor = new Color(100,100,0);
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, GraphicsConstants.PROVINCE_MENU_WIDTH, GraphicsConstants.PROVINCE_MENU_HEIGHT);
        drawMenu(g2d);
    }
	
    public void drawMenu(Graphics2D g2d) {
    	for (int i = 0; i < menuItems.size(); i++) {
            g2d.setColor(new Color(255,255,255));
      	    if (selectorIndexY == i)
		      	g2d.setColor(new Color(255,0,0));
    		g2d.drawString(menuItems.get(i).getName(), 10, 20 + i * 30);
    	}
    }
    
	public void keyPressed(KeyEvent e) {
		if (subMenu != null) {
			subMenu.keyPressed(e);
			return;
		}
		int keyCode = e.getKeyCode();
		System.out.println(keyCode);
		switch (keyCode) {
			case 87:
			case 38:
				moveUp();
				break;
			case 83:
			case 40:
				moveDown();
				break;
//				case 65:
//				case 37:
//					moveLeft();
//					break;
//				case 68:
//				case 39:
//					moveRight();
//					break;
			case 69:
			case 10:
				selectItem();
				break;
			case 80:
				closeAllMenus();
				BattleMap bm = (BattleMap) manager.getDominantPanel();
				bm.endTurn();
				break;
			case 81:
			case 84:
			case 27:
				closeMenu();
				break;
		}
		frame.refresh();
	}
    
	private void moveUp() {
		selectorIndexY--;
		if (selectorIndexY < 0)
			selectorIndexY = menuItems.size() - 1;
	}
	
	private void moveDown() {
		selectorIndexY++;
		if (selectorIndexY >= menuItems.size())
			selectorIndexY = 0;
	}
	
	private void selectItem() {
		menuItems.get(selectorIndexY).execute(this);
	}
    
	public static List<MenuItem> getStandardMenu() {
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
		menuItems.add(MenuPanel.exitMenuItem());
		return menuItems;
	}
	
	private List<MenuItem> getProvinceMenu(Province province) {
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
		MenuItem item = null;
		if (province.getOwner().equals("Player")) {
	    	item = new MenuItem() {
				private String name = "Build Improvements";
				
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
			menuItems.add(item);
		} else {
	    	item = new MenuItem() {
				private String name = "Invade Province";
				
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
			menuItems.add(item);
		}
    	item = new MenuItem() {
			private String name = "Inspect Province";
			
			@Override
			public void execute(GamePanel panel) {
				ProvinceMenuPanel menuPanel = (ProvinceMenuPanel) panel;
				menuPanel.openInspectProvinceMenu();
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
		menuItems.add(MenuPanel.exitMenuItem());
		//make a menu based on the province stats. for example, if you are the owner you maybe have build, if you aren't you have attack
		//all provinces should have some like get info panel as well.
		return menuItems;
	}
	
	private void openInspectProvinceMenu() {
    	List<MenuItem> menuItems = new ArrayList<MenuItem>();
    	menuItems.add(exitMenuItem());
    	ProvinceMenuPanel menuPanel = new ProvinceMenuPanel("Inspect Province Menu", frame, (EmpirePanelManager)manager, menuItems, layer + 1);
    	subMenu = menuPanel;
    	menuPanel.superMenu = this;
    	menuPanel.displayPanel();
		frame.repaint();
		frame.refresh();
	}
	
	public void setProvince(Province province) {
		menuItems = getProvinceMenu(province);
//		this.province = province;
	}

//	public Province getProvince() {
//		return province;
//	}
}
