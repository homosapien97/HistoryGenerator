package world;

import geometry.GridMap;
import geometry.PolygonUtils;
import javafx.geometry.Point2D;
import javafx.scene.shape.Polygon;
import utilities.Centralizable;

/**
 * Created by homosapien97 on 4/16/17.
 */
public class Mountain extends Polygon implements Centralizable  {
    public static GridMap<Mountain> mountainMap = new GridMap<>(100.0);
    Point2D center;
    double radius;
    //TODO: add these to grid, check for whether a circle around the midpoint of two cities contains a mountain before doing expensive intersection
    public Mountain(Polygon polygon) {
        this.getPoints().addAll(polygon.getPoints());
        center = PolygonUtils.center(this);
        radius = PolygonUtils.maxDistance(this, center);
        mountainMap.add(this);
//        System.out.println("Mountain Map Size " + mountainMap.size());
    }
    @Override
    public Point2D getCenter() {
        return center;
    }
    @Override
    public double getRadius() {
        return radius;
    }
}
