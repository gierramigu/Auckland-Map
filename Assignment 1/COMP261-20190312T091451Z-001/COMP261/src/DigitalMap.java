import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.util.*;
import java.io.FileReader;
import java.awt.Dimension;
import java.io.IOException;
import java.util.List;


public class DigitalMap extends GUI{

    static Map<Integer, Node> nodesMap = new HashMap<>(); //Map of nodes
    static Map<Integer, Road> roadsMap = new HashMap<>(); //Map of roads
    static Map<Integer, Segment> segmentMap = new HashMap<>(); //Maps of segments
    static ArrayList<Segment> segmentList = new ArrayList<>(); //List of segments
    static Set<Polygon> polygonSet = new HashSet<>(); //Set of polygons


    private Location origin, topLeft, bottomRight;
    private double scale;


    Dimension dimension = getDrawingAreaDimension();
    int width = dimension.width /2;
    int height = dimension.height / 2;

    //Trie trie = new Trie();


    @Override
    protected void redraw(Graphics g) {


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

    @Override
    protected void onLoad(File nodes, File roads, File segments, File polygons) {
        BufferedReader nodesReader, roadsReader, segmentsReader, polyReader;

        //NODE

        try {
            // Sets initial Northern, Southern, Easter and Western most points
            // at the center of Auckland
            double NORTH_LAT = -36.84;
            double SOUTH_LAT = -36.84;
            double WEST_LON = 174.76;
            double EAST_LON = 174.76;

            String currentLine;
            nodesReader = new BufferedReader(new FileReader(nodes));
            //nodesReader = new BufferedReader(new FileReader("res//data//small//nodeID-lat-lon.tab"));

            while ((currentLine = nodesReader.readLine()) != null){
                String[] values = currentLine.split("\t");

                int nodeID = Integer.parseInt(values[0]);
                double lat = Double.parseDouble(values[1]);
                double lon = Double.parseDouble(values[2]);
                Location location = Location.newFromLatLon(lat, lon); //UNSURE

                nodesMap.put(nodeID, new Node(nodeID, lat, lon, location, this.dimension));

                // Checks if the current latitude or longitude are further from
                // the center compared to the previously set latitude and
                // longitude
                if (lat > NORTH_LAT) {
                    NORTH_LAT = lat;
                }
                if (lat < SOUTH_LAT) {
                    SOUTH_LAT = lat;
                }
                if (lon < WEST_LON) {
                    WEST_LON = lon;
                }
                if (lon > EAST_LON) {
                    EAST_LON = lon;
                }
            }

            topLeft = Location.newFromLatLon(NORTH_LAT, WEST_LON);
            bottomRight = Location.newFromLatLon(SOUTH_LAT, EAST_LON);
            // Sets the maps origin and scale
            origin = Location.newFromLatLon(NORTH_LAT, WEST_LON);
            scale = 200 / (topLeft.y - bottomRight.y);

        } catch (IOException e) {
            e.printStackTrace();
        }

        //ROAD
        try {
            String currentLine;
            roadsReader = new BufferedReader(new FileReader(roads));
            //roadsReader = new BufferedReader(new FileReader("res//data//small//roadID-roadInfo.tab"));
            roadsReader.readLine(); // Skips over the first line

            while ((currentLine = roadsReader.readLine()) != null) {
            //while ((roadsReader.readLine()) != null) {
                String[] values = currentLine.split("\t");
                int roadID = Integer.parseInt(values[0]);
                String label = values[2];
                String city = values[3];
                int oneway = Integer.parseInt(values[4]);
                int speed = Integer.parseInt(values[5]);
                int roadclass = Integer.parseInt(values[6]);
                int notforcar = Integer.parseInt(values[7]);
                int notforpede = Integer.parseInt(values[8]);
                int notforbicy = Integer.parseInt(values[9]);

                roadsMap.put(roadID, new Road(roadID, oneway, speed, roadclass, notforcar, notforpede, notforbicy, label, city));
                //trie.add(label); // Used for prefix searching
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //SEGMENT
        try {
            String currentLine;
            segmentsReader = new BufferedReader(new FileReader(segments));
            //segmentsReader = new BufferedReader(new FileReader("res//data//small//roadSeg-roadID-length-nodeID-nodeID-coords.tab"));
            segmentsReader.readLine(); // Skips over the first line

            while ((currentLine = segmentsReader.readLine()) != null) {
                ArrayList<Location> coords = new ArrayList<Location>();

                String[] values = currentLine.split("\t");
                Road roadobj = roadsMap.get(Integer.parseInt(values[0]));
                int roadid = Integer.parseInt(values[0]);
                double length = Double.parseDouble(values[1]);
                Node nodeid1 = nodesMap.get(Integer.parseInt(values[2]));
                Node nodeid2 = nodesMap.get(Integer.parseInt(values[3]));

                // Adds the remaining values in the current line to an array
                // ignoring the the first 3 values of the line
                for (int j = 4; j < values.length; j = j + 2) {
                    Location location = Location.newFromLatLon(Double.parseDouble(values[j]),
                            Double.parseDouble(values[j + 1]));
                    coords.add(location);
                }
                Segment newSeg = new Segment(roadid, nodeid1, nodeid2, length, coords, this.dimension);
                segmentMap.put(roadid, newSeg);
                segmentList.add(newSeg);
                // unsure if these are the right way around
                nodeid1.addToStart(newSeg);
                nodeid2.addToEnd(newSeg);
                roadobj.segs.add(newSeg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* --Polygons-- */
        try {
            polyReader = new BufferedReader(new FileReader(polygons));
            String line;
            Integer type = null;
            String allCoords;
            List<String> allCoordsSplit;
            List<Location> coords;
            String values[];

            while ((line = polyReader.readLine()) != null) {

                // Type
                if (line.contains("Type")) {
                    values = line.split("=");
                    type = Integer.decode(values[1]);
                }

                // Data
                if (line.contains("Data")) {
                    allCoords = line.split("=")[1];
                    // Takes the first and last bracket out
                    allCoords = allCoords.substring(1, allCoords.lastIndexOf(')'));
                    // Splits the list
                    allCoordsSplit = Arrays.asList(allCoords.split("\\),\\("));
                    coords = new ArrayList<Location>();

                    for (String coord : allCoordsSplit) {
                        double lat = Double.parseDouble(coord.split(",")[0]);
                        double lon = Double.parseDouble(coord.split(",")[1]);
                        Location location = Location.newFromLatLon(lat, lon);
                        coords.add(location);
                    }
                    polygonSet.add(new Polygon(type, coords, dimension));
                }
            }

        } catch (IOException e) {
            System.out.println("IOException" + e);
        }
    }
    public static void main(String[] args) { new DigitalMap(); }
}
