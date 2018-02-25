package homework4;

import java.awt.Point;
import java.util.ArrayList;

/**
 *
 * @author  Tony Mendoza and Kristofer Giddens and Joseantonio Maciel
 */
public class SensorNode implements Comparable<SensorNode> {
    protected ArrayList<SensorNode> edges; // whatever it is connected to 
    protected Point point; // starting point
    protected boolean discovered; // discovered flag
    protected int dataCapacity;
    protected ArrayList<Packet> storedPackets;
    protected boolean generatesData;
    protected double cost;
    protected SensorNode parentSensorNode;
    protected SensorNode childSensorNode;
    int index;
    protected boolean source;
    protected boolean target;
    
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
        target = false;
        source = false;
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

    public SensorNode getParentSensorNode() {
        return parentSensorNode;
    }

    public void setParentSensorNode(SensorNode parentSensorNode) {
        this.parentSensorNode = parentSensorNode;
    }

    public SensorNode getChildSensorNode() {
        return childSensorNode;
    }

    public void setChildSensorNode(SensorNode childSensorNode) {
        this.childSensorNode = childSensorNode;
    }
            
    public boolean isSource() {
        return source;
    }

    public void setAsSource(boolean source) {
        this.source = source;
    }

    public boolean isTarget() {
        return target;
    }

    public void setAsTarget(boolean target) {
        this.target = target;
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