package homework3;

import java.awt.Point;
import java.util.ArrayList;

/**
 *
 * @author Tony Mendoza and Kristofer Giddens
 */
public class SensorNode {
    ArrayList<SensorNode> edges; // whatever it is connected to 
    Point point; // starting point
    public boolean discovered; // discovered flag
    public int layer; // layer it belongs to in relation to sourcen node
    public int tree; //
    
    // constructor 
    public SensorNode(int x, int y){
        point = new Point(x, y);
        edges = new ArrayList<>();
    }
    
    // return point of node
    public Point getPoint(){
        return point;
    }
    
    // add a unique edge
    public void connectEdge(SensorNode node){
        if(!edges.contains(node))
            edges.add(node);
    }
    
    // return nodes
    public SensorNode[] getConnections(){
        return edges.toArray(new SensorNode[edges.size()]);
    }

    // toString
    @Override
    public String toString() {
        return "{(" + point.x + ", " + point.y + "), tree=" + tree + ", layer=" + layer + "}";
    }
}
