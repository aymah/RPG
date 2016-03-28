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

import unit.Hero;
import unit.Party;
import unit.Unit;
import event.GenericMenuItem;
import event.MenuItem;

public class VictoryRewardsPanel extends MenuPanel{

	private int selectorIndexX;
	private int selectorIndexY;
	private int expReward;
	private int goldReward;
	private int levelDiff = 0;
	
	public VictoryRewardsPanel(String name, GameFrame frame, BattlePanelManager manager, List<MenuItem> menuItems, int layer) {
		super(menuItems, layer);
		manager.setVictoryPanel(this);
		this.manager = manager;
		this.setBounds(100, 100, 600, 400);
		this.name = name;
		this.frame = frame;
		this.selectorIndexX = 0;
		this.selectorIndexY = 0;
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
        drawVictoryRewards(g2d);
    }
    
    private void drawMenu(Graphics2D g2d) {
    	for (int i = 0; i < menuItems.size(); i++) {
            g2d.setColor(new Color(255,255,255));
            if (selectorIndexY == i)
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
    	
		BattlePanelManager manager = (BattlePanelManager)this.manager;
		Party party = manager.getBattleMap().getParty();
		Hero hero = (Hero)party.getUnit(0);
		
		if (levelDiff == 1)
			g2d.drawString("Level Up!", 300, 105);
		else if (levelDiff > 1) 
			g2d.drawString("Level Up! x" + levelDiff, 300, 105);
		g2d.drawString("Current Level: " + hero.getLevel(), 300, 130);
		g2d.drawString("Current EXP: " + hero.getExp(), 300, 155);
		g2d.drawString("EXP to next Level: " + hero.getToNextLevel(), 300, 180);
		g2d.drawString("Stat Points: " + hero.getStatPoints(), 300, 205);
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
			case 84:
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
	
	private void selectItem() {
		menuItems.get(selectorIndexY).execute(this);
	}

	public static List<MenuItem> getStandardMenu() {
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
		MenuItem item = new MenuItem() {
			private String name = "Close";
			
			@Override
			public void execute(GamePanel panel) {
				VictoryRewardsPanel menuPanel = (VictoryRewardsPanel) panel;
				BattlePanelManager manager = (BattlePanelManager)(menuPanel.getManager());
				manager.getBattleMap().endBattle();
			}

			@Override
			public String getName() {
				return name;
			}
		};
		menuItems.add(item);
		return menuItems;
	}


	public void setExpReward(int expReward) {
		this.expReward = expReward;
	}


	public void setGoldReward(int goldReward) {
		this.goldReward = goldReward;
	}


	public void executeRewards() {
		System.out.println("executing rewards");
		BattlePanelManager manager = (BattlePanelManager)this.manager;
		Party party = manager.getBattleMap().getParty();
		party.addGold(goldReward);
		for (Hero hero: party.getHeroList()) {
			int level = hero.getLevel();
			hero.addExp(expReward);
			levelDiff = hero.getLevel() - level;
		}
	}
}

