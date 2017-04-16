package geometry;

import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import world.City;

/**
 * Created by homosapien97 on 4/16/17.
 */
public class VoronoiCell extends Polygon{
    //dummy
    public City parent;
    public VoronoiCell(Polygon p, City parent) {
        this.getPoints().setAll(p.getPoints());
        this.parent = parent;
    }
}
