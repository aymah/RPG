package map;

import java.awt.event.KeyEvent;

public class PanelManager extends GamePanel{

	protected GamePanel dominantPanel;
	protected GamePanel lastPanel;
	
	public PanelManager(GameFrame frame) {
		this.frame = frame;
		frame.setPanel(this);
	}
	
	public GamePanel getDominantPanel() {
		return dominantPanel;
	}
	
	public GamePanel getLastPanel() {
		return lastPanel;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		dominantPanel.keyPressed(e);
	}

	public void setDominantPanel(GamePanel gamePanel) {
		dominantPanel = gamePanel;
	}
	
	public void changeDominantPanel(GamePanel gamePanel) {
		lastPanel = dominantPanel;
		dominantPanel = gamePanel;
	}
}
