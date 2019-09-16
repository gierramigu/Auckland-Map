import java.util.ArrayList;

public class Road {
    int ID;
    int oneway;
    int speed;
    int roadclass;
    int notforcar;
    int notforpede;
    int notforbicy;
    String label;
    String city;

    //ArrayList<Segment> segs = new ArrayList<Segment>();

    public Road(int roadID, int oneway, int speed, int roadclass, int notforcar, int notforpede, int notforbicy, String label, String city) {
        this.ID = roadID;
        this.oneway = oneway;
        this.speed = speed;
        this.roadclass = roadclass;
        this.notforcar = notforcar;
        this.notforpede = notforpede;
        this.notforbicy = notforbicy;
        this.label = label;
        this.city = city;
    }
}