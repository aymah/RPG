package map;

public class PathCoordinates extends Coordinates {

	private int length;
	private int value;
	
	public PathCoordinates(int y, int x, int length, int value) {
		super(y, x);
		this.length = length;
		this.value = value;
	}

	public int getLength() {
		return length;
	}
	
	public int getValue() {
		return value;
	}
}
