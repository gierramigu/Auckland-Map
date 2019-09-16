import java.util.*;
public class APNode {
    public Node node;
    public APNode parent;
    public int depth; //how many nodes its connected to
    public List <Node> listChildren;

    public APNode(Node node, int depth, APNode parent){
        this.node = node;
        this.depth = depth;
        this.parent = parent;
        listChildren = null;
    }

    public void listChildren (List<Node> listChildren){
        this.listChildren = listChildren;
    }

}
