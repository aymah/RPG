package map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import unit.Behavior;
import unit.Unit;
import event.Ability;

public class PathfindingUtilities {

	
	public static boolean inMovementRange(int[][] movementMap, Unit unit, Unit target, int rowIndex, int colIndex) {
    	if (movementMap[rowIndex][colIndex] <= unit.getCurrMovement() && 
    		movementMap[rowIndex][colIndex] * 5 <= unit.getCurrStamina() &&
    		(target == null || target.equals(unit)))
    		return true;
    	return false;
    }
	    
    public static boolean inAbilityRange(int[][] movementMap, Unit unit, int sourceY, int sourceX, int targetY, int targetX) {
    	for (Ability ability: unit.getActiveAbilities()) {
    		if ((!ability.hasParam("Level") || ability.getLevel() >= 0) && inRange(targetY, targetX, sourceY, sourceX, ability) &&
    			canAfford(ability, movementMap, unit, sourceY, sourceX, targetY, targetX)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public static boolean canAfford(Ability ability, int[][] movementMap, Unit unit, int sourceY, int sourceX, int targetY, int targetX) {
    	int diff = Behavior.getMoveDistance(movementMap, sourceY, sourceX, targetY, targetX);
    	int totalStaminaCost = ability.getStamCost(unit) + (diff * unit.getMovementCost());
    	int currentStamina = unit.getCurrStamina();
    	int currentMP = unit.getCurrMP();
    	int totalMPCost = ability.getMPCost(unit);
    	if (totalStaminaCost <= currentStamina && totalMPCost <= currentMP)
    		return true;
    	return false;
    }
    
    public static List<Coordinates> makeMovementPath() {
		List<Coordinates> path = new ArrayList<Coordinates>();
    	path.add(new Coordinates(movementIndexY, movementIndexX));
    	int value = movementMap[movementIndexY][movementIndexX];
    	while (path.get(0).getY() != avatarIndexY || path.get(0).getX() != avatarIndexX) {
    		if (path.get(0).getY() - 1 >= 0 && movementMap[path.get(0).getY() - 1][path.get(0).getX()] == value - 1 &&
    		   (orderOfBattle.getUnit(path.get(0).getY() - 1, path.get(0).getX()) == null ||
    			!currUnit.isEnemy(orderOfBattle.getUnit(path.get(0).getY() - 1, path.get(0).getX())))) {
    			value--;
    			path.add(0, new Coordinates(path.get(0).getY() - 1, path.get(0).getX()));
    		} else if (path.get(0).getY() + 1 < movementMap.length && movementMap[path.get(0).getY() + 1][path.get(0).getX()] == value - 1 &&
    	    		   (orderOfBattle.getUnit(path.get(0).getY() + 1, path.get(0).getX()) == null ||
    	    			!currUnit.isEnemy(orderOfBattle.getUnit(path.get(0).getY() + 1, path.get(0).getX())))) {
    			value--;
    			path.add(0, new Coordinates(path.get(0).getY() + 1, path.get(0).getX()));
    		} else if (path.get(0).getX() - 1 >= 0 && movementMap[path.get(0).getY()][path.get(0).getX() - 1] == value - 1 &&
    	    		   (orderOfBattle.getUnit(path.get(0).getY(), path.get(0).getX() - 1) == null ||
    	    			!currUnit.isEnemy(orderOfBattle.getUnit(path.get(0).getY(), path.get(0).getX() - 1)))) {
    			value--;
    			path.add(0, new Coordinates(path.get(0).getY(), path.get(0).getX() - 1));
    		} else if (path.get(0).getX() + 1 < movementMap[0].length && movementMap[path.get(0).getY()][path.get(0).getX() + 1] == value - 1 &&
    	    		   (orderOfBattle.getUnit(path.get(0).getY(), path.get(0).getX() + 1) == null ||
    	    			!currUnit.isEnemy(orderOfBattle.getUnit(path.get(0).getY(), path.get(0).getX() + 1)))) {
    			value--;
    			path.add(0, new Coordinates(path.get(0).getY(), path.get(0).getX() + 1));
    		} else {
    			break;
    		}
    	}
    	return path;
	}

	private boolean inRange(int rowIndex, int colIndex, Ability ability) {
    	int verticalDiff = Math.abs(avatarIndexY - rowIndex);
    	int horizontalDiff = Math.abs(avatarIndexX - colIndex);
    	if (verticalDiff + horizontalDiff <= ability.getRange()) 
    		return true;
    	return false;
    }
		
	public static boolean inRange(Unit target, Unit source, Ability ability) {
		return inRange(target.getPosIndexY(), target.getPosIndexX(), source.getPosIndexY(), source.getPosIndexX(), ability);
	}
	
	public static boolean inRange(int targetY, int targetX, int sourceY, int sourceX, Ability ability) {
		if (ability.hasParam("Area Of Effect")) {
			int[][] aoe = (int[][]) ability.get("Area Of Effect");
			for (int row = 0; row < aoe.length; row++) {
				for (int col = 0; col < aoe[row].length; col++) {
					int indexX = targetX + (col - (aoe[row].length/2));
					int indexY = targetY + (row - (aoe.length/2));
					if (aoe[row][col] > 0) {
				    	int verticalDiff = Math.abs(sourceY - indexY);
				    	int horizontalDiff = Math.abs(sourceX - indexX);
				    	if (verticalDiff + horizontalDiff <= ability.getRange()) 
				    		return true;
					}
				}
			}
		} else {
	    	int verticalDiff = Math.abs(sourceY - targetY);
	    	int horizontalDiff = Math.abs(sourceX - targetX);
	    	if (verticalDiff + horizontalDiff <= ability.getRange()) 
	    		return true;
		}
    	return false;
    }
	
	private boolean[][] createMovementBoolMap(int[][] movementMap, Unit unit) {
		boolean[][] map = new boolean[tileMap.getHeight()][tileMap.getWidth()];
		for (int y = 0; y < tileMap.getHeight(); y++) {
			for (int x = 0; x < tileMap.getWidth(); x++) {
				map[y][x] = inMovementRange(movementMap, unit, y, x);
			}
		}
		return map;
	}
	
	private void updateMovementAbilityMap(int[][] movementMap, int[][] map, Unit unit) {
		if (unit.isStunned())
			return;
		for (int y = 0; y < tileMap.getHeight(); y++) {
			for (int x = 0; x < tileMap.getWidth(); x++) {
				if (inMovementRange(movementMap, unit, y, x)) {
					map[y][x] = 1;
					markAbilityRange(movementMap, map, unit, y, x);
				}
			}
		}
	}
	
	private int[][] createTotalEnemyMovementAbilityMap() {
		int[][] totalMovementAbilityMap = new int[tileMap.getHeight()][tileMap.getWidth()];
		for (Unit unit: orderOfBattle.getUnitList()) {
			if (unit.getFaction().equals("ENEMY")) {
				createEnemyMovementAbilityMap(totalMovementAbilityMap, unit);
			}
		}
		return totalMovementAbilityMap;
	}
	
	private void createEnemyMovementAbilityMap(int[][] movementAbilityMap, Unit unit) {
		int[][] movementMap = createMovementMap(unit);
		updateMovementAbilityMap(movementMap, movementAbilityMap, unit);
	}
	
    public int[][] createMovementMap(Unit currUnit) {
    	int[][] movementMap = new int[tileMap.getHeight()][tileMap.getWidth()];
    	int searchSize = 99; 
    	for (int i = 0; i < movementMap.length; i++)
    		Arrays.fill(movementMap[i], searchSize);
    	movementMap[currUnit.getPosIndexY()][currUnit.getPosIndexX()] = 0;
    	Queue<Coordinates> queue = new LinkedBlockingQueue<Coordinates>();
    	int y = currUnit.getPosIndexY();
    	int x = currUnit.getPosIndexX();
    	queue.add(new Coordinates(y,x));
    	while(!queue.isEmpty()) {
    		Coordinates c = queue.remove();
    		y = c.getY();
    		x = c.getX();
        	int value = movementMap[y][x] + 1;
        	if (value > searchSize)
        		continue;
        	if ((orderOfBattle.getUnit(y, x) != null && currUnit.isEnemy(orderOfBattle.getUnit(y, x)))) {
        		continue;
        	}
        	if (y > 0 && movementMap[y-1][x] > value && (tileMap.getTile(y-1, x).isAccessable() || currUnit.isFlying())) {
        		if (orderOfBattle.getUnit(y-1, x) == null || !currUnit.isEnemy(orderOfBattle.getUnit(y-1, x)) ||
        			orderOfBattle.getUnit(y,x) == null || currUnit.isEnemy(orderOfBattle.getUnit(y, x))) {		
	        		movementMap[y-1][x] = value;
	            	queue.add(new Coordinates(y-1,x));
        		}
        	}
        	if (y < tileMap.getHeight() - 1 && movementMap[y+1][x] > value && (tileMap.getTile(y+1, x).isAccessable() || currUnit.isFlying())) {
        		if (orderOfBattle.getUnit(y+1, x) == null || !currUnit.isEnemy(orderOfBattle.getUnit(y+1, x)) ||
            		orderOfBattle.getUnit(y,x) == null || currUnit.isEnemy(orderOfBattle.getUnit(y, x))) {	
	        		movementMap[y+1][x] = value;
	            	queue.add(new Coordinates(y+1,x));
        		}
        	}
        	if (x > 0 && movementMap[y][x-1] > value && (tileMap.getTile(y, x-1).isAccessable() || currUnit.isFlying())) {
        		if (orderOfBattle.getUnit(y, x-1) == null || !currUnit.isEnemy(orderOfBattle.getUnit(y, x-1)) ||
            		orderOfBattle.getUnit(y,x) == null || currUnit.isEnemy(orderOfBattle.getUnit(y, x))) {	
	        		movementMap[y][x-1] = value;
	            	queue.add(new Coordinates(y,x-1));
        		}
        	}
        	if (x < tileMap.getWidth() - 1 && movementMap[y][x+1] > value && (tileMap.getTile(y, x+1).isAccessable() || currUnit.isFlying())) {
        		if (orderOfBattle.getUnit(y, x+1) == null || !currUnit.isEnemy(orderOfBattle.getUnit(y, x+1)) ||
            		orderOfBattle.getUnit(y,x) == null || currUnit.isEnemy(orderOfBattle.getUnit(y, x))) {	
        			movementMap[y][x+1] = value;
            		queue.add(new Coordinates(y,x+1));
        		}
        	}
//	        	if (y > 0 && movementMap[y-1][x] > value && orderOfBattle.getUnit(y-1, x) == null && tileMap.getTile(y-1, x).isAccessable()) {
//	        		movementMap[y-1][x] = value;
//	            	queue.add(new Coordinates(y-1,x));
//	        	}
//	        	if (y < tileMap.getHeight() - 1 && movementMap[y+1][x] > value && orderOfBattle.getUnit(y+1, x) == null && tileMap.getTile(y+1, x).isAccessable()) {
//	        		movementMap[y+1][x] = value;
//	            	queue.add(new Coordinates(y+1,x));
//	        	}
//	        	if (x > 0 && movementMap[y][x-1] > value && orderOfBattle.getUnit(y, x-1) == null && tileMap.getTile(y, x-1).isAccessable()) {
//	        		movementMap[y][x-1] = value;
//	            	queue.add(new Coordinates(y,x-1));
//	        	}
//	        	if (x < tileMap.getWidth() - 1 && movementMap[y][x+1] > value && orderOfBattle.getUnit(y, x+1) == null && tileMap.getTile(y, x+1).isAccessable()) {
//	        		movementMap[y][x+1] = value;
//	            	queue.add(new Coordinates(y,x+1));
//	        	}
    	}
//	    	for (int i = 0; i < movementMap.length; i++) {
//	    		for (int j = 0; j < movementMap[i].length; j++) {
//	    			System.out.print(movementMap[i][j] + " ");
//	    		}
//	    		System.out.println("");
//	    	}
//			System.out.println("");
    	return movementMap;
    }
}
