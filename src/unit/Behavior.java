package unit;

import java.util.ArrayList;
import java.util.List;

import map.BattleMap;
import map.Coordinates;
import event.Ability;

public interface Behavior {

	public void markMovementPriority(int[][] movePriorityMap, final int[][] movementMap,  Unit unit, List<Ability> abilities);
	
	public static Behavior makeBehavior(String type, Unit unit) {
		Behavior behavior = null;
		switch (type) {
		case "Defend":
			behavior = new Behavior() {
				@Override
				public void markMovementPriority(int[][] movePriorityMap, final int[][] movementMap, Unit target, List<Ability> abilities) {
					int acquisitionRange = unit.getAcquisitionRange("Acquisition Range");
					for (Ability ability: abilities) {
						int range = ability.getRange();
				    	for (int y = -range; y <= range; y++) {
				    		for (int x = -range; x <= range; x++) {
								if (target.getPosIndexY() + y >= 0 && target.getPosIndexX() + x >= 0 &&
									target.getPosIndexY() + y < movePriorityMap.length && target.getPosIndexX() + x < movePriorityMap[0].length) {
					    			if (BattleMap.inRange(target, range, target.getPosIndexY() + y, target.getPosIndexX() + x) &&
					    				movePriorityMap[target.getPosIndexY() + y][target.getPosIndexX() + x] > ability.getPriority())
					    				movePriorityMap[target.getPosIndexY() + y][target.getPosIndexX() + x] = ability.getPriority();
								}
				    		}
			    		}
			    	}
					for (int y = 0; y < movementMap.length; y++) {
						for (int x = 0; x < movementMap[y].length; x++) {
							if (movementMap[y][x] <= unit.getCurrMovement() && movementMap[target.getPosIndexY()][target.getPosIndexX()] < acquisitionRange) {
						    	int diff = getMoveDistance(movementMap, y, x, target.getPosIndexY(), target.getPosIndexX());
//						    	System.out.println(movementMap[target.getPosIndexY()][target.getPosIndexX()]);
						    	if(movePriorityMap[y][x] > diff) {
						    		movePriorityMap[y][x] = diff;
						    	}
							}
						}
					}
//					System.out.println("attacker:" + unit.getName() + " " + "target:" + target.getName());
//					for (int i = 0; i < movePriorityMap.length; i++) {
//						for (int j = 0; j < movePriorityMap[i].length; j++) {
//							System.out.print(movePriorityMap[i][j] + " ");
//						}
//						System.out.println("");
//					}
//					System.out.println("");
				}
			};
			break;
		case "Attack":
			behavior = new Behavior() {
				@Override
				public void markMovementPriority(int[][] movePriorityMap, final int[][] movementMap, Unit target, List<Ability> abilities) {
					for (Ability ability: abilities) {
						int range = ability.getRange();
				    	for (int y = -range; y <= range; y++) {
				    		for (int x = -range; x <= range; x++) {
								if (target.getPosIndexY() + y >= 0 && target.getPosIndexX() + x >= 0 &&
									target.getPosIndexY() + y < movePriorityMap.length && target.getPosIndexX() + x < movePriorityMap[0].length) {
					    			if (BattleMap.inRange(target, range, target.getPosIndexY() + y, target.getPosIndexX() + x) &&
					    				movePriorityMap[target.getPosIndexY() + y][target.getPosIndexX() + x] > ability.getPriority())
					    				movePriorityMap[target.getPosIndexY() + y][target.getPosIndexX() + x] = ability.getPriority();
								}
				    		}
			    		}
			    	}
					for (int y = 0; y < movementMap.length; y++) {
						for (int x = 0; x < movementMap[y].length; x++) {
							if (movementMap[y][x] <= unit.getCurrMovement() && movementMap[target.getPosIndexY()][target.getPosIndexX()] < 999) {
						    	int diff = getMoveDistance(movementMap, y, x, target.getPosIndexY(), target.getPosIndexX());
//						    	System.out.println(movementMap[target.getPosIndexY()][target.getPosIndexX()]);
						    	if(movePriorityMap[y][x] > diff) {
						    		movePriorityMap[y][x] = diff;
						    	}
							}
						}
					}
					movePriorityMap[target.getPosIndexY()][target.getPosIndexX()] = 99;
//					System.out.println("attacker:" + unit.getName() + " " + unit.getPosIndexY() + " " + unit.getPosIndexX() + " " +
//									   "target:" + target.getName() + " " + target.getPosIndexY() + " " + target.getPosIndexX());
//					for (int i = 0; i < movePriorityMap.length; i++) {
//						for (int j = 0; j < movePriorityMap[i].length; j++) {
//							System.out.print(movementMap[i][j] + " ");
//						}
//						System.out.println("");
//					}
//					System.out.println("");
//					for (int i = 0; i < movePriorityMap.length; i++) {
//						for (int j = 0; j < movePriorityMap[i].length; j++) {
//							System.out.print(movePriorityMap[i][j] + " ");
//						}
//						System.out.println("");
//					}
//					System.out.println("");
				}
			};
			break;
		default:
	}
	return behavior;
	}
	
	
    static int getMoveDistance(final int[][] movementMap, int sourceY, int sourceX, int targetY, int targetX) {
		List<Coordinates> path = new ArrayList<Coordinates>();
    	path.add(new Coordinates(targetY, targetX));
    	int value = movementMap[targetY][targetX];
    	while (path.get(0).getY() != sourceY || path.get(0).getX() != sourceX) {
    		if (movementMap[path.get(0).getY() - 1][path.get(0).getX()] == value - 1) {
    			value--;
    			path.add(0, new Coordinates(path.get(0).getY() - 1, path.get(0).getX()));
    		} else if (movementMap[path.get(0).getY() + 1][path.get(0).getX()] == value - 1) {
    			value--;
    			path.add(0, new Coordinates(path.get(0).getY() + 1, path.get(0).getX()));
    		} else if (movementMap[path.get(0).getY()][path.get(0).getX() - 1] == value - 1) {
    			value--;
    			path.add(0, new Coordinates(path.get(0).getY(), path.get(0).getX() - 1));
    		} else if (movementMap[path.get(0).getY()][path.get(0).getX() + 1] == value - 1) {
    			value--;
    			path.add(0, new Coordinates(path.get(0).getY(), path.get(0).getX() + 1));
    		} else {
    			break;
    		}
    	}
    	return path.size();
	}
}
