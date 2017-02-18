package event;

import java.util.Map;

public class Consumable implements Item{

	Map<String, Object> params;
	
	public Consumable(ItemFactory itemFactory) {
		this.params = itemFactory.getParams();
	}
	
	@Override
	public String getType() {
		return (String)params.get("Type");
	}

	@Override
	public Object get(String key) {
		return params.get(key);
	}

	@Override
	public String getName() {
		return (String)params.get("Name");
	}

}
