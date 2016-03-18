package map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class TestPanel extends GamePanel{
	
	int color = 0;
	
	public TestPanel(int color) {
		this.color = color;
	}
	
	@Override
	public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        System.out.println(color);
        Color bgColor = new Color(0,255,color);
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, GraphicsConstants.REGION_MAP_WIDTH, GraphicsConstants.REGION_MAP_HEIGHT);
    }
}
