package map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import event.Ability;
import event.GenericMenuItem;
import event.MenuItem;

public class BattleMenuPanel extends MenuPanel {
	
	private int selectorIndexX;
	private int selectorIndexY;

	
	public BattleMenuPanel(String name, GameFrame frame, BattlePanelManager manager, List<MenuItem> menuItems, int layer) {
		super(menuItems, layer);
		this.manager = manager;
		if (layer < 2) manager.setMenuPanel(this);
		this.setBounds(GraphicsConstants.BATTLE_ACTION_MENU_X, GraphicsConstants.BATTLE_ACTION_MENU_Y, 
					   GraphicsConstants.BATTLE_ACTION_MENU_WIDTH, GraphicsConstants.BATTLE_ACTION_MENU_HEIGHT);
		this.name = name;
		this.frame = frame;
		this.selectorIndexX = 0;
		this.selectorIndexY = 0;
	}

	
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        Color bgColor = new Color(100,100,0);
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, GraphicsConstants.BATTLE_ACTION_MENU_WIDTH, GraphicsConstants.BATTLE_ACTION_MENU_HEIGHT);
        drawMenu(g2d);
    }
    
    public void removePanel() {
    	frame.remove(this);
    }
    
    public void drawMenu(Graphics2D g2d) {
//    	items = new ArrayList<String>();
//    	items.add("Attack");
//    	items.add("test2");
//    	items.add("test3");
//    	items.add("Exit Menu");
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
		boolean moved = false;
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
				selectItem();
				break;
			case 84:
				closeMenu();
				break;
		}
		this.repaint();
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
	
	private void openAbilityMenu() {
    	List<Ability> abilities = ((BattlePanelManager) manager).getBattleMap().getCurrUnit().getAbilities();
//    	for (Ability ability: abilities) {
//    		MenuItem menuItem = new MenuItem() {
//    			String name = ability.getName();
//    			
//				@Override
//				public void execute(GamePanel panel) {
//					// TODO Auto-generated method stub
//					
//				}
//
//				@Override
//				public String getName() {
//					// TODO Auto-generated method stub
//					return null;
//				}
//    		}
//    		menuItems.add(ability);
//    	}
    	List<MenuItem> menuItems = new ArrayList<MenuItem>(abilities);
    	menuItems.add(exitMenuItem());
    	BattleMenuPanel abilityMenuPanel = new BattleMenuPanel("Ability Menu", frame, (BattlePanelManager) manager, menuItems, layer + 1);
    	subMenu = abilityMenuPanel;
    	abilityMenuPanel.superMenu = this;
    	abilityMenuPanel.displayPanel();
		frame.repaint();
		frame.refresh();
	}

	public void useTargetedAbility(Ability ability) {
		tempCloseAllMenus(ability);
	}
	
	private void tempCloseAllMenus(Ability ability) {
		if (superMenu != null) {
			((BattleMenuPanel)superMenu).tempCloseAllMenus(ability);
		} else {
			BattleMap battleMap = ((BattlePanelManager)manager).getBattleMap();
			battleMap.setTargetingMode(ability);
			manager.changeDominantPanel(battleMap);
			frame.refresh();

		}
		removePanel();
	}

	
	public static List<MenuItem> getStandardMenu() {
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
		MenuItem item = new MenuItem() {
			private String name = "Attack";
			
			@Override
			public void execute(GamePanel panel) {
				BattleMenuPanel menuPanel = (BattleMenuPanel) panel;
				menuPanel.openAbilityMenu();
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
		item = new GenericMenuItem("test1");
		menuItems.add(item);
		item = new GenericMenuItem("test2");
		menuItems.add(item);
    	menuItems.add(exitMenuItem());
		return menuItems;
	}
}
