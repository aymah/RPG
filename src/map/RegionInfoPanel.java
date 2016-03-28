package map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class RegionInfoPanel extends LayeredPanel {

	private RegionPanelManager manager;
	private String text = "";
	
	public RegionInfoPanel(String name, GameFrame frame, RegionPanelManager manager) {
		super(1);
		frame.add(this);
		manager.setInfoPanel(this);
		this.manager = manager;
		this.setBounds(0, GraphicsConstants.REGION_MAP_HEIGHT, GraphicsConstants.INFO_PANEL_WIDTH, GraphicsConstants.INFO_PANEL_HEIGHT);
		this.name = name;
		this.frame = frame;
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
    }
    
    public void displayText(String text) {
    	this.text = text;
    	this.repaint();
    }


	public void restore() {
		frame.add(this);
	}
}
