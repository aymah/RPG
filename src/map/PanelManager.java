package map;

import java.awt.event.KeyEvent;
import java.util.Stack;

public class PanelManager extends GamePanel {

	protected GamePanel dominantPanel;
	protected Stack<GamePanel> panelStack;
	protected LayeredPanel auxillaryPanel;
	
	
	public PanelManager(GameFrame frame) {
		this.frame = frame;
		frame.setPanel(this);
		panelStack = new Stack<GamePanel>();
	}
	
	public GamePanel getDominantPanel() {
		return dominantPanel;
	}
	
//	public GamePanel getLastPanel() {
//		return panelStack.peek();
//	}
//	
	@Override
	public void keyPressed(KeyEvent e) {
		dominantPanel.keyPressed(e);
	}

	public void setDominantPanel(GamePanel gamePanel) {
		dominantPanel = gamePanel;
	}
	
	public void changeDominantPanel(GamePanel gamePanel) {
		panelStack.add(dominantPanel);
		dominantPanel = gamePanel;
	}
	
	public void changeDominantPanelToPrevious() {
		dominantPanel = panelStack.pop();
	}
	
	public void setAuxillaryPanel(LayeredPanel panel) {
		this.auxillaryPanel = panel;
	}
}
