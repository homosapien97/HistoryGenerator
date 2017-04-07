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
    public MountainRange(Continent continent, double scale, double jaggedness, int deformations, Random rand) {
        super();
        boolean done = false;
        while(!done) {
            this.getPoints().addAll((new Blob(2, scale, jaggedness, deformations, rand)).getDoubleList());
            this.setRotate(rand.nextDouble() * 360);
            Bounds continentBounds = continent.getBoundsInParent();
            this.setTranslateX(continentBounds.getMinX() + rand.nextDouble() * continentBounds.getWidth());
            this.setTranslateY(continentBounds.getMinY() + rand.nextDouble() * continentBounds.getHeight());
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
}
