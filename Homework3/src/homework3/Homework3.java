package homework3;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.awt.*;
import java.util.LinkedHashMap;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

/**
 *
 * @author Tony Mendoza and Kristofer Giddens
 */
public class Homework3 {

    static Scanner input;
    static LinkedHashMap<Point, SensorNode> sensorNodes;
    static Random random;
    // Trees<>
    static ArrayList<ArrayList<ArrayList<SensorNode>>> bfsTree;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        input = new Scanner(System.in);

        // Set the width and height of the map, the amount of sensor nodes, and the transmisison range
        System.out.println("First, we need to determine the size of the sensor network.");
        System.out.println("What is width of the sensor network?");
        int x = input.nextInt();
        System.out.println("What is length of the sensor network?");
        int y = input.nextInt();
        System.out.println("Next, how many sensor nodes are there?");
        int N = input.nextInt();
        sensorNodes = new LinkedHashMap<>();
        System.out.println("Lastly, what is their transmission range?");
        int Tr = input.nextInt();

        // Randomly generate every SensorNode.
        random = new Random();
        SensorNode source = null; // however, set the source node randomly here
        int randomSourceIndex = (int) (random.nextDouble() * N); // use thiss to rnadomly set source
        // create nodes randomly
        for (int n = 0; n < N; n++) {
            Point p = new Point((int) (random.nextDouble() * x), (int) (random.nextDouble() * y)); // assign a random point
            if (sensorNodes.containsKey(p) && N <= (x * y)) { // if the point already exists and there is enough points to go around, then try to randomize again
                n--;
                continue;
            }
            SensorNode node = new SensorNode(p.x, p.y); // create the snesor node
            sensorNodes.put(new Point(p.x, p.y), node); // put in the point and the sensor node in the hashmap for comparision and retrieval later
            if (randomSourceIndex == n) // here, we will set node @ random to be our source node
            {
                source = node;
            }
        }

        // set discovered = false for all nodes. Already done by default
        bfsTree = new ArrayList<>(); // create the bfsTree list to output in the console
        bfsTree.add(new ArrayList<>()); // add a tree to the trees list
        bfsTree.get(bfsTree.size() - 1).add(new ArrayList<>()); //then add a layer to the tree
        bfsTree.get(bfsTree.size() - 1).get(0).add(source); // then add the source node to the layer

        ArrayDeque<SensorNode> breadthQueue = new ArrayDeque<>(); // used for breadth search
        breadthQueue.offer(source); // offer the source to the queue
        source.discovered = true; //source is discovered
        int i = 0; // used for iterating through the node array for other possible node trees
        SensorNode current; // used for breadth search
        int layerCounter = 1; // used for comparing the layer of the bfsTree in breadth search

        // breadth search algorith implementation
        while (!breadthQueue.isEmpty()) { // while there is something in the queue
            current = breadthQueue.poll(); // poll the current node for analyzing

            current.tree = bfsTree.size() - 1; // set the tree of the current node to match appropriately

            // if the layer we are on does not match the node's stored layer
            if (layerCounter != current.layer) {
                bfsTree.get(bfsTree.size() - 1).add(new ArrayList<>()); // add a new layer in the tree of the bfstree list
                layerCounter++; // incrememnt the layer we have
            }

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
                                node.layer = current.layer + 1; // then update its layer to the current layer + 1
                                breadthQueue.offer(node); // offer it to the breadth queue for analyzing its edges
                                bfsTree.get(bfsTree.size() - 1).get(layerCounter - 1).add(node); // add a node to the layer of the bfs tree
                                node.discovered = true; // set node's discovery to true
                            }
                        }
                    }
                }
            }

            // When we reach the end of a bfs tree, we have to see if there are any unconnected nodes that have not been given a tree of their own.
            if (breadthQueue.isEmpty()) { // if there are no more nodes to check
                SensorNode[] nodes = sensorNodes.values().toArray(new SensorNode[sensorNodes.size()]); // get the nodes from the hashmap
                for (i = i + 1; i < N; i++) { // iterate through every node, starting where we left off last time (by using i for iterating)
                    if (!nodes[i].discovered) { // if the node hasn't been discovered, 
                        source = nodes[i]; // then it is the node we want. Set it as the source node.
                        source.discovered = true; // set the node's discovery to true
                        breadthQueue.offer(source); // offer it to the breadth queue for analyzing
                        bfsTree.add(new ArrayList<>()); // add a new bfs tree to the list
                        bfsTree.get(bfsTree.size() - 1).add(new ArrayList<>()); // add a new layer to that tree
                        bfsTree.get(bfsTree.size() - 1).get(0).add(source); // add the new source to the bfs tree
                        layerCounter = 1; // reset the layer counter.
                        break;
                    }
                }
            }
        }

        // Used for printing our bfsTree.
        for (int t = 0; t < bfsTree.size(); t++) {
            System.out.println("bfsTree: Tree: " + t);
            for (int l = 0; l < bfsTree.get(t).size(); l++) {
                System.out.println("\tbfsTree: Layer: " + l);
                for (int n = 0; n < bfsTree.get(t).get(l).size(); n++) {
                    System.out.println("\t\tbfsTree: Node (" + bfsTree.get(t).get(l).get(n).getPoint() + ")");
                }
            }
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
        frame.setContentPane(scrollPane); // set the contentpane to the scroll pane
        frame.setSize(640, 448); // set the size of the frame
        frame.setVisible(true); // show the frame
        frame.setAlwaysOnTop(true); 
        frame.setAlwaysOnTop(false);
    }
}
