import java.awt.*;
import java.util.List;

public class Polygon {

    Integer type;
    List<Location> coords;
    int width, height;

    /**
     * @param type
     * @param coords
     * @param dimension
     */
    public Polygon(Integer type, List<Location> coords, Dimension dimension) {
        this.type = type;
        this.coords = coords;
        this.width = dimension.width / 2;
        this.height = dimension.height / 2;
    }

    public void draw(Graphics g, Location origin, double scale) {
        if (type <= 19 && type >= 1) {
            g.setColor(Color.decode("0xC7C9C8"));
        } else if (type <= 39 && type >= 20) {
            // Sets the color of the parks/forests/reserves
            g.setColor(Color.decode("#cbe6a3"));
        } else {
            // Sets the color of the water
            g.setColor(Color.decode("#a3ccff"));
        }

        int[] xPoints = new int[coords.size()];
        int[] yPoints = new int[coords.size()];
        int i = 0;

        for (Location location : coords) {
            xPoints[i] = location.asPoint(origin, scale).x + width;
            yPoints[i] = location.asPoint(origin, scale).y + height;
            i++;
        }
        g.fillPolygon(xPoints, yPoints, i);
    }
}