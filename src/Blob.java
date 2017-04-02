/**
 * Created by homosapien97 on 3/11/17.
 */

import javafx.geometry.Point2D;

import java.util.LinkedList;
import java.util.List;

public class Blob extends LinkedList<Point2D> {
    private static final int DEFAULT_DEFORMATIONS = 20;
    private static final double DEFAULT_SCALE = 1024.0;
    private static final RegularPolygon TRIANGLE = new RegularPolygon(new Point2D(0,0), 3, DEFAULT_SCALE);

    public Blob() {
        super(TRIANGLE);
        for(int i = 0; i < DEFAULT_DEFORMATIONS; i++) {
            deform();
        }
    }

    private static void deform() {

    }
}
