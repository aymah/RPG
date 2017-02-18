package map;

import misc.StartMenuPanel;

public abstract class ExplorePanelManager extends PanelManager {

	public ExplorePanelManager(GameFrame frame) {
		super(frame);
		// TODO Auto-generated constructor stub
	}

	public abstract void setMap(GenericMap map);
	
	public abstract GenericMap getMap();

	public abstract void restore();

	public abstract void setMenuPanel(ExploreMenuPanel exploreMenuPanel);
}
