package map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import unit.Hero;
import unit.Party;
import unit.Stats;
import unit.Unit;
import event.Ability;
import event.Effect;
import event.Equipment;
import event.GenericMenuItem;
import event.MenuItem;

//probably want to make a sort of menu items class, it will be similar in some ways to how events work. Might even extend off events.
public class RegionMenuPanel extends MenuPanel {
	
	private int selectorIndexX;
	private int selectorIndexY;
	private boolean heroSelectMenu;
	private boolean heroStatsMenu;
	private boolean heroSkillsMenu;
	private boolean heroEquipmentMenu;
	private boolean troopMenu;
	private boolean partyManagementMenu;
	private Hero hero;
	private Party party;

	
	public RegionMenuPanel(String name, GameFrame frame, RegionPanelManager manager, List<MenuItem> menuItems, int layer) {
		super(menuItems, layer);
		if (layer < 2) manager.setMenuPanel(this);
		this.manager = manager;
		this.setBounds(100, 100, 600, 400);
		this.name = name;
		this.frame = frame;
		this.selectorIndexX = 0;
		this.selectorIndexY = 0;
		heroSelectMenu = false;
		heroStatsMenu = false;
		heroSkillsMenu = false;
		heroEquipmentMenu = false;
		troopMenu = false;
		hero = null;
	}

	
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        Color bgColor = new Color(100,100,0);
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, 600, 400);
        drawMenu(g2d);
		Party party = ((RegionPanelManager)manager).getRegionMap().getParty();
		if (heroSelectMenu)
			drawHeroSelectMenu(g2d);
        if (troopMenu)
        	drawTroopInfo(g2d, party);
        if (heroStatsMenu)
        	drawHeroStatsInfo(g2d);
        if (heroSkillsMenu)
        	drawHeroSkillsInfo(g2d);
        if (heroEquipmentMenu)
        	drawHeroEquipmentInfo(g2d, party);
        if (partyManagementMenu)
        	drawPartyManagementInfo(g2d, party);
    }

	private void drawMenu(Graphics2D g2d) {
    	for (int i = 0; i < menuItems.size(); i++) {
            g2d.setColor(new Color(255,255,255));
            if (selectorIndexY == i)
            	g2d.setColor(new Color(255,0,0));
    		g2d.drawString(menuItems.get(i).getName(), 10, 20 + i * 30);
    	}
    }
    

	private void drawHeroSelectMenu(Graphics2D g2d) {
		if (selectorIndexY < 4) {
	        g2d.setColor(new Color(255,255,255));
	        Hero hero = (Hero)party.getUnit(selectorIndexY);
	        g2d.drawString("Stat Points: " + hero.getStatPoints() + " SP", 300, 20);
		}
	}
	
    private void drawTroopInfo(Graphics2D g2d, Party party) {
        g2d.setColor(new Color(255,255,255));
        g2d.drawString("Gold: " + party.getGold() + " G", 300, 20);
        if (selectorIndexY < 3) {
        	String type = "";
        	if (selectorIndexY == 0)
        		type = "Man At Arms";
        	if (selectorIndexY == 1)
        		type = "Archer";
        	if (selectorIndexY == 2)
        		type = "Knight";
        	int level = party.getLevel(type);
        	int cost = calculateCost(level);
	        g2d.drawString("Cost: " + cost + " G", 300, 40);
	        g2d.drawString("Current Level: " + level, 300, 60);
	        Stats stats = party.getStats(type);
	        Stats statsPerLevel = party.getStatsPerLevel(type);
	        g2d.drawString("Current Stats: ", 300, 100);
	        g2d.drawString("HP: ", 300, 120);
	        g2d.drawString("Strength: ", 300, 140);
	        g2d.drawString("Movement: ", 300, 160);
	        g2d.drawString("Initiative: ", 300, 180);
	        g2d.drawString("Stamina: ", 300, 200);
	        g2d.drawString("" + (stats.getHP() + (statsPerLevel.getHP() * level)), 380, 120);
	        g2d.drawString("" + (stats.getStrength() + (statsPerLevel.getStrength() * level)), 380, 140);
	        g2d.drawString("" + (stats.getMovement() + (statsPerLevel.getMovement() * level)), 380, 160);
	        g2d.drawString("" + (stats.getInitiative() + (statsPerLevel.getInitiative() * level)), 380, 180);
	        g2d.drawString("" + (stats.getStamina() + (statsPerLevel.getStamina() * level)), 380, 200);
	        g2d.drawString("On Level Up: ", 440, 100);
	        g2d.setColor(new Color(0,255,0));
	        g2d.drawString("+" + statsPerLevel.getHP(), 440, 120);
	        g2d.drawString("+" + statsPerLevel.getStrength(), 440, 140);
	        g2d.drawString("+" + statsPerLevel.getMovement(), 440, 160);
	        g2d.drawString("+" + statsPerLevel.getInitiative(), 440, 180);
	        g2d.drawString("+" + statsPerLevel.getStamina(), 440, 200);
        }
    }
    
    private void drawHeroStatsInfo(Graphics2D g2d) {
        g2d.setColor(new Color(255,255,255));
        g2d.drawString("Stat Points: " + hero.getStatPoints() + " SP", 300, 20);
        g2d.drawString("Current Stats: ", 300, 100);
        g2d.drawString("HP: ", 300, 120);
        g2d.drawString("MP: ", 300, 140);
        g2d.drawString("Strength: ", 300, 160);
        g2d.drawString("Magic: ", 300, 180);
        g2d.drawString("Movement: ", 300, 200);
        g2d.drawString("Initiative: ", 300, 220);
        g2d.drawString("Stamina: ", 300, 240);
        g2d.drawString("" + hero.getHP(), 380, 120);
        g2d.drawString("" + hero.getMP(), 380, 140);
        g2d.drawString("" + hero.getStrength(), 380, 160);
        g2d.drawString("" + hero.getMagic(), 380, 180);
        g2d.drawString("" + hero.getMovement(), 380, 200);
        g2d.drawString("" + hero.getInitiative(), 380, 220);
        g2d.drawString("" + hero.getStamina(), 380, 240);
        if (selectorIndexY < 5) {
        	String type = "";
	    	if (selectorIndexY == 0)
	    		type = "HP";
	    	if (selectorIndexY == 1)
	    		type = "MP";
	    	if (selectorIndexY == 2)
	    		type = "Strength";
	    	if (selectorIndexY == 3)
	    		type = "Magic";
	    	if (selectorIndexY == 4)
	    		type = "Stamina";
	    	int level = hero.getStatLevel(type);
	    	int cost = calculateStatCost(level);
	        g2d.drawString("Cost: " + cost + " SP", 300, 40);
	        g2d.drawString("Current Upgrades: " + level, 300, 60);
	        g2d.drawString("On Upgrade: ", 440, 100);
	        Stats statsForNextLevel = hero.calculateNextLevelStats();
	        g2d.setColor(new Color(0,255,0));
	        if (selectorIndexY == 0) {
	        	g2d.drawString("+" + (statsForNextLevel.getHP() - hero.getHP()), 440, 120);
	        }
	        if (selectorIndexY == 1) {
	        	g2d.drawString("+" + (statsForNextLevel.getMP() - hero.getMP()), 440, 140);
	        }
	        if (selectorIndexY == 2) {
	        	g2d.drawString("+" + (statsForNextLevel.getStrength() - hero.getStrength()), 440, 160);
	        }
	        if (selectorIndexY == 3) {
	        	g2d.drawString("+" + (statsForNextLevel.getMagic() - hero.getMagic()), 440, 180);
	        }
	        if (selectorIndexY == 4) {
	        	g2d.drawString("+" + (statsForNextLevel.getStamina() - hero.getStamina()), 440, 240);
	        }
        }
	}
    
    private void drawHeroSkillsInfo(Graphics2D g2d) {
    	List<Ability> skills = hero.getSkillList();
        g2d.setColor(new Color(255,255,255));
        g2d.drawString("Stat Points: " + hero.getStatPoints() + " SP", 300, 20);
        if (selectorIndexY < skills.size()) {
        	Ability skill = skills.get(selectorIndexY);
        	if (skill.hasParam("Equipment Type")) {
        		g2d.drawString("Equipment: " + skill.getEquipmentType(), 400, 40);
        	}
            g2d.drawString("Max Level: " + skill.get("Max Level"), 400, 60);
        	hero.setPotentialAbility(skill);
//        	Map<String, List<Effect>> effects = skill.getEffects("Modify Multiplier", "Ability Level");
        	Map<String, List<Effect>> effects = skill.getEffects("Origin", "Ability");
	        List<Effect> effectList = null;
	    	int level = skill.getLevel();
	    	int cost = skill.calculateSkillCost(level);
	    	if (level >= 0)
	    		g2d.drawString("Current Skill: ", 250, 100);
	        g2d.drawString("Cost: " + cost + " SP", 250, 40);
	        if (level >= 0) {
	        	g2d.drawString("Skill Level: " + level, 250, 60);
	        	g2d.drawString("On Level Up: ", 440, 100);
	        }
	        int pixelY = 120;
        	g2d.setColor(new Color(255,255,255));
        	if (skill.getName().equals("Pyromancer")) {
		        effectList = effects.get("HP");
		        Effect effect = effectList.get(0);
	        	g2d.drawString("Burn Magic Factor: ", 250, pixelY);
        		g2d.drawString("" + Math.abs(effect.getModifyValue() * skill.getLevel()), 380, pixelY);
            	g2d.setColor(new Color(0,255,0));
        		g2d.drawString("+" + Math.abs(effect.getModifyValue()), 440, pixelY);
	        	pixelY += 20;
	        	g2d.setColor(new Color(255,255,255));
	        	g2d.drawString("Duration: ", 250, pixelY);
        		g2d.drawString("3 Turns", 380, pixelY);
        	}
	        if (skill.hasEffect("Key", "Stamina")) {
	        	g2d.setColor(new Color(255,255,255));
		        g2d.drawString("Magic Factor: ", 250, pixelY);
		        effectList = effects.get("Stamina");
		        double factor = effectList.get(0).getModifyValue();
		        for (int i = 0; i < skill.getLevel(); i++) {
		        	factor += effectList.get(1).getModifyValue();
		        }
		        g2d.drawString("" + (Math.round(factor * 100.0) / 100.0), 390, pixelY);
		        if (effectList != null && level >= 0) {
		        	g2d.setColor(new Color(0,255,0));
		        	g2d.drawString("+" + effectList.get(1).getModifyValue(), 440, pixelY);
		        }
		        pixelY += 20;
	        }
	        if (Effect.getEffects(effects, "Key", "Strength").size() > 0) {
	        	double placeholderFactor = 0;
	        	double placeholderIncrease = 0;
		        effectList = effects.get("Strength");
	        	g2d.drawString("Strength: ", 250, pixelY);
		        if (effectList != null) {
		        	double factor = 0; //factor is additional str
		        	double increase = 0;
		        	String symbol = "+";
		        	if (!Effect.hasEffects(effectList, "Modify Type", "Additive")) {
		        		factor = 1;
		        		increase = 1;
		        		symbol = "x";
		        	}
//		        	if (Effect.hasEffects(effectList, "Modify Multiplier", "Ability Level * HP"))
//		        		factor = 0.01;
		        	effectList.sort(Effect.getComparator());
		        	boolean placeholder = false;

		        	for (Effect effect: effectList) {
		        		factor = effect.modifyValue(factor, hero);
		        		if (effect.get("Modify Type").equals("Additive")) {
		        			increase += (double)effect.getModifyValue();
		        			placeholder = true;
		        		}
		        		if (effect.get("Modify Type").equals("Multiplicative"))
		        			increase *= (double)effect.getModifyValue();
		        		if (effect.get("Modify Multiplier").equals("Ability Level * HP")) {
		        			increase *= hero.getHP();
		        			placeholderFactor = skill.getLevel() * (double)effect.getModifyValue();
		        			placeholderIncrease = (double)effect.getModifyValue();
		        		}
		        	}
		        	if (skill.hasParam("Strength"))
		        		factor += (double)skill.get("Strength");
		        	if (placeholder) //this should be something else. what, i don't know. but it should be.
		        		g2d.drawString("+" + (Math.round((factor) * 100.0) / 100.0), 380, pixelY);
		        	else
		        		g2d.drawString("x" + (Math.round((factor) * 100.0) / 100.0), 380, pixelY);
		        	if (level >= 0) {
			        	g2d.setColor(new Color(0,255,0));
			        	g2d.drawString(symbol + increase, 440, pixelY);
		        	}

		        }
		        pixelY += 20;
//	        	if (Effect.hasEffects(effectList, "Modify Multiplier", "Ability Level * HP")) {
//	            	g2d.setColor(new Color(255,255,255));
//		        	g2d.drawString("HP to Strength: ", 250, pixelY);
//	        		g2d.drawString("+" + (Math.round((placeholderFactor) * 100.0) / 100.0), 380, pixelY);
//		        	g2d.setColor(new Color(0,255,0));
//	        		g2d.drawString("+" + (Math.round((placeholderIncrease) * 100.0) / 100.0), 380, pixelY);
//	        		pixelY += 20;
//	        	}
	        }
        	g2d.setColor(new Color(255,255,255));
	        if (Effect.getEffects(effects, "Key", "Strength Factor").size() > 0) {
		        effectList = effects.get("Strength Factor");
	        	g2d.drawString("Strength Factor: ", 250, pixelY);
		        if (effectList != null) {
		        	double factor = 0;
		        	double increase = 0;
		        	String symbol = "+";
		        	if (!Effect.hasEffects(effectList, "Modify Type", "Additive")) {
		        		factor = 1;
		        		increase = 1;
		        		symbol = "x";
		        	}
		        	effectList.sort(Effect.getComparator());
		        	for (Effect effect: effectList) {
		        		factor = effect.modifyValue(factor, hero);
		        		if (effect.get("Modify Type").equals("Additive"))
		        			increase += (double)effect.getModifyValue();
		        		if (effect.get("Modify Type").equals("Multiplicative"))
		        			increase *= (double)effect.getModifyValue();
		        	}
		        	if (skill.hasParam("Strength Factor"))
		        		factor += (double)skill.get("Strength Factor");
		        	if (skill.hasParam("Strength Factor")) //this should be something else. what, i don't know. but it should be.
		        		g2d.drawString("+" + (Math.round((factor) * 100.0) / 100.0), 380, pixelY);
		        	else
		        		g2d.drawString("x" + (Math.round((factor) * 100.0) / 100.0), 380, pixelY);
		        	if (level >= 0) {
			        	g2d.setColor(new Color(0,255,0));
			        	g2d.drawString(symbol + (Math.round((increase) * 100.0) / 100.0), 440, pixelY);
		        	}

		        }
		        pixelY += 20;
	        }
        	g2d.setColor(new Color(255,255,255));
	        if (Effect.getEffects(effects, "Key", "Magic Factor").size() > 0) {
		        effectList = effects.get("Magic Factor");
	        	g2d.drawString("Magic Factor: ", 250, pixelY);
		        if (effectList != null) {
		        	double factor = 0;
		        	double increase = 0;
		        	boolean placeholder = false;
		        	String symbol = "+";
		        	if (!Effect.hasEffects(effectList, "Modify Type", "Additive")) {
		        		factor = 1;
		        		increase = 1;
		        		symbol = "x";
		        	}
		        	effectList.sort(Effect.getComparator());
		        	for (Effect effect: effectList) {
		        		factor = effect.modifyValue(factor, hero);
		        		if (effect.get("Modify Type").equals("Additive")) {
		        			increase += (double)effect.getModifyValue();
		        			placeholder = true;
		        		}
		        		if (effect.get("Modify Type").equals("Multiplicative"))
		        			increase *= (double)effect.getModifyValue();
		        	}
		        	if (skill.hasParam("Magic Factor"))
		        		factor += (double)skill.get("Magic Factor");
		        	if (placeholder) {
		        		g2d.drawString("+" + Math.abs((Math.round((factor) * 100.0) / 100.0)), 380, pixelY);
		        	
		        	} else
		        		g2d.drawString("x" + (Math.round((factor) * 100.0) / 100.0), 380, pixelY);
		        	if (level >= 0) {
		        		g2d.setColor(new Color(0,255,0));
		        		g2d.drawString(symbol + Math.abs((Math.round((increase) * 100.0) / 100.0)), 440, pixelY);
		        	}

		        }
		        pixelY += 20;
	        }
        	g2d.setColor(new Color(255,255,255));
	        if (skill.hasParam("Range")) {
	        	g2d.setColor(new Color(255,255,255));
		        g2d.drawString("Range: ", 250, pixelY);
		        g2d.drawString("" + skill.getRange(), 390, pixelY);
		        effectList = effects.get("Range");
		        if (effectList != null && level >= 0) {
		        	g2d.setColor(new Color(0,255,0));
		        	g2d.drawString("+" + effectList.get(0).getModifyValue(), 440, pixelY);
		        }
		        pixelY += 20;
	        }
        	g2d.setColor(new Color(255,255,255));
	        if (Effect.getEffects(effects, "Key", "Stamina Cost").size() > 0) {
		        effectList = effects.get("Stamina Cost");
	        	g2d.drawString("Stamina Cost: ", 250, pixelY);
		        if (effectList != null) {
		        	double factor = 0;
		        	double increase = 0;
		        	boolean placeholder = false;
		        	String symbol = "+";
		        	if (!Effect.hasEffects(effectList, "Modify Type", "Additive")) {
		        		factor = 1;
		        		increase = 1;
		        		symbol = "x";
		        	}
		        	effectList.sort(Effect.getComparator());
		        	for (Effect effect: effectList) {
		        		factor = effect.modifyValue(factor, hero);
		        		if (effect.get("Modify Type").equals("Additive")) {
		        			increase += (double)effect.getModifyValue();
		        			placeholder = true;
		        		}
		        		if (effect.get("Modify Type").equals("Multiplicative"))
		        			increase *= (double)effect.getModifyValue();
		        		if (effect.get("Modify Multiplier").equals("Ability Level * HP")) {
		        			increase *= hero.getHP();
		        		}
		        	}
		        	if (skill.hasParam("Stamina Cost"))
		        		factor += (int)skill.get("Stamina Cost");
		        	if (placeholder)
		        		g2d.drawString("+" + (Math.round((factor) * 100.0) / 100.0), 380, pixelY);
		        	else
		        		g2d.drawString("x" + (Math.round((factor) * 100.0) / 100.0), 380, pixelY);
		        	if (level >= 0) {
		        		g2d.setColor(new Color(255,0,0));
		        		g2d.drawString(symbol + increase, 440, pixelY);
		        	}
		        }
		        pixelY += 20;
	        }
        	g2d.setColor(new Color(255,255,255));
	        if (Effect.getEffects(effects, "Key", "MP Cost").size() > 0) {
		        effectList = effects.get("MP Cost");
	        	g2d.drawString("MP Cost: ", 250, pixelY);
		        if (effectList != null) {
		        	double factor = 0;
		        	double increase = 0;
		        	String symbol = "+";
		        	if (!Effect.hasEffects(effectList, "Modify Type", "Additive")) {
		        		factor = 1;
		        		increase = 1;
		        		symbol = "x";
		        	}
		        	effectList.sort(Effect.getComparator());
		        	for (Effect effect: effectList) {
		        		factor = effect.modifyValue(factor, hero);
		        		if (effect.get("Modify Type").equals("Additive"))
		        			increase += (double)effect.getModifyValue();
		        		if (effect.get("Modify Type").equals("Multiplicative"))
		        			increase *= (double)effect.getModifyValue();
		        	}
		        	if (skill.hasParam("MP Cost"))
		        		g2d.drawString("+" + (Math.round((factor + (int)skill.get("MP Cost")) * 100.0) / 100.0), 380, pixelY);
		        	else
		        		g2d.drawString("x" + (Math.round((factor) * 100.0) / 100.0), 380, pixelY);
		        	if (level >= 0) {
		        		g2d.setColor(new Color(255,0,0));
		        		g2d.drawString(symbol + increase, 440, pixelY);
		        	}
		        }
		        pixelY += 20;
	        }
        	g2d.setColor(new Color(255,255,255));
	        if (Effect.getEffects(effects, "Key", "HP").size() > 0) {
	        	if (!(Effect.getEffects(effects, "Type", "Create Stat Enemy Modifier").size() > 0)) {
			        effectList = effects.get("HP");
		        	g2d.drawString("HP Factor: ", 250, pixelY);
			        if (effectList != null) {
			        	double factor = 0;
			        	double increase = 0;
			        	String symbol = "+";
			        	if (!Effect.hasEffects(effectList, "Modify Type", "Additive")) {
			        		factor = 1;
			        		increase = 1;
			        		symbol = "x";
			        	}
			        	effectList.sort(Effect.getComparator());
			        	for (Effect effect: effectList) {
			        		factor = effect.modifyValue(factor, hero);
			        		if (effect.get("Modify Type").equals("Additive"))
			        			increase += (double)effect.getModifyValue();
			        		if (effect.get("Modify Type").equals("Multiplicative"))
			        			increase *= (double)effect.getModifyValue();
			        	}
			        	if (skill.hasParam("HP"))
			        		g2d.drawString("+" + (Math.round((factor + (int)skill.get("HP")) * 100.0) / 100.0), 380, pixelY);
			        	else
			        		g2d.drawString("x" + (Math.round((factor) * 100.0) / 100.0), 380, pixelY);
			        	if (level >= 0) {
			        		g2d.setColor(new Color(0,255,0));
			        		g2d.drawString(symbol + increase, 440, pixelY);
			        	}
			        } 
		        }
		        pixelY += 20;
	        }
	        if (skill.hasEffect("Key", "Movement")) {
	        	g2d.setColor(new Color(255,255,255));
		        g2d.drawString("Movement: ", 250, pixelY);
		        if (skill.hasEffect("Type", "Stat Enemy Modifier"))
			        g2d.drawString("-99", 380, pixelY);
		        else
	        		g2d.drawString("" + hero.getMovement(), 380, pixelY);
		        effectList = effects.get("Movement");
		        if (effectList != null && level >= 0) {
		        	g2d.setColor(new Color(0,255,0));
			        if (skill.hasEffect("Type", "Stat Enemy Modifier")) {
			        } else
		        	g2d.drawString("+" + effectList.get(0).getModifyValue(), 440, pixelY);
		        pixelY += 20;
		        }
	        }
	        if (skill.hasEffect("Key", "Initiative")) {
	        	g2d.setColor(new Color(255,255,255));
		        g2d.drawString("Initiative: ", 250, pixelY);
		        g2d.drawString("" + hero.getInitiative(), 380, pixelY);
		        effectList = effects.get("Initiative");
		        if (effectList != null && level >= 0) {
		        	g2d.setColor(new Color(0,255,0));
		        	g2d.drawString("x" + effectList.get(0).getModifyValue(), 440, pixelY);
		        pixelY += 20;
		        }
	        }
	        if (skill.hasEffect("Key", "Stamina Regen")) {
	        	g2d.setColor(new Color(255,255,255));
		        g2d.drawString("Stamina Regen: ", 250, pixelY);
		        effectList = effects.get("Stamina Regen");
		        double factor = 1;
		        for (int i = 0; i < skill.getLevel(); i++) {
		        	factor *= effectList.get(0).getModifyValue();
		        }
		        g2d.drawString("" + (Math.round(factor * 100.0) / 100.0), 380, pixelY);
		        if (effectList != null && level >= 0) {
		        	g2d.setColor(new Color(0,255,0));
		        	g2d.drawString("x" + effectList.get(0).getModifyValue(), 440, pixelY);
		        pixelY += 20;
		        }
	        }
	        g2d.setColor(new Color(255,255,255));
		    GraphicsConstants.drawParagraph(g2d, skill.getDescription(), 300, 250, 300);
        	hero.wipePotentialAbility();
        }
	}
    
    private void drawHeroEquipmentInfo(Graphics2D g2d, Party party) {
        g2d.setColor(new Color(255,255,255));
        g2d.drawString("Gold: " + party.getGold() + " G", 300, 20);
     	List<Equipment> items = hero.getEquipmentList();
     	if (selectorIndexY < items.size()) {
     		Equipment item = items.get(selectorIndexY);    	
     		int level = (int)item.get("Level");
	    	int cost = item.calculateUpgradeCost(level);
	    	g2d.drawString("Current Item: ", 250, 100);
	        g2d.drawString("Cost: " + cost + " G", 250, 40);
	        g2d.drawString("Item Level: " + level, 250, 60);
            g2d.drawString("Max Level: " + item.get("Max Level"), 400, 60);
	        g2d.drawString("On Level Up: ", 440, 100);
	        int pixelY = 120;
        	g2d.setColor(new Color(255,255,255));
	        if (item.hasEffect("Key", "Strength Factor")) {
	        	double strengthFactor = item.getEffectParam("Strength Factor");
	        	g2d.drawString("Strength Factor: ", 250, pixelY);
		        g2d.drawString("" + (Math.round(strengthFactor * 10.0) / 10.0), 380, pixelY);
		        Map<String, List<Effect>> effectMap = Effect.getEffects(item.getEffects("Key", "Strength Factor"), "Modify Multiplier", "Equipment Level");
		        List<Effect> effectList = effectMap.get("Strength Factor");
		        if (effectList != null) {
		        	g2d.setColor(new Color(0,255,0));
		        	g2d.drawString("+" + effectList.get(0).getModifyValue(), 440, pixelY);
		        }
		        pixelY += 20;
	        }
        	g2d.setColor(new Color(255,255,255));
	        if (item.hasEffect("Key", "Stamina Cost")) {
	        	double staminaCost = item.getEffectParam("Stamina Cost");
	        	g2d.drawString("Stamina Cost: ", 250, pixelY);
		        g2d.drawString("" + (Math.round(staminaCost * 10.0) / 10.0), 380, pixelY);
		        Map<String, List<Effect>> effectMap = Effect.getEffects(item.getEffects("Key", "Stamina Cost"), "Modify Multiplier", "Equipment Level");
		        List<Effect> effectList = effectMap.get("Stamina Cost");
		        if (effectList != null) {
		        	g2d.setColor(new Color(255,0,0));
		        	g2d.drawString("+" + effectList.get(0).getModifyValue(), 440, pixelY);
		        }
		        pixelY += 20;
	        }
        	g2d.setColor(new Color(255,255,255));
	        if (item.hasEffect("Key", "HP")) {
	        	double HP = item.getEffectParam("HP");
	        	g2d.drawString("HP: ", 250, pixelY);
		        g2d.drawString("+" + (Math.round(HP * 10.0) / 10.0), 380, pixelY);
		        Map<String, List<Effect>> effectMap = Effect.getEffects(item.getEffects("Key", "HP"), "Modify Multiplier", "Equipment Level");
		        List<Effect> effectList = effectMap.get("HP");
		        if (effectList != null) {
		        	g2d.setColor(new Color(0,255,0));
		        	g2d.drawString("+" + effectList.get(0).getModifyValue(), 440, pixelY);
		        }
		        pixelY += 20;
	        }
        	g2d.setColor(new Color(255,255,255));
	        if (item.hasEffect("Key", "Movement Stamina Cost")) {
	        	double staminaCost = item.getEffectParam("Movement Stamina Cost");
	        	g2d.drawString("Movement Cost: ", 250, pixelY);
		        g2d.drawString("+" + (Math.round(staminaCost * 10.0) / 10.0), 380, pixelY);
		        Map<String, List<Effect>> effectMap = Effect.getEffects(item.getEffects("Key", "Movement Stamina Cost"), "Modify Multiplier", "Equipment Level");
		        List<Effect> effectList = effectMap.get("Movement Stamina Cost");
		        if (effectList != null) {
		        	g2d.setColor(new Color(255,0,0));
		        	g2d.drawString("+" + effectList.get(0).getModifyValue(), 440, pixelY);
		        }
		        pixelY += 20;
	        }
	        if (item.hasEffect("Key", "Movement")) {
	        	double staminaCost = item.getEffectParam("Movement");
	        	g2d.drawString("Movement: ", 250, pixelY);
		        g2d.drawString("+" + (Math.round(staminaCost * 10.0) / 10.0), 380, pixelY);
		        Map<String, List<Effect>> effectMap = Effect.getEffects(item.getEffects("Key", "Movement"), "Modify Multiplier", "Equipment Level");
		        List<Effect> effectList = effectMap.get("Movement");
		        if (effectList != null) {
		        	g2d.setColor(new Color(0,255,0));
		        	g2d.drawString("+" + effectList.get(0).getModifyValue(), 440, pixelY);
		        }
		        pixelY += 20;
	        }
     	}
    }
    
    private void drawPartyManagementInfo(Graphics2D g2d, Party party) {
        g2d.setColor(new Color(255,255,255));
        g2d.drawString("Party Size: " + party.getPartyValue() + "/" + party.getMaxPartySize(), 300, 20);
    	String type = "";
    	if (selectorIndexY == 0)
    		type = "Man At Arms";
    	if (selectorIndexY == 1)
    		type = "Archer";
    	if (selectorIndexY == 2)
    		type = "Knight";
    	if (type.equals("Man At Arms"))
    		g2d.setColor(new Color(255,0,0));
    	g2d.drawString("" + party.getNumberOfType("Man At Arms"), 150, 20);
        g2d.setColor(new Color(255,255,255));
      	if (type.equals("Archer"))
    		g2d.setColor(new Color(255,0,0));
    	g2d.drawString("" + party.getNumberOfType("Archer"), 150, 50);
        g2d.setColor(new Color(255,255,255));
      	if (type.equals("Knight"))
    		g2d.setColor(new Color(255,0,0));
    	g2d.drawString("" + party.getNumberOfType("Knight"), 150, 80);
        g2d.setColor(new Color(255,255,255)); 
    }
    
	private int calculateCost(int level) {
		int cost = 750;
		for (int i = 0; i < level; i++) {
			cost *= 1.15;
		}
		return cost;
	}

	private int calculateStatCost(int level) {
		int cost = 250;
		for (int i = 0; i < level; i++) {
			cost *= 1.15;
		}
		return cost;
	}
	
	public void keyPressed(KeyEvent e) {
		if (subMenu != null) {
			subMenu.keyPressed(e);
			return;
		}
		if (partyManagementMenu) {
			partyManagementKeyPressed(e);
			return;
		}
		int keyCode = e.getKeyCode();
		System.out.println(keyCode);
		boolean moved = false;
		switch (keyCode) {
			case 87:
			case 38:
				moveUp();
				break;
			case 83:
			case 40:
				moveDown();
				break;
//			case 65:
//			case 37:
//				moveLeft();
//				break;
//			case 68:
//			case 39:
//				moveRight();
//				break;
			case 69:
			case 10:
				selectItem();
				break;
			case 81:
			case 84:
			case 27:
				closeMenu();
				break;
		}
		this.repaint();
	}
	
	private void partyManagementKeyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		System.out.println(keyCode);
		boolean moved = false;
		switch (keyCode) {
			case 87:
			case 38:
				moveUp();
				break;
			case 83:
			case 40:
				moveDown();
				break;
			case 65:
			case 37:
				moveLeft();
				break;
			case 68:
			case 39:
				moveRight();
				break;
			case 69:
			case 10:
				selectItem();
				break;
			case 81:
			case 84:
			case 27:
				closeMenu();
				break;
		}
		this.repaint();
		
	}


	private void moveUp() {
		selectorIndexY--;
		if (selectorIndexY < 0)
			selectorIndexY = menuItems.size() - 1;
	}
	
	private void moveDown() {
		selectorIndexY++;
		if (selectorIndexY >= menuItems.size())
			selectorIndexY = 0;
	}
	
	private void moveLeft() {
    	String type = "";
    	if (selectorIndexY == 0)
    		type = "Man At Arms";
    	if (selectorIndexY == 1)
    		type = "Archer";
    	if (selectorIndexY == 2)
    		type = "Knight";	
    	if (party.getNumberOfType(type) > 0) {
    		party.removeUnit(type);
    	}
    }
	
	private void moveRight() {
    	String type = "";
    	String filename = "";
    	if (selectorIndexY == 0) {
    		type = "Man At Arms";
    		filename = "testManAtArms1";
    	}
    	if (selectorIndexY == 1) {
    		type = "Archer";
    		filename = "testArcher1";
    	}
    	if (selectorIndexY == 2) {
    		type = "Knight";
    		filename = "testKnight1";
    	}
    	if (party.getPartyValue() + party.getUnitValue(type) <= party.getMaxPartySize()) {
			Unit unit = new Unit(filename, "ALLY", type.substring(0, 1) + (party.getNumberOfType(type) + 1), party.getLevel(type));
			party.addUnit(unit);
		}
	}
	
	private void selectItem() {
		menuItems.get(selectorIndexY).execute(this);
	}

	public static List<MenuItem> getStandardMenu() {
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
		MenuItem item = new MenuItem() {
			private String name = "Upgrade Heroes";
			
			@Override
			public void execute(GamePanel panel) {
				RegionMenuPanel menuPanel = (RegionMenuPanel) panel;
				Party party = ((RegionPanelManager)menuPanel.getManager()).getRegionMap().getParty();
				menuPanel.openHeroSelectMenu(party);
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
		item = new MenuItem() {
			private String name = "Upgrade Troops";
			
			@Override
			public void execute(GamePanel panel) {
				RegionMenuPanel menuPanel = (RegionMenuPanel) panel;
				menuPanel.openTroopMenu();
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
		item = new MenuItem() {
			private String name = "Manage Party";
			
			@Override
			public void execute(GamePanel panel) {
				RegionMenuPanel menuPanel = (RegionMenuPanel) panel;
				Party party = ((RegionPanelManager)menuPanel.getManager()).getRegionMap().getParty();
				menuPanel.openPartyManagementMenu(party);
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
    	menuItems.add(exitMenuItem());
		return menuItems;
	}


	private void openTroopMenu() {
		List<MenuItem> menuItems = getTroopMenu();
    	RegionMenuPanel troopMenu = new RegionMenuPanel("Troop Menu", frame, (RegionPanelManager) manager, menuItems, layer + 1);
    	subMenu = troopMenu;
    	troopMenu.superMenu = this;
    	troopMenu.displayPanel();
    	troopMenu.setTroopMenu(true);
		frame.refresh();
	}
	
	private void openPartyManagementMenu(Party party) {
		List<MenuItem> menuItems = getPartyManagementMenu();
    	RegionMenuPanel partyManagementMenu = new RegionMenuPanel("Party Management Menu", frame, (RegionPanelManager) manager, menuItems, layer + 1);
    	subMenu = partyManagementMenu;
    	partyManagementMenu.superMenu = this;
    	partyManagementMenu.displayPanel();
    	partyManagementMenu.setPartyManagementMenu(true);
    	partyManagementMenu.setParty(party);
		frame.refresh();
	}

	private void openHeroSelectMenu(Party party) {
		List<MenuItem> menuItems = getHeroSelectMenu(party);
    	RegionMenuPanel heroMenu = new RegionMenuPanel("Hero Select Menu", frame, (RegionPanelManager) manager, menuItems, layer + 1);
    	subMenu = heroMenu;
    	heroMenu.superMenu = this;
    	heroMenu.displayPanel();
    	heroMenu.setHeroSelectMenu(true);
    	heroMenu.setParty(party);
		frame.refresh();
	}
	
	private void openHeroMenu(Hero hero) {
		List<MenuItem> menuItems = getHeroMenu(hero);
    	RegionMenuPanel heroMenu = new RegionMenuPanel("Hero Menu", frame, (RegionPanelManager) manager, menuItems, layer + 1);
    	heroMenu.setHero(hero);
    	subMenu = heroMenu;
    	heroMenu.superMenu = this;
    	heroMenu.displayPanel();
		frame.refresh();
	}
	
	private void openHeroStatsMenu(Hero hero) {
		List<MenuItem> menuItems = getHeroStatsMenu(hero);
    	RegionMenuPanel heroStatsMenu = new RegionMenuPanel("Hero Stats Menu", frame, (RegionPanelManager) manager, menuItems, layer + 1);
    	heroStatsMenu.setHero(hero);
    	subMenu = heroStatsMenu;
    	heroStatsMenu.superMenu = this;
    	heroStatsMenu.displayPanel();
    	heroStatsMenu.setHeroStatsMenu(true);
		frame.refresh();
	}
	
	private void openHeroSkillsMenu(Hero hero) {
		List<MenuItem> menuItems = getHeroSkillsMenu(hero);
    	RegionMenuPanel heroSkillsMenu = new RegionMenuPanel("Hero Skills Menu", frame, (RegionPanelManager) manager, menuItems, layer + 1);
    	heroSkillsMenu.setHero(hero);
    	subMenu = heroSkillsMenu;
    	heroSkillsMenu.superMenu = this;
    	heroSkillsMenu.displayPanel();
    	heroSkillsMenu.setHeroSkillsMenu(true);
		frame.refresh();
	}
	
	private void openHeroEquipmentMenu(Hero hero) {
		List<MenuItem> menuItems = getHeroEquipmentMenu(hero);
    	RegionMenuPanel heroEquipmentMenu = new RegionMenuPanel("Hero Equipment Menu", frame, (RegionPanelManager) manager, menuItems, layer + 1);
    	heroEquipmentMenu.setHero(hero);
    	subMenu = heroEquipmentMenu;
    	heroEquipmentMenu.superMenu = this;
    	heroEquipmentMenu.displayPanel();
    	heroEquipmentMenu.setHeroEquipmentMenu(true);
		frame.refresh();
	}
	
	private List<MenuItem> getHeroSelectMenu(Party party) {
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
		List<Hero> heroList = party.getHeroList();
		MenuItem item = null;
		for (Hero hero: heroList) {
			item = new MenuItem() {
				private String name = hero.getName();
				
				@Override
				public void execute(GamePanel panel) {
					RegionMenuPanel menuPanel = (RegionMenuPanel) panel;
					menuPanel.openHeroMenu(hero);
				}

				@Override
				public String getName() {
					return name;
				}
			};
			menuItems.add(item);
		}
		menuItems.add(MenuPanel.exitMenuItem());	
		return menuItems;
	}
	
	private List<MenuItem> getTroopMenu() {
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
		MenuItem item = new MenuItem() {
			private String name = "Level Man At Arms";
			
			@Override
			public void execute(GamePanel panel) {
				RegionMenuPanel menuPanel = (RegionMenuPanel) panel;
				Party party = ((RegionPanelManager)menuPanel.getManager()).getRegionMap().getParty();
	        	int level = party.getLevel("Man At Arms");
	        	int cost = calculateCost(level);
				if (party.getGold() >= cost) {
					party.incrementLevel("Man At Arms");
					party.subtractGold(cost);
					party.updateUnitLevels();
				}
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
		item = new MenuItem() {
			private String name = "Level Archer";
			
			@Override
			public void execute(GamePanel panel) {
				RegionMenuPanel menuPanel = (RegionMenuPanel) panel;
				Party party = ((RegionPanelManager)menuPanel.getManager()).getRegionMap().getParty();
	        	int level = party.getLevel("Archer");
	        	int cost = calculateCost(level);
				if (party.getGold() >= cost) {
					party.incrementLevel("Archer");
					party.subtractGold(cost);
					party.updateUnitLevels();
				}			
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
		item = new MenuItem() {
			private String name = "Level Knight";
			
			@Override
			public void execute(GamePanel panel) {
				RegionMenuPanel menuPanel = (RegionMenuPanel) panel;
				Party party = ((RegionPanelManager)menuPanel.getManager()).getRegionMap().getParty();
	        	int level = party.getLevel("Knight");
	        	int cost = calculateCost(level);
				if (party.getGold() >= cost) {
					party.incrementLevel("Knight");
					party.subtractGold(cost);
					party.updateUnitLevels();
				}
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
    	menuItems.add(exitMenuItem());
		return menuItems;
	}
	
	private List<MenuItem> getPartyManagementMenu() {
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
		MenuItem item = new MenuItem() {
			private String name = "< Man At Arms >";
			
			@Override
			public void execute(GamePanel panel) {
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
		item = new MenuItem() {
			private String name = "< Archer >";
			
			@Override
			public void execute(GamePanel panel) {
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
		item = new MenuItem() {
			private String name = "< Knight >";
			
			@Override
			public void execute(GamePanel panel) {
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
    	menuItems.add(exitMenuItem());
		return menuItems;
	}

	private List<MenuItem> getHeroMenu(Hero hero) {
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
		MenuItem item = new MenuItem() {
			private String name = "Upgrade Stats";
			
			@Override
			public void execute(GamePanel panel) {
				RegionMenuPanel menuPanel = (RegionMenuPanel) panel;
				menuPanel.openHeroStatsMenu(hero);
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
		item = new MenuItem() {
			private String name = "Upgrade Skills";
			
			@Override
			public void execute(GamePanel panel) {
				RegionMenuPanel menuPanel = (RegionMenuPanel) panel;
				menuPanel.openHeroSkillsMenu(hero);			
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
		item = new MenuItem() {
			private String name = "Upgrade Equipment";
			
			@Override
			public void execute(GamePanel panel) {
				RegionMenuPanel menuPanel = (RegionMenuPanel) panel;
				menuPanel.openHeroEquipmentMenu(hero);			
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
    	menuItems.add(exitMenuItem());
		return menuItems;
	}
	
	
	private List<MenuItem> getHeroStatsMenu(Hero hero) {
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
		MenuItem item = new MenuItem() {
			private String name = "Increase HP";
			
			@Override
			public void execute(GamePanel panel) {
				RegionMenuPanel menuPanel = (RegionMenuPanel) panel;
//				menuPanel.setHero(hero);
	        	int level = hero.getStatLevel("HP");
	        	int cost = calculateStatCost(level);
				if (hero.getStatPoints() >= cost) {
					hero.subtractStatPoints(cost);
	        		hero.levelStat("HP");
				}
	        	
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
		item = new MenuItem() {
			private String name = "Increase MP";
			
			@Override
			public void execute(GamePanel panel) {
				RegionMenuPanel menuPanel = (RegionMenuPanel) panel;
//				menuPanel.setHero(hero);
	        	int level = hero.getStatLevel("MP");
	        	int cost = calculateStatCost(level);
				if (hero.getStatPoints() >= cost) {
					hero.subtractStatPoints(cost);
	        		hero.levelStat("MP");
				}
	        	
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
		item = new MenuItem() {
			private String name = "Increase Strength";
			
			@Override
			public void execute(GamePanel panel) {
				RegionMenuPanel menuPanel = (RegionMenuPanel) panel;
//				menuPanel.setHero(hero);
	        	int level = hero.getStatLevel("Strength");
	        	int cost = calculateStatCost(level);
				if (hero.getStatPoints() >= cost) {
					hero.subtractStatPoints(cost);
					hero.levelStat("Strength");
				}
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
		item = new MenuItem() {
			private String name = "Increase Magic";
			
			@Override
			public void execute(GamePanel panel) {
				RegionMenuPanel menuPanel = (RegionMenuPanel) panel;
//				menuPanel.setHero(hero);
	        	int level = hero.getStatLevel("Magic");
	        	int cost = calculateStatCost(level);
				if (hero.getStatPoints() >= cost) {
					hero.subtractStatPoints(cost);
					hero.levelStat("Magic");
				}
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
		item = new MenuItem() {
			private String name = "Increase Stamina";
			
			@Override
			public void execute(GamePanel panel) {
				RegionMenuPanel menuPanel = (RegionMenuPanel) panel;
//				menuPanel.setHero(hero);
	        	int level = hero.getStatLevel("Stamina");
	        	int cost = calculateStatCost(level);
				if (hero.getStatPoints() >= cost) {
					hero.subtractStatPoints(cost);
					hero.levelStat("Stamina");
				}
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
    	menuItems.add(exitMenuItem());
		return menuItems;
	}

	private List<MenuItem> getHeroSkillsMenu(Hero hero) {
		List<Ability> skills = hero.getSkillList();
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
//    	List<MenuItem> menuItems = new ArrayList<MenuItem>(abilities);
		for (Ability skill: skills) {
			MenuItem item = new MenuItem() {
				private String name = skill.getName();
				
				@Override
				public void execute(GamePanel panel) {
					RegionMenuPanel menuPanel = (RegionMenuPanel) panel;
		        	int level = skill.getLevel();
		        	int cost = skill.calculateSkillCost(level);
					if (hero.getStatPoints() >= cost && skill.getLevel() < skill.getMaxLevel()) {
						hero.subtractStatPoints(cost);
						skill.levelUp();
						hero.updateStats();
					}
				}

				@Override
				public String getName() {
					return name;
				}
			};
			menuItems.add(item);
		}
    	menuItems.add(exitMenuItem());
		return menuItems;
	}
	
	private List<MenuItem> getHeroEquipmentMenu(Hero hero) {
		List<Equipment> equipmentList = hero.getEquipmentList();
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
//    	List<MenuItem> menuItems = new ArrayList<MenuItem>(abilities);
		for (Equipment equipment: equipmentList) {
			MenuItem item = new MenuItem() {
				private String name = equipment.getName();
				
				@Override
				public void execute(GamePanel panel) {
					RegionMenuPanel menuPanel = (RegionMenuPanel) panel;
					Party party = ((RegionPanelManager)menuPanel.getManager()).getRegionMap().getParty();
		        	int level = (int)equipment.get("Level");
		        	int cost = equipment.calculateUpgradeCost(level);
					if (party.getGold() >= cost && (int)equipment.get("Level") < (int)equipment.get("Max Level")) {
						party.subtractGold(cost);
						hero.equip(equipment);
						equipment.upgrade(hero);
						hero.updateStats();
					}
				}

				@Override
				public String getName() {
					return name;
				}
			};
			menuItems.add(item);
		}
    	menuItems.add(exitMenuItem());
		return menuItems;
	}
	
	
	public void setHero(Hero hero) {
		this.hero = hero;
	}

	public void setHeroSelectMenu(boolean bool) {
		this.heroSelectMenu = bool;
	}
	
	public void setHeroStatsMenu(boolean bool) {
		this.heroStatsMenu = bool;
	}

	public void setHeroSkillsMenu(boolean bool) {
		this.heroSkillsMenu = bool;
	}
	
	public void setHeroEquipmentMenu(boolean bool) {
		this.heroEquipmentMenu = bool;
	}

	public void setTroopMenu(boolean bool) {
		this.troopMenu = bool;
	}

	public void setPartyManagementMenu(boolean bool) {
		this.partyManagementMenu = bool;
	}
	
	public void setParty(Party party) {
		this.party = party;
	}
}
