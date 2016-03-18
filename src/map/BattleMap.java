package map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import unit.ManAtArms;
import unit.OrderOfBattle;
import unit.Stats;
import unit.Unit;
import event.Ability;
import event.Event;
import event.MenuItem;
import event.TargetedAbility;

public class BattleMap extends GenericMap {

	private GameFrame frame;
	private BattlePanelManager manager;
	private OrderOfBattle orderOfBattle;
	private List<MenuItem> menuItems;
	private Unit currUnit;
	private boolean targetingMode;
	int avatarIndexX;
	int avatarIndexY;
	int screenIndexX = 0;
	int screenIndexY = 0;
	int movementIndexX = 0;
	int movementIndexY = 0;
	int targetingIndexX = 0;
	int targetingIndexY = 0;
	Ability ability;
	int[][] movementMap;
	
	public BattleMap(String name, GameFrame frame, BattlePanelManager manager) {
		frame.add(this);
		manager.setBattleMap(this);
        loadMap(name);
        this.setBounds(0, 0, GraphicsConstants.BATTLE_MAP_WIDTH, GraphicsConstants.BATTLE_MAP_HEIGHT);
		this.name = name;
		this.frame = frame;
		this.manager = manager;
		this.targetingMode = false;
		
		orderOfBattle = new OrderOfBattle(tileMap);
		Stats stats = new Stats(30, 10, 5, 100);
		List<Ability> abilities = new ArrayList<Ability>();
		Ability ability = new TargetedAbility("Basic Attack", 1, 1.5);
		abilities.add(ability);
		ability = new TargetedAbility("Ranged Attack", 2, 1);
		abilities.add(ability);
		Unit unit = new ManAtArms(stats, "ALLY");
		unit.setAbilities(abilities);
		unit.setPosition(1, 1);
		orderOfBattle.addUnit(unit);
		unit = new ManAtArms(stats, "ALLY");
		unit.setAbilities(abilities);
		unit.setPosition(1, 3);
		orderOfBattle.addUnit(unit);
		unit = new ManAtArms(stats, "ENEMY");
		unit.setAbilities(abilities);
		unit.setPosition(3, 4);
		orderOfBattle.addUnit(unit);
		unit = new ManAtArms(stats, "ENEMY");
		unit.setAbilities(abilities);
		unit.setPosition(3, 8);
		orderOfBattle.addUnit(unit);
		currUnit = orderOfBattle.getCurrUnit();
		setCoordinates(currUnit.getPosIndexY(), currUnit.getPosIndexX());
	}
	
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        Color bgColor = new Color(0,0,0);
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, GraphicsConstants.REGION_MAP_WIDTH, GraphicsConstants.REGION_MAP_HEIGHT);
        this.drawMap(g2d, screenIndexX, screenIndexY, 0, 0);
        this.drawUnits(g2d);
        if (targetingMode) {
        	this.drawRange(g2d);
        	this.drawTarget(g2d);
        } else {
        	createMovementMap();
//        	for (int i = 0; i < movementMap.length; i++) {
//        		for (int j = 0; j < movementMap[i].length; j++) {
//        			System.out.print(movementMap[i][j] + " ");
//        		}
//        		System.out.println("");
//        	}
        	this.drawMovement(g2d);
        	this.drawMovementCursor(g2d);
        }
    }
	
	private void drawUnits(Graphics2D g2d) {
		List<Unit> unitList = orderOfBattle.getUnitList();

		for (Unit unit: unitList) {
			Color c = new Color(255, 0, 0);
			if (unit.equals(currUnit))
				c = new Color(0, 255, 0);
			else if (unit.getFaction().equals("ALLY"))
				c = new Color(0, 255, 255);
			else if (unit.getFaction().equals("ENEMY"))
				c = new Color(255, 0 ,0);
			else if (unit.equals(currUnit))
				c = new Color(0, 255, 0);
	        g2d.setColor(c);
			g2d.fillOval((unit.getPosIndexX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE,
						 (unit.getPosIndexY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE,
						  GraphicsConstants.REGION_TILE_SIZE, GraphicsConstants.REGION_TILE_SIZE);
		}
	}
    
    private void drawRange(Graphics2D g2d) {
        Color c = new Color(255, 0, 0);
        g2d.setColor(c);
		for (int rowIndex = 0; rowIndex < tileMap.getHeight(); rowIndex++) {
			for (int colIndex = 0; colIndex < tileMap.getWidth(); colIndex++) {
				if (inRange(rowIndex, colIndex)) {
			        drawRect(g2d, (colIndex - screenIndexX)* GraphicsConstants.REGION_TILE_SIZE, 
			      			 (rowIndex - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE, 
			      			 GraphicsConstants.REGION_TILE_SIZE, 
			      			 GraphicsConstants.REGION_TILE_SIZE, 2);
				}
			}
		}
    }
	
    private void drawTarget(Graphics2D g2d) {
        Color c = new Color(0, 0, 0);
        g2d.setColor(c);
        drawRect(g2d, (targetingIndexX - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE, 
        		(targetingIndexY - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE, 
        		 GraphicsConstants.REGION_TILE_SIZE, 
        		 GraphicsConstants.REGION_TILE_SIZE, 3);
    }
    
    private void createMovementMap() {
    	movementMap = new int[tileMap.getHeight()][tileMap.getWidth()];
    	for (int i = 0; i < movementMap.length; i++)
    		Arrays.fill(movementMap[i], 99999);
    	movementMap[avatarIndexY][avatarIndexX] = 0;
    	propogate(avatarIndexY, avatarIndexX);
    }
    
    private void propogate(int y, int x) {
    	int value = movementMap[y][x] + 1;
    	if (value > currUnit.getCurrMovement())
    		return;
    	if (y > 0 && movementMap[y-1][x] > value && orderOfBattle.getUnit(y-1, x) == null && tileMap.getTile(y-1, x).isAccessable()) {
    		movementMap[y-1][x] = value;
    		propogate(y-1, x);
    	}
    	if (y < tileMap.getHeight() - 1 && movementMap[y+1][x] > value && orderOfBattle.getUnit(y+1, x) == null && tileMap.getTile(y+1, x).isAccessable()) {
    		movementMap[y+1][x] = value;
    		propogate(y+1, x);
    	}
    	if (x > 0 && movementMap[y][x-1] > value && orderOfBattle.getUnit(y, x-1) == null && tileMap.getTile(y, x-1).isAccessable()) {
    		movementMap[y][x-1] = value;
    		propogate(y, x-1);
    	}
    	if (x < tileMap.getWidth() - 1 && movementMap[y][x+1] > value && orderOfBattle.getUnit(y, x+1) == null && tileMap.getTile(y, x+1).isAccessable()) {
    		movementMap[y][x+1] = value;
    		propogate(y, x+1);
    	}
    }
    
    private void drawMovement(Graphics2D g2d) {
        Color c = new Color(255, 0, 0);
        g2d.setColor(c);
		for (int rowIndex = 0; rowIndex < tileMap.getHeight(); rowIndex++) {
			for (int colIndex = 0; colIndex < tileMap.getWidth(); colIndex++) {
				if (inMovementRange(movementMap, rowIndex, colIndex)) {
			        drawRect(g2d, (colIndex - screenIndexX)* GraphicsConstants.REGION_TILE_SIZE, 
			      			 (rowIndex - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE, 
			      			 GraphicsConstants.REGION_TILE_SIZE, 
			      			 GraphicsConstants.REGION_TILE_SIZE, 2);
				}
			}
		}
    }
    
    private void drawMovementCursor(Graphics2D g2d) {
        Color c = new Color(0, 255, 0);
        g2d.setColor(c);
		drawOval(g2d,(movementIndexX - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE,
				 (movementIndexY - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE,
				  GraphicsConstants.REGION_TILE_SIZE, GraphicsConstants.REGION_TILE_SIZE, 3);
    }
    
    private boolean inRange(int rowIndex, int colIndex) {
    	int verticalDiff = Math.abs(avatarIndexY - rowIndex);
    	int horizontalDiff = Math.abs(avatarIndexX - colIndex);
    	if (verticalDiff + horizontalDiff <= ((TargetedAbility)ability).getRange()) 
    		return true;
    	return false;
    }
    
    private boolean inMovementRange(int[][] movementMap, int rowIndex, int colIndex) {
//    	int verticalDiff = Math.abs(avatarIndexY - rowIndex);
//    	int horizontalDiff = Math.abs(avatarIndexX - colIndex);
//    	if (verticalDiff + horizontalDiff <= currUnit.getCurrMovement()) 
//    		return true;
//    	return false;
    	if (movementMap[rowIndex][colIndex] <= currUnit.getCurrMovement())
    		return true;
    	return false;
    }
    
	private void setCoordinates (int avatarIndexY, int avatarIndexX) {
		this.avatarIndexY = avatarIndexY;
		this.avatarIndexX = avatarIndexX;
		this.movementIndexY = avatarIndexY;
		this.movementIndexX = avatarIndexX;
//		currUnit.setPosition(avatarIndexX, avatarIndexY);
	}
    
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (targetingMode) {
			keyPressedTargetingMode(e);
			keyCode = 0;
		}
		boolean moved = false;
		switch (keyCode) {
			case 87:
				moveUp();
				break;
			case 83:
				moveDown();
				break;
			case 65:
				moveLeft();
				break;
			case 68:
				moveRight();
				break;
			case 38:
				screenUp();
				break;
			case 40:
				screenDown();
				break;
			case 37:
				screenLeft();
				break;
			case 39:
				screenRight();
				break;
			case 69:
				moveCurrUnit();
				break;
			case 84:
				openActionMenu();
				break;
		}
		currUnit.setPosition(avatarIndexY, avatarIndexX);
		checkTileTest();
		this.repaint();
	}
	
	private void keyPressedTargetingMode(KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch (keyCode) {
		case 87:
			moveTargetUp();
			break;
		case 83:
			moveTargetDown();
			break;
		case 65:
			moveTargetLeft();
			break;
		case 68:
			moveTargetRight();
			break;
		case 69:
			attackTarget();
			break;
		case 81:
			backToMenu();
		}
	}

	private void moveUp() {
		if (tileMap.getTile(movementIndexY - 1, movementIndexX).isAccessable() &&
			orderOfBattle.getUnit(movementIndexY - 1, movementIndexX) == null &&
			movementMap[movementIndexY - 1][movementIndexX] <= currUnit.getCurrMovement()) 
			movementIndexY--;
	}
	
	private void moveDown() {
		if (tileMap.getTile(movementIndexY + 1, movementIndexX).isAccessable() &&
				orderOfBattle.getUnit(movementIndexY + 1, movementIndexX) == null &&
				movementMap[movementIndexY + 1][movementIndexX] <= currUnit.getCurrMovement()) 
				movementIndexY++;
	}
	
	private void moveLeft() {
		if (tileMap.getTile(movementIndexY, movementIndexX - 1).isAccessable() &&
				orderOfBattle.getUnit(movementIndexY, movementIndexX - 1) == null &&
				movementMap[movementIndexY][movementIndexX - 1] <= currUnit.getCurrMovement()) 
				movementIndexX--;
	}
	
	private void moveRight() {
		if (tileMap.getTile(movementIndexY, movementIndexX + 1).isAccessable() &&
				orderOfBattle.getUnit(movementIndexY, movementIndexX + 1) == null &&
				movementMap[movementIndexY][movementIndexX + 1] <= currUnit.getCurrMovement()) 
				movementIndexX++;
	}
	
	private void screenUp() {
		screenIndexY--;
		if (screenIndexY < 0)
			screenIndexY = 0;
	}
	
	private void screenDown() {
		screenIndexY++;
		if (screenIndexY >= height)
			screenIndexY = height - 1;
	}
	
	private void screenLeft() {
		screenIndexX--;
		if (screenIndexX < 0)
			screenIndexX = 0;
	}
	
	private void screenRight() {
		screenIndexX++;
		if (screenIndexX >= width)
			screenIndexX = width - 1;
	}
	
	private void moveTargetUp() {
		if (inRange(targetingIndexY - 1, targetingIndexX))
			targetingIndexY--;
	}
	
	private void moveTargetDown() {
		if (inRange(targetingIndexY + 1, targetingIndexX))
			targetingIndexY++;
	}
	
	private void moveTargetLeft() {
		if (inRange(targetingIndexY, targetingIndexX - 1))
			targetingIndexX--;
	}
	
	private void moveTargetRight() {
		if (inRange(targetingIndexY, targetingIndexX + 1))
			targetingIndexX++;
	}
	
	private void moveCurrUnit() {
		int movementUsed = movementMap[movementIndexY][movementIndexX];
		currUnit.subractMovement(movementUsed);
		avatarIndexY = movementIndexY;
		avatarIndexX = movementIndexX;
	}
	
	private void openActionMenu() {
		MenuPanel menuPanel = manager.getMenuPanel();
		menuPanel.displayPanel();
		manager.changeDominantPanel(menuPanel);
		frame.refresh();
	}
	
	private void attackTarget() {
		((TargetedAbility)ability).dealDamage(currUnit, orderOfBattle.getUnit(targetingIndexY, targetingIndexX), (TargetedAbility)ability);
		targetingMode = false;
		MenuPanel menu = manager.getMenuPanel();
		menu.clearSubMenus();
		endTurn();
		frame.refresh();
	}
	
	private void endTurn() {
		currUnit.endTurn();
		currUnit = orderOfBattle.getNextUnit();
		setCoordinates(currUnit.getPosIndexY(), currUnit.getPosIndexX());
	}
	
	private void endBattle() {
		frame.removeAll();
		manager.getExplorePanelManager().restore();
	}
	
	private void backToMenu() {
		targetingMode = false;
		manager.changeDominantPanel(manager.getMenuPanel());
		MenuPanel menu = (MenuPanel)manager.getDominantPanel();
		frame.add(menu);
		while(menu.subMenu != null) {
			menu = menu.subMenu;
			frame.add(menu);
		}	}
	private void checkTileTest() {
//		Tile tile = tileMap.getTile(avatarIndexY, avatarIndexX);
//		String text = "";
//		if (tile.hasEvent()) {
//			Event event = tile.getEvent();
//			text = event.getType();
//		}
//		InfoPanel infoPanel = explorer.getInfoPanel();
//		infoPanel.displayText(text);
	}

	public Unit getCurrUnit() {
		return currUnit;
	}

	public void setTargetingMode(Ability ability) {
		this.targetingMode = true;
		targetingIndexY = avatarIndexY;
		targetingIndexX = avatarIndexX;
		this.ability = ability;
	}
	
	private void drawRect(Graphics2D g2d, int x, int y, int width, int height, int thickness) {
		for (int i = 0; i < thickness; i++) {
			g2d.drawRect(x + i, y + i, width - (2 * i), height - (2 * i));
		}
	}
	
	private void drawOval(Graphics2D g2d, int x, int y, int width, int height, int thickness) {
		for (int i = 0; i < thickness; i++) {
			g2d.drawOval(x + i, y + i, width - (2 * i), height - (2 * i));
		}
	}
}
