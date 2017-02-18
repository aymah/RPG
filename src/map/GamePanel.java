package map;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Serializable;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements Serializable {

	protected transient GameFrame frame;
	protected String name;
		
	public String getName() {
		return name;
	}
	
	public GameFrame getFrame() {
		return frame;
	}
	
	public void setFrame(GameFrame frame) {
		this.frame = frame;
	}
	
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
