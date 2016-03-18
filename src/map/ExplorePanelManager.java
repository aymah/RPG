package map;

import java.awt.event.KeyEvent;

public class ExplorePanelManager extends PanelManager {

	private RegionMap regionMap;
	private InfoPanel infoPanel;
	private RegionMenuPanel menuPanel;
	
	public ExplorePanelManager(GameFrame frame) {
		frame.setPanel(this);
	}
	
	public void setRegionMap(RegionMap regionMap) {
		this.regionMap = regionMap;
	}
	
	public RegionMap getRegionMap() {
		return regionMap;
	}
	
	public void setInfoPanel(InfoPanel infoPanel) {
		this.infoPanel = infoPanel;
	}
	
	public InfoPanel getInfoPanel() {
		return infoPanel;
	}
	
	public void setMenuPanel(RegionMenuPanel menuPanel) {
		this.menuPanel = menuPanel;
	}
	
	public RegionMenuPanel getMenuPanel() {
		return menuPanel;
	}
	
	public void restore() {
		dominantPanel = regionMap;
		regionMap.restore();
	}
}
