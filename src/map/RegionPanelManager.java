package map;

import java.awt.event.KeyEvent;

public class RegionPanelManager extends ExplorePanelManager {

	private RegionMap regionMap;
	private RegionInfoPanel infoPanel;
	private ExploreMenuPanel menuPanel;
	
	public RegionPanelManager(GameFrame frame) {
		super(frame);
	}
	
//	public void setRegionMap(RegionMap regionMap) {
//		this.regionMap = regionMap;
//	}
//	
	public RegionMap getRegionMap() {
		return regionMap;
	}
	
	public void setInfoPanel(RegionInfoPanel infoPanel) {
		this.infoPanel = infoPanel;
	}
	
	public RegionInfoPanel getInfoPanel() {
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
		dominantPanel = regionMap;
		regionMap.restore();
		infoPanel.restore();
	}

	@Override
	public GenericMap getMap() {
		return regionMap;
	}

	@Override
	public void setMap(GenericMap map) {
		regionMap = (RegionMap) map;
	}
}
