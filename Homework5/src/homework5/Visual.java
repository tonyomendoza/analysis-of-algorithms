package homework5;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.*;

/**
 *
 * @author Tony Mendoza and Kristofer Giddens and Joseantonio Maciel
 */
public class Visual extends JPanel {

    SensorNode[] nodes;
    ArrayList<Edge> results;
    int width, height;
    double scale;

    // constructor
    public Visual(SensorNode[] nodes, ArrayList<Edge> results, int width, int height, double scale) {
        super();
        this.nodes = nodes;
        this.width = width;
        this.height = height;
        this.scale = scale;
        this.results = results;
    }

    // paint component
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); //  base method call

        // draw and fill the map-area of the nodes (ie x and y variables)
        g.drawRect(0, 0, (int) (width * scale), (int) (height * scale));
        g.setColor(Color.white);
        g.fillRect(0, 0, (int) (width * scale), (int) (height * scale));

        // draw a grid
        g.setColor(Color.LIGHT_GRAY);
        for (int x = (int) (10 * scale); x < (int) (width * scale); x += (int) (10 * scale)) {
            g.drawLine(x, 0, x, (int) (height * scale));
        }

        for (int y = (int) (10 * scale); y < (int) (width * scale); y += (int) (10 * scale)) {
            g.drawLine(0, y, (int) (width * scale), y);
        }

        for(int i = 0; i < nodes.length; i ++){
            SensorNode node = nodes[i];
            // draw and fill the point
            g.drawOval(
                    (int) (node.getPoint().x * scale) - (int) (scale / 2),
                    (int) (node.getPoint().y * scale) - (int) (scale / 2),
                    (int) (scale), (int) (scale));

            if (node.isDataGenerator()) {
                g.setColor(Color.decode("#B5D1FF"));
            } else {
                g.setColor(Color.decode("#9AE298"));
            }
            g.fillOval(
                    (int) (node.getPoint().x * scale) - (int) (scale / 2),
                    (int) (node.getPoint().y * scale) - (int) (scale / 2),
                    (int) (scale), (int) (scale));

            // draw lines to the edges
            Edge[] edges = node.getConnections();
            for (int e = 0; e < edges.length; e++) {
                SensorNode edge = edges[e].getDestination();
                    g.setColor(Color.gray);
                if (results.contains(edges[e])) 
                    g.setColor(Color.red);
                g.drawLine(
                        (int) (node.getPoint().x * scale),
                        (int) (node.getPoint().y * scale),
                        (int) (edge.getPoint().x * scale),
                        (int) (edge.getPoint().y * scale));
                
            }

            // print statistical data about the node
            g.setColor(Color.black);
            g.drawChars(node.toString().toCharArray(), 0, node.toString().length(),
                    (int) (node.getPoint().x * scale) + (int) (scale / 2),
                    (int) (node.getPoint().y * scale) + (int) (scale / 2));
        }
    }
}
