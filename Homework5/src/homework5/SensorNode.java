package homework5;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Objects;
 
/**
 *
 * @author  Tony Mendoza and Kristofer Giddens and Joseantonio Maciel
 */
public class SensorNode implements Comparable<SensorNode> {
    protected ArrayList<Edge> edges; // whatever it is connected to 
    protected Point point; // starting point
    protected boolean discovered; // discovered flag
    protected int dataCapacity;
    protected ArrayList<Packet> storedPackets;
    protected boolean generatesData;
    protected double cost;
    protected SensorNode minimumSpan;
    int index;
    
    // constructor 
    public SensorNode(int x, int y, int dataCapacity, boolean generatesData){
        point = new Point(x, y);
        edges = new ArrayList<>();
        storedPackets = new ArrayList();
        if(!generatesData)
            this.dataCapacity = dataCapacity;
        this.generatesData = generatesData;
        discovered = false;
        cost = Double.MAX_VALUE;
        index = -1;
    }
    
    // return point of node
    public Point getPoint(){
        return point;
    }
    
    // add a unique edge
    public void connectEdge(SensorNode node, double weight){
        Edge edge = new Edge(this, node, weight);
        if(!edges.contains(edge))
            edges.add(edge);
    }
    
    // return nodes
    public Edge[] getConnections(){
        return edges.toArray(new Edge[edges.size()]);
    }

    public int getDataCapacity() {
        return dataCapacity;
    }    

    public boolean isDiscovered() {
        return discovered;
    }

    public void setDiscovered(boolean discovered) {
        this.discovered = discovered;
    }

    public ArrayList<Packet> getStoredPackets() {
        return storedPackets;
    }

    public boolean isDataGenerator() {
        return generatesData;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
              
    // toString
    @Override
    public String toString() {
        return "{(" + point.x + ", " + point.y + ")}, cost: " + (float)cost;
    }


    public SensorNode getMinimumSpanNode() {
        return minimumSpan;
    }

    public void setMinimumSpanNode(SensorNode minimumSpan) {
        this.minimumSpan = minimumSpan;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(this.edges);
        hash = 83 * hash + Objects.hashCode(this.point);
        hash = 83 * hash + this.index;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SensorNode other = (SensorNode) obj;
        if (this.index != other.index) {
            return false;
        }
        if (!Objects.equals(this.edges, other.edges)) {
            return false;
        }
        if (!Objects.equals(this.point, other.point)) {
            return false;
        }
        return true;
    }
    
    
    @Override
    public int compareTo(SensorNode t) {
            if(this.cost > t.cost)
                return 1;
            else if(this.cost < t.cost)
                return -1;
            return 0;
    }
}