import java.awt.*;
import java.util.ArrayList;

public class Node {
    int id;
    int size = 2;

    double lat, lon;
    Location location;
    Point point;

    boolean highlight = false;

    ArrayList<Segment> inSegs = new ArrayList<Segment>();
    ArrayList<Segment> outSegs = new ArrayList<Segment>();


    Dimension dimension;
    int height, width;


    public Node(int id, double lat, double lon, Location location, Dimension dimension){
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.location  = location;
        this.height = dimension.height / 2;
        this.width = dimension.width / 2;
    }

    public void draw(Graphics g, Location origin, double scale) {
        point = new Point(location.asPoint(origin, scale));

        // If highlight is true, it will draw an orange box on top.
        if (highlight) {
            g.setColor(Color.ORANGE);
            g.fillRect((int) point.x - 4 + width, (int) point.y - 4 + height, 8, 8);
        } else {
            g.setColor(Color.decode("#4c4c4c"));
            g.fillRect((int) point.x + width, (int) point.y + height, size, size);
        }
    }


    /**
     * Adds a segment to the in segments **/
    public void addToStart(Segment seg) {
        inSegs.add(seg);
    }

    /**
     * Adds a segment to the out segments
        */
    public void addToEnd(Segment seg) {
        outSegs.add(seg);

    }
}
