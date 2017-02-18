package map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.List;

import event.Ability;
import event.Effect;
import event.MenuItem;
import unit.Squad;
import unit.Unit;

public class BattleInfoPanel extends InfoPanel {

	private BattlePanelManager manager;
	private String text = "";
	
	public BattleInfoPanel(String name, GameFrame frame, BattlePanelManager manager) {
		frame.add(this, new Integer(1), 0);
		manager.setInfoPanel(this);
		this.manager = manager;
		this.setBounds(GraphicsConstants.BATTLE_INFO_PANEL_X, GraphicsConstants.BATTLE_INFO_PANEL_Y, GraphicsConstants.BATTLE_INFO_PANEL_WIDTH, GraphicsConstants.BATTLE_INFO_PANEL_HEIGHT);
		this.name = name;
		this.frame = frame;
	}

	
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        Color bgColor = new Color(0,100,100);
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, GraphicsConstants.BATTLE_INFO_PANEL_WIDTH, GraphicsConstants.BATTLE_INFO_PANEL_HEIGHT);
        g2d.setColor(new Color(255,255,255));
        displayAbilityInfo(g2d);
        displayUnitInfo(g2d);
        if (manager.getBattleMap().isStarted())
        	displayPhantomQueue(g2d);
        if (manager.getBattleMap().isTargetingMode() || manager.getBattleMap().isFreeSelectMode()) {
        	displayTargetInfo(g2d);
        } else {
        	drawHPBars(g2d, manager.getBattleMap().getCurrUnit());
        }
//        manager.getInfoPanel().repaint();
    }
    
    //TODO: clean this up, its a mess esp the colors
    private void displayUnitInfo(Graphics2D g2d) {
	    Unit unit = manager.getBattleMap().getCurrUnit();
	    if (unit.getFaction().equals("ALLY"))
		    g2d.setColor(new Color(0,255,255));
	    else
		    g2d.setColor(new Color(255,0,0));
	    int pixelY = 42;
	    g2d.drawString("HP:" + String.valueOf(unit.getCurrHP()) + "/" + String.valueOf(unit.getHP()), 10, pixelY);
	    pixelY += 20;
	    if (unit.getMP() > 0) {
	    	g2d.drawString("MP:" + String.valueOf(unit.getCurrMP()) + "/" + String.valueOf(unit.getMP()), 10, pixelY);
	    pixelY += 20;	
	    }
	    if (unit.hasPotentialStats() && unit.getPotentialStamina() > unit.getCurrStamina()) {
		    g2d.setColor(new Color(0,255,0));
		    g2d.drawString("SP:" + String.valueOf(unit.getPotentialStamina()) + "/" + String.valueOf(unit.getStamina()), 10, pixelY);
	    } else {
		    g2d.drawString("SP:" + String.valueOf(unit.getCurrStamina()) + "/" + String.valueOf(unit.getStamina()), 10, pixelY);
	    }
	    pixelY += 20;	
	    if (unit.getFaction().equals("ALLY"))
		    g2d.setColor(new Color(0,255,255));
	    else
		    g2d.setColor(new Color(255,0,0));
	    g2d.drawString("MV:" + String.valueOf(unit.getCurrMovement()) + "/" + String.valueOf(unit.getMovement()), 10, pixelY);
	    pixelY += 20;	
	    String status = "NORMAL";
	    //if has burning effect, add burning
	    if (Effect.getEffects(unit.getAllEffects(), "Status", "Crippled").size() > 0) {
	    	status = "CRIPPLED";
	    }
	    if (Effect.getEffects(unit.getAllEffects(), "Status", "Burning").size() > 0) {
	    	status = "BURNING";
	    }
	    if (Effect.getEffects(unit.getAllEffects(), "Status", "Stunned").size() > 0) {
	    	status = "STUNNED";
	    }
	    g2d.drawString("ST:" + status.toUpperCase(), 10, pixelY);
    }
   
    private void displayPhantomQueue(Graphics2D g2d) {
	    Squad currSquad = manager.getBattleMap().getCurrSquad();
	    List<Squad> squadList = manager.getBattleMap().getOrderOfBattle().getPhantomList();
	    for (int i = 0; i < squadList.size(); i++) {
		    Squad squad = squadList.get(i);
		    g2d.setColor(new Color(0,0,0));
	        g2d.fillRect(i * (GraphicsConstants.REGION_TILE_SIZE/4 * 3), 137, GraphicsConstants.REGION_TILE_SIZE/4 * 3, GraphicsConstants.REGION_TILE_SIZE/4 * 3);
		    if (squad.getFaction().equals("ALLY")) {
			    g2d.setColor(new Color(0,255,255));
		    } else {
		 	    g2d.setColor(new Color(255,0,0));
		    }
		    String length = squad.getName();
		    g2d.fillOval(i * (GraphicsConstants.REGION_TILE_SIZE/4 * 3), 137, (GraphicsConstants.REGION_TILE_SIZE/4 * 3), (GraphicsConstants.REGION_TILE_SIZE/4 * 3));
		    g2d.setColor(new Color(0,0,0));
		    g2d.drawString(squad.getName(), i * (GraphicsConstants.REGION_TILE_SIZE/4 * 3) + 24 - (4 * length.length()), 164);
		    
//		    g2d.drawString(String.valueOf(unit.getOrdering()), i * (GraphicsConstants.REGION_TILE_SIZE/3 *2), 132);

		    if (currSquad.equals(squad)) {
			    g2d.setColor(new Color(255,255,0));
			    GraphicsConstants.drawOval(g2d, i * (GraphicsConstants.REGION_TILE_SIZE/4 * 3), 137, 
			    						  (GraphicsConstants.REGION_TILE_SIZE/4 * 3), (GraphicsConstants.REGION_TILE_SIZE/4 * 3), 3);
		    }
	    }
    }
   
    private void displayTargetInfo(Graphics2D g2d) {
	    BattleMap battleMap = manager.getBattleMap();
	    int y = battleMap.getTargetingIndexY();
	    int x = battleMap.getTargetingIndexX();
	    Unit unit = battleMap.getOrderOfBattle().getUnit(y, x);
	    if (unit != null && unit.getFaction().equals("ALLY"))
		    g2d.setColor(new Color(0,255,255));
	    else
		    g2d.setColor(new Color(255,0,0));
	    if (unit != null) {
	    	int pixelY = 42;
		    g2d.drawString("HP:" + String.valueOf(unit.getCurrHP()) + "/" + String.valueOf(unit.getHP()), 100, pixelY);
		    pixelY += 20;
		    if (unit.getMP() > 0) {
			    g2d.drawString("MP:" + String.valueOf(unit.getCurrMP()) + "/" + String.valueOf(unit.getMP()), 100, pixelY);
		    	pixelY += 20;
		    }
		    g2d.drawString("SP:" + String.valueOf(unit.getCurrStamina()) + "/" + String.valueOf(unit.getStamina()), 100, pixelY);
		    pixelY += 20;
		    g2d.drawString("MV:" + String.valueOf(unit.getCurrMovement()) + "/" + String.valueOf(unit.getMovement()), 100, pixelY);
		    pixelY += 20;	
		    String status = "NORMAL";
		    //if has burning effect, add burning
		    if (Effect.getEffects(unit.getAllEffects(), "Status", "Crippled").size() > 0) {
		    	status = "CRIPPLED";
		    }
		    if (Effect.getEffects(unit.getAllEffects(), "Status", "Burning").size() > 0) {
		    	status = "BURNING";
		    }
		    if (Effect.getEffects(unit.getAllEffects(), "Status", "Stunned").size() > 0) {
		    	status = "STUNNED";
		    }
		    g2d.drawString("ST:" + status.toUpperCase(), 100, pixelY);
        	drawHPBars(g2d, unit);
	    }
    }
   
    private void displayAbilityInfo(Graphics2D g2d) {
	    g2d.setColor(new Color(255,255,255));
   	    BattleMenuPanel menuPanel = manager.getCurrentMenuPanel();
	    MenuItem menuItem = menuPanel.getMenuItem();
	    Unit unit = manager.getBattleMap().getCurrUnit();
	    if (menuItem.getClass() == Ability.class && unit.hasPotentialStats()) {
		    Ability ability = (Ability)menuItem;
		    if (ability.getStamCost(unit) > unit.getPotentialStamina() || ability.getMPCost(unit) > unit.getPotentialMP())
		   	    g2d.setColor(new Color(190, 190, 190));
		    int pixelY = 42;
		    int damage = ability.calculatePotentialDamage(unit);
		    BattleMap battleMap = manager.getBattleMap();
		    int y = battleMap.getTargetingIndexY();
		    int x = battleMap.getTargetingIndexX();
		    Unit target = battleMap.getOrderOfBattle().getUnit(y, x);
		    int adjustedDamage = damage;
		    if (target != null)
		    	adjustedDamage = ability.calculateDamage(unit, target);
		    if (damage > 0) {
				g2d.drawString("DMG:" + adjustedDamage, 200, pixelY);
				pixelY += 20;
		    } else if (damage < 0){
				g2d.drawString("+HP:" + Math.abs(adjustedDamage), 200, pixelY);
				pixelY += 20;
		    } else {
		    	g2d.drawString("+SP:" + Math.abs(ability.calculateAddedStamina(unit)), 200, pixelY);
				pixelY += 20;
		    }
			if (ability.getStamCost(unit) > 0) {
				g2d.drawString("SPC:" + ability.getStamCost(unit), 200, pixelY);
				pixelY += 20;
			}
			if (ability.getMPCost(unit) > 0) {
				g2d.drawString("MPC:" + ability.getMPCost(unit), 200, pixelY);
				pixelY += 20;
			}
		    g2d.drawString("RNG:" + ability.getRange(), 200, pixelY);
		    
		    GraphicsConstants.drawParagraph(g2d, ability.getDescription(), 250, 300, 30);
        	if (ability.hasParam("Equipment Type")) {
        		g2d.drawString("Equipment: " + ability.getEquipmentType(), 600, 42);
        	}
//		    g2d.drawString(ability.getDescription(), 300, 20);
	    }
    }
    
    private void drawHPBars(Graphics2D g2d, Unit unit) {
 	    BattleMenuPanel menuPanel = manager.getCurrentMenuPanel();
	    MenuItem menuItem = menuPanel.getMenuItem();
	    Ability ability = null;
	    if (menuItem.getClass() == Ability.class && unit.hasPotentialStats()) {
		    ability = (Ability)menuItem;
	    }
    	
		g2d.setColor(new Color(255,0,0));
		g2d.fillRect(0, 0, GraphicsConstants.BATTLE_INFO_PANEL_WIDTH, 9);
		g2d.setColor(new Color(0,255,0));
		g2d.fillRect(0, 0,(int)(GraphicsConstants.BATTLE_INFO_PANEL_WIDTH * (((double)unit.getCurrHP())/((double)unit.getHP()))), 9);
		g2d.setColor(new Color(0,0,0));
		GraphicsConstants.drawRect(g2d, 0, 0,GraphicsConstants.BATTLE_INFO_PANEL_WIDTH, 9, 2);

		g2d.setColor(new Color(255,0,255));
		g2d.fillRect(0, 9, GraphicsConstants.BATTLE_INFO_PANEL_WIDTH, 9);
		g2d.setColor(new Color(255,125,0));
		g2d.fillRect(0, 9,(int)(GraphicsConstants.BATTLE_INFO_PANEL_WIDTH * (((double)unit.getCurrStamina())/((double)unit.getStamina()))), 9);
		if (ability != null) {
			g2d.setColor(new Color(255,255,0));
			g2d.fillRect((int)(GraphicsConstants.BATTLE_INFO_PANEL_WIDTH * (((double)unit.getCurrStamina() - ability.getStamCost(unit))/((double)unit.getStamina()))), 9,(int)(GraphicsConstants.BATTLE_INFO_PANEL_WIDTH * ((ability.getStamCost(unit))/((double)unit.getStamina()))), 9);
		}
		g2d.setColor(new Color(0,0,0));
		GraphicsConstants.drawRect(g2d, 0, 9,GraphicsConstants.BATTLE_INFO_PANEL_WIDTH, 9, 2);
		
		if (unit.getMP() > 0) {
			g2d.setColor(new Color(255,0,255));
			g2d.fillRect(0, 18, GraphicsConstants.BATTLE_INFO_PANEL_WIDTH, 9);
			g2d.setColor(new Color(0,0,255));
			g2d.fillRect(0, 18,(int)(GraphicsConstants.BATTLE_INFO_PANEL_WIDTH * (((double)unit.getCurrMP())/((double)unit.getMP()))), 9);
			if (ability != null) {
				g2d.setColor(new Color(255,255,0));
				g2d.fillRect((int)(GraphicsConstants.BATTLE_INFO_PANEL_WIDTH * (((double)unit.getCurrMP() - ability.getMPCost(unit))/((double)unit.getMP()))), 18,(int)(GraphicsConstants.BATTLE_INFO_PANEL_WIDTH * ((ability.getMPCost(unit))/((double)unit.getMP()))), 9);
			}
			g2d.setColor(new Color(0,0,0));
			GraphicsConstants.drawRect(g2d, 0, 18,GraphicsConstants.BATTLE_INFO_PANEL_WIDTH, 9, 2);
		}
    }
}
