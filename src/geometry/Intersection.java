package geometry;

import javafx.geometry.Point2D;

/**
 * Created by homosapien97 on 4/2/17.
 */
public class Intersection {
    public final LineSegment a;
    public final LineSegment b;
    public Intersection(LineSegment a, LineSegment b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public int hashCode() {
        int ah = a.hashCode();
        return (ah << 16) + (ah >>> 16) + b.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if(o != null && o instanceof Intersection) {
            Intersection i = (Intersection) o;
            return hashCode() == i.hashCode() && a.equals(i.a) && b.equals(i.b);
        }
        return false;
    }
}
