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
        ((BattlePanelManager)manager).getInfoPanel().repaint(); //ensures the info panel updates after potential ability is set
    }
    
    public void removePanel() {
    	frame.remove(this);
    }
    
    public void drawMenu(Graphics2D g2d) {
    	for (int i = 0; i < menuItems.size(); i++) {
            g2d.setColor(new Color(255,255,255));
      	    if (menuItems.get(i).getClass() == Ability.class) {
     		    Unit unit = ((BattlePanelManager)manager).getBattleMap().getCurrUnit();
  			    Ability ability = (Ability)menuItems.get(i);
  			    unit.setPotentialAbility(ability);
  			    if (ability.getStamCost(unit) > unit.getPotentialStamina() || ability.getMPCost(unit) > unit.getPotentialMP()) {
  			    	g2d.setColor(new Color(190,190,190));
  			    }
  			    unit.wipePotentialAbility();
  		    } 
      	    if (selectorIndexY == i)
		      	g2d.setColor(new Color(255,0,0));
    		g2d.drawString(menuItems.get(i).getName(), 10, 20 + i * 30);
    	}
  	    if (menuItems.get(selectorIndexY).getClass() == Ability.class) {
 		    Unit unit = ((BattlePanelManager)manager).getBattleMap().getCurrUnit();
			Ability ability = (Ability)menuItems.get(selectorIndexY);
			unit.setPotentialAbility(ability);
  	    } else {
 		    Unit unit = ((BattlePanelManager)manager).getBattleMap().getCurrUnit();
		    unit.wipePotentialAbility();
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
			case 10:
				selectItem();
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
  	    if (menuItems.get(selectorIndexY).getClass() == Ability.class) {
 		    Ability ability = (Ability)menuItems.get(selectorIndexY);
 		    Unit unit = ((BattlePanelManager)manager).getBattleMap().getCurrUnit();
 		    if (ability.getStamCost(unit) > unit.getPotentialStamina() || ability.getMPCost(unit) > unit.getPotentialMP())
 		    	return;
 	    }
		menuItems.get(selectorIndexY).execute(this);
	}
	
	private void openAbilityMenu() {
    	List<Ability> abilities = ((BattlePanelManager) manager).getBattleMap().getCurrUnit().getActiveAbilities();
    	List<MenuItem> menuItems = new ArrayList<MenuItem>(abilities);
    	menuItems.add(exitMenuItem());
    	BattleMenuPanel abilityMenuPanel = new BattleMenuPanel("Ability Menu", frame, (BattlePanelManager) manager, menuItems, layer + 1);
    	subMenu = abilityMenuPanel;
    	abilityMenuPanel.superMenu = this;
    	abilityMenuPanel.displayPanel();
		frame.repaint();
		frame.refresh();
	}

	public void useAbility(Ability ability) {
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
		item = new MenuItem() {
			private String name = "Free Select";
			
			@Override
			public void execute(GamePanel panel) {
				MenuPanel menuPanel = (MenuPanel) panel;
				BattlePanelManager battleManager = (BattlePanelManager)menuPanel.getManager();
				battleManager.getBattleMap().setFreeSelectMode();
				menuPanel.closeMenu();
			}

			@Override
			public String getName() {
				return name;
			}
		};	
		menuItems.add(item);
		item = new MenuItem() {
			private String name = "End Turn";
			
			@Override
			public void execute(GamePanel panel) {
				MenuPanel menuPanel = (MenuPanel) panel;
				menuPanel.closeMenu();
				BattleMap bm = (BattleMap) menuPanel.manager.getDominantPanel();
				bm.endTurn();
			}

			@Override
			public String getName() {
				return name;
			}
		};		
		menuItems.add(item);
    	menuItems.add(exitMenuItem());
		return menuItems;
	}
	
	public int getSelectorIndexX() {
		return selectorIndexX;
	}


	public int getSelectorIndexY() {
		return selectorIndexY;
	}
	
	public MenuItem getMenuItem() {
		return menuItems.get(selectorIndexY);
	}


}
