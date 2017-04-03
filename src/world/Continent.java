package world;

import geometry.Blob;
import javafx.scene.shape.Polygon;

import java.util.Random;

/**
 * Created by homosapien97 on 4/1/17.
 */
public class Continent extends Polygon {
    public Continent(int startPolygon, double scale, double jaggedness, int deformations, Random rand) {
        super();
        this.getPoints().addAll((new Blob(startPolygon, scale, jaggedness, deformations, rand)).getDoubleList());
        System.out.println("Blob has been added to continent");
//        double tx = this.getLayoutBounds().getMinX() + this.getLayoutBounds().getWidth() / 2;
//        double ty = this.getLayoutBounds().getMinY() + this.getLayoutBounds().getHeight() / 2;
//        this.setTranslateX(tx);
//        this.setTranslateY(ty);
//        System.out.println("Continent has been translated");
//        this.setRotate(rand.nextDouble() * 360);
//        System.out.println("Continent has been rotated");
    }
}
