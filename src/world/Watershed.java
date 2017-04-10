package world;

import geometry.*;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by homosapien97 on 4/8/17.
 */
public class Watershed extends Polygon {
//    public final HashSet<Polygon> generatedPolygons = new HashSet<>();
//    public final HashSet<Circle> circles = new HashSet<>();
//    public final HashSet<Polygon> unions = new HashSet<>();
    public Point2D endPoint;

    public Watershed(Polygon polygon, Point2D endPoint) {
        this.getPoints().addAll(polygon.getPoints());
        this.endPoint = endPoint;
    }

    public Watershed(HashSet<Polygon> continents, double continentScale, int continentDeformations, Random rand) {
        boolean done = false;
        do {
            System.out.println("Generating watershed");
            ArrayList<Polygon> polygons = new ArrayList<>();
            double minScale = Math.abs(Math.log(continentScale)) * Math.sqrt(continentScale) / 2;
            double maxScale = Math.abs(Math.log(continentScale * (5 * rand.nextDouble() + 1))) * Math.sqrt(continentScale);
            Squiggly squiggly = new Squiggly(continents, minScale / 2, Math.PI / 3, rand);
            endPoint = new Point2D(squiggly.getLast().getX(), squiggly.getLast().getY());

            double scale = maxScale;
            System.out.println("Generating watershed blobs");
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
            System.out.println("Generating watershed union");
            Polygon blobUnion = new Polygon();
            for (int i = 0; i < polygons.size(); i++) {
//                System.out.println(i);
                Polygon safeUnion = PolygonUtils.nullSafeUnion(blobUnion, polygons.get(i));
                if (safeUnion == null) {
                    System.out.println("One of the watershed unions was null");
                } else {
                    blobUnion = safeUnion;
                }
            }
            System.out.println("Generating watershed intersection");
            try {
                Polygon safeUnion = PolygonUtils.intersection(blobUnion, squiggly.container);
                if (safeUnion != null && safeUnion.getPoints().size() != 0) {
                    blobUnion = safeUnion;
                    this.getPoints().addAll(blobUnion.getPoints());
                    done = true;
                } else {
                    System.out.println("Watershed intersection failure--restarting");
                }
            } catch (Exception e) {
                System.out.println("Watershed intersection failure--restarting");
            }
        } while(!done);
    }

    /*
    public Watershed(Polygon continent, double continentScale, int continentDeformations, Random rand) {
        boolean done = false;
        do {
            System.out.println("Generating watershed");
            ArrayList<Polygon> polygons = new ArrayList<>();
            double minScale = Math.abs(Math.log(continentScale)) * Math.sqrt(continentScale) / 2;
            double maxScale = Math.abs(Math.log(continentScale * (5 * rand.nextDouble() + 1))) * Math.sqrt(continentScale);
            Squiggly squiggly = new Squiggly(continent, minScale / 2, Math.PI / 6, rand);
            endPoint = new Point2D(squiggly.getLast().getX(), squiggly.getLast().getY());

            double scale = maxScale;
            System.out.println("Generating watershed blobs");
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
            System.out.println("Generating watershed union");
            Polygon blobUnion = new Polygon();
            for (int i = 0; i < polygons.size(); i++) {
//                System.out.println(i);
                Polygon safeUnion = PolygonUtils.nullSafeUnion(blobUnion, polygons.get(i));
                if (safeUnion == null) {
                    System.out.println("One of the watershed unions was null");
                } else {
                    blobUnion = safeUnion;
                }
            }
            System.out.println("Generating watershed intersection");
            try {
                Polygon safeUnion = PolygonUtils.intersection(blobUnion, continent);
                if (safeUnion != null && safeUnion.getPoints().size() != 0) {
                    blobUnion = safeUnion;
                    this.getPoints().addAll(blobUnion.getPoints());
                    done = true;
                } else {
                    System.out.println("Watershed intersection failure--restarting");
                }
            } catch (Exception e) {
                System.out.println("Watershed intersection failure--restarting");
            }
        } while(!done);
    }
    */

    public boolean setPolygon(Polygon polygon) {
        this.getPoints().clear();
        this.setTranslateX(0.0);
        this.setTranslateY(0.0);
        this.setRotate(0.0);
        this.getPoints().addAll(polygon.getPoints());
        return true;
    }
}
