import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.*;

/**
 * This is the main class for the mapping program. It extends the GUI abstract
 * class and implements all the methods necessary, as well as having a main
 * function.
 * 
 * @author tony
 */
public class Mapper extends GUI {
	public static final Color NODE_COLOUR = new Color(77, 113, 255);
	public static final Color SEGMENT_COLOUR = new Color(130, 130, 130);
	public static final Color HIGHLIGHT_COLOUR = new Color(255, 219, 77);

	// these two constants define the size of the node squares at different zoom
	// levels; the equation used is node size = NODE_INTERCEPT + NODE_GRADIENT *
	// log(scale)
	public static final int NODE_INTERCEPT = 1;
	public static final double NODE_GRADIENT = 0.8;

	// defines how much you move per button press, and is dependent on scale.
	public static final double MOVE_AMOUNT = 100;
	// defines how much you zoom in/out per button press, and the maximum and
	// minimum zoom levels.
	public static final double ZOOM_FACTOR = 1.3;
	public static final double MIN_ZOOM = 1, MAX_ZOOM = 200;

	// how far away from a node you can click before it isn't counted.
	public static final double MAX_CLICKED_DISTANCE = 0.15;

	// these two define the 'view' of the program, ie. where you're looking and
	// how zoomed in you are.
	private Location origin;
	private double scale;

	// our data structures.
	private Graph graph;
	private Trie trie;

	private Node startNode = null; //start
	private Node endNode = null; //end
	private Node neighbour = null; //neighbour



	@Override
	protected void doArtPoints() {
		articPoints articulationPoints = new articPoints(graph);
		Set<Node> apAlgorithm = new HashSet<>(articulationPoints.findArtPoints());
		graph.highlightAP.addAll(apAlgorithm);
		graph.setHighlightedAP(graph.highlightAP);
		System.out.println("AP TEST");
		getTextOutputArea().setText("Articulation Points: " + apAlgorithm.size());
	}

	@Override
	protected void redraw(Graphics g) {
		if (graph != null)
			graph.draw(g, getDrawingAreaDimension(), origin, scale);
	}



	@Override
	protected void onClick(MouseEvent e) {

		//this statement makes sure that only two nodes are highlighted on the pane every time
		if(graph.getList().size() >= 2){
			startNode = null;
			endNode = null;
			graph.getList().clear(); //clears the startToEndNode List
			graph.getSegmentList().clear(); //clears the segment list
		}

		Location clicked = Location.newFromPoint(e.getPoint(), origin, scale);
		// find the closest node.
		double bestDist = Double.MAX_VALUE;
		Node closest = null;

		for (Node node : graph.nodes.values()) {
			double distance = clicked.distance(node.location);
			if (distance < bestDist) {
				bestDist = distance;
				closest = node;
			}
		}

		// if it's close enough, highlight it and show some information.
		if (clicked.distance(closest.location) < MAX_CLICKED_DISTANCE) {
			graph.setHighlight(closest);
			getTextOutputArea().setText(closest.toString());
			findNodes(closest); //you get the closest Node in order to highlight
			graph.setHighlightSegments(graph.getSegmentList()); //highlights the segments to show the shortest path
		}

		//for printing
//		Map<String, Double> test = new HashMap <>();
//		for(Segment s: getPathSegments() ){
//			String name = s.road.name;
//			double length = s.length;
//			if(!test.containsKey(name)){
//				test.put(name, length);
//			}
//			else {
//				double segment = test.get(name);
//				double lengthSegment = segment + length;
//				test.put(name,lengthSegment);
//			}
//		}
//
//		Set<String> setTest = new HashSet<>();
//		double total =0;
//		double totalSegmentLength = 0;
//		for(Segment s: getPathSegments()){
//			String name = s.road.name;
//			if(!setTest.contains(name)){
//				setTest.add(name);
//				total += test.get(name);
//			}
//		}
	}

	//Finding the start and end nodes
	public void findNodes (Node node ){
		if(startNode == null){
			startNode = node;
			graph.addNode(startNode);
			System.out.println("Start Found");
		}
		else if(startNode != null){ //finding the end point
			endNode = node;
			graph.addNode(endNode);
			findShortestPath(startNode,endNode);
			System.out.println("End Found");
		}
	}


	//gets and returns the path after the A*Search algorithm has been established
	//essentially gets all the nodes that is along the path
	public List <Node> getPath (Node start, Node end ){
		List<Node> path = new ArrayList<>();
		Node tempNode = end;
		System.out.println("DO YOU EVEN COME THIS WAY");
		//if the current node is not equal to the root
		while(tempNode != start){ //keep adding the parentNode until the currentNode is the start goal
			path.add(tempNode); // add to the list of paths
			tempNode = tempNode.getPrev();
			System.out.println("GET THE PATH");
		}

		path.add(start);
		return path; //returning the list of nodes
	}

	//if other methods have been uncommented this method will apply
	public List <Segment> getPathSegments(){
		List<Segment> segment = new ArrayList<>();
		List<Node> getNodePath = getPath(startNode, endNode); //error here
		for(int i = 0; i < getNodePath.size()-1; i++){
			Node nodeOne = getNodePath.get(i);
			Node nodeTwo = getNodePath.get(i+1);
			for(Segment s: graph.segments){
				if(s.start == nodeOne && s.end == nodeTwo){ graph.highlightedSegments.add(s);}
				if(s.start == nodeOne && s.end == nodeTwo) { graph.highlightedSegments.add(s);}
				segment.add(s);
			}
		}
		return segment;
	}

	//A* Search
	//REMINDER: PRINT OUT THE ROADS ON THE ROUTE(DUPLICATES ARE OK)
	public void findShortestPath (Node start, Node end) {
		double startFScore = start.getLocation().distance(end.getLocation());
		Set<Node> visited = new HashSet<>(); //stores all visited nodes
		Queue<ASearch> fringe = new PriorityQueue<>(); //priority queue for the fringe
		ASearch rootNode = new ASearch(start, null, 0, startFScore); //heuristic parameter gets the distance between the start and the end node
		fringe.offer(rootNode); //adding the startNode into the queue
		while (!fringe.isEmpty()) {
			System.out.println("Goes through the fringe");
			ASearch currentNode = fringe.poll(); //this would take the shortest heuristic from the priority queue
            double gScore = currentNode.getGscore();
            Node previousNode = currentNode.getNode();
			if (!visited.contains(currentNode.getNode())) { //checks if current node is not visited
				visited.add(currentNode.getNode());
				currentNode.getNode().SetParentNode(previousNode); //set the currentNode's previous node's parentNode to the previousNode
               System.out.println("ASearch Test");
			}
			if (currentNode.getNode() == endNode) { //the currentNode reaches the end highlight the path
				System.out.println("GOAL"); //test
				List<Node> node = getPath(start,end); //getting the path list which has all the nodes in between the start and end
				for(int i = 0; i < node.size()-1 ; i++) {
					Node node1 = node.get(i); //gets the nodes in between the start and end nodes
					Node node2 = node.get(i+1);
					//these check if the start and node of each segment is part of the path
					for(Segment s: graph.segments){
						//if the starting node of the segment is equal to node1 and segment's end is equal to node2
						if(s.start == node1 && s.end == node2){ graph.highlightedSegments.add(s);}
						//same applies here but for the other case
						if(s.start == node2 && s.end == node1) { graph.highlightedSegments.add(s);}//then add those segments in the list to be highlighted
					}
				}
				break;
			}
			//iterating through every segment that's attached to the current node
			for(Segment s : currentNode.getNode().segments){ //going through the segments connected to the currentNode
				System.out.println("Getting the segments attached to the currentNode");
				if(s.start == currentNode.getNode()){neighbour = s.end;} //gets the neighbouring nodes of the currentNode
				if(s.end == currentNode.getNode()) {neighbour = s.start;}
				if(!visited.contains(neighbour)){
					visited.add(neighbour);
					//iterates and updates the gScore and fScore if the neighbour is not in the visited set
					double fScore = neighbour.getLocation().distance(endNode.getLocation());
					double g = gScore + s.length;
					double f = currentNode.getGscore() + fScore; //new gScore + the fScore from the root and its neighbour
					neighbour.prev = currentNode.getNode(); //setting neighbour's parent node to the current node
					ASearch newSearch = new ASearch(neighbour, previousNode, g, f );
					fringe.offer(newSearch); //add the next node to the fringe
					System.out.println("Do you even work????");
				}
			}
		}
	}

	@Override
	protected void onSearch() {
		if (trie == null)
			return;

		// get the search query and run it through the trie.
		String query = getSearchBox().getText();
		Collection<Road> selected = trie.get(query);

		// figure out if any of our selected roads exactly matches the search
		// query. if so, as per the specification, we should only highlight
		// exact matches. there may be (and are) many exact matches, however, so
		// we have to do this carefully.
		boolean exactMatch = false;
		for (Road road : selected)
			if (road.name.equals(query))
				exactMatch = true;

		// make a set of all the roads that match exactly, and make this our new
		// selected set.
		if (exactMatch) {
			Collection<Road> exactMatches = new HashSet<>();
			for (Road road : selected)
				if (road.name.equals(query))
					exactMatches.add(road);
			selected = exactMatches;
		}

		// set the highlighted roads.
		graph.setHighlight(selected);

		// now build the string for display. we filter out duplicates by putting
		// it through a set first, and then combine it.
		Collection<String> names = new HashSet<>();
		for (Road road : selected)
			names.add(road.name);
		String str = "";
		for (String name : names)
			str += name + "; ";

		if (str.length() != 0)
			str = str.substring(0, str.length() - 2);
		getTextOutputArea().setText(str);
	}

	@Override
	protected void onMove(Move m) {
		if (m == GUI.Move.NORTH) {
			origin = origin.moveBy(0, MOVE_AMOUNT / scale);
		} else if (m == GUI.Move.SOUTH) {
			origin = origin.moveBy(0, -MOVE_AMOUNT / scale);
		} else if (m == GUI.Move.EAST) {
			origin = origin.moveBy(MOVE_AMOUNT / scale, 0);
		} else if (m == GUI.Move.WEST) {
			origin = origin.moveBy(-MOVE_AMOUNT / scale, 0);
		} else if (m == GUI.Move.ZOOM_IN) {
			if (scale < MAX_ZOOM) {
				// yes, this does allow you to go slightly over/under the
				// max/min scale, but it means that we always zoom exactly to
				// the centre.
				scaleOrigin(true);
				scale *= ZOOM_FACTOR;
			}
		} else if (m == GUI.Move.ZOOM_OUT) {
			if (scale > MIN_ZOOM) {
				scaleOrigin(false);
				scale /= ZOOM_FACTOR;
			}
		}
	}

	@Override
	protected void onLoad(File nodes, File roads, File segments, File polygons) {
		graph = new Graph(nodes, roads, segments, polygons);
		trie = new Trie(graph.roads.values());
		origin = new Location(-250, 250); // close enough
		scale = 1;
	}

	/**
	 * This method does the nasty logic of making sure we always zoom into/out
	 * of the centre of the screen. It assumes that scale has just been updated
	 * to be either scale * ZOOM_FACTOR (zooming in) or scale / ZOOM_FACTOR
	 * (zooming out). The passed boolean should correspond to this, ie. be true
	 * if the scale was just increased.
	 */
	private void scaleOrigin(boolean zoomIn) {
		Dimension area = getDrawingAreaDimension();
		double zoom = zoomIn ? 1 / ZOOM_FACTOR : ZOOM_FACTOR;

		int dx = (int) ((area.width - (area.width * zoom)) / 2);
		int dy = (int) ((area.height - (area.height * zoom)) / 2);

		origin = Location.newFromPoint(new Point(dx, dy), origin, scale);
	}

	public static void main(String[] args) {
		new Mapper();
	}
}

// code for COMP261 assignments