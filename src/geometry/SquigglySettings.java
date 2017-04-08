package geometry;

import javafx.scene.shape.Polygon;

/**
 * Created by homosapien97 on 4/8/17.
 */
public class SquigglySettings {
    public final Polygon container;
    public final double segmentLength;
    public final double curviness;
    public SquigglySettings(Polygon container, double segmentLength, double curviness) {
        this.container = container;
        this.segmentLength = segmentLength;
        this.curviness = curviness;
    }
}
