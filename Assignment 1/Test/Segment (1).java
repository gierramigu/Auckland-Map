import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.*;

public class Segment {
    int roadid;
    Node nodeid1, nodeid2;
    Road road;
    double length;
    boolean highlight = false;

    Dimension dimension;
    int height, width;

    ArrayList<Location> coords;

    public Segment(int roadid, Node nodeid1, Node nodeid2, double length, ArrayList<Location> coords,
                   Dimension dimension) {
        this.roadid = roadid;
        this.nodeid1 = nodeid1;
        this.nodeid2 = nodeid2;
        this.length = length;
        this.coords = coords;
        this.height = dimension.height / 2;
        this.width = dimension.width / 2;
    }

    public void draw(Graphics g, Location origin, double scale) {
        if (highlight == true) {
            g.setColor(Color.ORANGE);
        } else {
            g.setColor(Color.decode("#4c4c4c"));
        }

        Location start, end;
        Point startPoint, endPoint;

        for (int i = 0; i < coords.size() - 1; i++) {
            start = coords.get(i);
            end = coords.get(i + 1);
            startPoint = start.asPoint(origin, scale);
            endPoint = end.asPoint(origin, scale);
            g.drawLine(startPoint.x + width, startPoint.y + height, endPoint.x + width, endPoint.y + height);
        }
    }

    public void move(String dir) {
        for (int i = 0; i < coords.size(); i++) {
            if (dir == "north") {
                coords.set(i, (coords.get(i).moveBy(0, -10)));
            } else if (dir == "east") {
                coords.set(i, (coords.get(i).moveBy(-10, 0)));
            } else if (dir == "south") {
                coords.set(i, (coords.get(i).moveBy(0, 10)));
            } else if (dir == "west") {
                coords.set(i, (coords.get(i).moveBy(10, 0)));
            }
        }
    }
}