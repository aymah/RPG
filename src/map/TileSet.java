package map;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class TileSet {

	transient BufferedImage tileSet;
	
	
	public TileSet(String filename) {
		try {
			tileSet = ImageIO.read(getClass().getResourceAsStream("/testMaps/tileSets/" + filename + ".png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public BufferedImage getTileImage(int y, int x) {
		BufferedImage image = tileSet.getSubimage(x * 16, y * 16, 16, 16);
		return image;
	}
}
