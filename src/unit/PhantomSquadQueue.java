package unit;

import java.io.Serializable;
import java.util.Comparator;
import java.util.PriorityQueue;

public class PhantomSquadQueue {

	PriorityQueue<SquadPlaceholder> pQueue;
	
	public PhantomSquadQueue() {
		Comparator comparator = buildComparator();
		pQueue = new PriorityQueue<SquadPlaceholder>(10, comparator);
	}
	
	public PhantomSquadQueue(SquadQueue squadQueue) {
		Comparator comparator = buildComparator();
		pQueue = new PriorityQueue<SquadPlaceholder>(10, comparator);
		Object[] squadArray = squadQueue.toArray();
		for (int i = 0; i < squadArray.length; i++) {
			SquadPlaceholder sP = this.enQueue((Squad)squadArray[i]);
			sP.setOrdering(((Squad)squadArray[i]).getOrdering());
		}
	}
	
	public Object[] toArray() {
		return pQueue.toArray();
	}

	private Comparator buildComparator() {
		Comparator<SquadPlaceholder> comparator = new Comparator<SquadPlaceholder>() {
			@Override
			public int compare(SquadPlaceholder o1, SquadPlaceholder o2) {
				if (o1.getOrdering() - o2.getOrdering() == 0) return 1;
				return o1.getOrdering() - o2.getOrdering();
			}
		};
		return comparator;
	}
	
	public Squad reQueue() {
		SquadPlaceholder unitPlaceholder = pQueue.remove();
		int ordering = unitPlaceholder.getOrdering();
		Squad squad = unitPlaceholder.squad;
		SquadPlaceholder sP = new SquadPlaceholder(squad);
		sP.setOrdering(ordering + squad.getInitiative());
		pQueue.add(sP);
		return squad;
	}
	
	public Squad deQueue() {
		return pQueue.remove().squad;
	}
	
	public SquadPlaceholder enQueue(Squad squad) {
		SquadPlaceholder sP = new SquadPlaceholder(squad);
		pQueue.add(sP);
		return sP;
	}
	
	public Squad peek() {
		if (pQueue.peek() == null) return null;
		return pQueue.peek().squad;
	}
	
	public void remove(Squad squad) {
		SquadPlaceholder uP = new SquadPlaceholder(squad);
		pQueue.remove(uP);
	}
	
	public PriorityQueue<SquadPlaceholder> getPQueue() {
		return pQueue;
	}
}
