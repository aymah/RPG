package event;

import java.io.Serializable;

import map.GameFrame;
import map.GamePanel;

public interface Event extends Serializable {
		
	//override this function
	void execute(GamePanel panel);
}
