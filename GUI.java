import java.awt.Canvas;
import java.awt.Frame;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

class GridsCanvas extends Canvas {
	int width, height;
	List<MyPoint> points;

	public GridsCanvas(int w, int h, List<MyPoint> points) {
		setSize(width = w, height = h);
		this.points = points;
	}
	public void paint(Graphics g) {
		width = getSize().width;
		height = getSize().height;
		for(int i = 0; i < points.size(); i++) {
			if(points.get(i).getColor() == 1) {
				g.setColor(Color.YELLOW);
			} else if(points.get(i).getColor() == 2) {
				g.setColor(Color.GREEN);
			} else if(points.get(i).getColor() == 3) {
				g.setColor(Color.BLUE);
			} else if(points.get(i).getColor() == 4) {
				g.setColor(Color.RED);
			}
			g.fillOval(points.get(i).getX() - Game.CircleSize / 2, points.get(i).getY() - Game.CircleSize / 2, Game.CircleSize, Game.CircleSize);
			g.setColor(Color.BLACK);
			for(MyPoint nei : points.get(i).neighbors) {
				g.drawLine(points.get(i).getX(), points.get(i).getY(), nei.getX(), nei.getY());
			}
		}
	}
}

public class GUI extends Frame {
	public GUI(List<MyPoint> points) {
		setTitle("GUI");
		setVisible(true);
    	// Now create a Canvas and add it to the Frame.
    	GridsCanvas xyz = new GridsCanvas(Game.GridSize, Game.GridSize, points);
    	this.add(xyz);
	    this.addWindowListener(new WindowAdapter() {
	      	public void windowClosing(WindowEvent e) {
	        	setVisible(false);
	        	dispose();
	        	System.exit(0);
			}
	    });
    	// Normal end ... pack it up!
    	pack();
	}
}