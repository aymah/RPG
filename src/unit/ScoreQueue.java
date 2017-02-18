package unit;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class ScoreQueue {
	PriorityQueue<ScoreGrid> pQueue;
	
	public ScoreQueue() {
		Comparator comparator = getComparator();
		pQueue = new PriorityQueue<ScoreGrid>(10, comparator); //probably need to change this size
	}
	
	public Object[] toArray() {
		return pQueue.toArray();
	}

	public static Comparator getComparator() {
		Comparator<ScoreGrid> comparator = new Comparator<ScoreGrid>() {
			@Override
			public int compare(ScoreGrid o1, ScoreGrid o2) {
				if (o1.getCurrSize() - o2.getCurrSize() >= 0) return 1;
				if (o1.getCurrSize() - o2.getCurrSize() < 0) return -1;
				return 1;
			}
		};
		return comparator;
	}
	
	public ScoreGrid deQueue() {
		return pQueue.remove();
	}
	
	public void enQueue(ScoreGrid scoreGrid) {
		pQueue.add(scoreGrid);
	}
	
	public ScoreGrid peek() {
		return pQueue.peek();
	}
	
	public void remove(ScoreGrid scoreGrid) {
		pQueue.remove(scoreGrid);
	}
	
	public Queue<AIMove> getBestMoves() {
		ScoreGrid bestMoves = null;
		for (ScoreGrid scoreGrid: pQueue) {
			if (bestMoves == null || bestMoves.getMaxScore() < scoreGrid.getMaxScore()) {
				bestMoves = scoreGrid;
			}
		}
		return bestMoves.getBestMoves();
	}
	
	public int size() {
		return pQueue.size();
	}
}
