package mapEditor;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import unit.Party;
import map.GameFrame;
import map.GenericMap;
import map.GraphicsConstants;
import map.PanelManager;

public class EditorMap extends GenericMap {

	private int screenIndexX = 0;
	private int screenIndexY = 0;
	PanelManager manager;
	JScrollPane mapPanel;
	
	public EditorMap(String mapName, GameFrame frame, EditorPanelManager manager, JScrollPane mapPanel) {
		this.mapPanel = mapPanel;
		this.mapPanel.add(this);
//		frame.add(this);
		Party party = new Party(); //this actually doesnt do anything, you can ignore it
		loadMap(mapName, party); //loads the map from file, pretty self explanatory
		this.manager = manager;
//		this.setBounds(0, 0, EditorConstants.FRAME_WIDTH, EditorConstants.FRAME_HEIGHT);
		this.name = name;
		this.frame = frame;	
	}
	
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        drawMap(g2d);
    }
	
	private void drawMap(Graphics2D g2d) {
		drawMap(g2d, screenIndexX, screenIndexY, 0, 0, EditorConstants.MAP_HEIGHT, EditorConstants.MAP_WIDTH, 0, 0);
	}

	
	/**
	 * Might need a panel editor manager roflmao
	 */
	@Override
	public PanelManager getManager() {
		// TODO Auto-generated method stub
		System.out.println("does this get called?");
		return null;
	}
	
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
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
		}
		this.repaint();
	}
	
	private void moveUp() {
		screenIndexY--;
	}
	
	private void moveDown() {
		screenIndexY++;
	}
	
	private void moveLeft() {
		screenIndexX--;
	}
	
	private void moveRight() {
		screenIndexX++;
	}	

}
