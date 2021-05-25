import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.util.GeometricShapeFactory;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Behnam Khazael on 2/23/2021.
 * To test simulation events placement.
 * @author Behnam Khazael
 * @version 0.1
 */
public class Rectangle {
    public static void main(String[] args) {
        DrawUtility drawChit = new DrawUtility();


        Polygon p1 = createPolygon(new Coordinate(250, 250), 500, 500);
        Polygon p2 = createPolygon(new Coordinate(250, 750), 500, 500);
        Polygon p3 = createPolygon(new Coordinate(750, 250), 500, 500);
        Polygon p4 = createPolygon(new Coordinate(750, 750), 500, 500);
        Geometry point = SpatialUtility.createRandomPointBetween(500, 500, 0, 0);
        Geometry circle = SpatialUtility.createCircle((Point) point, 200);
        Geometry point2 = SpatialUtility.createRandomPointBetween(500, 1000, 0, 500);
        Geometry circle2 = SpatialUtility.createCircle((Point) point2, 200);
        Geometry point3 = SpatialUtility.createRandomPointBetween(1000, 500, 500, 0);
        Geometry circle3 = SpatialUtility.createCircle((Point) point3, 200);
        Geometry point4 = SpatialUtility.createRandomPointBetween(1000, 1000, 500, 500);
        Geometry circle4 = SpatialUtility.createCircle((Point) point4, 200);


        for (int j = 0; j < 15; j++) {
            ArrayList<Geometry> arrayList = new ArrayList<>();
            int i = 0;
            while (i < 4) {

                ComplexEvent complexEvent = new ComplexEvent();
                complexEvent.setName("ce" + (j + 1 + i));
                complexEvent.setTime(System.currentTimeMillis());
                switch (i) {
                    case 0:
                        Geometry geometry = SpatialUtility.createCircle(SpatialUtility.createRandomPointBetween(300, 300, 200, 200), 200);
                        i++;
                        arrayList.add(geometry);
                        complexEvent.setTargetArea(geometry);
                        System.out.println(complexEvent.getTargetArea().toText());
                        circle = geometry;
                        break;
                    case 1:
                        geometry = SpatialUtility.createCircle(SpatialUtility.createRandomPointBetween(800, 300, 700, 200), 200);
                        boolean ch1 = false;
                        for (Geometry g :
                                arrayList) {
                            if (!check(g, geometry))
                                break;
                            ch1 = check(g, geometry);
                        }
                        if (ch1) {
                            i++;
                            arrayList.add(geometry);
                            complexEvent.setTargetArea(geometry);
                            System.out.println(complexEvent.getTargetArea().toText());
                            circle2 = geometry;
                        }
                        break;
                    case 2:
                        geometry = SpatialUtility.createCircle(SpatialUtility.createRandomPointBetween(300, 800, 200, 700), 200);
                        boolean ch2 = false;
                        for (Geometry g :
                                arrayList) {
                            if (!check(g, geometry))
                                break;
                            ch2 = check(g, geometry);
                        }
                        if (ch2) {
                            i++;
                            arrayList.add(geometry);
                            complexEvent.setTargetArea(geometry);
                            System.out.println(complexEvent.getTargetArea().toText());
                            circle3 = geometry;
                        }

                        break;
                    case 3:
                        geometry = SpatialUtility.createCircle(SpatialUtility.createRandomPointBetween(800, 800, 700, 700), 200);
                        boolean ch3 = false;
                        for (Geometry g :
                                arrayList) {
                            if (!check(g, geometry))
                                break;
                            ch3 = check(g, geometry);
                        }
                        if (ch3) {
                            i++;
                            arrayList.add(geometry);
                            complexEvent.setTargetArea(geometry);
                            System.out.println(complexEvent.getTargetArea().toText());
                            circle4 = geometry;
                        }

                        break;
                }

            }
        }


        drawChit.addShape(p1, Color.BLACK);
        drawChit.addShape(p2, Color.RED);
        drawChit.addShape(p3, Color.GREEN);
        drawChit.addShape(p4, Color.YELLOW);
        drawChit.addShape(circle, Color.BLACK);
        drawChit.addShape(circle2, Color.ORANGE);
        drawChit.addShape(circle3, Color.RED);
        drawChit.addShape(circle4, Color.BLUE);


        DrawUtility.showGui(drawChit);
    }

    private static Polygon createPolygon(Coordinate center, double width, double height) {
        GeometricShapeFactory shapeFactory = new GeometricShapeFactory();
        shapeFactory.setNumPoints(4);
        shapeFactory.setCentre(center);
        shapeFactory.setWidth(width);
        shapeFactory.setHeight(height);
        return shapeFactory.createRectangle();
    }


    private static boolean check(Geometry a, Geometry b) {
        return !(a.covers(b) || a.overlaps(b) || a.intersects(b) || a.crosses(b));
    }


}
