package homework4;

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
import javax.swing.JFrame;
import javax.swing.JScrollPane;

/**
 *
 * @author Tony Mendoza and Kristofer Giddens
 */
public class Homework4 {

    static Scanner input;
    static LinkedHashMap<Point, SensorNode> sensorNodes;
    static Random random;
    static int x, y, N, Tr, p, q, m, sourceIndex;
    static SensorNode source;
    static SensorNode target;
    static ArrayList<SensorNode> pathfinding;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        input = new Scanner(System.in);

        // Initializes the SensorNodes.
        // Almost everything in the last homework is in this class with the exception of
        //  the new prompts asking for DG amount (p), DG capacity (q), and Storage capacity (m)
        Initialize();

        // The homework really starts here, where we implement Dijkstra's Algorithm
        PriorityQueue<SensorNode> queue = new PriorityQueue<>(); // create a PQ
        // While we add every node to the PQ, we check if they are all connected at the same time.
        Set set = sensorNodes.entrySet();
        Iterator iterator = set.iterator();
        int i = 0;
        while (iterator.hasNext()) { // for every node
            Map.Entry entry = (Map.Entry) iterator.next();
            SensorNode node = (SensorNode) entry.getValue();
            // List the nodes to the user, so they can refer to it when setting the source and target nodes.
            if (node.isDataGenerator()) {
                System.out.println("\tNode @" + node.getPoint() + " is a data generator. Refer to it as index: " + node.getIndex());
            } else {
                System.out.println("\tNode @" + node.getPoint() + " is a storage generator. Refer to it as index: " + node.getIndex());
            }

            // If the node has not been discovered, then it is not connected and we must quit the program
            if (!node.discovered) {
                System.out.println("This node is not connected. Please restart the program.");
                System.exit(0);
            }
            queue.add(node); // this node is connected, so add it to the PQ
            i++;
            // you can set its cost to Integer.MAX_VALUE for INFINITY, however, our node already does that when initializing.
        }

        // If there are not enough storage nodes to store hte packets, then we must quit the program..
        if (p * q > (N - p) * m) {
            System.out.println("There is not enough storage nodes to store the packets. Please restart the program.");
            System.exit(0);
        }

        // The program succeeded. So, set the source index where we will generate the packets.
        do {
            System.out.println("Which node would you like to set as the 'source' (lack of a better term).");
            sourceIndex = input.nextInt();
        } while (sourceIndex >= N || sourceIndex < 0 || sourceIndex >= p);

        // Then set the target, the end node we expect to store the packets.
        int targetIndex = 0;
        do {
            System.out.println("Which node would you like to set as the 'target' (lack of a better term).");

            targetIndex = input.nextInt();
        } while (targetIndex >= N || targetIndex < 0 || targetIndex == sourceIndex || targetIndex < p);

        // Actually, this is where we set. The last parts is where we identify what nodes we want to mark as START and FINAL
        target = null;
        set = sensorNodes.entrySet();
        iterator = set.iterator();
        i = 0;
        while (iterator.hasNext()) { // for every node
            SensorNode node = (SensorNode) ((Map.Entry) iterator.next()).getValue();
            if (i == sourceIndex) { // set if source
                source = node;
                source.setAsSource(true);
            } else if (i == targetIndex) { // set if target
                target = node;
                target.setAsTarget(true);
            }
            i++;
        }

        source.setCost(0); // set source to 0

        // ArrayList<SensorNode> result = new ArrayList<>(); // not used in our program, but we would if we want to quickly access results sorted by cost
        while (!queue.isEmpty()) {
            SensorNode node = queue.poll();
            //result.add(node); // not used

            double recievingCost = (100 * 3200); // the recieving cost is the same for all nodes

            for (i = 0; i < node.getConnections().length; i++) { // for every edge connected
                SensorNode vertex = node.getConnections()[i];

                // Calculate what it will cost to send from the node to this vertex.
                // Eamp = Eelec, so multiplying Eelec by k (3200 bits) + Eamp by k by distance^2, will give us this formula down here
                double transmissionCost = (100 * 3200) + (100 * 3200 * ((Math.pow(node.point.x - vertex.point.x, 2) + Math.pow(node.point.y - vertex.point.y, 2))));

                // If the cost is cheaper, then set the cheaper cost
                if (node.cost + recievingCost + transmissionCost < vertex.cost) {
                    vertex.setCost(node.cost + recievingCost + transmissionCost);
                    vertex.setParentSensorNode(node); // set parent
                    node.setChildSensorNode(vertex); // set child

                    // Inefficient way to sort our priority queue. But we didn't really want to make a prioirty queue. We did that in Data Structures.
                    queue.remove(vertex); // remove O(log n)?
                    queue.offer(vertex); // reinsert O(log n)
                    // SORTING complete
                }
            }
        }

        // This code is used for finding the actual cost-effective path to the node
        pathfinding = new ArrayList<>();  // this structure holds the above mentioned apth
        SensorNode t = target;
        while (t != source) {
            pathfinding.add(t);
            t = t.parentSensorNode; // follow it parent to parent, back to source
        }
        pathfinding.add(t);

        Collections.sort(pathfinding); // sort each edge in the found paths by their cost

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
            node.setParentSensorNode(null);
            node.setChildSensorNode(null);
        }

        // This one sets only the parent's/children of the paths we are interested in.
        for (i = 0; i < pathfinding.size() - 1; i++) {
            pathfinding.get(i).childSensorNode = pathfinding.get(i + 1);
            pathfinding.get(i + 1).parentSensorNode = pathfinding.get(i);
        }

        // The next section demonstrates that our program is correct by visualizing it through a JPanel.
        // Create the frame
        JFrame frame = new JFrame("Visual");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Retrieve the scale of the drawing coordinates from user input
        System.out.println("What is the scale you wish to size your visualization?");
        double scale = input.nextDouble();

        int width = (int) (x * scale), height = (int) (y * scale); // get the width and height of the map and apply the scale
        Visual panel = new Visual(sensorNodes, x, y, scale); // initalize the custom drawing panel with our program's data
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
        x = input.nextInt();
        System.out.println("What is length of the sensor network?");
        y = input.nextInt();
        System.out.println("Next, how many sensor nodes are there?");
        N = input.nextInt();
        sensorNodes = new LinkedHashMap<>();
        System.out.println("And, what is their transmission range?");
        Tr = input.nextInt();
        p = 0;
        do {
            System.out.println("Next, how many of the sensor nodes are data generators?");
            p = input.nextInt();
        } while (p >= N || p < 0);
        System.out.println("And, how many data packets can each data generator have?");
        q = input.nextInt();
        System.out.println("Lastly, what is the storage capacity of the remaining storage nodes? (in bytes)");
        m = input.nextInt();

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

            // simulate a node scanning for a transmission range
            // could perhaps be done more efficently with listeners, but, didn't think of it until after
            for (int nY = 0 - Tr; nY <= Tr; nY++) { // check every x point within Tr
                for (int nX = 0 - Tr; nX <= Tr; nX++) { // check every y point within Tr
                    if (nX != 0 && nY != 0) { // ignore the center point (the current node's point)
                        Point point = new Point(current.getPoint().x + nX, current.getPoint().y + nY); // get the point we are checking
                        if (sensorNodes.containsKey(point)) { // if that point exists in our sensor node hashmap
                            SensorNode node = sensorNodes.get(point); // then it is the sensor node we want
                            current.connectEdge(node); // connect the current node to that found node

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
