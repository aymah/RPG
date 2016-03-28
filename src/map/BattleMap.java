package map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.Timer;

import org.json.JSONArray;

import unit.OrderOfBattle;
import unit.Party;
import unit.Stats;
import unit.Unit;
import event.Ability;
import event.Event;
import event.MenuItem;

public class BattleMap extends GenericMap {

	private OrderOfBattle orderOfBattle;
	private BattlePanelManager manager;
//	private List<MenuItem> menuItems;
	private Unit currUnit;
	private boolean targetingMode;
	private boolean animating;
	private boolean aiTurn;
	private boolean freeSelectMode;
	private int deathAnimation;
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
	int damageDealt = 0;
	Party party;
	
	public BattleMap(String name, GameFrame frame, BattlePanelManager manager, Party party) {
		frame.add(this);
		manager.setBattleMap(this);
        loadMap(name, party);
        this.party = party;
		this.name = name;
		this.frame = frame;
		this.manager = manager;
		this.targetingMode = false;
		this.animating = false;
		this.aiTurn = false;
		this.deathAnimation = 0;
		
		orderOfBattle = new OrderOfBattle(tileMap);
		for(Unit unit: tempUnitList)
			orderOfBattle.addUnit(unit);
		currUnit = orderOfBattle.getCurrUnit();
		setCoordinates(currUnit.getPosIndexY(), currUnit.getPosIndexX());
        this.setBounds(0, 0, GraphicsConstants.BATTLE_MAP_WIDTH, GraphicsConstants.BATTLE_MAP_HEIGHT);
		orderOfBattle.generatePhantomQueue();
		screenIndexY = currUnit.getPosIndexY() - 5;
		screenIndexX = currUnit.getPosIndexX() - 9;
	}
	
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        Color bgColor = new Color(0,0,0);
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, GraphicsConstants.BATTLE_MAP_WIDTH, GraphicsConstants.BATTLE_MAP_HEIGHT);
        this.drawMap(g2d, screenIndexX, screenIndexY, 0, 0, GraphicsConstants.BATTLE_MAP_HEIGHT, GraphicsConstants.BATTLE_MAP_WIDTH);
        if (targetingMode) {
        	this.drawAOE(g2d);
        	this.drawRange(g2d);
        	this.drawTarget(g2d);
        } else if (freeSelectMode) {
        	this.drawTarget(g2d);
        } else if (!animating){
        	if (!aiTurn) {
        		movementMap = createMovementMap();
        	}
        	this.drawMovement(g2d);
        }
        this.drawUnits(g2d);
        if (avatarIndexY != movementIndexY || avatarIndexX != movementIndexX) {
		    this.drawMovementCursor(g2d);
		    this.drawMovementPath(g2d);
        }
    }
	
	private void drawUnits(Graphics2D g2d) {
		List<Unit> unitList = orderOfBattle.getUnitList();

		for (Unit unit: unitList) {
			Color c = new Color(255, 0, 0);
			if (unit.getFaction().equals("ALLY")) {
				c = new Color(0, 255, 255);
				if (unit.isTakingDamage())
					c = new Color (0, 70, 70);
				}
			else if (unit.getFaction().equals("ENEMY")) {
				c = new Color(255, 0 ,0);
				if (unit.isTakingDamage())
					c = new Color (50, 0, 0);
			}

	        g2d.setColor(c);
			g2d.fillOval((unit.getPosIndexX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE,
						 (unit.getPosIndexY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE,
						  GraphicsConstants.REGION_TILE_SIZE, GraphicsConstants.REGION_TILE_SIZE);
			g2d.setColor(new Color(0,0,0));
			g2d.drawString(unit.getName(), (unit.getPosIndexX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE + (GraphicsConstants.REGION_TILE_SIZE/4),
						  (unit.getPosIndexY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE + (GraphicsConstants.REGION_TILE_SIZE/2));
			g2d.setColor(new Color(0, 0, 0));
			if (unit.equals(currUnit)) {
				g2d.setColor(new Color(255, 255, 0));		
			}
			GraphicsConstants.drawOval(g2d, (unit.getPosIndexX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE,
				 	 (unit.getPosIndexY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE,
				 	  GraphicsConstants.REGION_TILE_SIZE, GraphicsConstants.REGION_TILE_SIZE, 3);
			
			
			//hp bar
			g2d.setColor(new Color(255,0,0));
			g2d.fillRect((unit.getPosIndexX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE,
		 	 			 (unit.getPosIndexY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE,
		 	 			  GraphicsConstants.REGION_TILE_SIZE, 2);
			g2d.setColor(new Color(0,255,0));
			g2d.fillRect((unit.getPosIndexX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE,
		 	 			 (unit.getPosIndexY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE,
		 	 			 (int)(GraphicsConstants.REGION_TILE_SIZE * (((double)unit.getCurrHP())/((double)unit.getHP()))), 2);
			g2d.setColor(new Color(0,0,0));
			g2d.drawRect((unit.getPosIndexX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE,
	 	 			 (unit.getPosIndexY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE - 1,
	 	 			  GraphicsConstants.REGION_TILE_SIZE, 3);
			
			//stamina bar
			g2d.setColor(new Color(255,0,255));
			g2d.fillRect((unit.getPosIndexX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE,
		 	 			 (unit.getPosIndexY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE + 3,
		 	 			  GraphicsConstants.REGION_TILE_SIZE, 2);
			g2d.setColor(new Color(255,125,0));
			g2d.fillRect((unit.getPosIndexX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE,
		 	 			 (unit.getPosIndexY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE + 3,
		 	 			 (int)(GraphicsConstants.REGION_TILE_SIZE * (((double)unit.getCurrStamina())/((double)unit.getStamina()))), 2);
			g2d.setColor(new Color(0,0,0));
			g2d.drawRect((unit.getPosIndexX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE,
	 	 			 (unit.getPosIndexY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE + 2,
	 	 			  GraphicsConstants.REGION_TILE_SIZE, 3);
			
			//mp bar, if applicable
			if (unit.getMP() > 0) {
				g2d.setColor(new Color(255,0,255));
				g2d.fillRect((unit.getPosIndexX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE,
			 	 			 (unit.getPosIndexY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE + 6,
			 	 			  GraphicsConstants.REGION_TILE_SIZE, 2);
				g2d.setColor(new Color(0,0,255));
				g2d.fillRect((unit.getPosIndexX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE,
			 	 			 (unit.getPosIndexY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE + 6,
			 	 			 (int)(GraphicsConstants.REGION_TILE_SIZE * (((double)unit.getCurrMP())/((double)unit.getMP()))), 2);
				g2d.setColor(new Color(0,0,0));
				g2d.drawRect((unit.getPosIndexX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE,
		 	 			 (unit.getPosIndexY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE + 5,
		 	 			  GraphicsConstants.REGION_TILE_SIZE, 3);
			}
			
			
			
			
			if (unit.isTakingDamage() && ability != null) {
				int value = damageDealt;
				g2d.setColor(new Color(255,0,0));
				if (value < 0)
					g2d.setColor(new Color(0,255,0));
				if (value == 0) {
					g2d.setColor(new Color(255,125,0));
					value = ability.calculateAddedStamina(currUnit);
				}
				value = Math.abs(value);
				String length = String.valueOf(value);
				g2d.drawString("" + value,
							  (unit.getPosIndexX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE + 20 - (4 * length.length()),
							  (unit.getPosIndexY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE + 22);
			}
			if (unit.isDying() && deathAnimation > 0) {
				g2d.setColor(new Color(255 - deathAnimation * 10, 0, 0));
				g2d.fillOval((unit.getPosIndexX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE,
						 	 (unit.getPosIndexY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE,
						 	 GraphicsConstants.REGION_TILE_SIZE, GraphicsConstants.REGION_TILE_SIZE);
			}
		}
	}
    
	private void drawAOE(Graphics2D g2d) {
        g2d.setColor(new Color(125, 0, 0));
    	if (ability.hasParam("Area Of Effect")) {
    		System.out.println("calculating aoe");
    		JSONArray aoeJSON = (JSONArray) ability.get("Area Of Effect");
    		for (int row = 0; row < aoeJSON.length(); row++) {
    			JSONArray rowJSON = (JSONArray)aoeJSON.get(row);
    			for (int col = 0; col < rowJSON.length(); col++) {
    				int indexX = targetingIndexX - screenIndexX + (col - (rowJSON.length()/2));
    				int indexY = targetingIndexY - screenIndexY + (row - (aoeJSON.length()/2));
    				if ((int)rowJSON.get(col) > 0 && indexX >= 0 && indexX < tileMap.getWidth() && indexY >= 0 && indexY <= tileMap.getHeight()) {
    			        g2d.fillRect(indexX * GraphicsConstants.REGION_TILE_SIZE, 
    			        		indexY * GraphicsConstants.REGION_TILE_SIZE, 
    			        		 GraphicsConstants.REGION_TILE_SIZE, 
    			        		 GraphicsConstants.REGION_TILE_SIZE);
    				}
    			}
    		}
    	} else {
	        g2d.fillRect((targetingIndexX - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE, 
	        		(targetingIndexY - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE, 
	        		 GraphicsConstants.REGION_TILE_SIZE, 
	        		 GraphicsConstants.REGION_TILE_SIZE);
    	}
	}
	
    private void drawRange(Graphics2D g2d) {
        Color c = new Color(255, 0, 0);
        g2d.setColor(c);
		for (int rowIndex = 0; rowIndex < tileMap.getHeight(); rowIndex++) {
			for (int colIndex = 0; colIndex < tileMap.getWidth(); colIndex++) {
				if (inRange(rowIndex, colIndex)) {
			        GraphicsConstants.drawRect(g2d, (colIndex - screenIndexX)* GraphicsConstants.REGION_TILE_SIZE, 
			      			 (rowIndex - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE, 
			      			 GraphicsConstants.REGION_TILE_SIZE, 
			      			 GraphicsConstants.REGION_TILE_SIZE, 2);
				}
			}
		}
    }
	
    private void drawTarget(Graphics2D g2d) {
        g2d.setColor(new Color(255, 255, 0));
        GraphicsConstants.drawRect(g2d, (targetingIndexX - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE, 
        		(targetingIndexY - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE, 
        		 GraphicsConstants.REGION_TILE_SIZE, 
        		 GraphicsConstants.REGION_TILE_SIZE, 4);
    }
    
    public int[][] createMovementMap() {
    	int[][] movementMap = new int[tileMap.getHeight()][tileMap.getWidth()];
    	int searchSize = 99; //9 best for testing if printing out
    	for (int i = 0; i < movementMap.length; i++)
    		Arrays.fill(movementMap[i], searchSize);
    	movementMap[currUnit.getPosIndexY()][currUnit.getPosIndexX()] = 0;
    	Queue<Coordinates> queue = new LinkedBlockingQueue<Coordinates>();
    	int y = currUnit.getPosIndexY();
    	int x = currUnit.getPosIndexX();
    	queue.add(new Coordinates(y,x));
    	while(!queue.isEmpty()) {
    		Coordinates c = queue.remove();
    		y = c.getY();
    		x = c.getX();
        	int value = movementMap[y][x] + 1;
        	if (value > searchSize)
        		continue;
        	if ((orderOfBattle.getUnit(y, x) != null && currUnit.isEnemy(orderOfBattle.getUnit(y, x)))) {
        		continue;
        	}
        	if (y > 0 && movementMap[y-1][x] > value && tileMap.getTile(y-1, x).isAccessable()) {
        		movementMap[y-1][x] = value;
            	queue.add(new Coordinates(y-1,x));
        	}
        	if (y < tileMap.getHeight() - 1 && movementMap[y+1][x] > value && tileMap.getTile(y+1, x).isAccessable()) {
        		movementMap[y+1][x] = value;
            	queue.add(new Coordinates(y+1,x));
        	}
        	if (x > 0 && movementMap[y][x-1] > value && tileMap.getTile(y, x-1).isAccessable()) {
        		movementMap[y][x-1] = value;
            	queue.add(new Coordinates(y,x-1));
        	}
        	if (x < tileMap.getWidth() - 1 && movementMap[y][x+1] > value && tileMap.getTile(y, x+1).isAccessable()) {
        		movementMap[y][x+1] = value;
            	queue.add(new Coordinates(y,x+1));
        	}
//        	if (y > 0 && movementMap[y-1][x] > value && orderOfBattle.getUnit(y-1, x) == null && tileMap.getTile(y-1, x).isAccessable()) {
//        		movementMap[y-1][x] = value;
//            	queue.add(new Coordinates(y-1,x));
//        	}
//        	if (y < tileMap.getHeight() - 1 && movementMap[y+1][x] > value && orderOfBattle.getUnit(y+1, x) == null && tileMap.getTile(y+1, x).isAccessable()) {
//        		movementMap[y+1][x] = value;
//            	queue.add(new Coordinates(y+1,x));
//        	}
//        	if (x > 0 && movementMap[y][x-1] > value && orderOfBattle.getUnit(y, x-1) == null && tileMap.getTile(y, x-1).isAccessable()) {
//        		movementMap[y][x-1] = value;
//            	queue.add(new Coordinates(y,x-1));
//        	}
//        	if (x < tileMap.getWidth() - 1 && movementMap[y][x+1] > value && orderOfBattle.getUnit(y, x+1) == null && tileMap.getTile(y, x+1).isAccessable()) {
//        		movementMap[y][x+1] = value;
//            	queue.add(new Coordinates(y,x+1));
//        	}
    	}
//    	for (int i = 0; i < movementMap.length; i++) {
//    		for (int j = 0; j < movementMap[i].length; j++) {
//    			System.out.print(movementMap[i][j] + " ");
//    		}
//    		System.out.println("");
//    	}
//		System.out.println("");
    	return movementMap;
    }
    
//    private void propogate(int y, int x) {
//    	int value = movementMap[y][x] + 1;
//    	if (value > currUnit.getCurrMovement())
//    		return;
//    	if (y > 0 && movementMap[y-1][x] > value && orderOfBattle.getUnit(y-1, x) == null && tileMap.getTile(y-1, x).isAccessable()) {
//    		movementMap[y-1][x] = value;
//    		propogate(y-1, x);
//    	}
//    	if (y < tileMap.getHeight() - 1 && movementMap[y+1][x] > value && orderOfBattle.getUnit(y+1, x) == null && tileMap.getTile(y+1, x).isAccessable()) {
//    		movementMap[y+1][x] = value;
//    		propogate(y+1, x);
//    	}
//    	if (x > 0 && movementMap[y][x-1] > value && orderOfBattle.getUnit(y, x-1) == null && tileMap.getTile(y, x-1).isAccessable()) {
//    		movementMap[y][x-1] = value;
//    		propogate(y, x-1);
//    	}
//    	if (x < tileMap.getWidth() - 1 && movementMap[y][x+1] > value && orderOfBattle.getUnit(y, x+1) == null && tileMap.getTile(y, x+1).isAccessable()) {
//    		movementMap[y][x+1] = value;
//    		propogate(y, x+1);
//    	}
//    }
    
    private void drawMovement(Graphics2D g2d) {
        Color c = new Color(255, 0, 0);
        g2d.setColor(c);
		for (int rowIndex = 0; rowIndex < tileMap.getHeight(); rowIndex++) {
			for (int colIndex = 0; colIndex < tileMap.getWidth(); colIndex++) {
				if (inMovementRange(rowIndex, colIndex)) {
			        GraphicsConstants.drawRect(g2d, (colIndex - screenIndexX)* GraphicsConstants.REGION_TILE_SIZE, 
			      			 (rowIndex - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE, 
			      			 GraphicsConstants.REGION_TILE_SIZE, 
			      			 GraphicsConstants.REGION_TILE_SIZE, 2);
				}
			}
		}
    }
    
    private void drawMovementCursor(Graphics2D g2d) {
        g2d.setColor(new Color(255, 255, 0));
		GraphicsConstants.drawOval(g2d,(movementIndexX - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE,
				 (movementIndexY - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE,
				  GraphicsConstants.REGION_TILE_SIZE, GraphicsConstants.REGION_TILE_SIZE, 3);
    }
    
    private void drawMovementPath(Graphics2D g2d) {
    	List<Coordinates> path = makeMovementPath();
    	for (int i = 1; i < path.size() - 1; i++) {
	        Color c = new Color(255, 255, 0);
	        g2d.setColor(c);
			g2d.fillOval((path.get(i).getX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE + (GraphicsConstants.REGION_TILE_SIZE/2 - GraphicsConstants.REGION_TILE_SIZE/8),
					  	 (path.get(i).getY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE + (GraphicsConstants.REGION_TILE_SIZE/2 - GraphicsConstants.REGION_TILE_SIZE/8),
					  	GraphicsConstants.REGION_TILE_SIZE/4, GraphicsConstants.REGION_TILE_SIZE/4);
    	}
    }
    
    private List<Coordinates> makeMovementPath() {
		List<Coordinates> path = new ArrayList<Coordinates>();
    	path.add(new Coordinates(movementIndexY, movementIndexX));
    	int value = movementMap[movementIndexY][movementIndexX];
    	while (path.get(0).getY() != avatarIndexY || path.get(0).getX() != avatarIndexX) {
    		if (movementMap[path.get(0).getY() - 1][path.get(0).getX()] == value - 1 &&
    		   (orderOfBattle.getUnit(path.get(0).getY() - 1, path.get(0).getX()) == null ||
    			!currUnit.isEnemy(orderOfBattle.getUnit(path.get(0).getY() - 1, path.get(0).getX())))) {
    			value--;
    			path.add(0, new Coordinates(path.get(0).getY() - 1, path.get(0).getX()));
    		} else if (movementMap[path.get(0).getY() + 1][path.get(0).getX()] == value - 1 &&
    	    		   (orderOfBattle.getUnit(path.get(0).getY() + 1, path.get(0).getX()) == null ||
    	    			!currUnit.isEnemy(orderOfBattle.getUnit(path.get(0).getY() + 1, path.get(0).getX())))) {
    			value--;
    			path.add(0, new Coordinates(path.get(0).getY() + 1, path.get(0).getX()));
    		} else if (movementMap[path.get(0).getY()][path.get(0).getX() - 1] == value - 1 &&
    	    		   (orderOfBattle.getUnit(path.get(0).getY(), path.get(0).getX() - 1) == null ||
    	    			!currUnit.isEnemy(orderOfBattle.getUnit(path.get(0).getY(), path.get(0).getX() - 1)))) {
    			value--;
    			path.add(0, new Coordinates(path.get(0).getY(), path.get(0).getX() - 1));
    		} else if (movementMap[path.get(0).getY()][path.get(0).getX() + 1] == value - 1 &&
    	    		   (orderOfBattle.getUnit(path.get(0).getY(), path.get(0).getX() + 1) == null ||
    	    			!currUnit.isEnemy(orderOfBattle.getUnit(path.get(0).getY(), path.get(0).getX() + 1)))) {
    			value--;
    			path.add(0, new Coordinates(path.get(0).getY(), path.get(0).getX() + 1));
    		} else {
    			break;
    		}
    	}
    	return path;
	}

	private boolean inRange(int rowIndex, int colIndex) {
    	int verticalDiff = Math.abs(avatarIndexY - rowIndex);
    	int horizontalDiff = Math.abs(avatarIndexX - colIndex);
    	if (verticalDiff + horizontalDiff <= ability.getRange()) 
    		return true;
    	return false;
    }
    
    private boolean inMovementRange(int rowIndex, int colIndex) {
    	if (movementMap[rowIndex][colIndex] <= currUnit.getCurrMovement() && 
    		movementMap[rowIndex][colIndex] * 5 <= currUnit.getCurrStamina() &&
    		orderOfBattle.getUnit(rowIndex, colIndex) == null)
    		return true;
    	return false;
    }
    
	private void setCoordinates (int avatarIndexY, int avatarIndexX) {
		this.avatarIndexY = avatarIndexY;
		this.avatarIndexX = avatarIndexX;
		this.movementIndexY = avatarIndexY;
		this.movementIndexX = avatarIndexX;
	}
    
	public void keyTyped(KeyEvent e) {
		
	}
	
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (targetingMode) {
			keyPressedTargetingMode(e);
			keyCode = 0;
		}
		if (freeSelectMode) {
			keyPressedFreeSelectMode(e);
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
			case 10:
				if (orderOfBattle.getUnit(movementIndexY, movementIndexX) == null || orderOfBattle.getUnit(movementIndexY, movementIndexX) == currUnit) {
					moveCurrUnit();
					openActionMenu();
				}
				break;
			case 70:
				//go to free select mode
				break;
			case 84:
				winBattle();
				break;
		}
//		currUnit.setPosition(avatarIndexY, avatarIndexX);
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
		case 10:
			if (ability.hasParam("Area Of Effect")) {
				attackAOE();
			}else if (orderOfBattle.getUnit(targetingIndexY, targetingIndexX) != null) {
				attackTarget();
			}
			break;
		case 81:
		case 27:
			backToMenu();
		}
	}

	private void keyPressedFreeSelectMode(KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch (keyCode) {
		case 87:
			moveSelectorUp();
			break;
		case 83:
			moveSelectorDown();
			break;
		case 65:
			moveSelectorLeft();
			break;
		case 68:
			moveSelectorRight();
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
		case 10:
//			if (orderOfBattle.getUnit(targetingIndexY, targetingIndexX) != null)
//				attackTarget();
			//maybe put getDetailedInfo here
			break;
		case 81:
		case 27:
			backToMenu();
		}
	}
	
	private void moveSelectorUp() {
		targetingIndexY--;
	}

	private void moveSelectorDown() {
		targetingIndexY++;
	}
	
	private void moveSelectorLeft() {
		targetingIndexX--;
	}
	
	private void moveSelectorRight() {
		targetingIndexX++;
	}
	
	private void moveUp() {
		if (tileMap.getTile(movementIndexY - 1, movementIndexX).isAccessable() &&
			(orderOfBattle.getUnit(movementIndexY - 1, movementIndexX) == null || orderOfBattle.getUnit(movementIndexY - 1, movementIndexX).getFaction().equals("ALLY")) &&
			movementMap[movementIndexY - 1][movementIndexX] <= currUnit.getCurrMovement()) 
			movementIndexY--;
	}
	
	private void moveDown() {
		if (tileMap.getTile(movementIndexY + 1, movementIndexX).isAccessable() &&
			(orderOfBattle.getUnit(movementIndexY + 1, movementIndexX) == null || orderOfBattle.getUnit(movementIndexY + 1, movementIndexX).getFaction().equals("ALLY")) &&
			movementMap[movementIndexY + 1][movementIndexX] <= currUnit.getCurrMovement()) 
			movementIndexY++;
	}
	
	private void moveLeft() {
		if (tileMap.getTile(movementIndexY, movementIndexX - 1).isAccessable() &&
			(orderOfBattle.getUnit(movementIndexY, movementIndexX - 1) == null  || orderOfBattle.getUnit(movementIndexY, movementIndexX - 1).getFaction().equals("ALLY")) &&
			movementMap[movementIndexY][movementIndexX - 1] <= currUnit.getCurrMovement()) 
			movementIndexX--;
	}
	
	private void moveRight() {
		if (tileMap.getTile(movementIndexY, movementIndexX + 1).isAccessable() &&
			(orderOfBattle.getUnit(movementIndexY, movementIndexX + 1) == null  || orderOfBattle.getUnit(movementIndexY, movementIndexX + 1).getFaction().equals("ALLY")) &&
			movementMap[movementIndexY][movementIndexX + 1] <= currUnit.getCurrMovement()) 
			movementIndexX++;
	}
	
	private void screenUp() {
		screenIndexY--;
		if (screenIndexY < -10)
			screenIndexY = -10;
	}
	
	private void screenDown() {
		screenIndexY++;
		if (screenIndexY >= height)
			screenIndexY = height - 1;
	}
	
	private void screenLeft() {
		screenIndexX--;
		if (screenIndexX < -19)
			screenIndexX = -19;
	}
	
	private void screenRight() {
		screenIndexX++;
		if (screenIndexX >= width)
			screenIndexX = width - 1;
	}
	
	private void moveTargetUp() {
		if (inRange(targetingIndexY - 1, targetingIndexX)  && targetingIndexY > 0)
			targetingIndexY--;
	}
	
	private void moveTargetDown() {
		if (inRange(targetingIndexY + 1, targetingIndexX)  && targetingIndexY < tileMap.getHeight() - 1)
			targetingIndexY++;
	}
	
	private void moveTargetLeft() {
		if (inRange(targetingIndexY, targetingIndexX - 1) && targetingIndexX > 0)
			targetingIndexX--;
	}
	
	private void moveTargetRight() {
		if (inRange(targetingIndexY, targetingIndexX + 1) && targetingIndexX < tileMap.getWidth() - 1)
			targetingIndexX++;
	}

	private void moveCurrUnit() {
		Unit unit = currUnit;
		int movementUsed = movementMap[movementIndexY][movementIndexX];
		unit.subractMovement(movementUsed);
		List<Coordinates> path = makeMovementPath();
		animating = true;
		CountDownLatch cdl = new CountDownLatch(1);
    	orderOfBattle.moveToPhantomPlane(unit);
	    Timer timer = new Timer(50, new ActionListener() {
		   
		    int i = 0;
		    
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            if(i < path.size()) {
	            	orderOfBattle.movePhantomUnit(unit, path.get(i).getY(), path.get(i).getX());
	        		avatarIndexY = path.get(i).getY();
	        		avatarIndexX = path.get(i).getX();
	        		screenIndexY = path.get(i).getY() - 5;
	        		screenIndexX = path.get(i).getX() - 9;
	            	i++;
	                repaint();
	            } else {
	            	orderOfBattle.moveToRegularPlane(unit);
	                animating = false;
	                repaint();
	                cdl.countDown();
	                ((Timer)e.getSource()).stop();
	            }
	        }
	    });
	    timer.setRepeats(true);
	    timer.setDelay(100);
	    timer.start();
	    try {
			cdl.await();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

	}
	
	private void openActionMenu() {
		MenuPanel menuPanel = manager.getMenuPanel();
		menuPanel.displayPanel();
		manager.changeDominantPanel(menuPanel);
		frame.refresh();
	}
	
	private void attackTarget() {
		targetingMode = false;
		currUnit.useAbility(ability);
		Unit target = orderOfBattle.getUnit(targetingIndexY, targetingIndexX);
		damageDealt = ability.dealDamage(currUnit, target);
		ability.applyEffects(currUnit, target);
		target.applyInstantEffects();
		
		attackedAnimation(target);
		MenuPanel menu = manager.getMenuPanel();
		menu.clearSubMenus();
		endTurn();
	}
	
	private void attackAOE() {
		targetingMode = false;
		currUnit.useAbility(ability);

		
		JSONArray aoeJSON = (JSONArray) ability.get("Area Of Effect");
		for (int row = 0; row < aoeJSON.length(); row++) {
			JSONArray rowJSON = (JSONArray)aoeJSON.get(row);
			for (int col = 0; col < rowJSON.length(); col++) {
				int indexX = targetingIndexX + (col - (rowJSON.length()/2));
				int indexY = targetingIndexY + (row - (aoeJSON.length()/2));
				if ((int)rowJSON.get(col) > 0 && indexX >= 0 && indexX < tileMap.getWidth() && indexY >= 0 && indexY <= tileMap.getHeight() &&
					 orderOfBattle.getUnit(indexY, indexX) != null) {
					Unit target = orderOfBattle.getUnit(indexY, indexX);
					damageDealt = ability.dealDamage(currUnit, target);
					ability.applyEffects(currUnit, target);
					target.applyInstantEffects();
					attackedAnimation(target);
				}
			}
		}

		MenuPanel menu = manager.getMenuPanel();
		menu.clearSubMenus();
		endTurn();
	}
	
	public void attackedAnimation(Unit target) {
		animating = true;
		if (!onScreen(target.getPosIndexY(), target.getPosIndexX())) {
			screenIndexY = target.getPosIndexY() - 5;
			screenIndexX = target.getPosIndexX() - 9;
		}
		CountDownLatch cdl = new CountDownLatch(1);
	    Timer timer = new Timer(150, new ActionListener() {
		   
		    int i = 0;
		    
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            if(i == 0) {
	            	target.toggleTakingDamage();
	                repaint();
	            } else if (i == 2) {
	            	target.toggleTakingDamage();
	            	if (target.isDying()) 
		                endAnimation(e);
	                repaint();
	            } else if (i < 2) {
	            } else {
	                endAnimation(e);
	            }
	            i++;
	        }
	        
	        private void endAnimation(ActionEvent e) {
	        	animating = false;
                repaint();
                cdl.countDown();
                ((Timer)e.getSource()).stop();
	        }
	    });
	    timer.setRepeats(true);
	    timer.setDelay(300);
	    timer.start();
	    try {
			cdl.await();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
    	if (target.isDying())
    		deathAnimation(target);
	}
	
	private boolean onScreen(int posIndexY, int posIndexX) {
		if (posIndexY - screenIndexY < 10 && posIndexY - screenIndexY > 0 && posIndexX - screenIndexX < 19 && posIndexX - screenIndexX > 0) return true;
		return false;
	}

	private void deathAnimation(Unit killed) {
		animating = true;
		CountDownLatch cdl = new CountDownLatch(1);
	    Timer timer = new Timer(150, new ActionListener() {
		   
		    int i = 0;
		    
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            if(i < 20) {
	            	deathAnimation = i;
	                repaint();
	            } else {
		        	animating = false;
	            	deathAnimation = 0;
	            	orderOfBattle.removeUnit(killed);
	                cdl.countDown();
	                ((Timer)e.getSource()).stop();
	                repaint();
	            }
	            i++;
	        }
	    });
	    timer.setRepeats(true);
	    timer.setDelay(20);
	    timer.start();
	    try {
			cdl.await();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	    checkWin();
	}
	
	private void checkWin() {
		String faction = orderOfBattle.lastFactionStanding();
		if (faction.equals("ALLY")) {
			winBattle();
		} else if (faction.equals("ENEMY")) {
			loseBattle();
		}
	}
	
	private void winBattle() {
		System.out.println("you win!");
		VictoryRewardsPanel menuPanel = manager.getVictoryPanel();
		menuPanel.displayPanel();
		menuPanel.setExpReward(expReward);
		menuPanel.setGoldReward(goldReward);
		menuPanel.executeRewards();
		party.endBattleStatUpdates();
		manager.changeDominantPanel(menuPanel);
		frame.refresh();
//		endBattle();
	}
	
	private void loseBattle() {
		System.out.println("you lose!");
		party.endBattleStatUpdates();
		orderOfBattle.removeAll();
		endBattle();
		//TODO
	}
	
	public void endTurn() {
		aiTurn = false;
		currUnit.endTurn();
		currUnit = orderOfBattle.getNextUnit();
		currUnit.beginTurn(this);
		orderOfBattle.generatePhantomQueue();
		setCoordinates(currUnit.getPosIndexY(), currUnit.getPosIndexX());
		screenIndexY = currUnit.getPosIndexY() - 5;
		screenIndexX = currUnit.getPosIndexX() - 9;
		if(currUnit.getFaction().equals("ENEMY")) {
			aiTurn();
		}
		this.repaint();
	}
	
	private void aiTurn() {
		aiTurn = true;
		aiMove();
		aiUseAbility();
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		endTurn();
	}


	private void aiMove() {
		//calculate possible moves
    	movementMap = createMovementMap();
    	int[][] movePriorityMap = createMovePriorityMap();
    	aiSelectMove(movePriorityMap);
    	//set movementIndexX and Y
    	//move near an ALLY unit i guess
    	//maybe create move priority map

    	moveCurrUnit();
//		currUnit.setPosition(avatarIndexY, avatarIndexX);
		//choose move
		
	}

	private int[][] createMovePriorityMap() {
		int[][] movePriorityMap = new int[tileMap.getHeight()][tileMap.getWidth()];
		int acquisitionRange = 99; //9 is best number for testing
    	for (int i = 0; i < movePriorityMap.length; i++)
    		Arrays.fill(movePriorityMap[i], acquisitionRange);
    	
    	List<Unit> unitList = orderOfBattle.getUnitList();
    	for (Unit target: unitList) {
    		if (target.getFaction().equals("ALLY")) {
    			currUnit.markMovementPriority(movePriorityMap, movementMap, target);
//    			markPriority(movePriorityMap, unit, 1);
    		}
    	}
    	return movePriorityMap;
	}
	
//    private void markPriority(int[][] movePriorityMap, Unit unit, int range) {
//    	for (int y = -range; y <= range; y++) {
//    		for (int x = -range; x <= range; x++) {
//				if (unit.getPosIndexY() + y >= 0 && unit.getPosIndexX() + x >= 0) {
//	    			if (inRange(unit, range, unit.getPosIndexY() + y, unit.getPosIndexX() + x))
//	    				movePriorityMap[unit.getPosIndexY() + y][unit.getPosIndexX() + x] = 1;
//				}
//    		}
//    	}
//    }
    
    public static boolean inRange(Unit unit, int range, int rowIndex, int colIndex) {
    	int verticalDiff = Math.abs(unit.getPosIndexY() - rowIndex);
    	int horizontalDiff = Math.abs(unit.getPosIndexX() - colIndex);
    	if (verticalDiff + horizontalDiff <= range) 
    		return true;
    	return false;
    }

    private void aiSelectMove(int[][] movePriorityMap) {
    	List<PotentialMove> potentialMoves = new ArrayList<PotentialMove>();
    	potentialMoves.add(new PotentialMove(99, avatarIndexY, avatarIndexX));
//    	for (int i = 0; i < movementMap.length; i++) {
//    		for (int j = 0; j < movementMap[i].length; j++) {
//    			if (orderOfBattle.getUnit(i, j) == null)
//    				System.out.print("0 ");
//    			else System.out.print("1 ");
//    		}
//    		System.out.println("");
//    	}
//		System.out.println("");

    	for (int y = 0; y < movePriorityMap.length; y++) {
    		for (int x = 0; x < movePriorityMap[y].length; x++) {
    			if (movePriorityMap[y][x] < 99 && movementMap[y][x] < 99 && inMovementRange(y, x)) {
    				PotentialMove potentialMove = new PotentialMove(movePriorityMap[y][x], y, x);
    				potentialMoves.add(potentialMove);
    			}
    		}
    	}
    	Comparator<PotentialMove> comparator = new Comparator<PotentialMove>() {

			@Override
			public int compare(PotentialMove o1, PotentialMove o2) {
				return o1.getPotential() - o2.getPotential();
			}
    	};
    	potentialMoves.sort(comparator);
    	PotentialMove selectedMove = potentialMoves.get(0);
    	movementIndexY = selectedMove.getY();
    	movementIndexX = selectedMove.getX();
    }
    
	private void aiUseAbility() {
		List<Ability> abilityList = currUnit.getActiveAbilities();
		Comparator<Ability> c = new Comparator<Ability>() {

			@Override
			public int compare(Ability o1, Ability o2) {
				return o1.getPriority() - o2.getPriority();
			}
			
		};
		abilityList.sort(c);
		for (Ability ability: abilityList) {
			Unit unit = aiSelectTarget(ability.getRange());
			if (unit != null && ability.getStamCost(currUnit) <= currUnit.getCurrStamina()) {
				this.ability = ability;
				currUnit.setPotentialAbility(ability);
				currUnit.useAbility(ability);
				damageDealt = ability.dealDamage(currUnit, unit);
				attackedAnimation(unit);
		//			if (unit.willDie(((TargetedAbility)ability).calculateDamage(currUnit.getCurrStrength())))
		//				unit.isDying = true;
				break;
			}
		}
	}
	
	private Unit aiSelectTarget(int range) {
		for (int y = -range; y <= range; y++) {
			for (int x = -range; x <= range; x++) {
				if (avatarIndexY + y >= 0 && avatarIndexX + x >= 0 && avatarIndexY + y < tileMap.getHeight() && avatarIndexX + x < tileMap.getWidth() &&
					inRange(currUnit, range, avatarIndexY + y, avatarIndexX + x)) {
					Unit unit = orderOfBattle.getUnit(avatarIndexY + y, avatarIndexX + x);
					if (unit != null && unit.getFaction().equals("ALLY")) {
						return unit;
					}
				}
			}
		}
		return null;
	}

	public void endBattle() {
		frame.removeAll();
		manager.getExplorePanelManager().restore();
		frame.refresh();
	}
	
	private void backToMenu() {
		targetingMode = false;
		freeSelectMode = false;
		targetingIndexY = avatarIndexY;
		targetingIndexX = avatarIndexX;
		screenIndexY = avatarIndexY - 5;
		screenIndexX = avatarIndexX - 9;
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
	
	public void setFreeSelectMode() {
		this.freeSelectMode = true;
		targetingIndexY = screenIndexY + 5;
		targetingIndexX = screenIndexX + 9;
	}
	
	public OrderOfBattle getOrderOfBattle() {
		return orderOfBattle;
	}

	public int getTargetingIndexY() {
		return targetingIndexY;
	}

	public int getTargetingIndexX() {
		return targetingIndexX;
	}

	public boolean isTargetingMode() {
		return targetingMode;
	}
	
	public boolean isFreeSelectMode() {
		return freeSelectMode;
	}
	
	public void checkAiTurn() {
		if (currUnit.getFaction().equals("ENEMY")) {
			aiTurn();
		}
	}
	
	public void setDamageDealt(int damage) {
		this.damageDealt = damage;
	}
}
