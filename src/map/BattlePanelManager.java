package map;

public class BattlePanelManager extends PanelManager{

	private ExplorePanelManager exploreManager;
	private BattleMenuPanel battleMenuPanel;
	private BattleMap battleMap;
	
	public BattlePanelManager(ExplorePanelManager exploreManager) {
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

	public BattleMap getBattleMap() {
		return battleMap;
	}
	
	public ExplorePanelManager getExplorePanelManager() {
		return exploreManager;
	}
}
