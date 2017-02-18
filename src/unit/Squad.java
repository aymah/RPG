package unit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Squad implements Serializable {

	private String name;
	private Unit leader;
	private Queue<Unit> unitQueue;
	private List<Unit> unitList;
	private List<Unit> unitPlacementList;
	protected int ordering;
	
	public Squad(String name, String behavior) {
		this.unitList =  new ArrayList<Unit>();
		this.name = name;
	}
	
	public Squad(Unit leader) {
		this.unitList =  new ArrayList<Unit>();
		this.unitList.add(leader);
		this.leader = leader;
		this.name = leader.getName();
		for (Unit unit: unitList) {
			unit.setSquad(this);
		}
	}
	
	public Squad(Unit leader, List<Unit> units) {
		this.unitList =  new ArrayList<Unit>();
		this.unitList.add(leader);
		this.name = leader.getName();
		for (Unit unit: unitList) {
			unit.setSquad(this);
		}
	}
	
	public int getInitiative() {
		int initiative = 0;
		for (Unit unit: unitList) {
			if (unit.getInitiative() > initiative);
				initiative = unit.getCurrInitiative();
		}
		return initiative;
	}
	
	public void addUnit(Unit unit) {
		if (unitList.size() == 0)
			setLeader(unit);
		unitList.add(unit);
		unit.setSquad(this);
	}
	
	public List<Unit> getUnitList() {
		return unitList;
	}
	
	public List<Unit> getUnitPlacementList() {
		return unitPlacementList;
	}
	
	public void setLeader(Unit unit) {
		leader = unit;
	}
	
	public Unit getLeader() {
		return leader;
	}
	
	public int getOrdering() {
		return ordering;
	}
	
	public void setOrdering(int ordering) {
		this.ordering = ordering;
	}
	
	public void remove(Unit unit) {
		unitList.remove(unit);
		if (unitQueue.contains(unit))
			unitQueue.remove(unit);
	}
	
	public Unit getNextUnit() {
		unitQueue.poll();
		return unitQueue.peek();
	}
	
	public boolean isEmpty() {
		if (unitQueue.size() <= 0)
			return true;
		return false;
	}
	
	public void generateUnitQueue() {
		unitQueue = new ConcurrentLinkedQueue<Unit>(unitList);
	}
	
	public void generateUnitPlacementList() {
		unitPlacementList = new ArrayList<Unit>(unitList);
	}
	
	public Unit getCurrUnit() {
		return unitQueue.peek();
	}
	
	public Unit cycleUnit() {
		if (unitQueue.size() > 1)
			unitQueue.add(unitQueue.poll());
		return unitQueue.peek();
	}

	public String getFaction() {
		return leader.getFaction();
	}

	public String getName() {
		return name;
	}

	public Unit getPlacement(int index) {
		return unitPlacementList.get(index);
	}

	public int getSize() {
		return unitList.size();
	}

	public Behavior getBehavior() {
		return getCurrUnit().getBehavior();
	}
}
