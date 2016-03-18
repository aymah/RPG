package map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import event.GenericMenuItem;
import event.MenuItem;

//probably want to make a sort of menu items class, it will be similar in some ways to how events work. Might even extend off events.
public class RegionMenuPanel extends MenuPanel {
	
	private int selectorIndexX;
	private int selectorIndexY;
	
	public RegionMenuPanel(String name, GameFrame frame, ExplorePanelManager manager, List<MenuItem> menuItems, int layer) {
		super(menuItems, layer);
		manager.setMenuPanel(this);
		this.manager = manager;
		this.setBounds(100, 100, 600, 400);
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
        g2d.fillRect(0, 0, 600, 400);
        drawMenu(g2d);
    }
    
    public void drawMenu(Graphics2D g2d) {
    	for (int i = 0; i < menuItems.size(); i++) {
            g2d.setColor(new Color(255,255,255));
            if (selectorIndexY == i)
            	g2d.setColor(new Color(255,0,0));
    		g2d.drawString(menuItems.get(i).getName(), 10, 20 + i * 30);
    	}
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
//			case 65:
//			case 37:
//				moveLeft();
//				break;
//			case 68:
//			case 39:
//				moveRight();
//				break;
			case 69:
				selectItem();
				break;
			case 84:
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
	
	private void selectItem() {
		menuItems.get(selectorIndexY).execute(this);
	}

	public static List<MenuItem> getStandardMenu() {
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
		MenuItem item = new GenericMenuItem("test1");
		menuItems.add(item);
		item = new GenericMenuItem("test2");
		menuItems.add(item);
		item = new GenericMenuItem("test3");
		menuItems.add(item);
    	menuItems.add(exitMenuItem());
		return menuItems;
	}
}
