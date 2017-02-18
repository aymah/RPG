package unit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import map.BattleMap;
import map.Coordinates;
import map.PathCoordinates;
import map.TileMap;
import event.Ability;

public interface Behavior extends Serializable {
		
	static final int TOP_N = 1;
	
	public void markMovementPriority(int[][] movePriorityMap, final int[][] movementMap, TileMap tileMap, OrderOfBattle orderOfBattle, Unit unit);
	public Queue<AIMove> getSquadMoves(TileMap tileMap, OrderOfBattle orderOfBattle, Squad squad);
	public String getName();
	//[unit][y][x]
//    public static long sumScore(ScoreKeeper[][][] scoreGrid, int y, int x) {
//    	long score = 0;
//    	for (int i = 0; i < scoreGrid.length; i++) {
//    		ScoreKeeper[][] indvGrid = scoreGrid[i];
//    		score += indvGrid[y][x].getScore();
//    	}
//    	return score;
//    }
	
	public static Behavior makeBehavior(String type) {
		Behavior behavior = null;
		switch (type) {
			case "Defend":
				behavior = new Behavior() {
					private String name = type;
					private ScoreQueue scoreQueue;
					
					@Override
					public void markMovementPriority(int[][] movePriorityMap, final int[][] movementMap, TileMap tileMap, OrderOfBattle orderOfBattle, Unit unit) {
						scoreQueue = new ScoreQueue();
//						int acquisitionRange = unit.getAcquisitionRange();
				    	for (Unit target: orderOfBattle.getUnitList()) {
				    		if (target.getFaction().equals("ALLY")) {
//								for (Ability ability: abilities) {
//									int range = ability.getRange();
//							    	for (int y = -range; y <= range; y++) {
//							    		for (int x = -range; x <= range; x++) {
//											if (target.getPosIndexY() + y >= 0 && target.getPosIndexX() + x >= 0 &&
//												target.getPosIndexY() + y < movePriorityMap.length && target.getPosIndexX() + x < movePriorityMap[0].length) {
//								    			if (BattleMap.inRange(target, range, target.getPosIndexY() + y, target.getPosIndexX() + x) &&
//								    				movePriorityMap[target.getPosIndexY() + y][target.getPosIndexX() + x] > ability.getPriority() &&
//								    				BattleMap.inOrigRange(unit, acquisitionRange, target.getPosIndexY() + y, target.getPosIndexX() + x))
//								    				movePriorityMap[target.getPosIndexY() + y][target.getPosIndexX() + x] = ability.getPriority();
//											}
//							    		}
//						    		}
//						    	}
//								for (int y = 0; y < movementMap.length; y++) {
//									for (int x = 0; x < movementMap[y].length; x++) {
//										//acquisition range should be based off of distance from your original position
//										//
//										//if the position being considered is within movement range of the attacker and
//										//if the position of the target is within acquisition range of the attacker and
//										//if the position being considered is within acquisition range of the original position of the attacker
//										//right now the selectAiMovement doesn't try to make paths, so as long as its in range it will go for it.
//										if (movementMap[y][x] <= unit.getCurrMovement() && movementMap[target.getPosIndexY()][target.getPosIndexX()] < acquisitionRange) {
//											if (BattleMap.inOrigRange(unit, acquisitionRange, y, x)) {
//										    	int diff = getMoveDistance(movementMap, y, x, target.getPosIndexY(), target.getPosIndexX());
//				//						    	System.out.println(movementMap[target.getPosIndexY()][target.getPosIndexX()]);
//										    	if(movePriorityMap[y][x] > diff) {
//										    	   movePriorityMap[y][x] = diff;
//										    	}
//									    	} 
//										}
//									}
//								}
				    		}
				    	}
//				    	for (int y = 0; y < movementMap.length; y++) {
//							for (int x = 0; x < movementMap[y].length; x++) {
//								if (movementMap[y][x] <= unit.getCurrMovement() && movementMap[unit.getOrigIndexY()][unit.getOrigIndexX()] < acquisitionRange) {
//									if (BattleMap.inOrigRange(unit, acquisitionRange, y, x)) {
//								    	int diff = getMoveDistance(movementMap, y, x, unit.getOrigIndexY(), unit.getOrigIndexX());
//		//						    	System.out.println(movementMap[target.getPosIndexY()][target.getPosIndexX()]);
//								    	if(movePriorityMap[y][x] > diff) {
//								    	   movePriorityMap[y][x] = diff;
//								    	}
//							    	} 
//								}
//							}
//				    	}
//				    	movePriorityMap[unit.getOrigIndexY()][unit.getOrigIndexX()] = 0;
				    	//enter priority here
//						System.out.println("attacker:" + unit.getName() + " " + unit.getPosIndexY() + " " + unit.getPosIndexX());
//						for (int i = 0; i < movePriorityMap.length; i++) {
//							for (int j = 0; j < movePriorityMap[i].length; j++) {
//								if (String.valueOf(movementMap[i][j]).length() == 1)
//									System.out.print(" ");
//								System.out.print(movementMap[i][j] + " ");
//							}
//							System.out.println("");
//						}
//						System.out.println("");
//						for (int i = 0; i < movePriorityMap.length; i++) {
//							for (int j = 0; j < movePriorityMap[i].length; j++) {
//								if (String.valueOf(movePriorityMap[i][j]).length() == 1)
//									System.out.print(" ");
//								System.out.print(movePriorityMap[i][j] + " ");
//							}
//							System.out.println("");
//						}
//						System.out.println("");
					}

					@Override
					public String getName() {
						return name;
					}

					@Override
					public Queue<AIMove> getSquadMoves(TileMap tileMap,
							OrderOfBattle orderOfBattle, Squad squad) {
						// TODO Auto-generated method stub
						return null;
					}
				};
				break;
			case "Attack":
				behavior = new Behavior() {
					private String name = type;
					private ScoreQueue scoreQueue;

					@Override
					public void markMovementPriority(int[][] movePriorityMap, final int[][] movementMap, TileMap tileMap, OrderOfBattle orderOfBattle, Unit unit) {
						scoreQueue = new ScoreQueue();
						List<Ability> abilities = unit.getActiveAbilities();
						List<PotentialMove> potentialMoves = new ArrayList<PotentialMove>();
						for (Unit target: orderOfBattle.getUnitList()) {
				    		if (target.getFaction().equals("ALLY")) {
								for (Ability ability: abilities) {
									int range = ability.getRange();
							    	for (int y = -range; y <= range; y++) {
							    		for (int x = -range; x <= range; x++) {
											if (target.getPosIndexY() + y >= 0 && target.getPosIndexX() + x >= 0 &&
												target.getPosIndexY() + y < movePriorityMap.length && target.getPosIndexX() + x < movePriorityMap[0].length &&
												(orderOfBattle.getUnit(target.getPosIndexY() + y, target.getPosIndexX() + x) == null ||
												 orderOfBattle.getUnit(target.getPosIndexY() + y, target.getPosIndexX() + x).equals(unit))) {
								    			if (BattleMap.inRange(target, range, target.getPosIndexY() + y, target.getPosIndexX() + x)) {
								    				int diff = getMoveDistance(movementMap, unit.getPosIndexY(), unit.getPosIndexX(), target.getPosIndexY() + y, target.getPosIndexX() + x);
								    				unit.tempSubtractMovement(diff);
								    				int totalStamCost = ability.getStamCost(unit) + diff * unit.getMovementCost();
								    				if (totalStamCost <= unit.getCurrStamina() && diff >= 0) {
								    					if (ability.calculateDamage(unit, target) >= target.getCurrHP() && movePriorityMap[target.getPosIndexY() + y][target.getPosIndexX() + x] > (ability.getPriority() * 100000) + (-10000000/totalStamCost)) {
//								    						System.out.println("lethal considered: " + ability.getName() + " : " + -10000000/totalStamCost + " " + (target.getPosIndexY() + y) + " " + (target.getPosIndexX() + x) + " " + diff + " " + totalStamCost);
								    						movePriorityMap[target.getPosIndexY() + y][target.getPosIndexX() + x] = (ability.getPriority() * 100000) + (-10000000/totalStamCost);
								    						potentialMoves.add(new PotentialMove(new Coordinates(target.getPosIndexY() + y, target.getPosIndexX() + x), movePriorityMap[target.getPosIndexY() + y][target.getPosIndexX() + x]));
								    					} else if (movePriorityMap[target.getPosIndexY() + y][target.getPosIndexX() + x] > (ability.getPriority() * 1000) + (-10000/totalStamCost)) {
								    						movePriorityMap[target.getPosIndexY() + y][target.getPosIndexX() + x] = (ability.getPriority() * 1000) + (-10000/totalStamCost);
								    						potentialMoves.add(new PotentialMove(new Coordinates(target.getPosIndexY() + y, target.getPosIndexX() + x), movePriorityMap[target.getPosIndexY() + y][target.getPosIndexX() + x]));
								    					}
//							    						System.out.println("ability considered: " + ability.getName() + " : " + ((ability.getPriority() * 100) + (-10000/totalStamCost)) + " " + (target.getPosIndexY() + y) + " " + (target.getPosIndexX() + x) + " " + diff + " " + totalStamCost);
								    				}
								    				unit.tempReverseMovement(diff);
								    			}
											}
							    		}
						    		}
						    	}
								for (int y = 0; y < movementMap.length; y++) {
									for (int x = 0; x < movementMap[y].length; x++) {
										if (movementMap[y][x] <= unit.getCurrMovement()/*&& movementMap[target.getPosIndexY()][target.getPosIndexX()] < 99*/) {
									    	int diff = getMoveDistance(movementMap, y, x, target.getPosIndexY(), target.getPosIndexX());
			//						    	System.out.println(movementMap[target.getPosIndexY()][target.getPosIndexX()]);
//									    	System.out.println(diff + " " + y + " " + x);
									    	if(movePriorityMap[y][x] > diff && diff >= 0) {
									    		movePriorityMap[y][x] = diff;
									    	}
										}
									}
								}
								movePriorityMap[target.getPosIndexY()][target.getPosIndexX()] = 99;
							}
			    		}
						potentialMoves.sort(PotentialMove.getComparator());
						boolean hasMove = false;
						for (PotentialMove move: potentialMoves) {
							List<Coordinates> path = getMovePath(movementMap, orderOfBattle, unit, unit.getPosIndexY(), unit.getPosIndexX(), move.getCoordinate().getY(), move.getCoordinate().getX());
							if (path != null && path.size() > 0) {
								for (int i = 0; i < path.size(); i++) {
									Coordinates coordinates = path.get(i);
									if (movePriorityMap[coordinates.getY()][coordinates.getX()] > 98 - i)
										movePriorityMap[coordinates.getY()][coordinates.getX()] = 98 - i;
								}
								hasMove = true;
								break;
							}
						}
						if (!hasMove) {
							System.out.println("no moves found?");
							int[][] movementMapIgnoreUnits = createMovementMapIgnoreUnits(tileMap, unit);
							for (Unit target: orderOfBattle.getUnitList()) {
								for (int y = 0; y < movementMapIgnoreUnits.length; y++) {
									for (int x = 0; x < movementMapIgnoreUnits[y].length; x++) {
										if (movementMapIgnoreUnits[y][x] <= unit.getCurrMovement() && unit.isEnemy(target)) {
									    	int diff = getMoveDistance(movementMapIgnoreUnits, y, x, target.getPosIndexY(), target.getPosIndexX());
			//						    	System.out.println(movementMap[target.getPosIndexY()][target.getPosIndexX()]);
									    	if(movePriorityMap[y][x] > diff && diff >= 0) {
									    		movePriorityMap[y][x] = diff;
									    	}
										}
									}
								}
							}
//							for (int i = 0; i < movePriorityMap.length; i++) {
//								for (int j = 0; j < movePriorityMap[i].length; j++) {
//									if (String.valueOf(movementMapIgnoreUnits[i][j]).length() == 1)
//										System.out.print(" ");
//									System.out.print(movementMapIgnoreUnits[i][j] + " ");
//								}
//								System.out.println("");
//							}
//							System.out.println("");
						}
						
//						System.out.println("attacker:" + unit.getName() + " " + unit.getPosIndexY() + " " + unit.getPosIndexX());
//						for (int i = 0; i < movePriorityMap.length; i++) {
//							for (int j = 0; j < movePriorityMap[i].length; j++) {
//								if (String.valueOf(movementMap[i][j]).length() == 1)
//									System.out.print(" ");
//								System.out.print(movementMap[i][j] + " ");
//							}
//							System.out.println("");
//						}
//						System.out.println("");
//						for (int i = 0; i < movePriorityMap.length; i++) {
//							for (int j = 0; j < movePriorityMap[i].length; j++) {
//								if (String.valueOf(movePriorityMap[i][j]).length() == 1)
//									System.out.print(" ");
//								System.out.print(movePriorityMap[i][j] + " ");
//							}
//							System.out.println("");
//						}
//						System.out.println("");
					}

					@Override
					public String getName() {
						return name;
					}

					@Override
					public Queue<AIMove> getSquadMoves(TileMap tileMap, OrderOfBattle orderOfBattle, Squad origSquad) {
						//for the first pass, just try to implement squad moves in a serial order, e.g. have the same effect as we are currently looking at
						OrderOfBattle phantomOrder = Behavior.clone(orderOfBattle);
						Squad squad = phantomOrder.getSquad(origSquad.getName());
						ScoreQueue scoreQueue = new ScoreQueue();
						Queue<AIMove> squadMoves = null;
						for (Unit unit: squad.getUnitList()) {
							ScoreGrid scoreGrid = new ScoreGrid(squad, tileMap.getHeight(), tileMap.getWidth());
							scoreGrid.addUnitGrid(generateUnitGrid(tileMap, unit, phantomOrder), unit);
							scoreQueue.enQueue(scoreGrid);
						}
						if (squad.getUnitList().size() == 1) {
							squadMoves = scoreQueue.getBestMoves();
							return squadMoves;
						}
						ScoreQueue finalMoves = new ScoreQueue();
						while (scoreQueue.peek() != null) {
							ScoreGrid scoreGrid = scoreQueue.deQueue();
							for (int i = 0; i < TOP_N; i++) {
								List<Unit> unitList = scoreGrid.getUnusedUnits(squad);
								for (Unit unit: unitList) {
									updatePhantomOrder(phantomOrder, scoreGrid); //bring phantomOrder up to date with the series of actions from scoreGrid
									ScoreGrid newScoreGrid = new ScoreGrid(squad, tileMap.getHeight(), tileMap.getWidth());
									newScoreGrid.assimilate(scoreGrid);
									newScoreGrid.addUnitGrid(generateUnitGrid(tileMap, unit, phantomOrder), unit);
									for (Unit revertingUnit: phantomOrder.getUnitList()) //revert all moves on phantom Order
										revertingUnit.revert();
									if (unitList.size() != 1) {
										scoreQueue.enQueue(newScoreGrid);
									} else {
										finalMoves.enQueue(newScoreGrid);
									}
								}
							}
						}
						//make a scoreQueue
						//for each unit in squad
						//	make a scoregrid and add to scoreQueue
						//while queue is not empty
						//	dequeue the first scoregrid
						//	for each of the top x scores in the scoregrid
						//		for each unit in the squad that hasn't moved
						//			make a new scoregrid with 1 more length based off the original, and add a new entry to the grid from the current unit
						//	
						//take the first element of the squadQueue and return getBestMoves from it.
						squadMoves = finalMoves.getBestMoves();
						return squadMoves;
					}
					
//					@Override
//					public Queue<AIMove> getSquadMoves(TileMap tileMap, OrderOfBattle orderOfBattle, Squad origSquad) {
//						OrderOfBattle phantomOrder = Behavior.clone(orderOfBattle);
//						Squad squad = phantomOrder.getSquad(origSquad.getName());
//						ScoreQueue scoreQueue = new ScoreQueue();
//						ScoreGrid scoreGrid = new ScoreGrid(squad, tileMap.getHeight(), tileMap.getWidth());
//						for (Unit unit: squad.getUnitList()) {
//							updatePhantomOrder(phantomOrder, scoreGrid);
//							scoreGrid.addUnitGrid(generateUnitGrid(tileMap, unit, phantomOrder), unit);
//							for (Unit revertingUnit: phantomOrder.getUnitList())
//								revertingUnit.revert();
//						}
//						scoreQueue.enQueue(scoreGrid);
//						return scoreQueue.getBestMoves();
//					}
					
					private void updatePhantomOrder(OrderOfBattle phantomOrder, final ScoreGrid scoreGrid) {
						Queue<AIMove> bestMoves = scoreGrid.getBestMoves();
						for (AIMove bestMove: bestMoves) {
//							
//						}
//						while (bestMoves.peek() != null) {
//							AIMove bestMove = bestMoves.poll();
							Unit currUnit = bestMove.getUnit();
							Ability ability = bestMove.getAbility();

							currUnit.saveCurrentStats();
							if (bestMove.getTarget() != null) bestMove.getTarget().saveCurrentStats();
							
							//need to be careful that these units are making legal moves and shit
							phantomOrder.moveToPhantomPlane(currUnit);
							phantomOrder.movePhantomUnit(currUnit, bestMove.getY(), bestMove.getX());
							phantomOrder.moveToRegularPlane(currUnit);
							
							if (ability != null) {
								currUnit.setPotentialAbility(ability);
								currUnit.useAbility(ability);
								Unit target = bestMove.getTarget();
								ability.dealDamage(currUnit, target);
								if (target.isDying) {
									System.out.println("UNIT IS DYING: " + target.getName() + " " + target.getPosIndexY() + " " + target.getPosIndexX() + " " + target.currStats.HP);
									phantomOrder.unitMap[target.getPosIndexY()][target.getPosIndexX()] = null;
								}
							}
						}
						//TODO: this needs to be done before this shit will work, otherwise will while loop foreverrrr
					}
					
					private ScoreKeeper[][] generateUnitGrid(TileMap tileMap, Unit unit, OrderOfBattle orderOfBattle) {
						ScoreKeeper[][] unitGrid = new ScoreKeeper[tileMap.getHeight()][tileMap.getWidth()];
						//factors for scoring
							//ability to kill enemy (+enemy threat)
							//ability to harm enemy (+enemy threat)
							//ability priority
							//closeness by pathing to enemy
							//stamina consumed
						boolean hasMove = false;
						int movementMap[][] = createMovementMap(unit, tileMap, orderOfBattle);
						for (int y = 0; y < movementMap.length; y++) {
							for (int x = 0; x < movementMap[y].length; x++) {
								if (movementMap[y][x] <= unit.getCurrMovement() && (orderOfBattle.getUnit(y, x) == null || (orderOfBattle.getUnit(y, x).equals(unit)))) {
									for (Unit target: orderOfBattle.getUnitList()) {
										if (unit.isEnemy(target) && !target.isDying()) {
											for (Ability ability: unit.getActiveAbilities()) {
												if (BattleMap.inRange(target.getPosIndexY(), target.getPosIndexX(), y, x, ability)) {
													unitGrid[y][x] = new ScoreKeeper(unit, ability, target, movementMap[y][x]);
													hasMove = true;
													//update score based on factors
												}
											}
										}
									}
								}
							}
						}
						int[][] movementMapIgnoreUnits = null;
						if (true) {//if (!hasMove) {
							movementMapIgnoreUnits = createMovementMapIgnoreUnits(tileMap, unit);
//							movementMapIgnoreUnits = movementMap;
							for (Unit target: orderOfBattle.getUnitList()) {
								if (unit.isEnemy(target)) {
									for (int y = 0; y < movementMapIgnoreUnits.length; y++) {
										for (int x = 0; x < movementMapIgnoreUnits[y].length; x++) {
											if (orderOfBattle.getUnit(y, x) != null && !orderOfBattle.getUnit(y, x).equals(unit) && movementMapIgnoreUnits[y][x] == 0) {
												System.err.println("this should never happen..." + unit.getName() + " " + orderOfBattle.getUnit(y, x).getName() + " " + y + " " + x);
//												for (Unit u: orderOfBattle.getUnitList()) {
//													System.out.println(u.getName() + " " + u.getPosIndexY() + " " + u.getPosIndexX());
//												}
											}
											if (movementMapIgnoreUnits[y][x] <= unit.getCurrMovement() && (orderOfBattle.getUnit(y, x) == null || (orderOfBattle.getUnit(y, x).equals(unit)))) {
										    	int diff = getMoveDistance(movementMapIgnoreUnits, y, x, target.getPosIndexY(), target.getPosIndexX());
										    	if(diff >= 0 && (unitGrid[y][x] == null || unitGrid[y][x].getDistanceScore() > diff)) {
													unitGrid[y][x] = new ScoreKeeper(unit, diff, movementMapIgnoreUnits[y][x]);
													hasMove = true;
//													System.out.println("Path found " + unit.getName() + " " + target.getName() +  " " + y + " " + x);
//										    	} else if (diff < 0 && unit.getName().equals("M5")) {
//										    		System.out.println("Sorry, Path not found " + unit.getName() + " " + target.getName() +  " " + y + " " + x + " " + movementMapIgnoreUnits[y][x]);
										    	}
											} 
//											} else if (movementMapIgnoreUnits[y][x] <= unit.getCurrMovement()) {
//												System.out.println("this is in that spot! " + orderOfBattle.getUnit(y, x).getName() + " " + y + " " + x + " " + unit.getCurrMovement());
//											}
//											if (movementMapIgnoreUnits[y][x] <= unit.getCurrMovement()) {
//												System.out.println(y + " " + x);
//											}
										}
									}
								}
							}
						}
						if (!hasMove) {
							System.out.println("NO MOVE FOUND: " + unit.getName());
							printMovementMap(movementMapIgnoreUnits);
							printUnitGrid(orderOfBattle, movementMapIgnoreUnits);
//							int y = unit.getPosIndexY();
//							int x = unit.getPosIndexX();
//							unitGrid[y][x] = new ScoreKeeper(unit, , movementMapIgnoreUnits[y][x]);
						}
						//how to score
							//find all the spaces the unit can move to, give it a score based on stamina spent
							//for each of these spaces
								//for each enemy
									//for each ability
										//if enemy is in range of this space
											//update score based on factors for scoring (see above)
						return unitGrid;
					}

					private void printMovementMap(int[][] movementMap) {
						for (int i = 0; i < movementMap.length; i++) {
							for (int j = 0; j < movementMap[i].length; j++) {
								if (String.valueOf(movementMap[i][j]).length() == 1)
									System.out.print(" ");
								System.out.print(movementMap[i][j] + " ");
							}
							System.out.println("");
						}
						System.out.println("");
					}
					
					private void printUnitGrid(OrderOfBattle orderOfBattle, int[][] movementMap) {
						for (int i = 0; i < movementMap.length; i++) {
							for (int j = 0; j < movementMap[i].length; j++) {
								if (orderOfBattle.getUnit(i, j) == null) {
									if (String.valueOf(movementMap[i][j]).length() == 1)
										System.out.print(" ");
									System.out.print(movementMap[i][j] + " ");
								} else if (String.valueOf(orderOfBattle.getUnit(i, j).getName()).length() == 1) {
									System.out.print(" ");
									System.out.print(orderOfBattle.getUnit(i, j).getName() + " ");
								} else {
									System.out.print(orderOfBattle.getUnit(i, j).getName() + " ");
								}
							}
							System.out.println("");
						}
						System.out.println("");
					}
				};
				break;
			default:
		}
	return behavior;
	}
	
    public static int[][] createMovementMap(Unit currUnit, TileMap tileMap, OrderOfBattle orderOfBattle) {
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
    	}
    	return movementMap;
    }
	
	public static OrderOfBattle clone(OrderOfBattle orderOfBattle) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(orderOfBattle);
			oos.flush();
			oos.close();
			bos.close();
			byte[] byteData = bos.toByteArray();
			ByteArrayInputStream bais = new ByteArrayInputStream(byteData);
			return (OrderOfBattle) new ObjectInputStream(bais).readObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	//the reason this doesn't work is because the move distance formula is only guaranteed to work for paths headed to the origin of the created map, e.g. the current position of
	//the unit. so when a different option is the only viable alternative all of this shit just fails because it wasn't build to search for alternative paths. I believe this
	//algorithm can be fixed reasonably with a queue based approach and will only marginally increase runtime.
    public static int getMoveDistance(final int[][] movementMap, int sourceY, int sourceX, int targetY, int targetX) {	
    	if (sourceY == targetY && sourceX == targetX)
    		return 0;
//    	List<Coordinates> path = new ArrayList<Coordinates>();
    	Queue<PathCoordinates> paths = new PriorityQueue<PathCoordinates>(10, buildComparator());
    	paths.add(new PathCoordinates(targetY, targetX, 0, movementMap[targetY][targetX]));
    	while (paths.peek() != null) {
    		PathCoordinates path = paths.poll();
    		if (sourceY == path.getY() && sourceX == path.getX()) {
    			return path.getLength() - 1;
    		}
    		if (path.getY() - 1 > 0 && movementMap[path.getY() - 1][path.getX()] == path.getValue() - 1) {
    			paths.add(new PathCoordinates(path.getY() - 1, path.getX(), path.getLength() + 1, path.getValue() - 1));
    		}
    		if (path.getY() + 1 < movementMap.length && movementMap[path.getY() + 1][path.getX()] ==  path.getValue() - 1) {
    			paths.add(new PathCoordinates(path.getY() + 1, path.getX(), path.getLength() + 1,  path.getValue() - 1));
    		}
    		if (path.getX() - 1 > 0 && movementMap[path.getY()][path.getX() - 1] == path.getValue() - 1) {
    			paths.add(new PathCoordinates(path.getY(), path.getX() - 1, path.getLength() + 1,  path.getValue() - 1));
    		}
    		if (path.getX() + 1 < movementMap[0].length && movementMap[path.getY()][path.getX() + 1] == path.getValue() - 1) {
    			paths.add(new PathCoordinates(path.getY(), path.getX() + 1, path.getLength() + 1,  path.getValue() - 1));
//    		} else {
//    			System.out.println("no path found " + path.size() + " " + sourceY + " " + sourceX + " " + path.get(0).getY() + " " + path.get(0).getX());
//    			for (Coordinates coord: path) {
//    				System.out.println(coord.getY() + " " + coord.getX());
//    			}
//    			return -1;
//    			return path.size();
    		}
    	}
    	return - 1;
	}
    
	static Comparator buildComparator() {
		Comparator<PathCoordinates> comparator = new Comparator<PathCoordinates>() {
			@Override
			public int compare(PathCoordinates o1, PathCoordinates o2) {
				if (o1.getLength() - o2.getLength() == 0) return 1;
				return o2.getLength() - o1.getLength();
			}
		};
		return comparator;
	}
        
    public static List<Coordinates> getMovePath(final int[][] movementMap, OrderOfBattle orderOfBattle, Unit unit, int sourceY, int sourceX, int targetY, int targetX) {	
    	List<Coordinates> path = new ArrayList<Coordinates>();
    	path.add(new Coordinates(targetY, targetX));
    	int value = movementMap[targetY][targetX];
    	while (path.get(0).getY() != sourceY || path.get(0).getX() != sourceX) {
    		if (path.get(0).getY() - 1 > 0 && movementMap[path.get(0).getY() - 1][path.get(0).getX()] == value - 1) {
    			value--;
    			path.add(0, new Coordinates(path.get(0).getY() - 1, path.get(0).getX()));
    		} else if (path.get(0).getY() + 1 < movementMap.length && movementMap[path.get(0).getY() + 1][path.get(0).getX()] == value - 1) {
    			value--;
    			path.add(0, new Coordinates(path.get(0).getY() + 1, path.get(0).getX()));
    		} else if (path.get(0).getX() - 1 > 0 && movementMap[path.get(0).getY()][path.get(0).getX() - 1] == value - 1) {
    			value--;
    			path.add(0, new Coordinates(path.get(0).getY(), path.get(0).getX() - 1));
    		} else if (path.get(0).getX() + 1 < movementMap[0].length && movementMap[path.get(0).getY()][path.get(0).getX() + 1] == value - 1) {
    			value--;
    			path.add(0, new Coordinates(path.get(0).getY(), path.get(0).getX() + 1));
    		} else {
//    			System.out.println("no path found");
    			return null;
//    			return path.size();
    		}
    	}
    	return path;
	}
    
    public static int[][] createMovementMapIgnoreUnits(TileMap tileMap, Unit currUnit) {
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
        	if (y > 0 && movementMap[y-1][x] > value && tileMap.getTile(y-1, x).isAccessable()) {
        		movementMap[y-1][x] = value;
            	queue.add(new Coordinates(y-1,x));
        	}
        	if (y < tileMap.getHeight() - 1 && movementMap[y+1][x] > value && tileMap.getTile(y+1, x).isAccessable()) {
        		movementMap[y+1][x] = value;
            	queue.add(new Coordinates(y+1,x));
        	}
        	if (x > 0 && movementMap[y][x-1] > value && tileMap.getTile(y, x-1).isAccessable()) {
        		movementMap[y][x-1] = value;
            	queue.add(new Coordinates(y,x-1));
        	}
        	if (x < tileMap.getWidth() - 1 && movementMap[y][x+1] > value && tileMap.getTile(y, x+1).isAccessable()) {
    			movementMap[y][x+1] = value;
        		queue.add(new Coordinates(y,x+1));
    		}
    	}
    	return movementMap;
    }
    
    public class PotentialMove {
    	private Coordinates coordinates;
    	private int priority;
    	
    	public PotentialMove(Coordinates coordinates, int priority) {
    		this.coordinates = coordinates;
    		this.priority = priority;
    	}
    	
    	public static Comparator<PotentialMove> getComparator() {
    		return new Comparator<PotentialMove>() {

    			@Override
    			public int compare(PotentialMove o1, PotentialMove o2) {
    				return o1.getPriority() - o2.getPriority();
    			}
        	};
    	}
    	
    

		public Coordinates getCoordinate() {
			return coordinates;
		}

		public int getPriority() {
			return priority;
		}
	}
}
