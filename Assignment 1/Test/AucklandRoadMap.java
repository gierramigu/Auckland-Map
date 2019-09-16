import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.util.*;
import java.io.FileReader;
import java.awt.Dimension;
import java.io.IOException;
import java.util.List;


public class AucklandRoadMap extends GUI{

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

        for (Node n : nodesMap.values()) {
            n.draw(g, origin, scale);
        }

        for(Segment s : segmentList){
            s.draw(g, origin, scale);
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
        //Zoom

        if (m == Move.ZOOM_IN) {
            scale = scale * 1.1;
        } else if (m == Move.ZOOM_OUT) {
            scale = scale / 1.1;
        }

        for (Node n : nodesMap.values()) {
            if (m == Move.NORTH) {
                n.move("north");
            } else if (m == Move.EAST) {
                n.move("east");
            } else if (m == Move.SOUTH) {
                n.move("south");
            } else if (m == Move.WEST) {
                n.move("west");
            }
        }

        for (Segment s : segmentList) {
            if (m == Move.NORTH) {
                s.move("north");
            } else if (m == Move.EAST) {
                s.move("east");
            } else if (m == Move.SOUTH) {
                s.move("south");
            } else if (m == Move.WEST) {
                s.move("west");
            }
        }

    }

    @Override
    protected void onLoad(File nodes, File roads, File segments, File polygons) { //File polygons
        BufferedReader nodesReader, roadsReader, segmentsReader, polyReader;

        //NODE

        try {
            // Sets initial Northern, Southern, Easter and Western most points
            // at the center of Auckland
            double NORTH_LAT = -35.52; //-36.84
            double SOUTH_LAT = -37.10;
            double WEST_LON = 173.40; //174.76
            double EAST_LON = 175.9;

            String currentLine;
            nodesReader = new BufferedReader(new FileReader(nodes));

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

            System.out.println(NORTH_LAT);
            System.out.println(SOUTH_LAT);
            System.out.println(WEST_LON);
            System.out.println(EAST_LON);

            topLeft = Location.newFromLatLon(NORTH_LAT, WEST_LON);
            bottomRight = Location.newFromLatLon(SOUTH_LAT, EAST_LON);
            // Sets the maps origin and scale
            System.out.println(origin);
            origin = Location.newFromLatLon(NORTH_LAT, WEST_LON);
            System.out.println(origin);
            scale = 200 / (topLeft.y - bottomRight.y);

            //double xScale = (getDrawingAreaDimension().width / (NORTH_LAT - SOUTH_LAT));
            //double yScale = (getDrawingAreaDimension().height /(WEST_LON - EAST_LON));
            //scale = Math.min(xScale,yScale);

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

    }
    public static void main(String[] args) { new AucklandRoadMap(); }
}
