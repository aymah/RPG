package map;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import event.Ability;
import event.Encounter;
import event.MapEvent;
import unit.Hero;
import unit.OrderOfBattle;
import unit.Party;
import unit.Squad;
import unit.Unit;
import unit.UnitFactory;

public class SimBattle {

	GameFrame frame;
	EmpirePanelManager manager;
	List<Unit> unitList;
	Party party;
	Encounter encounter;
	int expReward;
	int goldReward;
	String techReward;
	String provReward;
	int maxMeeleSpace;
	
	
	public SimBattle(String destination, GameFrame frame, Party party, EmpirePanelManager manager, Encounter encounter) {
		this.frame = frame;
		this.party = party;
		this.manager = manager;
		this.encounter = encounter;
		unitList = new ArrayList<Unit>();
		loadSimBattle(destination);
		simulateBattle();
		//simulate battle based on party vs units on map
		//load a results page that details the results of the battle
	}


	private void loadSimBattle(String mapName) {
		String str = "";
		Scanner scanner;
		InputStream in = getClass().getResourceAsStream("/testMaps/" + mapName + ".txt"); 
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		try {
			while ((str = br.readLine()) != null) {
				scanner = new Scanner(str);
				if (scanner.hasNext()) {
					String next = scanner.next();
					if (next.equals("UNIT")) {
						String filename = scanner.next();
						String faction = scanner.next();
						String name = scanner.next();
						int level = Integer.parseInt(scanner.next());
						UnitFactory unitFactory = new UnitFactory(filename);
						Unit unit = unitFactory.createInstance(faction, name, level);
						unit.setPosition(0, 0);
						System.out.println("unit " + unit.getName()+ " " + unit.getLevel());
						unitList.add(unit);
					}
					if (next.equals("MEELESPACE")) {
						maxMeeleSpace = Integer.parseInt(scanner.next());
					}
					if (next.equals("REWARD")) {
						expReward = Integer.parseInt(scanner.next());
						goldReward = Integer.parseInt(scanner.next());
					}
					if (next.equals("TECHREWARD")) {
						techReward = scanner.nextLine().substring(1);
					}
					if (next.equals("PROVREWARD")) {
						provReward = scanner.nextLine().substring(1);
					}
				}
				scanner.close();
			}
			br.close();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void simulateBattle() {
		List<Unit> unitList = new ArrayList<Unit>();
		unitList.addAll(party.getUnitList());
		unitList.addAll(this.unitList);
		TileMap tileMap = new TileMap(1, 1);
		OrderOfBattle orderOfBattle = new OrderOfBattle(tileMap);
		int allyMeeleSpace = 0;
		int enemyMeeleSpace = 0;
		for (Unit unit: unitList) {
			Squad squad = new Squad(unit);
			orderOfBattle.addSquad(squad);
			orderOfBattle.addUnit(unit);
			squad.generateUnitQueue();
			unit.resetMeeleAttacks();
		}
		orderOfBattle.setInitialOrdering();
		while (orderOfBattle.lastFactionStanding().equals("")) {
			Unit bestTarget = null;
			Ability bestAbility = null;
			int bestDamage = 0;
			Unit unit = orderOfBattle.getNextUnit();
			unit.resetMeeleAttacks();
			if (unit.getFaction().equals("ALLY") && unit.takesMeeleSpace())
				allyMeeleSpace--;
			else if (unit.takesMeeleSpace())
				enemyMeeleSpace--;
			unit.setMeeleSpace(false);
			for (Ability ability: unit.getActiveAbilities()) {
				for (Unit target: orderOfBattle.getUnitList()) {
					if (target.isEnemy(unit)) {
						//
						if ((ability.getRange() > 1 || (target.canBeMeeleAttacked()) && ((target.getFaction().equals("ALLY") && (target.takesMeeleSpace() || allyMeeleSpace < maxMeeleSpace) && enemyMeeleSpace < maxMeeleSpace) || (target.getFaction().equals("ENEMY") && (target.takesMeeleSpace() || enemyMeeleSpace < maxMeeleSpace) && allyMeeleSpace < maxMeeleSpace)))) {
							int damage = ability.calculateDamage(unit, target);
							if ((damage > bestDamage)/* || 
							   ((maxMeeleSpace < unitList.size() && bestAbility.getRange() == 1 && ability.getRange() > 1) ||
							   !(maxMeeleSpace < unitList.size() && bestAbility.getRange() > 1 && ability.getRange() == 1))*/) { //if there are more units than maxMeeleSpace and unit has ranged attack, used ranged attack.
								bestTarget = target;
								bestAbility = ability;
								bestDamage = damage;
							}
						}
					}
				}
			}
			if (bestTarget != null) {
				bestAbility.dealDamage(unit , bestTarget);
				if (bestAbility.getRange() == 1) {
					bestTarget.incrementMeeleAttacks();
					if (!unit.takesMeeleSpace()) {
						unit.setMeeleSpace(true);
						if (unit.getFaction().equals("ALLY"))
							allyMeeleSpace++;
						else
							enemyMeeleSpace++;
					}
					if (!bestTarget.takesMeeleSpace()) {
						if (bestTarget.getFaction().equals("ALLY") && !bestTarget.takesMeeleSpace())
							allyMeeleSpace++;
						else if (!bestTarget.takesMeeleSpace())
							enemyMeeleSpace++;
						bestTarget.setMeeleSpace(true);
					}
				}
				System.out.println("attack! " + unit.getName() + " attacks " + bestTarget.getName() + " with " + bestAbility.getName() + " for " + bestDamage + ". Remaining HP: " + bestTarget.getCurrHP() + " Remaining Armor: " + bestTarget.getTotalAbsorption() + " Ally Meele Space " + allyMeeleSpace + " Enemy Meele Space " + enemyMeeleSpace);
				if (bestTarget.isDying()) {
					orderOfBattle.removeUnit(bestTarget);
					if (bestTarget.getFaction().equals("ALLY"))
						allyMeeleSpace--;
					else
						enemyMeeleSpace--;
					System.out.println("Unit Killed! " + bestTarget.getName());
					bestTarget.setDying(false);
				}
			} else {
				System.out.println("attack! " + unit.getName() + " could not find a target to attack.");
			}
			//find target with highest damage
			//hit that target
		}
		party.endBattleStatUpdates();
		for (Unit unit: party.getUnitList()) {
			unit.setMeeleSpace(false);
		}

		party.restoreArmor();
		if (orderOfBattle.lastFactionStanding().equals("ALLY")) {
			System.out.println("Victory!");
			winBattle();
		} else {
			System.out.println("Defeat!");
			loseBattle();
		}
	}
	
//	private void simulateBattle() {
//		int allyBattleScore = calculateBattleScore(party.getUnitList());
//		int enemyBattleScore = calculateBattleScore(unitList);
//		System.out.println(allyBattleScore + " " + enemyBattleScore);
//		if (allyBattleScore > enemyBattleScore) {
//			System.out.println("Victory!");
//			winBattle();
//		}
//	}
//
//	private int calculateBattleScore(List<Unit> unitList) {
//		int battleScore = 0;
//		for(Unit unit: unitList) {
//			System.out.println(unit.getName() + " " + unit.getLevel());
//			if (unit.getClass() == Hero.class)
//				battleScore += 10 + ((unit.getLevel() + 1) * 3);
//			else
//				battleScore += (5 * party.getUnitValue(unit.getType())) + ((unit.getLevel() + 1) * party.getUnitValue(unit.getType()));
//		}
//		return battleScore;
//	}


	private void winBattle() {
		SimVictoryRewardsPanel menuPanel = manager.getSimVictoryPanel();
		menuPanel.displayPanel();
		menuPanel.setExpReward(expReward);
		menuPanel.setGoldReward(goldReward);
		menuPanel.setTechReward(techReward);
		menuPanel.setProvReward(provReward);
		menuPanel.executeRewards();
		encounter.battleResult("Win");
		manager.changeDominantPanel(menuPanel);
		frame.setAcceptingInput(true);
		frame.refresh();
	}
	
	private void loseBattle() {
		encounter.battleResult("Loss");		
	}
}
