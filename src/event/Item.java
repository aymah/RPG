package event;

import java.io.Serializable;
import java.util.Map;

import map.GamePanel;

public interface Item extends Serializable{

//	Map<String, Object> params;
	
//	public Item(ItemFactory itemFactory) {
//		this.params = itemFactory.getParams();
//	}
	
	public String getType();
//	public String getType() {
//		return (String)params.get("Type");
//	}
	public Object get(String key);
//	public Object get(String key) {
//		return params.get(key);
//	}
	public String getName();
//	public String getName() {
//		return (String)params.get("Name");
//	}
}
