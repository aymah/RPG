package event;

import map.GameFrame;
import map.GamePanel;

public interface Event {

	
//	public default String getType() {
//		return type;
//	}
	
	//override this function
	void execute(GamePanel panel);
}
