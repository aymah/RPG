package map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class InfoPanel extends GamePanel {

	private GameFrame frame;
	private ExplorePanelManager explorer;
	private String text = "";
	
	public InfoPanel(String name, GameFrame frame, ExplorePanelManager explorer) {
		frame.add(this);
		explorer.setInfoPanel(this);
		this.explorer = explorer;
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
        g2d.fillRect(0, 0, GraphicsConstants.FRAME_WIDTH, GraphicsConstants.FRAME_HEIGHT);
        g2d.setColor(new Color(255,255,255));
        g2d.drawString(text, 10, 20);
    }
    
    public void displayText(String text) {
    	this.text = text;
    	this.repaint();
    }
}
