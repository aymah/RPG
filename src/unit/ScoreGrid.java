package unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class ScoreGrid {

	//unit, y, x
	private ScoreKeeper[][][] scoreGrid;
	long maxScore;
	Queue<AIMove> bestMoves;
	Queue<AIMove> currMoves;
	List<Unit> unitList;
	int size;
	
	public ScoreGrid(Squad squad, int y, int x) {
		scoreGrid = new ScoreKeeper[squad.getSize()][y][x];
//		bestMoves = null;
		size = squad.getSize();
		bestMoves = new ArrayBlockingQueue<AIMove>(size);
		currMoves = new ArrayBlockingQueue<AIMove>(size);
//		maxScore = Integer.MIN_VALUE;
		maxScore = 0;
		unitList = new ArrayList<Unit>();
	}
	
	public ScoreKeeper getScoreKeeper(int unit, int y, int x) {
		return scoreGrid[unit][y][x];
	}
	
	public long getMaxScore() {
		if (this.maxScore != Integer.MIN_VALUE)
			return maxScore;
//		calcBestMoves();
		return maxScore;
	}
	
	public Queue<AIMove> getBestMoves() {
//		if (this.bestMoves != null) {
//			return this.bestMoves;
//		}
//		calcBestMoves();
//		System.out.println("size " + bestMoves.size());
		return bestMoves;
	}
	
	
	private void calcBestMoves() {
		Queue<AIMove> bestMoves = new ArrayBlockingQueue<AIMove>(scoreGrid.length);
		long maxScore = 0;
		for (int i = 0; i < unitList.size(); i++) {
			ScoreKeeper[][] unitScore = scoreGrid[i];
			long maxUnitScore = Long.MIN_VALUE;
			int unitY = -1;
			int unitX = -1;
			ScoreKeeper scoreKeeper = null;
			boolean hasBestMoveBeenFound = false;
			for (int y = 0; y < unitScore.length; y++) {
				for (int x = 0; x < unitScore[0].length; x++) {
					if (unitScore[y][x] != null && maxUnitScore < unitScore[y][x].getScore()) {
						maxUnitScore = unitScore[y][x].getScore();
						scoreKeeper = unitScore[y][x];
						unitY = y;
						unitX = x;
						hasBestMoveBeenFound = true;
					}
				}
			}
			if (hasBestMoveBeenFound == false) {
				System.err.println("Houston, we have a problem. " + unitList.get(i).getName());
			}
			AIMove bestMove = new AIMove(scoreKeeper, unitY, unitX);
			bestMoves.add(bestMove);
			maxScore += maxUnitScore;
		}
		this.maxScore = maxScore;
		this.bestMoves = bestMoves;
	}
	
	public int getCurrSize() {
		return unitList.size();
	}
	
	public void addUnitGrid(ScoreKeeper[][] unitGrid, Unit unit) {
		unitList.add(unit);
//		scoreGrid[unitList.size() - 1] = unitGrid;
		
		ScoreKeeper[][] unitScore = unitGrid;
		long maxUnitScore = Long.MIN_VALUE;
		int unitY = -1;
		int unitX = -1;
		ScoreKeeper scoreKeeper = null;
		boolean hasBestMoveBeenFound = false;
		for (int y = 0; y < unitScore.length; y++) {
			for (int x = 0; x < unitScore[0].length; x++) {
				if (unitScore[y][x] != null && maxUnitScore < unitScore[y][x].getScore()) {
					maxUnitScore = unitScore[y][x].getScore();
					scoreKeeper = unitScore[y][x];
					unitY = y;
					unitX = x;
					hasBestMoveBeenFound = true;
				}
			}
		}
		if (!hasBestMoveBeenFound) System.out.println("apparently this garbage still can't find moves");
		bestMoves.add(new AIMove(scoreKeeper, unitY, unitX));
		maxScore += maxUnitScore;
	}

	public void assimilate(ScoreGrid oldScoreGrid) {
//		for (int i = 0; i < oldScoreGrid.getCurrSize(); i++) {
//			scoreGrid[i] = oldScoreGrid.getUnitGrid(i);
//		}
		unitList.addAll(oldScoreGrid.getUnitList());
		bestMoves = new ArrayBlockingQueue<AIMove>(size);
		bestMoves.addAll(oldScoreGrid.getBestMoves());
		maxScore = oldScoreGrid.getMaxScore();
	}
	
	private ScoreKeeper[][] getUnitGrid(int i) {
		return scoreGrid[i];
	}
	
	public Queue<AIMove> getCurrMoves() {
		return currMoves;
	}
	
	public void addMove(AIMove move) {
		currMoves.add(move);
	}
//	public int getNumUnits() {
//		for (int i = 0; i < scoreGrid.length; i++) {
//			if (scoreGrid[i] == null)
//				return i;
//		}
//		return scoreGrid.length;
//	}

	public List<Unit> getUnusedUnits(Squad squad) {
		List<Unit> unusedUnits = new ArrayList(squad.getUnitList());
		for (Unit unit: unitList) {
			if (unusedUnits.contains(unit))
				unusedUnits.remove(unit);
		}
		return unusedUnits;
	}
	
	public List<Unit> getUnitList() {
		return unitList;
	}
	
	public void addUnit(Unit unit) {
		unitList.add(unit);
	}
}
