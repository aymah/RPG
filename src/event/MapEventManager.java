package event;

import java.util.ArrayList;
import java.util.List;

import map.GamePanel;

public class MapEventManager implements Event {

	private List<MapEvent> events;
	private int currentEventIndex;
	private boolean active;
	
	public MapEventManager() {
		events = new ArrayList<MapEvent>();
		currentEventIndex = 0;
		active = true;
	}
	
	public void addEvent(MapEvent event) {
		events.add(event);
		event.setEventManager(this);
	}
	
	public void setEventIndex(int i) {
		currentEventIndex = i;
	}
	
	public void setActive(boolean bool) {
		active = bool;
	}
	
	@Override
	public void execute(GamePanel panel) {
		if (active)
			events.get(currentEventIndex).execute(panel);
	}

	public Event getCurrentEvent() {
		return events.get(currentEventIndex);
	}

}
