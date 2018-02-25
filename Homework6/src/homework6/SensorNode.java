package homework6;

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
    protected int storedPackets;
    protected boolean generatesData;
    protected double cost;
    protected SensorNode minimumSpan;
    int index;
    double initialEnergyLevel;
    double energyLevel;
    protected SensorNode parentSensorNode;
    protected SensorNode childSensorNode;
    protected boolean source;
    protected boolean target;
    public boolean disabled;
    public int disabledEdges;
    
    // constructor 
    public SensorNode(int x, int y, int dataCapacity, boolean generatesData){
        point = new Point(x, y);
        edges = new ArrayList<>();
        storedPackets = 0;
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

    public int getStoredPackets() {
        return storedPackets;
    }
    
    public void addStoredPackets(int value){
        storedPackets += value;
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
        return "Node" + index + "{(" + point.x + ", " + point.y + ")}, energy: " + (float)energyLevel + ", data storage: " + storedPackets;
    }

    public SensorNode getMinimumSpanNode() {
        return minimumSpan;
    }

    public void setMinimumSpanNode(SensorNode minimumSpan) {
        this.minimumSpan = minimumSpan;
    }

    public double getInitialEnergyLevel() {
        return initialEnergyLevel;
    }

    public void setInitialEnergyLevel(double initialEnergyLevel) {
        this.initialEnergyLevel = initialEnergyLevel;
    }

    public double getEnergyLevel() {
        return energyLevel;
    }

    public void setEnergyLevel(double energyLevel) {
        this.energyLevel = energyLevel;
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
    
    public void removeEdge(Edge edge){
        edges.remove(edge);
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
    /*public int compareTo(SensorNode t) {
            if(this.initialEnergyLevel > t.initialEnergyLevel)
                return -1;
            else if(this.initialEnergyLevel < t.initialEnergyLevel)
                return 1;
            return 0;
    }*/
}