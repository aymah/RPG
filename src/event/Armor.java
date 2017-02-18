package event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Armor extends Equipment {

	protected int durability;
	protected int maxDurability;
	protected double maxAbsorption;
	protected int weight;
	
	public Armor(ItemFactory itemFactory) {
		super(itemFactory);
		this.maxDurability = (int)params.get("Durability");
		this.durability = maxDurability;
		this.maxAbsorption = (float)params.get("Absorption");
		this.weight = (int)params.get("Weight");
	}
 	
	public Armor(Map<String, Object> params, Map<String, List<Effect>> effects) {
		super(params, effects);
		this.maxDurability = (int)params.get("Durability");
		this.durability = maxDurability;
		this.maxAbsorption = (float)params.get("Absorption");
		this.weight = (int)params.get("Weight");
	}
	
	public Armor(Map<String, Object> params, List<Effect> effects) {
		super(params, effects);
		this.maxDurability = (int)params.get("Durability");
		this.durability = maxDurability;
		this.maxAbsorption = (double)params.get("Absorption");
		this.weight = (int)params.get("Weight");
	}

	public Armor(Equipment equip) {
		super(equip);
		this.maxDurability = (int)params.get("Durability");
		this.durability = maxDurability;
		this.maxAbsorption = (double)params.get("Absorption");
		this.weight = (int)params.get("Weight");
	}

	public int getDurability() {
		return durability;
	}
	
	public int getMaxDurability() {
		return maxDurability;
	}
	
	public void subtractDurability(int damage) {
		durability -= damage;
		if (durability < 0)
			durability = 0;
	}
	
	public double getAbsorption() {
		return ((double)durability/(double)maxDurability) * maxAbsorption;
	}
	
	public double getMaxAbsorption() {
		return maxAbsorption;
	}
	
	public int getWeight() {
		return weight;
	}
	
	public void resetDurability() {
		durability = maxDurability;
	}
}
