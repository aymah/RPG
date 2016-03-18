package map;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JPanel;

import event.Corridor;
import event.Encounter;
import event.Event;


public abstract class GenericMap extends GamePanel{

	TileMap tileMap;
	int height;
	int width;
	
	public void loadMap(String mapName) {
		readMapFile(mapName);
	}
	
	private void readMapFile(String mapName) {
		String fileName = "/Users/andrewmah/Documents/workspace/RPG/src/TestMaps/" + mapName + ".txt";
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			br.mark(50000);
			List<Event> mapEvents = processEvents(br);
			String str = "";
			Scanner scanner;
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
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private List<Event> processEvents(BufferedReader br) throws IOException {
		String str = "";
		Scanner scanner;
		List<Event> mapEvents = new ArrayList<Event>();
		while ((str = br.readLine()) != null) {
			scanner = new Scanner(str);
			if (scanner.hasNext() && scanner.next().equals("EVENT")) {
				mapEvents.add(makeEvent(scanner));
			}
		}
		br.reset();
		return mapEvents;
	}
	
	private Event makeEvent(Scanner scanner) {
		String type = scanner.next();
		if (type.equals("CORRIDOR")) {
			String destination = scanner.next();
			int destIndexY = Integer.parseInt(scanner.next());
			int destIndexX = Integer.parseInt(scanner.next());
			Event event = new Corridor(destination, destIndexY, destIndexX);
			return event;
		} else if (type.equals("ENCOUNTER")) {
			String destination = scanner.next();
			int destIndexY = Integer.parseInt(scanner.next());
			int destIndexX = Integer.parseInt(scanner.next());
			Event event = new Encounter(destination, destIndexY, destIndexX);
			return event;
		}
		return null;
	}
	
	private int fillMapRow(String str, int y, List<Event> mapEvents, int eventCounter) {
		Scanner scanner = new Scanner(str);
		for (int x = 0; x < tileMap.getWidth(); x++) {
			String tileId = scanner.next();
			Tile tile = new Tile(tileId);
			if (!isAccessable(tileId))
				tile.setNotAccessable();
			if (isEvent(tileId)) {
				tile.setEvent(mapEvents.get(eventCounter));
				eventCounter++;
			}
			tileMap.setTile(tile, y, x);
		}
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
	
	public void drawMap(Graphics2D g2d, int avatarPosX, int avatarPosY, int centerX, int centerY) {
		for (int rowIndex = 0; rowIndex < tileMap.getHeight(); rowIndex++) {
			if (inMapBounds(avatarPosY, rowIndex, GraphicsConstants.REGION_MAP_HEIGHT, centerY)) {
				for (int colIndex = 0; colIndex < tileMap.getWidth(); colIndex++) {
					if (inMapBounds(avatarPosX, colIndex, GraphicsConstants.REGION_MAP_WIDTH, centerX)) {
						Tile tile = tileMap.getTile(rowIndex, colIndex);
						tile.draw(g2d, rowIndex, colIndex, avatarPosX, avatarPosY, centerX, centerY);
					}
				}
			}
		}
	}
	
	public void drawMap(Graphics2D g2d) {
		for (int rowIndex = 0; rowIndex < tileMap.getHeight(); rowIndex++) {
			if (inMapBounds(rowIndex, GraphicsConstants.REGION_MAP_HEIGHT)) {
				for (int colIndex = 0; colIndex < tileMap.getWidth(); colIndex++) {
					if (inMapBounds(colIndex, GraphicsConstants.REGION_MAP_WIDTH)) {
						Tile tile = tileMap.getTile(rowIndex, colIndex);
						tile.draw(g2d, rowIndex, colIndex);
					}
				}
			}
		}
	}
	
	private boolean inMapBounds(int avatarIndex, int mapIndex, int bounds, int center) {
		if ((((mapIndex - avatarIndex) * GraphicsConstants.REGION_TILE_SIZE) + center) < bounds &&
			(((mapIndex - avatarIndex) * GraphicsConstants.REGION_TILE_SIZE) + center) > -GraphicsConstants.REGION_TILE_SIZE )
			return true;
		return false;
	}
	
	private boolean inMapBounds(int mapIndex, int bounds) {
		if ((mapIndex * GraphicsConstants.REGION_TILE_SIZE) < bounds &&
			(mapIndex * GraphicsConstants.REGION_TILE_SIZE) > -GraphicsConstants.REGION_TILE_SIZE )
			return true;
		return false;
	}
	
	//for testing purposes
	public void printMapFile() {
		for (int i = 0; i < tileMap.getHeight(); i++) {
			for (int j = 0; j < tileMap.getWidth(); j++) {
				Tile tile = tileMap.getTile(i, j);
				System.out.print(tile.getTileId() + " ");
			}
			System.out.println("");
		}
	}
}
