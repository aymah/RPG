package misc;

import map.GameFrame;
import map.PanelManager;

public class StartPanelManager extends PanelManager {

	private StartMenuPanel menuPanel;
	
	public StartPanelManager(GameFrame frame) {
		super(frame);
	}
	
	public void setMenuPanel(StartMenuPanel menuPanel) {
		this.menuPanel = menuPanel;
	}
}
