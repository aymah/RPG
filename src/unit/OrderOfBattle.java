package unit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import map.TileMap;

public class OrderOfBattle implements Serializable {
	Unit[][] unitMap;
	Unit[][] phantomUnitMap;
	List<Unit> unitList;
	List<Squad> squadList;
	transient SquadQueue squadQueue;
	transient PhantomSquadQueue phantomQueue;
	List<Squad> phantomList;
	
	public OrderOfBattle(TileMap tileMap) {
		unitMap = new Unit[tileMap.getHeight()][tileMap.getWidth()];
		phantomUnitMap = new Unit[tileMap.getHeight()][tileMap.getWidth()];
		unitList = new ArrayList<Unit>();
		squadList = new ArrayList<Squad>();
	}
	
	public List<Unit> getUnitList() {
		return unitList;
	}
	
//	public void addUnit(Unit unit) {
//		unit.setRandomOrdering();
//		unitList.add(unit);
//		squadQueue.enQueue(unit);
//		unitMap[unit.getPosIndexY()][unit.getPosIndexX()] = unit;
//		unit.setOrderOfBattle(this);
//	}
	
	public void addSquad(Squad squad) {
//		for (Unit unit: squad.getUnitList()) {
//			unitList.add(unit);
//			unitMap[unit.getPosIndexY()][unit.getPosIndexX()] = unit;
//			unit.setOrderOfBattle(this);
//		}
		squadList.add(squad);
	}
	
//	public void enQueueSquad(Squad squad) {
//		squadQueue.enQueue(squad);
//	}
	
	public void addUnit(Unit unit) {
		unitList.add(unit);
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
		unit.getSquad().remove(unit);
		if (unit.getSquad().getUnitList().size() <= 0) {
			squadQueue.remove(unit.getSquad());
		}
//		squadQueue.remove(unit);
		unitMap[unit.getPosIndexY()][unit.getPosIndexX()] = null;
//		while (unitQueue.peek() != null) {
//			Unit unit1 = unitQueue.deQueue();
//			System.out.println("HP: " + unit.getCurrHP());
//		}
	}
	
	public Squad getCurrSquad() {
		return squadQueue.peek();
	}
	
	public Unit getCurrUnit() {
		return squadQueue.peek().getCurrUnit();
	}
	
	public Unit getNextUnit() {
		if (squadQueue.peek().getNextUnit() == null) {
			getNextSquad();
			squadQueue.peek().generateUnitQueue();
			return squadQueue.peek().getCurrUnit();
		}
		return squadQueue.peek().getCurrUnit();
	}
	
	public Squad getNextSquad() {
		squadQueue.peek().setOrdering(squadQueue.peek().getOrdering() + squadQueue.peek().getInitiative());
		squadQueue.enQueue(squadQueue.deQueue());
		return squadQueue.peek();
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
		phantomQueue = new PhantomSquadQueue(squadQueue);
		phantomList = new ArrayList<Squad>();
		for (int i = 0; i < 50; i++) {
			Squad squad = phantomQueue.reQueue();
			phantomList.add(squad);
		}
	}

	public List<Squad> getPhantomList() {
		return phantomList;
	}

	public void moveToPhantomPlane(Unit unit) {
		unitMap[unit.getPosIndexY()][unit.getPosIndexX()] = null;
		phantomUnitMap[unit.getPosIndexY()][unit.getPosIndexX()] = unit;
	}

	public void moveToRegularPlane(Unit unit) {
//		if (phantomUnitMap[unit.getPosIndexY()][unit.getPosIndexX()] != null && unitMap[unit.getPosIndexY()][unit.getPosIndexX()] != null) {
//			System.err.println("WARNING: Moving unit into occupied space " + unit.getName() + " " + phantomUnitMap[unit.getPosIndexY()][unit.getPosIndexX()].getName() + " " + unitMap[unit.getPosIndexY()][unit.getPosIndexX()].getName() + " " + unit.getPosIndexY() + " " + unit.getPosIndexX());
//		}
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
		squadQueue = new SquadQueue();
	}
	
	public void cleanUp() {
		unitMap = null;
		phantomUnitMap = null;
		unitList = null;
		squadQueue = null;
		phantomQueue = null;
		phantomList = null;
	}
	
	public void setInitialOrdering() {
		SquadQueue squadQueue = new SquadQueue();
		List<Squad> allySquads = new ArrayList<Squad>();
		List<Squad> enemySquads = new ArrayList<Squad>();
		for (Squad squad: squadList) {
			if (squad.getLeader().getFaction().equals("ALLY"))
				allySquads.add(squad);
			else 
				enemySquads.add(squad);
		}
		
		for (int i = 0; i < allySquads.size(); i++) {
			Squad squad = allySquads.get(i);
			squad.setOrdering((100 * i)/allySquads.size());
			squadQueue.enQueue(squad);
		}
		
		for (int i = 0; i < enemySquads.size(); i++) {
			Squad squad = enemySquads.get(i);
			squad.setOrdering((100 * i)/enemySquads.size());
			squadQueue.enQueue(squad);
		}
		this.squadQueue = squadQueue;
	}
	
	public void revert() {
		
	}

	public Squad getSquad(String name) {
		for (Squad squad: squadList) {
			if (squad.getName().equals(name))
				return squad;
		}
		return null;
	}
}
	
