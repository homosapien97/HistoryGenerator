package world;

import geometry.Blob;
import geometry.RegularPolygon;
import javafx.geometry.Bounds;
import javafx.scene.shape.*;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by homosapien97 on 4/6/17.
 */
public class MountainRange extends Polygon {
//    public Polygon preintersection;
//    public ArrayList<Polygon> intersectionChunks;
    Continent continent;
    public MountainRange(Continent continent, double scale, double jaggedness, int deformations, Random rand) {
        super();
        this.continent = continent;
        boolean done = false;
        while(!done) {
            System.out.println("---");
            this.getPoints().addAll((new Blob(2, scale, jaggedness, deformations, rand)).getDoubleList());
//            rand.nextDouble();
//            this.setRotate(rand.nextDouble() * 360);
            Bounds continentBounds = continent.getBoundsInParent();
            this.setTranslateX(-this.getTranslateX() + continentBounds.getMinX() + rand.nextDouble() * continentBounds.getWidth());
            this.setTranslateX(-this.getTranslateY() + continentBounds.getMinY() + rand.nextDouble() * continentBounds.getHeight());
//            preintersection = new Polygon();
//            preintersection.getPoints().addAll(this.getPoints());
            Path intersectionPath = (Path) (Polygon.intersect(this, continent));
//        Polygon intersectionPolygon = new Polygon();
            ArrayList<Polygon> intersections = new ArrayList<Polygon>();
            for (PathElement pe : intersectionPath.getElements()) {
                if (pe instanceof MoveTo) {
                    MoveTo mt = (MoveTo) pe;
                    intersections.add(new Polygon());
                    intersections.get(intersections.size() - 1).getPoints().addAll(mt.getX(), mt.getY());
                } else if (pe instanceof LineTo) {
                    LineTo lt = (LineTo) pe;
                    intersections.get(intersections.size() - 1).getPoints().addAll(lt.getX(), lt.getY());
                }
            }
            if (intersections.size() > 0) {
                System.out.println("Intersection chunks: " + intersections.size());
//                intersectionChunks = intersections;
                Polygon max = intersections.get(0);
                double maxarea = max.computeAreaInScreen();
                double currarea;
                for (Polygon pol : intersections) {
                    currarea = pol.computeAreaInScreen();
                    if (currarea > maxarea) {
                        maxarea = currarea;
                        max = pol;
                    }
                }
                this.setTranslateX(0.0);
                this.setTranslateY(0.0);
                this.getPoints().clear();
                this.getPoints().addAll(max.getPoints());
                done = true;
            } else {
                this.getPoints().clear();
                this.setTranslateX(0.0);
                this.setTranslateY(0.0);
                this.setRotate(0.0);
                done = false;
            }
        }
    }
    public boolean union(MountainRange m) {
        Path unionPath = (Path) (Polygon.union(this, m));
//        Polygon intersectionPolygon = new Polygon();
        Polygon union = new Polygon();
        int unions = 0;
        for (PathElement pe : unionPath.getElements()) {
            if (pe instanceof MoveTo) {
                if(unions == 0) {
                    unions++;
                    MoveTo mt = (MoveTo) pe;
                    union.getPoints().addAll(mt.getX(), mt.getY());
                } else {
                    return false;
                }
            } else if (pe instanceof LineTo) {
                LineTo lt = (LineTo) pe;
                union.getPoints().addAll(lt.getX(), lt.getY());
            }
        }
        this.setTranslateX(0.0);
        this.setTranslateY(0.0);
        this.getPoints().clear();
        this.getPoints().addAll(union.getPoints());
        return true;
    }
}
