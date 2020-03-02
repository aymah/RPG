package map;

import java.awt.Graphics2D;

public abstract class MapPanel extends GamePanel {

	private GenericMap map;


	
	public void drawMap(Graphics2D g2d, int avatarPosX, int avatarPosY, int centerX, int centerY, int mapHeight, int mapWidth, int animationOffsetX, int animationOffsetY) {
		for (int rowIndex = 0; rowIndex < map.getTileMap().getHeight(); rowIndex++) {
			if (inMapBounds(avatarPosY, rowIndex, mapHeight, centerY)) {
				for (int colIndex = 0; colIndex < map.getTileMap().getWidth(); colIndex++) {
					if (inMapBounds(avatarPosX, colIndex, mapWidth, centerX)) {
						Tile tile = map.getTileMap().getTile(rowIndex, colIndex);
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

	public abstract PanelManager getManager();
	
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
	
	

}
