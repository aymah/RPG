package unit;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.util.List;

import event.Ability;
import event.Effect;
import map.BattleMap;
import map.BattlePanelManager;
import map.GameFrame;
import map.GraphicsConstants;
import map.InfoPanel;
import map.LayeredPanel;


public class UnitInfoPanel extends LayeredPanel {

	BattlePanelManager manager;
	Unit currUnit;
	
	public UnitInfoPanel(String name, GameFrame frame, BattlePanelManager manager) {
		super(1);
//		frame.add(this, new Integer(1), 0);
		manager.setUnitInfoPanel(this);
		this.manager = manager;
		this.setBounds(GraphicsConstants.UNIT_INFO_PANEL_X, GraphicsConstants.UNIT_INFO_PANEL_Y, GraphicsConstants.UNIT_INFO_PANEL_WIDTH, GraphicsConstants.UNIT_INFO_PANEL_HEIGHT);
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
		g2d.fillRect(GraphicsConstants.UNIT_INFO_PANEL_X, GraphicsConstants.UNIT_INFO_PANEL_Y, GraphicsConstants.UNIT_INFO_PANEL_WIDTH, GraphicsConstants.UNIT_INFO_PANEL_HEIGHT);
		displayStats(g2d);
		displayAbilities(g2d);
	}
	 
	private void displayStats(Graphics2D g2d) {
	    g2d.setColor(new Color(255,255,255));
	    int pixelY = 20;

    	g2d.drawString("Name:" + String.valueOf(currUnit.getName()), 10, pixelY);
	    pixelY += 20;	
    	g2d.drawString("Class:" + String.valueOf(currUnit.getType()), 10, pixelY);
	    pixelY += 20;	
	    String status = "Normal";
	    //if has burning effect, add burning
	    if (Effect.getEffects(currUnit.getAllEffects(), "Status", "Crippled").size() > 0) {
	    	status = "Crippled";
	    }
	    if (Effect.getEffects(currUnit.getAllEffects(), "Status", "Burning").size() > 0) {
	    	status = "Burning";
	    }
	    if (Effect.getEffects(currUnit.getAllEffects(), "Status", "Stunned").size() > 0) {
	    	status = "Stunned";
	    }
	    g2d.drawString("Status:" + status.toUpperCase(), 10, pixelY);
	    pixelY += 20;
	    if (currUnit.isFlying()) {
	    	g2d.drawString("Flyer", 10, pixelY);
	    	pixelY += 20;	
	    }
	    g2d.drawString("HP:" + String.valueOf(currUnit.getCurrHP()) + "/" + String.valueOf(currUnit.getHP()), 10, pixelY);
	    pixelY += 20;
	    if (currUnit.getMP() > 0) {
	    	g2d.drawString("MP:" + String.valueOf(currUnit.getCurrMP()) + "/" + String.valueOf(currUnit.getMP()), 10, pixelY);
		    pixelY += 20;	
	    }
		g2d.drawString("Stamina:" + String.valueOf(currUnit.getCurrStamina()) + "/" + String.valueOf(currUnit.getStamina()), 10, pixelY);
	    pixelY += 20;	
	    g2d.drawString("Movement:" + String.valueOf(currUnit.getCurrMovement()) + "/" + String.valueOf(currUnit.getMovement()), 10, pixelY);
	    pixelY += 20;	
    	g2d.drawString("Strength:" + String.valueOf(currUnit.getCurrStrength()), 10, pixelY);
    	if (currUnit.getMagic() > 0) {
		    pixelY += 20;	
	    	g2d.drawString("Magic:" + String.valueOf(currUnit.getCurrMagic()), 10, pixelY);
    	}
	    pixelY += 20;	
    	g2d.drawString("Initiative:" + String.valueOf(currUnit.getCurrInitiative()), 10, pixelY);
    	Unit unit = manager.getBattleMap().getOrderOfBattle().getCurrUnit();
	    pixelY += 20;	
    	g2d.drawString("Current Time:" + String.valueOf(unit.getSquad().getOrdering()), 10, pixelY);
	    pixelY += 20;	
    	g2d.drawString("Next Move:" + String.valueOf(currUnit.getSquad().getOrdering()), 10, pixelY);
	}
	
	private void displayAbilities(Graphics2D g2d) {
		List<Ability> abilities = currUnit.getActiveAbilities();
		int pixelY = 20;
		int pixelX = 200;
		for (Ability ability: abilities) {
			if (pixelY > 340) {
				pixelX += 200;
				pixelY = 20;
			}
			currUnit.setPotentialAbility(ability);
	    	g2d.drawString("Ability:" + String.valueOf(ability.getName()), pixelX, pixelY);
		    pixelY += 20;	
		    if (ability.calculatePotentialDamage(currUnit) > 0) {
				g2d.drawString("Damage:" + ability.calculatePotentialDamage(currUnit), pixelX, pixelY);
				pixelY += 20;
		    } else if (ability.calculatePotentialDamage(currUnit) < 0){
				g2d.drawString("Heal:" + Math.abs(ability.calculatePotentialDamage(currUnit)), pixelX, pixelY);
				pixelY += 20;
		    } else {
		    	g2d.drawString("Stamina Regen:" + Math.abs(ability.calculateAddedStamina(currUnit)), pixelX, pixelY);
				pixelY += 20;
		    }
	    	g2d.drawString("Range:" + String.valueOf(ability.getRange()), pixelX, pixelY);
			if (ability.getStamCost(currUnit) > 0) {
				pixelY += 20;
				g2d.drawString("Stamina Cost:" + ability.getStamCost(currUnit), pixelX, pixelY);
			}
			if (ability.getMPCost(currUnit) > 0) {
				pixelY += 20;
				g2d.drawString("MP Cost:" + ability.getMPCost(currUnit), pixelX, pixelY);
			}
			pixelY += 40;
			currUnit.wipePotentialAbility();
		}
	}

	public void displayUnit(Unit currUnit) {
		this.currUnit = currUnit;
		manager.changeDominantPanel(this);
		displayPanel();
	}
	
	
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch (keyCode) {
//			case 69:
//			case 10:
//				selectItem();
//				break;
			case 81:
			case 84:
			case 27:
				closePanel();
				break;
		}
		frame.refresh();
	}
	
	public void closePanel() {
		removePanel();
		manager.changeDominantPanelToPrevious();
		frame.repaint();
		frame.refresh();
	}
	
    public void removePanel() {
    	frame.remove(this);
    }
}
