package map;

import java.awt.event.KeyEvent;

import javax.sound.sampled.Clip;

import event.Corridor;
import event.Encounter;
import event.EnterProvince;
import event.MapEvent;
import event.MapEventManager;
import event.Province;
import event.SimEncounter;
import unit.Party;

public class EmpireController {

	private EmpireMap map;
	private EmpirePanel panel;
	
	public EmpireController (GameFrame frame) {
		panel = new EmpirePanel(frame);

	}
	
	public void loadMap(String mapName, Party party) {
		map = new EmpireMap(mapName, party);
		panel.setMap(map);
	}
	
	public EmpirePanel getPanel() {
		return panel;
	}
		
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		System.out.println(keyCode);
		boolean moved = false;
		String direction = "";
		switch (keyCode) {
			case 87:
			case 38:
				moved = map.up();
				direction = "up";
				break;
			case 83:
			case 40:
				moved = map.down();
				direction = "down";
				break;
			case 65:
			case 37:
				moved = map.left();
				direction = "left";
				break;
			case 68:
			case 39:
				moved = map.right();
				direction = "right";
				break;
			case 69:
			case 10:
				activateEvent();
				break;
			case 84:
				openMenu("Standard");
				break;
		}
//		if (moved) {
//			((EmpireMap) manager.getMap()).getParty().getEmpire().incrementTime();
//			walkingAnimation(direction);
//			walkingEvents();
//		} else if (!direction.equals("")) { //if move command but couldnt move
//			avatarImage = avatarStandingImage;
//		}
		checkTileTest();
		panel.repaint();
	}
	
	public void openMenu(String source) {
		EmpirePanelManager manager = (EmpirePanelManager) panel.getManager();
		ExploreMenuPanel menuPanel = manager.getMenuPanel();
		menuPanel.setSource(source);
		menuPanel.selectTabItem();
		menuPanel.displayPanel();
		manager.changeDominantPanel(menuPanel);
	}
	
	public void activateEvent() {
		TileMap tileMap = map.getTileMap();
		Tile tile = tileMap.getTile(map.getTargetingIndexY(), map.getTargetingIndexX());
		if (tile.hasEvent()) {
			MapEventManager event = tile.getEvent();
			if (((MapEvent) event.getCurrentEvent()).getActivationMethod().equals("ACTIVATION"))
				event.execute(panel);
		}
	}
	
	public void checkTileTest() {
		EmpirePanelManager manager = (EmpirePanelManager) panel.getManager();
		TileMap tileMap = map.getTileMap();
		EmpireInfoPanel infoPanel = manager.getInfoPanel();
		Tile tile = tileMap.getTile(map.getTargetingIndexY(), map.getTargetingIndexX());
		String text = "";
		if (tile.hasEvent()) {
			MapEventManager event = tile.getEvent();
			if (event.getCurrentEvent().getClass() == SimEncounter.class)
				text = ((SimEncounter)event.getCurrentEvent()).getDestination();
			else
				text = event.getClass().toString();
			if (event.getCurrentEvent().getClass().equals(EnterProvince.class)) {
				infoPanel.displayProvinceInfo(((EnterProvince)event.getCurrentEvent()).getProvince());
			} else {
				infoPanel.clearProvince();
			}
		} else {
			infoPanel.clearProvince();
		}
		infoPanel.displayText(text);
	}
	
//	public void takeCorridor(Corridor corridor) {
//		Party party = map.getParty();
//		party.getGameStateManager().add(map);
//		panel.removePanel();
//	    EmpireMap map = (EmpireMap)party.getGameStateManager().getMap(corridor.getDestination());
//	    if (map == null) {
//			map = new EmpireMap(corridor.getDestination(), frame, manager, party);
//	    } else {
//		    map.setManager(manager);
//		    map.setFrame(frame);
//		    map.restoreMap();
//	    }
//	    
//	    if (!bgmName.equals(map.getBGMName())) {
//			stopBGM();
//		    map.startBGM();
//	    } else {
//	    	map.setBGM(bgm);
//	    }
//	    map.setCoordinates(corridor.getDestIndexY(), corridor.getDestIndexX());
//		manager.changeDominantPanel(map);
//		frame.refresh();
//	}
	
//	public void takeEncounter(Encounter encounter) {
////		frame.remove(this);
////		RegionMap testMap = new RegionMap(corridor.getDestination(), frame, explorer);
////		testMap.loadMap(corridor.getDestination());
////		testMap.setCoordinates(corridor.getDestIndexY(), corridor.getDestIndexX());
////		explorer.changeDominantPanel(testMap);
////		frame.refresh();
//		frame.removeAll();
//		stopBGM();
//		BattlePanelManager battleManager = new BattlePanelManager(manager, frame);
//		BattleMap testMap = new BattleMap(encounter.getDestination(), frame, battleManager, party);
//		testMap.startBGM();
////		battleManager.changeDominantPanel(testMap);
//		frame.refresh();
//	}
//	
//	public void takeSimEncounter(SimEncounter encounter) {
////		frame.removeAll();
////		stopBGM();
////		BattlePanelManager battleManager = new BattlePanelManager(manager, frame);
//		SimVictoryRewardsPanel victoryPanel = new SimVictoryRewardsPanel("VictoryRewardsPanel", frame, manager, SimVictoryRewardsPanel.getStandardMenu(), 2);
//		SimBattle simBattle = new SimBattle(encounter.getDestination(), frame, party, manager, encounter);
////		BattleMap map = new SimBattle(encounter.getDestination(), frame, battleManager, party);
////		map.startBGM();
//		frame.refresh();
//		
//	}
	
	public void startBGM() {
		Clip bgm = map.getBGM();
		if (bgm == null)
			map.loadBGM(map.getBGMName());
		bgm.setFramePosition(0);
		bgm.loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	public void stopBGM() {
		Clip bgm = map.getBGM();
    	if (bgm.isRunning()) bgm.stop();
	}

	public void setStartingLocation(int y, int x) {
		map.setCoordinates(y, x);
	}
	
	public void openProvinceMenu(Province province) {
		EmpirePanelManager manager = (EmpirePanelManager) panel.getManager();
		ProvinceMenuPanel menuPanel = manager.getProvinceMenuPanel();
		menuPanel.setProvince(province);
		menuPanel.displayPanel();
		manager.changeDominantPanel(menuPanel);
	}
}
