package world;

import geometry.*;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.shape.*;

import java.util.Random;

/**
 * Created by homosapien97 on 4/10/17.
 */
public class City extends Polygon {
    public static final double DEFAULT_RADIUS = 20.0;
    public Watershed watershed;
    private Random rand;
    public double radius;
    public Point2D center;

    public City(Watershed watershed, Random rand) {
//        this(watershed, PolygonUtils.approxRandomInteriorPoint(watershed, 2 * DEFAULT_RADIUS, rand), DEFAULT_RADIUS, rand);
        this.watershed = watershed;
        watershed.cities.add(this);
        this.rand = rand;
        this.radius = DEFAULT_RADIUS;
//        Polygon subtraction = null;
        Polygon circle;
        Bounds watershedBounds = watershed.getBoundsInParent();
        if(watershedBounds.getWidth() < 2 * DEFAULT_RADIUS && watershedBounds.getHeight() < 2 * DEFAULT_RADIUS) {
            this.center = PolygonUtils.approxRandomInteriorPoint(watershed, 2 * DEFAULT_RADIUS, rand);
            double fx = center.getX();
            double fy = center.getY();
            double s = Math.sqrt(2 * radius * radius - 2 * radius * radius * Math.cos(Math.PI * 2 / 20));
            circle = (new RegularPolygon(new Point2D(fx - s / 2, fy - Math.sqrt(radius * radius - s * s / 4)), 20, s)).getPolygon();
//            circle = PolygonUtils.intersection(watershed, circle);
            if (circle == null || circle.getPoints().size() == 0) {
                throw new IllegalArgumentException("City cannot exist with this radius in this location");
            }
        } else {
            do {
                this.center = PolygonUtils.approxRandomInteriorPoint(watershed, 2 * DEFAULT_RADIUS, rand);
                double fx = center.getX();
                double fy = center.getY();
                double s = Math.sqrt(2 * radius * radius - 2 * radius * radius * Math.cos(Math.PI * 2 / 20));
                circle = (new RegularPolygon(new Point2D(fx - s / 2, fy - Math.sqrt(radius * radius - s * s / 4)), 20, s)).getPolygon();
//            circle = PolygonUtils.intersection(watershed, circle);
//                subtraction = PolygonUtils.subtract(circle, watershed);
                if (circle == null || circle.getPoints().size() == 0) {
                    throw new IllegalArgumentException("City cannot exist with this radius in this location");
                }
            } while (!PolygonUtils.containsEveryVertex(watershed, circle));
        }
        this.getPoints().addAll(circle.getPoints());
    }

    public City(Watershed watershed, Point2D center, double radius, Random rand) {
        this.watershed = watershed;
        watershed.cities.add(this);
        this.rand = rand;
        this.radius = radius;
        this.center = center;
        double fx = center.getX();
        double fy = center.getY();
        double s = Math.sqrt(2 * radius * radius - 2 * radius * radius * Math.cos(Math.PI * 2 / 20));
        Polygon circle = (new RegularPolygon(new Point2D(fx - s / 2, fy - Math.sqrt(radius * radius - s * s / 4)), 20, s)).getPolygon();
//        circle = PolygonUtils.intersection(watershed, circle);
//        if(circle == null || circle.getPoints().size() == 0) {
//            throw new IllegalArgumentException("City cannot exist with this radius in this location");
//        }
        this.getPoints().addAll(circle.getPoints());
    }

    public static City merge(City a, City b) {
        if (a.watershed == b.watershed
                /*a.center.distance(b.center) <= a.radius + b.radius*/
                && (PolygonUtils.containsAVertex(a, b) || PolygonUtils.containsAVertex(b, a))) {
            double ar2 = a.radius * a.radius;
            double br2 = b.radius * b.radius;
            double cx;
            double cy;
//            double cx = (a.center.getX() * ar2 + b.center.getX() * br2) / (ar2 + br2);
//            double cy = (a.center.getY() * ar2 + b.center.getY() * br2) / (ar2 + br2);
            if(ar2 > br2) {
                cx = a.center.getX();
                cy = b.center.getY();
            } else {
                cx = a.center.getX();
                cy = b.center.getY();
            }
            try {
                a.watershed.cities.remove(a);
                b.watershed.cities.remove(b);
                City ret = new City(a.watershed, new Point2D(cx, cy), Math.sqrt((ar2 + br2) / 2), a.rand);
                System.out.println("Cities successfully merged");
                return ret;
            } catch (IllegalArgumentException e) {
                System.out.println("Cities should have merged but didn't");
                return null;
            }
        } else {
            System.out.println("Cities did not fit merge criteria");
            return null;
        }
    }
}