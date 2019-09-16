public class ASearch implements Comparable <ASearch>  {
   private Node currentNode;
   //private ASearch previousSearch;
   private Node previousSearch;
   private double gscore;
   private double fscore;


//A compareTo method to compare the heuristics between the two nodes
   public int compareTo(ASearch other){
       if(this.fscore < other.fscore){return -1;}
       else if(this.fscore > other.fscore){return 1;}
       else {return 0;}
   }


   public ASearch(Node currentNode, Node previousSearch, double gscore, double fscore){
       this.currentNode = currentNode;
       this.previousSearch = previousSearch;
       this.gscore = gscore;
       this.fscore = fscore;

   }

   //getters and setters
    public Node getNode(){return currentNode;}
    public double getGscore() {return gscore;}
    //public Node getParentNode() {return previousSearch;}



}
