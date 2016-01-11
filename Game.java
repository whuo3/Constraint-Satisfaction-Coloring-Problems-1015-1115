import java.util.Random;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Game{
	public static Random rand = new Random();
	public static int GridSize = 500;
	public static int CircleSize = 20;
	public static List<MyPoint> col;

	public static void main(String[] args) {
		int Times = 1;
		int[] backTrackingAssignments = new int[1];
		int[] forwardTrackingAssignments = new int[1];
		int[] forwardTrackingOrderAssignments = new int[1];
		double bTTime = 0.0;
		double fTTime = 0.0;
		double foTTime = 0.0;
		int numberOfEdges = 0;
		int localSearchSuccess = 0;
		int localSearchFail = 0;
		for(int j = 0; j < Times; j++) {
			col = getMyPoints(5);
			buildGraph();
			for(MyPoint p: col) {
				numberOfEdges += p.neighbors.size();
			}
			/*long start_time = System.nanoTime();
			backTracking(backTrackingAssignments);
			long end_time = System.nanoTime();
			bTTime += (end_time - start_time)/1e9;
			for(int i = 0; i < col.size(); i++) {
				col.get(i).init();
			}
			start_time = System.nanoTime();
			forwardTracking(forwardTrackingAssignments);
			end_time = System.nanoTime();
			fTTime += (end_time - start_time)/1e9;
			for(int i = 0; i < col.size(); i++) {
				col.get(i).init();
			}
			start_time = System.nanoTime();*/
			forwardTrackingWithOrdering(forwardTrackingOrderAssignments);
			//end_time = System.nanoTime();
			//foTTime += (end_time - start_time)/1e9;
			/*if(localSearch()) {
				localSearchSuccess++;
				System.out.println("Success");
			} else {
				localSearchFail++;
				System.out.println("Fail");
			}*/
			GUI ui = new GUI(col);
		}
		System.out.println("Local Search success: " + localSearchSuccess);
		System.out.println("Local Search fail: " + localSearchFail);
		System.out.println(((numberOfEdges/2)/Times));
		System.out.println((backTrackingAssignments[0] / Times));
		System.out.println((forwardTrackingAssignments[0] / Times));
		System.out.println((forwardTrackingOrderAssignments[0] / Times));
		System.out.println();
		System.out.println((bTTime/Times));
		System.out.println((fTTime/Times));
		System.out.println((foTTime/Times));
	}
	/***********Basic backward checking******************/
	public static boolean backTracking(int[] assignments) {
		return recursiveBackTracking(0, assignments);
	}
	public static boolean recursiveBackTracking(int idx, int[] assignments) {
		if(idx == col.size()) {
			return true;
		}
		for(int i = 1; i <= 4; i++) {
			boolean canTry = true;
			for(MyPoint nei : col.get(idx).neighbors) {
				if(nei.getColor() != 0 && nei.getColor() == i) {
					canTry = false;
					break;
				}
			}
			if(canTry) {
				col.get(idx).setColor(i);
				assignments[0]++;
				if(recursiveBackTracking(idx + 1, assignments)) {
					return true;
				}
			}
		}
		col.get(idx).setColor(0);
		return false;
	}
	/****************Forward checking**********************/
	public static boolean forwardTracking(int[] assignments) {
		return recursiveForwardTracking(0, assignments);
	}
	public static boolean isEmptyDomain(boolean[] domains) {
		return !domains[1] && !domains[2] && !domains[3] && !domains[4];
	}
	public static void recoverDomain(List<MyPoint> helper, int i) {
		for(MyPoint nei : helper) {
			nei.colorAvailable[i] = true;
			nei.setRemainValues(nei.getRemainValues() + 1);
		}
	}
	public static boolean recursiveForwardTracking(int idx, int[] assignments) {
		if(idx == col.size()) {
			return true;
		}
		MyPoint cur = col.get(idx);
		List<MyPoint> neighbors = cur.neighbors;
		for(int i = 1; i <= 4; i++) {
			if(cur.colorAvailable[i]) {
				//helper record how many neibors' feasible domain will be made change.
				List<MyPoint> helper = new ArrayList<MyPoint>();
				boolean isEmpty = false; 
				for(MyPoint nei : neighbors) {
					if(nei.getColor() != 0) {
						continue;
					}
					if(nei.colorAvailable[i]) {
						helper.add(nei);
						nei.colorAvailable[i] = false;
						nei.setRemainValues(nei.getRemainValues() - 1);
					}
					isEmpty = isEmpty || isEmptyDomain(nei.colorAvailable);
				}
				if(!isEmpty) {
					cur.setColor(i);
					assignments[0]++;
					if(recursiveForwardTracking(idx + 1, assignments)) {
						return true;
					}
				}
				//Recover the changed domains
				recoverDomain(helper, i);
			}
		}
		col.get(idx).setColor(0);
		return false;
	}
	/*****************Forward checking with minimum remaining value(vairable order) + least constraning value*********************/
	public static boolean forwardTrackingWithOrdering(int[] assignments) {
		PriorityQueue<MyPoint> pq = new PriorityQueue<MyPoint>(col.size(), new Comparator<MyPoint>(){
			@Override
			public int compare(MyPoint one, MyPoint two) {
				if(one.getRemainValues() == two.getRemainValues()) {
					return 0;
				}
				return one.getRemainValues() < two.getRemainValues()? -1 : 1;
			}
		});
		for(int i = 0; i < col.size(); i++) {
			pq.offer(col.get(i));
		}
		return recursiveForwardTrackingWithOrdering(pq, assignments);
	}
	public static boolean recursiveForwardTrackingWithOrdering(PriorityQueue<MyPoint> pq, int[] assignments) {
		if(pq.isEmpty()) {
			return true;
		}
		MyPoint cur = pq.poll();
		List<MyPoint> neighbors = cur.neighbors;
		Integer[] colorTab = {1, 2, 3, 4};
		final int[] leastContrainingVals = new int[5]; 
		//Count the constraining values for each color
		for(int i = 1; i <= 4; i++) {
			if(cur.colorAvailable[i]) {
				for(MyPoint nei : neighbors) {
					if(nei.getColor() != 0) {
						continue;
					}
					if(nei.colorAvailable[i]) {
						leastContrainingVals[i] += 1;
					}
				}
			}
		}
		Arrays.sort(colorTab, new Comparator<Integer>(){
			@Override
			public int compare(Integer i, Integer j) {
				if(leastContrainingVals[i] == leastContrainingVals[j]) {
					return 0;
				}
				return leastContrainingVals[i] < leastContrainingVals[j]? -1 : 1;
			}
		});
		for(int i = 0; i < 4; i++) {
			if(cur.colorAvailable[colorTab[i]]) {
				List<MyPoint> helper = new ArrayList<MyPoint>();
				boolean isEmpty = false; 
				for(MyPoint nei : neighbors) {
					if(nei.getColor() != 0) {
						continue;
					}
					if(nei.colorAvailable[colorTab[i]]) {
						helper.add(nei);
						nei.colorAvailable[colorTab[i]] = false;
						nei.setRemainValues(nei.getRemainValues() - 1);
					}
					isEmpty = isEmpty || isEmptyDomain(nei.colorAvailable);
				}
				if(!isEmpty) {
					cur.setColor(colorTab[i]);
					assignments[0]++;
					if(recursiveForwardTrackingWithOrdering(pq, assignments)) {
						return true;
					}
				}
				//Recover the changed domains
				recoverDomain(helper, colorTab[i]);
			}
		}
		pq.offer(cur);
		cur.setColor(0);
		return false;
	}

	/*****************Local Search************************/
	private static boolean checkSolution() {
		for(MyPoint p: col) {
			for(MyPoint pNei : p.neighbors) {
				if(p.getColor() == pNei.getColor()) {
					return false;
				}
			}
		}
		return true;
	}
	public static boolean localSearch() {
		for(int i = 0; i < col.size(); i++) {
			col.get(i).setColor(rand.nextInt(4) + 1);
		}
		for(long i = 0; i < 10000000; i++) {
			if(checkSolution()) {
				return true;
			} else {
				int varIdx = rand.nextInt(col.size());
				MyPoint cur = col.get(varIdx);
				List<MyPoint> neighbors = cur.neighbors;
				//Find the minimum conflicts value
				Integer[] colorTab = {1, 2, 3, 4};
				final int[] leastContrainingVals = new int[5]; 
				//Count the constraining values for each color
				for(int j = 1; j <= 4; j++) {
					if(cur.colorAvailable[j]) {
						for(MyPoint nei : neighbors) {
							if(nei.getColor() != 0) {
								continue;
							}
							if(nei.colorAvailable[j]) {
								leastContrainingVals[j] += 1;
							}
						}
					}
				}
				Arrays.sort(colorTab, new Comparator<Integer>(){
					@Override
					public int compare(Integer i, Integer j) {
						if(leastContrainingVals[i] == leastContrainingVals[j]) {
							return 0;
						}
						return leastContrainingVals[i] < leastContrainingVals[j]? -1 : 1;
					}
				});
				col.get(varIdx).setColor(colorTab[0]);
			}
		}
		return false;
	}


	/*****************Others helper functions*******************/
	public static void buildGraph() {
		MyPoint[] helper = new MyPoint[col.size()]; //Helper maintain the liveness of the nodes
		int lastIdx = helper.length - 1;
		for(int i = 0; i < col.size(); i++) {
			col.get(i).init();
			helper[i] = col.get(i);
		}
		while(lastIdx > 0) {
			int i = rand.nextInt(lastIdx + 1);
			if(!helper[i].getConnection()) {
				swap(i, lastIdx, helper);
				lastIdx--;
			}
		}
	}
	public static List<MyPoint> getMyPoints(int size) {
		if(size < 0) {
			return null;
		}
		List<MyPoint> result = new ArrayList<MyPoint>();
		HashSet<Integer> duplicatedFounder = new HashSet<Integer>();
		for(int i = 0; i < size; i++) {
			int x = rand.nextInt(Game.GridSize - Game.CircleSize / 2);
			int y = rand.nextInt(Game.GridSize - Game.CircleSize / 2);
			//Avoid dulplicate
			while(duplicatedFounder.contains(cantorPairing(x, y))) {
				x = rand.nextInt(Game.GridSize - Game.CircleSize / 2);
				y = rand.nextInt(Game.GridSize - Game.CircleSize / 2);
			}
			result.add(new MyPoint(x, y));
		}
		return result;
	}
	//cantorPairing generate a unique number from two integers.
	public static int cantorPairing(int x, int y) {
		return ((x + y) * (x + y + 1) + y) / 2;
	}
	private static void swap(int i, int j, MyPoint[] array) {
			MyPoint t = array[i];
			array[i] = array[j];
			array[j] = t;
	}
}