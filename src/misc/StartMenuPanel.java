package misc;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import unit.Hero;
import unit.Party;
import unit.Unit;
import event.Ability;
import event.MenuItem;
import map.BattlePanelManager;
import map.GameFrame;
import map.GamePanel;
import map.GraphicsConstants;
import map.MenuPanel;
import map.RegionInfoPanel;
import map.RegionMap;
import map.RegionMenuPanel;
import map.RegionPanelManager;

public class StartMenuPanel extends MenuPanel {
	
    private int selectorIndexX;
	private int selectorIndexY;
	
    public StartMenuPanel(String name, GameFrame frame, StartPanelManager manager, List<MenuItem> menuItems, int layer) {
		super(menuItems, layer);
		this.manager = manager;
		if (layer < 2) manager.setMenuPanel(this);
		this.setBounds(0, 0, GraphicsConstants.FRAME_WIDTH, GraphicsConstants.FRAME_HEIGHT);
		this.name = name;
		this.frame = frame;	
		this.selectorIndexX = 0;
		this.selectorIndexY = 0;
	}
	
	@Override
	public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        Color bgColor = new Color(0,0,0);
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, GraphicsConstants.FRAME_WIDTH, GraphicsConstants.FRAME_HEIGHT);
        drawMenu(g2d);
	}
	
	private void drawMenu(Graphics2D g2d) {
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
			case 69:
			case 10:
				selectItem();
				break;
			case 81:
			case 27:
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
		MenuItem item = new MenuItem() {
			private String name = "Load Save Game";
			
			@Override
			public void execute(GamePanel panel) {

			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
		item = new MenuItem() {
			private String name = "Start New Game";
			
			@Override
			public void execute(GamePanel panel) {
				StartMenuPanel menuPanel= (StartMenuPanel)panel;
				menuPanel.startGame();
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
		item = new MenuItem() {
			private String name = "Quit";
			
			@Override
			public void execute(GamePanel panel) {
				System.exit(0);
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);		
		return menuItems;	
	}
	
	public void startGame() {
		frame.removeAll();
        RegionPanelManager manager = new RegionPanelManager(frame);
        Party party = new Party();
        Hero hero = new Hero("Leader", "ALLY", "L", party);
        party.addUnit(hero);
        hero = new Hero("Wizard", "ALLY", "W", party);
        party.addUnit(hero);
        hero = new Hero("Tank", "ALLY", "T", party);
        party.addUnit(hero);
        hero = new Hero("Scout", "ALLY", "S", party);
        party.addUnit(hero);
        Unit unit = new Unit("testManAtArms1", "ALLY", "M1", party);
        party.addUnit(unit);
        unit = new Unit("testManAtArms1", "ALLY", "M2", party);
        party.addUnit(unit);
        RegionMap panel = new RegionMap("testMap", frame, manager, party);
        panel.setCoordinates(1,1);
        RegionInfoPanel infoPanel = new RegionInfoPanel("testInfoPanel", frame, manager);
        RegionMenuPanel menuPanel = new RegionMenuPanel("testMenuPanel", frame, manager, RegionMenuPanel.getStandardMenu(), 1);
        manager.setDominantPanel(panel);
        frame.refresh();
	}
}
