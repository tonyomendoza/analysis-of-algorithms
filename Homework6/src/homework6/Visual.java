package homework6;

import java.awt.*;
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

    LinkedHashMap<Point, SensorNode> nodes;
    int width, height;
    double scale;

    // constructor
    public Visual(LinkedHashMap<Point, SensorNode> nodes, int width, int height, double scale) {
        super();
        this.nodes = nodes;
        this.width = width;
        this.height = height;
        this.scale = scale;
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

        Set set = nodes.entrySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) { // for every node
            Map.Entry entry = (Map.Entry) iterator.next();
            SensorNode node = (SensorNode) entry.getValue();
            // draw and fill the point
            g.drawOval(
                    (int) (node.getPoint().x * scale) - (int) (scale / 2),
                    (int) (node.getPoint().y * scale) - (int) (scale / 2),
                    (int) (scale), (int) (scale));

            if (node.isSource()) {
                g.setColor(Color.decode("#3A5EFF"));
            } else if (node.isDataGenerator()) {
                g.setColor(Color.decode("#B5D1FF"));
            } else if (node.isTarget()) {
                g.setColor(Color.decode("#45CC41"));
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
                Edge edge = edges[e];
                if (edge.destination.parentSensorNode == node || edge.destination.childSensorNode == node) {
                    g.setColor(Color.red);
                } else {
                    g.setColor(Color.gray);
                }
                g.drawLine(
                        (int) (node.getPoint().x * scale),
                        (int) (node.getPoint().y * scale),
                        (int) (edge.getDestination().getPoint().x * scale),
                        (int) (edge.getDestination().getPoint().y * scale));
            }

            // print statistical data about the node
            g.setColor(Color.black);
            g.drawChars(node.toString().toCharArray(), 0, node.toString().length(),
                    (int) (node.getPoint().x * scale) + (int) (scale / 2),
                    (int) (node.getPoint().y * scale) + (int) (scale / 2));
        }
    }
}
