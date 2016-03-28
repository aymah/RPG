package map;

import java.awt.Graphics2D;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.text.AttributedString;

public class GraphicsConstants {

	public static final int FRAME_HEIGHT = 600;
	public static final int FRAME_WIDTH = 800;
	
	public static final int REGION_MAP_HEIGHT = (int)Math.round((((double)440/(double)600)*FRAME_HEIGHT));
	public static final int REGION_MAP_WIDTH = FRAME_WIDTH;
	public static final int REGION_TILE_SIZE = 40;
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
}
