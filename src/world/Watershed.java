package world;

import Utilities.Dependable;
import Utilities.Dependent;
import Utilities.Update;
import geometry.*;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by homosapien97 on 4/8/17.
 */
public class Watershed extends Polygon implements Dependable {
    public Point2D endPoint;
    public Continent continent;
    public HashSet<City> cities = new HashSet<>();
    public Random rand;
    public double area;
    private HashSet<Dependent> dependents = new HashSet<>();

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

    public Watershed(Continent continent, Polygon polygon, Point2D endPoint, Random rand) {
        this.continent = continent;
        this.getPoints().addAll(polygon.getPoints());
        this.endPoint = endPoint;
        this.rand = rand;
        this.area = PolygonUtils.area(this);
    }

    public Watershed(Continent continent, HashSet<Polygon> continents, double continentScale, int continentDeformations, Random rand) {
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
    }

    public void addCity() {
        boolean merge = false;
        City city = new City(this, rand);
        for (City extant : cities) {
            City union = City.merge(city, extant);
            if (union != null) {
                cities.remove(extant);
                cities.add(union);
                merge = true;
                break;
            }
        }
        if (!merge) {
            cities.add(city);
        }
    }
}
