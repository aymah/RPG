package map;

public class GraphicsConstants {

	public static final int FRAME_HEIGHT = 600;
	public static final int FRAME_WIDTH = 800;
	
	public static final int REGION_MAP_HEIGHT = 440;
	public static final int REGION_MAP_WIDTH = FRAME_WIDTH;
	public static final int REGION_TILE_SIZE = 40;
	public static final int REGION_CENTER_Y = (REGION_MAP_HEIGHT - REGION_TILE_SIZE)/2;
	public static final int REGION_CENTER_X = (REGION_MAP_WIDTH - REGION_TILE_SIZE)/2;
	
	public static final int INFO_PANEL_HEIGHT = FRAME_HEIGHT - REGION_MAP_HEIGHT;
	public static final int INFO_PANEL_WIDTH = FRAME_WIDTH;
	
	public static final int BATTLE_MAP_HEIGHT = FRAME_HEIGHT;
	public static final int BATTLE_MAP_WIDTH = FRAME_WIDTH;
	
	public static final int BATTLE_INFO_PANEL_HEIGHT = 160;
	public static final int BATTLE_INFO_PANEL_WIDTH = FRAME_WIDTH;
	public static final int BATTLE_ACTION_MENU_HEIGHT = BATTLE_MAP_HEIGHT - BATTLE_INFO_PANEL_HEIGHT;
	public static final int BATTLE_ACTION_MENU_WIDTH = 120;
	public static final int BATTLE_ACTION_MENU_Y = 0;
	public static final int BATTLE_ACTION_MENU_X = BATTLE_MAP_WIDTH - BATTLE_ACTION_MENU_WIDTH;
}
