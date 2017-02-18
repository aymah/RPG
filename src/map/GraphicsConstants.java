package map;

import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.text.AttributedString;

public class GraphicsConstants {

	public static final int FRAME_HEIGHT = 1080;
	public static final int FRAME_WIDTH = 1920;
	
	public static final int TILE_SIZE_VERY_SMALL = 1;
	public static final int TILE_SIZE_SMALL = 2;
	public static final int TILE_SIZE_MEDIUM = 4;
	public static final int TILE_SIZE_LARGE = 6;
	public static final int TILE_SIZE_SELECTED = TILE_SIZE_MEDIUM;

	
	public static final int REGION_MAP_HEIGHT = (int)Math.round((((double)440/(double)600)*FRAME_HEIGHT));
	public static final int REGION_MAP_WIDTH = FRAME_WIDTH;
	public static final int REGION_TILE_SIZE = 16 * TILE_SIZE_SELECTED;
	public static final int REGION_CENTER_Y = (REGION_MAP_HEIGHT - REGION_TILE_SIZE)/2;
	public static final int REGION_CENTER_X = (REGION_MAP_WIDTH - REGION_TILE_SIZE)/2;
	
	public static final int INFO_PANEL_HEIGHT = FRAME_HEIGHT - REGION_MAP_HEIGHT;
	public static final int INFO_PANEL_WIDTH = FRAME_WIDTH;
	
	public static final int BATTLE_MAP_HEIGHT = FRAME_HEIGHT;
	public static final int BATTLE_MAP_WIDTH = FRAME_WIDTH;
	
	public static final int BATTLE_INFO_PANEL_HEIGHT = (int)Math.round((((double)160/(double)600)*FRAME_HEIGHT));
	public static final int BATTLE_INFO_PANEL_WIDTH = FRAME_WIDTH;
	public static final int BATTLE_INFO_PANEL_Y = FRAME_HEIGHT - BATTLE_INFO_PANEL_HEIGHT;
	public static final int BATTLE_INFO_PANEL_X = 0;
	
	public static final int BATTLE_ACTION_MENU_HEIGHT = BATTLE_MAP_HEIGHT - BATTLE_INFO_PANEL_HEIGHT;
	public static final int BATTLE_ACTION_MENU_WIDTH = (int)Math.round((((double)120/(double)800)*FRAME_WIDTH));
	public static final int BATTLE_ACTION_MENU_Y = 0;
	public static final int BATTLE_ACTION_MENU_X = BATTLE_MAP_WIDTH - BATTLE_ACTION_MENU_WIDTH;
	
	public static final int UNIT_INFO_PANEL_HEIGHT = (int)Math.round((((double)440/(double)600)*FRAME_HEIGHT));
	public static final int UNIT_INFO_PANEL_WIDTH = FRAME_WIDTH;
	public static final int UNIT_INFO_PANEL_Y = 0;
	public static final int UNIT_INFO_PANEL_X = 0;	
	
    public static void drawParagraph(Graphics2D g, String paragraph, float width, int startX, int startY) {
        LineBreakMeasurer linebreaker = new LineBreakMeasurer(new AttributedString(paragraph)
            .getIterator(), g.getFontRenderContext());

        float y = 0.0f;
        while (linebreaker.getPosition() < paragraph.length()) {
          TextLayout tl = linebreaker.nextLayout(width);

          y += tl.getAscent();
          tl.draw(g, startX, startY + y);
          y += tl.getDescent() + tl.getLeading();
        }
    }
    
	public static void drawRect(Graphics2D g2d, int x, int y, int width, int height, int thickness) {
		for (int i = 0; i < thickness; i++) {
			g2d.drawRect(x + i, y + i, width - (2 * i), height - (2 * i));
		}
	}
	
	public static void drawOval(Graphics2D g2d, int x, int y, int width, int height, int thickness) {
		for (int i = 0; i < thickness; i++) {
			g2d.drawOval(x + i, y + i, width - (2 * i), height - (2 * i));
		}
	}
	
    public static int[][] colorWhite(BufferedImage image, int colorScaling, int[][] oldColors) {
        int width = image.getWidth();
        int height = image.getHeight();
    	oldColors = new int[height][width];
        for (int xx = 0; xx < width; xx++) {
            for (int yy = 0; yy < height; yy++) {
                int pixel = image.getRGB(xx, yy);
                oldColors[yy][xx] = pixel;
                int alpha = (pixel >> 24) & 0xff;
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;
//                if (alpha != 0xff) {
                	red = 0xff;
                	green = 0xff;
                	blue = 0xff;
//                	pixel = Integer.parseInt(String.valueOf(alpha) + String.valueOf(red) + String.valueOf(green) + String.valueOf(blue));
                	pixel = (pixel & 0xff000000) | 0x00ffffff;
                	image.setRGB(xx, yy, pixel);
//                }
            }
        }
        return oldColors;
    }
    
    public static BufferedImage colorNormal(BufferedImage image, int colorScaling, int[][] oldColors) {
        int width = image.getWidth();
        int height = image.getHeight();
        for (int xx = 0; xx < width; xx++) {
            for (int yy = 0; yy < height; yy++) {
                image.setRGB(xx, yy, oldColors[yy][xx]);
            }
        }
        return image;
    }
    
    public static int[][] colorBlack(BufferedImage image, int colorScaling, int[][] oldColors) {
        int width = image.getWidth();
        int height = image.getHeight();
    	oldColors = new int[height][width];
        for (int xx = 0; xx < width; xx++) {
            for (int yy = 0; yy < height; yy++) {
                int pixel = image.getRGB(xx, yy);
                oldColors[yy][xx] = pixel;
                int alpha = (pixel >> 24) & 0xff;
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;
//                if (alpha != 0xff) {
                	red = 0x00;
                	green = 0x00;
                	blue = 0x00;
//                	pixel = Integer.parseInt(String.valueOf(alpha) + String.valueOf(red) + String.valueOf(green) + String.valueOf(blue));
                	pixel = (pixel & 0xff000000) | 0x00000000;
                	image.setRGB(xx, yy, pixel);
//                }
            }
        }
        return oldColors;
    }
}
