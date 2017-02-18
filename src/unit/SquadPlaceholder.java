package unit;

import java.io.Serializable;

public class SquadPlaceholder {
	int ordering;
	Squad squad;
	
	public SquadPlaceholder(Squad squad) {
		this.squad = squad;
		ordering = squad.getOrdering();
	}
	
	public int getOrdering() {
		return ordering;
	}
	
	public void setOrdering(int ordering) {
		this.ordering = ordering;
	}
	
	@Override
	public boolean equals(Object o1) {
		if (this.squad.equals(((SquadPlaceholder)o1).squad)) return true;
		return false;
	}
	
}
