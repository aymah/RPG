package event;

import map.GameFrame;
import map.GamePanel;
import map.GenericMap;
import map.RegionMap;

public class Corridor extends MapEvent {

		private String destination;
		private int destIndexY;
		private int destIndexX;
		
		public Corridor(String destination, int destIndexY, int destIndexX, String activationMethod, Switch s) {
			super(activationMethod, s);
			this.destination = destination;
			this.destIndexY = destIndexY;
			this.destIndexX = destIndexX;
		}
		
		@Override
		public void execute(GamePanel panel) {
			RegionMap map = (RegionMap) panel;
			map.takeCorridor(this);
		}
		
		public String getDestination() {
			return destination;
		}
		
		public int getDestIndexY() {
			return destIndexY;
		}
		
		public int getDestIndexX() {
			return destIndexX;
		}
}
