package world;

import ui.Selectable;
import ui.Selection;
import utilities.Centralizable;
import utilities.Describable;
import geometry.*;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.shape.*;

import java.util.HashSet;
import java.util.Random;

/**
 * Created by homosapien97 on 4/10/17.
 */
public class City extends Polygon implements Describable, Centralizable, Selectable, Influencer, Influencable {
    private static final double DEFAULT_RADIUS = 20.0;
    private static final double DEFAULT_GRID_SIZE = 100.0;
    private static final double INFLUENCE_RADIUS_FACTOR = 15.0;
    private static final double INFLUENCE_FACTOR = 1.0/16.0;
    private static final double DRIFT_FACTOR = 2.0;
    private static final GridMap cityMap = new GridMap(DEFAULT_GRID_SIZE);
    public Watershed watershed;
    private Random rand;
    public double radius;
    public Point2D center;
    public SphereOfInfluence sphere;
    public String name;
    public Culture culture;
    private Culture temporaryCulture;

    public void driftCulture() {
        temporaryCulture.drift(DRIFT_FACTOR);
    }

    @Override
    public boolean finalizeInfluence() {
        if(culture == temporaryCulture) return false;
        culture = temporaryCulture;
        return true;
    }

    @Override
    public boolean influenceBy(Influencer o) {
        if(o instanceof City) {
            City influencer = (City) o;
            temporaryCulture.driftTo(culture, influencer.culture, 1 - 1 / (INFLUENCE_FACTOR * radius * 1 / center.distance(influencer.center) + 1));
            return true;
        } else if(o instanceof Watershed) {
            Watershed influencer = (Watershed) o;
            temporaryCulture.driftTo(culture, influencer.culture, 1 - 1 / (INFLUENCE_FACTOR * radius * 1/ center.distance(influencer.endPoint) + 1));
            return true;
        }
        return false;
    }

    @Override
    public <T extends Centralizable & Influencable> boolean influence(T c) {
//        if(c.getCenter().distance(center) < sphere.radius) {
        if(sphere.contains(c.getCenter())) {
            return c.influenceBy(this);
        } else {
            return false;
        }
    }

    @Override
    public boolean select() {
        System.out.println("Selecting city " + name);
        Selection.citySelection.clear();
        Selection.citySelection.addUnique(this);
        Selection.citySelection.addAll(inSphere());
        return true;
    }

    @Override
    public Point2D getCenter() {
        return center;
    }

    @Override
    public double getRadius() {
        return radius;
    }

    @Override
    public String getDescription() {
        return name + " " + culture;/* + ", " + watershed.name + ". C,R: " + center + ", " + radius;*/
    }

    public City(String name, Watershed watershed, Random rand) {
//        this(watershed, PolygonUtils.approxRandomInteriorPoint(watershed, 2 * DEFAULT_RADIUS, rand), DEFAULT_RADIUS, rand);
        this.name = name;
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
        cityMap.add(this);
        sphere = new SphereOfInfluence(center, INFLUENCE_RADIUS_FACTOR * radius);
        culture = new Culture(rand);
        temporaryCulture = culture;
    }

    public City(Culture culture, String name, Watershed watershed, Point2D center, double radius, Random rand) {
        this.name = name;
        this.watershed = watershed;
        watershed.cities.add(this);
        this.rand = rand;
        this.radius = radius;
        this.center = center;
        double fx = center.getX();
        double fy = center.getY();
        double s = Math.sqrt(2 * radius * radius - 2 * radius * radius * Math.cos(Math.PI * 2 / 20));
        Polygon circle = (new RegularPolygon(new Point2D(fx - s / 2, fy - Math.sqrt(radius * radius - s * s / 4)), 20, s)).getPolygon();
        this.getPoints().addAll(circle.getPoints());
        cityMap.add(this);
        sphere = new SphereOfInfluence(center, INFLUENCE_RADIUS_FACTOR * radius);
        this.culture = culture;
        temporaryCulture = culture;
    }

    public static City merge(City a, City b) {
        if (a.center.distance(b.center) <= a.radius + b.radius
                /*&& (PolygonUtils.containsAVertex(a, b) || PolygonUtils.containsAVertex(b, a))*/) {
            double ar2 = a.radius * a.radius;
            double br2 = b.radius * b.radius;
            double cx;
            double cy;
            String n;
            Watershed w;
            Culture cul;
//            double cx = (a.center.getX() * ar2 + b.center.getX() * br2) / (ar2 + br2);
//            double cy = (a.center.getY() * ar2 + b.center.getY() * br2) / (ar2 + br2);
            if(ar2 > br2) {
                cx = a.center.getX();
                cy = b.center.getY();
                n = a.name;
                w = a.watershed;
                cul = a.culture;
            } else {
                cx = a.center.getX();
                cy = b.center.getY();
                n = b.name;
                w = b.watershed;
                cul = b.culture;
            }
            try {
                a.watershed.cities.remove(a);
                b.watershed.cities.remove(b);
                cityMap.remove(a);
                cityMap.remove(b);
                City ret = new City(cul, n, w, new Point2D(cx, cy), Math.sqrt((ar2 + br2)), a.rand);
                System.out.println("Successfully merged " + a.name + " and " + b.name );
                return ret;
            } catch (IllegalArgumentException e) {
                System.out.println("Cities should have merged but didn't");
                return null;
            }
        } else {
            System.out.println("Cities " + a.name + " and " + b.name + " did not fit merge criteria");
            return null;
        }
    }

    public <T extends Shape & Centralizable> HashSet<T> inSphere() {
//        System.out.println("CM SIZE " + cityMap.get(sphere.center, sphere.radius).size());
//        return cityMap.get(sphere.center, sphere.radius);
        HashSet<T> ret = new HashSet<>();
        for(Object o : cityMap.get(sphere.center, sphere.radius)) {
            City city = (City) o;
            if(city.center.distance(this.center) <= sphere.radius) {
                ret.add((T) city);
            }
        }
        if(spheredWatershedEndpoint()) {
            ret.add((T) watershed);
        }
        return ret;
    }

    public boolean spheredWatershedEndpoint() {
//        return center.distance(watershed.endPoint) <= sphere.radius;
        return sphere.contains(watershed.endPoint);
    }
}