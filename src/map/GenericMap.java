package map;

import java.awt.Graphics2D;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JPanel;

import misc.StartGame;

import org.json.JSONException;
import org.json.JSONObject;

import unit.OrderOfBattle;
import unit.Party;
import unit.Squad;
import unit.Unit;
import unit.UnitFactory;
import event.Corridor;
import event.Encounter;
import event.Event;
import event.MapEventManager;
import event.MapEvent;
import event.Message;
import event.EnterBase;
import event.SimEncounter;
import event.Switch;
import event.Technology;


public abstract class GenericMap extends GamePanel {

	Party party;
	protected List<Coordinates> partyPlacements;
	protected List<Coordinates> armyPlacements;
	protected List<Squad> tempSquadList;
	protected List<Event> eventList;
	transient protected TileSet tileSet;
	TileMap tileMap;
	int height;
	int width;
	int expReward;
	int goldReward;
	String techReward;
	String provReward;
	String bgmName;
	transient Clip bgm;
	
	protected void loadMap(String mapName, Party party) {
		this.party = party;
		partyPlacements = new ArrayList<Coordinates>();
		armyPlacements = new ArrayList<Coordinates>();
		eventList = new ArrayList<Event>();
		tileSet = null;
		readMapFile(mapName);
	}
	
	private void readMapFile(String mapName) {
		try {
			InputStream in = getClass().getResourceAsStream("/testMaps/" + mapName + ".txt"); 
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			br.mark(50000);
			List<MapEventManager> mapEvents = processEvents(br);
			String str = "";
			Scanner scanner = null;
			int counter = 0;
			height = 0;
			width = 0;
			int eventCounter = 0;
			while ((str = br.readLine()) != null) {
				if (counter - 2 >= height) break;
				scanner = new Scanner(str);
				String token = null;
				if (scanner.hasNext())
					token = scanner.next();
				if (token != null && !token.equals("#")) {
					if (counter == 0) 
						height = Integer.parseInt(token);
					else if (counter == 1)
						width = Integer.parseInt(token);
					else if (counter == 2)
						tileMap = new TileMap(height, width);
					if (counter >= 2)
						eventCounter = fillMapRow(str, counter - 2, mapEvents, eventCounter);
					counter++;
				}
				scanner.close();
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private List<MapEventManager> processEvents(BufferedReader br) throws IOException {
		String str = "";
		Scanner scanner;
		List<MapEventManager> mapEvents = new ArrayList<MapEventManager>();
		tempSquadList = new ArrayList<Squad>();
		Squad squad = null;
		while ((str = br.readLine()) != null) {
			scanner = new Scanner(str);
			if (scanner.hasNext()) {
				String next = scanner.next();
				if (next.equals("EVENT")) {
					MapEventManager eventManager = new MapEventManager();
					eventManager.addEvent(makeEvent(scanner));
					mapEvents.add(eventManager);
				}
				if (next.equals("CHAIN")) {
					MapEventManager eventManager = mapEvents.get(mapEvents.size() - 1);
					eventManager.addEvent(makeEvent(scanner));
				}
				if (next.equals("SQUAD")) {
					String squadName = scanner.next();
					String behavior = scanner.next();
					squad = new Squad(squadName, behavior);
					tempSquadList.add(squad);
				}
				if (next.equals("UNIT")) {
					String filename = scanner.next();
					String faction = scanner.next();
					String name = scanner.next();
					int level = Integer.parseInt(scanner.next());
					int y = Integer.parseInt(scanner.next());
					int x = Integer.parseInt(scanner.next());
					UnitFactory unitFactory = new UnitFactory(filename);
					Unit unit = unitFactory.createInstance(faction, name, level);
					unit.setPosition(y, x);
					squad.addUnit(unit);
				}
				if (next.equals("REWARD")) {
					expReward = Integer.parseInt(scanner.next());
					goldReward = Integer.parseInt(scanner.next());
				}
				if (next.equals("TECHREWARD")) {
					techReward = scanner.nextLine();
				}
				if (next.equals("PROVREWARD")) {
					provReward = scanner.nextLine();
				}
				if (next.equals("PARTY")) {
					int y = Integer.parseInt(scanner.next());
					int x = Integer.parseInt(scanner.next());
					partyPlacements.add(new Coordinates(y, x));
					partyPlacements.sort(Coordinates.getComparator());
//					int partyIndex = Integer.parseInt(scanner.next());
//					Unit unit = party.getUnit(partyIndex);
//					if (unit != null) {
//						int y = Integer.parseInt(scanner.next());
//						int x = Integer.parseInt(scanner.next());
//						unit.setPosition(y, x);
//						tempUnitList.add(unit);
//					}
				}
				if (next.equals("ARMY")) {
					int y = Integer.parseInt(scanner.next());
					int x = Integer.parseInt(scanner.next());
					armyPlacements.add(new Coordinates(y, x));
					armyPlacements.sort(Coordinates.getComparator());
//					int armyIndex = Integer.parseInt(scanner.next());
//					List<Unit> unitList = party.getArmy().getUnitListNotInParty();
//					if (armyIndex < unitList.size()) {
//						Unit unit = unitList.get(armyIndex);
//						int y = Integer.parseInt(scanner.next());
//						int x = Integer.parseInt(scanner.next());
//						unit.setPosition(y, x);
//						tempUnitList.add(unit);
//					}
				}
				if (next.equals("BGM")) {
					String filename = scanner.next();
					bgmName = filename;
					loadBGM(filename);
				}
				if (next.equals("TILESET")) {
					tileSet = new TileSet(scanner.next());
				}

			}
		}
		br.reset();
		return mapEvents;
	}
	
	private MapEvent makeEvent(Scanner scanner) {
		String type = scanner.next();
		String switchType = scanner.next();
		Switch s = null;
		if (!switchType.equals("PERMANENT")) {
			String condition = scanner.next();
			if (switchType.equals("CHANGE")) {
				int changeIndex = Integer.parseInt(scanner.next());
				s = new Switch(switchType, condition, changeIndex);
			} else {
				s = new Switch(switchType, condition);
			}
		} else {
			s = new Switch(switchType);
		}
		if (type.equals("CORRIDOR")) {
			String activationMethod = scanner.next();
			String destination = scanner.next();
			int destIndexY = Integer.parseInt(scanner.next());
			int destIndexX = Integer.parseInt(scanner.next());
			MapEvent event = new Corridor(destination, destIndexY, destIndexX, activationMethod, s);
			return event;
		} else if (type.equals("ENCOUNTER")) {
			String activationMethod = scanner.next();
			String destination = scanner.next();
			MapEvent event = new Encounter(destination, activationMethod, s);
			return event;
		} else if (type.equals("SIMENCOUNTER")) {
			String activationMethod = scanner.next();
			String destination = scanner.next();
			MapEvent event = new SimEncounter(destination, activationMethod, s);
			return event;
		} else if (type.equals("MESSAGE")) {
			String activationMethod = scanner.next();
			String text = scanner.nextLine();
			MapEvent event = new Message(text, activationMethod, s);
			return event;
		} else if (type.equals("MAP")) {
			eventList.add(makeEvent(scanner));
		} else if (type.equals("ENTERBASE")) {
			String activationMethod = scanner.next();
			MapEvent event = new EnterBase(activationMethod, s);
			return event;
		}
			
		return null;
	}
	
	private int fillMapRow(String str, int y, List<MapEventManager> mapEvents, int eventCounter) {
		Scanner scanner = new Scanner(str);
		for (int x = 0; x < tileMap.getWidth(); x++) {
			String tileId = scanner.next();
			Tile tile = new Tile(tileId, tileSet);
			if (!isAccessable(tileId))
				tile.setNotAccessable();
			if (isEvent(tileId)) {
				tile.setEvent(mapEvents.get(eventCounter));
				eventCounter++;
			}
			tileMap.setTile(tile, y, x);
		}
		scanner.close();
		return eventCounter;
	}
	
	private boolean isAccessable(String tileId) {
		char firstLetter = tileId.charAt(0);
		if (firstLetter <= 'Z')
			return false;
		return true;
	}
	
	private boolean isEvent(String tileId) {
		char firstLetter = tileId.charAt(0);
		if (firstLetter == 'z' || firstLetter == 'Z')
			return true;
		return false;
	}
	
	public void drawMap(Graphics2D g2d, int avatarPosX, int avatarPosY, int centerX, int centerY, int mapHeight, int mapWidth, int animationOffsetX, int animationOffsetY) {
		for (int rowIndex = 0; rowIndex < tileMap.getHeight(); rowIndex++) {
			if (inMapBounds(avatarPosY, rowIndex, mapHeight, centerY)) {
				for (int colIndex = 0; colIndex < tileMap.getWidth(); colIndex++) {
					if (inMapBounds(avatarPosX, colIndex, mapWidth, centerX)) {
						Tile tile = tileMap.getTile(rowIndex, colIndex);
						tile.draw(g2d, rowIndex, colIndex, avatarPosX, avatarPosY, centerX, centerY, animationOffsetX, animationOffsetY);
					}
				}
			}
		}
	}
	
//	public void drawMap(Graphics2D g2d) {
//		for (int rowIndex = 0; rowIndex < tileMap.getHeight(); rowIndex++) {
//			if (inMapBounds(rowIndex, GraphicsConstants.REGION_MAP_HEIGHT)) {
//				for (int colIndex = 0; colIndex < tileMap.getWidth(); colIndex++) {
//					if (inMapBounds(colIndex, GraphicsConstants.REGION_MAP_WIDTH)) {
//						Tile tile = tileMap.getTile(rowIndex, colIndex);
//						tile.draw(g2d, rowIndex, colIndex);
//					}
//				}
//			}
//		}
//	}
	
	private boolean inMapBounds(int avatarIndex, int mapIndex, int bounds, int center) {
		if ((((mapIndex - avatarIndex - 1) * GraphicsConstants.REGION_TILE_SIZE) + center) <= bounds &&
			(((mapIndex - avatarIndex + 1) * GraphicsConstants.REGION_TILE_SIZE) + center) >= -bounds )
			return true;
		return false;
	}
	
	private boolean inMapBounds(int mapIndex, int bounds) {
		if ((mapIndex * GraphicsConstants.REGION_TILE_SIZE) < bounds &&
			(mapIndex * GraphicsConstants.REGION_TILE_SIZE) > -GraphicsConstants.REGION_TILE_SIZE )
			return true;
		return false;
	}
	
//	//for testing purposes
//	public void printMapFile() {
//		for (int i = 0; i < tileMap.getHeight(); i++) {
//			for (int j = 0; j < tileMap.getWidth(); j++) {
//				Tile tile = tileMap.getTile(i, j);
//				System.out.print(tile.getTileId() + " ");
//			}
//			System.out.println("");
//		}
//	}

	public Party getParty() {
		return party;
	}
	
	public void startBGM() {
		if (bgm == null)
			loadBGM(bgmName);
		bgm.setFramePosition(0);
		bgm.loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	private void loadBGM(String filename) {
		try {
	    	InputStream in = StartGame.class.getResourceAsStream("/testSounds/" + filename + ".wav"); 
	    	BufferedInputStream bin = new BufferedInputStream(in);
	    	AudioInputStream audioIn = AudioSystem.getAudioInputStream(bin);
	    	bgm = AudioSystem.getClip();
	    	bgm.open(audioIn);
	        FloatControl gainControl = (FloatControl) bgm.getControl(FloatControl.Type.MASTER_GAIN);
	        gainControl.setValue(-20.0f);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void stopBGM() {
    	if (bgm.isRunning()) bgm.stop();
	}


	public String getBGMName() {
		return bgmName;
	}
	
	public void setBGM(Clip bgm) {
		this.bgm = bgm;
	}
	
	protected void executeEvents() {
		for (Event event: eventList) {
			event.execute(this);
		}
	}
	
	public abstract PanelManager getManager();
}
