package map;

import java.awt.Color;
import java.awt.Image;

public class TileTypes {

	//might eventually be implemented when move to actual graphics?
	//in that we probably don't want hardcoded stuff, instead read a dictionary from a config file
	public static Image tileImage() {
		return null;
	}
	
	public static Color tileColor(String tileId) {
		String precursor = tileId.substring(0, 1);
		int id = Integer.parseInt(tileId.substring(1));
		switch(precursor) {
			case "a":
			case "A":
				return testTileSet(id);
			case "z":
			case "Z":
				return eventTileSet(id);
			default:
				return new Color(255,255,255);
		}
	}
	
	public static Color testTileSet(int id) {
		switch(id) {
			case 0:
				return new Color(196,196,196);
			case 1:
				return new Color(152,152,152);
			case 2:
				return new Color(0,0,255);
			case 3:
				return new Color(0,125,0);
			default:
				return new Color(255,255,255);
		}
	}
	
	public static Color eventTileSet(int id) {
		switch(id) {
			case 0:
				return new Color(255,0,255);
			case 1:
				return new Color(0,255,255);
			default:
				return new Color(255,255,255);
		}
	}
}
