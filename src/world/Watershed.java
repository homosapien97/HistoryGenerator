package world;

import ui.Selectable;
import ui.Selection;
import utilities.*;
import geometry.*;
import javafx.geometry.Point2D;
import javafx.scene.shape.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by homosapien97 on 4/8/17.
 */
public class Watershed extends Polygon implements Dependable, Describable, Centralizable, Influencable, Influencer, Selectable {
    private static final double DEFAULT_GRID_SIZE = 200.0;
    private static final double CITY_INFLUENCE_FACTOR = 1.0/32.0;
    private static final double WATERSHED_INFLUENCE_FACTOR = 1.0/1024.0;
    private static final double INFLUENCE_RADIUS_FACTOR = 1.0;
    public Point2D endPoint;
    public Continent continent;
    public HashSet<City> cities = new HashSet<>();
    public Random rand;
    public double area;
    private HashSet<Dependent> dependents = new HashSet<>();
    public String name;
    private Point2D center;
    private double radius;
    private SphereOfInfluence sphere;
    public Culture culture;
    private Culture temporaryCulture;
    public static final GridMap<Watershed> watershedMap = new GridMap<>(DEFAULT_GRID_SIZE);

    @Override
    public boolean select() {
        System.out.println("Selecting watershed " + name);
        Selection.watershedSelection.clear();
        Selection.watershedSelection.addUnique(this);
        Selection.watershedSelection.addAll(inSphere());
        return true;
    }

    @Override
    public boolean finalizeInfluence() {
        if(culture == temporaryCulture) return false;
        culture = temporaryCulture;
        return true;
    }

    @Override
    public boolean influenceBy(Influencer influencer) {
        if(influencer instanceof City) {
            City city = (City) influencer;
            temporaryCulture.driftTo(culture, city.culture, 1 - 1 / (CITY_INFLUENCE_FACTOR * area + 1));
        } else if(influencer instanceof Watershed) {
            Watershed watershed = (Watershed) influencer;
            temporaryCulture.driftTo(culture, watershed.culture, 1 - 1 / (WATERSHED_INFLUENCE_FACTOR * area * 1 / center.distance(watershed.center) + 1));
        }
        return true;
    }

    @Override
    public <T extends Centralizable & Influencable> boolean influence(T t) {
        if(t instanceof City) {
            City toInfluence = (City) t;
            if(cities.contains(toInfluence)) {
                return toInfluence.influenceBy(this);
            }
            return false;
        } else if(t instanceof Watershed) {
            Watershed toInfluence = (Watershed) t;
            if(sphere.contains(toInfluence.endPoint)) {
                return toInfluence.influenceBy(this);
            }
            return false;
        }
        return false;
    }

    @Override
    public Point2D getCenter() {
//        return center;
//        return PolygonUtils.center(this);
        return endPoint;
    }

    @Override
    /**
     * WARNING: DOES NOT GIVE ACTUAL RADIUS. FOR MAP USE ONLY.
     */
    public double getRadius() {
//        return radius;
//        return PolygonUtils.maxDistance(this, getCenter());
//        return sphere.radius;
        return 1.0;
    }

    @Override
    public String getDescription() {
        return name;
    }

    @Override
    public void addDependent(Dependent dependent) {
        dependents.add(dependent);
    }

    @Override
    public void updateDependents() {
        for(Dependent dependent : dependents) {
            dependent.update(this, new Update(Watershed.class));
        }
    }

    public Watershed(String name, Continent continent, Polygon polygon, Point2D endPoint, Random rand) {
        this.name = name;
        this.continent = continent;
        this.getPoints().addAll(polygon.getPoints());
        this.endPoint = endPoint;
        this.rand = rand;
        this.area = PolygonUtils.area(this);
        this.center = PolygonUtils.center(this);
        this.radius = PolygonUtils.maxDistance(this, center);
        this.culture = new Culture(rand);
        temporaryCulture = culture;
        sphere = new SphereOfInfluence(endPoint, INFLUENCE_RADIUS_FACTOR * Math.sqrt(this.area));
        watershedMap.add(this);
    }

    public Watershed(String name, Continent continent, HashSet<Polygon> continents, double continentScale, int continentDeformations, Random rand) {
        this.name = name;
        this.rand = rand;
        this.continent = continent;
        boolean done = false;
        do {
            ArrayList<Polygon> polygons = new ArrayList<>();
            double minScale = Math.abs(Math.log(continentScale)) * Math.sqrt(continentScale) / 2;
//            double maxScale = Math.abs(Math.log(continentScale * (6.0 * rand.nextDouble() + 1.0))) * Math.sqrt(continentScale);
            Squiggly squiggly = new Squiggly(continent, continents, minScale / 2, Math.PI / 3, true, rand);
            double maxScale = minScale + Math.sqrt(Math.abs(Math.log(squiggly.length())));
            endPoint = new Point2D(squiggly.getLast().getX(), squiggly.getLast().getY());
            double scale = maxScale;
            Polygon last;
            int j = 0;
            for (Iterator<Point2D> iterator = squiggly.iterator(); iterator.hasNext(); ) {
                Point2D move = iterator.next();
                last = new PolyBlob(new Blob(6, scale, 1.0, continentDeformations / 2, rand), move);
                polygons.add(last);
                j++;
                scale = Math.sqrt(Math.sqrt(minScale)) + 1.0 / (j + 1.0 / (Math.sqrt(Math.sqrt(maxScale)) - Math.sqrt(Math.sqrt(minScale))));
                scale = scale * scale * scale * scale;
            }
            Polygon blobUnion = new Polygon();
            for (int i = 0; i < polygons.size(); i++) {
                Polygon safeUnion = PolygonUtils.nullSafeUnion(blobUnion, polygons.get(i));
                if (safeUnion == null) {
                } else {
                    blobUnion = safeUnion;
                }
            }
            try {
                Polygon safeUnion = PolygonUtils.intersection(blobUnion, squiggly.container);
                if (safeUnion != null && safeUnion.getPoints().size() != 0) {
                    blobUnion = safeUnion;
                    this.getPoints().addAll(blobUnion.getPoints());
                    done = true;
                } else {
                }
            } catch (Exception e) {
            }
        } while(!done);
        this.area = PolygonUtils.area(this);
        this.center = PolygonUtils.center(this);
        this.radius = PolygonUtils.maxDistance(this, center);
        this.culture = new Culture(rand);
        temporaryCulture = culture;
        sphere = new SphereOfInfluence(endPoint, INFLUENCE_RADIUS_FACTOR * Math.sqrt(this.area));
        watershedMap.add(this);
    }

    public void addCity(String name) {
        boolean merge = false;
        City city = new City(name, this, rand);
        int i = 0;
        System.out.println("Adding " + name);
        for (City extant : cities) {
            System.out.println(i++);
            if(city != extant) {
                City union = City.merge(city, extant);
                if (union != null) {
                    cities.remove(extant);
                    cities.add(union);
                    city = union;
                    merge = true;
                    break;
                }
            }
        }
        if (!merge) {
            cities.add(city);
        }
    }

    public void setAverageCulture() {
        double l = 0.0;
        double r = 0.0;
        double i = 0.0;
        for(City city : cities) {
            l += city.culture.language / cities.size();
            r += city.culture.religion / cities.size();
            i += city.culture.individualism / cities.size();
        }
        culture.language = l;
        culture.religion = r;
        culture.individualism = i;
    }

    public <T extends Shape & Centralizable> HashSet<T> inSphere() {
//        System.out.println("CM SIZE " + cityMap.get(sphere.center, sphere.radius).size());
//        return cityMap.get(sphere.center, sphere.radius);
        HashSet<T> ret = new HashSet<>();
        for(Object o : watershedMap.get(sphere.center, sphere.radius)) {
            Watershed watershed = (Watershed) o;
            if(watershed.center.distance(this.center) <= sphere.radius) {
                ret.add((T) watershed);
            }
        }
        return ret;
    }
}
