package unit;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import map.TileMap;

public class OrderOfBattle {
	Unit[][] unitMap;
	Unit[][] phantomUnitMap;
	List<Unit> unitList;
	UnitQueue unitQueue;
	PhantomUnitQueue phantomQueue;
	List<Unit> phantomList;
	
	public OrderOfBattle(TileMap tileMap) {
		unitMap = new Unit[tileMap.getHeight()][tileMap.getWidth()];
		phantomUnitMap = new Unit[tileMap.getHeight()][tileMap.getWidth()];
		unitList = new ArrayList<Unit>();
		unitQueue = new UnitQueue();
	}
	
	public List<Unit> getUnitList() {
		return unitList;
	}
	
	public void addUnit(Unit unit) {
		unitList.add(unit);
		unitQueue.enQueue(unit);
		unitMap[unit.getPosIndexY()][unit.getPosIndexX()] = unit;
		unit.setOrderOfBattle(this);
	}
	
	public Unit getUnit(int y, int x) {
		return unitMap[y][x];
	}
	
	public Unit getPhantomUnit(int y, int x) {
		return phantomUnitMap[y][x];
	}
	
	public void removeUnit(Unit unit) {
		unitList.remove(unit);
		unitMap[unit.getPosIndexY()][unit.getPosIndexX()] = null;
		unitQueue.remove(unit);
//		while (unitQueue.peek() != null) {
//			Unit unit1 = unitQueue.deQueue();
//			System.out.println("HP: " + unit.getCurrHP());
//		}
	}
	
	public Unit getCurrUnit() {
		return unitQueue.peek();
	}
	
	public Unit getNextUnit() {
		Unit currUnit = unitQueue.deQueue();
		unitQueue.enQueue(currUnit);
		return unitQueue.peek();
	}

//	public void moveUnit(Unit unit, int movementIndexY, int movementIndexX) {
//		unitMap[unit.getPosIndexY()][unit.getPosIndexX()] = null;
//		unitMap[movementIndexY][movementIndexX] = unit;
//		unit.setPosition(movementIndexY, movementIndexX);
//	}
	

	public void movePhantomUnit(Unit unit, int movementIndexY, int movementIndexX) {
		phantomUnitMap[unit.getPosIndexY()][unit.getPosIndexX()] = null;
		phantomUnitMap[movementIndexY][movementIndexX] = unit;
		unit.setPosition(movementIndexY, movementIndexX);
	}
	
	public void generatePhantomQueue() {
		phantomQueue = new PhantomUnitQueue(unitQueue);
		phantomList = new ArrayList<Unit>();
		for (int i = 0; i < 50; i++) {
			Unit unit = phantomQueue.reQueue();
			phantomList.add(unit);
		}
	}
	
	public class UnitPlaceholder {
		int ordering;
		Unit unit;
		
		public UnitPlaceholder(Unit unit) {
			this.unit = unit;
			ordering = unit.getOrdering();
		}
		
		public int getOrdering() {
			return ordering;
		}
		
		public void setOrdering(int ordering) {
			this.ordering = ordering;
		}
		
		@Override
		public boolean equals(Object o1) {
			if (this.unit.equals(((UnitPlaceholder)o1).unit)) return true;
			return false;
		}
		
	}
	
	public class PhantomUnitQueue {
		
		PriorityQueue<UnitPlaceholder> pQueue;
		
		public PhantomUnitQueue() {
			Comparator comparator = buildComparator();
			pQueue = new PriorityQueue<UnitPlaceholder>(10, comparator);
		}
		
		public PhantomUnitQueue(UnitQueue unitQueue) {
			Comparator comparator = buildComparator();
			pQueue = new PriorityQueue<UnitPlaceholder>(10, comparator);
			Object[] unitArray = unitQueue.toArray();
			for (int i = 0; i < unitArray.length; i++) {
				UnitPlaceholder uP = this.enQueue((Unit)unitArray[i]);
				uP.setOrdering(((Unit)unitArray[i]).getOrdering());
			}
		}
		
		private Object[] toArray() {
			return pQueue.toArray();
		}

		private Comparator buildComparator() {
			Comparator<UnitPlaceholder> comparator = new Comparator<UnitPlaceholder>() {
				@Override
				public int compare(UnitPlaceholder o1, UnitPlaceholder o2) {
					if (o1.getOrdering() - o2.getOrdering() == 0) return 1;
					return o1.getOrdering() - o2.getOrdering();
				}
			};
			return comparator;
		}
		
		public Unit reQueue() {
			UnitPlaceholder unitPlaceholder = pQueue.remove();
			int ordering = unitPlaceholder.getOrdering();
			Unit unit = unitPlaceholder.unit;
			UnitPlaceholder uP = new UnitPlaceholder(unit);
			uP.setOrdering(ordering + unit.getCurrInitiative());
			pQueue.add(uP);
			return unit;
		}
		
		public Unit deQueue() {
			return pQueue.remove().unit;
		}
		
		public UnitPlaceholder enQueue(Unit unit) {
			UnitPlaceholder uP = new UnitPlaceholder(unit);
			pQueue.add(uP);
			return uP;
		}
		
		public Unit peek() {
			if (pQueue.peek() == null) return null;
			return pQueue.peek().unit;
		}
		
		public void remove(Unit unit) {
			UnitPlaceholder uP = new UnitPlaceholder(unit);
			pQueue.remove(uP);
		}
		
		public PriorityQueue<UnitPlaceholder> getPQueue() {
			return pQueue;
		}
	}

	public List<Unit> getPhantomList() {
		return phantomList;
	}

	public void moveToPhantomPlane(Unit unit) {
		unitMap[unit.getPosIndexY()][unit.getPosIndexX()] = null;
		phantomUnitMap[unit.getPosIndexY()][unit.getPosIndexX()] = unit;
	}

	public void moveToRegularPlane(Unit unit) {
		phantomUnitMap[unit.getPosIndexY()][unit.getPosIndexX()] = null;
		unitMap[unit.getPosIndexY()][unit.getPosIndexX()] = unit;
	}

	public String lastFactionStanding() {
		String faction = unitList.get(0).getFaction();
		for (Unit unit: unitList) {
			if (!unit.getFaction().equals(faction)) {
				return "";
			}
		}
		return faction;
	}

	public void removeAll() {
		unitQueue = new UnitQueue();
	}
}
	
