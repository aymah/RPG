package map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import event.Event;
import event.MapEventManager;
import event.MapEvent;

public class Tile implements Serializable {
	
	private int tileSize;
	private String tileId;
	private boolean accessable;
	private MapEventManager event;
	private transient BufferedImage image;
	
	public Tile(String tileId, TileSet tileSet) {
		this.tileId = tileId;
		tileSize = GraphicsConstants.REGION_TILE_SIZE;
		accessable = true;
		event = null;
		int x = Integer.parseInt(tileId.substring(1,3));
		int y = Integer.parseInt(tileId.substring(4,6));
		image = tileSet.getTileImage(y, x);
	}
	
	public void draw(Graphics2D g2d, int rowIndex, int colIndex, int avatarIndexX, int avatarIndexY, int centerX, int centerY, int animationOffsetX, int animationOffsetY) {
//		Color c = TileTypes.tileColor(tileId);
//		g2d.setColor(c);
//		g2d.fillRect(((colIndex - avatarIndexX) * tileSize) + centerX,
//					 ((rowIndex - avatarIndexY) * tileSize) + centerY, tileSize, tileSize);
		g2d.drawImage(image, ((colIndex - avatarIndexX) * tileSize) + centerX + animationOffsetX,
					((rowIndex - avatarIndexY) * tileSize) + centerY + animationOffsetY, tileSize, tileSize, null);
	}
	
//	public void draw(Graphics2D g2d, int rowIndex, int colIndex) {
//		Color c = TileTypes.tileColor(tileId);
//		g2d.setColor(c);
//		g2d.fillRect(colIndex * tileSize, rowIndex * tileSize, tileSize, tileSize);
//	}
	
//	public String getTileId() {
//		return tileId;
//	}
	
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
	
	public MapEventManager getEvent() {
		return event;
	}
	
	public void setEvent(MapEventManager event) {
		this.event = event;
	}
}
