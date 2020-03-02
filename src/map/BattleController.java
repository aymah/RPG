package map;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import event.Ability;
import event.MenuItem;
import unit.Army;
import unit.Party;
import unit.Squad;
import unit.Unit;

public class BattleController {

	BattlePanel panel;
	BattleMap map;
	
	public void keyTyped(KeyEvent e) {
		
	}
	
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (map.isPartyPlacementMode() || map.isArmyPlacementMode()) {
			keyPressedPlacementMode(e);
			keyCode = 0;
		}
		if (map.isTargetingMode()) {
			keyPressedTargetingMode(e);
			keyCode = 0;
		}
		if (map.isFreeSelectMode()) {
			keyPressedFreeSelectMode(e);
			keyCode = 0;
		}
		boolean moved = false;
		switch (keyCode) {
			case 87:
				moveUp();
				break;
			case 83:
				moveDown();
				break;
			case 65:
				moveLeft();
				break;
			case 68:
				moveRight();
				break;
			case 38:
				screenUp();
				break;
			case 40:
				screenDown();
				break;
			case 37:
				screenLeft();
				break;
			case 39:
				screenRight();
				break;
			case 69:
			case 10:
				TileMap tileMap = map.getTileMap();
				if (tileMap.getTile(map.getMovementIndexY(), map.getMovementIndexX()).isAccessable() && map.getOrderOfBattle().getUnit(map.getMovementIndexY(), map.getMovementIndexX()) == null || map.getOrderOfBattle().getUnit(map.getMovementIndexY(), map.getMovementIndexX()) == map.getCurrUnit()) {
					moveCurrUnit();
					openActionMenu();
				}
				break;
			case 70:
				toggleFreeSelect();
				break;
			case 82:
				panel.toggleRangeMap();
				break;
			case 84:
				panel.toggleEnemyMap();
				break;
			case 80:
				endTurn();
				break;
			case 9:
				map.cycleSquadUnits();
				break;
			case 192:
				winBattle();
				break;
		}
//		currUnit.setPosition(avatarIndexY, avatarIndexX);
		panel.repaint();
	}

	private void keyPressedPlacementMode(KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch (keyCode) {
//		case 87:
//		case 83:
		case 65:
			selectPrevPosition();
			break;
		case 68:
			selectNextPosition();
			break;
		case 38:
			screenUp();
			break;
		case 40:
			screenDown();
			break;
		case 37:
			screenLeft();
			break;
		case 39:
			screenRight();
			break;
		case 69:
		case 10:
			placeUnit();
			break;
		case 81:
		case 27:
			openSquadPlacementMenu();
			break;
		}
	}
	
	private void keyPressedTargetingMode(KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch (keyCode) {
		case 87:
			moveTargetUp();
			break;
		case 83:
			moveTargetDown();
			break;
		case 65:
			moveTargetLeft();
			break;
		case 68:
			moveTargetRight();
			break;
		case 38:
			screenUp();
			break;
		case 40:
			screenDown();
			break;
		case 37:
			screenLeft();
			break;
		case 39:
			screenRight();
			break;
		case 69:
		case 10:
			if (map.currAbilityCanTargetSelf() || 
			   (map.getTargetedUnit() != null && 
			    map.getTargetedUnit().getFaction().equals("ENEMY"))) {
				if (map.getCurrAbility().hasParam("Area Of Effect")) {
					map.attackAOE();
				}else if (map.getTargetedUnit() != null) {
					map.attackTarget();
				}
			}
			break;
		case 81:
		case 27:
			backToMenu();
		}
	}

	private void keyPressedFreeSelectMode(KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch (keyCode) {
		case 87:
			moveSelectorUp();
			break;
		case 83:
			moveSelectorDown();
			break;
		case 65:
			moveSelectorLeft();
			break;
		case 68:
			moveSelectorRight();
			break;
		case 38:
			screenUp();
			break;
		case 40:
			screenDown();
			break;
		case 37:
			screenLeft();
			break;
		case 39:
			screenRight();
			break;
		case 69:
		case 10:
			if (map.getTargetedUnit() != null) {
				openUnitInfo();
			}
			break;
		case 27:
		case 70:
		case 81:
			toggleFreeSelect();
			break;
		}
	}
	
	private void openUnitInfo() {
		BattlePanelManager manager = (BattlePanelManager)map.getManager();
		manager.getUnitInfoPanel().displayUnit(map.getTargetedUnit());
	}

	private void selectPrevPosition() {
		int wrapAround = 0;
		Coordinates coordinates = null;
		if (map.isPartyPlacementMode())
			wrapAround = map.getPartyPlacements().size() - 1;
		if (map.isArmyPlacementMode())
			wrapAround = map.getArmyPlacements().size() - 1;
		if (map.getPlacementIndex() - 1 < 0)
			map.setPlacementIndex(wrapAround);
		else
			map.setPlacementIndex(map.getPlacementIndex() - 1);
		centerScreen(placementIndex);
	}
	
	private void selectNextPosition() {
		int limit = 0;
		if (partyPlacementMode)
			limit = partyPlacements.size();
		if (armyPlacementMode)
			limit = armyPlacements.size();
		if (placementIndex + 1 >= limit)
			placementIndex = 0;
		else
			placementIndex++;
		centerScreen(placementIndex);
	}
	
	private void placeUnit() {
		Army army = party.getArmy();
		boolean sameSquad = true;
		Unit unit = currUnit;
		currSquad.getUnitPlacementList().remove(currUnit);
		if (currSquad.getUnitPlacementList().size() > 0) {
			currUnit = currSquad.getUnitPlacementList().get(0);
		} else {
			sameSquad = false;
			squadPlacementList.remove(currSquad);
			if (squadPlacementList.size() > 0) {
				currSquad = squadPlacementList.get(0);	
				currUnit = currSquad.getUnitPlacementList().get(0);
			}
		} 
//		orderOfBattle.addSquad(new Squad(currUnit)); //remove this later, these squads should be added before the placements. 
													 //This will allow the player to see the move order before placements as well.
													 //This is only here temporarily until the party/army squad sorting is implemented.
		if (partyPlacementMode) {
			Coordinates coordinates = partyPlacements.get(placementIndex);
			unit.setPosition(coordinates.getY(), coordinates.getX());
			orderOfBattle.addUnit(unit);
			partyPlacements.remove(coordinates);
			placementIndex = 0;
			numPlaced++;
			if (partyPlacements.size() > 0 && squadPlacementList.size() > 0) {
//				currUnit = party.getUnitPlacementList().get(0);
//				if (sameSquad)
//					openUnitPlacementMenu();
//				else
					openSquadPlacementMenu();
		    	centerScreen(partyPlacements.get(placementIndex).getY(), partyPlacements.get(placementIndex).getX());
			} else if (armyPlacements.size() > 0 && squadPlacementList.size() > 0){
				numPlaced = 0;
//				party.addUnitPlacementList(army.getUnitListNotInParty());
//				currUnit = party.getUnitPlacementList().get(0);
//				if (sameSquad)
//					openUnitPlacementMenu();
//				else
					openSquadPlacementMenu();
		    	centerScreen(armyPlacements.get(placementIndex).getY(), armyPlacements.get(placementIndex).getX());
				partyPlacementMode = false;
				armyPlacementMode = true;
			} else {
				startBattle();
			}
		} else if (armyPlacementMode) {
			Coordinates coordinates = armyPlacements.get(placementIndex);
			unit.setPosition(coordinates.getY(), coordinates.getX());
			orderOfBattle.addUnit(unit);
			armyPlacements.remove(coordinates);
			placementIndex = 0;
			numPlaced++;
			if (armyPlacements.size() > 0) {
//				currSquad = tempSquadList.get(0);
//				if (sameSquad)
//					openUnitPlacementMenu();
//				else
					openSquadPlacementMenu();
		    	centerScreen(armyPlacements.get(placementIndex).getY(), armyPlacements.get(placementIndex).getX());
			} else {
				startBattle();
			}
		}
	}
	
	private void moveSelectorUp() {
		targetingIndexY--;
	}

	private void moveSelectorDown() {
		targetingIndexY++;
	}
	
	private void moveSelectorLeft() {
		targetingIndexX--;
	}
	
	private void moveSelectorRight() {
		targetingIndexX++;
	}
	
	private void moveUp() {
		if (movementIndexY - 1 >= 0 && ((tileMap.getTile(movementIndexY - 1, movementIndexX).isAccessable() &&
			(orderOfBattle.getUnit(movementIndexY - 1, movementIndexX) == null || orderOfBattle.getUnit(movementIndexY - 1, movementIndexX).getFaction().equals("ALLY"))) || currUnit.isFlying()) &&
			movementMap[movementIndexY - 1][movementIndexX] <= currUnit.getCurrMovement()) 
			movementIndexY--;
	}
	
	private void moveDown() {
		if (movementIndexY + 1 < tileMap.getHeight() && ((tileMap.getTile(movementIndexY + 1, movementIndexX).isAccessable() &&
			(orderOfBattle.getUnit(movementIndexY + 1, movementIndexX) == null || orderOfBattle.getUnit(movementIndexY + 1, movementIndexX).getFaction().equals("ALLY"))) || currUnit.isFlying()) &&
			movementMap[movementIndexY + 1][movementIndexX] <= currUnit.getCurrMovement()) 
			movementIndexY++;
	}
	
	private void moveLeft() {
		if (movementIndexX - 1 >= 0 && ((tileMap.getTile(movementIndexY, movementIndexX - 1).isAccessable() &&
			(orderOfBattle.getUnit(movementIndexY, movementIndexX - 1) == null  || orderOfBattle.getUnit(movementIndexY, movementIndexX - 1).getFaction().equals("ALLY"))) || currUnit.isFlying()) &&
			movementMap[movementIndexY][movementIndexX - 1] <= currUnit.getCurrMovement()) 
			movementIndexX--;
	}
	
	private void moveRight() {
		if (movementIndexX + 1 < tileMap.getWidth() && ((tileMap.getTile(movementIndexY, movementIndexX + 1).isAccessable() &&
			(orderOfBattle.getUnit(movementIndexY, movementIndexX + 1) == null  || orderOfBattle.getUnit(movementIndexY, movementIndexX + 1).getFaction().equals("ALLY"))) || currUnit.isFlying()) &&
			movementMap[movementIndexY][movementIndexX + 1] <= currUnit.getCurrMovement()) 
			movementIndexX++;
	}
	
	private void screenUp() {
		screenIndexY--;
		if (screenIndexY < -10)
			screenIndexY = -10;
	}
	
	private void screenDown() {
		screenIndexY++;
		if (screenIndexY >= height)
			screenIndexY = height - 1;
	}
	
	private void screenLeft() {
		screenIndexX--;
		if (screenIndexX < -19)
			screenIndexX = -19;
	}
	
	private void screenRight() {
		screenIndexX++;
		if (screenIndexX >= width)
			screenIndexX = width - 1;
	}
	
	private void moveTargetUp() {
		if (inRange(targetingIndexY - 1, targetingIndexX, ability)  && targetingIndexY > 0)
			targetingIndexY--;
	}
	
	private void moveTargetDown() {
		if (inRange(targetingIndexY + 1, targetingIndexX, ability)  && targetingIndexY < tileMap.getHeight() - 1)
			targetingIndexY++;
	}
	
	private void moveTargetLeft() {
		if (inRange(targetingIndexY, targetingIndexX - 1, ability) && targetingIndexX > 0)
			targetingIndexX--;
	}
	
	private void moveTargetRight() {
		if (inRange(targetingIndexY, targetingIndexX + 1, ability) && targetingIndexX < tileMap.getWidth() - 1)
			targetingIndexX++;
	}
	
	private void moveCurrUnit() {
		Unit unit = map.getCurrUnit();
		int movementUsed = map.getMovementUsed();
		unit.subtractMovement(movementUsed);
		List<Coordinates> path = PathfindingUtilities.makeMovementPath();
		panel.animateMovement();
	}
	
	private void openActionMenu() {
		BattlePanelManager manager = (BattlePanelManager) map.getManager();
		MenuPanel menuPanel = manager.getMenuPanel();
		menuPanel.displayPanel();
		((BattleMenuPanel)menuPanel).resetSelector();
		manager.changeDominantPanel(menuPanel);
	}
	
	//i dont remember what the math does, should make new functions describing what they do when you get around to reimplementing free select shite.
	public void toggleFreeSelect() {
		if (map.isFreeSelectMode()) {
//			targetingIndexY = screenIndexY - 5;
//			targetingIndexX = screenIndexX - 9;
			panel.setFreeSelectMode(false);
		} else {
//			targetingIndexY = screenIndexY + 5;
//			targetingIndexX = screenIndexX + 9;
//			movementIndexY = avatarIndexY;
//			movementIndexX = avatarIndexX;
			panel.setFreeSelectMode(true);
		}
	}
	

	
//	public void setTargetingMode(Ability ability) {
//		targetingMode = true;
//		targetingIndexY = avatarIndexY;
//		targetingIndexX = avatarIndexX;
//		this.ability = ability;
//	}
	
	public void endTurn() {
		aiTurn = false;
		frame.setAcceptingInput(true);
		Squad squad = currSquad;
		currUnit.endTurn();
		currUnit = orderOfBattle.getNextUnit();
		currSquad = currUnit.getSquad();
		if (!currSquad.equals(squad)) { //if a new squad
			currUnit.beginSquadTurn(this);
			if (currUnit.getFaction().equals("ENEMY")) {
				System.out.println("assigning squad moves");
				squadMoves = currSquad.getBehavior().getSquadMoves(tileMap, orderOfBattle, currSquad);
			}
		}
		image = currUnit.getImage();
		orderOfBattle.generatePhantomQueue();
		setCoordinates(currUnit.getPosIndexY(), currUnit.getPosIndexX());
		screenIndexY = currUnit.getPosIndexY() - 5;
		screenIndexX = currUnit.getPosIndexX() - 9;
		if(currUnit.getFaction().equals("ENEMY")) {
			aiTurn();
		}
		this.repaint();
	}
	
	private void aiTurn() {
		aiTurn = true;
		frame.setAcceptingInput(false);
		aiMove = squadMoves.poll();
		System.out.print("AI: polling next move: " + aiMove.getUnit().getName() + " " + aiMove.getY() + " " + aiMove.getX());
		if (aiMove.getTarget() != null)
			System.out.println(" " + aiMove.getTarget().getName());
		else
			System.out.println("");
		String unitName = aiMove.getUnit().getName();
		for (Unit unit: orderOfBattle.getUnitList()) {
			if (unit.getName().equals(unitName) && unit.getFaction().equals("ENEMY")) {
				currUnit = unit;
			}
		}
//		currUnit = aiMove.getUnit();
		if (!currUnit.isDying()) {
			aiMove();
			aiUseAbility();
	//		try {
	//			Thread.sleep(1000);
	//		} catch (InterruptedException e) {
	//			e.printStackTrace();
	//		}
		}
		endTurn();
	}


	private void aiMove() {
		//calculate possible moves
    	movementMap = createMovementMap(currUnit);
//    	int[][] movePriorityMap = createMovePriorityMap();
//    	aiSelectMove(movePriorityMap);
    	
    	//use these two lines to set where you want the unit to move
    	movementIndexY = aiMove.getY();
    	movementIndexX = aiMove.getX();
    	
    	
    	//set movementIndexX and Y
    	//move near an ALLY unit i guess
    	//maybe create move priority map

    	moveCurrUnit();
//		currUnit.setPosition(avatarIndexY, avatarIndexX);
		//choose move
		
	}
	
	private void winBattle() {
		System.out.println("you win!");
		VictoryRewardsPanel menuPanel = manager.getVictoryPanel();
		menuPanel.displayPanel();
		menuPanel.setExpReward(expReward);
		menuPanel.setGoldReward(goldReward);
		menuPanel.executeRewards();
		manager.changeDominantPanel(menuPanel);
		frame.setAcceptingInput(true);
		frame.refresh();
//		endBattle();
	}
	
	private void loseBattle() {
		endBattle();
		quitToMenu();
	}
	
	private void openSquadPlacementMenu() {
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
    	BattleMap battleMap = ((BattlePanelManager) manager).getBattleMap();
    	Party party = battleMap.getParty();
		for (Squad squad: squadPlacementList) {
			MenuItem item = new MenuItem() {
				private String name = squad.getName();
				
				@Override
				public void execute(GamePanel panel) {
					BattleMenuPanel menuPanel = (BattleMenuPanel) panel;
					currSquad = squad;
					battleMap.openUnitPlacementMenu();
				}

				@Override
				public String getName() {
					return name;
				}
				
			};
			menuItems.add(item);
		}
		currSquad = squadPlacementList.get(0);
    	squadPlacementMenuPanel = new BattleMenuPanel("Squad Placement Menu", frame, (BattlePanelManager) manager, menuItems, 2);
    	squadPlacementMenuPanel.displayPanel();
    	manager.changeDominantPanel(squadPlacementMenuPanel);
		frame.refresh();
	}
	
	private void backToMenu() {
		panel.setTargetingMode(false);
		panel.setFreeSelectMode(false);
		map.setTargetingIndexY(map.getAvatarIndexY());
		map.setTargetingIndexX(map.getAvatarIndexX());
		map.setScreenIndexY(map.getAvatarIndexY() - 5);
		map.setScreenIndexX(map.getAvatarIndexX() - 9);
		BattlePanelManager manager = (BattlePanelManager) map.getManager();
		manager.changeDominantPanel(manager.getMenuPanel());
		MenuPanel menu = (MenuPanel)manager.getDominantPanel();
		panel.frame.add(menu);
//		frame.add(menu);
		while(menu.subMenu != null) {
			menu = menu.subMenu;
			panel.frame.add(menu);
//			frame.add(menu);
		}	
	}
}
