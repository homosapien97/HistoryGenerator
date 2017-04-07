package test;

import geometry.RegularPolygon;
import javafx.geometry.Point2D;
import javafx.scene.shape.Polygon;

/**
 * Created by homosapien97 on 4/6/17.
 */
public class DrawablePolygon extends Polygon{
    public DrawablePolygon(Point2D start, int numSides, double scale) {
        super();
        this.getPoints().addAll((new RegularPolygon(start, numSides, scale)).getDoubleList());
    }
}
