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
import event.MenuItem;
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
        displayPhantomQueue(g2d);
        if (manager.getBattleMap().isTargetingMode() || manager.getBattleMap().isFreeSelectMode()) {
        	displayTargetInfo(g2d);
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
	    int pixelY = 20;
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
    }
   
    private void displayPhantomQueue(Graphics2D g2d) {
	    Unit currUnit = manager.getBattleMap().getCurrUnit();
	    List<Unit> unitList = manager.getBattleMap().getOrderOfBattle().getPhantomList();
	    for (int i = 0; i < unitList.size(); i++) {
		    Unit unit = unitList.get(i);
		    g2d.setColor(new Color(0,0,0));
	        g2d.fillRect(i * (GraphicsConstants.REGION_TILE_SIZE/3 * 2), 112, GraphicsConstants.REGION_TILE_SIZE/3 * 2, GraphicsConstants.REGION_TILE_SIZE/3 * 2);
		    if (unit.getFaction().equals("ALLY")) {
			    g2d.setColor(new Color(0,255,255));
		    } else {
		 	    g2d.setColor(new Color(255,0,0));
		    }
		    String length = unit.getName();
		    g2d.fillOval(i * (GraphicsConstants.REGION_TILE_SIZE/3 * 2), 112, (GraphicsConstants.REGION_TILE_SIZE/3 * 2), (GraphicsConstants.REGION_TILE_SIZE/3 * 2));
		    g2d.setColor(new Color(0,0,0));
		    g2d.drawString(unit.getName(), i * (GraphicsConstants.REGION_TILE_SIZE/3 *2) + 12 - (4 * length.length()), 130);
		    if (currUnit.equals(unit)) {
			    g2d.setColor(new Color(255,255,0));
			    GraphicsConstants.drawOval(g2d, i * (GraphicsConstants.REGION_TILE_SIZE/3 * 2), 112, 
			    						  (GraphicsConstants.REGION_TILE_SIZE/3 * 2), (GraphicsConstants.REGION_TILE_SIZE/3 * 2), 3);
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
	    	int pixelY = 20;
		    g2d.drawString("HP:" + String.valueOf(unit.getCurrHP()) + "/" + String.valueOf(unit.getHP()), 100, pixelY);
		    pixelY += 20;
		    if (unit.getMP() > 0) {
			    g2d.drawString("MP:" + String.valueOf(unit.getCurrMP()) + "/" + String.valueOf(unit.getMP()), 100, pixelY);
		    	pixelY += 20;
		    }
		    g2d.drawString("SP:" + String.valueOf(unit.getCurrStamina()) + "/" + String.valueOf(unit.getStamina()), 100, pixelY);
		    pixelY += 20;
		    g2d.drawString("MV:" + String.valueOf(unit.getCurrMovement()) + "/" + String.valueOf(unit.getMovement()), 100, pixelY);
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
		    int pixelY = 20;
		    int damage = ability.calculatePotentialDamage(unit);
		    if (damage > 0) {
				g2d.drawString("DMG:" + ability.calculatePotentialDamage(unit), 200, pixelY);
				pixelY += 20;
		    } else if (damage < 0){
				g2d.drawString("+HP:" + Math.abs(ability.calculatePotentialDamage(unit)), 200, pixelY);
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
		    
		    GraphicsConstants.drawParagraph(g2d, ability.getDescription(), 250, 300, 7);
        	if (ability.hasParam("Equipment Type")) {
        		g2d.drawString("Equipment: " + ability.getEquipmentType(), 600, 20);
        	}
//		    g2d.drawString(ability.getDescription(), 300, 20);
	    }
    }
}
