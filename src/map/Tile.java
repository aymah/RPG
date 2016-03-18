package map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import event.Event;

public class Tile {
	
	private TileSet tileSet;
	private int tileIndex;
	private int tileSize;
	private String tileId;
	private boolean accessable;
	private Event event;
	
	public Tile(String tileId) {
		this.tileId = tileId;
		tileSize = GraphicsConstants.REGION_TILE_SIZE; //random default number, change to a constant later
		accessable = true;
		event = null;
	}
	
	public void draw(Graphics2D g2d, int rowIndex, int colIndex, int avatarIndexX, int avatarIndexY, int centerX, int centerY) {
		Color c = TileTypes.tileColor(tileId);
		g2d.setColor(c);
		g2d.fillRect(((colIndex - avatarIndexX) * tileSize) + centerX,
					 ((rowIndex - avatarIndexY) * tileSize) + centerY, tileSize, tileSize);
	}
	
	public void draw(Graphics2D g2d, int rowIndex, int colIndex) {
		Color c = TileTypes.tileColor(tileId);
		g2d.setColor(c);
		g2d.fillRect(colIndex * tileSize, rowIndex * tileSize, tileSize, tileSize);
	}
	
	public String getTileId() {
		return tileId;
	}
	
	public boolean isAccessable() {
		return accessable;
	}
	
	public void setNotAccessable() {
		accessable = false;
	}
	
	public boolean hasEvent() {
		if (event == null)
			return false;
		return true;
	}
	
	public Event getEvent() {
		return event;
	}
	
	public void setEvent(Event event) {
		this.event = event;
	}
}
