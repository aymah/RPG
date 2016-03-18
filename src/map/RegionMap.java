package map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComponent;
import javax.swing.JFrame;

import event.Corridor;
import event.Encounter;
import event.Event;

public class RegionMap extends GenericMap{

	private GameFrame frame;
	private ExplorePanelManager manager;
	int avatarIndexX;
	int avatarIndexY;
	
	public RegionMap(String name, GameFrame frame, ExplorePanelManager explorer) {
		frame.add(this);
		explorer.setRegionMap(this);
        loadMap(name);
        this.setBounds(0, 0, GraphicsConstants.REGION_MAP_WIDTH, GraphicsConstants.REGION_MAP_HEIGHT);
		this.name = name;
		this.frame = frame;
		this.manager = explorer;
	}
	
//	public RegionMap(String name, GameFrame frame, JComponent prev) {
//		frame.getContentPane().removeAll();
//		frame.getContentPane().repaint();
//		frame.add(this);
//		this.name = name;
//		this.frame = frame;
//	}

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        Color bgColor = new Color(0,0,0);
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, GraphicsConstants.REGION_MAP_WIDTH, GraphicsConstants.REGION_MAP_HEIGHT);
        this.drawMap(g2d, avatarIndexX, avatarIndexY, GraphicsConstants.REGION_CENTER_X, GraphicsConstants.REGION_CENTER_Y);
        drawAvatar(g2d);
    }
	
    public void drawAvatar(Graphics2D g2d) {
        Color c = new Color(0, 255, 0);
        g2d.setColor(c);
        g2d.fillOval(GraphicsConstants.REGION_CENTER_X, GraphicsConstants.REGION_CENTER_Y, GraphicsConstants.REGION_TILE_SIZE, GraphicsConstants.REGION_TILE_SIZE);
    }
    
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public void keyPressed(KeyEvent e) {
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
			case 37:
				moveLeft();
				break;
			case 68:
			case 39:
				moveRight();
				break;
			case 69:
				toggleEvent();
				break;
			case 84:
				openMenu();
				break;
		}
		checkTileTest();
		this.repaint();
	}
	
	private void moveUp() {
		avatarIndexY--;
		if (avatarIndexY < 0)
			avatarIndexY = 0;
		else if (!tileMap.getTile(avatarIndexY, avatarIndexX).isAccessable())
			avatarIndexY++;
			
	}
	
	private void moveDown() {
		avatarIndexY++;
		if (avatarIndexY > height - 1)
			avatarIndexY = height - 1;
		else if (!tileMap.getTile(avatarIndexY, avatarIndexX).isAccessable())
			avatarIndexY--;
	}
	
	private void moveLeft() {
		avatarIndexX--;
		if (avatarIndexX < 0)
			avatarIndexX = 0;
		else if (!tileMap.getTile(avatarIndexY, avatarIndexX).isAccessable()) 
			avatarIndexX++;
	}
	
	private void moveRight() {
		avatarIndexX++;
		if (avatarIndexX > width - 1)
			avatarIndexX = width - 1;
		else if (!tileMap.getTile(avatarIndexY, avatarIndexX).isAccessable())
			avatarIndexX--;
	}
	
	private void openMenu() {
		RegionMenuPanel menuPanel = manager.getMenuPanel();
		menuPanel.displayPanel();
		manager.changeDominantPanel(menuPanel);
		frame.refresh();
	}
	
	private void toggleEvent() {
		Tile tile = tileMap.getTile(avatarIndexY, avatarIndexX);
		if (tile.hasEvent()) {
			Event event = tile.getEvent();
			event.execute(this);
		}
	}
	
	public void takeCorridor(Corridor corridor) {
		frame.remove(this);
		RegionMap testMap = new RegionMap(corridor.getDestination(), frame, manager);
		testMap.loadMap(corridor.getDestination());
		testMap.setCoordinates(corridor.getDestIndexY(), corridor.getDestIndexX());
		manager.changeDominantPanel(testMap);
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
		BattlePanelManager battleManager = new BattlePanelManager(manager);
		BattleMap testMap = new BattleMap(encounter.getDestination(), frame, battleManager);
		testMap.loadMap(encounter.getDestination());
		battleManager.changeDominantPanel(testMap);
				
		BattleMenuPanel battleMenuPanel = new BattleMenuPanel("BattleMenuPanel", frame, battleManager, BattleMenuPanel.getStandardMenu(), 1);
		frame.setPanel(battleManager);
		frame.repaint();
		frame.refresh();
	}
	
	public void setCoordinates (int avatarIndexY, int avatarIndexX) {
		this.avatarIndexY = avatarIndexY;
		this.avatarIndexX = avatarIndexX;
	}
	
	private void checkTileTest() {
		Tile tile = tileMap.getTile(avatarIndexY, avatarIndexX);
		String text = "";
		if (tile.hasEvent()) {
			Event event = tile.getEvent();
			text = event.getClass().toString();
		}
		InfoPanel infoPanel = manager.getInfoPanel();
		infoPanel.displayText(text);
	}
	
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public void restore() {
		frame.add(this);
		frame.setPanel(manager);
	}
}
