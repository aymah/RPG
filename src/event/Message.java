package event;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;

import unit.Unit;
import map.BattlePanelManager;
import map.GameFrame;
import map.GamePanel;
import map.GraphicsConstants;
import map.LayeredPanel;
import map.PanelManager;
import map.GenericMap;
import map.RegionPanelManager;

public class Message extends MapEvent {

	private String text;
	
	public Message(String text, String activationMethod, Switch s) {
		super(activationMethod, s);
		this.text = text;
	}

	@Override
	public void execute(GamePanel panel) {
		PanelManager manager = ((GenericMap)panel).getManager();
		MessagePanel messagePanel = new MessagePanel(text, manager.getFrame(), manager);
		if (!s.getType().equals("PERMANENT")) {
			if (s.getCondition().equals("COMPLETE")) {
				if (s.getType().equals("CHANGE"))
					this.mapEventManager.setEventIndex(s.getChangeIndex());
			}
		}
	}
	
	public class MessagePanel extends LayeredPanel {

		PanelManager manager;
		Unit currUnit;
		
		public MessagePanel(String name, GameFrame frame, PanelManager manager) {
			super(1);
			manager.setAuxillaryPanel(this);
			manager.changeDominantPanel(this);
			this.manager = manager;
			this.setBounds(100, 100, 600, 400);
			this.name = name;
			this.frame = frame;
			displayPanel();
		}
		
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
								 RenderingHints.VALUE_ANTIALIAS_ON);
	        Color bgColor = new Color(100,100,0);
			g2d.setColor(bgColor);
			g2d.fillRect(0, 0, 600, 400);
			displayText(g2d);
		}
		
		private void displayText(Graphics2D g2d) {
			g2d.setColor(new Color(255,255,255));
		    GraphicsConstants.drawParagraph(g2d, text, 500, 20, 7);

		}
		
		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();
			switch (keyCode) {
//				case 69:
//				case 10:
//					selectItem();
//					break;
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

}
