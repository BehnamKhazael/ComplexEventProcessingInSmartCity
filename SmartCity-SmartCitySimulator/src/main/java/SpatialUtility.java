import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.util.GeometricShapeFactory;

import java.util.ArrayList;

/**
 * Created by Behnam Khazael on 2/22/2021.
 * Utilities for spatial data processing.
 * @author Behnam Khazael
 * @version 0.1
 */
public class SpatialUtility {
    /**
     * @return
     */
    public static LineString createRandomLineString(int n) {
        //(Math.random() * range) + min;
        double latitude = (Math.random() * 180.0) - 90.0;
        double longitude = (Math.random() * 360.0) - 180.0;
        GeometryFactory geometryFactory = new GeometryFactory();
    /* Longitude (= x coord) first ! */
        ArrayList<Coordinate> points = new ArrayList<Coordinate>();
        points.add(new Coordinate(longitude, latitude));
        for (int i = 1; i < n; i++) {
            //(Math.random() * range) + min;
            double deltaX = (Math.random() * 10.0) - 5.0;
            double deltaY = (Math.random() * 10.0) - 5.0;
            longitude += deltaX;
            latitude += deltaY;
            points.add(new Coordinate(longitude, latitude));
        }
        LineString line = geometryFactory.createLineString((Coordinate[]) points.toArray(new Coordinate[]{}));
        return line;
    }


    public static Polygon createRandomPolygon(int n) {
        //(Math.random() * range) + min;
        double latitude = (Math.random() * 180.0) - 90.0;
        double longitude = (Math.random() * 360.0) - 180.0;
        GeometryFactory geometryFactory = new GeometryFactory();
    /* Longitude (= x coord) first ! */
        Polygon poly = null;
        boolean valid = false;
        while (!valid) {
            ArrayList<Coordinate> points = new ArrayList<Coordinate>();
            points.add(new Coordinate(longitude, latitude));
            double lon = longitude;
            double lat = latitude;
            for (int i = 1; i < n; i++) {
                //(Math.random() * range) + min;
                double deltaX = (Math.random() * 10.0) - 5.0;
                double deltaY = (Math.random() * 10.0) - 5.0;
                lon += deltaX;
                lat += deltaY;
                points.add(new Coordinate(lon, lat));
            }
            points.add(new Coordinate(longitude, latitude));
            poly = geometryFactory.createPolygon((Coordinate[]) points.toArray(new Coordinate[]{}));
            valid = poly.isValid();
        }
        return poly;
    }

    public static Point createRandomPoint() {
        //(Math.random() * range) + min;
        double latitude = (Math.random() * 1000.0) - 0.0;
        double longitude = (Math.random() * 1000.0) - 0.0;
        GeometryFactory geometryFactory = new GeometryFactory();
    /* Longitude (= x coord) first ! */
        com.vividsolutions.jts.geom.Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));

        return point;
    }

    public static Point createRandomPoint(double width, double height) {
        double latitude = (Math.random() * width) - 0.0;
        double longitude = (Math.random() * height) - 0.0;
        GeometryFactory geometryFactory = new GeometryFactory();
        com.vividsolutions.jts.geom.Point point = geometryFactory.createPoint(new Coordinate(latitude, longitude));
        return point;
    }

    public static Point createRandomPointBetween(double maxWidth, double maxHeight, double minWidth, double minHeight) {
        double latitude = Math.random() * ((maxWidth - minWidth) + 1) + minWidth;
        double longitude = Math.random() * ((maxHeight - minHeight) + 1) + minHeight;
        GeometryFactory geometryFactory = new GeometryFactory();
        com.vividsolutions.jts.geom.Point point = geometryFactory.createPoint(new Coordinate(latitude, longitude));
        return point;
    }

    public static Geometry createCircle(Point loc, final double RADIUS) {
        GeometricShapeFactory shapeFactory = new GeometricShapeFactory();
        shapeFactory.setNumPoints(32);
        shapeFactory.setCentre(loc.getCoordinate());
        shapeFactory.setSize(RADIUS * 2);
        return shapeFactory.createCircle();
    }

    public static Geometry createRectangle(Point loc1, Point loc2, Point loc3, Point loc4) {
        GeometricShapeFactory shapeFactory = new GeometricShapeFactory();
        Polygon polygon = shapeFactory.createRectangle();

        shapeFactory.createRectangle();
        return shapeFactory.createCircle();
    }
}
