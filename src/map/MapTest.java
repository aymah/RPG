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

@SuppressWarnings("serial")
public class MapTest {
	static GameFrame frame = null;

    int x = 0;
    int y = 0;

    public static void main(String[] args) throws InterruptedException {
    	JFrame f = new JFrame();
        frame = new GameFrame();
        ExplorePanelManager explorer = new ExplorePanelManager(frame);

//        InfoPanel infoPanel = new InfoPanel("testInfoPanel", frame);

        frame.setLayout(null);
        RegionMap panel = new RegionMap("testMap", frame, explorer);
        panel.setCoordinates(1,1);
        InfoPanel infoPanel = new InfoPanel("testInfoPanel", frame, explorer);
        RegionMenuPanel menuPanel = new RegionMenuPanel("testMenuPanel", frame, explorer, RegionMenuPanel.getStandardMenu(), 1);
        explorer.setDominantPanel(panel);

//        JLayeredPane mainPanel = new JLayeredPane();
//        mainPanel.setLayout(null);
//        TestPanel testPanel1 = new TestPanel(0);
//        testPanel1.setBounds(0, 0, 800, 420);
//        mainPanel.add(testPanel1);        
//        TestPanel testPanel2 = new TestPanel(255);
//        testPanel2.setBounds(0, 420, 800, 180);
//        mainPanel.add(testPanel2);
//        TestPanel testPanel3 = new TestPanel(175);
//        testPanel3.setBounds(100, 100, 500, 420);
//        mainPanel.add(testPanel3, new Integer(1), 0);
//        frame.add(mainPanel);
        

        MapTest m = new MapTest();
        m.setup(f);
    }
    
    void setup(JFrame f) {
        f.add(frame);
        f.setSize(GraphicsConstants.FRAME_WIDTH, GraphicsConstants.FRAME_HEIGHT);
        f.setVisible(true);
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
