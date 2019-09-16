import java.util.Collection;
import java.util.Stack;
import java.util.HashSet;
import java.util.*;


public class articPoints {
    private Graph g;
    Set<Node> visitedNode = new HashSet<>();
    Set<Node> articPoints = new HashSet<>();

    public articPoints(Graph g) {
        this.g = g;
    }

    public Set<Node> findArtPoints () {
        for (Node n : g.nodes.values()) {
            int numSubtrees = 0;
            APNode root = new APNode(n, 0, null);
            findNeighbours(root.node); //gets the node of the root
            if (!visitedNode.contains(n)) {
                //gets the findNeighbours Set and iterates through
                for (Node neigh : findNeighbours(n)) {
                    APNode neighbourAPoint = new APNode(neigh, 1, root);
                    if (neigh.depth == Integer.MAX_VALUE) {
                        iterArtPoints(neighbourAPoint, 1, root); //call main method here
                        numSubtrees++;
                    }
                    if (numSubtrees > 1) { articPoints.add(root.node); }
                        visitedNode.add(root.node);
                        System.out.println("Test");
                }
            }
        }
        return articPoints;
    }

    //Articulation Points Algorithm
    public void iterArtPoints (APNode initialNode, int depth, APNode root){
        //initializing stack as a single element <firstNode, depth, root>
        Stack <APNode> fringe = new Stack<>();
        int infinite = Integer.MAX_VALUE;
        initialNode.parent = root;
        fringe.push(initialNode);
        while(!fringe.isEmpty()){ //iterating through the stack until empty and peeking whats on the top
        APNode currentNode = fringe.peek();
            //first time visit
            if(currentNode.node.depth == infinite ){ //if this node hasn't been visited, then visit it
                currentNode.node.depth = currentNode.depth; //initializing depth and reachBack
                currentNode.node.reachBack = currentNode.depth;
                //getting all the children from this currentNode except the parent and adding it to the list of children
                currentNode.listChildren(findChildren(currentNode.node,currentNode.parent.node));
            }
             //going through the children list to check for each child in the currentNode the depth and reachBack and updates the values on them
            else if(!currentNode.listChildren.isEmpty()){
                ///get a child from the list of children and remove it in the list
                 Node currentChild = currentNode.listChildren.remove(0);
                if(currentChild.depth < infinite){ //if the child has been visited
                    currentNode.node.reachBack = Math.min(currentNode.node.reachBack, currentChild.depth); //update reachBack
                }
                else { //if current list of children is empty and there's no more child node then push to the stack/fringe
                  fringe.push(new APNode(currentChild, currentNode.node.depth + 1, currentNode));
                }
            }
            else { //if (children(*n) is empty, no more child node
                if(currentNode.node.nodeID != initialNode.node.nodeID){
                currentNode.parent.node.reachBack = Math.min(currentNode.parent.node.reachBack, currentNode.node.reachBack );
                    if(currentNode.node.reachBack >= currentNode.parent.node.depth){
                        articPoints.add(currentNode.parent.node);
                    }
                }
               //removing manually
               Node finishedNode = fringe.pop().node;
               visitedNode.add(finishedNode); //add the currentNode into the list of visited nodes
            }
        }
    }

    //Finding the neighbouring nodes and returns the neighbouringNodes
    public Set<Node> findNeighbours (Node startNode){
        Set<Node> neighbourNodes = new HashSet<>();
        for (Segment s: startNode.segments){
            neighbourNodes.add(s.end);
            neighbourNodes.add(s.start);
        }
        neighbourNodes.remove(startNode);
        return neighbourNodes;
    }

    //returns the list of children of the currentNode (without the parent)
    public List<Node> findChildren (Node currentNode, Node parent){
        List<Node> listChildren = new ArrayList<>();
        for(Segment s: currentNode.segments){
            //checking the parent node if it has children and adding those children to the list
            if(parent.nodeID != s.end.nodeID){ listChildren.add(s.end); }
            if(parent.nodeID != s.start.nodeID){ listChildren.add(s.start);}
        }
        return listChildren;
    }

}
