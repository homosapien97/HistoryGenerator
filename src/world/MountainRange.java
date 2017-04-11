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
public class MountainRange extends Polygon {

    public MountainRange(Polygon polygon, Point2D endPoint) {
        this.getPoints().addAll(polygon.getPoints());
    }

    public MountainRange(HashSet<Polygon> continents, double continentScale, int continentDeformations, Random rand) {
        boolean done = false;
        do {
            System.out.println("Generating mountain range");
            ArrayList<Polygon> polygons = new ArrayList<>();
            double minScale = Math.abs(Math.log(continentScale)) * Math.sqrt(continentScale) / 7;
//            double maxScale = Math.abs(Math.log(continentScale * (5 * rand.nextDouble() + 1))) * Math.sqrt(continentScale);
            Squiggly squiggly = new Squiggly(continents, minScale / 2, Math.PI / 3, false, rand);
            squiggly.removeTail(1.0/3.0);

            double scale = minScale;
            System.out.println("Generating mountain range blobs");
            Polygon last;
//            int j = 0;
            for (Iterator<Point2D> iterator = squiggly.iterator(); iterator.hasNext(); ) {
                Point2D move = iterator.next();
                last = new PolyBlob(new Blob(6, scale, 1.0, continentDeformations / 2, rand), move);
                polygons.add(last);
//                j++;
//                scale = Math.sqrt(Math.sqrt(minScale)) + 1.0 / (j + 1.0 / (Math.sqrt(Math.sqrt(maxScale)) - Math.sqrt(Math.sqrt(minScale))));
//                scale = scale * scale * scale * scale;
            }
            System.out.println("Generating mountain range union");
            Polygon blobUnion = new Polygon();
            for (int i = 0; i < polygons.size(); i++) {
                Polygon safeUnion = PolygonUtils.nullSafeUnion(blobUnion, polygons.get(i));
                if (safeUnion == null) {
                    System.out.println("One of the mountain range unions was null");
                } else {
                    blobUnion = safeUnion;
                }
            }
            System.out.println("Generating mountain range intersection");
            try {
                Polygon safeUnion = PolygonUtils.intersection(blobUnion, squiggly.container);
                if (safeUnion != null && safeUnion.getPoints().size() != 0) {
                    blobUnion = safeUnion;
                    this.getPoints().addAll(blobUnion.getPoints());
                    done = true;
                } else {
                    System.out.println("Mountain range intersection failure--restarting");
                }
            } catch (Exception e) {
                System.out.println("Mountain range intersection failure--restarting");
            }
        } while(!done);
    }

    public boolean setPolygon(Polygon polygon) {
        this.getPoints().clear();
        this.setTranslateX(0.0);
        this.setTranslateY(0.0);
        this.setRotate(0.0);
        this.getPoints().addAll(polygon.getPoints());
        return true;
    }
}
