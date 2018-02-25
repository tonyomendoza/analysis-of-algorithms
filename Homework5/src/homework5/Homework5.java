package homework5;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.awt.*;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

/**
 *
 * @author Tony Mendoza and Kristofer Giddens
 */
public class Homework5 {

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

        // The homework really starts here, where we implement Kruscal's Algorithm
        DisjointSet<SensorNode> nodeSet = new DisjointSet<>(); // create a PQ
        ArrayList<Edge> edges = new ArrayList<>();
        
        // While we add every node to the PQ, we check if they are all connected at the same time.
        Set set = sensorNodes.entrySet();
        Iterator iterator = set.iterator();
        int i = 0;
        while (iterator.hasNext()) { // for every node
            Map.Entry entry = (Map.Entry) iterator.next();
            SensorNode node = (SensorNode) entry.getValue();

            // If the node has not been discovered, then it is not connected and we must quit the program
            if (!node.discovered) {
                System.out.println("A node is not connected. Please restart the program.");
                System.exit(0);
            }
            nodeSet.add(node); // this node is connected, so add it to the PQ
            for (int j = 0; j < node.getConnections().length; j++) {
                if (!edges.contains(node.getConnections()[j])) {
                    edges.add(node.getConnections()[j]);
                }
            }
            i++;
            // you can set its cost to Integer.MAX_VALUE for INFINITY, however, our node already does that when initializing.
        }

        // If there are not enough storage nodes to store hte packets, then we must quit the program..
        if (p * q > (N - p) * m) {
            System.out.println("There is not enough storage nodes to store the packets. Please restart the program.");
            System.exit(0);
        }

        // A = ∅
        results = new ArrayList<>();

        //foreach v ∈ G.V: MAKE-SET(v)
        // Already have an arraylist of vertices
        //foreach (u, v) in G.E ordered by weight(u, v), increasing:
        // Have it sorted before this step
        for (i = 0; i < edges.size(); i++) {
            SensorNode u = ((Edge) edges.get(i)).getSource();
            SensorNode v = ((Edge) edges.get(i)).getDestination();
            //if FIND-SET(u) ≠ FIND-SET(v):
            if (nodeSet.find(v) != nodeSet.find(u)) { // ???
                //A = A ∪ {(u, v)}
                results.add(edges.get(i));
                //UNION(u, v)
                nodeSet.union(u, v);
            }
        }

        // return A
        for (i = 0; i < results.size(); i++) {
            System.out.println(results.get(i).toString());
        }

        Visualize();
    }

    // Visualize
    // We will also draw the cost-effective path
    static void Visualize() {
        // We only want to represent the actual cost-effective path
        // So, this section resets every node's parent/children
        Set set = sensorNodes.entrySet();
        Iterator iterator = set.iterator();
        int i = 0;
        while (iterator.hasNext()) { // for every node
            i++;
            SensorNode node = (SensorNode) ((Map.Entry) iterator.next()).getValue();
            //node.setMinimumSpanNode(null);
        }

        // This one sets only the parent's/children of the paths we are interested in.
        /*for (i = 0; i < pathfinding.size() - 1; i++) {
            pathfinding.get(i).childSensorNode = pathfinding.get(i + 1);
            pathfinding.get(i + 1).parentSensorNode = pathfinding.get(i);
        }*/
        // The next section demonstrates that our program is correct by visualizing it through a JPanel.
        // Create the frame
        JFrame frame = new JFrame("Visual");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Retrieve the scale of the drawing coordinates from user input
        System.out.println("What is the scale you wish to size your visualization?");
        double scale = input.nextDouble();

        int width = (int) (x * scale), height = (int) (y * scale); // get the width and height of the map and apply the scale
        Visual panel = new Visual(sensorNodes.values().toArray(new SensorNode[sensorNodes.size()]), results, x, y, scale); // initalize the custom drawing panel with our program's data
        panel.setPreferredSize(new Dimension(width, height)); // set the size of our drawing panel
        JScrollPane scrollPane = new JScrollPane(panel); // create the scroll pane to contain the drawing panel
        frame.setContentPane(scrollPane); // set the conten/tpane to the scroll pane
        frame.setSize(640, 448); // set the size of the frame
        frame.setVisible(true); // show the frame
        frame.setAlwaysOnTop(true);
        frame.setAlwaysOnTop(false);
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
        p = 5;
        System.out.println("And, how many data packets can each data generator have?");
        //q = input.nextInt();
        q = 100;
        System.out.println("Lastly, what is the storage capacity of the remaining storage nodes? (in packets)");
        //m = input.nextInt();
        m = 100;

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
    }
}