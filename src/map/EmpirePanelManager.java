package map;

import java.awt.event.KeyEvent;

public class EmpirePanelManager extends ExplorePanelManager {

	private EmpireMap empireMap;
	private EmpireInfoPanel infoPanel;
	private ExploreMenuPanel menuPanel;
	private SimVictoryRewardsPanel simVictoryPanel;
	
	public EmpirePanelManager(GameFrame frame) {
		super(frame);
	}
	
//	public void setEmpireMap(EmpireMap empireMap) {
//		this.empireMap = empireMap;
//	}
//	
//	public GenericMap getExploreMap() {
//		return empireMap;
//	}
	
	public void setInfoPanel(EmpireInfoPanel infoPanel) {
		this.infoPanel = infoPanel;
	}
	
	public EmpireInfoPanel getInfoPanel() {
		return infoPanel;
	}
	
	public void setMenuPanel(ExploreMenuPanel menuPanel) {
		this.menuPanel = menuPanel;
	}
	
	public ExploreMenuPanel getMenuPanel() {
		return menuPanel;
	}
	
	public void restore() {
		System.out.println("restore");
		dominantPanel = empireMap;
		empireMap.restore();
		infoPanel.restore();
	}

	@Override
	public GenericMap getMap() {
		return empireMap;
	}

	@Override
	public void setMap(GenericMap map) {
		empireMap = (EmpireMap) map;
	}

	public void setSimVictoryPanel(SimVictoryRewardsPanel simVictoryRewardsPanel) {
		this.simVictoryPanel = simVictoryRewardsPanel;
	}

	public SimVictoryRewardsPanel getSimVictoryPanel() {
		return simVictoryPanel;
	}
}
