package event;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import map.ExploreMenuPanel;
import map.ExplorePanelManager;
import map.GamePanel;
import misc.Factory;

import org.json.JSONArray;
import org.json.JSONObject;

import unit.Party;

public class ItemFactory extends Factory implements Serializable, MenuItem {
	
	Map<String, Object> params;
	
	public ItemFactory(String filename) {
		params = new HashMap<String, Object>();
		readItemFile("/items/" + filename);
	}

	private void readItemFile(String filename) {
		JSONObject itemObject = getJSONObject(filename + ".json"); 
		
		for (String key: itemObject.keySet()) {
			params.put(key, itemObject.get(key));
		}
	}

	public Item createInstance() {
		switch(getType()) {
			case "Consumable":
				return new Consumable(this);
			case "Equipment":
				if (params.get("Equipment Type").equals("Armor"))
					return new Armor(this);
				return new Equipment(this);
			default: return null;
		}
	}

	public Object get(String key) {
		return params.get(key);
	}

	public String getName() {
		return (String)params.get("Name");
	}
	
	public int getCost() {
		return (int)params.get("Gold Cost");
	}
	
	public String getDescription() {
		return (String)params.get("Description");
	}
	
	public String getType() {
		return (String)params.get("Type");
	}

	public Map<String, Object> getParams() {
		return params;
	}

	@Override
	public void execute(GamePanel panel) {
		ExploreMenuPanel menuPanel = (ExploreMenuPanel) panel;
		Party party = ((ExplorePanelManager)menuPanel.getManager()).getMap().getParty();
		Item item = party.getItem(this);
		System.out.println(item.getName());
		menuPanel.selectTabItem();
		if (item.getType().equals("Consumable")) {
			if (item.get("Consumable Type").equals("Healing")) {
				System.out.println("open party select item menu, setting item to " + item.getName());
				menuPanel.openPartySelectItemMenu(item);
				
				//bring up a new menu, maybe partyItemUseMenu or some shit, with all party members as selectable menu items.
				//when this item is executed, it checks for the Consumable Type of the consumable and if it is a healing item
				//it heals that party member for the set amount. You can also go "back" to the other menu, which will add the item
				//back to the item menu (otherwise the item will be abandoned here and poof)
			}
		}
			
	}

}
