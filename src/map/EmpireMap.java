package map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.Timer;

import unit.Party;
import unit.Unit;
import event.Corridor;
import event.Encounter;
import event.EnterBase;
import event.EnterProvince;
import event.Event;
import event.MapEventManager;
import event.MapEvent;
import event.Province;
import event.SimEncounter;

public class EmpireMap extends GenericMap {
		private EmpirePanelManager manager;
		private boolean animating;
		private transient BufferedImage avatarStandingImage;
		private transient BufferedImage avatarImage;
		private transient BufferedImage avatarMoveAnimationImages;
		private Party party;
		private int y;
		private int x;
		private int targetingIndexY;
		private int targetingIndexX;
		
		public EmpireMap(String name, GameFrame frame, EmpirePanelManager manager, Party party) {
			frame.add(this);
			manager.setMap(this);
			party.setMapName(name);
	        loadMap(name, party);
	        this.setBounds(0, 0, GraphicsConstants.REGION_MAP_WIDTH, GraphicsConstants.REGION_MAP_HEIGHT);
			this.name = name;
			this.frame = frame;
			this.manager = manager;
			this.party = party;
			this.y = 0;
			this.x = 0;
			this.targetingIndexY = 0;
			this.targetingIndexX = 0;
			try {
				avatarStandingImage = ImageIO.read(getClass().getResourceAsStream("/testUnits/sprites/testRenal.gif"));
				avatarImage = avatarStandingImage;
				avatarMoveAnimationImages = ImageIO.read(getClass().getResourceAsStream("/testUnits/sprites/testRenalMoveAnimations.gif"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			executeEvents();
		}
		
		public void restoreMap() {
			frame.add(this);
			manager.setMap(this);
		}
		
//		public RegionMap(String name, GameFrame frame, JComponent prev) {
//			frame.getContentPane().removeAll();
//			frame.getContentPane().repaint();
//			frame.add(this);
//			this.name = name;
//			this.frame = frame;
//		}

	    @Override
	    public void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        Graphics2D g2d = (Graphics2D) g;
	        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	            RenderingHints.VALUE_ANTIALIAS_ON);
	        Color bgColor = new Color(0,0,0);
	        g2d.setColor(bgColor);
	        g2d.fillRect(0, 0, GraphicsConstants.REGION_MAP_WIDTH, GraphicsConstants.REGION_MAP_HEIGHT);
	        this.drawMap(g2d, targetingIndexX, targetingIndexY, GraphicsConstants.REGION_CENTER_X, GraphicsConstants.REGION_CENTER_Y,
	        			 GraphicsConstants.REGION_MAP_HEIGHT, GraphicsConstants.REGION_MAP_WIDTH, x, y);
	        drawProvinceBorders(g2d);
	        drawAvatar(g2d);
	        drawTarget(g2d);
	    }
		
	    public void drawAvatar(Graphics2D g2d) {
//	        Color c = new Color(0, 255, 0);
//	        g2d.setColor(c);
//	        g2d.fillOval(GraphicsConstants.REGION_CENTER_X, GraphicsConstants.REGION_CENTER_Y, GraphicsConstants.REGION_TILE_SIZE, GraphicsConstants.REGION_TILE_SIZE);
	    	int offsetX = (GraphicsConstants.REGION_TILE_SIZE - avatarImage.getWidth(null) * GraphicsConstants.TILE_SIZE_SELECTED)/2 + GraphicsConstants.REGION_CENTER_X;
	    	int offsetY = (GraphicsConstants.REGION_TILE_SIZE - avatarImage.getHeight(null) * GraphicsConstants.TILE_SIZE_SELECTED)/2 + GraphicsConstants.REGION_CENTER_Y;
	    	g2d.drawImage(avatarImage, (party.getAvatarIndexX() - targetingIndexX) * GraphicsConstants.REGION_TILE_SIZE + offsetX, (party.getAvatarIndexY() - targetingIndexY) * GraphicsConstants.REGION_TILE_SIZE + offsetY, avatarImage.getWidth(null)*GraphicsConstants.TILE_SIZE_SELECTED, avatarImage.getHeight(null)*GraphicsConstants.TILE_SIZE_SELECTED, null);
	    }
	    
	    private void drawTarget(Graphics2D g2d) {
	        g2d.setColor(new Color(255, 255, 0));
	        GraphicsConstants.drawRect(g2d, GraphicsConstants.REGION_CENTER_X, 
	        		 GraphicsConstants.REGION_CENTER_Y, 
	        		 GraphicsConstants.REGION_TILE_SIZE, 
	        		 GraphicsConstants.REGION_TILE_SIZE, 4);
	    }
	    
	    private void drawProvinceBorders(Graphics2D g2d) {
	    	List<Province> provinces = party.getEmpire().getAllProvinces();
	    	Province selectedProvince = null;
	    	for (Province province: provinces) {
	    		Tile tile = tileMap.getTile(targetingIndexY, targetingIndexX);
	    		if ((tile.hasEvent() && tile.getEvent().getCurrentEvent().getClass().equals(EnterProvince.class) && ((EnterProvince)tile.getEvent().getCurrentEvent()).getProvince().getName().equals(province.getName())) ||
	    			(tile.hasEvent() && tile.getEvent().getCurrentEvent().getClass().equals(EnterBase.class) && ((EnterBase)tile.getEvent().getCurrentEvent()).getProvince().getName().equals(province.getName()))) {
	    			selectedProvince = province;
	    		} else {
	    			g2d.setColor(new Color(0, 0, 0));
	    			drawProvinceBorder(g2d, province);
	    		}
	    		
//	    					//check above
//	    					if (y == 0 || !province.isInArea(y - 1, x)) {
//	    						g2d.drawRect(((x + provOffsetX) * GraphicsConstants.REGION_TILE_SIZE) + offsetX, 
//	    									 ((y + provOffsetY) * GraphicsConstants.REGION_TILE_SIZE) + offsetY , 
//	    									 GraphicsConstants.REGION_TILE_SIZE, 
//	    									 1);
//	    					}
//	    					//check below
//	    					if (y == province.getHeight() - 1 || !province.isInArea(y + 1, x)) {
//	    						g2d.drawRect(((x + provOffsetX) * GraphicsConstants.REGION_TILE_SIZE) + offsetX, 
//	    									 ((y + provOffsetY + 1) * GraphicsConstants.REGION_TILE_SIZE) + offsetY - 2, 
//	    									 GraphicsConstants.REGION_TILE_SIZE, 
//	    									 1);
//	    					}
//	    					//check left
//	    					if (x == 0 || !province.isInArea(y, x - 1)) {
//	    						g2d.drawRect(((x + provOffsetX) * GraphicsConstants.REGION_TILE_SIZE) + offsetX, 
//	    								     ((y + provOffsetY) * GraphicsConstants.REGION_TILE_SIZE) + offsetY, 
//	    								     1, 
//	    								     GraphicsConstants.REGION_TILE_SIZE);
//	    					}
//	    					//check right
//	    					if (x == province.getWidth() -1 || !province.isInArea(y, x + 1)) {
//	    						g2d.drawRect(((x + provOffsetX + 1) * GraphicsConstants.REGION_TILE_SIZE) + offsetX - 2, 
//	    								     ((y + provOffsetY) * GraphicsConstants.REGION_TILE_SIZE) + offsetY, 
//	    								     1, 
//	    								     GraphicsConstants.REGION_TILE_SIZE);
//	    					}

	    		//double for loop through all the area of each province
	    			//if the square is in the province
	    				//check each side of the square
	    					//if the side of the square is not in the province, draw a border
	    		
	    	}
	    	if (selectedProvince != null) {
		    	g2d.setColor(new Color(255, 0, 0));
    			drawProvinceBorder(g2d, selectedProvince);
	    	}
	    }
	    
		private void drawProvinceBorder(Graphics2D g2d, Province province) {
			int provOffsetX = province.getX() - province.getCenter().getX() - targetingIndexX;
    		int provOffsetY = province.getY() - province.getCenter().getY() - targetingIndexY;
	    	int offsetX = this.x + GraphicsConstants.REGION_CENTER_X;
	    	int offsetY = this.y + GraphicsConstants.REGION_CENTER_Y;
    		for (int y = 0; y < province.getHeight(); y++) {
    			for (int x = 0; x < province.getWidth(); x++) {
    				if (province.isInArea(y, x)) {
    					//check above
    					if (y == 0 || !province.isInArea(y - 1, x)) {
    						g2d.drawLine(((x + provOffsetX) * GraphicsConstants.REGION_TILE_SIZE) + offsetX, 
    									 ((y + provOffsetY) * GraphicsConstants.REGION_TILE_SIZE) + offsetY , 
    									 ((x + provOffsetX + 1) * GraphicsConstants.REGION_TILE_SIZE) + offsetX, 
    									 ((y + provOffsetY) * GraphicsConstants.REGION_TILE_SIZE) + offsetY);
    					}
    					//check below
    					if (y == province.getHeight() - 1 || !province.isInArea(y + 1, x)) {
    						g2d.drawLine(((x + provOffsetX) * GraphicsConstants.REGION_TILE_SIZE) + offsetX, 
    									 ((y + provOffsetY + 1) * GraphicsConstants.REGION_TILE_SIZE) + offsetY, 
    									 ((x + provOffsetX + 1) * GraphicsConstants.REGION_TILE_SIZE) + offsetX, 
    									 ((y + provOffsetY + 1) * GraphicsConstants.REGION_TILE_SIZE) + offsetY);
    					}
    					//check left
    					if (x == 0 || !province.isInArea(y, x - 1)) {
    						g2d.drawLine(((x + provOffsetX) * GraphicsConstants.REGION_TILE_SIZE) + offsetX, 
    								     ((y + provOffsetY) * GraphicsConstants.REGION_TILE_SIZE) + offsetY, 
    								     ((x + provOffsetX) * GraphicsConstants.REGION_TILE_SIZE) + offsetX, 
    								     ((y + provOffsetY + 1) * GraphicsConstants.REGION_TILE_SIZE) + offsetY);
    					}
    					//check right
    					if (x == province.getWidth() -1 || !province.isInArea(y, x + 1)) {
    						g2d.drawLine(((x + provOffsetX + 1) * GraphicsConstants.REGION_TILE_SIZE) + offsetX, 
    								     ((y + provOffsetY) * GraphicsConstants.REGION_TILE_SIZE) + offsetY, 
    								     ((x + provOffsetX + 1) * GraphicsConstants.REGION_TILE_SIZE) + offsetX, 
    								     ((y + provOffsetY + 1) * GraphicsConstants.REGION_TILE_SIZE) + offsetY);
    					}		
    				}
    			}
    		}
		}

		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}
		
		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();
			System.out.println(keyCode);
			boolean moved = false;
			String direction = "";
			switch (keyCode) {
				case 87:
				case 38:
					moved = moveUp();
					direction = "up";
					break;
				case 83:
				case 40:
					moved = moveDown();
					direction = "down";
					break;
				case 65:
				case 37:
					moved = moveLeft();
					direction = "left";
					break;
				case 68:
				case 39:
					moved = moveRight();
					direction = "right";
					break;
				case 69:
				case 10:
					activateEvent();
					break;
				case 84:
					openMenu("Standard");
					break;
			}
//			if (moved) {
//				((EmpireMap) manager.getMap()).getParty().getEmpire().incrementTime();
//				walkingAnimation(direction);
//				walkingEvents();
//			} else if (!direction.equals("")) { //if move command but couldnt move
//				avatarImage = avatarStandingImage;
//			}
			checkTileTest();
			this.repaint();
		}
		
		private void walkingAnimation(String direction) {
			animating = true;
			frame.setAcceptingInput(false);
			CountDownLatch cdl = new CountDownLatch(1);
		    Timer timer = new Timer(0, new ActionListener() {
			   
			    int i = 0;
			    
		        @Override
		        public void actionPerformed(ActionEvent e) {
		            if(i < 4) {
		            	switch (direction) {
		            		case "up":
		            			y += GraphicsConstants.REGION_TILE_SIZE/4;
		            			break;
		            		case "down":
		            			y -= GraphicsConstants.REGION_TILE_SIZE/4;
		            			break;
		            		case "left":
		            			x += GraphicsConstants.REGION_TILE_SIZE/4;
		            			break;
		            		case "right":
		            			x -= GraphicsConstants.REGION_TILE_SIZE/4;
		            			break;
		            	}
		            	avatarImage = getAnimationImage(direction, i);
		            	repaint();
		            } else {
		            	if (!frame.hasNextInput()) 
		            		avatarImage = avatarStandingImage;
		                endAnimation(e);
		            }
		            i++;
		        }
		        
		        private void endAnimation(ActionEvent e) {
		        	x = 0;
		        	y = 0;
		        	animating = false;
		    		frame.setAcceptingInput(true);
	                repaint();
	                cdl.countDown();
	                ((Timer)e.getSource()).stop();
		        }
		    });
		    timer.setRepeats(true);
		    timer.setDelay(40);
		    timer.start();
		    try {
				cdl.await();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}

		private boolean moveUp() {
//			if (party.getAvatarIndexY() - 1 >= 0 && tileMap.getTile(party.getAvatarIndexY() - 1, party.getAvatarIndexX()).isAccessable()) {
//				party.setAvatarIndexY(party.getAvatarIndexY() - 1);
//				y -= GraphicsConstants.REGION_TILE_SIZE;
//				return true;
//			}
//			return false;
			if (targetingIndexY - 1 >= 0) {
				targetingIndexY -= 1;
//				y -= GraphicsConstants.REGION_TILE_SIZE;
				return true;
			}
			return false;
		}
		
		private boolean moveDown() {
//			if (party.getAvatarIndexY() + 1 < height && tileMap.getTile(party.getAvatarIndexY() + 1, party.getAvatarIndexX()).isAccessable()) {
//				party.setAvatarIndexY(party.getAvatarIndexY() + 1);
//				y += GraphicsConstants.REGION_TILE_SIZE;
//				return true;
//			}
//			return false;
			if (targetingIndexY + 1 < height) {
				targetingIndexY += 1;
//				y += GraphicsConstants.REGION_TILE_SIZE;
				return true;
			}
			return false;
		}
		
		private boolean moveLeft() {
//			if (party.getAvatarIndexX() - 1 >= 0 && tileMap.getTile(party.getAvatarIndexY(), party.getAvatarIndexX() - 1).isAccessable()) {
//				party.setAvatarIndexX(party.getAvatarIndexX() - 1);
//				x -= GraphicsConstants.REGION_TILE_SIZE;
//				return true;
//			}
//			return false;
			if (targetingIndexX - 1 >= 0) {
				targetingIndexX -= 1;
//				x -= GraphicsConstants.REGION_TILE_SIZE;
				return true;
			}
			return false;
		}
		
		private boolean moveRight() {
//			if (party.getAvatarIndexX() + 1 < width && tileMap.getTile(party.getAvatarIndexY(), party.getAvatarIndexX() + 1).isAccessable()) {
//				party.setAvatarIndexX(party.getAvatarIndexX() + 1);
//				x += GraphicsConstants.REGION_TILE_SIZE;
//				return true;
//			}
//			return false;
			if (targetingIndexX + 1 < width) {
				targetingIndexX += 1;
//				x += GraphicsConstants.REGION_TILE_SIZE;
				return true;
			}
			return false;
		}
		
		public void openMenu(String source) {
			ExploreMenuPanel menuPanel = manager.getMenuPanel();
			menuPanel.setSource(source);
			menuPanel.selectTabItem();
			menuPanel.displayPanel();
			manager.changeDominantPanel(menuPanel);
			frame.refresh();
		}
		
		private void activateEvent() {
			Tile tile = tileMap.getTile(targetingIndexY, targetingIndexX);
			if (tile.hasEvent()) {
				MapEventManager event = tile.getEvent();
				if (((MapEvent) event.getCurrentEvent()).getActivationMethod().equals("ACTIVATION"))
					event.execute(this);
			}
		}

		public void takeCorridor(Corridor corridor) {
			party.getGameStateManager().add(this);
			frame.remove(this);
		    EmpireMap panel = (EmpireMap)party.getGameStateManager().getMap(corridor.getDestination());
		    if (panel == null) {
				panel = new EmpireMap(corridor.getDestination(), frame, manager, party);
		    } else {
			    panel.setManager(manager);
			    panel.setFrame(frame);
			    panel.restoreMap();
		    }
		    if (!bgmName.equals(panel.getBGMName())) {
				stopBGM();
			    panel.startBGM();
		    } else {
		    	panel.setBGM(bgm);
		    }
		    panel.setCoordinates(corridor.getDestIndexY(), corridor.getDestIndexX());
			manager.changeDominantPanel(panel);
			frame.refresh();
		}
		
		public void takeEncounter(Encounter encounter) {
//			frame.remove(this);
//			RegionMap testMap = new RegionMap(corridor.getDestination(), frame, explorer);
//			testMap.loadMap(corridor.getDestination());
//			testMap.setCoordinates(corridor.getDestIndexY(), corridor.getDestIndexX());
//			explorer.changeDominantPanel(testMap);
//			frame.refresh();
			frame.removeAll();
			stopBGM();
			BattlePanelManager battleManager = new BattlePanelManager(manager, frame);
			BattleMap testMap = new BattleMap(encounter.getDestination(), frame, battleManager, party);
			testMap.startBGM();
//			battleManager.changeDominantPanel(testMap);
			frame.refresh();
		}
		
		public void takeSimEncounter(SimEncounter encounter) {
//			frame.removeAll();
//			stopBGM();
//			BattlePanelManager battleManager = new BattlePanelManager(manager, frame);
			SimVictoryRewardsPanel victoryPanel = new SimVictoryRewardsPanel("VictoryRewardsPanel", frame, manager, SimVictoryRewardsPanel.getStandardMenu(), 2);
			SimBattle simBattle = new SimBattle(encounter.getDestination(), frame, party, manager, encounter);
//			BattleMap map = new SimBattle(encounter.getDestination(), frame, battleManager, party);
//			map.startBGM();
			frame.refresh();
			
		}
		
		public void setCoordinates(int avatarIndexY, int avatarIndexX) {
			party.setAvatarIndexY(avatarIndexY);
			party.setAvatarIndexX(avatarIndexX);
			this.targetingIndexX = avatarIndexX;
			this.targetingIndexY = avatarIndexY;
		}
		
		private void checkTileTest() {
			EmpireInfoPanel infoPanel = manager.getInfoPanel();
			Tile tile = tileMap.getTile(targetingIndexY, targetingIndexX);
			String text = "";
			if (tile.hasEvent()) {
				MapEventManager event = tile.getEvent();
				if (event.getCurrentEvent().getClass() == SimEncounter.class)
					text = ((SimEncounter)event.getCurrentEvent()).getDestination();
				else
					text = event.getClass().toString();
				if (event.getCurrentEvent().getClass().equals(EnterProvince.class)) {
					infoPanel.displayProvinceInfo(((EnterProvince)event.getCurrentEvent()).getProvince());
				} else {
					infoPanel.clearProvince();
				}
			} else {
				infoPanel.clearProvince();
			}
			infoPanel.displayText(text);
			
		}
		
		private void walkingEvents() {
			Tile tile = tileMap.getTile(targetingIndexY, targetingIndexX);
			if (tile.hasEvent()) {
				MapEventManager event = tile.getEvent();
				if (((MapEvent) event.getCurrentEvent()).getActivationMethod().equals("WALKING")) {
					event.execute(this);
				}
			}
		}
		
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}
		
		public void restore() {
			frame.add(this);
			frame.setPanel(manager);
		}

		public void setManager(EmpirePanelManager manager) {
			this.manager = manager;
		}
		
//		public RegionPanelManager getManager() {
//			return manager;
//		}
		
		@Override
		public PanelManager getManager() {
			return manager;
		}

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
			BufferedImage image = avatarMoveAnimationImages.getSubimage(x * 32, y * 32, 32, 32);
			return image;
		}

		public void openProvinceMenu(Province province) {
			ProvinceMenuPanel menuPanel = manager.getProvinceMenuPanel();
			menuPanel.setProvince(province);
			menuPanel.displayPanel();
			manager.changeDominantPanel(menuPanel);
		}
	}

