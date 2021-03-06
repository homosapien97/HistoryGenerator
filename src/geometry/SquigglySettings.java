package geometry;

import javafx.scene.shape.Polygon;

/**
 * Created by homosapien97 on 4/8/17.
 */
public class SquigglySettings {
    public final Polygon superContainer;
    public final Polygon container;
    public final double segmentLength;
    public final double curviness;
    public final boolean shorter;
    public SquigglySettings(Polygon superContainer, Polygon container, double segmentLength, double curviness, boolean shorter) {
        this.superContainer = superContainer;
        this.container = container;
        this.segmentLength = segmentLength;
        this.curviness = curviness;
        this.shorter = shorter;
    }
}
