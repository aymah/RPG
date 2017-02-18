package map;

import java.util.Comparator;

import event.Effect;

public class Coordinates {
	
	int y;
	int x;
	
	public Coordinates(int y, int x) {
		this.y = y;
		this.x = x;
	}
	
//	public Coordinates(int y, int x, Coordinates prev) {
//		this.y = y;
//		this.x = x;
//		this.prev = prev;
//	}

	public int getY() {
		return y;
	}

	public int getX() {
		return x;
	}

	public static Comparator<Coordinates> getComparator() {
		Comparator<Coordinates> comparator = new Comparator<Coordinates>() {
			@Override
			public int compare(Coordinates o1, Coordinates o2) {
				if (o1.getY() != o2.getY()) 
					return o1.getY() - o2.getY();
				return o1.getX() - o2.getX();
			}
		};
		return comparator;
	}
	
//	public Coordinates getPrev() {
//		return prev;
//	}
}
