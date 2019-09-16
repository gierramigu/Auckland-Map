import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;


/**
 * This represents the data structure storing all the roads, nodes, and
 * segments, as well as some information on which nodes and segments should be
 * highlighted.
 * 
 * @author tony
 */
public class Graph {
	// map node IDs to Nodes.
	Map<Integer, Node> nodes = new HashMap<>();
	// map road IDs to Roads.
	Map<Integer, Road> roads;
	// just some collection of Segments.
	Collection<Segment> segments;

	Node highlightedNode;
	Collection<Road> highlightedRoads = new HashSet<>();
	List<Node> startToEndNode = new ArrayList<>(); //this is where you would store the start and end node
	List<Segment> highlightedSegments = new ArrayList<>(); // for storing the highlighted segments
	List<Node> highlightAP = new ArrayList<>(); //list for articulation point

	public Graph(File nodes, File roads, File segments, File polygons) {
		this.nodes = Parser.parseNodes(nodes, this);
		this.roads = Parser.parseRoads(roads, this);
		this.segments = Parser.parseSegments(segments, this);
	}

	public void draw(Graphics g, Dimension screen, Location origin, double scale) {
		// a compatibility wart on swing is that it has to give out Graphics
		// objects, but Graphics2D objects are nicer to work with. Luckily
		// they're a subclass, and swing always gives them out anyway, so we can
		// just do this.
		Graphics2D g2 = (Graphics2D) g;

		// draw all the segments.
		g2.setColor(Mapper.SEGMENT_COLOUR);
		for (Segment s : segments)
			s.draw(g2, origin, scale);

		// draw the segments of all highlighted roads.
		g2.setColor(Mapper.HIGHLIGHT_COLOUR);
		g2.setStroke(new BasicStroke(3));
		for (Road road : highlightedRoads) {
			for (Segment seg : road.components) {
				seg.draw(g2, origin, scale);
			}
		}

		// draw all the nodes.
		g2.setColor(Mapper.NODE_COLOUR);
		for (Node n : nodes.values()) {
			//System.out.println("trigger2");
			if(startToEndNode.contains(n)) {
				//System.out.println("trigger1");
				System.out.println(startToEndNode.size());
				g2.setColor(Color.RED);
				n.draw(g2, screen, origin, scale);
				g2.setColor(Mapper.NODE_COLOUR);
			} else{
				n.draw(g2, screen, origin, scale);
			}
		}
		// draw the highlighted node, if it exists.
		if (highlightedNode != null) {
			g2.setColor(Mapper.HIGHLIGHT_COLOUR);
			highlightedNode.draw(g2, screen, origin, scale);
		}


		//draws the start and end nodes from the list for ASearch
		if (startToEndNode != null){
			g2.setColor(Mapper.HIGHLIGHT_COLOUR);
			for (Node n : startToEndNode)
				n.draw(g2, screen, origin, scale);
		}

		//draw the highlighted segment, if it exists.
		if(highlightedSegments != null){
			g2.setColor(Color.red);
			for(Segment s: highlightedSegments)
				s.draw(g2, origin, scale);
		}

		//draws and highlights the articulation points
		if(highlightAP !=null){
			g2.setColor(Color.magenta);
			for(Node n: highlightAP){
				n.draw(g2, screen, origin, scale);
			}
		}
	}

	public void setHighlight(Node node) {
		this.highlightedNode = node;
	}
	public void setHighlight(Collection<Road> roads) {
		this.highlightedRoads = roads;
	}
	public void addNode(Node node) {startToEndNode.add(node);}
	public List <Node> getList () {return startToEndNode;} //gets the list
	public void setHighlightSegments (List <Segment> segment){ this.highlightedSegments = segment; }
	public List <Segment> getSegmentList (){return highlightedSegments;}
	public void setHighlightedAP (List <Node> node ){this.highlightAP = node;}
}

// code for COMP261 assignments