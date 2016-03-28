package map;

public class PotentialMove {
	
	private int potential;
	private int y;
	private int x;
	
	public PotentialMove(int potential, int y, int x) {
		this.potential = potential;
		this.y = y;
		this.x = x;
	}

	public int getPotential() {
		return potential;
	}

	public int getY() {
		return y;
	}

	public int getX() {
		return x;
	}
}
