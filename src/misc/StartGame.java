package misc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;

import javax.sound.sampled.*;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import map.GameFrame;
import map.GraphicsConstants;
import map.MapTest;
import map.ExploreMenuPanel;
import map.RegionPanelManager;

public class StartGame {
	static GameFrame frame = null;

	public static void main(String[] args) throws InterruptedException {
		File dir = new File(System.getProperty("user.home")+"/saves");
		dir.mkdir();
//		try {
//			System.setErr(new PrintStream(new FileOutputStream(System.getProperty("user.home")+"/error.log")));
//		} catch (FileNotFoundException ex) {
//			ex.printStackTrace();
//		}
		
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame f = new JFrame();

		
		frame = new GameFrame();
        StartPanelManager manager = new StartPanelManager(frame);
//		RegionPanelManager manager = new RegionPanelManager(frame);
	    frame.setLayout(null);
        StartMenuPanel menuPanel = new StartMenuPanel("Start Game Screen", frame, manager, StartMenuPanel.getStandardMenu(), 1);
        manager.setDominantPanel(menuPanel);
        menuPanel.displayPanel();

        StartGame m = new StartGame();
        m.setup(f);
    }
    
    public void setup(JFrame f) {
        f.add(frame);
        f.setSize(GraphicsConstants.FRAME_WIDTH + 10, GraphicsConstants.FRAME_HEIGHT + 32);
        f.setVisible(true);
        frame.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.addKeyListener(frame);    
        f.setFocusTraversalKeysEnabled(false);
	}
}
