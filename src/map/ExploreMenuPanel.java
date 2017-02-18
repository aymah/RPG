package map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import misc.StartMenuPanel;
import misc.StartPanelManager;
import unit.Army;
import unit.Empire;
import unit.Hero;
import unit.Party;
import unit.Stats;
import unit.Unit;
import unit.UnitFactory;
import event.Ability;
import event.Building;
import event.BuildingFactory;
import event.Effect;
import event.Equipment;
import event.GenericMenuItem;
import event.Item;
import event.ItemFactory;
import event.MenuItem;
import event.MenuTab;

//probably want to make a sort of menu items class, it will be similar in some ways to how events work. Might even extend off events.
public class ExploreMenuPanel extends MenuPanel {
	
	private List<MenuTab> tabItems;
	private String source;
	private int selectorIndexX;
	private int selectorIndexY;
	private boolean viewPartyMenu;
	private boolean partyItemMenu;
	private boolean partySelectItemMenu;
	private boolean heroSelectMenu;
	private boolean heroStatsMenu;
	private boolean heroSkillsMenu;
	private boolean heroEquipmentMenu;
	private boolean troopMenu;
	private boolean partyManagementMenu;
	private boolean armyManagementMenu;
	private boolean buildingManagementMenu;
	private boolean purchaseItemMenu;
	private boolean miscMenu;
	private Hero hero;
	private Item item;
	private Party party;
	
	public ExploreMenuPanel(String name, GameFrame frame, ExplorePanelManager manager, List<MenuItem> menuItems, int layer, List<MenuTab> tabItems, int selectorIndexX) {
		super(menuItems, layer);
		if (layer < 2) manager.setMenuPanel(this);
		this.manager = manager;
		this.setBounds(0, 0, GraphicsConstants.FRAME_WIDTH, GraphicsConstants.FRAME_HEIGHT);
		this.name = name;
		this.frame = frame;
		this.selectorIndexX = 0;
		this.selectorIndexY = 0;
		viewPartyMenu = false;
		partyItemMenu = false;
		partySelectItemMenu = false;
		heroSelectMenu = false;
		heroStatsMenu = false;
		heroSkillsMenu = false;
		heroEquipmentMenu = false;
		troopMenu = false;
		partyManagementMenu = false;
		armyManagementMenu = false;
		buildingManagementMenu = false;
		purchaseItemMenu = false;
		miscMenu = false;
		hero = null;
		source = "";
		
		//test
//		tabItems = new ArrayList<MenuItem>();
//		for (int i = 0; i < 8; i++) {
//			tabItems.add(new GenericMenuItem("test"));
//		}
		this.selectorIndexX = selectorIndexX;
		party = manager.getMap().getParty();
		if (menuItems == null) {
			viewPartyMenu = true;
			this.menuItems = getViewPartyMenu();
		}
		this.tabItems = tabItems;
	}
	
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        Color bgColor = new Color(0,0,0);
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, GraphicsConstants.FRAME_WIDTH, GraphicsConstants.FRAME_HEIGHT);
        drawMenu(g2d);
		Party party = ((ExplorePanelManager)manager).getMap().getParty();
		if (viewPartyMenu)
			drawViewPartyInfo(g2d);
		if (partyItemMenu)
			drawPartyItemInfo(g2d);
		if (partySelectItemMenu)
			drawPartySelectItemMenu(g2d);
		if (heroSelectMenu)
			drawHeroSelectInfo(g2d);
        if (troopMenu)
        	drawTroopInfo(g2d, party);
        if (heroStatsMenu)
        	drawHeroStatsInfo(g2d);
        if (heroSkillsMenu)
        	drawHeroSkillsInfo(g2d, party);
        if (heroEquipmentMenu)
        	drawHeroEquipmentInfo(g2d, party);
        if (partyManagementMenu)
        	drawPartyManagementInfo(g2d, party);
        if (armyManagementMenu)
        	drawArmyManagementInfo(g2d, party);
        if (buildingManagementMenu)
        	drawBuildingManagementInfo(g2d, party);
        if (purchaseItemMenu)
        	drawPurchaseItemInfo(g2d);
    }

    private TileMap makeBlankTileMap(int height, int width, TileSet tileSet) {
    	TileMap tileMap = new TileMap(height, width);
    	for (int y = 0; y < tileMap.getHeight(); y++) {
    		for (int x = 0; x < tileMap.getWidth(); x++) {
    			tileMap.setTile(new Tile("a01,01",tileSet), y, x);
    		}
    	}
    	return tileMap;
    }
    
    private static final int TAB_WIDTH = 3;
    private static final int TAB_HEIGHT = 2;
    private static final int BORDER_OFFSET = 2;
    
    private TileMap makeMenuBorder(TileMap tileMap, TileSet tileSet, int startY, int startX, int endY, int endX) {
    	for (int y = startY + 1; y < endY; y++) { //draw vertical borders
    		Tile tile = new Tile("a00,01", tileSet);
    		tileMap.setTile(tile, y, startX);
    		tile = new Tile("a02,01", tileSet);
    		tileMap.setTile(tile, y, endX);
    	}
		for (int x = startX + 1; x < endX; x++) { //draw horizontal borders
    		Tile tile = new Tile("a01,00", tileSet);
    		tileMap.setTile(tile, startY, x);
    		tile = new Tile("a01,02", tileSet);
    		tileMap.setTile(tile, endY, x);
		}
		//draw corners
		Tile tile = new Tile("a00,00", tileSet);
		tileMap.setTile(tile, startY, startX);
		tile = new Tile("a02,00", tileSet);
		tileMap.setTile(tile, startY, endX);
		tile = new Tile("a00,02", tileSet);
		tileMap.setTile(tile, endY, startX);
		tile = new Tile("a02,02", tileSet);
		tileMap.setTile(tile, endY, endX);
    	return tileMap;
    }
    
    private TileMap makeMenuTabs(TileMap tileMap, TileSet tileSet, int numTabs, int tabHeight, int tabWidth, int startX, int endX, int y) {
    	//make middle tabs
    	for (int i = 1; i < numTabs; i++) {
    		
    		Tile tile = new Tile("a05,00", tileSet);
    		if (selectorIndexX == 0 && i == 1)
        		tile = new Tile("a08,00", tileSet);
    		else if (selectorIndexX == i)
        		tile = new Tile("a09,00", tileSet);
    		else if (selectorIndexX == i - 1)
    			tile = new Tile("a08,00", tileSet);
    		tileMap.setTile(tile, y, startX + (2 * i));
    		
    		tile = new Tile("a05,01", tileSet);
    		if (selectorIndexX == 0 && i == 1)
        		tile = new Tile("a08,01", tileSet);
    		else if (selectorIndexX == i)
        		tile = new Tile("a09,01", tileSet);
    		else if (selectorIndexX == i - 1)
    			tile = new Tile("a08,01", tileSet);
    		tileMap.setTile(tile, y+1, startX + (2 * i));
    		
    		tile = new Tile("a01,00", tileSet);
    		if (selectorIndexX == i)
        		tile = new Tile("a09,02", tileSet);
    		tileMap.setTile(tile, y, startX + 1 + (2 * i));
    		
    		tile = new Tile("a01,00", tileSet);
    		if (selectorIndexX == i)
        		tile = new Tile("a08,02", tileSet);
    		tileMap.setTile(tile, y+1, startX + 1 + (2 * i));
    		
    	}
    	//make start tab
		Tile tile = new Tile("a03,00", tileSet);
		if (selectorIndexX == 0)
    		tile = new Tile("a06,00", tileSet);
		tileMap.setTile(tile, y, startX);
		tile = new Tile("a03,01", tileSet);
		if (selectorIndexX == 0)
    		tile = new Tile("a06,01", tileSet);
		tileMap.setTile(tile, y+1, startX);
		tile = new Tile("a01,00", tileSet);
		if (selectorIndexX == 0)
    		tile = new Tile("a09,02", tileSet);
		tileMap.setTile(tile, y, startX + 1);
		tile = new Tile("a01,00", tileSet);
		if (selectorIndexX == 0)
    		tile = new Tile("a08,02", tileSet);
		tileMap.setTile(tile, y+1, startX + 1);
    	//make end tab
		tile = new Tile("a04,00", tileSet);
		if (selectorIndexX == numTabs - 1)
			tile = new Tile("a07,00", tileSet);
		tileMap.setTile(tile, y, endX - 1);
		tile = new Tile("a04,02", tileSet);
		if (selectorIndexX == numTabs - 1)
			tile = new Tile("a07,02", tileSet);
		tileMap.setTile(tile, y+1, endX - 1);
		
//    	if (selectorIndexX > 0 && selectorIndexX < tabItems.size() - 1) {
//    		//change middle tab
//    	} else 
//    		tileMap.setTile(tile, rowIndex, colIndex);
//    	} else if (selectorIndexX == tabItems.size() - 1) {
//    		//change last tab
//    	}
    	return tileMap;
    }
    
    //automatically offsets the tabs to right justify on a menu border fullscreen. this function is actually useless so remove it in favor of the one above.
    private TileMap makeMenuTabs(TileMap tileMap, TileSet tileSet, int numTabs, int tabHeight, int tabWidth) {
    	return makeMenuTabs(tileMap, tileSet, numTabs, tabHeight, tabWidth, tileMap.getWidth() - (numTabs * tabWidth) - 1, tileMap.getWidth(), 1);
    }
    
	private void drawMenu(Graphics2D g2d) {
		TileSet tileSet = new TileSet("testMenu");
		List<MenuTab> relevantTabs = getRelevantTabs(source);
		TileMap tileMap = makeBlankTileMap((GraphicsConstants.FRAME_HEIGHT/GraphicsConstants.REGION_TILE_SIZE), (GraphicsConstants.FRAME_WIDTH/GraphicsConstants.REGION_TILE_SIZE), tileSet);
		tileMap = makeMenuBorder(tileMap, tileSet, 2, 0, GraphicsConstants.FRAME_HEIGHT/GraphicsConstants.REGION_TILE_SIZE - 1, GraphicsConstants.FRAME_WIDTH/GraphicsConstants.REGION_TILE_SIZE - 1);
		tileMap = makeMenuTabs(tileMap, tileSet, relevantTabs.size(), 3, 2);
//		int[][] y = {{0,0,0,2,0,2,0,1},
//					 {2,0,1,2,1,0,1,0},
//					 {1,1,1,1,1,1,1,1},
//					 {1,1,1,1,1,1,1,1},
//					 {1,1,1,1,1,1,1,1},
//					 {2,2,2,2,2,2,2,2}};
//		int[][] x = {{3,1,9,9,8,5,4,1},
//				 	 {3,1,9,8,8,1,4,2},
//				 	 {0,1,1,1,1,1,1,2},
//				 	 {0,1,1,1,1,1,1,2},
//				 	 {0,1,1,1,1,1,1,2},
//				 	 {0,1,1,1,1,1,1,2}};
//		for (int i = 0; i < y.length; i++) {
//			for (int j = 0; j < y[0].length; j++) {
//				Tile tile = new Tile("a0" + x[i][j] + ",0" + y[i][j], tileSet);
//				tileMap.setTile(tile, i, j);
//			}
//		}
		for (int rowIndex = 0; rowIndex < tileMap.getHeight(); rowIndex++) {
			for (int colIndex = 0; colIndex < tileMap.getWidth(); colIndex++) {
				Tile tile = tileMap.getTile(rowIndex, colIndex);
				tile.draw(g2d, rowIndex, colIndex, 0, 0, 0, 0, 0, 0);
			}
		}
		for (int i = 0; i < relevantTabs.size(); i++) {
            g2d.setColor(new Color(255,255,255));
            if (selectorIndexX == i)
            	g2d.setColor(new Color(0,0,0));
    		g2d.drawString(relevantTabs.get(i).getName(), (1905 - relevantTabs.size() * 128) + (i * 128), 120);
		}
		
    	for (int i = 0; i < menuItems.size(); i++) {
            g2d.setColor(new Color(255,255,255));
            if (selectorIndexY == i)
            	g2d.setColor(new Color(255,0,0));
    		g2d.drawString(menuItems.get(i).getName(), 50, 180 + i * 30);
    	}
    }
    
	private List<MenuTab> getRelevantTabs(String source) {
		List<MenuTab> menuTabs = new ArrayList<MenuTab>();
		for (MenuTab menuTab: tabItems) {
			if (menuTab.hasType(source))
				menuTabs.add(menuTab);
		}
		return menuTabs;
	}

	private void drawViewPartyInfo(Graphics2D g2d) {
		int incr = 0;
		int pixelY = 180;
		for (Unit unit: party.getUnitList()) {
			Stats permStats = unit.getPermStats();
	        g2d.setColor(new Color(255,255,255));
	        g2d.drawString(unit.getName(), 50, pixelY + 20 * incr);
	        g2d.drawString("HP:" + String.valueOf(unit.getCurrHP()) + "/" + String.valueOf(permStats.getHP()), 300, pixelY + 20 * incr);
	        incr++;
		    if (unit.getMP() > 0) 
			    g2d.drawString("MP:" + String.valueOf(unit.getCurrMP()) + "/" + String.valueOf(permStats.getMP()), 300, pixelY + 20 * incr);
		    g2d.drawString("Armor Score:" + String.valueOf(unit.getArmorScore()), 300, pixelY + 20 * incr);
	        incr += 2;
		}
	}
	
	private void drawPartyItemInfo(Graphics2D g2d) {
		int incr = 0;
		int pixelY = 180;
        g2d.setColor(new Color(255,255,255));
		for (ItemFactory itemFactory: party.getItemFactories()) {
			g2d.drawString("x" + party.getItemCount(itemFactory), 300, pixelY + 30 * incr);
			incr++;
		}
	}
	
	private void drawPartySelectItemMenu(Graphics2D g2d) {
		int incr = 0;
		int pixelY = 180;
        g2d.setColor(new Color(255,255,255));
        for (Unit unit: party.getUnitList()) {
        	Stats permStats = unit.getPermStats();
        	g2d.drawString("HP:" + String.valueOf(unit.getCurrHP()) + "/" + String.valueOf(permStats.getHP()), 300, pixelY + 30 * incr);
        	incr++;
        }

	}

	private void drawHeroSelectInfo(Graphics2D g2d) {
		if (selectorIndexY < 4) {
	        g2d.setColor(new Color(255,255,255));
	        Hero hero = (Hero)party.getHeroList().get(selectorIndexY);
	        g2d.drawString("Stat Points: " + hero.getStatPoints() + " SP", 300, 180);
		}
	}
	
    private void drawTroopInfo(Graphics2D g2d, Party party) {
        g2d.setColor(new Color(255,255,255));
        Army army = party.getArmy();
        Empire empire = party.getEmpire();
        List<UnitFactory> availableUnits = empire.getBuildableUnits();
        int pixelY = 180;
        int incr = 20;
        g2d.drawString("Gold: " + party.getGold() + " G", 300, pixelY);
        if (selectorIndexY < availableUnits.size()) {
            UnitFactory unitFactory = availableUnits.get(selectorIndexY);
            int level = unitFactory.getLevel();
        	int cost = calculateCost(level);
	        g2d.drawString("Cost: " + cost + " G", 300, pixelY + incr * 1);
	        g2d.drawString("Current Level: " + level, 300, pixelY + incr * 2);
	        Stats stats = unitFactory.getStats();
	        Stats statsPerLevel = unitFactory.getPerLevelStats();
	        g2d.drawString("Current Stats: ", 300, pixelY + incr * 4);
	        g2d.drawString("HP: ", 300, pixelY + incr * 5);
	        g2d.drawString("MP: ", 300,  pixelY + incr * 6);
	        g2d.drawString("Strength: ", 300,  pixelY + incr * 7);
	        g2d.drawString("Magic: ", 300,  pixelY + incr * 8);
	        g2d.drawString("Movement: ", 300,  pixelY + incr * 9);
	        g2d.drawString("Initiative: ", 300,  pixelY + incr * 10);
	        g2d.drawString("Stamina: ", 300,  pixelY + incr * 11);
	        g2d.drawString("" + (stats.getHP() + (statsPerLevel.getHP() * level)), 380,  pixelY + incr * 5);
	        g2d.drawString("" + (stats.getMP() + (statsPerLevel.getMP() * level)), 380,  pixelY + incr * 6);
	        g2d.drawString("" + (stats.getStrength() + (statsPerLevel.getStrength() * level)), 380,  pixelY + incr * 7);
	        g2d.drawString("" + (stats.getMagic() + (statsPerLevel.getMagic() * level)), 380,  pixelY + incr * 8);
	        g2d.drawString("" + (stats.getMovement() + (statsPerLevel.getMovement() * level)), 380,  pixelY + incr * 9);
	        g2d.drawString("" + (stats.getInitiative() + (statsPerLevel.getInitiative() * level)), 380,  pixelY + incr * 10);
	        g2d.drawString("" + (stats.getStamina() + (statsPerLevel.getStamina() * level)), 380,  pixelY + incr * 11);
	        g2d.drawString("On Level Up: ", 440,  pixelY + incr * 4);
	        g2d.setColor(new Color(0,255,0));
	        g2d.drawString("+" + statsPerLevel.getHP(), 440,  pixelY + incr * 5);
	        g2d.drawString("+" + statsPerLevel.getMP(), 440,  pixelY + incr * 6);
	        g2d.drawString("+" + statsPerLevel.getStrength(), 440,  pixelY + incr * 7);
	        g2d.drawString("+" + statsPerLevel.getMagic(), 440,  pixelY + incr * 8);
	        g2d.drawString("+" + statsPerLevel.getMovement(), 440,  pixelY + incr * 9);
	        g2d.drawString("+" + statsPerLevel.getInitiative(), 440,  pixelY + incr * 10);
	        g2d.drawString("+" + statsPerLevel.getStamina(), 440,  pixelY + incr * 11);
        }
    }
    
    private void drawHeroStatsInfo(Graphics2D g2d) {
        g2d.setColor(new Color(255,255,255));
        int pixelY = 180;
        int incr = 20;
        Stats permStats = hero.getPermStats();
        g2d.drawString("Stat Points: " + hero.getStatPoints() + " SP", 300,  pixelY + incr * 0);
        g2d.drawString("Current Stats: ", 300, pixelY + incr * 4);
        g2d.drawString("HP: ", 300, pixelY + incr * 5);
        g2d.drawString("MP: ", 300, pixelY + incr * 6);
        g2d.drawString("Strength: ", 300, pixelY + incr * 7);
        g2d.drawString("Magic: ", 300, pixelY + incr * 8);
        g2d.drawString("Movement: ", 300, pixelY + incr * 9);
        g2d.drawString("Initiative: ", 300, pixelY + incr * 10);
        g2d.drawString("Stamina: ", 300, pixelY + incr * 11);
        g2d.drawString("" + permStats.getHP(), 380, pixelY + incr * 5);
        g2d.drawString("" + permStats.getMP(), 380, pixelY + incr * 6);
        g2d.drawString("" + permStats.getStrength(), 380, pixelY + incr * 7);
        g2d.drawString("" + permStats.getMagic(), 380, pixelY + incr * 8);
        g2d.drawString("" + permStats.getMovement(), 380, pixelY + incr * 9);
        g2d.drawString("" + permStats.getInitiative(), 380, pixelY + incr * 10);
        g2d.drawString("" + permStats.getStamina(), 380, pixelY + incr * 11);
        if (selectorIndexY < 5) {
        	String type = "";
	    	if (selectorIndexY == 0)
	    		type = "HP";
	    	if (selectorIndexY == 1)
	    		type = "MP";
	    	if (selectorIndexY == 2)
	    		type = "Strength";
	    	if (selectorIndexY == 3)
	    		type = "Magic";
	    	if (selectorIndexY == 4)
	    		type = "Stamina";
	    	int level = hero.getStatLevel(type);
	    	int cost = calculateStatCost(level);
	        g2d.drawString("Cost: " + cost + " SP", 300, pixelY + incr * 1);
	        g2d.drawString("Current Upgrades: " + level, 300, pixelY + incr * 2);
	        g2d.drawString("On Upgrade: ", 440, pixelY + incr * 4);
	        Stats statsForNextLevel = hero.calculateNextLevelStats();
	        g2d.setColor(new Color(0,255,0));
	        if (selectorIndexY == 0) {
	        	g2d.drawString("+" + (statsForNextLevel.getHP() - permStats.getHP()), 440, pixelY + incr * 5);
	        }
	        if (selectorIndexY == 1) {
	        	g2d.drawString("+" + (statsForNextLevel.getMP() - permStats.getMP()), 440, pixelY + incr * 6);
	        }
	        if (selectorIndexY == 2) {
	        	g2d.drawString("+" + (statsForNextLevel.getStrength() - permStats.getStrength()), 440, pixelY + incr * 7);
	        }
	        if (selectorIndexY == 3) {
	        	g2d.drawString("+" + (statsForNextLevel.getMagic() - permStats.getMagic()), 440, pixelY + incr * 8);
	        }
	        if (selectorIndexY == 4) {
	        	g2d.drawString("+" + (statsForNextLevel.getStamina() - permStats.getStamina()), 440, pixelY + incr * 11);
	        }
        }
	}
    
    private void drawHeroSkillsInfo(Graphics2D g2d, Party party) {
    	List<Ability> skills = hero.getSkillList();
        g2d.setColor(new Color(255,255,255));
        int pixelY = 180;
        int incr = 20;
        g2d.drawString("Stat Points: " + hero.getStatPoints() + " SP", 300, pixelY + incr * 0);
        if (selectorIndexY < skills.size()) {
        	Ability skill = skills.get(selectorIndexY);
        	if (skill.hasParam("Equipment Type")) {
        		g2d.drawString("Equipment: " + skill.getEquipmentType(), 400, pixelY + incr * 1);
        	}
            g2d.drawString("Max Level: " + skill.get("Max Level"), 400, pixelY + incr * 2);
        	hero.setPotentialAbility(skill);
//        	Map<String, List<Effect>> effects = skill.getEffects("Modify Multiplier", "Ability Level");
        	Map<String, List<Effect>> effects = null;
        	if (skill.get("Type").equals("Passive")) {
        		effects = skill.getEffects("Origin", "Passive");
        	} else {
        		effects = skill.getEffects("Origin", "Ability");
        	}
	        List<Effect> effectList = null;
	    	int level = skill.getLevel();
	    	int cost = skill.calculateSkillCost(level);
	    	if (level >= 0)
	    		g2d.drawString("Current Skill: ", 250, pixelY + incr * 4);
	        g2d.drawString("Cost: " + cost + " SP", 250, pixelY + incr * 1);
	        if (level >= 0) {
	        	g2d.drawString("Skill Level: " + level, 250, pixelY + incr * 2);
	        	g2d.drawString("On Level Up: ", 440, pixelY + incr * 4);
	        }
	        pixelY += 100;
        	g2d.setColor(new Color(255,255,255));
        	if (skill.getName().equals("Pyromancer")) {
		        effectList = effects.get("HP");
		        Effect effect = effectList.get(0);
	        	g2d.drawString("Burn Magic Factor: ", 250, pixelY);
        		g2d.drawString("" + Math.abs((Math.round(effect.getModifyValue() * skill.getLevel() * 100.0) / 100.0)), 380, pixelY);
            	g2d.setColor(new Color(0,255,0));
        		g2d.drawString("+" + Math.abs(effect.getModifyValue()), 440, pixelY);
	        	pixelY += 20;
	        	g2d.setColor(new Color(255,255,255));
	        	g2d.drawString("Duration: ", 250, pixelY);
        		g2d.drawString("3 Turns", 380, pixelY);
        	}
	        if (skill.hasEffect("Key", "Stamina")) {
		        if (skill.getName().equals("Rejuvenate")) {
		        	g2d.setColor(new Color(255,255,255));
			        g2d.drawString("Magic Factor: ", 250, pixelY);
			        effectList = effects.get("Stamina");
			        double factor = effectList.get(0).getModifyValue();
			        for (int i = 0; i < skill.getLevel(); i++) {
			        	factor += effectList.get(1).getModifyValue();
			        }
			        g2d.drawString("+" + (Math.round(factor * 100.0) / 100.0), 380, pixelY);
			        if (effectList != null && level >= 0) {
			        	g2d.setColor(new Color(0,255,0));
			        	g2d.drawString("+" + effectList.get(1).getModifyValue(), 440, pixelY);
			        }
			        pixelY += 20;
	        	}
	        }
	        if (Effect.getEffects(effects, "Key", "Strength").size() > 0) {
	        	double placeholderFactor = 0;
	        	double placeholderIncrease = 0;
		        effectList = effects.get("Strength");
	        	g2d.drawString("Strength: ", 250, pixelY);
		        if (effectList != null) {
		        	double factor = 0; //factor is additional str
		        	double increase = 0;
		        	String symbol = "+";
		        	if (!Effect.hasEffects(effectList, "Modify Type", "Additive")) {
		        		factor = 1;
		        		increase = 1;
		        		symbol = "x";
		        	}
//		        	if (Effect.hasEffects(effectList, "Modify Multiplier", "Ability Level * HP"))
//		        		factor = 0.01;
		        	effectList.sort(Effect.getComparator());
		        	boolean placeholder = false;

		        	for (Effect effect: effectList) {
		        		factor = effect.modifyValue(factor, hero);
		        		if (effect.get("Modify Type").equals("Additive")) {
		        			increase += (double)effect.getModifyValue();
		        			placeholder = true;
		        		}
		        		if (effect.get("Modify Type").equals("Multiplicative"))
		        			increase *= (double)effect.getModifyValue();
		        		if (effect.get("Modify Multiplier").equals("Ability Level * HP")) {
		        			increase *= hero.getHP();
		        			placeholderFactor = skill.getLevel() * (double)effect.getModifyValue();
		        			placeholderIncrease = (double)effect.getModifyValue();
		        		}
		        	}
		        	if (skill.hasParam("Strength"))
		        		factor += (double)skill.get("Strength");
		        	if (placeholder) //this should be something else. what, i don't know. but it should be.
		        		g2d.drawString("+" + (Math.round((factor) * 100.0) / 100.0), 380, pixelY);
		        	else
		        		g2d.drawString("x" + (Math.round((factor) * 100.0) / 100.0), 380, pixelY);
		        	if (level >= 0) {
			        	g2d.setColor(new Color(0,255,0));
			        	g2d.drawString(symbol + (Math.round((increase) * 100.0) / 100.0), 440, pixelY);
		        	}

		        }
		        pixelY += 20;
//	        	if (Effect.hasEffects(effectList, "Modify Multiplier", "Ability Level * HP")) {
//	            	g2d.setColor(new Color(255,255,255));
//		        	g2d.drawString("HP to Strength: ", 250, pixelY);
//	        		g2d.drawString("+" + (Math.round((placeholderFactor) * 100.0) / 100.0), 380, pixelY);
//		        	g2d.setColor(new Color(0,255,0));
//	        		g2d.drawString("+" + (Math.round((placeholderIncrease) * 100.0) / 100.0), 380, pixelY);
//	        		pixelY += 20;
//	        	}
	        }
        	g2d.setColor(new Color(255,255,255));
	        if (Effect.getEffects(effects, "Key", "Strength Factor").size() > 0) {
		        effectList = effects.get("Strength Factor");
	        	g2d.drawString("Strength Factor: ", 250, pixelY);
		        if (effectList != null) {
		        	double factor = 0;
		        	double increase = 0;
		        	String symbol = "+";
		        	if (!Effect.hasEffects(effectList, "Modify Type", "Additive")) {
		        		factor = 1;
		        		increase = 1;
		        		symbol = "x";
		        	}
		        	effectList.sort(Effect.getComparator());
		        	for (Effect effect: effectList) {
		        		factor = effect.modifyValue(factor, hero);
		        		if (effect.get("Modify Type").equals("Additive"))
		        			increase += (double)effect.getModifyValue();
		        		if (effect.get("Modify Type").equals("Multiplicative"))
		        			increase *= (double)effect.getModifyValue();
		        	}
		        	if (skill.hasParam("Strength Factor"))
		        		factor += (double)skill.get("Strength Factor");
		        	if (skill.hasParam("Strength Factor")) //this should be something else. what, i don't know. but it should be.
		        		g2d.drawString("+" + (Math.round((factor) * 100.0) / 100.0), 380, pixelY);
		        	else
		        		g2d.drawString("x" + (Math.round((factor) * 100.0) / 100.0), 380, pixelY);
		        	if (level >= 0) {
			        	g2d.setColor(new Color(0,255,0));
			        	g2d.drawString(symbol + (Math.round((increase) * 100.0) / 100.0), 440, pixelY);
		        	}

		        }
		        pixelY += 20;
	        }
        	g2d.setColor(new Color(255,255,255));
	        if (Effect.getEffects(effects, "Key", "Magic Factor").size() > 0 && !skill.getName().equals("Rejuvenate")) {
		        effectList = effects.get("Magic Factor");
	        	g2d.drawString("Magic Factor: ", 250, pixelY);
		        if (effectList != null) {
		        	double factor = 0;
		        	double increase = 0;
		        	boolean placeholder = false;
		        	String symbol = "+";
		        	if (!Effect.hasEffects(effectList, "Modify Type", "Additive")) {
		        		factor = 1;
		        		increase = 1;
		        		symbol = "x";
		        	}
		        	effectList.sort(Effect.getComparator());
		        	for (Effect effect: effectList) {
		        		factor = effect.modifyValue(factor, hero);
		        		if (effect.get("Modify Type").equals("Additive")) {
		        			increase += (double)effect.getModifyValue();
		        			placeholder = true;
		        		}
		        		if (effect.get("Modify Type").equals("Multiplicative"))
		        			increase *= (double)effect.getModifyValue();
		        	}
		        	if (skill.hasParam("Magic Factor"))
		        		factor += (double)skill.get("Magic Factor");
		        	if (skill.get("Name").equals("Enchanter") && (int)skill.get("Level") < 0) 
		        		factor = effectList.get(1).getModifyValue();
		        	if (skill.get("Name").equals("Enchanter") && (int)skill.get("Level") >= 0)
		        		increase -= effectList.get(1).getModifyValue();
		        	if (placeholder) {
		        		g2d.drawString("+" + Math.abs((Math.round((factor) * 100.0) / 100.0)), 380, pixelY);
		        	
		        	} else
		        		g2d.drawString("x" + (Math.round((factor) * 100.0) / 100.0), 380, pixelY);
		        	if (level >= 0) {
		        		g2d.setColor(new Color(0,255,0));
		        		g2d.drawString(symbol + Math.abs((Math.round((increase) * 100.0) / 100.0)), 440, pixelY);
		        	}

		        }
		        pixelY += 20;
	        }
        	g2d.setColor(new Color(255,255,255));
	        if (skill.hasParam("Range")) {
	        	g2d.setColor(new Color(255,255,255));
		        g2d.drawString("Range: ", 250, pixelY);
		        g2d.drawString("" + skill.getRange(), 390, pixelY);
		        effectList = effects.get("Range");
		        if (effectList != null && level >= 0) {
		        	g2d.setColor(new Color(0,255,0));
		        	g2d.drawString("+" + effectList.get(0).getModifyValue(), 440, pixelY);
		        }
		        pixelY += 20;
	        }
        	g2d.setColor(new Color(255,255,255));
	        if (Effect.getEffects(effects, "Key", "Stamina Cost").size() > 0) {
		        effectList = effects.get("Stamina Cost");
	        	g2d.drawString("Stamina Cost: ", 250, pixelY);
		        if (effectList != null) {
		        	double factor = 0;
		        	double increase = 0;
		        	boolean placeholder = false;
		        	String symbol = "+";
		        	if (!Effect.hasEffects(effectList, "Modify Type", "Additive")) {
		        		factor = 1;
		        		increase = 1;
		        		symbol = "x";
		        	}
		        	effectList.sort(Effect.getComparator());
		        	for (Effect effect: effectList) {
		        		factor = effect.modifyValue(factor, hero);
		        		if (effect.get("Modify Type").equals("Additive")) {
		        			increase += (double)effect.getModifyValue();
		        			placeholder = true;
		        		}
		        		if (effect.get("Modify Type").equals("Multiplicative"))
		        			increase *= (double)effect.getModifyValue();
		        		if (effect.get("Modify Multiplier").equals("Ability Level * HP")) {
		        			increase *= hero.getHP();
		        		}
		        	}
		        	if (skill.hasParam("Stamina Cost"))
		        		factor += (int)skill.get("Stamina Cost");
		        	if (placeholder)
		        		g2d.drawString("+" + (Math.round((factor) * 100.0) / 100.0), 380, pixelY);
		        	else
		        		g2d.drawString("x" + (Math.round((factor) * 100.0) / 100.0), 380, pixelY);
		        	if (level >= 0) {
		        		g2d.setColor(new Color(255,0,0));
		        		g2d.drawString(symbol + (Math.round((increase) * 100.0) / 100.0), 440, pixelY);
		        	}
		        }
		        pixelY += 20;
	        }
        	g2d.setColor(new Color(255,255,255));
	        if (Effect.getEffects(effects, "Key", "MP Cost").size() > 0) {
		        effectList = effects.get("MP Cost");
	        	g2d.drawString("MP Cost: ", 250, pixelY);
		        if (effectList != null) {
		        	double factor = 0;
		        	double increase = 0;
		        	String symbol = "+";
		        	if (!Effect.hasEffects(effectList, "Modify Type", "Additive")) {
		        		factor = 1;
		        		increase = 1;
		        		symbol = "x";
		        	}
		        	effectList.sort(Effect.getComparator());
		        	for (Effect effect: effectList) {
		        		factor = effect.modifyValue(factor, hero);
		        		if (effect.get("Modify Type").equals("Additive"))
		        			increase += (double)effect.getModifyValue();
		        		if (effect.get("Modify Type").equals("Multiplicative"))
		        			increase *= (double)effect.getModifyValue();
		        	}
		        	if (skill.hasParam("MP Cost"))
		        		g2d.drawString("+" + (Math.round((factor + (int)skill.get("MP Cost")) * 100.0) / 100.0), 380, pixelY);
		        	else
		        		g2d.drawString("x" + (Math.round((factor) * 100.0) / 100.0), 380, pixelY);
		        	if (level >= 0) {
		        		g2d.setColor(new Color(255,0,0));
		        		g2d.drawString(symbol + increase, 440, pixelY);
		        	}
		        }
		        pixelY += 20;
	        }
        	g2d.setColor(new Color(255,255,255));
	        if (Effect.getEffects(effects, "Key", "HP").size() > 0) {
	        	if (!(Effect.getEffects(effects, "Type", "Create Stat Enemy Modifier").size() > 0)) {
			        effectList = effects.get("HP");
		        	g2d.drawString("HP Factor: ", 250, pixelY);
			        if (effectList != null) {
			        	double factor = 0;
			        	double increase = 0;
			        	String symbol = "+";
			        	if (!Effect.hasEffects(effectList, "Modify Type", "Additive")) {
			        		factor = 1;
			        		increase = 1;
			        		symbol = "x";
			        	}
			        	effectList.sort(Effect.getComparator());
			        	for (Effect effect: effectList) {
			        		factor = effect.modifyValue(factor, hero);
			        		if (effect.get("Modify Type").equals("Additive"))
			        			increase += (double)effect.getModifyValue();
			        		if (effect.get("Modify Type").equals("Multiplicative"))
			        			increase *= (double)effect.getModifyValue();
			        	}
			        	if (skill.hasParam("HP"))
			        		g2d.drawString("+" + (Math.round((factor + (int)skill.get("HP")) * 100.0) / 100.0), 380, pixelY);
			        	else
			        		g2d.drawString("x" + (Math.round((factor) * 100.0) / 100.0), 380, pixelY);
			        	if (level >= 0) {
			        		g2d.setColor(new Color(0,255,0));
			        		g2d.drawString(symbol + increase, 440, pixelY);
			        	}
			        } 
		        }
		        pixelY += 20;
	        }
	        if (skill.hasEffect("Key", "Movement")) {
	        	g2d.setColor(new Color(255,255,255));
		        g2d.drawString("Movement: ", 250, pixelY);
		        if (skill.hasEffect("Type", "Stat Enemy Modifier"))
			        g2d.drawString("-99", 380, pixelY);
		        else
	        		g2d.drawString("" + hero.getMovement(), 380, pixelY);
		        effectList = effects.get("Movement");
		        if (effectList != null && level >= 0) {
		        	g2d.setColor(new Color(0,255,0));
			        if (skill.hasEffect("Type", "Stat Enemy Modifier")) {
			        } else
		        	g2d.drawString("+" + effectList.get(0).getModifyValue(), 440, pixelY);
		        pixelY += 20;
		        }
	        }
	        if (skill.hasEffect("Key", "Initiative")) {
	        	g2d.setColor(new Color(255,255,255));
		        g2d.drawString("Initiative: ", 250, pixelY);
		        g2d.drawString("" + hero.getInitiative(), 380, pixelY);
		        effectList = effects.get("Initiative");
		        if (effectList != null && level >= 0) {
		        	g2d.setColor(new Color(0,255,0));
		        	g2d.drawString("x" + effectList.get(0).getModifyValue(), 440, pixelY);
		        pixelY += 20;
		        }
	        }
	        if (skill.hasEffect("Key", "Stamina Regen")) {
	        	g2d.setColor(new Color(255,255,255));
		        g2d.drawString("Stamina Regen: ", 250, pixelY);
		        effectList = effects.get("Stamina Regen");
		        double factor = 1;
		        for (int i = 0; i < skill.getLevel(); i++) {
		        	factor *= effectList.get(0).getModifyValue();
		        }
		        g2d.drawString("x" + (Math.round(factor * 100.0) / 100.0), 380, pixelY);
		        if (effectList != null && level >= 0) {
		        	g2d.setColor(new Color(0,255,0));
		        	g2d.drawString("x" + effectList.get(0).getModifyValue(), 440, pixelY);
		        pixelY += 20;
		        }
	        }
	        if (skill.hasEffect("Key", "Party Size")) {
	        	g2d.setColor(new Color(255,255,255));
		        g2d.drawString("Party Size: ", 250, pixelY);
		        effectList = effects.get("Party Size");
		        g2d.drawString("+" + effectList.get(1).modifyValue(effectList.get(0).modifyValue(0.0, hero), hero), 380, pixelY);
		        if (effectList != null && level >= 0) {
		        	g2d.setColor(new Color(0,255,0));
		        	g2d.drawString("+" + effectList.get(1).getModifyValue(), 440, pixelY);
		        pixelY += 20;
		        }
	        }
	        if (skill.hasParam("Area Of Effect")) {
	        	int[][] aoe = (int[][]) skill.get("Area Of Effect");
	        	g2d.drawString("Area Of Effect:", 250, pixelY);
				for (int row = 0; row < aoe.length; row++) {
					for (int col = 0; col < aoe[row].length; col++) {
						g2d.setColor(new Color(150,150,150));
						if (aoe[row][col] == 1)
							g2d.setColor(new Color(255,0,0));
	    				g2d.fillRect(380 + (col * 20), pixelY + (row * 20) - 7, 20, 20);
						g2d.setColor(new Color(0,0,0));
	    				GraphicsConstants.drawRect(g2d, 380 + (col * 20), pixelY + (row * 20) - 7, 20, 20, 2);
					}
				}
	        }
	        g2d.setColor(new Color(255,255,255));
		    GraphicsConstants.drawParagraph(g2d, skill.getDescription(), 300, 250, 460);
        	hero.wipePotentialAbility();
        }
	}
    
    private void drawHeroEquipmentInfo(Graphics2D g2d, Party party) {
        g2d.setColor(new Color(255,255,255));
        int pixelY = 180;
        int incr = 20;
        g2d.drawString("Gold: " + party.getGold() + " G", 300, pixelY + incr * 0);
     	List<Equipment> items = hero.getEquipmentList();
     	if (selectorIndexY < items.size()) {
     		Equipment item = items.get(selectorIndexY);    	
     		int level = (int)item.get("Level");
	    	int cost = item.calculateUpgradeCost(level);
	    	g2d.drawString("Current Item: ", 250, pixelY + incr * 4);
	        g2d.drawString("Cost: " + cost + " G", 250, pixelY + incr * 1);
	        g2d.drawString("Item Level: " + level, 250, pixelY + incr * 2);
            g2d.drawString("Max Level: " + item.get("Max Level"), 400, pixelY + incr * 2);
	        g2d.drawString("On Level Up: ", 440, pixelY + incr * 4);
	        pixelY += 100;
        	g2d.setColor(new Color(255,255,255));
	        if (item.hasEffect("Key", "Strength Factor")) {
	        	double strengthFactor = item.getEffectParam("Strength Factor");
	        	g2d.drawString("Strength Factor: ", 250, pixelY);
		        g2d.drawString("" + (Math.round(strengthFactor * 10.0) / 10.0), 380, pixelY);
		        Map<String, List<Effect>> effectMap = Effect.getEffects(item.getEffects("Key", "Strength Factor"), "Modify Multiplier", "Equipment Level");
		        List<Effect> effectList = effectMap.get("Strength Factor");
		        if (effectList != null) {
		        	g2d.setColor(new Color(0,255,0));
		        	g2d.drawString("+" + effectList.get(0).getModifyValue(), 440, pixelY);
		        }
		        pixelY += 20;
	        }
        	g2d.setColor(new Color(255,255,255));
	        if (item.hasEffect("Key", "Stamina Cost")) {
	        	double staminaCost = item.getEffectParam("Stamina Cost");
	        	g2d.drawString("Stamina Cost: ", 250, pixelY);
		        g2d.drawString("" + (Math.round(staminaCost * 10.0) / 10.0), 380, pixelY);
		        Map<String, List<Effect>> effectMap = Effect.getEffects(item.getEffects("Key", "Stamina Cost"), "Modify Multiplier", "Equipment Level");
		        List<Effect> effectList = effectMap.get("Stamina Cost");
		        if (effectList != null) {
		        	g2d.setColor(new Color(255,0,0));
		        	g2d.drawString("+" + effectList.get(0).getModifyValue(), 440, pixelY);
		        }
		        pixelY += 20;
	        }
        	g2d.setColor(new Color(255,255,255));
	        if (item.hasEffect("Key", "HP")) {
	        	double HP = item.getEffectParam("HP");
	        	g2d.drawString("HP: ", 250, pixelY);
		        g2d.drawString("+" + (Math.round(HP * 10.0) / 10.0), 380, pixelY);
		        Map<String, List<Effect>> effectMap = Effect.getEffects(item.getEffects("Key", "HP"), "Modify Multiplier", "Equipment Level");
		        List<Effect> effectList = effectMap.get("HP");
		        if (effectList != null) {
		        	g2d.setColor(new Color(0,255,0));
		        	g2d.drawString("+" + effectList.get(0).getModifyValue(), 440, pixelY);
		        }
		        pixelY += 20;
	        }
        	g2d.setColor(new Color(255,255,255));
	        if (item.hasEffect("Key", "Movement Stamina Cost")) {
	        	double staminaCost = item.getEffectParam("Movement Stamina Cost");
	        	g2d.drawString("Movement Cost: ", 250, pixelY);
		        g2d.drawString("+" + (Math.round(staminaCost * 10.0) / 10.0), 380, pixelY);
		        Map<String, List<Effect>> effectMap = Effect.getEffects(item.getEffects("Key", "Movement Stamina Cost"), "Modify Multiplier", "Equipment Level");
		        List<Effect> effectList = effectMap.get("Movement Stamina Cost");
		        if (effectList != null) {
		        	g2d.setColor(new Color(255,0,0));
		        	g2d.drawString("+" + effectList.get(0).getModifyValue(), 440, pixelY);
		        }
		        pixelY += 20;
	        }
	        if (item.hasEffect("Key", "Movement")) {
	        	double staminaCost = item.getEffectParam("Movement");
	        	g2d.drawString("Movement: ", 250, pixelY);
		        g2d.drawString("+" + (Math.round(staminaCost * 10.0) / 10.0), 380, pixelY);
		        Map<String, List<Effect>> effectMap = Effect.getEffects(item.getEffects("Key", "Movement"), "Modify Multiplier", "Equipment Level");
		        List<Effect> effectList = effectMap.get("Movement");
		        if (effectList != null) {
		        	g2d.setColor(new Color(0,255,0));
		        	g2d.drawString("+" + effectList.get(0).getModifyValue(), 440, pixelY);
		        }
		        pixelY += 20;
	        }
     	}
    }
    
    private void drawPartyManagementInfo(Graphics2D g2d, Party party) {
    	Army army = party.getArmy();
    	Empire empire = party.getEmpire();
        g2d.setColor(new Color(255,255,255));
        int pixelY = 180;
        int incr = 0;
        g2d.drawString("Party Size: " + party.getPartyValue() + "/" + party.getMaxPartySize(), 300, pixelY + incr * 30);
    	List<UnitFactory> availableUnits = empire.getBuildableUnits();
    	for (int i = 0; i < availableUnits.size(); i++) {
    		UnitFactory unitFactory = availableUnits.get(i);
    		if (i == selectorIndexY)
    			g2d.setColor(new Color(255,0,0));
    		g2d.drawString("" + party.getNumUnitsByType(unitFactory.getType()) + "/" + empire.getNumUnitsByType(unitFactory.getType()), 180, pixelY + incr * 30);
            g2d.setColor(new Color(255,255,255));
            incr++;
    	}
    }
    
    private void drawArmyManagementInfo(Graphics2D g2d, Party party) {
    	Empire empire = party.getEmpire();
    	g2d.setColor(new Color(255,255,255));
    	int pixelY = 180;
    	int incr = 0;    
        g2d.drawString("Manpower: " + empire.getManpower(), 300, pixelY + 20 * incr);
        
    	List<UnitFactory> availableUnits = empire.getBuildableUnits();
    	List<Building> buildings = empire.getBuildableUnitBuildings();
        for (int i = 0; i < availableUnits.size(); i++) {
    		UnitFactory unitFactory = availableUnits.get(i);
    		Building building = buildings.get(i);
    		if (i == selectorIndexY)
    			g2d.setColor(new Color(255,0,0));
    		g2d.drawString("" + empire.getNumUnitsByType(unitFactory.getType()) + "/" + building.getLevel(), 180, pixelY + incr * 30);
            g2d.setColor(new Color(255,255,255));
            incr++;
    	}
    }
    
    private void drawBuildingManagementInfo(Graphics2D g2d, Party party) {
    	Army army = party.getArmy();
    	Empire empire = party.getEmpire();
    	int pixelY = 180;
    	int incr = 20;        
        
        g2d.setColor(new Color(255,255,255));
        g2d.drawString("Gold: " + party.getGold() + " G", 300, pixelY + incr * 0);
        
        List<Building> constructedBuildings = army.getConstructedBuildings();
        List<UnitFactory> availableUnits = new ArrayList<UnitFactory>();
        int i = 0;
    	int unitCapacity = 0;
		for (Building constructedBuilding: constructedBuildings) {
			List<UnitFactory> recruitableUnits = army.checkAvailableUnits(constructedBuilding.getUnitsEnabled());
			for (UnitFactory unitFactory: recruitableUnits) {
				availableUnits.add(unitFactory);
				if (selectorIndexY == i) unitCapacity = constructedBuilding.getLevel();
				i++;
			}
		}
		List<BuildingFactory> availableBuildings = army.getAvailableBuildings();
		for (BuildingFactory availableBuilding: availableBuildings) {
			List<UnitFactory> recruitableUnits = army.checkAvailableUnits(availableBuilding.getUnitsEnabled());
			availableUnits.addAll(recruitableUnits);
		}
		if (selectorIndexY < constructedBuildings.size()) {
			Building building = constructedBuildings.get(selectorIndexY);
	        int cost = building.calculateCost();
	        g2d.drawString("Cost: " + cost + " G", 300, pixelY + incr * 1);
		} else {
			BuildingFactory buildingFactory = availableBuildings.get(selectorIndexY - constructedBuildings.size());
	        int cost = buildingFactory.getBaseCost();
	        g2d.drawString("Cost: " + cost + " G", 300, pixelY + incr * 1);

		}



//        if (selectorIndexY < availableUnits.size()) {
//        	UnitFactory unitFactory = availableUnits.get(selectorIndexY);
//        	String type = unitFactory.getType();
//        	int level = unitFactory.getLevel();
//	        int cost = calculateBuildingCost(empire.getBuildingLevel(type), party, unitFactory);
//	        g2d.drawString(type + " Level: " + level, 450, pixelY + incr * 2);
//	        g2d.drawString("Cost: " + cost + " G", 300, pixelY + incr * 1);
//	        g2d.drawString("Unit Capacity: " + unitCapacity , 300, pixelY + incr * 2);
//	        Stats stats = unitFactory.getStats();
//	        Stats statsPerLevel = unitFactory.getPerLevelStats();
//	        g2d.drawString(type + " Stats: ", 300, pixelY + incr * 3);
//	        g2d.drawString("HP: ", 300, pixelY + incr * 5);
//	        g2d.drawString("Strength: ", 300, pixelY + incr * 6);
//	        g2d.drawString("Movement: ", 300, pixelY + incr * 7);
//	        g2d.drawString("Initiative: ", 300, pixelY + incr * 8);
//	        g2d.drawString("Stamina: ", 300, pixelY + incr * 9);
//	        g2d.drawString("" + (stats.getHP() + (statsPerLevel.getHP() * level)), 380, pixelY + incr * 5);
//	        g2d.drawString("" + (stats.getStrength() + (statsPerLevel.getStrength() * level)), 380, pixelY + incr * 6);
//	        g2d.drawString("" + (stats.getMovement() + (statsPerLevel.getMovement() * level)), 380, pixelY + incr * 7);
//	        g2d.drawString("" + (stats.getInitiative() + (statsPerLevel.getInitiative() * level)), 380, pixelY + incr * 8);
//	        g2d.drawString("" + (stats.getStamina() + (statsPerLevel.getStamina() * level)), 380, pixelY + incr * 9);
//        }
    }
    
	
	private void drawPurchaseItemInfo(Graphics2D g2d) {
		Empire empire = party.getEmpire();
		List<ItemFactory> buildableItems = empire.getBuildableItems();
		int pixelY = 180;
		int incr = 0;
        g2d.setColor(new Color(255,255,255));
		if (buildableItems.size() > 0) {
			ItemFactory itemFactory = buildableItems.get(selectorIndexY);
	        g2d.drawString("Gold: " + party.getGold() + " G", 350, pixelY + 20 * incr);
	        g2d.drawString("Cost: " + itemFactory.getCost() + " G", 250, pixelY + 20 * incr);
			g2d.drawString("x" + party.getItemCount(itemFactory), 200, pixelY + 30 * incr);
		} else {
	        g2d.drawString("No items available at this time.", 50, pixelY + 20 * incr);
		}
	}
    
	private int calculateCost(int level) {
		int cost = 500;
		for (int i = 0; i < level; i++) {
			cost *= 1.15;
		}
		return cost;
	}
	
	private int calculateBuildingCost(BuildingFactory buildingFactory) {
		return calculateBuildingCost(buildingFactory.getBaseCost(), 0, buildingFactory.getCostScalar());
	}
	
	private int calculateBuildingCost(Building building) {
		return calculateBuildingCost(building.getBaseCost(), building.getLevel(), building.getCostScalar());
	}
	
	private int calculateBuildingCost(int baseCost, int level, double costScalar) {
		double cost = baseCost;
		for (int i = 0; i < level; i++) {
			cost *= costScalar;
		}
		return (int)cost;
	}

	private int calculateStatCost(int level) {
		int cost = 250;
		for (int i = 0; i < level; i++) {
			cost *= 1.15;
		}
		return cost;
	}
	
	public void keyPressed(KeyEvent e) {
		if (subMenu != null) {
			subMenu.keyPressed(e);
			return;
		}
		if (partyManagementMenu) {
			partyManagementKeyPressed(e);
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
			case 37:
				moveLeft();
				break;
			case 39:
				moveRight();
				break;
			case 69:
			case 10:
				selectItem();
				break;
			case 84:
				//close all menus?
				closeAllMenus();
			case 81:
			case 27:
				closeMenu();
				break;
		}
		this.repaint();
	}
	
	private void partyManagementKeyPressed(KeyEvent e) {
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
			case 65:
				moveLeftNumber();
				break;
			case 37:
				moveLeft();
				break;
			case 68:
				moveRightNumber();
				break;
			case 39:
				moveRight();
				break;
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
	
	private void moveLeft() {
		List<MenuTab> menuTabs = getRelevantTabs(source);
		if (selectorIndexX > 0)
			selectorIndexX--;
		else
			selectorIndexX = menuTabs.size() - 1;
		ExploreMenuPanel menuPanel = (ExploreMenuPanel) closeAllSubMenus();
		menuPanel.clearBooleans();
		menuPanel.setSelectorIndexX(selectorIndexX);
		menuPanel.setSelectorIndexY(0);
		menuPanel.selectTabItem();
	}
	
	private void moveRight() {
		List<MenuTab> menuTabs = getRelevantTabs(source);
		if (selectorIndexX < menuTabs.size() - 1)
			selectorIndexX++;
		else
			selectorIndexX = 0;
		ExploreMenuPanel menuPanel = (ExploreMenuPanel) closeAllSubMenus();
		menuPanel.clearBooleans();
		menuPanel.setSelectorIndexX(selectorIndexX);
		menuPanel.setSelectorIndexY(0);

		menuPanel.selectTabItem();
	}
	
	public void setSelectorIndexX(int x) {
		selectorIndexX = x;
	}
	
	public void setSelectorIndexY(int y) {
		selectorIndexY = y;
	}
	
	private void moveLeftNumber() {
		List<UnitFactory> availableUnits = party.getEmpire().getBuildableUnits();
		String type = availableUnits.get(selectorIndexY).getType();
    	if (party.getNumUnitsByType(type) > 0) {
    		Unit unit = party.removeUnit(type);
    		party.getArmy().addUnit(unit);
		}
    }
	
	private void moveRightNumber() {
		List<UnitFactory> availableUnits = party.getEmpire().getBuildableUnits();
		UnitFactory unitFactory = availableUnits.get(selectorIndexY);
		String type = unitFactory.getType();
	    if (party.getPartyValue() + unitFactory.getSupplyCost() <= party.getMaxPartySize() && party.getArmy().getNumUnitsByType(type) > 0) {
    		Unit unit = party.getArmy().removeUnit(type);
			party.addUnit(unit);
		}
	}
	
	public void selectItem() {
		menuItems.get(selectorIndexY).execute(this);
	}
	
	public void selectTabItem() {
		List<MenuTab> menuTabs = getRelevantTabs(source);
		menuTabs.get(selectorIndexX).execute(this);
	}

	public static List<MenuTab> getStandardMenu() {
		List<MenuTab> menuItems = new ArrayList<MenuTab>();
		MenuTab item = new MenuTab("Standard, Base"){
			private String name = "View Party";
			
			@Override
			public void execute(GamePanel panel) {
				ExploreMenuPanel menuPanel = (ExploreMenuPanel) panel;
				Party party = ((ExplorePanelManager)menuPanel.getManager()).getMap().getParty();
				menuPanel.openViewPartyMenu();
			}

			@Override
			public String getName() {
				return name;
			}	
		};
		menuItems.add(item);
		item = new MenuTab("Standard, Base"){
			private String name = "Use Items";
			
			@Override
			public void execute(GamePanel panel) {
				ExploreMenuPanel menuPanel = (ExploreMenuPanel) panel;
				Party party = ((ExplorePanelManager)menuPanel.getManager()).getMap().getParty();
				menuPanel.openPartyItemMenu();
			}

			@Override
			public String getName() {
				return name;
			}	
		};
		menuItems.add(item);
		item = new MenuTab("Standard, Base") {
			private String name = "Upgrade Heroes";
			
			@Override
			public void execute(GamePanel panel) {
				ExploreMenuPanel menuPanel = (ExploreMenuPanel) panel;
				Party party = ((ExplorePanelManager)menuPanel.getManager()).getMap().getParty();
				menuPanel.openHeroSelectMenu(party);
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
		item = new MenuTab("Base") {
			private String name = "Upgrade Troops";
			
			@Override
			public void execute(GamePanel panel) {
				ExploreMenuPanel menuPanel = (ExploreMenuPanel) panel;
				menuPanel.openTroopMenu();
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
		item = new MenuTab("Base") {
			private String name = "Manage Party";
			
			@Override
			public void execute(GamePanel panel) {
				ExploreMenuPanel menuPanel = (ExploreMenuPanel) panel;
				Party party = ((ExplorePanelManager)menuPanel.getManager()).getMap().getParty();
				menuPanel.openPartyManagementMenu(party);
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
		item = new MenuTab("Base") {
			private String name = "Manage Army";
			
			@Override
			public void execute(GamePanel panel) {
				ExploreMenuPanel menuPanel = (ExploreMenuPanel) panel;
				Party party = ((ExplorePanelManager)menuPanel.getManager()).getMap().getParty();
				menuPanel.openArmyManagementMenu(party);
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
		item = new MenuTab("Base") {
			private String name = "Manage Buildings";
			
			@Override
			public void execute(GamePanel panel) {
				ExploreMenuPanel menuPanel = (ExploreMenuPanel) panel;
				Party party = ((ExplorePanelManager)menuPanel.getManager()).getMap().getParty();
				menuPanel.openBuildingManagementMenu(party);
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
		item = new MenuTab("Base") {
			private String name = "Purchase Items";
			
			@Override
			public void execute(GamePanel panel) {
				ExploreMenuPanel menuPanel = (ExploreMenuPanel) panel;
				Party party = ((ExplorePanelManager)menuPanel.getManager()).getMap().getParty();
				menuPanel.openPurchaseItemMenu();
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
		item = new MenuTab("Standard, Base") {
			private String name = "Misc";
			
			@Override
			public void execute(GamePanel panel) {
				ExploreMenuPanel menuPanel = (ExploreMenuPanel) panel;
//				Party party = ((ExplorePanelManager)menuPanel.getManager()).getMap().getParty();
				menuPanel.openMiscMenu();
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
//    	menuItems.add(exitMenuItem());
		return menuItems;
	}

	private void openViewPartyMenu() {
		menuItems = getViewPartyMenu();
		setViewPartyMenu(true);
		frame.refresh();
	}
	
	private void openPartyItemMenu() {
		menuItems = getPartyItemMenu();
		setPartyItemMenu(true);
		frame.refresh();
	}
	
	public void openPartySelectItemMenu(Item item) {
		List<MenuItem> menuItems = getPartySelectItemMenu();
    	ExploreMenuPanel partySelectItemMenu = new ExploreMenuPanel("Party Select Item Menu", frame, (ExplorePanelManager) manager, menuItems, layer + 1, tabItems, selectorIndexX);
    	partySelectItemMenu.setSource(source);
    	partySelectItemMenu.setItem(item);
//    	this.item = item;
    	subMenu = partySelectItemMenu;
    	partySelectItemMenu.superMenu = this;
    	partySelectItemMenu.displayPanel();
		partySelectItemMenu.setPartySelectItemMenu(true);
		frame.refresh();
	}

	private void openTroopMenu() {
		menuItems = getTroopMenu();
		setTroopMenu(true);
//		List<MenuItem> menuItems = getTroopMenu();
//    	RegionMenuPanel troopMenu = new RegionMenuPanel("Troop Menu", frame, (ExplorePanelManager) manager, menuItems, layer + 1, tabItems, selectorIndexX);
//    	subMenu = troopMenu;
//    	troopMenu.superMenu = this;
//    	troopMenu.displayPanel();
//    	troopMenu.setTroopMenu(true);
		frame.refresh();
	}
	
	private void openPartyManagementMenu(Party party) {
		menuItems = getPartyManagementMenu();
		setPartyManagementMenu(true);
//		List<MenuItem> menuItems = getPartyManagementMenu();
//    	RegionMenuPanel partyManagementMenu = new RegionMenuPanel("Party Management Menu", frame, (ExplorePanelManager) manager, menuItems, layer + 1, tabItems, selectorIndexX);
//    	subMenu = partyManagementMenu;
//    	partyManagementMenu.superMenu = this;
//    	partyManagementMenu.displayPanel();
//    	partyManagementMenu.setPartyManagementMenu(true);
//    	partyManagementMenu.setParty(party);
		frame.refresh();
	}
	
	private void openArmyManagementMenu(Party party) {
//		List<MenuItem> menuItems = getArmyManagementMenu();
		menuItems = getArmyManagementMenu();
//    	RegionMenuPanel armyManagementMenu = new RegionMenuPanel("Army Management Menu", frame, (ExplorePanelManager) manager, menuItems, layer + 1, tabItems, selectorIndexX);
//    	subMenu = armyManagementMenu;
//    	armyManagementMenu.superMenu = this;
//    	armyManagementMenu.displayPanel();
//    	armyManagementMenu.setArmyManagementMenu(true);
    	setArmyManagementMenu(true);
		//    	armyManagementMenu.setParty(party);
		frame.refresh();
	}
	
	private void openBuildingManagementMenu(Party party) {
		menuItems = getBuildingManagementMenu();
    	setBuildingManagementMenu(true);
		frame.refresh();
	}
	
	private void openHeroSelectMenu(Party party) {
//		List<MenuItem> menuItems = getHeroSelectMenu(party);
		menuItems = getHeroSelectMenu();
		setHeroSelectMenu(true);
//    	RegionMenuPanel heroMenu = new RegionMenuPanel("Hero Select Menu", frame, (ExplorePanelManager) manager, menuItems, layer + 1, tabItems, selectorIndexX);
//    	subMenu = heroMenu;
//    	heroMenu.superMenu = this;
//    	heroMenu.displayPanel();
//    	heroMenu.setHeroSelectMenu(true);
//    	heroMenu.setParty(party);
		frame.refresh();
	}
	
	private void openHeroMenu(Hero hero) {
		List<MenuItem> menuItems = getHeroMenu(hero);
    	ExploreMenuPanel heroMenu = new ExploreMenuPanel("Hero Menu", frame, (ExplorePanelManager) manager, menuItems, layer + 1, tabItems, selectorIndexX);
    	heroMenu.setSource(source);
    	heroMenu.setHero(hero);
    	subMenu = heroMenu;
    	heroMenu.superMenu = this;
    	heroMenu.displayPanel();
		frame.refresh();
	}
	
	private void openHeroStatsMenu(Hero hero) {
		List<MenuItem> menuItems = getHeroStatsMenu(hero);
    	ExploreMenuPanel heroStatsMenu = new ExploreMenuPanel("Hero Stats Menu", frame, (ExplorePanelManager) manager, menuItems, layer + 1, tabItems, selectorIndexX);
    	heroStatsMenu.setSource(source);
    	heroStatsMenu.setHero(hero);
    	subMenu = heroStatsMenu;
    	heroStatsMenu.superMenu = this;
    	heroStatsMenu.displayPanel();
    	heroStatsMenu.setHeroStatsMenu(true);
		frame.refresh();
	}
	
	private void openHeroSkillsMenu(Hero hero) {
		List<MenuItem> menuItems = getHeroSkillsMenu(hero);
    	ExploreMenuPanel heroSkillsMenu = new ExploreMenuPanel("Hero Skills Menu", frame, (ExplorePanelManager) manager, menuItems, layer + 1, tabItems, selectorIndexX);
    	heroSkillsMenu.setSource(source);
    	heroSkillsMenu.setHero(hero);
    	subMenu = heroSkillsMenu;
    	heroSkillsMenu.superMenu = this;
    	heroSkillsMenu.displayPanel();
    	heroSkillsMenu.setHeroSkillsMenu(true);
		frame.refresh();
	}
	
	private void openHeroEquipmentMenu(Hero hero) {
		List<MenuItem> menuItems = getHeroEquipmentMenu(hero);
    	ExploreMenuPanel heroEquipmentMenu = new ExploreMenuPanel("Hero Equipment Menu", frame, (ExplorePanelManager) manager, menuItems, layer + 1, tabItems, selectorIndexX);
    	heroEquipmentMenu.setSource(source);
    	heroEquipmentMenu.setHero(hero);
    	subMenu = heroEquipmentMenu;
    	heroEquipmentMenu.superMenu = this;
    	heroEquipmentMenu.displayPanel();
    	heroEquipmentMenu.setHeroEquipmentMenu(true);
		frame.refresh();
	}
	
	private void openPurchaseItemMenu() {
		menuItems = getPurchaseItemMenu();
		setPurchaseItemMenu(true);
		frame.refresh();
	}
	
	private void openMiscMenu() {
		menuItems = getMiscMenu();
		setMiscMenu(true);
//		List<MenuItem> menuItems = getMiscMenu();
//		RegionMenuPanel miscMenu = new RegionMenuPanel("Misc Menu", frame, (ExplorePanelManager) manager, menuItems, layer + 1, tabItems, selectorIndexX);
//    	subMenu = miscMenu;
//    	miscMenu.superMenu = this;
//    	miscMenu.displayPanel();
//    	miscMenu.setMiscMenu(true);
		frame.refresh(); 	
	}
	
	private List<MenuItem> getViewPartyMenu() {
		return new ArrayList<MenuItem>();
	}
	
	private List<MenuItem> getPartyItemMenu() {
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
		for (ItemFactory itemFactory: party.getItemFactories()) {
			menuItems.add(itemFactory);
		}
		return menuItems;
	}
	
	private List<MenuItem> getPartySelectItemMenu() {
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
		List<Unit> unitList = party.getUnitList();
		MenuItem menuItem = null;
		for (Unit unit: unitList) {
			menuItem = new MenuItem() {
				private String name = unit.getName();
				
				@Override
				public void execute(GamePanel panel) {
					ExploreMenuPanel menuPanel = (ExploreMenuPanel) panel;
					
					unit.useItem(menuPanel.getItem());
					menuPanel.setItem(null);
					menuPanel.closeMenu();
				}

				@Override
				public String getName() {
					return name;
				}
			};
			menuItems.add(menuItem);
		}
		return menuItems;
	}
	
	private List<MenuItem> getHeroSelectMenu() {
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
		List<Hero> heroList = party.getHeroList();
		MenuItem item = null;
		for (Hero hero: heroList) {
			item = new MenuItem() {
				private String name = hero.getName();
				
				@Override
				public void execute(GamePanel panel) {
					ExploreMenuPanel menuPanel = (ExploreMenuPanel) panel;
					menuPanel.openHeroMenu(hero);
				}

				@Override
				public String getName() {
					return name;
				}
			};
			menuItems.add(item);
		}
		return menuItems;
	}
	
	private List<MenuItem> getTroopMenu() {
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
		Army army = party.getArmy();
		Empire empire = party.getEmpire();
		List<UnitFactory> availableUnits = empire.getBuildableUnits();
		for (UnitFactory unitFactory: availableUnits) {
			MenuItem item = new MenuItem() {
				private String name = "Level " + unitFactory.getType();
				
				@Override
				public void execute(GamePanel panel) {
					ExploreMenuPanel menuPanel = (ExploreMenuPanel) panel;
					Party party = ((ExplorePanelManager)menuPanel.getManager()).getMap().getParty();
		        	int level = unitFactory.getLevel();
		        	int cost = calculateCost(level);
					if (party.getGold() >= cost) {
						unitFactory.incrementLevel();
						party.subtractGold(cost);
						party.updateUnitLevels(unitFactory);
					}
				}
	
				@Override
				public String getName() {
					return name;
				}
			};
			menuItems.add(item);
		}
		return menuItems;
	}
	
	private List<MenuItem> getPartyManagementMenu() {
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
		Army army = party.getArmy();
		Empire empire = party.getEmpire();
		List<UnitFactory> availableUnits = empire.getBuildableUnits();
		for (UnitFactory unit: availableUnits) {
			MenuItem item = new MenuItem() {
				private String name = "< " + unit.getType() +" >";
				
				@Override
				public void execute(GamePanel panel) {
				}

				@Override
				public String getName() {
					return name;
				}
			};
			menuItems.add(item);
		}	
		return menuItems;
	}
	
	private List<MenuItem> getArmyManagementMenu() {
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
		Army army = party.getArmy();
		Empire empire = party.getEmpire();
		List<Building> buildings = empire.getBuildings();
		List<UnitFactory> availableUnits = empire.getBuildableUnits();
//		for (UnitFactory unitFactory: availableUnits) {
//		for (Building building: buildings) {
		for (UnitFactory unitFactory: availableUnits) {
//			UnitFactory unitFactory = building.getUnitsEnabled().get(0);
			MenuItem item = new MenuItem() {
				private String name = "Train " + unitFactory.getType();
				
				@Override
				public void execute(GamePanel panel) {
//					ExploreMenuPanel menuPanel = (ExploreMenuPanel) panel;
//					Party party = ((ExplorePanelManager)menuPanel.getManager()).getMap().getParty();
//					Army army = party.getArmy();
					if (empire.getManpower() >= unitFactory.getSupplyCost()/* && empire.getNumUnitsByType(unitFactory.getType()) < building.getLevel()*/) {
						Unit unit = empire.createUnit(unitFactory);
						army.addUnit(unit);
						empire.spendManpower(unitFactory.getSupplyCost());
					}
				}
	
				@Override
				public String getName() {
					return name;
				}
			};
			menuItems.add(item);
		}
		return menuItems;
	}
	
	private List<MenuItem> getBuildingManagementMenu() {
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
		Army army = party.getArmy();
		Empire empire = party.getEmpire();
		List<BuildingFactory> availableBuildings = army.getAvailableBuildings();
		List<Building> constructedBuildings = (List<Building>) army.getConstructedBuildings();
		for (Building constructedBuilding: constructedBuildings) {
			if (constructedBuilding.getLevel() < constructedBuilding.getMaxLevel()) {
				List<UnitFactory> recruitableUnits = army.checkAvailableUnits(constructedBuilding.getUnitsEnabled());
				MenuItem item = new MenuItem() {
					private String name = "Expand " + constructedBuilding.getName();
					
					@Override
					public void execute(GamePanel panel) {
						ExploreMenuPanel menuPanel = (ExploreMenuPanel) panel;
						Party party = ((ExplorePanelManager)menuPanel.getManager()).getMap().getParty();
						Army army = party.getArmy();
						int buildingLevel = constructedBuilding.getLevel();
						int cost = 0;
			        	cost = calculateBuildingCost(constructedBuilding);
						if (party.getGold() >= cost) {
							constructedBuilding.incrementLevel();
							party.subtractGold(cost);
						}
					}
		
					@Override
					public String getName() {
						return name;
					}
				};
				menuItems.add(item);
			}
		}
		for (BuildingFactory availableBuilding: availableBuildings) {
			List<UnitFactory> recruitableUnits = army.checkAvailableUnits(availableBuilding.getUnitsEnabled());
			MenuItem item = new MenuItem() {
				private String name = "Build " + availableBuilding.getName();
				
				@Override
				public void execute(GamePanel panel) {
					ExploreMenuPanel menuPanel = (ExploreMenuPanel) panel;
					Party party = ((ExplorePanelManager)menuPanel.getManager()).getMap().getParty();
//					Army army = party.getArmy();
//		        	int number = army.getNumUnitByType(recruitableUnits.get(0));
		        	int cost = calculateBuildingCost(availableBuilding);
					if (party.getGold() >= cost) {
						empire.createBuilding(availableBuilding);
						party.subtractGold(cost);
						menuPanel.selectTabItem();
					}
				}
	
				@Override
				public String getName() {
					return name;
				}
			};
			menuItems.add(item);
		}
		return menuItems;
	}

	private List<MenuItem> getHeroMenu(Hero hero) {
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
		MenuItem item = new MenuItem() {
			private String name = "Upgrade Stats";
			
			@Override
			public void execute(GamePanel panel) {
				ExploreMenuPanel menuPanel = (ExploreMenuPanel) panel;
				menuPanel.openHeroStatsMenu(hero);
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
		item = new MenuItem() {
			private String name = "Upgrade Skills";
			
			@Override
			public void execute(GamePanel panel) {
				ExploreMenuPanel menuPanel = (ExploreMenuPanel) panel;
				menuPanel.openHeroSkillsMenu(hero);			
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
		item = new MenuItem() {
			private String name = "Upgrade Equipment";
			
			@Override
			public void execute(GamePanel panel) {
				ExploreMenuPanel menuPanel = (ExploreMenuPanel) panel;
				menuPanel.openHeroEquipmentMenu(hero);			
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
//		item = new MenuItem() {
//			private String name = "Respec";
//			
//			@Override
//			public void execute(GamePanel panel) {
//				RegionMenuPanel menuPanel = (RegionMenuPanel) panel;
//				hero.respec();		
//			}
//
//			@Override
//			public String getName() {
//				return name;
//			}
//		};
//		menuItems.add(item);
    	menuItems.add(exitMenuItem());
		return menuItems;
	}
	
	
	private List<MenuItem> getHeroStatsMenu(Hero hero) {
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
		MenuItem item = new MenuItem() {
			private String name = "Increase HP";
			
			@Override
			public void execute(GamePanel panel) {
				ExploreMenuPanel menuPanel = (ExploreMenuPanel) panel;
//				menuPanel.setHero(hero);
	        	int level = hero.getStatLevel("HP");
	        	int cost = calculateStatCost(level);
				if (hero.getStatPoints() >= cost) {
					hero.subtractStatPoints(cost);
	        		hero.levelStat("HP");
				}
	        	
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
		item = new MenuItem() {
			private String name = "Increase MP";
			
			@Override
			public void execute(GamePanel panel) {
				ExploreMenuPanel menuPanel = (ExploreMenuPanel) panel;
//				menuPanel.setHero(hero);
	        	int level = hero.getStatLevel("MP");
	        	int cost = calculateStatCost(level);
				if (hero.getStatPoints() >= cost) {
					hero.subtractStatPoints(cost);
	        		hero.levelStat("MP");
				}
	        	
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
		item = new MenuItem() {
			private String name = "Increase Strength";
			
			@Override
			public void execute(GamePanel panel) {
				ExploreMenuPanel menuPanel = (ExploreMenuPanel) panel;
//				menuPanel.setHero(hero);
	        	int level = hero.getStatLevel("Strength");
	        	int cost = calculateStatCost(level);
				if (hero.getStatPoints() >= cost) {
					hero.subtractStatPoints(cost);
					hero.levelStat("Strength");
				}
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
		item = new MenuItem() {
			private String name = "Increase Magic";
			
			@Override
			public void execute(GamePanel panel) {
				ExploreMenuPanel menuPanel = (ExploreMenuPanel) panel;
//				menuPanel.setHero(hero);
	        	int level = hero.getStatLevel("Magic");
	        	int cost = calculateStatCost(level);
				if (hero.getStatPoints() >= cost) {
					hero.subtractStatPoints(cost);
					hero.levelStat("Magic");
				}
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
		item = new MenuItem() {
			private String name = "Increase Stamina";
			
			@Override
			public void execute(GamePanel panel) {
				ExploreMenuPanel menuPanel = (ExploreMenuPanel) panel;
//				menuPanel.setHero(hero);
	        	int level = hero.getStatLevel("Stamina");
	        	int cost = calculateStatCost(level);
				if (hero.getStatPoints() >= cost) {
					hero.subtractStatPoints(cost);
					hero.levelStat("Stamina");
				}
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

	private List<MenuItem> getHeroSkillsMenu(Hero hero) {
		List<Ability> skills = hero.getSkillList();
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
//    	List<MenuItem> menuItems = new ArrayList<MenuItem>(abilities);
		for (Ability skill: skills) {
			MenuItem item = new MenuItem() {
				private String name = skill.getName();
				
				@Override
				public void execute(GamePanel panel) {
					ExploreMenuPanel menuPanel = (ExploreMenuPanel) panel;
		        	int level = skill.getLevel();
		        	int cost = skill.calculateSkillCost(level);
					if (hero.getStatPoints() >= cost && skill.getLevel() < skill.getMaxLevel()) {
						hero.subtractStatPoints(cost);
						skill.levelUp();
//						hero.healUnit();
						hero.updateStats();
					}
				}

				@Override
				public String getName() {
					return name;
				}
			};
			menuItems.add(item);
		}
    	menuItems.add(exitMenuItem());
		return menuItems;
	}
	

	
	private List<MenuItem> getHeroEquipmentMenu(Hero hero) {
		List<Equipment> equipmentList = hero.getEquipmentList();
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
//    	List<MenuItem> menuItems = new ArrayList<MenuItem>(abilities);
		for (Equipment equipment: equipmentList) {
			MenuItem item = new MenuItem() {
				private String name = equipment.getName();
				
				@Override
				public void execute(GamePanel panel) {
					ExploreMenuPanel menuPanel = (ExploreMenuPanel) panel;
					Party party = ((ExplorePanelManager)menuPanel.getManager()).getMap().getParty();
		        	int level = (int)equipment.get("Level");
		        	int cost = equipment.calculateUpgradeCost(level);
					if (party.getGold() >= cost && (int)equipment.get("Level") < (int)equipment.get("Max Level")) {
						party.subtractGold(cost);
						hero.equip(equipment);
						equipment.upgrade(hero);
//						hero.healUnit();
						hero.updateStats();
					}
				}

				@Override
				public String getName() {
					return name;
				}
			};
			menuItems.add(item);
		}
    	menuItems.add(exitMenuItem());
		return menuItems;
	}
	
	private List<MenuItem> getPurchaseItemMenu() {
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
		MenuItem menuItem = null;
//		for (Building building: party.getEmpire().getBuildingsByType("Merchant")) {
//			for (ItemFactory itemFactory: building.getItemsSold()) {
//				itemFactories.add(itemFactory);
//			}
//		}
		for (ItemFactory itemFactory: party.getEmpire().getBuildableItems()) {
			menuItem = new MenuItem() {
				private String name = itemFactory.getName();
			
				@Override
				public void execute(GamePanel panel) {
					ExploreMenuPanel menuPanel = (ExploreMenuPanel) panel;
					Party party = ((ExplorePanelManager)menuPanel.getManager()).getMap().getParty();
					if (party.getGold() > itemFactory.getCost()) {
						party.addItem(itemFactory);
						party.subtractGold(itemFactory.getCost());
					}
				}
				
				@Override
				public String getName() {
					return name;
				}
			};
			menuItems.add(menuItem);
		}
		return menuItems;

	}
	
	private List<MenuItem> getMiscMenu() {
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
		MenuItem item = null;
		item = new MenuItem() {
			private String name = "Respec";
		
			@Override
			public void execute(GamePanel panel) {
				ExploreMenuPanel menuPanel = (ExploreMenuPanel) panel;
				Party party = ((ExplorePanelManager)menuPanel.getManager()).getMap().getParty();
//				party.respecStatAndGold();	
			}
			
			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
		item = new MenuItem() {
			private String name = "Save Game";
			
			@Override
			public void execute(GamePanel panel) {
				ExploreMenuPanel menuPanel = (ExploreMenuPanel) panel;
				Party party = ((ExplorePanelManager)menuPanel.getManager()).getMap().getParty();
				party.getGameStateManager().add(((ExplorePanelManager)menuPanel.getManager()).getMap());
				saveGame(party);
				menuPanel.closeMenu();
			}
	
			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
		item = new MenuItem() {
			private String name = "Load Game";
			
			@Override
			public void execute(GamePanel panel) {
				ExploreMenuPanel menuPanel = (ExploreMenuPanel) panel;
				Party party = ((ExplorePanelManager)menuPanel.getManager()).getMap().getParty();
				menuPanel.loadGame();
			}
	
			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
		item = new MenuItem() {
			private String name = "Quit Game";
			
			@Override
			public void execute(GamePanel panel) {
				ExploreMenuPanel menuPanel = (ExploreMenuPanel) panel;
				menuPanel.quitToMenu();
			}
	
			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
		return menuItems;
	}
	
	
	public void setHero(Hero hero) {
		this.hero = hero;
	}
	
	public void setItem(Item item) {
		this.item = item;
	}

	public void setViewPartyMenu(boolean bool) {
		this.viewPartyMenu = bool;
	}
	
	public void setPartyItemMenu(boolean bool) {
		this.partyItemMenu = bool;
	}
	
	public void setPartySelectItemMenu(boolean bool) {
		this.partySelectItemMenu = bool;
	}
	
	public void setHeroSelectMenu(boolean bool) {
		this.heroSelectMenu = bool;
	}
	
	public void setHeroStatsMenu(boolean bool) {
		this.heroStatsMenu = bool;
	}

	public void setHeroSkillsMenu(boolean bool) {
		this.heroSkillsMenu = bool;
	}
	
	public void setHeroEquipmentMenu(boolean bool) {
		this.heroEquipmentMenu = bool;
	}

	public void setTroopMenu(boolean bool) {
		this.troopMenu = bool;
	}

	public void setPartyManagementMenu(boolean bool) {
		this.partyManagementMenu = bool;
	}
	
	public void setArmyManagementMenu(boolean bool) {
		this.armyManagementMenu = bool;
	}
	
	public void setBuildingManagementMenu(boolean bool) {
		this.buildingManagementMenu = bool;
	}
	
	public void setPurchaseItemMenu(boolean bool) {
		this.purchaseItemMenu = bool;
	}
	
	public void setMiscMenu(boolean bool) {
		this.miscMenu = bool;
	}
	
	public void setParty(Party party) {
		this.party = party;
	}
	
	private static void saveGame(Party party) {
		try {
			FileOutputStream saveFile = new FileOutputStream(System.getProperty("user.home")+"/saves/testSave.sav");
			ObjectOutputStream save = new ObjectOutputStream(saveFile);
			save.writeObject(party);
			save.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loadGame() {
		Party party = null;
		try {
//			InputStream saveFile = getClass().getClassLoader().getResourceAsStream("/saves/testSave.sav");
			FileInputStream saveFile = new FileInputStream(System.getProperty("user.home")+"/saves/testSave.sav");
			ObjectInputStream restore = new ObjectInputStream(saveFile);
			party = (Party)restore.readObject();
		} catch (IOException | ClassNotFoundException e) {
			return;
		}
		frame.removeAll();
	    ((ExplorePanelManager)manager).getMap().stopBGM();

	    RegionPanelManager manager = new RegionPanelManager(frame);
	    RegionMap panel = (RegionMap)party.getGameStateManager().getMap(party.getMapName());
	    if (panel == null) {
			panel = new RegionMap(party.getMapName(), frame, manager, party);
	    }
	    panel.startBGM();
	    panel.setManager(manager);
	    panel.setFrame(frame);
	    panel.restoreMap();
	    panel.setCoordinates(party.getAvatarIndexY(),party.getAvatarIndexX());
	    RegionInfoPanel infoPanel = new RegionInfoPanel("testInfoPanel", frame, manager);
	    ExploreMenuPanel menuPanel = new ExploreMenuPanel("testMenuPanel", frame, manager, null, 1, tabItems, 0);
	    manager.setDominantPanel(panel);
	    frame.refresh();
	}
	
	public void quitToMenu() {
		frame.removeAll();
	    GenericMap panel = ((ExplorePanelManager)manager).getMap();
	    panel.stopBGM();
		StartPanelManager startManager = new StartPanelManager(frame);
		StartMenuPanel menuPanel = new StartMenuPanel("Start Game Screen", frame, startManager, StartMenuPanel.getStandardMenu(), 1);		
		startManager.changeDominantPanel(menuPanel);
        menuPanel.displayPanel();
		frame.refresh();
	}

	private void clearBooleans() {
		viewPartyMenu = false;
		partyItemMenu = false;
		partySelectItemMenu = false;
		heroSelectMenu = false;
		heroStatsMenu = false;
		heroSkillsMenu = false;
		heroEquipmentMenu = false;
		troopMenu = false;
		partyManagementMenu = false;
		armyManagementMenu = false;
		buildingManagementMenu = false;
		purchaseItemMenu = false;
		miscMenu = false;
	}
	
	public void setSource(String source) {
		this.source = source;
		if (selectorIndexX >= getRelevantTabs(source).size()) {
			selectorIndexX = 0;
		}
	}
	
	public Item getItem() {
		return item;
	}
}
