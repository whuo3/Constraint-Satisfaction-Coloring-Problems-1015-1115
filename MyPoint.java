import java.util.HashSet;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class MyPoint {
	private int x;
	private int y;
	//0: no color
	//1: color yellow
	//2: color green
	//3: color blue
	//4: color red
	private int color;
	private int remainValues;
	boolean[] colorAvailable = new boolean[5];
	List<MyPoint> neighbors;
	private PriorityQueue<MyPoint> neighborCandidates;
	public MyPoint(int x, int y) {
		this.x = x;
		this.y = y;
		this.color = 0;
		this.remainValues = 4;
		final int tx = x;
		final int ty = y;
		neighbors = new ArrayList<MyPoint>();
		neighborCandidates = new PriorityQueue<MyPoint>(Game.GridSize, new Comparator<MyPoint>(){
			@Override
			public int compare(MyPoint a, MyPoint b) {
				int aVal = (a.getX() - tx) * (a.getX() - tx) + (a.getY() - ty) * (a.getY() - ty);
				int bVal = (b.getX() - tx) * (b.getX() - tx) + (b.getY() - ty) * (b.getY() - ty);
				if(aVal == bVal) {
					return 0;
				}
				return aVal < bVal? -1 : 1;
			}
		});
		Arrays.fill(colorAvailable, true);
	}
	public void init() {
		this.color = 0;
		this.remainValues = 4;
		Arrays.fill(colorAvailable, true);
		for(int i = 0; i < Game.col.size(); i++) {
			if(Game.col.get(i) != this) {
				neighborCandidates.offer(Game.col.get(i));
			}
		}
	}
	public boolean getConnection() { 
		while(!neighborCandidates.isEmpty()) {
			MyPoint candidate = neighborCandidates.poll();
			if(!isIntersected(this, candidate)) {
				this.addNeighbor(candidate);
				candidate.addNeighbor(this);
				return true;
			}
			candidate.removeCandidate(this); //If there is an intersection, this is no more to be a candidate of candidate
		}
		return false;
	}
	//Intersection algo, please refer to http://paulbourke.net/geometry/pointlineplane/
	private boolean isIntersected(MyPoint one, MyPoint two) {
		for(MyPoint p : Game.col) {
			for(MyPoint pNei : p.neighbors) {
				if(one == p || one == pNei || two == p || two == pNei) {
					continue;
				}
				int denominater = (pNei.getY() - p.getY()) * (two.getX() - one.getX()) - (pNei.getX() - p.getX()) * (two.getY() - one.getY());
				if(denominater == 0) {
					continue;
				}
				double uA = (pNei.getX() - p.getX()) * (one.getY() - p.getY()) - (pNei.getY() - p.getY()) * (one.getX() - p.getX());
				double uB = (two.getX() - one.getX()) * (one.getY() - p.getY()) - (two.getY() - one.getY()) * (one.getX() - p.getX());
				uA /= denominater;
				uB /= denominater;
				if(0.0 <= uA && uA <= 1.0 && 0.0 <= uB && uB <= 1.0) {
					return true;
				}
			}
		}
		return false;
	}
	public void setX(int x) {
		this.x = x;
	}
	public void setY(int y) {
		this.y = y;
	}
	public void setColor(int color) {
		this.color = color;
	}
	public void setRemainValues(int val) {
		this.remainValues = val;
	}
	public int getColor() {
		return color;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getRemainValues() {
		return remainValues;
	}
	public void addNeighbor(MyPoint nei) {
		this.neighborCandidates.remove(nei);
		this.neighbors.add(nei);
	}
	public void removeCandidate(MyPoint can) {
		neighborCandidates.remove(can);
	}
	public boolean isColored() {
		return color <= 4 && color >= 1;
	}
}