package map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import javax.imageio.ImageIO;
import javax.swing.Timer;

import event.Ability;
import event.Armor;
import unit.Behavior;
import unit.OrderOfBattle;
import unit.Unit;
import unit.UnitInfoPanel;

public class BattlePanel extends MapPanel {

	private BattlePanelManager manager;
	private boolean freeSelectMode;
	private boolean enemyMap;
	private boolean rangeMap;
	private boolean targetingMode;
	private boolean animating;
	private int deathAnimation;

	int screenIndexX = 0;
	int screenIndexY = 0;
	
	private boolean partyPlacementMode;
	private boolean armyPlacementMode;
	private int[][] oldColorsTakingDamage = null;
	private int[][] oldColorsDying = null;
	private Unit unitTakingDamage;
	private Unit unitDying;
	transient BufferedImage unitDenotations;
	

	int moveAnimationOffsetX = 0;
	int moveAnimationOffsetY = 0;
	
	public BattlePanel(GameFrame frame) {
		frame.add(this);
		this.frame = frame;
		this.targetingMode = false;
		this.animating = false;
		this.enemyMap = false;
		this.rangeMap = false;
		this.partyPlacementMode = true;
		this.armyPlacementMode = false;
		this.deathAnimation = 0;
        this.setBounds(0, 0, GraphicsConstants.BATTLE_MAP_WIDTH, GraphicsConstants.BATTLE_MAP_HEIGHT);
    	try {
			unitDenotations = ImageIO.read(getClass().getResourceAsStream("/testUnits/sprites/unitDenotations.gif"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		manager.setDominantPanel(this);
		
		BattleInfoPanel infoPanel = new BattleInfoPanel("BattleInfoPanel", frame, manager);
		UnitInfoPanel unitInfoPanel = new UnitInfoPanel("UnitInfoPanel", frame, manager);
		BattleMenuPanel battleMenuPanel = new BattleMenuPanel("BattleMenuPanel", frame, manager, BattleMenuPanel.getStandardMenu(), 1);
		VictoryRewardsPanel victoryPanel = new VictoryRewardsPanel("VictoryRewardsPanel", frame, manager, VictoryRewardsPanel.getStandardMenu(), 2);
	}
	
	@Override
	public PanelManager getManager() {
		// TODO Auto-generated method stub
		return null;
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
    
   
    
//    private boolean inEnemyMovementRange(int rowIndex, int colIndex) {
//		for (Unit unit: orderOfBattle.getUnitList()) {
//			int[][] movementMap = createMovementMap(unit);
//			if (unit.getFaction().equals("ENEMY") && !aiTurn && /*inAbilityRange(unit, rowIndex, colIndex)*/ inMovementRange(movementMap, unit, rowIndex, colIndex)) {
//				return true;
//			}
//		}
//		return false;
//    }
    
	public void animateMovement() {
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
	
	public boolean isFreeSelectMode() {
		return freeSelectMode;
	}
	
	public void setFreeSelectMode(boolean bool) {
		this.freeSelectMode = bool;
	}
	
	public boolean isTargetingMode() {
		return targetingMode;
	}
	
	public void setTargetingMode(boolean bool) {
		this.targetingMode = bool;
	}
	
	public void toggleEnemyMap() {
		if (enemyMap) enemyMap = false;
		else enemyMap = true;
	}
	
	public void toggleRangeMap() {
		if (rangeMap) rangeMap = false;
		else rangeMap = true;
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
	
}
