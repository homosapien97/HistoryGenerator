package world;

import geometry.PolygonUtils;
import geometry.RegularPolygon;
import javafx.geometry.Point2D;
import javafx.scene.shape.Polygon;
import utilities.Centralizable;

import java.util.Iterator;

/**
 * Created by homosapien97 on 4/11/17.
 */
public class SphereOfInfluence /*extends Polygon*/ implements Centralizable {
    Point2D center;
    double radius;

    public boolean contains(Point2D point) {
        return center.distance(point) < radius;
    }

    @Override
    public Point2D getCenter() {
        return center;
    }

    @Override
    public double getRadius() {
        return radius;
    }

    public SphereOfInfluence(Point2D center, double radius) {
        this.center = center;
        this.radius = radius;
//        double fx = center.getX();
//        double fy = center.getY();
//        double s = Math.sqrt(2 * radius * radius - 2 * radius * radius * Math.cos(Math.PI * 2 / 20));
//        Polygon circle = (new RegularPolygon(new Point2D(fx - s / 2, fy - Math.sqrt(radius * radius - s * s / 4)), 20, s)).getPolygon();
//        this.getPoints().addAll(circle.getPoints());
    }

//    public boolean stretch(Polygon stretcher, double scale) {
//        Polygon intersection = PolygonUtils.intersection(this, stretcher);
//        if(intersection == null) {
//            return false;
//        }
//        Point2D iCenter = PolygonUtils.center(intersection);
//        Polygon scaledIntersection = new Polygon();
//        for(Iterator<Double> iterator = intersection.getPoints().iterator(); iterator.hasNext();) {
//            scaledIntersection.getPoints().addAll(iterator.next() * scale, iterator.next() * scale);
//        }
//        Point2D sCenter = PolygonUtils.center(scaledIntersection);
//        double dx = sCenter.getX() - iCenter.getX();
//        double dy = sCenter.getY() - iCenter.getY();
//        scaledIntersection.setTranslateX(dx);
//        scaledIntersection.setTranslateY(dy);
//        Polygon union = PolygonUtils.union(this, scaledIntersection);
//        this.getPoints().clear();
//        this.getPoints().addAll(union.getPoints());
//        return true;
//    }
}
