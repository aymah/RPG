package map;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Serializable;
import java.util.Stack;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class GameFrame extends JLayeredPane implements KeyListener {

	GamePanel gamePanel;
	Stack<KeyEvent> eventStack = new Stack<KeyEvent>();
//	private Stack<Boolean> keyHeld = new Stack<Boolean>();
	transient ExecutorService es = Executors.newSingleThreadExecutor();
	transient ExecutorService waitingThreadPool = Executors.newFixedThreadPool(10);
	private boolean acceptingInput = true;
	private KeyEvent nextInput;
	Runnable runnable = new Runnable() {
	
		@Override
		public void run() {
			if (eventStack.size() > 0)
				gamePanel.keyPressed(eventStack.peek());
		}
	};
	
	Runnable runNextInput = new Runnable() {
		
		@Override
		public void run() {
			if (nextInput != null)
				gamePanel.keyPressed(nextInput);
			nextInput = null;
		}
	};
	
	Runnable wait = new Runnable() {
		
		@Override
		public void run() {
			if (eventStack.size() > 0)
				waitingTimer(eventStack.peek());
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
		if (!containsKeyEvent(e)) {
			if (eventStack.size() > 0) {
				nextInput = e;
			}
			eventStack.add(e);
			waitingThreadPool.execute(wait);
		}
		
//		if (acceptingInput) {
//			this.e = e;
//			es.execute(runnable);
//		} else {
//			nextInput = e;
//		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		for (KeyEvent keyEvent: eventStack) {
			if (keyEvent.getKeyCode() ==  e.getKeyCode()) {
				eventStack.remove(keyEvent);
				break;
			}
		}
	}
	
	public void setAcceptingInput(boolean bool) {
		acceptingInput = bool;
		if (bool && nextInput != null) {
//			e = nextInput;
			es.execute(runNextInput);
		}
	}
	
	public void waitingTimer(KeyEvent keyEvent) {
		CountDownLatch cdl = new CountDownLatch(1);
	    Timer timer = new Timer(0, new ActionListener() {
		   
		    int i = 17;
		    
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            if (!containsKeyEvent(keyEvent)) {
	            	end(e);
	            } else {
	    			if (i > 16 && acceptingInput && keyEvent.getKeyCode() == eventStack.peek().getKeyCode()) {
	    				i = 0;
	    				es.execute(runnable);
	    			}
	            }
	            i++;
	        }
	        
	        private void end(ActionEvent e) {
                cdl.countDown();
                ((Timer)e.getSource()).stop();
	        }
	    });
	    timer.setRepeats(true);
	    timer.setDelay(10);
	    timer.start();
	    try {
			cdl.await();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
	
	private boolean containsKeyEvent(KeyEvent e) {
		for (KeyEvent keyEvent: eventStack) {
			if (keyEvent.getKeyCode() == e.getKeyCode())
				return true;
		}
		return false;
	}
	
	public boolean hasNextInput() {
		if (eventStack.size() > 0) return true;
		return false;
	}
}
