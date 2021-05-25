import com.vividsolutions.jts.awt.ShapeWriter;
import com.vividsolutions.jts.geom.Geometry;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Behnam Khazael on 2/22/2021.
 * Utilities for visualizing the simulation area.
 * @author Behnam Khazael
 * @version 0.1
 */
public class DrawUtility extends JPanel {
    private static final int PREF_W = 1000;
    private static final int PREF_H = 500;
    private static final java.util.List<Shape> shapes = new ArrayList<>();
    private HashMap<Shape, Color> shapesColor = new HashMap<>();
    private HashMap<Geometry, Shape> shapesGeometry = new HashMap<>();

    public DrawUtility() {
        setBackground(Color.white);
    }

    public synchronized void addShape(Shape shape) {
        shapes.add(shape);
        repaint();
    }

    public synchronized void addShape(Geometry geometryShape, Color color) {

        ShapeWriter sw = new ShapeWriter();
        Shape shape = sw.toShape(geometryShape);
        shapes.add(shape);
        shapesColor.put(shape, color);
        shapesGeometry.put(geometryShape, shape);
        repaint();
    }

    public synchronized void clearAll() {
        shapes.clear();
        repaint();
    }

    public synchronized void removeShape(Geometry geometryShape) {
        Shape shape = shapesGeometry.get(geometryShape);
        shapes.remove(shape);
        shapesColor.remove(shape);
        repaint();
    }

    @Override // make it bigger
    public Dimension getPreferredSize() {
        if (isPreferredSizeSet()) {
            return super.getPreferredSize();
        }
        return new Dimension(PREF_W, PREF_H);
    }

    @Override
    protected synchronized void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        for (Shape shape : shapes) {
            g2.setColor(shapesColor.get(shape));
            g2.draw(shape);
        }

    }

    public static void showGui(DrawUtility drawChit) {
        JFrame frame = new JFrame("Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(drawChit);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }
}
