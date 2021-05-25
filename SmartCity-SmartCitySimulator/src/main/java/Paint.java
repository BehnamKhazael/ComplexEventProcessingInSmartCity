import com.vividsolutions.jts.awt.ShapeWriter;
import com.vividsolutions.jts.geom.Geometry;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Behnam Khazael on 2/22/2021.
 * @author Behnam Khazael
 * @version 0.1
 */
public class Paint extends JPanel {
    Geometry geometry;

    Paint(Geometry geo) {
        geometry = geo;
    }

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        ShapeWriter sw = new ShapeWriter();
        Shape polyShape = sw.toShape(geometry);
        g2d.setColor(Color.blue);
        g2d.setBackground(Color.BLUE);
        g2d.draw(polyShape);
        g2d.setColor(new Color(1f, 0f, 0f, .5f));
        g2d.fill(polyShape);
    }
}
