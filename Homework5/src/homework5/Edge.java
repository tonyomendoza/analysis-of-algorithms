/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package homework5;

import java.util.Objects;

/**
 *
 * @author Tony Mendoza
 */
public class Edge implements Comparable {
    SensorNode source;
    SensorNode destination;
    double weight;
    
    public Edge(SensorNode source, SensorNode destination, double weight){
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    public SensorNode getSource() {
        return source;
    }

    public SensorNode getDestination() {
        return destination;
    }  

    public double getWeight() {
        return weight;
    }
       
    @Override
    public int compareTo(Object t) {
        Edge edge = (Edge)t;
        if(weight > edge.getWeight())
            return 1;
        else if(weight < edge.getWeight())
            return -1;
        return 1;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + Objects.hashCode(this.source);
        hash = 13 * hash + Objects.hashCode(this.destination);
        hash = 13 * hash + (int) (Double.doubleToLongBits(this.weight) ^ (Double.doubleToLongBits(this.weight) >>> 32));
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
        final Edge other = (Edge) obj;
        if (Double.doubleToLongBits(this.weight) != Double.doubleToLongBits(other.weight)) {
            return false;
        }
        if (!Objects.equals(this.source, other.source) && !Objects.equals(this.source, other.destination) ) {
            return false;
        }
        if (!Objects.equals(this.destination, other.destination) && !Objects.equals(this.destination, other.source) ) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Edge{" + "source=" + source + ", destination=" + destination + ", weight=" + weight + '}';
    }
}