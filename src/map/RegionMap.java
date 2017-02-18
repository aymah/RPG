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
import java.util.concurrent.CountDownLatch;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.Timer;

import unit.Party;
import unit.Unit;
import event.Corridor;
import event.Encounter;
import event.Event;
import event.MapEventManager;
import event.MapEvent;

public class RegionMap extends GenericMap{

	private RegionPanelManager manager;
	private boolean animating;
	private transient BufferedImage avatarStandingImage;
	private transient BufferedImage avatarImage;
	private transient BufferedImage avatarMoveAnimationImages;
	private int y;
	private int x;
	
	public RegionMap(String name, GameFrame frame, RegionPanelManager manager, Party party) {
		frame.add(this);
		manager.setMap(this);
		party.setMapName(name);
        loadMap(name, party);
        this.setBounds(0, 0, GraphicsConstants.REGION_MAP_WIDTH, GraphicsConstants.REGION_MAP_HEIGHT);
		this.name = name;
		this.frame = frame;
		this.manager = manager;
		this.y = 0;
		this.x = 0;
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
	
//	public RegionMap(String name, GameFrame frame, JComponent prev) {
//		frame.getContentPane().removeAll();
//		frame.getContentPane().repaint();
//		frame.add(this);
//		this.name = name;
//		this.frame = frame;
//	}

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        Color bgColor = new Color(0,0,0);
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, GraphicsConstants.REGION_MAP_WIDTH, GraphicsConstants.REGION_MAP_HEIGHT);
        this.drawMap(g2d, party.getAvatarIndexX(), party.getAvatarIndexY(), GraphicsConstants.REGION_CENTER_X, GraphicsConstants.REGION_CENTER_Y,
        			 GraphicsConstants.REGION_MAP_HEIGHT, GraphicsConstants.REGION_MAP_WIDTH, x, y);
        drawAvatar(g2d);
    }
	
    public void drawAvatar(Graphics2D g2d) {
//        Color c = new Color(0, 255, 0);
//        g2d.setColor(c);
//        g2d.fillOval(GraphicsConstants.REGION_CENTER_X, GraphicsConstants.REGION_CENTER_Y, GraphicsConstants.REGION_TILE_SIZE, GraphicsConstants.REGION_TILE_SIZE);
    	int offsetX = (GraphicsConstants.REGION_TILE_SIZE - avatarImage.getWidth(null) * GraphicsConstants.TILE_SIZE_SELECTED)/2;
    	int offsetY = (GraphicsConstants.REGION_TILE_SIZE - avatarImage.getHeight(null) * GraphicsConstants.TILE_SIZE_SELECTED)/2;
    	g2d.drawImage(avatarImage, GraphicsConstants.REGION_CENTER_X + offsetX, GraphicsConstants.REGION_CENTER_Y + offsetY, avatarImage.getWidth(null)*GraphicsConstants.TILE_SIZE_SELECTED, avatarImage.getHeight(null)*GraphicsConstants.TILE_SIZE_SELECTED, null);
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
		if (moved) {
			walkingAnimation(direction);
			walkingEvents();
		} else if (!direction.equals("")) { //if move command but couldnt move
			avatarImage = avatarStandingImage;
		}
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
		if (party.getAvatarIndexY() - 1 >= 0 && tileMap.getTile(party.getAvatarIndexY() - 1, party.getAvatarIndexX()).isAccessable()) {
			party.setAvatarIndexY(party.getAvatarIndexY() - 1);
			y -= GraphicsConstants.REGION_TILE_SIZE;
			return true;
		}
		return false;
	}
	
	private boolean moveDown() {
		if (party.getAvatarIndexY() + 1 < height && tileMap.getTile(party.getAvatarIndexY() + 1, party.getAvatarIndexX()).isAccessable()) {
			party.setAvatarIndexY(party.getAvatarIndexY() + 1);
			y += GraphicsConstants.REGION_TILE_SIZE;
			return true;
		}
		return false;
	}
	
	private boolean moveLeft() {
		if (party.getAvatarIndexX() - 1 >= 0 && tileMap.getTile(party.getAvatarIndexY(), party.getAvatarIndexX() - 1).isAccessable()) {
			party.setAvatarIndexX(party.getAvatarIndexX() - 1);
			x -= GraphicsConstants.REGION_TILE_SIZE;
			return true;
		}
		return false;
	}
	
	private boolean moveRight() {
		if (party.getAvatarIndexX() + 1 < width && tileMap.getTile(party.getAvatarIndexY(), party.getAvatarIndexX() + 1).isAccessable()) {
			party.setAvatarIndexX(party.getAvatarIndexX() + 1);
			x += GraphicsConstants.REGION_TILE_SIZE;
			return true;
		}
		return false;
	}
	
	private void openMenu(String source) {
		ExploreMenuPanel menuPanel = manager.getMenuPanel();
		menuPanel.setSource(source);
		menuPanel.displayPanel();
		manager.changeDominantPanel(menuPanel);
		frame.refresh();
	}
	
	private void activateEvent() {
		Tile tile = tileMap.getTile(party.getAvatarIndexY(), party.getAvatarIndexX());
		if (tile.hasEvent()) {
			MapEventManager event = tile.getEvent();
			if (((MapEvent) event.getCurrentEvent()).getActivationMethod().equals("ACTIVATION"))
				event.execute(this);
		}
	}

	public void takeCorridor(Corridor corridor) {
		party.getGameStateManager().add(this);
		frame.remove(this);
	    RegionMap panel = (RegionMap)party.getGameStateManager().getMap(corridor.getDestination());
	    if (panel == null) {
			panel = new RegionMap(corridor.getDestination(), frame, manager, party);
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
//		frame.remove(this);
//		RegionMap testMap = new RegionMap(corridor.getDestination(), frame, explorer);
//		testMap.loadMap(corridor.getDestination());
//		testMap.setCoordinates(corridor.getDestIndexY(), corridor.getDestIndexX());
//		explorer.changeDominantPanel(testMap);
//		frame.refresh();
		frame.removeAll();
		stopBGM();
		BattlePanelManager battleManager = new BattlePanelManager(manager, frame);
		BattleMap testMap = new BattleMap(encounter.getDestination(), frame, battleManager, party);
		testMap.startBGM();
//		battleManager.changeDominantPanel(testMap);
		frame.refresh();
	}
	
	public void setCoordinates (int avatarIndexY, int avatarIndexX) {
		party.setAvatarIndexY(avatarIndexY);
		party.setAvatarIndexX(avatarIndexX);
	}
	
	private void checkTileTest() {
		Tile tile = tileMap.getTile(party.getAvatarIndexY(), party.getAvatarIndexX());
		String text = "";
		if (tile.hasEvent()) {
			Event event = tile.getEvent();
			text = event.getClass().toString();
		}
		RegionInfoPanel infoPanel = manager.getInfoPanel();
		infoPanel.displayText(text);
	}
	
	private void walkingEvents() {
		Tile tile = tileMap.getTile(party.getAvatarIndexY(), party.getAvatarIndexX());
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

	public void setManager(RegionPanelManager manager) {
		this.manager = manager;
	}
	
//	public RegionPanelManager getManager() {
//		return manager;
//	}
	
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
}
