package unit;

import java.util.Comparator;
import java.util.PriorityQueue;

public class SquadQueue {

	PriorityQueue<Squad> pQueue;
	
	public SquadQueue() {
		Comparator comparator = getComparator();
		pQueue = new PriorityQueue<Squad>(10, comparator);
	}
	
	public Object[] toArray() {
		return pQueue.toArray();
	}

	public static Comparator getComparator() {
		Comparator<Squad> comparator = new Comparator<Squad>() {
			@Override
			public int compare(Squad o1, Squad o2) {
				if (o1.getOrdering() - o2.getOrdering() == 0) return 1;
				return o1.getOrdering() - o2.getOrdering();
			}
		};
		return comparator;
	}
	
	public Squad deQueue() {
		return pQueue.remove();
	}
	
	public void enQueue(Squad squad) {
		pQueue.add(squad);
	}
	
	public Squad peek() {
		return pQueue.peek();
	}
	
	public void remove(Squad squad) {
		pQueue.remove(squad);
	}
}
