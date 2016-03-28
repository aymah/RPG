package map;

import java.awt.event.KeyEvent;

public class RegionPanelManager extends PanelManager {

	private RegionMap regionMap;
	private RegionInfoPanel infoPanel;
	private RegionMenuPanel menuPanel;
	
	public RegionPanelManager(GameFrame frame) {
		super(frame);
	}
	
	public void setRegionMap(RegionMap regionMap) {
		this.regionMap = regionMap;
	}
	
	public RegionMap getRegionMap() {
		return regionMap;
	}
	
	public void setInfoPanel(RegionInfoPanel infoPanel) {
		this.infoPanel = infoPanel;
	}
	
	public RegionInfoPanel getInfoPanel() {
		return infoPanel;
	}
	
	public void setMenuPanel(RegionMenuPanel menuPanel) {
		this.menuPanel = menuPanel;
	}
	
	public RegionMenuPanel getMenuPanel() {
		return menuPanel;
	}
	
	public void restore() {
		System.out.println("restore");
		dominantPanel = regionMap;
		regionMap.restore();
		infoPanel.restore();
	}
}
