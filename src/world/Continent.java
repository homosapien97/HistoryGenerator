package world;

import geometry.Blob;
import geometry.BlobSettings;
import javafx.geometry.Bounds;
import javafx.scene.shape.*;

import java.util.Random;

/**
 * Created by homosapien97 on 4/1/17.
 */
public class Continent extends Polygon {
    public final int startPolygon;
    public final double scale;
    public final double jaggedness;
    public final int deformations;
    public final Random rand;
    public Continent(BlobSettings blobSettings, Random rand) {
        this(blobSettings.startPolygon, blobSettings.scale, blobSettings.jaggedness, blobSettings.deformations, rand);
    }
    public Continent(int startPolygon, double scale, double jaggedness, int deformations, Random rand) {
        super();
        this.startPolygon = startPolygon;
        this.scale = scale;
        this.jaggedness = jaggedness;
        this.deformations = deformations;
        this.rand = rand;
        this.getPoints().addAll((new Blob(startPolygon, scale, jaggedness, deformations, rand)).getDoubleList());
        System.out.println("Blob has been added to continent");
//        double tx = this.getLayoutBounds().getMinX() + this.getLayoutBounds().getWidth() / 2;
//        double ty = this.getLayoutBounds().getMinY() + this.getLayoutBounds().getHeight() / 2;
//        this.setTranslateX(tx);
//        this.setTranslateY(ty);
//        System.out.println("Continent has been translated");
        Bounds bounds = this.getBoundsInParent();
        this.setTranslateX((rand.nextDouble()-0.5) * bounds.getWidth());
        this.setTranslateY((rand.nextDouble()-0.5) * bounds.getHeight());
        this.setRotate(rand.nextDouble() * 360);
//        System.out.println("Continent has been rotated");
    }
    public boolean union(Continent c) {
        Path unionPath = (Path) (Polygon.union(this, c));
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
