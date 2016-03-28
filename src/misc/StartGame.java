package misc;

import javax.swing.JFrame;

import map.GameFrame;
import map.GraphicsConstants;
import map.MapTest;
import map.RegionMenuPanel;
import map.RegionPanelManager;

public class StartGame {
	static GameFrame frame = null;

	public static void main(String[] args) throws InterruptedException {
		JFrame f = new JFrame();
		frame = new GameFrame();
        StartPanelManager manager = new StartPanelManager(frame);
//		RegionPanelManager manager = new RegionPanelManager(frame);
	    frame.setLayout(null);
	    
        StartMenuPanel menuPanel = new StartMenuPanel("Start Game Screen", frame, manager, StartMenuPanel.getStandardMenu(), 1);
//	    RegionMenuPanel menuPanel = new RegionMenuPanel("test", frame, manager, RegionMenuPanel.getStandardMenu(), 1);
        manager.setDominantPanel(menuPanel);
        menuPanel.displayPanel();
//		frame.refresh();

        StartGame m = new StartGame();
        m.setup(f);
    }
    
    public void setup(JFrame f) {
        f.add(frame);
        f.setSize(GraphicsConstants.FRAME_WIDTH, GraphicsConstants.FRAME_HEIGHT);
        f.setVisible(true);
        frame.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.addKeyListener(frame);
    
	}
}
