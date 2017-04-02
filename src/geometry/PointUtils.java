package geometry;

import javafx.geometry.Point2D;

/**
 * Created by homosapien97 on 3/11/17.
 */
public class PointUtils {
    public static final double PRECISION = Double.MIN_VALUE;
    public static final Point2D NaNPoint = new Point2D(Double.NaN, Double.NaN);

    /**
     * Gets the orientation of three points. Positive is clockwise, negative is counterclockwise, and 0 is collinear.
     * @param p First point
     * @param q Second point
     * @param r Third point
     * @return Orientation
     */
    public static double orientation(Point2D p, Point2D q, Point2D r) {
        return (q.getY() - p.getY()) * (r.getX() - q.getX()) - (q.getX() - p.getX()) * (r.getY() - q.getY());
    }

    public static double squareDistance(Point2D a, Point2D b) {
        return (a.getX() - b.getX()) * (a.getX() - b.getX()) + (a.getY() - b.getY()) * (a.getY() - b.getY());
    }

    /**
     * Determines whether two points are closer than c to eachother
     * @param a a point
     * @param b a point
     * @param c a distance
     * @return
     */
    public static boolean close(Point2D a, Point2D b, double c) {
        return a.distance(b) <= c;
    }

    public static boolean squareClose(Point2D a, Point2D b, double csquared) {
        return squareDistance(a, b) <= csquared;
    }
}
