package map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import javax.imageio.ImageIO;
import javax.swing.Timer;

import misc.StartMenuPanel;
import misc.StartPanelManager;

import org.json.JSONArray;

import unit.AIMove;
import unit.Army;
import unit.Behavior;
import unit.OrderOfBattle;
import unit.Party;
import unit.Squad;
import unit.Stats;
import unit.Unit;
import unit.UnitInfoPanel;
import event.Ability;
import event.Armor;
import event.Event;
import event.MenuItem;

public class BattleMap extends GenericMap {

	private OrderOfBattle orderOfBattle;
	private BattlePanelManager manager;
	private List<Squad> squadPlacementList;
	private BattleMenuPanel squadPlacementMenuPanel;
//	private List<MenuItem> menuItems;
	private Unit currUnit;
	private Squad currSquad;
	private boolean isStarted;
	private boolean targetingMode;
	private boolean animating;
	private boolean aiTurn;
	private boolean freeSelectMode;
	private boolean enemyMap;
	private boolean rangeMap;
	private boolean partyPlacementMode;
	private boolean armyPlacementMode;
	private int placementIndex;
	private int numPlaced;
	private int deathAnimation;
	private transient BufferedImage image;
	private int[][] oldColorsTakingDamage = null;
	private int[][] oldColorsDying = null;
	private Unit unitTakingDamage;
	private Unit unitDying;
	private Queue<AIMove> squadMoves;
	private AIMove aiMove;
	transient BufferedImage unitDenotations;
	int avatarIndexX;
	int avatarIndexY;
	int screenIndexX = 0;
	int screenIndexY = 0;
	int movementIndexX = 0;
	int movementIndexY = 0;
	int targetingIndexX = 0;
	int targetingIndexY = 0;
	int moveAnimationOffsetX = 0;
	int moveAnimationOffsetY = 0;
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
		this.isStarted = false;
		this.targetingMode = false;
		this.animating = false;
		this.aiTurn = false;
		this.enemyMap = false;
		this.rangeMap = false;
		this.partyPlacementMode = true;
		this.armyPlacementMode = false;
		this.placementIndex = 0;
		this.numPlaced = 0;
		this.deathAnimation = 0;
		this.squadPlacementList = new ArrayList<Squad>();
        this.setBounds(0, 0, GraphicsConstants.BATTLE_MAP_WIDTH, GraphicsConstants.BATTLE_MAP_HEIGHT);
    	try {
			unitDenotations = ImageIO.read(getClass().getResourceAsStream("/testUnits/sprites/unitDenotations.gif"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		manager.setDominantPanel(this);

		


		orderOfBattle = new OrderOfBattle(tileMap);
		squadPlacementList.addAll(party.getSquadList());
		
		//add ally units from party/army
		for(Squad squad: squadPlacementList) {
			squad.generateUnitQueue();
        	squad.generateUnitPlacementList();
			orderOfBattle.addSquad(squad);
//			for (Unit unit: squad.getUnitList())
//				orderOfBattle.addUnit(unit);
		}
		
		//add enemy units scripted from map
		for (Squad squad: tempSquadList) {
			squad.generateUnitQueue();
			orderOfBattle.addSquad(squad);
			for (Unit unit: squad.getUnitList())
				orderOfBattle.addUnit(unit);
		}
		orderOfBattle.setInitialOrdering();
        currSquad = squadPlacementList.get(0);
		currUnit = party.getUnit(numPlaced);
		centerScreen(0);
        
		BattleInfoPanel infoPanel = new BattleInfoPanel("BattleInfoPanel", frame, manager);
		UnitInfoPanel unitInfoPanel = new UnitInfoPanel("UnitInfoPanel", frame, manager);
		BattleMenuPanel battleMenuPanel = new BattleMenuPanel("BattleMenuPanel", frame, manager, BattleMenuPanel.getStandardMenu(), 1);
		VictoryRewardsPanel victoryPanel = new VictoryRewardsPanel("VictoryRewardsPanel", frame, manager, VictoryRewardsPanel.getStandardMenu(), 2);
        
        openSquadPlacementMenu();
		executeEvents();
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
        this.drawMap(g2d, screenIndexX, screenIndexY, 0, 0, GraphicsConstants.BATTLE_MAP_HEIGHT, GraphicsConstants.BATTLE_MAP_WIDTH, 0, 0);
        if (partyPlacementMode || armyPlacementMode) {
        	this.drawPlacement(g2d);
        } else {
        	if (targetingMode) {
	        	this.drawAOE(g2d);
	        	this.drawRange(g2d);
	        	this.drawTarget(g2d);
	        } else if (freeSelectMode) {
	        	this.drawTargetMap(g2d);
	        	this.drawTarget(g2d);
	        } else if (!animating){
	        	if (!aiTurn) {
	        		movementMap = createMovementMap(currUnit);
	        		if (rangeMap)
	        			this.drawMovementMap(g2d, currUnit);
	            	this.drawMovement(g2d);
	            	if (enemyMap)
	            		this.drawTotalEnemyMap(g2d);
	        	}
	        }
        }
	        this.drawUnits(g2d);
        if (avatarIndexY != movementIndexY || avatarIndexX != movementIndexX) {
		    this.drawMovementCursor(g2d);
		    this.drawMovementPath(g2d);
        }
    }
    
    private void drawPlacement(Graphics2D g2d) {
    	List<Coordinates> coordinatesList = null;
    	if (partyPlacementMode)
    		coordinatesList = partyPlacements;
    	if (armyPlacementMode)
    		coordinatesList = armyPlacements;
    	g2d.setColor(new Color(0, 255, 0));
    	for (Coordinates coordinates: coordinatesList) {
    		g2d.fillRect((coordinates.getX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE,
    					 (coordinates.getY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE, 
    					  GraphicsConstants.REGION_TILE_SIZE, GraphicsConstants.REGION_TILE_SIZE);
    	}
    	Coordinates coordinates = coordinatesList.get(placementIndex);
//        g2d.setColor(new Color(0, 255, 255));
//		g2d.fillOval((coordinates.getX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE,
//					 (coordinates.getY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE,
//					  GraphicsConstants.REGION_TILE_SIZE, GraphicsConstants.REGION_TILE_SIZE);
//		g2d.setColor(new Color(0,0,0));
//		g2d.drawString(currUnit.getName(), (coordinates.getX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE + (GraphicsConstants.REGION_TILE_SIZE/4),
//					  (coordinates.getY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE + (GraphicsConstants.REGION_TILE_SIZE/2) + 4);
//		g2d.setColor(new Color(0, 0, 0));
//		GraphicsConstants.drawOval(g2d, (coordinates.getX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE,
//			 	 (coordinates.getY()- screenIndexY) * GraphicsConstants.REGION_TILE_SIZE,
//			 	  GraphicsConstants.REGION_TILE_SIZE, GraphicsConstants.REGION_TILE_SIZE, 3);
		BufferedImage unitImage = currUnit.getImage();
    	int offsetX = GraphicsConstants.REGION_TILE_SIZE/2 - (unitImage.getWidth(null) * GraphicsConstants.TILE_SIZE_SELECTED/2);
    	int offsetY = GraphicsConstants.REGION_TILE_SIZE/2 - (unitImage.getHeight(null) * GraphicsConstants.TILE_SIZE_SELECTED/2);
		g2d.drawImage(unitImage, (coordinates.getX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE + offsetX,
					 (coordinates.getY()- screenIndexY) * GraphicsConstants.REGION_TILE_SIZE + offsetY,
					  GraphicsConstants.REGION_TILE_SIZE, GraphicsConstants.REGION_TILE_SIZE, null);
    }
	
	private void drawUnits(Graphics2D g2d) {
		List<Unit> unitList = orderOfBattle.getUnitList();

		for (Unit unit: unitList) {
//			Color c = new Color(255, 0, 0);
//			if (unit.getFaction().equals("ALLY")) {
//				c = new Color(0, 255, 255);
//				if (unit.isTakingDamage())
//					c = new Color (0, 70, 70);
//				}
//			else if (unit.getFaction().equals("ENEMY")) {
//				c = new Color(255, 0 ,0);
//				if (unit.isTakingDamage())
//					c = new Color (50, 0, 0);
//			}

//	        g2d.setColor(c);
//			g2d.fillOval((unit.getPosIndexX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE,
//						 (unit.getPosIndexY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE,
//						  GraphicsConstants.REGION_TILE_SIZE, GraphicsConstants.REGION_TILE_SIZE);
//			g2d.setColor(new Color(0,0,0));
//			g2d.drawString(unit.getName(), (unit.getPosIndexX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE + (GraphicsConstants.REGION_TILE_SIZE/4),
//						  (unit.getPosIndexY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE + (GraphicsConstants.REGION_TILE_SIZE/2) + 4);
//			g2d.setColor(new Color(0, 0, 0));
//			if (unit.equals(currUnit)) {
//				g2d.setColor(new Color(255, 255, 0));		
//			}
//			GraphicsConstants.drawOval(g2d, (unit.getPosIndexX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE,
//				 	 (unit.getPosIndexY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE,
//				 	  GraphicsConstants.REGION_TILE_SIZE, GraphicsConstants.REGION_TILE_SIZE, 3);
			BufferedImage unitImage = unit.getImage();
			int offsetX = 0;
			int offsetY = 0;
			if (unit.equals(currUnit) && animating) {
				unitImage = image;
				offsetX = moveAnimationOffsetX;
				offsetY = moveAnimationOffsetY;
			}
	    	offsetX += GraphicsConstants.REGION_TILE_SIZE/2 - (unitImage.getWidth(null) * GraphicsConstants.TILE_SIZE_SELECTED/2);
	    	offsetY += GraphicsConstants.REGION_TILE_SIZE/2 - (unitImage.getHeight(null) * GraphicsConstants.TILE_SIZE_SELECTED/2);
	    	if (unit.isTakingDamage()) {
	    		oldColorsTakingDamage = GraphicsConstants.colorWhite(unitImage, 255, oldColorsTakingDamage);
	    		unitTakingDamage = unit;
	    	} else if (oldColorsTakingDamage != null && unit.equals(unitTakingDamage)) {
	    		unitImage = GraphicsConstants.colorNormal(unitImage, 0, oldColorsTakingDamage);
	    		oldColorsTakingDamage = null;
	    	} else if (unit.isDying() && deathAnimation > 0) {
	    		oldColorsDying = GraphicsConstants.colorBlack(unitImage, 255, oldColorsDying);
	    		unitDying = unit;
	    	} else if (oldColorsDying != null && unit.equals(unitDying)) {
	    		unitImage = GraphicsConstants.colorNormal(unitImage, 0, oldColorsDying);
	    		oldColorsDying = null;
	    	}
			g2d.drawImage(unitImage, (unit.getPosIndexX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE + offsetX,
						 (unit.getPosIndexY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE + offsetY,
						  unitImage.getWidth(null)*GraphicsConstants.TILE_SIZE_SELECTED, unitImage.getHeight(null)*GraphicsConstants.TILE_SIZE_SELECTED, null);
//	    	g2d.drawImage(unitImage, GraphicsConstants.REGION_CENTER_X + offsetX, GraphicsConstants.REGION_CENTER_Y + offsetY, unitImage.getWidth(null)*2, unitImage.getHeight(null)*2, null);
			if (!animating) {
				if (unit.equals(unit.getSquad().getLeader())) {
					BufferedImage image = unitDenotations.getSubimage(0, 0, 16, 16);
					g2d.drawImage(image, (unit.getPosIndexX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE,
							 (unit.getPosIndexY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE,
							 image.getWidth(null)*GraphicsConstants.TILE_SIZE_SELECTED, image.getHeight(null)*GraphicsConstants.TILE_SIZE_SELECTED, null);
				}
				if (!unit.inLeadership()) {
					BufferedImage image = unitDenotations.getSubimage(16, 0, 16, 16);
					g2d.drawImage(image, (unit.getPosIndexX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE,
							 (unit.getPosIndexY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE,
							 image.getWidth(null)*GraphicsConstants.TILE_SIZE_SELECTED, image.getHeight(null)*GraphicsConstants.TILE_SIZE_SELECTED, null);
				}
			}
		}
		for (Unit unit: unitList) {
			int offsetX = 0;
			int offsetY = 0;
			if (unit.equals(currUnit) && animating) {
				offsetX = moveAnimationOffsetX;
				offsetY = moveAnimationOffsetY;
			}
			//hp bar
			g2d.setColor(new Color(255,0,0));
			g2d.fillRect((unit.getPosIndexX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE + offsetX,
		 	 			 (unit.getPosIndexY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE -4 + offsetY,
		 	 			  GraphicsConstants.REGION_TILE_SIZE, 4);
			g2d.setColor(new Color(0,255,0));
			g2d.fillRect((unit.getPosIndexX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE + offsetX,
		 	 			 (unit.getPosIndexY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE -4 + offsetY,
		 	 			 (int)(GraphicsConstants.REGION_TILE_SIZE * (((double)unit.getCurrHP())/((double)unit.getHP()))), 4);
			g2d.setColor(new Color(0,0,0));
			g2d.drawRect((unit.getPosIndexX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE + offsetX,
	 	 			 (unit.getPosIndexY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE - 4 + offsetY,
	 	 			  GraphicsConstants.REGION_TILE_SIZE, 4);
			
			//armor bar
			g2d.setColor(new Color(0,0,0));
			g2d.fillRect((unit.getPosIndexX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE + offsetX,
		 	 			 (unit.getPosIndexY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE + offsetY,
		 	 			  GraphicsConstants.REGION_TILE_SIZE, 4);
			g2d.setColor(new Color(255,255,0));
			g2d.fillRect((unit.getPosIndexX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE + offsetX,
		 	 			 (unit.getPosIndexY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE + offsetY,
		 	 			 (int)(GraphicsConstants.REGION_TILE_SIZE * (double)unit.getTotalAbsorption()), 4);
			g2d.setColor(new Color(0,0,0));
			g2d.drawRect((unit.getPosIndexX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE + offsetX,
	 	 			 (unit.getPosIndexY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE + offsetY,
	 	 			  GraphicsConstants.REGION_TILE_SIZE, 4);
			double totalAbsorption = 0;
			for (Armor armor: unit.getArmorList()) {
				totalAbsorption += armor.getAbsorption();
				g2d.drawRect((int) ((unit.getPosIndexX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE + offsetX + GraphicsConstants.REGION_TILE_SIZE * totalAbsorption),
		 	 			 (unit.getPosIndexY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE + offsetY,
		 	 			  1, 4);
			}
			
			//stamina bar
			g2d.setColor(new Color(255,0,255));
			g2d.fillRect((unit.getPosIndexX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE + offsetX,
		 	 			 (unit.getPosIndexY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE + 4 + offsetY,
		 	 			  GraphicsConstants.REGION_TILE_SIZE, 4);
			g2d.setColor(new Color(255,125,0));
			g2d.fillRect((unit.getPosIndexX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE + offsetX,
		 	 			 (unit.getPosIndexY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE + 4 + offsetY,
		 	 			 (int)(GraphicsConstants.REGION_TILE_SIZE * (((double)unit.getCurrStamina())/((double)unit.getStamina()))), 4);
			g2d.setColor(new Color(0,0,0));
			g2d.drawRect((unit.getPosIndexX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE + offsetX,
	 	 			 (unit.getPosIndexY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE + 4 + offsetY,
	 	 			  GraphicsConstants.REGION_TILE_SIZE, 4);
			
			//mp bar, if applicable
			if (unit.getMP() > 0) {
				g2d.setColor(new Color(255,0,255));
				g2d.fillRect((unit.getPosIndexX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE + offsetX,
			 	 			 (unit.getPosIndexY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE + 8 + offsetY,
			 	 			  GraphicsConstants.REGION_TILE_SIZE, 4);
				g2d.setColor(new Color(0,0,255));
				g2d.fillRect((unit.getPosIndexX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE + offsetX,
			 	 			 (unit.getPosIndexY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE + 8 + offsetY,
			 	 			 (int)(GraphicsConstants.REGION_TILE_SIZE * (((double)unit.getCurrMP())/((double)unit.getMP()))), 4);
				g2d.setColor(new Color(0,0,0));
				g2d.drawRect((unit.getPosIndexX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE + offsetX,
		 	 			 (unit.getPosIndexY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE + 8 + offsetY,
		 	 			  GraphicsConstants.REGION_TILE_SIZE, 4);
			}
			
			
			
			String valueStr = "";
			if (unit.isTakingDamage() && ability != null) {
				int value = damageDealt;
				g2d.setColor(new Color(255,0,0));
				if (value < 0)
					g2d.setColor(new Color(0,255,0));
				if (value == 0) {
					if (ability.calculateAddedStamina(currUnit) > 0) {
						g2d.setColor(new Color(255,125,0));
						value = ability.calculateAddedStamina(currUnit);
					} else if (currUnit.isStunned()) {
						g2d.setColor(new Color(255,0,0));
						valueStr = "STUN";
					}
				}
				value = Math.abs(value);
				if (value != 0) {
					valueStr = String.valueOf(value);
				}
				g2d.drawString("" + valueStr,
							  (unit.getPosIndexX() - screenIndexX) * GraphicsConstants.REGION_TILE_SIZE + 10 * GraphicsConstants.TILE_SIZE_SELECTED - (4 * valueStr.length()),
							  (unit.getPosIndexY() - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE + 11 * GraphicsConstants.TILE_SIZE_SELECTED);
			}
		}
	}
    
	private void drawAOE(Graphics2D g2d) {
        g2d.setColor(new Color(125, 0, 0));
    	if (ability.hasParam("Area Of Effect")) {
    		int[][] aoe = (int[][]) ability.get("Area Of Effect");
			for (int row = 0; row < aoe.length; row++) {
				for (int col = 0; col < aoe[row].length; col++) {
					int indexX = targetingIndexX - screenIndexX + (col - (aoe[row].length/2));
					int indexY = targetingIndexY - screenIndexY + (row - (aoe.length/2));
    				if (aoe[row][col] > 0 && indexX >= 0 && indexX < tileMap.getWidth() && indexY >= 0 && indexY <= tileMap.getHeight()) {
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
				if (inRange(rowIndex, colIndex, ability)) {
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
        
    public int[][] createMovementMap(Unit currUnit) {
    	int[][] movementMap = new int[tileMap.getHeight()][tileMap.getWidth()];
    	int searchSize = 99; 
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
        	if (y > 0 && movementMap[y-1][x] > value && (tileMap.getTile(y-1, x).isAccessable() || currUnit.isFlying())) {
        		if (orderOfBattle.getUnit(y-1, x) == null || !currUnit.isEnemy(orderOfBattle.getUnit(y-1, x)) ||
        			orderOfBattle.getUnit(y,x) == null || currUnit.isEnemy(orderOfBattle.getUnit(y, x))) {		
	        		movementMap[y-1][x] = value;
	            	queue.add(new Coordinates(y-1,x));
        		}
        	}
        	if (y < tileMap.getHeight() - 1 && movementMap[y+1][x] > value && (tileMap.getTile(y+1, x).isAccessable() || currUnit.isFlying())) {
        		if (orderOfBattle.getUnit(y+1, x) == null || !currUnit.isEnemy(orderOfBattle.getUnit(y+1, x)) ||
            		orderOfBattle.getUnit(y,x) == null || currUnit.isEnemy(orderOfBattle.getUnit(y, x))) {	
	        		movementMap[y+1][x] = value;
	            	queue.add(new Coordinates(y+1,x));
        		}
        	}
        	if (x > 0 && movementMap[y][x-1] > value && (tileMap.getTile(y, x-1).isAccessable() || currUnit.isFlying())) {
        		if (orderOfBattle.getUnit(y, x-1) == null || !currUnit.isEnemy(orderOfBattle.getUnit(y, x-1)) ||
            		orderOfBattle.getUnit(y,x) == null || currUnit.isEnemy(orderOfBattle.getUnit(y, x))) {	
	        		movementMap[y][x-1] = value;
	            	queue.add(new Coordinates(y,x-1));
        		}
        	}
        	if (x < tileMap.getWidth() - 1 && movementMap[y][x+1] > value && (tileMap.getTile(y, x+1).isAccessable() || currUnit.isFlying())) {
        		if (orderOfBattle.getUnit(y, x+1) == null || !currUnit.isEnemy(orderOfBattle.getUnit(y, x+1)) ||
            		orderOfBattle.getUnit(y,x) == null || currUnit.isEnemy(orderOfBattle.getUnit(y, x))) {	
        			movementMap[y][x+1] = value;
            		queue.add(new Coordinates(y,x+1));
        		}
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
    	boolean[][] movementBoolMap = createMovementBoolMap(movementMap, currUnit);
		for (int rowIndex = 0; rowIndex < tileMap.getHeight(); rowIndex++) {
			for (int colIndex = 0; colIndex < tileMap.getWidth(); colIndex++) {
				if (movementBoolMap[rowIndex][colIndex]) {
				    g2d.setColor(new Color(255, 255, 0));
			        GraphicsConstants.drawRect(g2d, (colIndex - screenIndexX)* GraphicsConstants.REGION_TILE_SIZE, 
			      			 (rowIndex - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE, 
			      			 GraphicsConstants.REGION_TILE_SIZE, 
			      			 GraphicsConstants.REGION_TILE_SIZE, 2);
				}
			}
		}
    }
    
    private void drawMovementMap(Graphics2D g2d, Unit unit) {
    	if (unit.isStunned())
    		return;
    	int[][] movementMap = createMovementMap(unit);
       	int[][] movementAbilityMap = new int[tileMap.getHeight()][tileMap.getWidth()];
       	updateMovementAbilityMap(movementMap, movementAbilityMap, unit);
       	Unit leader = unit.getSquad().getLeader();
		for (int rowIndex = 0; rowIndex < tileMap.getHeight(); rowIndex++) {
			for (int colIndex = 0; colIndex < tileMap.getWidth(); colIndex++) {
				if (movementAbilityMap[rowIndex][colIndex] == 2) {
					g2d.setColor(new Color(0, 255, 0, 100));
					g2d.fillRect((colIndex - screenIndexX)* GraphicsConstants.REGION_TILE_SIZE, 
			      			 (rowIndex - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE, 
			      			 GraphicsConstants.REGION_TILE_SIZE, 
			      			 GraphicsConstants.REGION_TILE_SIZE);
				}
				if (movementAbilityMap[rowIndex][colIndex] == 1 && inRange(leader, leader.getLeadership(), rowIndex, colIndex) || 
					movementAbilityMap[rowIndex][colIndex] == 1 && leader.equals(unit)) {
					g2d.setColor(new Color(0, 255, 255, 180));
					g2d.fillRect((colIndex - screenIndexX)* GraphicsConstants.REGION_TILE_SIZE, 
			      			 (rowIndex - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE, 
			      			 GraphicsConstants.REGION_TILE_SIZE, 
			      			 GraphicsConstants.REGION_TILE_SIZE);
				} else if (movementAbilityMap[rowIndex][colIndex] == 1) {
					g2d.setColor(new Color(128, 0, 128, 60));
					g2d.fillRect((colIndex - screenIndexX)* GraphicsConstants.REGION_TILE_SIZE, 
			      			 (rowIndex - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE, 
			      			 GraphicsConstants.REGION_TILE_SIZE, 
			      			 GraphicsConstants.REGION_TILE_SIZE);
				}
//				if (inRange(leader, leader.getLeadership(), rowIndex, colIndex)) {
//					g2d.setColor(new Color(255, 255, 255, 200));
//					g2d.fillRect((colIndex - screenIndexX)* GraphicsConstants.REGION_TILE_SIZE, 
//			      			 (rowIndex - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE, 
//			      			 GraphicsConstants.REGION_TILE_SIZE, 
//			      			 GraphicsConstants.REGION_TILE_SIZE);
//				}
			}
		}
    }
    
    private void drawEnemyMap(Graphics2D g2d, Unit unit) {
    	int[][] movementAbilityMap = new int[tileMap.getHeight()][tileMap.getWidth()];
    	createEnemyMovementAbilityMap(movementAbilityMap, unit);
		for (int rowIndex = 0; rowIndex < tileMap.getHeight(); rowIndex++) {
			for (int colIndex = 0; colIndex < tileMap.getWidth(); colIndex++) {
				if (movementAbilityMap[rowIndex][colIndex] == 2){
					g2d.setColor(new Color(255, 125 ,0, 180));
					g2d.fillRect((colIndex - screenIndexX)* GraphicsConstants.REGION_TILE_SIZE, 
			      			 (rowIndex - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE, 
			      			 GraphicsConstants.REGION_TILE_SIZE, 
			      			 GraphicsConstants.REGION_TILE_SIZE);
				}
				if (movementAbilityMap[rowIndex][colIndex] == 1){
					g2d.setColor(new Color(255, 0 ,0, 180));
					g2d.fillRect((colIndex - screenIndexX)* GraphicsConstants.REGION_TILE_SIZE, 
			      			 (rowIndex - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE, 
			      			 GraphicsConstants.REGION_TILE_SIZE, 
			      			 GraphicsConstants.REGION_TILE_SIZE);
				}
			}
		}

    }
    
    private void drawTotalEnemyMap(Graphics2D g2d) {
    	int[][] totalMovementAbilityMap = createTotalEnemyMovementAbilityMap();
		for (int rowIndex = 0; rowIndex < tileMap.getHeight(); rowIndex++) {
			for (int colIndex = 0; colIndex < tileMap.getWidth(); colIndex++) {
				if (totalMovementAbilityMap[rowIndex][colIndex] == 2){
					g2d.setColor(new Color(255, 125 ,0, 180));
					g2d.fillRect((colIndex - screenIndexX)* GraphicsConstants.REGION_TILE_SIZE, 
			      			 (rowIndex - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE, 
			      			 GraphicsConstants.REGION_TILE_SIZE, 
			      			 GraphicsConstants.REGION_TILE_SIZE);
				}
				if (totalMovementAbilityMap[rowIndex][colIndex] == 1){
					g2d.setColor(new Color(255, 0 ,0, 180));
					g2d.fillRect((colIndex - screenIndexX)* GraphicsConstants.REGION_TILE_SIZE, 
			      			 (rowIndex - screenIndexY) * GraphicsConstants.REGION_TILE_SIZE, 
			      			 GraphicsConstants.REGION_TILE_SIZE, 
			      			 GraphicsConstants.REGION_TILE_SIZE);
				}
			}
		}
    }
    
    private void drawTargetMap(Graphics2D g2d) {
    	Unit unit = orderOfBattle.getUnit(targetingIndexY, targetingIndexX);
    	if (unit != null) {
    		if (unit.getFaction().equals("ALLY")) {
    			drawMovementMap(g2d, unit);
    		}
    		if (unit.getFaction().equals("ENEMY")) {
    			drawEnemyMap(g2d, unit);
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
    		if (path.get(0).getY() - 1 >= 0 && movementMap[path.get(0).getY() - 1][path.get(0).getX()] == value - 1 &&
    		   (orderOfBattle.getUnit(path.get(0).getY() - 1, path.get(0).getX()) == null ||
    			!currUnit.isEnemy(orderOfBattle.getUnit(path.get(0).getY() - 1, path.get(0).getX())))) {
    			value--;
    			path.add(0, new Coordinates(path.get(0).getY() - 1, path.get(0).getX()));
    		} else if (path.get(0).getY() + 1 < movementMap.length && movementMap[path.get(0).getY() + 1][path.get(0).getX()] == value - 1 &&
    	    		   (orderOfBattle.getUnit(path.get(0).getY() + 1, path.get(0).getX()) == null ||
    	    			!currUnit.isEnemy(orderOfBattle.getUnit(path.get(0).getY() + 1, path.get(0).getX())))) {
    			value--;
    			path.add(0, new Coordinates(path.get(0).getY() + 1, path.get(0).getX()));
    		} else if (path.get(0).getX() - 1 >= 0 && movementMap[path.get(0).getY()][path.get(0).getX() - 1] == value - 1 &&
    	    		   (orderOfBattle.getUnit(path.get(0).getY(), path.get(0).getX() - 1) == null ||
    	    			!currUnit.isEnemy(orderOfBattle.getUnit(path.get(0).getY(), path.get(0).getX() - 1)))) {
    			value--;
    			path.add(0, new Coordinates(path.get(0).getY(), path.get(0).getX() - 1));
    		} else if (path.get(0).getX() + 1 < movementMap[0].length && movementMap[path.get(0).getY()][path.get(0).getX() + 1] == value - 1 &&
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

	private boolean inRange(int rowIndex, int colIndex, Ability ability) {
    	int verticalDiff = Math.abs(avatarIndexY - rowIndex);
    	int horizontalDiff = Math.abs(avatarIndexX - colIndex);
    	if (verticalDiff + horizontalDiff <= ability.getRange()) 
    		return true;
    	return false;
    }
		
	public static boolean inRange(Unit target, Unit source, Ability ability) {
		return inRange(target.getPosIndexY(), target.getPosIndexX(), source.getPosIndexY(), source.getPosIndexX(), ability);
	}
	
	public static boolean inRange(int targetY, int targetX, int sourceY, int sourceX, Ability ability) {
		if (ability.hasParam("Area Of Effect")) {
			int[][] aoe = (int[][]) ability.get("Area Of Effect");
			for (int row = 0; row < aoe.length; row++) {
				for (int col = 0; col < aoe[row].length; col++) {
					int indexX = targetX + (col - (aoe[row].length/2));
					int indexY = targetY + (row - (aoe.length/2));
					if (aoe[row][col] > 0) {
				    	int verticalDiff = Math.abs(sourceY - indexY);
				    	int horizontalDiff = Math.abs(sourceX - indexX);
				    	if (verticalDiff + horizontalDiff <= ability.getRange()) 
				    		return true;
					}
				}
			}
		} else {
	    	int verticalDiff = Math.abs(sourceY - targetY);
	    	int horizontalDiff = Math.abs(sourceX - targetX);
	    	if (verticalDiff + horizontalDiff <= ability.getRange()) 
	    		return true;
		}
    	return false;
    }
	
	private boolean[][] createMovementBoolMap(int[][] movementMap, Unit unit) {
		boolean[][] map = new boolean[tileMap.getHeight()][tileMap.getWidth()];
		for (int y = 0; y < tileMap.getHeight(); y++) {
			for (int x = 0; x < tileMap.getWidth(); x++) {
				map[y][x] = inMovementRange(movementMap, unit, y, x);
			}
		}
		return map;
	}
	
	private void updateMovementAbilityMap(int[][] movementMap, int[][] map, Unit unit) {
		if (unit.isStunned())
			return;
		for (int y = 0; y < tileMap.getHeight(); y++) {
			for (int x = 0; x < tileMap.getWidth(); x++) {
				if (inMovementRange(movementMap, unit, y, x)) {
					map[y][x] = 1;
					markAbilityRange(movementMap, map, unit, y, x);
				}
			}
		}
	}
	
	private int[][] createTotalEnemyMovementAbilityMap() {
		int[][] totalMovementAbilityMap = new int[tileMap.getHeight()][tileMap.getWidth()];
		for (Unit unit: orderOfBattle.getUnitList()) {
			if (unit.getFaction().equals("ENEMY")) {
				createEnemyMovementAbilityMap(totalMovementAbilityMap, unit);
			}
		}
		return totalMovementAbilityMap;
	}
	
	private void createEnemyMovementAbilityMap(int[][] movementAbilityMap, Unit unit) {
		int[][] movementMap = createMovementMap(unit);
		updateMovementAbilityMap(movementMap, movementAbilityMap, unit);
	}
	
//	private boolean[][] createAbilityRangeBoolMap(int[][] movementMap, boolean[][] movementBoolMap, Unit unit) {
//		boolean[][] abilityRangeBoolMap = new boolean[tileMap.getHeight()][tileMap.getWidth()];
//		for (int rowIndex = 0; rowIndex < tileMap.getHeight(); rowIndex++) {
//			for (int colIndex = 0; colIndex < tileMap.getWidth(); colIndex++) {
//				if (movementBoolMap[rowIndex][colIndex]) {
//					markAbilityRange(movementMap, abilityRangeBoolMap, unit, rowIndex, colIndex);
//				}
//			}
//		}
//		return abilityRangeBoolMap;
//	}
				
//	private void markAbilityRange(int[][] movementMap, boolean[][] abilityRangeBoolMap, Unit unit, int rowIndex, int colIndex) {
//		for (int y = 0; y < tileMap.getHeight(); y++) {
//			for (int x = 0; x < tileMap.getWidth(); x++) {
//				if (inAbilityRange(movementMap, unit, rowIndex, colIndex, y, x))
//					abilityRangeBoolMap[y][x] = true;
//			}
//		}
//	}
	
	private void markAbilityRange(int[][] movementMap, int[][] movementAbilityMap, Unit unit, int rowIndex, int colIndex) {
		for (int y = 0; y < tileMap.getHeight(); y++) {
			for (int x = 0; x < tileMap.getWidth(); x++) {
				if (inAbilityRange(movementMap, unit, rowIndex, colIndex, y, x) && movementAbilityMap[y][x] < 1)
					movementAbilityMap[y][x] = 2;
			}
		}
	}
    
    private boolean inMovementRange(int[][] movementMap, Unit unit, int rowIndex, int colIndex) {
    	if (movementMap[rowIndex][colIndex] <= unit.getCurrMovement() && 
    		movementMap[rowIndex][colIndex] * 5 <= unit.getCurrStamina() &&
    		(orderOfBattle.getUnit(rowIndex, colIndex) == null || orderOfBattle.getUnit(rowIndex, colIndex).equals(unit)))
    		return true;
    	return false;
    }
    
    private boolean inAbilityRange(int[][] movementMap, Unit unit, int sourceY, int sourceX, int targetY, int targetX) {
    	for (Ability ability: unit.getActiveAbilities()) {
    		if ((!ability.hasParam("Level") || ability.getLevel() >= 0) && inRange(targetY, targetX, sourceY, sourceX, ability) &&
    			canAfford(ability, movementMap, unit, sourceY, sourceX, targetY, targetX)) {
    			return true;
    		}
    	}
    	return false;
    }
    
//    private boolean inEnemyMovementRange(int rowIndex, int colIndex) {
//		for (Unit unit: orderOfBattle.getUnitList()) {
//			int[][] movementMap = createMovementMap(unit);
//			if (unit.getFaction().equals("ENEMY") && !aiTurn && /*inAbilityRange(unit, rowIndex, colIndex)*/ inMovementRange(movementMap, unit, rowIndex, colIndex)) {
//				return true;
//			}
//		}
//		return false;
//    }
    
    private boolean canAfford(Ability ability, int[][] movementMap, Unit unit, int sourceY, int sourceX, int targetY, int targetX) {
    	int diff = Behavior.getMoveDistance(movementMap, sourceY, sourceX, targetY, targetX);
    	int totalStaminaCost = ability.getStamCost(unit) + (diff * unit.getMovementCost());
    	int currentStamina = unit.getCurrStamina();
    	int currentMP = unit.getCurrMP();
    	if (!unit.equals(currUnit)) {
    		currentStamina += unit.calculateStaminaRegen();
    	}
    	int totalMPCost = ability.getMPCost(unit);
    	if (totalStaminaCost <= currentStamina && totalMPCost <= currentMP)
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
		if (partyPlacementMode || armyPlacementMode) {
			keyPressedPlacementMode(e);
			keyCode = 0;
		}
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
				if (tileMap.getTile(movementIndexY, movementIndexX).isAccessable() && orderOfBattle.getUnit(movementIndexY, movementIndexX) == null || orderOfBattle.getUnit(movementIndexY, movementIndexX) == currUnit) {
					moveCurrUnit();
					openActionMenu();
				}
				break;
			case 70:
				toggleFreeSelect();
				break;
			case 82:
				toggleRangeMap();
				break;
			case 84:
				toggleEnemyMap();
				break;
			case 80:
				endTurn();
				break;
			case 9:
				cycleSquadUnits();
				break;
			case 192:
				winBattle();
				break;
		}
//		currUnit.setPosition(avatarIndexY, avatarIndexX);
		checkTileTest();
		this.repaint();
	}

	private void keyPressedPlacementMode(KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch (keyCode) {
//		case 87:
//		case 83:
		case 65:
			selectPrevPosition();
			break;
		case 68:
			selectNextPosition();
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
			placeUnit();
			break;
		case 81:
		case 27:
			openSquadPlacementMenu();
			break;
		}
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
			if (canTargetSelf(ability) || 
			   (orderOfBattle.getUnit(targetingIndexY, targetingIndexX) != null && 
			    orderOfBattle.getUnit(targetingIndexY, targetingIndexX).getFaction().equals("ENEMY"))) {
				if (ability.hasParam("Area Of Effect")) {
					attackAOE();
				}else if (orderOfBattle.getUnit(targetingIndexY, targetingIndexX) != null) {
					attackTarget();
				}
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
			if (orderOfBattle.getUnit(targetingIndexY, targetingIndexX) != null) {
				openUnitInfo();
			}
			break;
		case 27:
		case 70:
		case 81:
			toggleFreeSelect();
			break;
		}
	}
	
	private void openUnitInfo() {
		manager.getUnitInfoPanel().displayUnit(orderOfBattle.getUnit(targetingIndexY, targetingIndexX));
	}

	private void selectPrevPosition() {
		int wrapAround = 0;
		Coordinates coordinates = null;
		if (partyPlacementMode)
			wrapAround = partyPlacements.size() - 1;
		if (armyPlacementMode)
			wrapAround = armyPlacements.size() - 1;
		if (placementIndex - 1 < 0)
			placementIndex = wrapAround;
		else
			placementIndex--;
		centerScreen(placementIndex);
	}
	
	private void selectNextPosition() {
		int limit = 0;
		if (partyPlacementMode)
			limit = partyPlacements.size();
		if (armyPlacementMode)
			limit = armyPlacements.size();
		if (placementIndex + 1 >= limit)
			placementIndex = 0;
		else
			placementIndex++;
		centerScreen(placementIndex);
	}
	
	private void placeUnit() {
		Army army = party.getArmy();
		boolean sameSquad = true;
		Unit unit = currUnit;
		currSquad.getUnitPlacementList().remove(currUnit);
		if (currSquad.getUnitPlacementList().size() > 0) {
			currUnit = currSquad.getUnitPlacementList().get(0);
		} else {
			sameSquad = false;
			squadPlacementList.remove(currSquad);
			if (squadPlacementList.size() > 0) {
				currSquad = squadPlacementList.get(0);	
				currUnit = currSquad.getUnitPlacementList().get(0);
			}
		} 
//		orderOfBattle.addSquad(new Squad(currUnit)); //remove this later, these squads should be added before the placements. 
													 //This will allow the player to see the move order before placements as well.
													 //This is only here temporarily until the party/army squad sorting is implemented.
		if (partyPlacementMode) {
			Coordinates coordinates = partyPlacements.get(placementIndex);
			unit.setPosition(coordinates.getY(), coordinates.getX());
			orderOfBattle.addUnit(unit);
			partyPlacements.remove(coordinates);
			placementIndex = 0;
			numPlaced++;
			if (partyPlacements.size() > 0 && squadPlacementList.size() > 0) {
//				currUnit = party.getUnitPlacementList().get(0);
//				if (sameSquad)
//					openUnitPlacementMenu();
//				else
					openSquadPlacementMenu();
		    	centerScreen(partyPlacements.get(placementIndex).getY(), partyPlacements.get(placementIndex).getX());
			} else if (armyPlacements.size() > 0 && squadPlacementList.size() > 0){
				numPlaced = 0;
//				party.addUnitPlacementList(army.getUnitListNotInParty());
//				currUnit = party.getUnitPlacementList().get(0);
//				if (sameSquad)
//					openUnitPlacementMenu();
//				else
					openSquadPlacementMenu();
		    	centerScreen(armyPlacements.get(placementIndex).getY(), armyPlacements.get(placementIndex).getX());
				partyPlacementMode = false;
				armyPlacementMode = true;
			} else {
				startBattle();
			}
		} else if (armyPlacementMode) {
			Coordinates coordinates = armyPlacements.get(placementIndex);
			unit.setPosition(coordinates.getY(), coordinates.getX());
			orderOfBattle.addUnit(unit);
			armyPlacements.remove(coordinates);
			placementIndex = 0;
			numPlaced++;
			if (armyPlacements.size() > 0) {
//				currSquad = tempSquadList.get(0);
//				if (sameSquad)
//					openUnitPlacementMenu();
//				else
					openSquadPlacementMenu();
		    	centerScreen(armyPlacements.get(placementIndex).getY(), armyPlacements.get(placementIndex).getX());
			} else {
				startBattle();
			}
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
		if (movementIndexY - 1 >= 0 && ((tileMap.getTile(movementIndexY - 1, movementIndexX).isAccessable() &&
			(orderOfBattle.getUnit(movementIndexY - 1, movementIndexX) == null || orderOfBattle.getUnit(movementIndexY - 1, movementIndexX).getFaction().equals("ALLY"))) || currUnit.isFlying()) &&
			movementMap[movementIndexY - 1][movementIndexX] <= currUnit.getCurrMovement()) 
			movementIndexY--;
	}
	
	private void moveDown() {
		if (movementIndexY + 1 < tileMap.getHeight() && ((tileMap.getTile(movementIndexY + 1, movementIndexX).isAccessable() &&
			(orderOfBattle.getUnit(movementIndexY + 1, movementIndexX) == null || orderOfBattle.getUnit(movementIndexY + 1, movementIndexX).getFaction().equals("ALLY"))) || currUnit.isFlying()) &&
			movementMap[movementIndexY + 1][movementIndexX] <= currUnit.getCurrMovement()) 
			movementIndexY++;
	}
	
	private void moveLeft() {
		if (movementIndexX - 1 >= 0 && ((tileMap.getTile(movementIndexY, movementIndexX - 1).isAccessable() &&
			(orderOfBattle.getUnit(movementIndexY, movementIndexX - 1) == null  || orderOfBattle.getUnit(movementIndexY, movementIndexX - 1).getFaction().equals("ALLY"))) || currUnit.isFlying()) &&
			movementMap[movementIndexY][movementIndexX - 1] <= currUnit.getCurrMovement()) 
			movementIndexX--;
	}
	
	private void moveRight() {
		if (movementIndexX + 1 < tileMap.getWidth() && ((tileMap.getTile(movementIndexY, movementIndexX + 1).isAccessable() &&
			(orderOfBattle.getUnit(movementIndexY, movementIndexX + 1) == null  || orderOfBattle.getUnit(movementIndexY, movementIndexX + 1).getFaction().equals("ALLY"))) || currUnit.isFlying()) &&
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
		if (inRange(targetingIndexY - 1, targetingIndexX, ability)  && targetingIndexY > 0)
			targetingIndexY--;
	}
	
	private void moveTargetDown() {
		if (inRange(targetingIndexY + 1, targetingIndexX, ability)  && targetingIndexY < tileMap.getHeight() - 1)
			targetingIndexY++;
	}
	
	private void moveTargetLeft() {
		if (inRange(targetingIndexY, targetingIndexX - 1, ability) && targetingIndexX > 0)
			targetingIndexX--;
	}
	
	private void moveTargetRight() {
		if (inRange(targetingIndexY, targetingIndexX + 1, ability) && targetingIndexX < tileMap.getWidth() - 1)
			targetingIndexX++;
	}

	private void moveCurrUnit() {
		Unit unit = currUnit;
		int movementUsed = movementMap[movementIndexY][movementIndexX];
		unit.subtractMovement(movementUsed);
		List<Coordinates> path = makeMovementPath();
		animating = true;
		frame.setAcceptingInput(false);
		CountDownLatch cdl = new CountDownLatch(1);
    	orderOfBattle.moveToPhantomPlane(unit); 
    	moveAnimationOffsetY = 0;
    	moveAnimationOffsetX = 0;
	    Timer timer = new Timer(50, new ActionListener() {
		   
		    int i = 1;
		    int animationCounter = 0;
		    
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            if(i < path.size()) {
	        		String direction = "";
	        		if (unit.getPosIndexY() - path.get(i).getY() > 0)
	        			direction = "up";
	        		if (unit.getPosIndexY() - path.get(i).getY() < 0)
	        			direction = "down";
	        		if (unit.getPosIndexX() - path.get(i).getX() > 0)
	        			direction = "left";
	        		if (unit.getPosIndexX() - path.get(i).getX() < 0)
	        			direction = "right";
	        		if (animationCounter < 4) {
		            	switch (direction) {
		            		case "up":
		            			moveAnimationOffsetY -= GraphicsConstants.REGION_TILE_SIZE/4;
		            			break;
		            		case "down":
		            			moveAnimationOffsetY += GraphicsConstants.REGION_TILE_SIZE/4;
		            			break;
		            		case "left":
		            			moveAnimationOffsetX -= GraphicsConstants.REGION_TILE_SIZE/4;
		            			break;
		            		case "right":
		            			moveAnimationOffsetX += GraphicsConstants.REGION_TILE_SIZE/4;
		            			break;
		            	}
		            	image = getAnimationImage(direction, animationCounter);
		            	animationCounter++;
		            }
	        		if (animationCounter == 4) {
		            	moveAnimationOffsetY = 0;
		            	moveAnimationOffsetX = 0;
		            	animationCounter = 0;
		            	orderOfBattle.movePhantomUnit(unit, path.get(i).getY(), path.get(i).getX());
		        		avatarIndexY = path.get(i).getY();
		        		avatarIndexX = path.get(i).getX();
		        		screenIndexY = path.get(i).getY() - 5;
		        		screenIndexX = path.get(i).getX() - 9;
		            	i++;
		            }
	                repaint();
	            } else {
	            	image = unit.getImage();
	            	orderOfBattle.moveToRegularPlane(unit);
	                animating = false;
	                frame.setAcceptingInput(true);
	                repaint();
	                cdl.countDown();
	                ((Timer)e.getSource()).stop();
	            }
	        }
	    });
	    timer.setRepeats(true);
	    timer.setDelay(30);
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
		((BattleMenuPanel)menuPanel).resetSelector();
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

		
		int[][] aoe = (int[][]) ability.get("Area Of Effect");
		for (int row = 0; row < aoe.length; row++) {
			for (int col = 0; col < aoe[row].length; col++) {
				int indexX = targetingIndexX + (col - (aoe[row].length/2));
				int indexY = targetingIndexY + (row - (aoe.length/2));
				if (aoe[row][col] > 0 && indexX >= 0 && indexX < tileMap.getWidth() && indexY >= 0 && indexY <= tileMap.getHeight() &&
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
		frame.setAcceptingInput(false);
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
	    		frame.setAcceptingInput(true);
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
		frame.setAcceptingInput(false);
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
		    		frame.setAcceptingInput(true);
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
	
	private void startBattle() {
		partyPlacementMode = false;
		armyPlacementMode = false;
		isStarted = true;
		currSquad = orderOfBattle.getCurrSquad();
		currSquad.generateUnitQueue();
		currUnit = currSquad.getCurrUnit();
		setCoordinates(currUnit.getPosIndexY(), currUnit.getPosIndexX());
		orderOfBattle.generatePhantomQueue();
		screenIndexY = currUnit.getPosIndexY() - 5;
		screenIndexX = currUnit.getPosIndexX() - 9;
		if (currUnit.getFaction().equals("ENEMY")) {
			squadMoves = currSquad.getBehavior().getSquadMoves(tileMap, orderOfBattle, currSquad);
			aiTurn();
		}
	}
	
	private void winBattle() {
		System.out.println("you win!");
		VictoryRewardsPanel menuPanel = manager.getVictoryPanel();
		menuPanel.displayPanel();
		menuPanel.setExpReward(expReward);
		menuPanel.setGoldReward(goldReward);
		menuPanel.executeRewards();
		manager.changeDominantPanel(menuPanel);
		frame.setAcceptingInput(true);
		frame.refresh();
//		endBattle();
	}
	
	private void loseBattle() {
		endBattle();
		quitToMenu();
	}
	
	public void quitToMenu() {
		manager.getExplorePanelManager().getMap().stopBGM();
		frame.removeAll();
		StartPanelManager startManager = new StartPanelManager(frame);
		StartMenuPanel menuPanel = new StartMenuPanel("Start Game Screen", frame, startManager, StartMenuPanel.getStandardMenu(), 1);		
		startManager.changeDominantPanel(menuPanel);
        menuPanel.displayPanel();
		frame.refresh();
	}
	
	public void endTurn() {
		aiTurn = false;
		frame.setAcceptingInput(true);
		Squad squad = currSquad;
		currUnit.endTurn();
		currUnit = orderOfBattle.getNextUnit();
		currSquad = currUnit.getSquad();
		if (!currSquad.equals(squad)) { //if a new squad
			currUnit.beginSquadTurn(this);
			if (currUnit.getFaction().equals("ENEMY")) {
				System.out.println("assigning squad moves");
				squadMoves = currSquad.getBehavior().getSquadMoves(tileMap, orderOfBattle, currSquad);
			}
		}
		image = currUnit.getImage();
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
		frame.setAcceptingInput(false);
		aiMove = squadMoves.poll();
		System.out.print("AI: polling next move: " + aiMove.getUnit().getName() + " " + aiMove.getY() + " " + aiMove.getX());
		if (aiMove.getTarget() != null)
			System.out.println(" " + aiMove.getTarget().getName());
		else
			System.out.println("");
		String unitName = aiMove.getUnit().getName();
		for (Unit unit: orderOfBattle.getUnitList()) {
			if (unit.getName().equals(unitName) && unit.getFaction().equals("ENEMY")) {
				currUnit = unit;
			}
		}
//		currUnit = aiMove.getUnit();
		if (!currUnit.isDying()) {
			aiMove();
			aiUseAbility();
	//		try {
	//			Thread.sleep(1000);
	//		} catch (InterruptedException e) {
	//			e.printStackTrace();
	//		}
		}
		endTurn();
	}


	private void aiMove() {
		//calculate possible moves
    	movementMap = createMovementMap(currUnit);
//    	int[][] movePriorityMap = createMovePriorityMap();
//    	aiSelectMove(movePriorityMap);
    	
    	//use these two lines to set where you want the unit to move
    	movementIndexY = aiMove.getY();
    	movementIndexX = aiMove.getX();
    	
    	
    	//set movementIndexX and Y
    	//move near an ALLY unit i guess
    	//maybe create move priority map

    	moveCurrUnit();
//		currUnit.setPosition(avatarIndexY, avatarIndexX);
		//choose move
		
	}

//	private int[][] createMovePriorityMap() {
//		int[][] movePriorityMap = new int[tileMap.getHeight()][tileMap.getWidth()];
//		int acquisitionRange = 99; //9 is best number for testing
//    	for (int i = 0; i < movePriorityMap.length; i++)
//    		Arrays.fill(movePriorityMap[i], acquisitionRange);
//    	currUnit.markMovementPriority(movePriorityMap, movementMap, tileMap);
//    	return movePriorityMap;
//	}
	
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
    	return inRange(unit.getPosIndexY(), unit.getPosIndexX(), rowIndex, colIndex, range);
    }
    
    public static boolean inRange(int y, int x, int newY, int newX, int range) {
    	int verticalDiff = Math.abs(y - newY);
    	int horizontalDiff = Math.abs(x - newX);
    	if (verticalDiff + horizontalDiff <= range)
    		return true;
    	return false;
    }
    
//    private void aiSelectMove(int[][] movePriorityMap) {
//    	List<PotentialMove> potentialMoves = new ArrayList<PotentialMove>();
//    	potentialMoves.add(new PotentialMove(99, avatarIndexY, avatarIndexX));
////    	for (int i = 0; i < movementMap.length; i++) {
////    		for (int j = 0; j < movementMap[i].length; j++) {
////    			if (orderOfBattle.getUnit(i, j) == null)
////    				System.out.print("0 ");
////    			else System.out.print("1 ");
////    		}
////    		System.out.println("");
////    	}
////		System.out.println("");
//
//    	for (int y = 0; y < movePriorityMap.length; y++) {
//    		for (int x = 0; x < movePriorityMap[y].length; x++) {
//    			if (movePriorityMap[y][x] < 99 && movementMap[y][x] < 99 && inMovementRange(movementMap, currUnit, y, x) /*&& 
//    				currUnit.hasAcquisitionRange() && inOrigRange(currUnit, currUnit.getAcquisitionRange(), y, x)*/) {
//    				PotentialMove potentialMove = new PotentialMove(movePriorityMap[y][x], y, x);
//    				potentialMoves.add(potentialMove);
//    			}
//    		}
//    	}
//    	Comparator<PotentialMove> comparator = new Comparator<PotentialMove>() {
//
//			@Override
//			public int compare(PotentialMove o1, PotentialMove o2) {
//				return o1.getPotential() - o2.getPotential();
//			}
//    	};
//    	potentialMoves.sort(comparator);
//    	PotentialMove selectedMove = potentialMoves.get(0);
//    	movementIndexY = selectedMove.getY();
//    	movementIndexX = selectedMove.getX();
//    }
    
	private void aiUseAbility() {
//		List<Ability> abilityList = currUnit.getActiveAbilities();
//		Comparator<Ability> c = new Comparator<Ability>() {
//
//			@Override
//			public int compare(Ability o1, Ability o2) {
////				if (o1.calculateDamage(currUnit) >= target.getCurrHP() && o2.calculateDamage(currUnit) >= target.getCurrHP()) 
////					return o1.getStamCost(currUnit) - o2.getStamCost(currUnit);
////				else if (o1.calculateDamage(currUnit) >= target.getCurrHP())
////					return -1;
////				else if (o2.calculateDamage(currUnit) >= target.getCurrHP())
////					return 1;
////				else
//					return o1.getPriority() - o2.getPriority();
//			}
//			
//		};
//		abilityList.sort(c);
//		boolean attacking = false;
//		Unit target = null;
//		int priority = Integer.MAX_VALUE;
//		for (Ability ability: abilityList) {
//			Unit unit = aiSelectTarget(ability);
//			if (unit != null && ability.getStamCost(currUnit) <= currUnit.getCurrStamina()) {
//				if (priority > unit.getTargetPriority()) {
////					System.out.println(unit.getName() + " " + ability.getName() + " " + priority + " " + unit.getTargetPriority());
//					priority = unit.getTargetPriority();
//					target = unit;
//					this.ability = ability;
//					attacking = true;
//				}
//			}
//		}
		if (aiMove.getAbility() != null) {
			String abilityName = aiMove.getAbility().getName();
			for (Ability ability: currUnit.getActiveAbilities()) {
				if (ability.getName().equals(abilityName))
					this.ability = ability;
			}
//			ability = aiMove.getAbility();
			currUnit.setPotentialAbility(ability);
			currUnit.useAbility(ability);
			Unit target = null;
			for (Unit unit: orderOfBattle.getUnitList()) {
				if (unit.getName().equals(aiMove.getTarget().getName()) && unit.getFaction().equals(aiMove.getTarget().getFaction()))
					target = unit;
			}
			damageDealt = ability.dealDamage(currUnit, target);
			attackedAnimation(target);
		}
	}
	
//	private Unit aiSelectTarget(Ability ability) {
//		int range = ability.getRange();
//		int priority = Integer.MAX_VALUE;
//		Unit target = null;
//		for (int y = -range; y <= range; y++) {
//			for (int x = -range; x <= range; x++) {
//				if (avatarIndexY + y >= 0 && avatarIndexX + x >= 0 && avatarIndexY + y < tileMap.getHeight() && avatarIndexX + x < tileMap.getWidth() &&
//					inRange(currUnit, range, avatarIndexY + y, avatarIndexX + x)) {
//					Unit unit = orderOfBattle.getUnit(avatarIndexY + y, avatarIndexX + x);
//					if (unit != null && unit.getFaction().equals("ALLY")) {
//						int currPriority = ability.getStamCost(unit);
//						if (ability.calculateDamage(currUnit, unit) >= unit.getCurrHP())
//							currPriority -= 10000;
//						if (priority > currPriority) {
//							target = unit;
//							priority = currPriority;
//						}
//						target.setTargetPriority(priority);
//					}
//				}
//			}
//		}
//		return target;
//	}

	public void endBattle() {
		frame.setAcceptingInput(true);
		frame.removeAll();
		stopBGM();
		party.endBattleStatUpdates();
		orderOfBattle.cleanUp();
		manager.getExplorePanelManager().restore();
		manager.getExplorePanelManager().getMap().startBGM();
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
		}	
	}
	
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
	
	public Squad getCurrSquad() {
		return currSquad;
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
	
	private boolean canTargetSelf(Ability ability) {
		if (!ability.get("Subtype").equals("Targeted"))
			return true;
		if (ability.hasParam("Can Target Self") && (boolean)ability.get("Can Target Self"))
			return true;
		return false;
	}
	
	private void toggleFreeSelect() {
		if (freeSelectMode) {
			screenIndexY = avatarIndexY - 5;
			screenIndexX = avatarIndexX - 9;
			freeSelectMode = false;
		} else {
			targetingIndexY = screenIndexY + 5;
			targetingIndexX = screenIndexX + 9;
			movementIndexY = avatarIndexY;
			movementIndexX = avatarIndexX;
			freeSelectMode = true;
		}
	}
	
	private void toggleEnemyMap() {
		if (enemyMap) enemyMap = false;
		else enemyMap = true;
	}
	
	private void toggleRangeMap() {
		if (rangeMap) rangeMap = false;
		else rangeMap = true;
	}

	public boolean isStarted() {
		return isStarted;
	}
	
	public void centerScreen(Unit unit) {
		centerScreen(unit.getPosIndexY(), unit.getPosIndexX());
	}
	
	public void centerScreen(int y, int x) {
		screenIndexY = y - 5;
		screenIndexX = x - 9;
	}
	
	public void centerScreen(int placementIndex) {
		Coordinates coordinates = null;
		if (partyPlacementMode)
			coordinates = partyPlacements.get(placementIndex);
		if (armyPlacementMode)
			coordinates = armyPlacements.get(placementIndex);
		centerScreen(coordinates.getY(), coordinates.getX());
	}

	public boolean isPartyPlacementMode() {
		return partyPlacementMode;
	}	
	
	public boolean isArmyPlacementMode() {
		return armyPlacementMode;
	}
	
	private void openSquadPlacementMenu() {
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
    	BattleMap battleMap = ((BattlePanelManager) manager).getBattleMap();
    	Party party = battleMap.getParty();
		for (Squad squad: squadPlacementList) {
			MenuItem item = new MenuItem() {
				private String name = squad.getName();
				
				@Override
				public void execute(GamePanel panel) {
					BattleMenuPanel menuPanel = (BattleMenuPanel) panel;
					currSquad = squad;
					battleMap.openUnitPlacementMenu();
				}

				@Override
				public String getName() {
					return name;
				}
				
			};
			menuItems.add(item);
		}
		currSquad = squadPlacementList.get(0);
    	squadPlacementMenuPanel = new BattleMenuPanel("Squad Placement Menu", frame, (BattlePanelManager) manager, menuItems, 2);
    	squadPlacementMenuPanel.displayPanel();
    	manager.changeDominantPanel(squadPlacementMenuPanel);
		frame.refresh();
	}
	
	public void openUnitPlacementMenu() {
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
		for (Unit unit: currSquad.getUnitPlacementList()) {
			MenuItem item = new MenuItem() {
				private String name = unit.getName();

				@Override
				public void execute(GamePanel panel) {
					BattleMenuPanel menuPanel = (BattleMenuPanel) panel;
					menuPanel.closeAllMenus();
				}

				@Override
				public String getName() {
					return name;
				}
			};
			menuItems.add(item);
		}
		currUnit = currSquad.getUnitPlacementList().get(0);
    	BattleMenuPanel unitPlacementMenuPanel = new BattleMenuPanel("Unit Placement Menu", frame, (BattlePanelManager) manager, menuItems, 3);
    	unitPlacementMenuPanel.displayPanel();
    	//probably need to subpanel this instead of changing dominant panel
    	squadPlacementMenuPanel.subMenu = unitPlacementMenuPanel;
    	unitPlacementMenuPanel.superMenu = squadPlacementMenuPanel;
    	unitPlacementMenuPanel.displayPanel();
		frame.refresh();
	}
	
	public void setCurrSquad(int index) {
		currSquad = squadPlacementList.get(index);
	}
	
	public void setCurrUnit(int index) {
		setCurrUnit(currSquad.getPlacement(index));
	}
	
	public void setCurrUnit(Unit unit) {
		currUnit = unit;
	}

	@Override
	public PanelManager getManager() {
		return manager;
	}
	
//	public void walkingAnimation(String direction) {
//		animating = true;
//		frame.setAcceptingInput(false);
//		CountDownLatch cdl = new CountDownLatch(1);
//	    Timer timer = new Timer(0, new ActionListener() {
//		   
//		    int i = 0;
//		    
//	        @Override
//	        public void actionPerformed(ActionEvent e) {
//	            if(i < 4) {
//	            	switch (direction) {
//	            		case "up":
//	            			moveAnimationOffsetY += GraphicsConstants.REGION_TILE_SIZE/4;
//	            			break;
//	            		case "down":
//	            			moveAnimationOffsetY -= GraphicsConstants.REGION_TILE_SIZE/4;
//	            			break;
//	            		case "left":
//	            			moveAnimationOffsetX += GraphicsConstants.REGION_TILE_SIZE/4;
//	            			break;
//	            		case "right":
//	            			moveAnimationOffsetX -= GraphicsConstants.REGION_TILE_SIZE/4;
//	            			break;
//	            	}
//	            	image = getAnimationImage(direction, i);
//	            	repaint();
//	            } else {
//	            	if (!frame.hasNextInput())
//	            		image = currUnit.getImage();
//	                endAnimation(e);
//	            }
//	            i++;
//	        }
//	        
//	        private void endAnimation(ActionEvent e) {
//	        	moveAnimationOffsetX = 0;
//	        	moveAnimationOffsetY = 0;
//	        	animating = false;
//	    		frame.setAcceptingInput(true);
//                repaint();
//                cdl.countDown();
//                ((Timer)e.getSource()).stop();
//	        }
//	    });
//	    timer.setRepeats(true);
//	    timer.setDelay(60);
//	    timer.start();
//	    try {
//			cdl.await();
//		} catch (InterruptedException e1) {
//			e1.printStackTrace();
//		}
//	}
	
	private BufferedImage getAnimationImage(String direction, int x) {
		int y = 0;
		switch (direction) {
			case "left":
				y = 0;
				break;
			case "down":
				y = 1;
				break;
			case "up":
				y = 2;
				break;
			case "right":
				y = 3;
				break;
		}
		BufferedImage image = currUnit.getMoveAnimationImages().getSubimage(x * 32, y * 32, 32, 32);
		return image;
	}
	
	private void cycleSquadUnits() {
		currSquad.cycleUnit();
		currUnit = currSquad.getCurrUnit();
		setCoordinates(currUnit.getPosIndexY(), currUnit.getPosIndexX());
		screenIndexY = currUnit.getPosIndexY() - 5;
		screenIndexX = currUnit.getPosIndexX() - 9;
	}
}
