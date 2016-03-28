package map;

import java.util.concurrent.CountDownLatch;

public class BattlePanelManager extends PanelManager{

	private RegionPanelManager exploreManager;
	private BattleMenuPanel battleMenuPanel;
	private BattleMap battleMap;
	private BattleInfoPanel infoPanel;
	private VictoryRewardsPanel victoryPanel;
	
	public BattlePanelManager(RegionPanelManager exploreManager, GameFrame frame) {
		super(frame);
		this.exploreManager = exploreManager;
	}
	
	public void setBattleMap(BattleMap battleMap) {
		this.battleMap = battleMap;
	}
	
	public void setMenuPanel(MenuPanel menuPanel) {
		this.battleMenuPanel = (BattleMenuPanel) menuPanel;
	}
	
	public BattleMenuPanel getMenuPanel() {
		return battleMenuPanel;
	}
	
	public BattleMenuPanel getCurrentMenuPanel() {
		BattleMenuPanel bmp = battleMenuPanel;
		while (bmp.subMenu != null) {
			bmp = (BattleMenuPanel) bmp.subMenu;
		}
		return bmp;
	}

	public BattleMap getBattleMap() {
		return battleMap;
	}
	
	public RegionPanelManager getExplorePanelManager() {
		return exploreManager;
	}
	
	public BattleInfoPanel getInfoPanel() {
		return infoPanel;
	}
	
	public void setInfoPanel(BattleInfoPanel infoPanel) {
		this.infoPanel = infoPanel;
	}

	public VictoryRewardsPanel getVictoryPanel() {
		return victoryPanel;
	}

	public void setVictoryPanel(VictoryRewardsPanel victoryPanel) {
		this.victoryPanel = victoryPanel;
	}
}
