package map;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.List;

import unit.Empire;
import unit.Hero;
import unit.Party;
import unit.Unit;
import event.GenericMenuItem;
import event.MenuItem;

public class SimVictoryRewardsPanel extends MenuPanel{

	private List<String> battleLog;
	private int selectorIndexX;
	private int selectorIndexY;
	private int expReward;
	private int goldReward;
	private String techReward;
	private String provReward;
	private int levelDiff = 0;
	private static final int BATTLE_LOG_LINE_DISPLAY_NUM = 20;
	
	public SimVictoryRewardsPanel(String name, GameFrame frame, EmpirePanelManager manager, List<MenuItem> menuItems, int layer) {
		super(menuItems, layer);
		manager.setSimVictoryPanel(this);
		this.manager = manager;
		this.setBounds(0, 0, GraphicsConstants.FRAME_WIDTH, GraphicsConstants.FRAME_HEIGHT);
		this.name = name;
		this.frame = frame;
		expReward = 0;
		goldReward = 0;
		techReward = null;
		this.selectorIndexX = 0;
		this.selectorIndexY = 0;
	}

	
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        Color bgColor = new Color(0,0,0);
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, GraphicsConstants.FRAME_WIDTH, GraphicsConstants.FRAME_HEIGHT);
        drawMenu(g2d);
        drawVictoryRewards(g2d);
        drawBattleLog(g2d);
    }
    
    private void drawMenu(Graphics2D g2d) {
    	for (int i = 0; i < menuItems.size(); i++) {
//            g2d.setColor(new Color(255,255,255));
//            if (selectorIndexY == i)
            g2d.setColor(new Color(255,0,0));
    		g2d.drawString(menuItems.get(i).getName(), 10, 390 + i * 30);
    	}
    }
    
    private void drawVictoryRewards(Graphics2D g2d) {
//    	TextAttribute attribute = new TextAttribute("test");
    	g2d.setFont(new Font("Dialog", Font.PLAIN, 40));
    	g2d.setColor(new Color(255,255,255));
    	g2d.drawString("Victory!", 225, 40);
    	g2d.setFont(new Font("Dialog", Font.PLAIN, 16));
    	g2d.drawString("Rewards:", 100, 80);
    	g2d.drawString("EXP: " + expReward , 100, 105);
    	g2d.drawString("GOLD: " + goldReward , 100, 130);
    	
		EmpirePanelManager manager = (EmpirePanelManager)this.manager;
		Party party = manager.getMap().getParty();
		Hero hero = party.getHeroList().get(0);
		
		if (levelDiff == 1)
			g2d.drawString("Level Up!", 300, 105);
		else if (levelDiff > 1) 
			g2d.drawString("Level Up! x" + levelDiff, 300, 105);
		g2d.drawString("Current Level: " + hero.getLevel(), 300, 130);
		g2d.drawString("Current EXP: " + hero.getExp(), 300, 155);
		g2d.drawString("EXP to next Level: " + hero.getToNextLevel(), 300, 180);
		g2d.drawString("Stat Points: " + hero.getStatPoints(), 300, 205);
		
    }
    
    private void drawBattleLog(Graphics2D g2d) {
    	g2d.setFont(new Font("Dialog", Font.PLAIN, 16));
    	g2d.setColor(new Color(255,255,255));
    	for (int i = selectorIndexY; i < selectorIndexY + BATTLE_LOG_LINE_DISPLAY_NUM; i++) {
    		g2d.drawString(battleLog.get(i), 550, 40 + (i - selectorIndexY) * 20);
    	}
    	
    	
		g2d.setColor(new Color(255,255,255));
		g2d.fillRect(525, 20, 4, 20 * BATTLE_LOG_LINE_DISPLAY_NUM);
		g2d.setColor(new Color(255,0,0));
		g2d.fillRect(525, (int) (20 + (20 * (BATTLE_LOG_LINE_DISPLAY_NUM - 1) * ((double)selectorIndexY/(double)(battleLog.size() - BATTLE_LOG_LINE_DISPLAY_NUM)))), 4, 20);
    }
    
	public void keyPressed(KeyEvent e) {
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
				selectItem();
				break;
		}
		this.repaint();
	}
	
	private void moveUp() {
		selectorIndexY--;
		if (selectorIndexY < 0)
			selectorIndexY = 0;
	}
	
	private void moveDown() {
		selectorIndexY++;
		if (selectorIndexY >= battleLog.size() - BATTLE_LOG_LINE_DISPLAY_NUM)
			selectorIndexY = battleLog.size() - BATTLE_LOG_LINE_DISPLAY_NUM;
	}
	
	private void selectItem() {
		menuItems.get(selectorIndexY).execute(this);
	}

	public static List<MenuItem> getStandardMenu() {
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
		menuItems.add(MenuPanel.exitMenuItem());
		return menuItems;
	}


	public void setExpReward(int expReward) {
		this.expReward = expReward;
	}


	public void setGoldReward(int goldReward) {
		this.goldReward = goldReward;
	}
	
	public void setTechReward(String techReward) {
		this.techReward = techReward;
	}

	public void setProvReward(String provReward) {
		this.provReward = provReward;
	}

	public void executeRewards() {
		System.out.println("executing rewards");
		EmpirePanelManager manager = (EmpirePanelManager)this.manager;
		Party party = manager.getMap().getParty();
		Empire empire = party.getEmpire();
		party.addGold(goldReward);
		if (techReward != null)
			empire.readTechnology(techReward);
		if (provReward != null)
			empire.readProvince(provReward);
		for (Hero hero: party.getHeroList()) {
			int level = hero.getLevel();
			hero.addExp(expReward);
			levelDiff = hero.getLevel() - level;
		}
	}
	
	public void setBattleLog(List<String> battleLog) {
		this.battleLog = battleLog;
	}
	
}

