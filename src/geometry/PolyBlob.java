package geometry;

import javafx.geometry.Point2D;
import javafx.scene.shape.Polygon;

/**
 * Created by homosapien97 on 4/9/17.
 */
public class PolyBlob extends Polygon {
    public PolyBlob(Blob blob, Point2D center) {
//        System.out.println("Polyblob");
        double centerX = 0.0;
        double centerY = 0.0;
        final double div = (double) blob.size();
        for(Point2D p : blob) {
            centerX += p.getX() / div;
            centerY += p.getY() / div;
            this.getPoints().addAll(p.getX(), p.getY());
        }

//        System.out.println("\tcenterX = " + centerX);
//        System.out.println("\tcenterY = " + centerY);
//        System.out.println("\tmoveto " + center);
        this.setTranslateX(-centerX + center.getX());
        this.setTranslateY(-centerY + center.getY());
    }

}
