package unit;

import java.util.ArrayList;
import java.util.List;

import map.TileMap;

public class OrderOfBattle {
	Unit[][] unitMap;
	List<Unit> unitList;
	UnitQueue unitQueue;
	UnitQueue phantomQueue;
	
	public OrderOfBattle(TileMap tileMap) {
		unitMap = new Unit[tileMap.getHeight()][tileMap.getWidth()];
		unitList = new ArrayList<Unit>();
		unitQueue = new UnitQueue(unitList);
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
}
	
