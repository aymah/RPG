package unit;

import java.util.Comparator;
import java.util.PriorityQueue;

import unit.OrderOfBattle.UnitPlaceholder;

public class UnitQueue {

	PriorityQueue<Unit> pQueue;
	
	public UnitQueue() {
		Comparator comparator = buildComparator();
		pQueue = new PriorityQueue<Unit>(10, comparator);
	}
	
	public Object[] toArray() {
		return pQueue.toArray();
	}

	public static Comparator buildComparator() {
		Comparator<Unit> comparator = new Comparator<Unit>() {
			@Override
			public int compare(Unit o1, Unit o2) {
				if (o1.getOrdering() - o2.getOrdering() == 0) return 1;
				return o1.getOrdering() - o2.getOrdering();
			}
		};
		return comparator;
	}
	
	public Unit deQueue() {
		return pQueue.remove();
	}
	
	public void enQueue(Unit unit) {
		pQueue.add(unit);
	}
	
	public Unit peek() {
		return pQueue.peek();
	}
	
	public void remove(Unit unit) {
		pQueue.remove(unit);
	}
}
