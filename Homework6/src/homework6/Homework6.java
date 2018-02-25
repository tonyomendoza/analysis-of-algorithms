package homework6;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 *
 * @author Tony Mendoza and Kristofer Giddens
 */
class InitialEnergyComparator implements Comparator<SensorNode> {

    @Override
    public int compare(SensorNode a, SensorNode b) {
        if (a.getEnergyLevel() > b.getEnergyLevel() && a.getStoredPackets() < a.getDataCapacity()) {
            return -1;
        } else if (a.getEnergyLevel() < b.getEnergyLevel() && b.getStoredPackets() < a.getDataCapacity()) {
            return 1;
        } else {
            return 0;
        }
    }
}

public class Homework6 {

    static Scanner input;
    static LinkedHashMap<Point, SensorNode> sensorNodes;
    static Random random;
    static int x, y, N, Tr, p, q, m, sourceIndex;
    static SensorNode source;
    static SensorNode target;
    static ArrayList<SensorNode> pathfinding;
    static ArrayList<Edge> results;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        input = new Scanner(System.in);

        // Initializes the SensorNodes.
        // Almost everything in the last homework is in this class with the exception of
        //  the new prompts asking for DG amount (p), DG capacity (q), and Storage capacity (m)
        Initialize();

        // Current work
        System.out.println("Input the range of all the node's intial energy range. First the minimum:");
        double a = 10000.0;//input.nextDouble();
        System.out.println("Then the maximum:");
        double b = 100000.0;///input.nextDouble();

        Set set = sensorNodes.entrySet();
        Iterator iterator = set.iterator();
        int i = 0;
        while (iterator.hasNext()) { // for every node
            Map.Entry entry = (Map.Entry) iterator.next();
            SensorNode node = (SensorNode) entry.getValue();

            // set the range of hte initial energy
            double initialEnergy = (Math.random() * (b - a));
            if (initialEnergy < a) {
                initialEnergy += a;
            }
            node.setInitialEnergyLevel(initialEnergy);
        }

        //Dijkstra(false);
        System.out.println("\tData Resiliency = " + Algorithm1(5));
        System.out.println("\tData Resiliency = " + Algorithm2(5));
        System.out.println("\tData Resiliency = " + Algorithm3(5));
    }

    static void Initialize() {

        // Set the width and height of the map, the amount of sensor nodes, and the transmisison range
        System.out.println("First, we need to determine the size of the sensor network.");
        System.out.println("What is width of the sensor network?");
        //x = input.nextInt();
        x = 100;
        System.out.println("What is length of the sensor network?");
        //y = input.nextInt();
        y = 100;
        System.out.println("Next, how many sensor nodes are there?");
        //N = input.nextInt();
        N = 10;
        sensorNodes = new LinkedHashMap<>();
        System.out.println("And, what is their transmission range?");
        //Tr = input.nextInt();
        Tr = 75;
        p = 0;
        /*do {
            System.out.println("Next, how many of the sensor nodes are data generators?");
            p = input.nextInt();
        } while (p >= N || p < 0);*/
        System.out.println("Only of the sensor nodes are data generators.");
        p = 1;
        System.out.println("And it is storage depleted.");
        //q = input.nextInt();
        q = 0;
        System.out.println("Lastly, what is the storage capacity of the remaining storage nodes? (in packets)");
        //m = input.nextInt();
        m = 5; // let's make the test a small number

        // Randomly generate every SensorNode.
        random = new Random();
        source = null; // however, set the source node randomly here
        sourceIndex = (int) (random.nextDouble() * N); // use thiss to rnadomly set source
        int P = 0;
        // create nodes randomly
        for (int n = 0; n < N; n++) {
            Point point = new Point((int) (random.nextDouble() * x), (int) (random.nextDouble() * y)); // assign a random point
            if (sensorNodes.containsKey(point) && N <= (x * y)) { // if the point already exists and there is enough points to go around, then try to randomize again
                n--;
                continue;
            }
            SensorNode node;
            if (P < p) {
                node = new SensorNode(point.x, point.y, 0, true);
                P++;
            } else {
                node = new SensorNode(point.x, point.y, m, false);
            }
            node.setIndex(n);
            sensorNodes.put(new Point(point.x, point.y), node); // put in the point and the sensor node in the hashmap for comparision and retrieval later
            if (sourceIndex == n) // here, we will set node @ random to be our source node
            {
                source = node;
            }
        }

        ArrayDeque<SensorNode> breadthQueue = new ArrayDeque<>(); // used for breadth search
        breadthQueue.offer(source); // offer the source to the queue
        source.setDiscovered(true); //source is discovered
        int i = 0; // used for iterating through the node array for other possible node trees
        SensorNode current; // used for breadth search
        int layerCounter = 1; // used for comparing the layer of the bfsTree in breadth search

        // breadth search algorith implementation
        while (!breadthQueue.isEmpty()) { // while there is something in the queue
            current = breadthQueue.poll(); // poll the current node for analyzing

            double recievingCost = (100 * 3200); // the recieving cost is the same for all nodes

            // simulate a node scanning for a transmission range
            // could perhaps be done more efficently with listeners, but, didn't think of it until after
            for (int nY = 0 - Tr; nY <= Tr; nY++) { // check every x point within Tr
                for (int nX = 0 - Tr; nX <= Tr; nX++) { // check every y point within Tr
                    if (nX != 0 && nY != 0) { // ignore the center point (the current node's point)
                        Point point = new Point(current.getPoint().x + nX, current.getPoint().y + nY); // get the point we are checking
                        if (sensorNodes.containsKey(point)) { // if that point exists in our sensor node hashmap
                            SensorNode node = sensorNodes.get(point); // then it is the sensor node we want
                            // Calculate what it will cost to send from the node to this vertex.
                            // Eamp = Eelec, so multiplying Eelec by k (3200 bits) + Eamp by k by distance^2, will give us this formula down here
                            double transmissionCost = (100 * 3200) + (100 * 3200 * ((Math.pow(current.getPoint().x - node.getPoint().x, 2) + Math.pow(current.getPoint().y - node.getPoint().y, 2))));

                            current.connectEdge(node, recievingCost + transmissionCost); // connect the current node to that found node

                            if (!sensorNodes.get(point).discovered) { // if it hasn't been discovered
                                breadthQueue.offer(node); // offer it to the breadth queue for analyzing its edges
                                node.setDiscovered(true); // set node's discovery to true
                            }
                        }
                    }
                }
            }
        }

        // While we add every node to the PQ, we check if they are all connected at the same time.
        Set set = sensorNodes.entrySet();
        Iterator iterator = set.iterator();
        i = 0;
        while (iterator.hasNext()) { // for every node
            Map.Entry entry = (Map.Entry) iterator.next();
            SensorNode node = (SensorNode) entry.getValue();
            // List the nodes to the user, so they can refer to it when setting the source and target nodes.
            if (node.isDataGenerator()) {
                System.out.println("\tNode @" + node.getPoint() + " is a data generator. Refer to it as index: " + node.getIndex());
            } else {
                System.out.println("\tNode @" + node.getPoint() + " is a storage generator. Refer to it as index: " + node.getIndex());
            }
            node.setIndex(i);

            // If the node has not been discovered, then it is not connected and we must quit the program
            if (!node.discovered) {
                System.out.println("This node is not connected. Please restart the program.");
                System.exit(0);
            }
            i++;
            // you can set its cost to Integer.MAX_VALUE for INFINITY, however, our node already does that when initializing.
        }

        // If there are not enough storage nodes to store hte packets, then we must quit the program..
        if (p * q > (N - p) * m) {
            System.out.println("There is not enough storage nodes to store the packets. Please restart the program.");
            System.exit(0);
        }
    }

    static int Offloading(ArrayList<SensorNode> nodes, int packets) {
        String error = "";
        PriorityQueue<SensorNode> queue = new PriorityQueue<>(); // create a PQ
        // While we add every node to the PQ, we check if they are all connected at the same time.
        for (int j = 0; j < nodes.size(); j++) {
            SensorNode node = nodes.get(j);

            //if (node.getEnergyLevel() > (1 * 32) * packets) { // if able to at least recieve
            queue.add(node); // this node is connected, so add it to the PQ
            node.setChildSensorNode(null);
            node.setParentSensorNode(null);

            if (node != target) {
                node.setAsTarget(false);
            }
            if (node != source) {
                node.setAsSource(false);
                node.setCost(Integer.MAX_VALUE);
            }
            //}
        }

        while (!queue.isEmpty()) {
            SensorNode node = queue.poll();

            node.disabledEdges = 0;

            double recievingCost = (1 * 32) * packets; // the recieving cost is the same for all nodes

            for (int i = 0; i < node.getConnections().length; i++) { // for every edge connected
                Edge edge = node.getConnections()[i];

                if (node != target) {
                    // Calculate what it will cost to send from the node to this vertex.
                    // Eamp = Eelec, so multiplying Eelec by k (3200 bits) + Eamp by k by distance^2, will give us this formula down here
                    double transmissionCost = ((1 * 32) + (1 * 32 * ((Math.pow(node.point.x - edge.destination.point.x, 2) + Math.pow(node.point.y - edge.destination.point.y, 2))))) * packets;

                    // If the cost is cheaper, then set the cheaper cost
                    if (node.cost + recievingCost + transmissionCost < edge.destination.cost && edge.destination != node.getParentSensorNode()) {
                        if (edge.destination.disabled != true && node.getEnergyLevel() >= transmissionCost) {
                            edge.destination.setCost(node.cost + recievingCost + transmissionCost);
                            edge.destination.setParentSensorNode(node); // set parent
                            node.setChildSensorNode(edge.destination); // set child

                            // Inefficient way to sort our priority queue. But we didn't really want to make a prioirty queue. We did that in Data Structures.
                            queue.remove(edge.destination); // remove O(log n)?
                            queue.offer(edge.destination); // reinsert O(log n)
                            // SORTING complete
                            //continue;
                        } else {
                            edge.destination.disabled = true;
                            edge.destination.setCost(Integer.MAX_VALUE);
                            node.disabledEdges++;
                            if (!error.isEmpty()) {
                                error += "\n";
                            }
                            error += "\t\t" + "Node disabled: " + edge.destination;
                            if (node.getConnections().length == node.disabledEdges) {
                                if (node != source) {
                                    queue.offer(node.getParentSensorNode());
                                    node.getParentSensorNode().setChildSensorNode(null);
                                    node.setParentSensorNode(null);
                                }
                                node.disabled = true;
                                error += "\n\t\t" + "Node disabled: " + node;
                                break;
                            }
                        }
                    }
                }
            }
        }

        // This code is used for finding the actual cost-effective path to the node
        if (!source.disabled) {
            pathfinding = new ArrayList<>();  // this structure holds the above mentioned apth
            SensorNode t = target;
            while (t != source) {
                if (t == null) {
                    System.out.println("\tx Target is unreachable @ " + pathfinding.get(pathfinding.size() - 1) + ". Path from " + source);
                    System.out.println(error);
                    return -1;
                }

                pathfinding.add(t);
                t = t.getParentSensorNode(); // follow it parent to parent, back to source
            }
            pathfinding.add(t);
        } else {
            System.out.println("\tx Not enough energy to complete sending @ " + "from " + source);
            System.out.println(error);
            return -1;
        }

        Collections.sort(pathfinding); // sort each edge in the found paths by their cost
        /*for (int c = 0; c < pathfinding.size(); c++) {
            double recievingCost = (1 * 32) * packets;
            SensorNode node = pathfinding.get(c);
            if (node != target) {
                SensorNode nodeTarget = pathfinding.get(c + 1);
                double transmissionCost = packets * ((1 * 32) + (1 * 32 * ((Math.pow(node.point.x - nodeTarget.point.x, 2) + Math.pow(node.point.y - nodeTarget.point.y, 2)))));

                if (transmissionCost > pathfinding.get(c).getEnergyLevel() && recievingCost > pathfinding.get(c + 1).getEnergyLevel()) {
                    System.out.println("\t- " + node + ", " + nodeTarget + ": Cannot complete data storage.");
                    return 0;
                }
            }
        }*/

        for (int c = 0; c < pathfinding.size(); c++) {
            double recievingCost = (1 * 32) * packets;
            SensorNode node = pathfinding.get(c);
            if (node != target) {
                SensorNode nodeTarget = pathfinding.get(c + 1);
                double transmissionCost = packets * ((1 * 32) + (1 * 32 * ((Math.pow(node.point.x - nodeTarget.point.x, 2) + Math.pow(node.point.y - nodeTarget.point.y, 2)))));
                node.setEnergyLevel(node.getEnergyLevel() - transmissionCost);
                nodeTarget.setEnergyLevel(nodeTarget.getEnergyLevel() - recievingCost);
            } else {
                node.addStoredPackets(1);
            }
        }
        System.out.println("\to Data sent to Node" + target.index + "@ " + target.getPoint() + " from Node" + source.index + "@ " + source.getPoint() + " with cost of " + target.getCost());
        return 1;
    }

    static ArrayList<SensorNode> ResetSensorNodes() {
        SensorNode[] n = sensorNodes.values().toArray(new SensorNode[sensorNodes.size()]);
        ArrayList<SensorNode> nodes = new ArrayList<>();
        for (int i = 0; i < n.length; i++) {
            nodes.add(n[i]);
        }

        source = nodes.get(0);
        source.setAsSource(true);
        source.setCost(0); // set source to 0
        source.setEnergyLevel(source.initialEnergyLevel);
        source.disabled = false;
        source.disabledEdges = 0;
        source.storedPackets = 0;
        nodes.remove(source);
        for (int i = 1; i < nodes.size(); i++) {
            // set the range of the initial energy
            nodes.get(i).setEnergyLevel(nodes.get(i).initialEnergyLevel);
            nodes.get(i).disabled = false;
            nodes.get(i).disabledEdges = 0;
            nodes.get(i).storedPackets = 0;
        }

        return nodes;
    }

    static double Algorithm1(int aTotal) {
        System.out.println("Performing Network-Based Algorithm");
        ArrayList<SensorNode> nodes = ResetSensorNodes();

        // Step 1: Sort storage node in non-ascending order
        nodes.sort(new InitialEnergyComparator()); // sort through comparator

        // Step 2: Find the top k highest energy nodes, such that kTotal < aTotal <= kTotal + 1
        int k = 0;
        int kTotal = 0;
        while (kTotal < aTotal && k < nodes.size() - 1) {
            kTotal += nodes.get(k).getDataCapacity();
            k++;
        }
        if (kTotal < aTotal) { // not enough nodes?
            System.out.println("\tx Not enough storage nodes?");
            return -1;
        }
        k--;
        nodes.add(source);

        // Step 3 for i <= k
        int i = 0;
        for (i = 0; i < k; i++) {
            target = nodes.get(i);
            target.setAsTarget(true);

            int result = Offloading(nodes, 1);
            switch (result) {
                case -1:
                    //i = k;
                    break;
                default:
                    break;
            }
        }
        target = nodes.get(i + 1);
        target.setAsTarget(true);
        while (i < aTotal) {
            int result = Offloading(nodes, 1);
            switch (result) {
                case -1:
                    i = aTotal;
                    break;
                default:
                    break;
            }
        }

        if (i < aTotal) {
            System.out.println("\tx Could not offload all remaining items: " + (aTotal - i));
        }

        double dataResilience = 0;
        for (SensorNode node : nodes) {
            //if (node.getEnergyLevel() != node.getInitialEnergyLevel()) {
            dataResilience += node.getEnergyLevel() * node.getDataCapacity();// 
            //}
        }
        return dataResilience;
    }

    static double Algorithm2(int aTotal) {
        System.out.println("Performing Node-Based Algorithm");
        ArrayList<SensorNode> nodes = ResetSensorNodes();

        int i = 0;
        nodes.sort(new InitialEnergyComparator());
        // Step 1: while there is still data to unload
        while (i < aTotal) {
            if (source.getEnergyLevel() > 0) {
                nodes.add(source);

                // Step 2: find the  storage node with available storage and highest energy
                target = nodes.get(0);
                if (target.getEnergyLevel() <= 0) {
                    nodes.sort(new InitialEnergyComparator());
                    target = nodes.get(0);
                }
                target.setAsTarget(true);

                // Step 3: Offload to the storage node with higehst energy and available storage, then update energy levels of all nodes involved
                int result = Offloading(nodes, 1);
                switch (result) {
                    case -1:
                        i = aTotal;
                        break;
                    default:
                        i++;
                        nodes.remove(source);
                        break;
                }
            }
        }
        double dataResilience = 0;
        for (SensorNode node : nodes) {
            //if (node.getEnergyLevel() != node.getInitialEnergyLevel()) {
            dataResilience += node.getEnergyLevel() * node.getDataCapacity();// 
            //}
        }

        return dataResilience;
    }

    static double Algorithm3(int aTotal) {
        System.out.println("Performing Data-Based Algorithm");
        ArrayList<SensorNode> nodes = ResetSensorNodes();

        // Step 1: for each of the data items
        for (int i = 0; i < aTotal; i++) {
            if (source.getEnergyLevel() > 0) {
                nodes.sort(new InitialEnergyComparator());
                nodes.add(source);

                target = nodes.get(0);
                target.setAsTarget(true);

                // Step 2: Offload to the storage node with higehst energy and available storage, then update energy levels of all nodes involved
                int result = Offloading(nodes, 1);
                switch (result) {
                    case -1:
                        //i = aTotal;
                        break;
                    default:
                        nodes.remove(source);
                        break;
                }
            }
        }
        double dataResilience = 0;
        for (SensorNode node : nodes) {
            //if (node.getEnergyLevel() != node.getInitialEnergyLevel()) {
            dataResilience += node.getEnergyLevel() * node.getDataCapacity();// 
            //}
        }

        return dataResilience;
    }
}
