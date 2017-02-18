package event;

import map.GamePanel;

public class Province extends MapEvent {
	
	private String name;
	private int goldIncome;
	private int manpowerIncome;
	
	public Province(String name, int goldIncome, int manpowerIncome) {
		super("ACTIVATION", null);
		this.name = name;
		this.goldIncome = goldIncome;
		this.manpowerIncome = manpowerIncome;
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

	@Override
	public void execute(GamePanel panel) {
		// TODO Auto-generated method stub
		
	}
}
