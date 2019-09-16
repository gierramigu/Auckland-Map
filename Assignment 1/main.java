import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Set;




public class AucklandMap extends GUI{
    static Map <Integer, Node> NodeData = new HashMap<>();
    static Map <Integer, Road> RoadData = new HashMap<>();
    static Set <Segment> SegmentData = new HashSet<>();
    private double top, right = Double.MIN_VALUE;
    private double bottom, left = Double.MAX_VALUE;
    private double scale, xScale, yScale;
    private double dx = 1.3; //distance
    private double dy =  1.3; //distance
    private Location origin;
    Dimension dimension = getDrawingAreaDimension();
    List<Segment> allSegments = new ArrayList<>();

    @Override
    protected void redraw(Graphics g) {
        //iterating through the NodeMap
        for(Node node: NodeData.values()){
            node.drawNode(g, origin, scale);
        }
    }

    @Override
    protected void onClick(MouseEvent e) {


    }


    @Override
    protected void onSearch() {
  
    }

    @Override
    protected void onMove(Move m) {

       
    }

    //reads the data files
    @Override
    protected void onLoad(File nodes, File roads, File segments, File polygons) {
        /**Reading Node Data***/
        //Nodes
            BufferedReader nodesReader, roadReader, segmentReader;
            BufferedReader br = null;
            FileReader fr = null;

            try {
                String currentLine;
                nodesReader = new BufferedReader(new FileReader(nodes));
                while ((currentLine = nodesReader.readLine()) != null) {
                    //String Line = nodesReader.readLine(); //test
                    String[] splitting = currentLine.split("\t");
                    int nodeID = Integer.parseInt(splitting[0]); //conversion from string to an integer
                    double latitude = Double.parseDouble(splitting[1]);
                    double longitude  = Double.parseDouble(splitting[2]);

                    //Location locNode = new Node.getLocation()
                    Node node = new Node(nodeID, latitude, longitude); //creating new node and storing the data into the list
                    NodeData.put(nodeID,node);
                    System.out.println(nodeID); //test data println
                    Location location = Location.newFromLatLon(latitude, longitude);

                    //sorting and comparing the lat and long coordinates
                    //finding the most top and bottom coordinates to determine size of map
                    //if loc x is less than the current left most node's x
                    if (left > location.x) {left  = location.x;}

                    //if loc x is greater than the current right most node's y
                    if (right < location.x) {right = location.x;}

                    // if loc y is greater than the current top most node's y
                    if (top < location.y) {top = location.y;}

                    //if loc y is less than the current bottom most node's y
                    if (bottom > location.y) {bottom = location.y;}

                    //calculating scale & origin
                    xScale = (getDrawingAreaDimension().width /(right - left));
                    yScale = (getDrawingAreaDimension().height /(top - bottom));
                    scale = Math.min(xScale, yScale);
                    System.out.println(origin); //debugging
                    origin = new Location(left, top); //finding the origin point
                }
            } catch (IOException e) {
                System.out.println("Error");
                e.printStackTrace();
            }

        /**Reading Road Data***/
            try{
                roadReader = new BufferedReader(new FileReader(roads));
                String currentLine;
                roadReader.readLine();
                while ((currentLine = roadReader.readLine()) != null) {
                    //String Line = roadReader.readLine();
                    String splitting [] = currentLine.split("\t");
                    int roadID = Integer.parseInt( splitting[0]);
                    int type = Integer.parseInt(splitting[1]);
                    String label = splitting[2];
                    String city = splitting[3];
                    int oneWay = Integer.parseInt(splitting[4]);
                    int speed = Integer.parseInt(splitting[5]);
                    int roadClass = Integer.parseInt(splitting[6]);
                    int notForCar = Integer.parseInt(splitting[7]);
                    int notForPede = Integer.parseInt(splitting[8]);
                    int notForBicy = Integer.parseInt(splitting[9]);
                    Road road = new Road (roadID, type, label, city, oneWay, speed, roadClass, notForCar, notForPede, notForBicy);
                    RoadData.put(roadID,road);
                    System.out.println(roadID); //test data print
                    System.out.println("Test");

                }
            } catch (IOException e) {
                System.out.println("Error");
                e.printStackTrace();
            }

        /**Reading Segment Data***/
        //By using File/Buffered Reader, reads through the segment data file
            try {
                String currentLine = null;
                segmentReader = new BufferedReader(new FileReader(segments));
                segmentReader.readLine(); //reads the labels first
                while((currentLine = segmentReader.readLine())!=null){
                    //StringTokenizer token = new StringTokenizer(segmentReader.readLine());
                    String splitting[] = currentLine.split("\t");
                    int roadID = Integer.parseInt(splitting[0]);
                    double length = Double.parseDouble(splitting[1]);
                    int nodeID1 = Integer.parseInt(splitting[2]);
                    int nodeID2 = Integer.parseInt(splitting[3]);

                    //every location coordinate through this iteration is stored into this List
                    List <Location> loc = new ArrayList<>();

                    int i = 4;
                    //iterates through the coordinates and stores them all into a List starting at the 4th index
                    while (i < splitting.length) {
                        Double latitude = Double.parseDouble(splitting[i]);
                        Double longitude = Double.parseDouble(splitting[i + 1]);
                        Location coords = Location.newFromLatLon(latitude, longitude);
                        loc.add(coords);
                        i += 2;
                    }
                    System.out.println("Segment TEST"); //test
                    Segment segment = new Segment (roadID, length, nodeID1, nodeID2, loc);
                    RoadData.get(roadID).addSegment(segment); //places the new segment data into the roadID key into the RoadData Map
                    //allSegments.add(segment);
                }
            } catch(IOException e){
                System.out.println("Error");
                e.printStackTrace();
            }
        }

    //main method
    public static void main(String [] args){
        new AucklandMap();
    }

