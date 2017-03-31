package event;

import map.Coordinates;
import map.GamePanel;

public class Province extends MapEvent {
	
	private String name;
	private int goldIncome;
	private int manpowerIncome;
	private String owner;
	private Coordinates coordinates;
	private int[][] area;
	
	public Province(String name, int goldIncome, int manpowerIncome, String owner, Coordinates coordinates, int[][] area) {
		super("ACTIVATION", null);
		this.name = name;
		this.goldIncome = goldIncome;
		this.manpowerIncome = manpowerIncome;
		this.owner = owner;
		this.coordinates = coordinates;
		this.area = area;
	}

	public String getName() {
		return name;
	}

	public int getGoldIncome() {
		return goldIncome;
	}

	public int getManpowerIncome() {
		return manpowerIncome;
	}
	
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public String getOwner() {
		return owner;
	}

	public int getX() {
		return coordinates.getX();
	}
	
	public int getY() {
		return coordinates.getY();
	}
	
//	public int[][] getArea() {
//		return area;
//	}
	
	public Coordinates getCenter() {
		Coordinates coordinates = null;
		for (int y = 0; y < area.length; y++) {
			for (int x = 0; x < area[y].length; x++) {
				if (area[y][x] == 2)
					coordinates = new Coordinates(y, x);
			}
		}
		return coordinates;
	}
	
	public boolean isInArea(int y, int x) {
		if (area[y][x] > 0)
			return true;
		return false;
	}
	
	public int getHeight() {
		return area.length;
	}
	
	public int getWidth() {
		return area[0].length;
	}
	
	@Override
	public void execute(GamePanel panel) {
		// TODO Auto-generated method stub
		
	}
}
