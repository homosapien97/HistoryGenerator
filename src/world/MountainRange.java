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
    public Squiggly squiggly;

    public MountainRange(Polygon polygon, Squiggly squiggly) {
        this.getPoints().addAll(polygon.getPoints());
        this.squiggly = squiggly;
    }

    public MountainRange(Polygon continent, HashSet<Polygon> continents, double continentScale, int continentDeformations, Random rand) {
        boolean done = false;
        do {
            ArrayList<Polygon> polygons = new ArrayList<>();
            double minScale = Math.abs(Math.log(continentScale)) * Math.sqrt(continentScale) / 6;
            Squiggly squiggly = new Squiggly(continent, continents, minScale / 2, Math.PI / 3.5, false, rand);
            squiggly.removeTail(1.0/3.0);
            this.squiggly = squiggly;

            double scale = minScale;
            Polygon last;
            for (Iterator<Point2D> iterator = squiggly.iterator(); iterator.hasNext(); ) {
                Point2D move = iterator.next();
                last = new PolyBlob(new Blob(6, scale, 1.0, continentDeformations / 2, rand), move);
                polygons.add(last);
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
    }
}
