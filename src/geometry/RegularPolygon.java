package geometry;

import javafx.geometry.Point2D;

import java.util.ArrayList;

/**
 * Created by homosapien97 on 3/11/17.
 */
public class RegularPolygon extends ArrayList<Point2D> {
    public final double exteriorAngle;

    public RegularPolygon(int vertices) {
        this(new Point2D(0,0), vertices, 1.0);
    }
    public RegularPolygon(Point2D start, int vertices, double scale) {
        super(vertices);
        exteriorAngle = Math.PI * 2.0 / vertices;
        this.add(start);
        for(int i = 0; i < vertices - 1; i++) {
            this.add(new Point2D(this.get(i).getX() + scale * Math.cos(exteriorAngle * i),
                    this.get(i).getY() + scale * Math.sin(exteriorAngle * i)));
        }
    }
}
