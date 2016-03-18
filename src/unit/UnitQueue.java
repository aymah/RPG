package unit;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class UnitQueue {

	PriorityQueue<Unit> pQueue;
	
	public UnitQueue(List<Unit> unitList) {
		Comparator comparator = new UnitComparator();
		pQueue = new PriorityQueue<Unit>(10, comparator);
		pQueue.addAll(unitList);
	}
	
	
	public class UnitComparator implements Comparator {

		@Override
		public int compare(Object o1, Object o2) {
			Unit unit1 = (Unit)o1;
			Unit unit2 = (Unit)o2;
			return unit1.getOrdering() - unit2.getOrdering();
		}
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
