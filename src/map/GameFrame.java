package map;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class GameFrame extends JLayeredPane implements KeyListener{

	GamePanel gamePanel;
	KeyEvent e;
	ExecutorService es = Executors.newSingleThreadExecutor();
	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			gamePanel.keyPressed(e);
		}
	};

//	public void init() {
//        initKeyListeners();
//	}
	
	public void refresh() {
		repaint();
        setVisible(true);
	}
	
//    public void initKeyListeners() {
//    	this.addKeyListener(this);
//    }
	
    public void setPanel(GamePanel gamePanel) {
    	this.gamePanel = gamePanel;
    }
    
//    public void setTestPanel(String panelName) {
//    	GenericMap map = new RegionMap(panelName, this);
//    	map.loadMap(panelName);
//    	this.add(map);
//    }
    
//    public GamePanel getPanel() {
//    	return gamePanel;
//    }
    
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

//	@Override
//	public void keyPressed(KeyEvent e) {
//		System.out.println(gamePanel.getName());
//		gamePanel.keyPressed(e);		
//	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		this.e = e;
		es.execute(runnable);
//		gamePanel.keyPressed(e);		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
