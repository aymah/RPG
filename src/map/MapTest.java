package map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.TimeUnit;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import unit.Hero;
import unit.Party;

@SuppressWarnings("serial")
public class MapTest {
	static GameFrame frame = null;

    int x = 0;
    int y = 0;

    public static void main(String[] args) throws InterruptedException {
    	JFrame f = new JFrame();
    	frame = new GameFrame();
        RegionPanelManager manager = new RegionPanelManager(frame);
        frame.setLayout(null);
        Party party = new Party();
        Hero hero = new Hero("testHero1", "ALLY", "H1", party);
        party.addUnit(hero);
//        hero = new Hero("testHero1", "ALLY", "H2", 0);
//        party.addUnit(hero);
        RegionMap panel = new RegionMap("testMap", frame, manager, party);
        panel.setCoordinates(1,1);
        RegionInfoPanel infoPanel = new RegionInfoPanel("testInfoPanel", frame, manager);
        ExploreMenuPanel menuPanel = new ExploreMenuPanel("testMenuPanel", frame, manager, null, 1, ExploreMenuPanel.getStandardMenu(), 0);
        manager.setDominantPanel(panel);


        MapTest m = new MapTest();
        m.setup(f);
    }
    
    void setup(JFrame f) {
        f.add(frame);
        f.setSize(GraphicsConstants.FRAME_WIDTH, GraphicsConstants.FRAME_HEIGHT);
        f.setVisible(true);
        frame.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.addKeyListener(frame);
    }
//    
//    class Test extends GamePanel {
//    	
//    	public Test(String name, GameFrame frame) {
//    		frame.add(this);
//    		frame.setPanel(this);
//    		this.name = name;
//    	}
//    	
//    	@Override
//    	public void paint(Graphics g) {
//    		System.out.println("this works?");
//            super.paint(g);
//            Graphics2D g2d = (Graphics2D) g;
//            Color c = new Color(255, 0, 0);
//            g2d.setColor(c);
//            g2d.fillOval(x, y, 30, 30);
//    	}
//    }
}
