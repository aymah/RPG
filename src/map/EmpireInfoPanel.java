package map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import event.Province;
import unit.Empire;
import unit.Party;

public class EmpireInfoPanel extends LayeredPanel {

	private EmpirePanelManager manager;
	private String text = "";
	private Province province;
	
	public EmpireInfoPanel(String name, GameFrame frame, EmpirePanelManager manager) {
		super(1);
		frame.add(this);
		manager.setInfoPanel(this);
		this.manager = manager;
		this.setBounds(0, GraphicsConstants.REGION_MAP_HEIGHT, GraphicsConstants.INFO_PANEL_WIDTH, GraphicsConstants.INFO_PANEL_HEIGHT);
		this.name = name;
		this.frame = frame;
		this.province = null;
	}

	
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        Color bgColor = new Color(0,100,100);
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, GraphicsConstants.INFO_PANEL_WIDTH, GraphicsConstants.INFO_PANEL_HEIGHT);
        g2d.setColor(new Color(255,255,255));
        g2d.drawString(text, 10, 20);
        drawResources(g2d);
        if (province != null)
        	drawProvinceInfo(g2d);
    }
    
    private void drawResources(Graphics g2d) {
		Party party = manager.getMap().getParty();
		Empire empire = party.getEmpire();
        g2d.drawString("Gold: "+ party.getGold() + "G (+" + empire.getGoldIncome() + ")", 200, 200);
        g2d.drawString("Manpower: "+ empire.getManpower() + " (+" + empire.getManpowerIncome() + ")", 200, 220);
        g2d.drawString("Date: "+ empire.getDate().getDateString(), 200, 240);
	}

    private void drawProvinceInfo(Graphics g2d) {
    	g2d.drawString("Gold Income: " + province.getGoldIncome(), 400, 200);
    	g2d.drawString("Manpower Income: " + province.getManpowerIncome(), 400, 220);
    }

	public void displayText(String text) {
    	this.text = text;
    	this.repaint();
    }
	
	public void displayProvinceInfo(Province province) {
        this.province = province;
	}

	public void clearProvince() {
		province = null;
	}
	
	public void restore() {
		frame.add(this);
	}
}
