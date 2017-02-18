package map;

import java.util.List;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.Serializable;


public class TileMap implements Serializable {
	
	private Tile[][] tileMap; //Tile[height][width]
	private int height;
	private int width;
	
	public TileMap(int height, int width) {
		tileMap = new Tile[height][width];
		this.height = height;
		this.width = width;
	}
	
//	public void drawMap(Graphics2D g2d) {
//		for (int rowIndex = 0; rowIndex < height; rowIndex++) {
//			for (int colIndex = 0; colIndex < width; colIndex++) {
//				Tile tile = tileMap[rowIndex][colIndex];
//				tile.draw(g2d, rowIndex, colIndex);
//			}
//		}
//	}
	
	public Tile getTile(int rowIndex, int colIndex) {
		return tileMap[rowIndex][colIndex];
	}
	
	public void setTile(Tile tile, int rowIndex, int colIndex) {
		tileMap[rowIndex][colIndex] = tile;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
}
