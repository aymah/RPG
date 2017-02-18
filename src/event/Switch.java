package event;

import java.io.Serializable;

public class Switch implements Serializable {

	private String type;
	private int changeIndex;
	private String condition;
	
	public Switch(String type) {
		this.type = type;
	}
	
	public Switch(String type, String condition) {
		this.type = type;
		this.condition = condition;
		changeIndex = -1;
	}
	
	public Switch(String type, String condition, int changeIndex) {
		this.type = type;
		this.condition = condition;
		this.changeIndex = changeIndex;
	}
	
	public String getCondition() {
		return condition;
	}
	
//	public boolean conditionsMet() {
//		return false;
//	}
	
	public int getChangeIndex() {
		return changeIndex;
	}

	public String getType() {
		return type;
	}
}
